package com.levi9.functionaltests.ui.helper;

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
}
