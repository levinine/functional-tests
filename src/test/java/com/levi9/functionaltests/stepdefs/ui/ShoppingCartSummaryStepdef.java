package com.levi9.functionaltests.stepdefs.ui;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.ui.page.paymentwizzard.ShoppingCartSummaryPage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
public class ShoppingCartSummaryStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private ShoppingCartSummaryPage shoppingCartSummaryPage;

	@Given("^Checked shopping cart summary$")
	public void checkedShoppingCartSummary() {
		final int expectedNumberOfDresses = storage.getDresses().size();
		final int actualNumberOfDresses = shoppingCartSummaryPage.getNumberOfProducts();

		assertThat(expectedNumberOfDresses).as("Number of dresses are not correct!").isEqualTo(actualNumberOfDresses);
		shoppingCartSummaryPage.clickProceedToCheckoutButton();
	}
}
