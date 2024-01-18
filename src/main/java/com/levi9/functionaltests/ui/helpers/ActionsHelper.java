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
@SuppressWarnings({ "unused" })
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
	 * Scroll Element into view, having it aligned to the top of the view.
	 *
	 * @param by {@link By}
	 */
	public void scrollElementIntoViewAlignToTop(final By by) {
		getJs().executeScript("arguments[0].scrollIntoView({block: 'start', inline: 'nearest'});", getDriver().findElement(by));
	}

	/**
	 * Scroll Element into view, having it aligned to the top of the view.
	 *
	 * @param element {@link WebElement}
	 */
	public void scrollElementIntoViewAlignToTop(final WebElement element) {
		getJs().executeScript("arguments[0].scrollIntoView({block: 'start', inline: 'nearest'});", element);
	}

	/**
	 * Scroll Element into view, having it aligned to the bottom of the view.
	 *
	 * @param by {@link By}
	 */
	public void scrollElementIntoViewAlignToBottom(final By by) {
		getJs().executeScript("arguments[0].scrollIntoView({block: 'end', inline: 'nearest'});", getDriver().findElement(by));
	}

	/**
	 * Scroll Element into view, having it aligned to the bottom of the view.
	 *
	 * @param element {@link WebElement}
	 */
	public void scrollElementIntoViewAlignToBottom(final WebElement element) {
		getJs().executeScript("arguments[0].scrollIntoView({block: 'end', inline: 'nearest'});", element);
	}

	/**
	 * Drag and drop between two elements.
	 * Click and hold on drag from element, and then release on drag to element.
	 *
	 * @param dragFromBy drag from locator {@link By}
	 * @param dragToBy   drag to locator {@link By}
	 */
	public void dragAndDrop(final By dragFromBy, final By dragToBy) {
		dragAndDrop(getDriver().findElement(dragFromBy), getDriver().findElement(dragToBy));
	}

	/**
	 * Drag and drop between two elements.
	 * Click and hold on drag from element, and then release on drag to element.
	 *
	 * @param dragFromElement drag from element {@link WebElement}
	 * @param dragToElement   drag to element {@link WebElement}
	 */
	public void dragAndDrop(final WebElement dragFromElement, final WebElement dragToElement) {
		scrollElementIntoViewAlignToTop(dragFromElement);
		getActions().clickAndHold(dragFromElement).moveToElement(dragToElement).release(dragToElement).build().perform();
	}
}
