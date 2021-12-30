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
public class ShoppingCartSummaryPage extends BasePage<ShoppingCartSummaryPage> {

	// Page identification
	private final By pageId = By.xpath("//ul[@id='order_step']/li[1]/span[1]");

	// Page elements
	private final By productsTable = By.xpath("//table[@id='cart_summary']/tbody/tr");
	private final By proceedToCheckoutButton = By.cssSelector("div#center_column>p:nth-of-type(2)>a>span");

	public ShoppingCartSummaryPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() throws Error {
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "", pageId);
	}

	/**
	 * Get number of products in table
	 *
	 * @return number of products {@link Integer}
	 */
	public int getNumberOfProducts() {
		return getDriver().findElements(productsTable).size();
	}

	/**
	 * Click on proceed to checkout button
	 */
	public void clickProceedToCheckoutButton() {
		waitAndClick(proceedToCheckoutButton);
	}

}
