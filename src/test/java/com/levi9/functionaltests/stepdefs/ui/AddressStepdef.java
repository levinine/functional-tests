package com.levi9.functionaltests.stepdefs.ui;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.CustomerEntity;
import com.levi9.functionaltests.ui.page.paymentwizzard.AddressPage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
public class AddressStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private AddressPage addressPage;

	@Given("^[Cc]hecked [Aa]ddress$")
	public void checkedAddress() {
		final CustomerEntity customer = storage.getLastCustomer();

		final String expectedAddress = customer.getAddress();
		final String actualAddress = addressPage.getAddressOfCustomer();

		assertThat(actualAddress).as("Address is not correct!").isEqualTo(expectedAddress);
		addressPage.clickProceedToCheckoutButton();
	}
}
