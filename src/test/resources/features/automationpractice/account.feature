Feature: Account

	Customer must be able to create new account and be logged in automatically.

	@ui
	Scenario: Customer logged in on successful account creation
		Given Customer navigated to authentication page
		When  Creates new account
		Then  Will be logged in on successful account creation

	@ui @fail
	Scenario: Fail - Customer logged in on successful account creation
		Given Customer navigated to authentication page
		When  Creates new account
		Then  Will not be logged in on successful account creation