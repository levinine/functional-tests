package com.levi9.functionaltests.rest.data.petstore;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Order Status Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum OrderStatus {

	ORDER("placed"),
	APPROVED("approved"),
	DELIVERED("delivered");

	@Getter(AccessLevel.PUBLIC)
	private final String value;

	OrderStatus(final String value) {
		this.value = value;
	}

	/**
	 * Get Order Status enum
	 *
	 * @param value order status {@link String}
	 *
	 * @return order status {@link OrderStatus}
	 */
	public static OrderStatus getEnum(final String value) {
		for (final OrderStatus orderStatus : values()) {
			if (orderStatus.getValue().equals(value)) {
				return orderStatus;
			}
		}
		throw new FunctionalTestsException("Order Status Enum with value {} not found!", value);
	}
}