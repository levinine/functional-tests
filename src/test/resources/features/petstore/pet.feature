Feature: Pets

	User is able to add new pet to the store, when it is added to the store user can fetch the information about the pet,
	change pet information or delete the pet from the store.

	@pet @api @sanity
	Scenario: User is able to sell a pet in status available
		Given User added pet "Beagle" to the pet store
		When Pet status is set to "available"
		Then it will be possible to sell it

	@pet @api @pdf
	Scenario: User is able to remove pet from the Pet Store
		Given User added pet "Pomeranian" to the pet store
		When Pet is removed from the pet store
		Then Pet will not be present in the pet store

	@pet @api @image
	Scenario: User is able to upload image of a pet
		Given User added pet "Beagle" to the pet store
		And   Pet status is set to "available"
		Then  image happy-beagle.jpg will be uploaded successfully
