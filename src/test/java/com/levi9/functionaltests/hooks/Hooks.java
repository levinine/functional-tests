package com.levi9.functionaltests.hooks;

import static java.time.Duration.ofSeconds;

import com.levi9.functionaltests.config.SpringConfig;
import com.levi9.functionaltests.rest.service.restfulbooker.RoomService;
import com.levi9.functionaltests.storage.ScenarioEntity;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.restfulbooker.RoomEntity;
import com.levi9.functionaltests.ui.base.BaseDriver;

import java.util.List;

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

	@Autowired
	private RoomService roomService;

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
		log.info("Setting up Selenium WebDriver.");
		baseDriver.initialize();
		baseDriver.getDriver().manage().timeouts().pageLoadTimeout(ofSeconds(30));
		baseDriver.getDriver().manage().timeouts().scriptTimeout(ofSeconds(30));
		baseDriver.getDriver().manage().window().maximize();
	}

	@After
	public void scenarioEnd(final Scenario scenario) {
		log.info("SCENARIO: '{}' finished with status {}!", scenario.getName(), scenario.getStatus());
	}

	@After(value = "@ui", order = 1)
	public void tearDownDriver() {
		log.info("Tearing Down Selenium WebDriver");
		baseDriver.tearDown();
	}

	@After(order = 2)
	public void cleanUp() {
		log.info("Cleaning up created rooms");
		final List<RoomEntity> createdRooms = storage.getRooms().stream().filter((room) -> null != room.getRoomId()).toList();
		log.info("Found {} room(s) that needs deleting! Room names: {}", createdRooms.size(), createdRooms.stream().map(RoomEntity::getRoomName).toList());
		createdRooms.forEach(room -> roomService.deleteRoom(room));
	}

	@After(value = "@ui", order = 3)
	public void embedScenarioFailedScreenshot(final Scenario scenario) {
		log.info("Will take screenshot if test failed.");
		if (scenario.isFailed()) {
			log.info("Scenario failed, taking screenshot");
			scenario.attach(baseDriver.takeScreenshot(), "image/png", "Screenshot");
		}
	}
}
