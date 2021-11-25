package com.levi9.functionaltests.stepdefs.rest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.data.store.OrderDSO;
import com.levi9.functionaltests.rest.data.store.OrderStatus;
import com.levi9.functionaltests.rest.proxy.storeservice.StoreServiceProxy;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.petstore.OrderEntity;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
public class StoreStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private StoreServiceProxy storeServiceProxy;

	@When("^Place[sd] order for a pet with quantity of (\\d+), ship date in (\\d+) ([Dd]ays?|[Mm]onths?) with status (placed|approved|delivered)$")
	public void orderPet(final int quantity, final int shipDateOffset, final String shipDateOffsetUnit, final String status) {
		final PetEntity pet = storage.getLastPet();
		final LocalDateTime shipDate = determineShipDate(LocalDateTime.now().withNano(0), shipDateOffset, shipDateOffsetUnit);
		final OrderStatus orderStatus = OrderStatus.getEnum(status);
		storeServiceProxy.placeAnOrderForPet(pet, quantity, shipDate, orderStatus);
	}

	@Then("^[Oo]rder (?:will be|is)? placed$")
	public void validateOrderIsPlaced() {
		final OrderEntity expectedOrder = storage.getLastOrder();
		final OrderDSO actualOrder = storeServiceProxy.getOrder(expectedOrder);

		assertSoftly(softly -> {
			softly.assertThat(actualOrder.getId()).as("Order id is not correct!").isEqualTo(expectedOrder.getId());
			softly.assertThat(actualOrder.getPetId()).as("Oder pet ID is not correct!").isEqualTo(expectedOrder.getPet().getId());
			softly.assertThat(actualOrder.getQuantity()).as("Oder quantity is not correct!").isEqualTo(expectedOrder.getQuantity());
			softly.assertThat(actualOrder.getShipDate()).as("Oder Ship Date is not correct!").isEqualTo(expectedOrder.getShipDate());
			softly.assertThat(actualOrder.getStatus()).as("Oder status is not correct!").isEqualTo(expectedOrder.getStatus().getValue());
			softly.assertThat(actualOrder.getComplete()).as("Oder Complete is not correct!").isEqualTo(expectedOrder.getComplete());
		});
	}

	@When("^[Rr]emove[sd] order from pet store$")
	public void removeOrder() {
		final OrderEntity order = storage.getLastOrder();
		final Integer orderId = order.getId();
		if (storeServiceProxy.removeOrder(order)) {
			log.info("Order with ID {} successfully removed from pet store.", orderId);
		} else {
			throw new FunctionalTestsException("Order with ID {} not unsuccessfully removed!", orderId);
		}
		order.setDeleted(true);
	}

	@Then("^Order (?:is|will be) successfully removed$")
	public void validateOrderRemoved() {
		final OrderEntity order = storage.getLastOrder();
		assertThat(storeServiceProxy.getUnavailableOrder(order)).as("Order is found!").isTrue();
		storage.getTestScenario().embedHtml("<marquee>Some text passing by</marquee>");
	}

	/**
	 * Determine ship date based on start date, offset and offset unit.
	 *
	 * @param startDate  date from which we are moving
	 * @param offset     number of units
	 * @param offsetUnit type of unit
	 *
	 * @return {@link LocalDateTime}
	 */
	private LocalDateTime determineShipDate(final LocalDateTime startDate, final int offset, final String offsetUnit) {
		if (offsetUnit.toLowerCase().matches("^(days?)$")) {
			return startDate.plusDays(offset);
		} else if (offsetUnit.toLowerCase().matches("^(months?)$")) {
			return startDate.plusMonths(offset);
		} else {
			throw new FunctionalTestsException("Offset unit {} is not supported!", offsetUnit);
		}
	}
}
