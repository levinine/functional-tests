package com.levi9.functionaltests.rest.data.petstore;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Pet Status Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum PetStatus {

	AVAILABLE("available"), PENDING("pending"), SOLD("sold");

	@Getter(AccessLevel.PUBLIC)
	private final String value;

	PetStatus(final String value) {
		this.value = value;
	}

	/**
	 * Get Pet Status enum
	 *
	 * @param value pet status {@link String}
	 *
	 * @return pet status {@link PetStatus}
	 */
	public static PetStatus getEnum(final String value) {
		for (final PetStatus petStatus : values()) {
			if (petStatus.getValue().equals(value)) {
				return petStatus;
			}
		}
		throw new FunctionalTestsException("Pet Status Enum with value {} not found!", value);
	}

}