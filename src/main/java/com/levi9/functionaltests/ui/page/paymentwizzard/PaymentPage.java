package com.levi9.functionaltests.ui.page.paymentwizzard;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Djordje Borisavljevic (dj.borisavljevic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class PaymentPage extends BasePage<PaymentPage> {

	// Page identification
	private final By pageId = By.xpath("//*[@id='step_end']/span");

	// Page elements
	private final By payByBankWire = By.xpath("//*[@id='HOOK_PAYMENT']/div[1]/div/p/a");
	private final By payByCheck = By.xpath("//*[@id='HOOK_PAYMENT']/div[2]/div/p/a");
	private final By iConfirmMyOrderButton = By.xpath("//*[@id='cart_navigation']/button/span");
	private final By successfulPaymentMessage = By.xpath("//*[@id='center_column']/div/p");
	private final By successfulPaymentCheck = By.xpath("//*[@id='center_column']/p[1]");

	@Autowired
	private Storage storage;

	public PaymentPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "", pageId);
	}

	/**
	 * Click on pay by bank wire to checkout button
	 */
	public void clickOnPayByBankWireButton() {
		waitAndClick(payByBankWire);
	}

	/**
	 * Click on pay by check button
	 */
	public void clickOnPayByCheckButton() {
		waitAndClick(payByCheck);

	}

	/**
	 * Click on i Confirm my order button
	 */
	public void clickConfirmMyOrderButton() {
		waitAndClick(iConfirmMyOrderButton);
	}

	/**
	 * Get text of success
	 *
	 * @return text of success {@link String}
	 */
	public String getSuccessfulPaymentMessage() {
		final String paymentMethod = storage.getLastPayment().getPaymentMethod();
		if (paymentMethod.equalsIgnoreCase("bank wire")) {
			return getDriver().findElements(successfulPaymentMessage).get(0).getText();
		} else if (paymentMethod.equalsIgnoreCase("check")) {
			return getDriver().findElements(successfulPaymentCheck).get(0).getText();
		} else {
			throw new FunctionalTestsException("Unknown payment method!");
		}
	}
}
