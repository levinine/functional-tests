package com.levi9.functionaltests.typeregistry;

import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;

import io.cucumber.java.ParameterType;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public class ParameterTypes {
	
	@ParameterType("Single|Twin|Double|Family|Suite")
	public RoomType roomType(final String roomType) {
		return RoomType.getEnum(roomType);
	}

	@ParameterType("Accessible|Not Accessible")
	public boolean accessible(final String accessible) {
		return !accessible.toLowerCase().contains("not");
	}
}