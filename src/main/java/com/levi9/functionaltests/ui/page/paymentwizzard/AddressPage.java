package com.levi9.functionaltests.ui.page.paymentwizzard;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class AddressPage extends BasePage<AddressPage> {

	// Page identification
	private final By pageId = By.xpath("//ul[@id='order_step']/li[1]/span[1]");

	// Page elements
	private final By proceedToCheckoutButton = By.xpath("//span[text()='Proceed to checkout']");

	private final By addressOfCustomer = By.xpath("//*[@id='address_delivery']/li[4]");

	public AddressPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "", pageId);
	}

	/**
	 * Get user address
	 *
	 * @return address {@link String}
	 */
	public String getAddressOfCustomer() {
		return waitAndGetText(addressOfCustomer);
	}

	/**
	 * Click on proceed to checkout button
	 */
	public void clickProceedToCheckoutButton() {
		waitAndClick(proceedToCheckoutButton);
	}

}
