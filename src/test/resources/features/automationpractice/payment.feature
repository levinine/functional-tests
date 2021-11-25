Feature: Payments

	Customer can have two pays of payments:
	1) Check payment
	2) Bank-Wire payment

	Background: Created account and logged in successfully
		Given Customer navigates to authentication page
		And  Creates new account
		And  Will be logged in on successful account creation

	@ui
	Scenario: Customer makes a check payment
		Given Added 2 Black M Summer dresses to shopping cart
		And Dresses successfully added to shopping cart
		And Proceeded to checkout
		And Checked shopping cart summary
		And Checked address
		And Agreed to Terms of Service and proceeded to checkout
		When Customer paid with check payment method
		Then Success message will be shown to customer

	@ui @skip
	Scenario: Skip - Customer makes a bank-wire payment
		Given Added 1 Orange S Casual dress to shopping cart
		And Dresses successfully added to shopping cart
		And Proceeded to checkout
		And Checked shopping cart summary
		And Checked address
		And Agreed to Terms of Service and proceeded to checkout
		When Customer paid with bank wire payment method
		Then Success message will be shown to customer
