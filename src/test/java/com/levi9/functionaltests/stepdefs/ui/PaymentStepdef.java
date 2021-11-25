package com.levi9.functionaltests.stepdefs.ui;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.PaymentEntity;
import com.levi9.functionaltests.ui.page.paymentwizzard.PaymentPage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * @author Djordje Borisavljevic (dj.borisavljevic@levi9.com)
 */
public class PaymentStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private PaymentPage paymentPage;

	@Given("^[Cc]ustomer paid with (check|bank wire) payment method$")
	public void customerPaidWithSelectedPaymentMethod(final String paymentMethod) {
		if (paymentMethod.equalsIgnoreCase("BANK WIRE")) {
			paymentPage.clickOnPayByBankWireButton();
		} else if (paymentMethod.equalsIgnoreCase("CHECK")) {
			paymentPage.clickOnPayByCheckButton();
		}
		paymentPage.clickConfirmMyOrderButton();

		storage.getPayments().add(PaymentEntity.builder()
			.paymentMethod(paymentMethod)
			.build()
		);
	}

	@Then("^[Ss]uccess message will be shown to customer$")
	public void successMessageShown() {
		final String actualSuccessMessage = paymentPage.getSuccessfulPaymentMessage();
		final String expectedSuccessMessage = "Your order on My Store is complete.";
		assertThat(actualSuccessMessage).as("Payment not processed!!").isEqualTo(expectedSuccessMessage);
	}
}
