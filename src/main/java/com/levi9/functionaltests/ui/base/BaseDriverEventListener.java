package com.levi9.functionaltests.ui.base;

import com.paulhammant.ngwebdriver.NgWebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Event Listener used with Base Driver. Domain related methods should not be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class BaseDriverEventListener implements WebDriverEventListener {

	private By lastFindBy;
	private String originalValue;

	private void highlightElement(final WebDriver driver, final WebElement element) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "color: red; border: 2px solid red;");
	}

	@Override
	public void beforeNavigateTo(final String url, final WebDriver driver) {
		log.debug("BeforeNavigateTo: {}", url);
	}

	@Override
	public void afterNavigateTo(final String url, final WebDriver driver) {
		log.debug("AfterNavigateTo: {}", url);
		if (!url.contains("unlimited")) {
			try {
				final JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("return angular.reloadWithDebugInfo();");
				new NgWebDriver((JavascriptExecutor) driver).waitForAngularRequestsToFinish();
				Thread.sleep(250);
			} catch (final Exception e) {
				log.debug("Can't execute angular.reloadWithDebugInfo(): " + e.getMessage());
			}
		}
	}

	@Override
	public void beforeNavigateBack(final WebDriver driver) {
		log.debug("BeforeNavigateBack");
	}

	@Override
	public void afterNavigateBack(final WebDriver driver) {
		log.debug("AfterNavigateBack");
	}

	@Override
	public void beforeNavigateForward(final WebDriver driver) {
		log.debug("BeforeNavigateForward");
	}

	@Override
	public void afterNavigateForward(final WebDriver driver) {
		log.debug("AfterNavigateForward");
	}

	@Override
	public void beforeNavigateRefresh(final WebDriver driver) {
		log.debug("BeforeNavigateRefresh");
	}

	@Override
	public void afterNavigateRefresh(final WebDriver driver) {
		log.debug("AfterNavigateRefresh");
	}

	@Override
	public void beforeFindBy(final By by, final WebElement element, final WebDriver driver) {
		log.debug("BeforeFindBy: {}", by.toString());
		try {
			lastFindBy = by;
		} catch (final Exception e) {
			log.debug("Element not found: {}", e.getMessage());
		}
	}

	@Override
	public void afterFindBy(final By by, final WebElement element, final WebDriver driver) {
		log.debug("AfterFindBy: {}", by.toString());
	}

	@Override
	public void beforeClickOn(final WebElement element, final WebDriver driver) {
		highlightElement(driver, element);
	}

	@Override
	public void afterClickOn(final WebElement element, final WebDriver driver) {
		log.debug("afterClickOn: {}", element.toString());

	}

	@Override
	public void beforeChangeValueOf(final WebElement element, final WebDriver driver, final CharSequence[] keysToSend) {
		try {
			originalValue = element.getAttribute("value");
			highlightElement(driver, element);
		} catch (final StaleElementReferenceException e) {
			log.debug(e.getMessage());
		}
	}

	@Override
	public void afterChangeValueOf(final WebElement element, final WebDriver driver, final CharSequence[] keysToSend) {
		log.debug("AfterChangeValueOf: {}", element.toString());
		try {
			log.debug(
				"WebDriver changing value in element found " + lastFindBy + " from '" + originalValue + "' to '" + element.getAttribute("value"));
		} catch (final StaleElementReferenceException e) {
			log.debug(e.getMessage());
		}

	}

	@Override
	public void beforeScript(final String script, final WebDriver driver) {
		log.debug("BeforeScript: {}", script);
	}

	@Override
	public void afterScript(final String script, final WebDriver driver) {
		log.debug("AfterScript: {}", script);
	}

	@Override
	public void onException(final Throwable throwable, final WebDriver driver) {
		log.debug("OnException");
	}

	@Override
	public void beforeAlertAccept(final WebDriver driver) {
		log.debug("BeforeAlertAccept");
	}

	@Override
	public void afterAlertAccept(final WebDriver driver) {
		log.debug("AfterAlertAccept");
	}

	@Override
	public void afterAlertDismiss(final WebDriver driver) {
		log.debug("AfterAlertDismiss");
	}

	@Override
	public void beforeAlertDismiss(final WebDriver driver) {
		log.debug("BeforeAlertDismiss");
	}

	@Override
	public void beforeSwitchToWindow(final String windowName, final WebDriver driver) {
		log.debug("BeforeSwitchToWindow: {}", windowName);
	}

	@Override
	public void afterSwitchToWindow(final String windowName, final WebDriver driver) {
		log.debug("AfterSwitchToWindow: {}", windowName);
	}

	@Override
	public <X> void beforeGetScreenshotAs(final OutputType<X> target) {
		log.debug("BeforeGetScreenshotAs");
	}

	@Override
	public <X> void afterGetScreenshotAs(final OutputType<X> target, final X screenshot) {
		log.debug("AfterGetScreenshotAs");
	}

	@Override
	public void beforeGetText(final WebElement webElement, final WebDriver webDriver) {
		log.debug("BeforeGetText");
	}

	@Override
	public void afterGetText(final WebElement webElement, final WebDriver webDriver, final String s) {
		log.debug("AfterGetText");
	}

}
