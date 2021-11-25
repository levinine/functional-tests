package com.levi9.functionaltests.ui.page.paymentwizzard;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Djordje Borisavljevic (dj.borisavljevic@levi9.com)
 */

@Slf4j
@Component
@Scope("cucumber-glue")
public class ShippingPage extends BasePage<ShippingPage> {

	// Page identification
	private final By pageId = By.xpath("//*[@id='order_step']/li[4]/span");

	// Page elements
	private final By proceedToCheckoutButton = By.xpath("//*[@id='form']/p/button/span");

	private final By termsOfServiceCheckbox = By.xpath("//*[@id='form']/div/p[2]/label");

	public ShippingPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() throws Error {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "", pageId);
	}

	/**
	 * Click on proceed to checkout button
	 */
	public void clickProceedToCheckoutButton() {
		waitAndClick(proceedToCheckoutButton);
	}

	/**
	 * Click on Terms of Service checkbox
	 */
	public void clickOnTermsOfServiceCheckbox() {
		waitAndClick(termsOfServiceCheckbox);
	}

}
