package com.levi9.functionaltests.ui.helper;

import com.paulhammant.ngwebdriver.NgWebDriver;

import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import groovy.util.logging.Slf4j;

/**
 * Wait helper. All common driver waits should be placed here. Domain related methods should not be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class WaitHelper {

	private final EventFiringWebDriver driver;
	private final NgWebDriver ngDriver;

	public WaitHelper(final EventFiringWebDriver driver) {
		this.driver = driver;
		this.ngDriver = new NgWebDriver(driver);
	}

	/**
	 * Waits for Angular to finish.
	 *
	 * @return {@link WaitHelper}
	 */
	public WaitHelper waitForAngularToFinish() {
		ngDriver.waitForAngularRequestsToFinish();
		return this;
	}

	/**
	 * Wait for some Expected condition. Do not wait for Angular.
	 *
	 * @param expectedCondition expected condition
	 * @param timeoutInSeconds  timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForExpectedCondition(final ExpectedCondition<?> expectedCondition, final long timeoutInSeconds) {
		final WebDriverWait driverWait = new WebDriverWait(driver, timeoutInSeconds);
		driverWait.until(expectedCondition);
	}

	/**
	 * Waits for Angular to finish and than wait for some Expected condition.
	 *
	 * @param expectedCondition expected condition
	 * @param timeoutInSeconds  timeout in seconds
	 */
	private void driverWaitFor(final ExpectedCondition<?> expectedCondition, final long timeoutInSeconds) {
		waitForAngularToFinish();
		waitForExpectedCondition(expectedCondition, timeoutInSeconds);
	}

	/**
	 * Waits for element to be present on the DOM of a page. This does not necessarily mean that the element is visible.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeDisplayed(final By by, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.presenceOfElementLocated(by), timeoutInSeconds);
	}

	/**
	 * Wait for element to be present on the DOM of a page and visible. Visibility means that the element is not only displayed but also has a height
	 * and width that is greater than 0.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeVisible(final By by, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.visibilityOfElementLocated(by), timeoutInSeconds);
	}

	/**
	 * Wait for element to be present on the DOM of a page and visible. Waits for default timeout Visibility means that the element is not only
	 * displayed but also has a height and width that is greater than 0.
	 *
	 * @param by element locator
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeVisibleByDefaultTimeout(final By by) {
		driverWaitFor(ExpectedConditions.visibilityOfElementLocated(by), 5);
	}

	/**
	 * Wait for element to be present on the DOM of a page and visible. Visibility means that the element is not only displayed but also has a height
	 * and width that is greater than 0.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeVisible(final WebElement element, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.visibilityOf(element), timeoutInSeconds);
	}

	/**
	 * Wait for element to be visible and enabled such that you can click it.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeClickable(final By by, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.elementToBeClickable(by), timeoutInSeconds);
	}

	/**
	 * Wait for element to be visible and enabled such that you can click it.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementToBeClickable(final WebElement element, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.elementToBeClickable(element), timeoutInSeconds);
	}

	/**
	 * Wait for the URL of the current page to be a specific URL.
	 *
	 * @param url              specific URL
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForUrl(final String url, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.urlToBe(url), timeoutInSeconds);
	}

	/**
	 * Wait for the URL of the current page to contain specific text.
	 *
	 * @param text             specific text
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForUrlToConatin(final String text, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.urlContains(text), timeoutInSeconds);
	}

	/**
	 * Waits for element to be either invisible or not present on the DOM.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementNotToBeVisible(final By by, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.invisibilityOfElementLocated(by), timeoutInSeconds);
	}

	/**
	 * Waits for element to be either invisible or not present on the DOM.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementNotToBeVisible(final WebElement element, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.invisibilityOfAllElements(Arrays.asList(element)), timeoutInSeconds);
	}

	/**
	 * Wait until an element is no longer attached to the DOM.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @throws TimeoutException If the timeout expires.
	 */
	public void waitForElementNotToBeDisplayed(final WebElement element, final long timeoutInSeconds) {
		driverWaitFor(ExpectedConditions.stalenessOf(element), timeoutInSeconds);
	}

}
