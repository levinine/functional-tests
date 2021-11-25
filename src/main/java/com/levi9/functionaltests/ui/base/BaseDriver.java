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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
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
	private final ThreadLocal<EventFiringWebDriver> driver = new ThreadLocal<>();
	private final ChromeOptions chromeOptions = new ChromeOptions();
	private final FirefoxOptions firefoxOptions = new FirefoxOptions();
	@Getter(AccessLevel.PUBLIC)
	private final Browser browser;
	private final WebDriverEventListener eventListener = new BaseDriverEventListener();
	@Autowired
	private Storage storage;
	private DesiredCapabilities capability = new DesiredCapabilities();
	private boolean useRemoteWebDriver = false;
	private String remoteWebDriverUrl = null;

	/**
	 * Setup method with browser and Selenium grid.
	 *
	 * @param browser possible values "chrome" and "firefox" (case insensitive)
	 * @param grid    Selenium Grid, possible values "none", "local" and "cloud" (case insensitive)
	 */
	public BaseDriver(@Value("${browser:chrome}") final String browser, @Value("${grid:none}") final String grid) {
		this.browser = Browser.getEnum(browser);
		if (Grid.getEnum(grid).equals(Grid.NONE)) {
			this.useRemoteWebDriver = false;
		} else {
			this.useRemoteWebDriver = true;
			this.remoteWebDriverUrl = Grid.getEnum(grid).url;
		}
	}

	/**
	 * Do Initialization of Driver / WebDriver.
	 */
	public void initialize() {
		if (!useRemoteWebDriver) {
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
	 * Get driver.
	 *
	 * @return {@link EventFiringWebDriver}
	 */
	public EventFiringWebDriver getDriver() {
		return driver.get();
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
		if (this.useRemoteWebDriver) {
			log.info("Using Remote Webdriver");
			if (browser.equals(FIREFOX)) {
				setFirefoxCapabilities();
			} else if (browser.equals(CHROME)) {
				setChromeCapabilities();
			}
			try {
				final RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(remoteWebDriverUrl), capability);
				remoteDriver.setFileDetector(new LocalFileDetector());
				log.info("Initializing remote webdriver with url {} and with browser {} ", remoteWebDriverUrl,
					capability.getBrowserName().toUpperCase());
				driver.set(new EventFiringWebDriver(remoteDriver).register(eventListener));
			} catch (final MalformedURLException e) {
				final String msg = "Error while initializing remote webdriver with url: " + remoteWebDriverUrl.toUpperCase();
				log.error(msg, e);
				throw new FunctionalTestsException(msg, e);
			}
		} else if (browser.equals(FIREFOX)) {
			setFirefoxCapabilities();
			log.info("Initializing webdriver with browser {}", firefoxOptions.getBrowserName().toUpperCase());
			driver.set(new EventFiringWebDriver(new FirefoxDriver(firefoxOptions)).register(eventListener));
		} else if (browser.equals(CHROME)) {
			setChromeCapabilities();
			log.info("Initializing webdriver with browser {}", chromeOptions.getBrowserName().toUpperCase());
			driver.set(new EventFiringWebDriver(new ChromeDriver(chromeOptions)).register(eventListener));
		} else {
			final String msg = "No proper browser setting is found!";
			log.error(msg);
			throw new FunctionalTestsException(msg);
		}
	}

	/**
	 * Sets Firefox capabilities.
	 */
	private void setFirefoxCapabilities() {
		capability = DesiredCapabilities.firefox();
		final FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("dom.forms.number", false);
		firefoxOptions.setProfile(firefoxProfile);
		capability.merge(firefoxOptions);
	}

	/**
	 * Sets Chrome capabilities.
	 */
	private void setChromeCapabilities() {
		//chromeOptions.addArguments("window-size=1920,1080");
		//chromeOptions.addArguments("headless");
		//chromeOptions.addArguments("no-sandbox");
		chromeOptions.addArguments("verbose");
		chromeOptions.addArguments("whitelisted-ips=");
		chromeOptions.addArguments("disable-extensions");
		capability = DesiredCapabilities.chrome();
		capability.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
	}

	/**
	 * Tear down driver and clear all cookies.
	 */
	public void tearDown() {
		log.info("Closing Selenium Driver");
		if (null != getDriver()) {
			try {
				log.info("Deleting Cookies");
				getDriver().manage().deleteAllCookies();
				log.info("Executing Driver Close");
				getDriver().close();
				log.info("Executing Driver Quit");
				getDriver().quit();
				log.info("Closed Selenium Driver");
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
		final TakesScreenshot screenshot = getDriver();
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