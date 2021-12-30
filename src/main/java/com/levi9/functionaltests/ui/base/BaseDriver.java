package com.levi9.functionaltests.ui.base;

import static com.levi9.functionaltests.ui.base.Browser.CHROME;
import static com.levi9.functionaltests.ui.base.Browser.FIREFOX;
import static com.levi9.functionaltests.ui.base.Os.getEnum;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base Driver for all Test classes. Domain related methods should not be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class BaseDriver {

	/**
	 * Initialization.
	 */
	private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	@Getter(AccessLevel.PUBLIC)
	private final Browser browser;
	private final WebDriverListener eventListener = new BaseDriverListener();
	private final boolean remote;
	@Value("${selenium.grid.url}")
	private String remoteWebDriverUrl;
	@Autowired
	private Storage storage;

	/**
	 * Setup method with browser and Selenium grid.
	 *
	 * @param browser possible values "chrome" and "firefox" (case-insensitive)
	 * @param remote  true to execute remotely on Selenium Grid, false otherwise
	 */
	public BaseDriver(@Value("${browser:chrome}") final String browser, @Value("${remote:false}") final boolean remote) {
		this.browser = Browser.getEnum(browser);
		this.remote = remote;
	}

	/**
	 * Get driver.
	 *
	 * @return {@link WebDriver}
	 */
	public WebDriver getDriver() {
		return driver.get();
	}

	/**
	 * Do Initialization of Driver / WebDriver.
	 */
	public void initialize() {
		if (!remote) {
			final Os os = getEnum();
			if (getBrowser().equals(FIREFOX)) {
				initializeDriver(FIREFOX.getSystemVariable(), os, os.geckoPath);
			}
			if (getBrowser().equals(CHROME)) {
				initializeDriver(CHROME.getSystemVariable(), os, os.chromePath);
			}
		}
		initializeWebDriver(getBrowser());
	}

	/**
	 * Initialize driver file.
	 *
	 * @param browserSystemVariable the browser system variable
	 * @param os                    the os
	 * @param browserPath           the browser path
	 */
	private void initializeDriver(final String browserSystemVariable, final Os os, final String browserPath) {
		if (browserPath != null) {
			try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(os.getPrefix() + browserPath + os.getSuffix())) {
				final File temp = File.createTempFile(browserPath, os.getSuffix());
				log.trace("Set Executable on driver file returned {}", temp.setExecutable(true));
				FileUtils.copyInputStreamToFile(inputStream, temp);
				System.setProperty(browserSystemVariable, temp.getAbsolutePath());
			} catch (final NullPointerException npe) {
				final String msg = "File not found! {}";
				log.error(msg, npe.getMessage());
				throw new FunctionalTestsException(msg, npe);
			} catch (final IOException ioe) {
				final String msg = "Error while copying driver executable! {}";
				log.error(msg, ioe.getMessage());
				throw new FunctionalTestsException(msg, ioe);
			}
		}
	}

	/**
	 * Initialize Selenium web driver for selected Browser.
	 *
	 * @param browser browser
	 */
	private void initializeWebDriver(final Browser browser) {
		final AbstractDriverOptions browserOptions = getBrowserOptions(browser);
		final String browserName = browserOptions.getBrowserName().toUpperCase();
		if (this.remote) {
			log.info("Using Remote Webdriver");
			try {
				final RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(remoteWebDriverUrl), browserOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				log.info("Initializing remote WebDriver with url {} and with browser {} ", remoteWebDriverUrl, browserName);
				driver.set(new EventFiringDecorator(eventListener).decorate(remoteDriver));
			} catch (final MalformedURLException e) {
				final String msg = "Error while initializing remote webdriver with url: " + remoteWebDriverUrl.toUpperCase();
				log.error(msg, e);
				throw new FunctionalTestsException(msg, e);
			}
		} else if (browser.equals(FIREFOX)) {
			final FirefoxOptions firefoxOptions = (FirefoxOptions) browserOptions;
			final FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
			log.info("Initializing Local WebDriver with {} browser", browserName);
			driver.set(new EventFiringDecorator(eventListener).decorate(firefoxDriver));
		} else if (browser.equals(CHROME)) {
			log.info("Initializing Local WebDriver with {} browser", browserName);
			final ChromeOptions chromeOptions = (ChromeOptions) browserOptions;
			final ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
			driver.set(new EventFiringDecorator(eventListener).decorate(chromeDriver));
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
	 * @param <T>
	 *
	 * @return {@link FirefoxOptions} or {@link ChromeOptions}
	 */
	private <T extends AbstractDriverOptions> T getBrowserOptions(final Browser browser) {
		if (browser.equals(FIREFOX)) {
			return (T) getFirefoxOptions();
		} else {
			return (T) getChromeOptions();
		}
	}

	/**
	 * Get Firefox Options.
	 */
	private FirefoxOptions getFirefoxOptions() {
		final FirefoxOptions browserOptions = new FirefoxOptions();
		final FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("dom.forms.number", false);
		browserOptions.setProfile(firefoxProfile);
		return browserOptions;
	}

	/**
	 * Get Chrome Options.
	 */
	private ChromeOptions getChromeOptions() {
		final ChromeOptions browserOptions = new ChromeOptions();
		//chromeOptions.addArguments("window-size=1920,1080");
		//chromeOptions.addArguments("headless");
		//chromeOptions.addArguments("no-sandbox");
		browserOptions.addArguments("verbose");
		browserOptions.addArguments("whitelisted-ips=");
		browserOptions.addArguments("disable-extensions");
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
		final String fileName = timestamp + "_" + storage.getTestScenario().getScreenshotCounter();
		final String fileExtension = ".png";
		final TakesScreenshot screenshot = (TakesScreenshot) getDriver();
		final String screenShotSaveLocationPath = storage.getTestScenario().getScenarioScreenshotsLocationPath() + fileName + fileExtension;
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