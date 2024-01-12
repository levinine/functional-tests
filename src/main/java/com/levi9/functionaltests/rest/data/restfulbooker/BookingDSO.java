package com.levi9.functionaltests.rest.data.restfulbooker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingDSO {

	private int bookingid;
	private int roomid;
	private String firstname;
	private String lastname;
	private boolean depositpaid;
	private BookingDatesDSO bookingdates;
}