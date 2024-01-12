package com.levi9.functionaltests.rest.service.restfulbooker;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.RestfulBookerRestClient;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomAmenities;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomDSO;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomsDSO;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.restfulbooker.RoomEntity;

import java.util.List;

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
public class RoomService {

	public static final String REST_PATH = "room/";

	private final RestfulBookerRestClient restfulBookerRestClient;

	private final BookingService bookingService;
	private final Storage storage;

	@Autowired
	public RoomService(final RestfulBookerRestClient restfulBookerRestClient, final BookingService bookingService, final Storage storage) {
		this.restfulBookerRestClient = restfulBookerRestClient;
		this.bookingService = bookingService;
		this.storage = storage;
	}

	/**
	 * Create new Room via API.
	 *
	 * @param roomName      room name
	 * @param roomPrice     room price
	 * @param roomType      room type ({@link RoomType})
	 * @param accessible    is room accessible or not
	 * @param roomAmenities room amenities ({@link RoomAmenities})
	 */
	public void createRoom(final String roomName, final RoomType roomType, final boolean accessible, final String roomPrice,
		final RoomAmenities roomAmenities) {

		final RoomDSO body = RoomDSO.builder()
			.roomName(roomName)
			.roomPrice(roomPrice)
			.type(roomType.getValue())
			.description("Created with Java Cucumber E2E Test Automation Framework")
			.accessible(accessible)
			.features(roomAmenities.getAmenitiesAsList())
			.image(getImageUrl(roomType))
			.build();

		final Response response = restfulBookerRestClient.post(body, null, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_CREATED) {
			throw new FunctionalTestsException("Room can not be created. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}

		final RoomDSO createdRoom = response.as(RoomDSO.class);
		final RoomEntity roomEntity = new RoomEntity(body);
		roomEntity.setRoomId(createdRoom.getRoomid());

		storage.getRooms().add(roomEntity);
	}

	/**
	 * Get Room Image per room type.
	 *
	 * @param roomType room type for which image URL will be returned
	 *
	 * @return image URL as {@link String}
	 */
	private String getImageUrl(final RoomType roomType) {
		return switch (roomType) {
			case RoomType.SINGLE -> "https://images.pexels.com/photos/271618/pexels-photo-271618.jpeg";
			case RoomType.TWIN -> "https://images.pexels.com/photos/14021932/pexels-photo-14021932.jpeg";
			case RoomType.DOUBLE -> "https://images.pexels.com/photos/11857305/pexels-photo-11857305.jpeg";
			case RoomType.FAMILY -> "https://images.pexels.com/photos/237371/pexels-photo-237371.jpeg";
			case RoomType.SUITE -> "https://images.pexels.com/photos/6585757/pexels-photo-6585757.jpeg";
		};
	}

	/**
	 * Get all rooms.
	 *
	 * @return all rooms as {@link RoomsDSO}
	 */
	public RoomsDSO getRooms() {

		final Response response = restfulBookerRestClient.get(null, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Rooms can not be fetched. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(RoomsDSO.class);
	}

	/**
	 * Get actual room from system by room name.
	 *
	 * @param roomName {@link String}
	 *
	 * @return room {@link RoomDSO}
	 */
	public RoomDSO getRoom(final String roomName) {
		final List<RoomDSO> rooms = getRooms().getRooms();
		return rooms.stream().filter(room -> room.getRoomName().equals(roomName))
			.reduce((first, last) -> last)
			.orElseThrow(() -> new FunctionalTestsException("Room not found!"));
	}

	/**
	 * /**
	 * Delete Room.
	 *
	 * @param room room
	 */
	public void deleteRoom(final RoomEntity room) {
		bookingService.deleteAllBookings(room);
		final Response response = restfulBookerRestClient.delete(null, REST_PATH + room.getRoomId());
		if (response.statusCode() != HttpStatus.SC_ACCEPTED && response.statusCode() != HttpStatus.SC_NOT_FOUND) {
			throw new FunctionalTestsException("Booking not deleted!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
	}
}
