package com.levi9.functionaltests.stepdefs.ui;

import com.levi9.functionaltests.ui.page.paymentwizzard.ShippingPage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;

/**
 * @author Djordje Borisavljevic (dj.borisavljevic@levi9.com)
 */
public class ShippingStepdef {

	@Autowired
	private ShippingPage shippingPage;

	@Given("^[Aa]greed to Terms of Service and proceeded to checkout$")
	public void acceptTermsOfService() {
		shippingPage.clickOnTermsOfServiceCheckbox();
		shippingPage.clickProceedToCheckoutButton();
	}
}
