package com.levi9.functionaltests.rest.service.petstore;

import static com.levi9.functionaltests.rest.data.petstore.PetStatus.PENDING;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.PetStoreRestClient;
import com.levi9.functionaltests.rest.data.petstore.MessageDSO;
import com.levi9.functionaltests.rest.data.petstore.PetDSO;
import com.levi9.functionaltests.rest.data.petstore.PetStatus;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.petstore.PetEntity;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class PetService {

	public static final String REST_PATH = "v2/pet/";

	private final PetStoreRestClient petStoreRestClient;
	private final Storage storage;

	@Autowired
	public PetService(final PetStoreRestClient petStoreRestClient, final Storage storage) {
		this.petStoreRestClient = petStoreRestClient;
		this.storage = storage;
	}

	/**
	 * Add new pet to the Pet Store.
	 *
	 * @param petName pet name
	 */
	public void addPetToStore(final String petName) {

		final PetDSO body = PetDSO.builder()
			.id(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
			.name(petName)
			.status(PENDING.getValue())
			.build();

		final Response response = petStoreRestClient.post(body, null, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Pet can not be added. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}

		final PetEntity pet = PetEntity.builder()
			.id(body.getId())
			.name(body.getName())
			.status(PENDING)
			.build();

		storage.getPets().add(pet);
	}

	/**
	 * Update status of the Pet.
	 *
	 * @param pet       pet to updated
	 * @param petStatus new pet status
	 */
	public void updatePetStatus(final PetEntity pet, final PetStatus petStatus) {

		final PetDSO body = PetDSO.builder()
			.id(pet.getId())
			.name(pet.getName())
			.category(pet.getCategory())
			.photoUrls(pet.getPhotoUrls())
			.tags(pet.getTags())
			.status(petStatus.getValue())
			.build();

		final Response response = petStoreRestClient.put(body, null, REST_PATH);
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Pet can not be updated. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		pet.setStatus(petStatus);
	}

	/**
	 * Get pet from Pet Store.
	 *
	 * @param pet to be fetched
	 *
	 * @return Pet {@link PetDSO}
	 */
	public PetDSO getPet(final PetEntity pet) {

		final Response response = petStoreRestClient.get(null, REST_PATH + pet.getId());
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Pet can not be fetched. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(PetDSO.class);
	}

	/**
	 * Try to get unavailable pet from Pet Store.
	 *
	 * @param pet pet {@link PetDSO}
	 *
	 * @return 404 when it's not found or bad request 400 {@link MessageDSO}
	 */
	public MessageDSO getUnavailablePet(final PetEntity pet) {

		final Response response = petStoreRestClient.get(null, REST_PATH + pet.getId());
		if (response.statusCode() == HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Pet is available in the pet store. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(MessageDSO.class);
	}

	/**
	 * Remove pet from Pet Store.
	 *
	 * @param pet pet to be removed
	 *
	 * @return true if pet is removed successfully, false otherwise
	 */
	public boolean removePet(final PetEntity pet) {
		final Response response = petStoreRestClient.delete(null, REST_PATH + pet.getId());
		return response.statusCode() == HttpStatus.SC_OK;
	}

	/**
	 * Upload image with image name.
	 *
	 * @param pet      pet for which image will be uploaded
	 * @param fileName file name of image to upload
	 *
	 * @return true if pet image is uploaded successfully, false otherwise
	 */
	public boolean uploadPetImage(final PetEntity pet, final String fileName) {
		try {
			final String imagePath = Objects.requireNonNull(getClass().getClassLoader().getResource("test-data/dog-images/" + fileName)).toURI().getPath();
			final File imageFile = new File(imagePath);
			final Response response =
				petStoreRestClient.uploadFile(imageFile, null, REST_PATH + pet.getId() + "/uploadImage");
			return response.statusCode() == HttpStatus.SC_OK;
		} catch (final URISyntaxException e) {
			log.error("Not able to find image {}!", fileName);
			throw new FunctionalTestsException(e);
		}
	}
}
