package com.levi9.functionaltests.ui.page.dresses;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class DressesViewModalPage extends BasePage<DressesViewModalPage> {

	// Page identification
	private final By pageId = By.className("fancybox-iframe");

	// Dress parameters
	private final By quantityInput = By.name("qty");
	private final By dressSizeSelect = By.id("group_1");
	private final By dressPriceText = By.id("our_price_display");

	// Add to cart button
	private final By addToCartButton = By.name("Submit");

	public DressesViewModalPage(final BaseDriver baseDriver) {
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
	 * Select dress quantity
	 *
	 * @param quantity quantity
	 */
	public void selectDressQuantity(final int quantity) {
		waitForElement(quantityInput);
		waitAndSendKeys(quantityInput, String.valueOf(quantity));
	}

	/**
	 * Select dress size
	 *
	 * @param dressSize dress size (S,M or L)
	 */
	public void selectDressSize(final String dressSize) {
		new Select(getDriver().findElement(dressSizeSelect)).selectByVisibleText(dressSize);
	}

	/**
	 * Select dress color
	 *
	 * @param dressColor dress color
	 */
	public void selectDressColor(final String dressColor) {
		waitAndClick(getDriver().findElement(By.name(dressColor)));
	}

	/**
	 * CLick on add to cart button
	 */
	public void clickAddToCartButton() {
		waitAndClick(addToCartButton);
	}

	/**
	 * Get dress price before its added to the shopping cart
	 *
	 * @return dress price {@link Double}
	 */
	public double getDressPrice() {
		return Double.parseDouble(waitAndGetText(dressPriceText).replace("$", ""));
	}

}
