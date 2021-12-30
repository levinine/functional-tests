package com.levi9.functionaltests.ui.page.dresses;

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
public class DressSuccessfullyAddedToCartPage extends BasePage<DressSuccessfullyAddedToCartPage> {

	// Page identification
	private final By pageId = By.className("clearfix");

	// Page elements
	private final By okayIcon = By.className("icon-ok");
	private final By productQuantityText = By.id("layer_cart_product_quantity");
	private final By productColorAndSizeText = By.id("layer_cart_product_attributes");
	private final By proceedToCheckoutButton = By.xpath("//span[text()[normalize-space()='Proceed to checkout']]");
	private final By continueShoppingButton = By.xpath("//span[text()[normalize-space()='Continue shopping']]");

	public DressSuccessfullyAddedToCartPage(final BaseDriver baseDriver) {
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
	 * Check that dress ia added successfully to shopping cart
	 *
	 * @return true if dress is added, false if it is not
	 */
	public boolean isDressAddedSuccessfully() {
		// Used for firefox to be able to find element bellow
		getDriver().switchTo().defaultContent();
		return isElementVisible(okayIcon, 5);
	}

	/**
	 * Get Product quantity
	 *
	 * @return Product quantity {@link Integer}
	 */
	public int getProductQuantity() {
		return Integer.parseInt(waitAndGetText(productQuantityText));
	}

	/**
	 * Get product color and size
	 *
	 * @return product color and size {@link String}
	 */
	public String getProductColorAndSize() {
		return waitAndGetText(productColorAndSizeText);
	}

	/**
	 * Click proceed to checkout button
	 */
	public void clickProceedToCheckoutButton() {
		waitAndClick(proceedToCheckoutButton);
	}

	/**
	 * CLick continue shopping button
	 */
	public void clickContinueShoppingButton() {
		waitAndClick(continueShoppingButton);
	}
}
