package com.levi9.functionaltests.rest.data.restfulbooker;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum RoomType {
	SINGLE("Single"),
	TWIN("Twin"),
	DOUBLE("Double"),
	FAMILY("Family"),
	SUITE("Suite");

	@Getter(AccessLevel.PUBLIC)
	private final String value;

	RoomType(final String value) {
		this.value = value;
	}

	/**
	 * Get Room Type enum
	 *
	 * @param value room type {@link String}
	 *
	 * @return room type {@link RoomType}
	 */
	public static RoomType getEnum(final String value) {
		for (final RoomType roomType : values()) {
			if (roomType.getValue().equals(value)) {
				return roomType;
			}
		}
		throw new FunctionalTestsException("Room Type Enum with value {} not found!", value);
	}
}
