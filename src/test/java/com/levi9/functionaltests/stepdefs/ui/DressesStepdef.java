package com.levi9.functionaltests.stepdefs.ui;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.DressEntity;
import com.levi9.functionaltests.ui.page.dresses.DressSuccessfullyAddedToCartPage;
import com.levi9.functionaltests.ui.page.dresses.DressesPage;
import com.levi9.functionaltests.ui.page.dresses.DressesViewModalPage;
import com.levi9.functionaltests.ui.page.home.HomePage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
public class DressesStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private HomePage homePage;

	@Autowired
	private DressesPage dressesPage;

	@Autowired
	private DressesViewModalPage dressesViewModalPage;

	@Autowired
	private DressSuccessfullyAddedToCartPage dressSuccessfullyAddedToCartPage;

	@Given("^Added (\\d+) (Black|Orange|Blue|Yellow) (S|M|L) ([Cc]asual|[Ee]vening|[Ss]ummer) dress(?:es)? to shopping cart$")
	public void addDressesToTheCart(final int quantity, final String dressColor, final String dressSize, final String dressType) {
		log.info("User is navigating to Dresses Sub Menu item");
		homePage.clickOnDressesSubMenuItem(dressType);

		log.info("User selects first dress");
		dressesPage.clickOnFirstDress();

		log.info("User selects dress quantity");
		dressesViewModalPage.selectDressQuantity(quantity);

		log.info("User selects dress size");
		dressesViewModalPage.selectDressSize(dressSize);

		log.info("User selects dress color");
		dressesViewModalPage.selectDressColor(dressColor);

		log.info("Get total cost!");
		final double totalCost = dressesViewModalPage.getDressPrice() * quantity;

		log.info("User clicks on add to cart button");
		dressesViewModalPage.clickAddToCartButton();

		// Add dress to storage
		storage.getDresses().add(DressEntity.builder()
			.quantity(quantity)
			.size(dressSize)
			.color(dressColor)
			.price(totalCost)
			.type(dressType)
			.build());
	}

	@Given("^Dresse?s? successfully added to shopping cart$")
	public void dressesSuccessfullyAddedToShoppingCart() {
		// Check that dress is added successfully with correct attributes
		assertSoftly(softly -> {
			softly.assertThat(dressSuccessfullyAddedToCartPage.isDressAddedSuccessfully()).as("Dress is not added successfully to shopping cart!").isTrue();
			softly.assertThat(dressSuccessfullyAddedToCartPage.getProductQuantity()).as("Product quantity is not correct!")
				.isEqualTo(storage.getLastDress().getQuantity());
			softly.assertThat(dressSuccessfullyAddedToCartPage.getProductColorAndSize()).as("Product color or size are not correct!")
				.isEqualTo(storage.getLastDress().getColor() + ", " + storage.getLastDress().getSize());
		});
	}

	@Given("^(?:Proceeded to)?\\s?(checkout|[Cc]ontinues?d? shopping)$")
	public void proceedToCheckout(final String action) {
		if (action.trim().equalsIgnoreCase("CHECKOUT")) {
			dressSuccessfullyAddedToCartPage.clickProceedToCheckoutButton();
		} else {
			dressSuccessfullyAddedToCartPage.clickContinueShoppingButton();
		}
	}
}
