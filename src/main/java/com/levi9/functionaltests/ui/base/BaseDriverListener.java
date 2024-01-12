package com.levi9.functionaltests.ui.base;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Event Listener used with Base Driver. Domain related methods should not be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class BaseDriverListener implements WebDriverListener {

	private void highlightElement(final WebDriver driver, final WebElement element) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "color: red; border: 2px solid red;");
	}

	@Override
	public void afterFindElement(final WebDriver driver, final By locator, final WebElement result) {
		log.debug("Found WebElement");
		// highlightElement(driver, result);
	}

	@Override
	public void afterFindElements(final WebDriver driver, final By locator, final List<WebElement> result) {
		log.debug("Found WebElements");
		// result.forEach(element -> highlightElement(driver, element));
	}

	@Override
	public void beforeClose(final WebDriver driver) {
		log.debug("Closing Selenium WebDriver");
	}

	@Override
	public void afterClose(final WebDriver driver) {
		log.debug("Selenium WebDriver Closed");
	}

	@Override
	public void beforeQuit(final WebDriver driver) {
		log.debug("Quiting Selenium WebDriver");
	}

	@Override
	public void afterQuit(final WebDriver driver) {
		log.debug("Selenium WebDriver Quited");
	}

	@Override
	public void beforeDeleteAllCookies(final Options options) {
		log.debug("Deleting All Cookies");
	}

	@Override
	public void afterDeleteAllCookies(final Options options) {
		log.debug("All Cookies Deleted");
	}
}
