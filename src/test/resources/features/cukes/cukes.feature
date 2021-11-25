Feature: Cukes Feature

	This feature provides example of Cucumber Expression matchers with both primitive and custom type examples.

	@test @sanity
	Scenario: Test primitive types in Gherkin: int
		Given I have 5 cukes in my belly

	@test @sanity
	Scenario:  Test primitive types in Gherkin: float
		Given I have 5.3 cukes floated

	@test @sanity
	Scenario:  Test primitive types in Gherkin: word
		Given I have banana cukes worded

	@test @sanity
	Scenario:  Test custom primitive types in Gherkin: color
		Given I have red cukes
