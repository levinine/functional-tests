package com.levi9.functionaltests.storage;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.domain.automationpractice.CustomerEntity;
import com.levi9.functionaltests.storage.domain.automationpractice.DressEntity;
import com.levi9.functionaltests.storage.domain.automationpractice.PaymentEntity;
import com.levi9.functionaltests.storage.domain.petstore.OrderEntity;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;

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
	private final List<CustomerEntity> customers = new ArrayList<>();
	private final List<DressEntity> dresses = new ArrayList<>();
	private final List<PaymentEntity> payments = new ArrayList<>();

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
	 * Get last customer from storage
	 *
	 * @return last customer {@link CustomerEntity}
	 */
	public CustomerEntity getLastCustomer() {
		return customers.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Customer not found!"));
	}

	/**
	 * Get first customer from storage
	 *
	 * @return first customer {@link CustomerEntity}
	 */
	public CustomerEntity getFirstCustomer() {
		return customers.stream().reduce((first, last) -> first).orElseThrow(() -> new FunctionalTestsException("Customer not found!"));
	}

	/**
	 * Get last dress from storage
	 *
	 * @return last dress {@link DressEntity}
	 */
	public DressEntity getLastDress() {
		return dresses.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Dress not found!"));
	}

	/**
	 * Get last payment from storage
	 *
	 * @return last payment {@link PaymentEntity}
	 */
	public PaymentEntity getLastPayment() {
		return payments.stream().reduce((first, last) -> last).orElseThrow(() -> new FunctionalTestsException("Payment not found!"));
	}

}
