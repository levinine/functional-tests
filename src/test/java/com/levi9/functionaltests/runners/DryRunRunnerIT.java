package com.levi9.functionaltests.runners;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Dry Runner used to check if all feature steps are defined.
 * It is executed as regular JUnit test.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = { "src/test/resources/features" },
	plugin = { "pretty",
		"html:target/cucumber/html/dry-run.html",
		"json:target/cucumber/json/dry-run.json",
		"usage:target/cucumber/usage/dry-run.jsonx",
		"junit:target/cucumber/junit/dry-run.xml",
		"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" },
	glue = { "com.levi9.functionaltests" },
	monochrome = true,
	dryRun = true)
public class DryRunRunnerIT {

}
