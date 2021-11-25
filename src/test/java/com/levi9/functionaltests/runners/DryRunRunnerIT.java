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
		"html:target/reports/cucumber/html",
		"json:target/reports/cucumber/json/cucumber.json",
		"usage:target/usage.jsonx",
		"junit:target/reports/junit.xml" },
	glue = { "com.levi9.functionaltests" },
	monochrome = true,
	dryRun = true)
public class DryRunRunnerIT {

}
