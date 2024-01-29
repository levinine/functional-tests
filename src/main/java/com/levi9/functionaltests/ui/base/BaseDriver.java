package com.levi9.functionaltests.ui.base;

import static com.levi9.functionaltests.ui.base.Browser.CHROME;
import static com.levi9.functionaltests.ui.base.Browser.FIREFOX;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base Driver for all Test classes. Domain related methods should not be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@SuppressWarnings({ "java:S3740", "rawtypes", "unchecked" })
@Slf4j
@Component
@Scope("cucumber-glue")
public class BaseDriver {

	/**
	 * Initialization.
	 */
	private final ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();

	@Getter(PRIVATE)
	private final WebDriverListener eventListener = new BaseDriverListener();

	@Getter(PUBLIC)
	private final Browser browser;

	@Value("${headless:false}")
	@Getter(PRIVATE)
	private boolean headLess;

	@Value("${remote:false}")
	@Getter(PRIVATE)
	private boolean remote;

	@Value("${remoteUrl:http://localhost:4444/wd/hub}")
	@Getter(PRIVATE)
	private String remoteUrl;

	@Getter(PRIVATE)
	private final Storage storage;

	/**
	 * Setup method with browser and Selenium grid.
	 *
	 * @param browser possible values "chrome" and "firefox" (case-insensitive)
	 */
	@Autowired
	public BaseDriver(@Value("${browser:chrome}") final String browser, final Storage storage) {
		this.browser = Browser.getEnum(browser);
		this.storage = storage;
	}

	/**
	 * Get driver.
	 *
	 * @return {@link WebDriver}
	 */
	public WebDriver getDriver() {
		return threadLocalDriver.get();
	}

	/**
	 * Do Initialization of Driver / WebDriver.
	 */
	public void initialize() {
		initializeWebDriver(getBrowser());
	}

	/**
	 * Initialize Selenium web driver for selected Browser.
	 *
	 * @param browser browser
	 */

	private void initializeWebDriver(final Browser browser) {
		final var browserOptions = getBrowserOptions(browser);
		final String browserName = browserOptions.getBrowserName().toUpperCase();
		if (isRemote()) {
			log.info("Using Remote WebDriver with URL: {}", getRemoteUrl());
			try {
				final RemoteWebDriver remoteDriver = new RemoteWebDriver(URI.create(getRemoteUrl()).toURL(), browserOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				log.info("Initializing remote WebDriver with url {} and with browser {} ", getRemoteUrl(), browserName);
				threadLocalDriver.set(new EventFiringDecorator(getEventListener()).decorate(remoteDriver));
			} catch (final MalformedURLException e) {
				final String msg = "Error while initializing Remote WebDriver with url: " + getRemoteUrl().toUpperCase();
				log.error(msg, e);
				throw new FunctionalTestsException(msg, e);
			}
		} else if (browser.equals(FIREFOX)) {
			log.info("Initializing Local WebDriver with {} browser", browserName);
			final FirefoxDriverService firefoxService = new GeckoDriverService.Builder().withLogLevel(FirefoxDriverLogLevel.ERROR).build();
			final FirefoxOptions firefoxOptions = (FirefoxOptions) browserOptions;
			final FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxService, firefoxOptions);
			threadLocalDriver.set(new EventFiringDecorator(getEventListener()).decorate(firefoxDriver));
		} else if (browser.equals(CHROME)) {
			log.info("Initializing Local WebDriver with {} browser", browserName);
			final ChromeDriverService chromeService = new ChromeDriverService.Builder().withLogLevel(ChromiumDriverLogLevel.SEVERE).build();
			final ChromeOptions chromeOptions = (ChromeOptions) browserOptions;
			final ChromeDriver chromeDriver = new ChromeDriver(chromeService, chromeOptions);
			threadLocalDriver.set(new EventFiringDecorator(getEventListener()).decorate(chromeDriver));
		} else {
			final String msg = "No proper browser setting is found!";
			log.error(msg);
			throw new FunctionalTestsException(msg);
		}
	}

	/**
	 * Get Browser Options depending on Browser.
	 *
	 * @param browser {@link Browser}
	 *
	 * @return {@link FirefoxOptions} or {@link ChromeOptions}
	 */
	private <T extends AbstractDriverOptions> T getBrowserOptions(final Browser browser) {
		return switch (browser) {
			case FIREFOX -> (T) getFirefoxOptions();
			case CHROME -> (T) getChromeOptions();
		};
	}

	/**
	 * Get Firefox Options.
	 */
	private FirefoxOptions getFirefoxOptions() {
		final FirefoxOptions browserOptions = new FirefoxOptions();
		final FirefoxProfile firefoxProfile = new FirefoxProfile();
		if (isHeadLess()) {
			browserOptions.addArguments("--headless"); // run in headless mode
		}
		browserOptions.setProfile(firefoxProfile);
		return browserOptions;
	}

	/**
	 * Get Chrome Options.
	 */
	private ChromeOptions getChromeOptions() {
		final ChromeOptions browserOptions = new ChromeOptions();
		if (isHeadLess()) {
			browserOptions.addArguments("--headless=new"); // run in headless mode
		}
		browserOptions.addArguments("disable-infobars"); // disabling info bars
		browserOptions.addArguments("--disable-extensions"); // disabling extensions
		browserOptions.addArguments("--disable-gpu"); // disabling hardware acceleration
		browserOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		browserOptions.addArguments("--no-sandbox"); // bypass OS security model
		return browserOptions;
	}

	/**
	 * Tear down driver and clear all cookies.
	 */
	public void tearDown() {
		if (null != getDriver()) {
			try {
				getDriver().manage().deleteAllCookies();
				getDriver().close();
				getDriver().quit();
				threadLocalDriver.remove();
			} catch (final Exception e) {
				log.info("Error with Closing Selenium Driver: {}", e.getMessage());
			}
		}
	}

	/**
	 * Take Screenshot of current page.
	 * Screenshots will be saved in scenario folder of working directory.
	 */
	public byte[] takeScreenshot() {
		final Calendar now = Calendar.getInstance();
		final String timestamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS").format(now.getTime());
		final String fileName = timestamp + "_" + getStorage().getTestScenario().getScreenshotCounter();
		final String fileExtension = ".png";
		final TakesScreenshot screenshot = (TakesScreenshot) getDriver();
		final String screenShotSaveLocationPath = getStorage().getTestScenario().getScenarioScreenshotsLocationPath() + fileName + fileExtension;
		try {
			final byte[] screenShootByteArray = screenshot.getScreenshotAs(OutputType.BYTES);
			FileUtils.writeByteArrayToFile(new File(screenShotSaveLocationPath), screenShootByteArray);
			return screenShootByteArray;
		} catch (final ClassCastException cce) {
			throw new FunctionalTestsException("There is no driver found. Skipping taking screenshot. {}", cce.getMessage());
		} catch (final NoSuchSessionException nsse) {
			throw new FunctionalTestsException("There is no session found. Skipping taking screenshot. {}", nsse.getMessage());
		} catch (final IOException ioe) {
			throw new FunctionalTestsException(ioe.getMessage());
		}
	}
}