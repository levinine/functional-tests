@ui @contact
Feature: Contact Hotel

	Visitors must be able to contact the property by filling up all mandatory fields with valid values on contact form with valid data and clicking Submit button.
	If any of the mandatory fields is missing, proper Contact Mandatory Error Message must be displayed.

	Background: Visitor is on the Front Page
		Given Visitor is on the Front Page

	@sanity
	Scenario: Visitor must be able to contact the property by filling up all mandatory fields
		When Visitor 'John Doe' tries to contact property regarding 'Special Accommodation' by filling up all mandatory fields with valid data
		Then Visitor 'John Doe' will get Thanks for getting in touch message regarding subject 'Special Accommodation'

	Scenario: Visitor must NOT be able to contact the property without filling up name field
		When Visitor tries to contact property regarding 'Special Accommodation' without filling up name field
		Then Visitor will get Contact Mandatory Error Message: 'Name may not be blank'

	Scenario: Visitor must NOT be able to contact the property without filling up email field
		When Visitor 'John Doe' tries to contact property regarding 'Special Accommodation' without filling up email field
		Then Visitor will get Contact Mandatory Error Message: 'Email may not be blank'

	Scenario: Visitor must NOT be able to contact the property without filling up phone field
		When Visitor 'John Doe' tries to contact property regarding 'Special Accommodation' without filling up phone field
		Then Visitor will get Contact Mandatory Error Message: 'Phone may not be blank'
		And Visitor will get Contact Validation Error Message: 'Phone must be between 11 and 21 characters.'

	Scenario: Visitor must NOT be able to contact the property without filling up subject field
		When Visitor 'John Doe' tries to contact property without filling up subject field
		Then Visitor will get Contact Mandatory Error Message: 'Subject may not be blank'
		And Visitor will get Contact Validation Error Message: 'Subject must be between 5 and 100 characters.'

	Scenario: Visitor must NOT be able to contact the property without filling up message field
		When Visitor 'John Doe' tries to contact property regarding 'Special Accommodation' without filling up message field
		Then Visitor will get Contact Mandatory Error Message: 'Message may not be blank'
		And Visitor will get Contact Validation Error Message: 'Message must be between 20 and 2000 characters.'