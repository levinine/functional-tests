@ui @booking
Feature: Booking a Room

	Visitor must be able to book a room for available dates by filling up all Mandatory fields with valid values, selecting available date and clicking on Book button.
	If any of the Mandatory fields is missing, proper Mandatory Error Message must be displayed.

	Background: User is logged in as Administrator and has Created a New Room
		Given User is logged in as Administrator
		And User has created Single type Accessible room '1408' priced at 50 GBP with 'WiFi, TV, Refreshments and Safe'
		And Visitor is on the Front Page

	@blocker @sanity
	Scenario: Visitor must be able to book a room for available dates by filling up all Mandatory fields
		When Visitor 'John' 'Doe' with an email 'john.doe@email.com' and phone number '+44 1632 960018' tries to book a room '1408'
		Then Visitor will get Booking Successful! Message

	@critical
	Scenario: Visitor must NOT be able to book a room without filling up first name field
		When Visitor 'Doe' with an email 'john.doe@email.com' and phone number '+44 1632 960018' tries to book a room '1408' without filling up first name field
		Then Visitor will get Booking Mandatory Error Message: 'Firstname should not be blank'
		And Visitor will get Booking Validation Error Message: 'size must be between 3 and 18'

	@critical
	Scenario: Visitor must NOT be able to book a room without filling up last name field
		When Visitor 'John' with an email 'john.doe@email.com' and phone number '+44 1632 960018' tries to book a room '1408' without filling up last name field
		Then Visitor will get Booking Mandatory Error Message: 'Lastname should not be blank'
		And Visitor will get Booking Validation Error Message: 'size must be between 3 and 30'

	@critical
	Scenario: Visitor must NOT be able to book a room without filling up email field
		When Visitor 'John' 'Doe' with phone number '+44 1632 960018' tries to book a room '1408' without filling up email field
		Then Visitor will get Booking Mandatory Error Message: 'must not be empty'

	@critical
	Scenario: Visitor must NOT be able to book a room without filling up phone field
		When Visitor 'John' 'Doe' with an email 'john.doe@email.com' tries to book a room '1408' without filling up phone field
		Then Visitor will get Booking Mandatory Error Message: 'must not be empty'
		And Visitor will get Booking Validation Error Message: 'size must be between 11 and 21'

	@critical
	Scenario: Visitor must NOT be able to book a room setting booking dates
		When Visitor 'John' 'Doe' with an email 'john.doe@email.com' and phone number '+44 1632 960018' tries to book a room '1408' without setting booking dates
		Then Visitor will get Booking Mandatory Error Message: 'must not be null'
