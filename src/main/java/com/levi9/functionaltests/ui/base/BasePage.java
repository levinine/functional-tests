package com.levi9.functionaltests.ui.base;

import com.levi9.functionaltests.ui.helper.ActionsHelper;
import com.levi9.functionaltests.ui.helper.UploadHelper;
import com.levi9.functionaltests.ui.helper.WaitHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Getter
public abstract class BasePage<T> {

	public static final String ELEMENT_NOT_FOUND = "Element not found! {}";
	private final WebDriver driver;
	private final JavascriptExecutor js;
	private final WaitHelper waitHelper;
	private final UploadHelper uploadHelper;
	private final ActionsHelper actionsHelper;

	@Value("${ui.automationpractice.url}")
	private String automationPracticeUrl;

	protected BasePage(final BaseDriver baseDriver) {
		this.driver = baseDriver.getDriver();
		this.js = (JavascriptExecutor) baseDriver.getDriver();
		this.waitHelper = new WaitHelper(baseDriver.getDriver());
		this.uploadHelper = new UploadHelper(baseDriver.getDriver());
		this.actionsHelper = new ActionsHelper(baseDriver.getDriver());
		PageFactory.initElements(driver, this);
	}

	/**
	 * Get Web Element.
	 *
	 * @return {@link WebElement}
	 */
	public WebElement getWebElement(final By by) {
		return getDriver().findElement(by);
	}

	/**
	 * Gets the current page title.
	 *
	 * @return the current page title
	 */
	public String getCurrentTitle() {
		return getDriver().getTitle();
	}

	/**
	 * Gets the page source.
	 *
	 * @return the page source
	 */
	public String getSource() {
		return getDriver().getPageSource();
	}

	/**
	 * Gets the current url.
	 *
	 * @return the current url
	 */
	public String getCurrentUrl() {
		return getDriver().getCurrentUrl();
	}

	/**
	 * Refresh the page.
	 */
	public T refresh() {
		getDriver().navigate().refresh();
		return (T) this;
	}

	/**
	 * Checks if element is present on the DOM of a page. This does not necessarily mean that the element is visible.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true if element is present on the DOM of a page before timeout, false if not
	 */
	protected boolean isElementDisplayed(final By by, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForElementToBeDisplayed(by, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.debug(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if element is present on the DOM of a page and visible. Visibility means that the element is not only displayed but also has a height
	 * and width that is greater than 0.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true if element is present on the DOM of a page and visible before timeout, false it not
	 */
	protected boolean isElementVisible(final By by, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForElementToBeVisible(by, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.debug(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if element is present on the DOM of a page and visible. Visibility means that the element is not only displayed but also has a height
	 * and width that is greater than 0.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true if element is present on the DOM of a page and visible before timeout, false it not
	 */
	protected boolean isElementVisible(final WebElement element, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForElementToBeVisible(element, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.info(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if element is visible and enabled such that you can click it.
	 *
	 * @param by               element locator
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true it element is visible and enabled before timeout, false if not
	 */
	protected boolean isElementClickable(final By by, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForElementToBeClickable(by, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.info(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if element is visible and enabled such that you can click it.
	 *
	 * @param element          web element
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true it element is visible and enabled before timeout, false if not
	 */
	protected boolean isElementClickable(final WebElement element, final long timeoutInSeconds) {
		try {
			waitHelper.waitForElementToBeClickable(element, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.info(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if URL of the current page is specific URL.
	 *
	 * @param url              specific URL
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true if the URL of the current page to be a specific URL before timeout, false if not
	 */
	public boolean isUrl(final String url, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForUrl(url, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.info(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if URL of the current page contains specific text.
	 *
	 * @param text             specific text
	 * @param timeoutInSeconds wait timeout in seconds
	 *
	 * @return true if the URL of the current page to contain specific text before timeout, false if not
	 */
	public boolean doesUrlContains(final String text, final long timeoutInSeconds) {
		try {
			getWaitHelper().waitForUrlToContain(text, timeoutInSeconds);
			return true;
		} catch (final TimeoutException e) {
			log.info(ELEMENT_NOT_FOUND, e.getMessage());
			return false;
		}
	}

	/**
	 * Wait for element to be visible and clickable.
	 *
	 * @param element web element
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitForElement(final WebElement element) {
		getWaitHelper().waitForElementToBeVisible(element, 15);
		getWaitHelper().waitForElementToBeClickable(element, 15);
	}

	/**
	 * Wait for element to be visible and clickable before performing click.
	 *
	 * @param element web element
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndClick(final WebElement element) {
		waitForElement(element);
		element.click();
	}

	/**
	 * Wait for element to be visible and clickable before clearing and sending text to it.
	 *
	 * @param element web element
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSendKeys(final WebElement element, final String keysToSend) {
		waitForElement(element);
		element.clear();
		element.sendKeys(keysToSend);
	}

	/**
	 * Wait for element to be visible and clickable.
	 *
	 * @param by locator used to find the element
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitForElement(final By by) {
		getWaitHelper().waitForElementToBeDisplayed(by, 15);

	}

	/**
	 * Wait for element to be visible and clickable before performing click.
	 *
	 * @param by locator used to find the element used
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndClick(final By by) {
		waitForElement(by);
		waitAndClick(getDriver().findElement(by));
	}

	/**
	 * Wait for element to be visible and clickable before clearing and sending text to it.
	 *
	 * @param by         locator used to find the element
	 * @param keysToSend keys to send
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSendKeys(final By by, final String keysToSend) {
		waitForElement(by);
		waitAndSendKeys(getDriver().findElement(by), keysToSend);
	}

	/**
	 * Wait for element to be visible and clickable before selecting by visible text.
	 *
	 * @param element     web element
	 * @param visibleText visible text
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSelectByVisibleText(final WebElement element, final String visibleText) {
		waitForElement(element);
		final Select select = new Select(element);
		select.selectByVisibleText(visibleText);
	}

	/**
	 * Wait for element to be visible and clickable before selecting by index.
	 *
	 * @param by    locator used to find the element
	 * @param index select index (index in dropdown)
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSelectByIndex(final By by, final int index) {
		waitForElement(by);
		waitAndSelectByIndex(getDriver().findElement(by), index);
	}

	/**
	 * Wait for element to be visible and clickable before selecting by index.
	 *
	 * @param element web element
	 * @param index   select index (index in dropdown)
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSelectByIndex(final WebElement element, final int index) {
		waitForElement(element);
		final Select select = new Select(element);
		select.selectByIndex(index);
	}

	/**
	 * Wait for element to be visible and clickable before selecting by visible text.
	 *
	 * @param by          locator used to find the element
	 * @param visibleText visible text
	 *
	 * @throws TimeoutException if element not found.
	 */
	public void waitAndSelectByVisibleText(final By by, final String visibleText) {
		waitForElement(by);
		waitAndSelectByVisibleText(getDriver().findElement(by), visibleText);
	}

	/**
	 * Wait for element to be visible and clickable before getting its text.
	 *
	 * @param element web element
	 *
	 * @return element text
	 *
	 * @throws TimeoutException if element not found.
	 */
	public String waitAndGetText(final WebElement element) {
		waitForElement(element);
		return element.getText();
	}

	/**
	 * Wait for element to be visible and clickable before getting its text.
	 *
	 * @param by locator used to find the element
	 *
	 * @return element text
	 *
	 * @throws TimeoutException if element not found.
	 */
	public String waitAndGetText(final By by) {
		waitForElement(by);
		return waitAndGetText(getDriver().findElement(by));
	}

	/**
	 * Opens URL.
	 *
	 * @param url URL as a {@link String}
	 *
	 * @return
	 */
	public T openUrl(final String url) {
		// Catching of TimeoutException is done because some versions of Firefox
		// timeouts on page load even if page load is done
		try {
			getDriver().get(url);
		} catch (final TimeoutException e) {
			log.info(e.getMessage());
		}
		return (T) this;
	}

	/**
	 * Opens Base URL defined in hooks.
	 *
	 * @return
	 */
	public T openAutomationPracticeUrl() {
		openUrl(getAutomationPracticeUrl());
		isLoaded();
		return (T) this;
	}

	/**
	 * It's unpredictable which error is thrown
	 *
	 * @return
	 */
	public T open() {
		try {
			isLoaded();
			return (T) this;
		} catch (final Exception e) {
			log.trace(e.getMessage());
			load();
		}
		isLoaded();

		return (T) this;
	}

	protected abstract void isLoaded();

	protected abstract void load();

	/**
	 * <b>IMPORTANT:</b> Use only if navigation to the page is not possible!!!<br>
	 * Checks if page is opened. If not, it will be loaded forcefully.
	 *
	 * @return {@link BasePage}
	 */
	protected T openPage(final String pageUrl, final By pageLocator) {
		if (isUrl(pageUrl, 1) && isElementDisplayed(pageLocator, 1) && isElementClickable(pageLocator, 1)) {
			return (T) this;

		} else {
			openUrl(pageUrl);
			getWaitHelper().waitForElementToBeDisplayed(pageLocator, 15);
			getWaitHelper().waitForElementToBeClickable(pageLocator, 15);
			return (T) this;
		}
	}
}
