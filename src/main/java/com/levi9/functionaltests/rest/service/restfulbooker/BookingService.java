package com.levi9.functionaltests.rest.service.restfulbooker;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.RestfulBookerRestClient;
import com.levi9.functionaltests.rest.data.restfulbooker.BookingDSO;
import com.levi9.functionaltests.rest.data.restfulbooker.BookingsDSO;
import com.levi9.functionaltests.storage.domain.restfulbooker.RoomEntity;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class BookingService {

	public static final String REST_PATH = "booking/";

	private final RestfulBookerRestClient restfulBookerRestClient;

	@Autowired
	public BookingService(final RestfulBookerRestClient restfulBookerRestClient) {
		this.restfulBookerRestClient = restfulBookerRestClient;
	}

	/**
	 * /**
	 * Delete Booking.
	 *
	 * @param booking booking to be deleted
	 */
	public void deleteBooking(final BookingDSO booking) {
		final Response response = restfulBookerRestClient.delete(null, REST_PATH + booking.getBookingid() + "/");
		if (response.statusCode() != HttpStatus.SC_ACCEPTED && response.statusCode() != HttpStatus.SC_NOT_FOUND) {
			throw new FunctionalTestsException("Booking not deleted!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
	}

	/**
	 * Get All Bookings for Room.
	 *
	 * @param room room for which all bookings are fetched
	 *
	 * @return all bookings for room as {@link BookingsDSO}
	 */
	public BookingsDSO getBookings(final RoomEntity room) {

		final Map<String, String> parameters = new HashMap<>();
		parameters.put("roomId", Integer.toString(room.getRoomId()));

		final Response response = restfulBookerRestClient.get(parameters, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Bookings not found!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(BookingsDSO.class);
	}

	/**
	 * Delete All Bookings for Room.
	 *
	 * @param room room for which all bookings will be deleted
	 */
	public void deleteAllBookings(final RoomEntity room) {
		final BookingsDSO bookings = getBookings(room);
		bookings.getBookings().forEach((this::deleteBooking));
	}
}
