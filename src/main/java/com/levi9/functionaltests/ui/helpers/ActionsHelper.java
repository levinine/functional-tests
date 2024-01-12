package com.levi9.functionaltests.ui.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import groovy.util.logging.Slf4j;
import lombok.Getter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Getter
public class ActionsHelper {

	private final WebDriver driver;
	private final JavascriptExecutor js;
	private final Actions actions;
	private final WaitHelper waitHelper;

	public ActionsHelper(final WebDriver driver) {
		this.driver = driver;
		this.js = (JavascriptExecutor) driver;
		this.actions = new Actions(driver);
		this.waitHelper = new WaitHelper(driver);
	}

	/**
	 * Moves the mouse to the middle of the element.
	 *
	 * @param element {@link WebElement}
	 */
	public void moveToElement(final WebElement element) {
		getActions().moveToElement(element).perform();
	}

	/**
	 * Moves the mouse to the middle of the element.
	 *
	 * @param by {@link By}
	 */
	public void moveToElement(final By by) {
		getActions().moveToElement(getDriver().findElement(by)).perform();
	}

	/**
	 * Move to element and click
	 *
	 * @param by {@link By}
	 */
	public void moveToElementAndClick(final By by) {
		getActions().moveToElement(getDriver().findElement(by)).click().perform();
	}

	/**
	 * Scroll down to the element.
	 *
	 * @param by {@link By}
	 */
	public void scrollToElement(final By by) {
		getJs().executeScript("arguments[0].scrollIntoView(true);", by);
	}

	/**
	 * Scroll down to the element.
	 *
	 * @param element {@link WebElement}
	 */
	public void scrollToElement(final WebElement element) {
		getJs().executeScript("arguments[0].scrollIntoView(true);", element);
	}

	/**
	 * Drag and drop between two elements.
	 * Click and hold on drag from element, and then release on drag to element.
	 *
	 * @param dragFromBy drag from locator {@link By}
	 * @param dragToBy   drag to locator {@link By}
	 */
	public void dragAndDrop(final By dragFromBy, final By dragToBy) {
		getActions().clickAndHold(getDriver().findElement(dragFromBy)).moveToElement(getDriver().findElement(dragToBy))
			.release(getDriver().findElement(dragToBy)).perform();
	}

	/**
	 * Drag and drop between two elements.
	 * Click and hold on drag from element, and then release on drag to element.
	 *
	 * @param dragFromElement drag from element {@link WebElement}
	 * @param dragToElement   drag to element {@link WebElement}
	 */
	public void dragAndDrop(final WebElement dragFromElement, final WebElement dragToElement) {
		getActions().clickAndHold(dragFromElement).moveToElement(dragToElement).release(dragToElement).perform();
	}
}
