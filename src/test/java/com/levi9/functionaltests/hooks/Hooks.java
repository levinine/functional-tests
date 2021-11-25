package com.levi9.functionaltests.hooks;

import static java.util.concurrent.TimeUnit.*;

import com.levi9.functionaltests.config.SpringConfig;
import com.levi9.functionaltests.storage.ScenarioEntity;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.ui.base.BaseDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * Cucumber Hooks class.
 * All Before and After methods should be placed here.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@CucumberContextConfiguration
@ContextConfiguration(classes = { SpringConfig.class })
public class Hooks {

	@Autowired
	private Storage storage;

	@Autowired
	private BaseDriver baseDriver;

	@Before(order = 0)
	public void scenarioStart(final Scenario scenario) {
		log.info("SCENARIO: '{}' started!", scenario.getName());
		final ScenarioEntity testScenario = storage.getTestScenario();
		testScenario.setScenario(scenario);
		testScenario.setScenarioName(scenario.getName());
		testScenario.setWorkingDirectory(System.getProperty("user.dir"));
	}

	@Before(value = "@ui", order = 1)
	public void setupDriver() {
		log.info("Setting up Selenium Driver.");
		baseDriver.initialize();
		baseDriver.getDriver().manage().timeouts().pageLoadTimeout(30, SECONDS);
		baseDriver.getDriver().manage().timeouts().setScriptTimeout(30, SECONDS);
		baseDriver.getDriver().manage().window().maximize();
	}

	@After
	public void scenarioEnd(final Scenario scenario) {
		log.info("SCENARIO: '{}' finished with status {}!", scenario.getName(), scenario.getStatus());
	}

	@After(value = "@ui", order = 1)
	public void tearDownDriver() {
		log.info("Closing Selenium Driver");
		baseDriver.tearDown();
	}

	@After(value = "@ui", order = 2)
	public void embedScenarioFailedScreenshot(final Scenario scenario) {
		log.info("Will take screenshot if test failed.");
		if (scenario.isFailed()) {
			log.info("Scenario failed, taking screenshot");
			scenario.attach(baseDriver.takeScreenshot(), "image/png", "Screenshot");
		}
	}
}
