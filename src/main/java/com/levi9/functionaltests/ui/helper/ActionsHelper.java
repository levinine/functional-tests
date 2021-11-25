/**
 *
 */
package com.levi9.functionaltests.ui.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import groovy.util.logging.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class ActionsHelper {

	private final EventFiringWebDriver driver;

	private final Actions actions;

	private final WaitHelper waitHelper;

	public ActionsHelper(final EventFiringWebDriver driver) {
		this.driver = driver;
		this.actions = new Actions(driver);
		this.waitHelper = new WaitHelper(driver);
	}

	/**
	 * Gets Actions.
	 *
	 * @return {@link Actions}
	 */
	public Actions getActions() {
		return actions;
	}

	/**
	 * Moves the mouse to the middle of the element.
	 *
	 * @param element
	 */
	public void moveToElement(final WebElement element) {
		getActions().moveToElement(element).perform();
	}

	/**
	 * Moves the mouse to the middle of the element.
	 *
	 * @param by
	 */
	public void moveToElement(final By by) {
		getActions().moveToElement(driver.findElement(by)).perform();
	}

	/**
	 * Move to element and click
	 *
	 * @param by
	 */
	public void moveToElementAndClick(final By by) {
		getActions().moveToElement(driver.findElement(by)).click().perform();
		waitHelper.waitForAngularToFinish();
	}

	/**
	 * Scroll down to the element.
	 *
	 * @param by
	 */
	public void scrollToElement(final By by) {
		driver.executeScript("arguments[0].scrollIntoView(true);", by);
	}

	/**
	 * Scroll down to the element.
	 *
	 * @param element
	 */
	public void scrollToElement(final WebElement element) {
		driver.executeScript("arguments[0].scrollIntoView(true);", element);
	}
}
