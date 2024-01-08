package com.levi9.functionaltests.stepdefs.rest;

import static com.levi9.functionaltests.rest.data.petstore.PetStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.rest.data.common.MessageDSO;
import com.levi9.functionaltests.rest.data.petstore.PetDSO;
import com.levi9.functionaltests.rest.data.petstore.PetStatus;
import com.levi9.functionaltests.rest.service.petstore.PetService;
import com.levi9.functionaltests.rest.service.randomdogimage.RandomDogImageService;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class PetStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private PetService petService;

	@Autowired
	private RandomDogImageService randomDogImageService;

	@Given("^[Uu]ser add(?:s|ed) pet \"(.*)\" to the pet store$")
	public void addPet(final String petName) {
		petService.addPetToStore(petName);
		log.info("Pet " + petName + " added to the store.");
	}

	@When("^[Pp]et status is set to \"(available|pending|sold)\"$")
	public void setPetStatus(final String petStatus) {
		final PetEntity pet = storage.getLastPet();
		final PetStatus status = PetStatus.getEnum(petStatus);
		petService.updatePetStatus(pet, status);
		log.info("Pet status is set to " + petStatus);
	}

	@Then("^[Ii]t (?:will be|is)? possible to sell it$")
	public void validatePossibleToSell() {
		final PetEntity expectedPet = storage.getLastPet();
		final PetDSO actualPet = petService.getPet(expectedPet);
		assertThat(actualPet.getStatus()).as("Pet is not available!").isEqualTo(AVAILABLE.getValue());
		log.info("It is possible to sell the Pet.");
	}

	@When("^[Pp]et is removed from the pet store$")
	public void removePet() {
		final PetEntity pet = storage.getLastPet();
		final boolean isPetDeleted = petService.removePet(pet);
		assertThat(isPetDeleted).as("Pet is not deleted!").isTrue();
		log.info("Pet is removed from pet store.");
		pet.setDeleted(true);
	}

	@Then("^[Pp]et (?:will not be|is)? present in the pet store$")
	public void validatePetRemovedFromStore() {
		final PetEntity pet = storage.getLastPet();
		final MessageDSO actualError = petService.getUnavailablePet(pet);
		assertThat(actualError.getMessage()).as("Pet is available in the pet store!").isEqualTo("Pet not found");
		storage.getTestScenario().embedPdfToScenario();
	}

	@When("^[Ii]mage (.*) will be uploaded successfully$")
	public void uploadPetImage(final String petImageName) {
		final PetEntity pet = storage.getLastPet();
		final boolean uploadStatus = petService.uploadPetImage(pet, petImageName);
		assertThat(uploadStatus).as("Pet image is not uploaded successfully!").isTrue();
		log.info("Image of a Pet is uploaded.");
		storage.getTestScenario().embedPicture(randomDogImageService.getRandomDogImageUrl());
	}
}
