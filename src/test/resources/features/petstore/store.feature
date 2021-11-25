Feature: Pet Store orders

	User is able to make order for a pet in my pet store.
	User is able to delete order from pet store.

	@store @api @sanity
	Scenario: User is able to place an order for a pet
		Given User adds pet "German Shepherd" to the pet store
		When  Places order for a pet with quantity of 5, ship date in 10 days with status placed
		Then  Order will be placed

	@store @api @html
	Scenario: User is able to successfully remove order from pet store
		Given User adds pet "Golden Retriever" to the pet store
		And   Places order for a pet with quantity of 5, ship date in 1 month with status approved
		And   Order is placed
		When  Removes order from pet store
		Then  Order will be successfully removed
