package com.levi9.functionaltests.storage;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.domain.petstore.OrderEntity;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;
import com.levi9.functionaltests.storage.domain.restfulbooker.RoomEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Test storage used for saving data used in test, mostly domain data.
 */
@Getter
@Component
@Scope("cucumber-glue")
public class Storage {

	// Test Scenario
	private final ScenarioEntity testScenario = new ScenarioEntity();

	// Used for REST API tests
	private final List<PetEntity> pets = new ArrayList<>();
	private final List<OrderEntity> orders = new ArrayList<>();

	// Used for UI tests
	private final List<RoomEntity> rooms = new ArrayList<>();

	/**
	 * Get last pet from storage
	 *
	 * @return last pet {@link PetEntity}
	 */
	public PetEntity getLastPet() {
		return pets.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Last Pet not found!"));
	}

	/**
	 * Get last order from storage
	 *
	 * @return last order {@link OrderEntity}
	 */
	public OrderEntity getLastOrder() {
		return orders.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Last Order not found!"));
	}

	/**
	 * Get last room from storage
	 *
	 * @return last room {@link RoomEntity}
	 */
	public RoomEntity getLastRoom() {
		return rooms.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Room not found!"));
	}

}
