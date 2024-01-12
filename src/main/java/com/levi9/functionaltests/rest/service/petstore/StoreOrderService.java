package com.levi9.functionaltests.rest.service.petstore;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.PetStoreRestClient;
import com.levi9.functionaltests.rest.data.petstore.OrderDSO;
import com.levi9.functionaltests.rest.data.petstore.OrderStatus;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.petstore.OrderEntity;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class StoreOrderService {

	public static final String REST_PATH = "v2/store/order/";

	private final PetStoreRestClient petStoreRestClient;
	private final Storage storage;

	@Autowired
	public StoreOrderService(final PetStoreRestClient petStoreRestClient, final Storage storage) {
		this.petStoreRestClient = petStoreRestClient;
		this.storage = storage;
	}

	/**
	 * Place an order for a pet
	 *
	 * @param pet      pet
	 * @param quantity quantity
	 * @param shipDate ship date
	 * @param status   status of the order
	 */
	public void placeAnOrderForPet(final PetEntity pet, final int quantity, final LocalDateTime shipDate, final OrderStatus status) {

		final OrderDSO body = OrderDSO.builder()
			.id(ThreadLocalRandom.current().nextInt(10))
			.petId(pet.getId())
			.quantity(quantity)
			.shipDate(shipDate)
			.status(status.getValue())
			.complete(true)
			.build();

		final Response response = petStoreRestClient.post(body, null, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Order could not be placed!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}

		final OrderEntity order = OrderEntity.builder()
			.id(body.getId())
			.pet(pet)
			.quantity(body.getQuantity())
			.shipDate(body.getShipDate())
			.status(OrderStatus.getEnum(body.getStatus()))
			.complete(body.getComplete())
			.build();

		storage.getOrders().add(order);
	}

	/**
	 * Get Order from pet store.
	 *
	 * @param order order to be fetched
	 *
	 * @return Order {@link OrderDSO}
	 */
	public OrderDSO getOrder(final OrderEntity order) {

		final Response response = petStoreRestClient.get(null, REST_PATH + order.getId());
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Order not found!. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(OrderDSO.class);
	}

	/**
	 * Try to get unavailable order from pet store
	 *
	 * @param order order to be fetched
	 *
	 * @return true if order is not found, false otherwise
	 */
	public boolean getUnavailableOrder(final OrderEntity order) {

		final Response response = petStoreRestClient.get(null, REST_PATH + order.getId());
		return response.statusCode() == HttpStatus.SC_NOT_FOUND;
	}

	/**
	 * Remove order from pet store
	 *
	 * @param order order to be removed
	 *
	 * @return true if order is removed successfully, false otherwise
	 */
	public boolean removeOrder(final OrderEntity order) {
		final Response response = petStoreRestClient.delete(null, REST_PATH + order.getId());
		return response.statusCode() == HttpStatus.SC_OK;
	}
}
