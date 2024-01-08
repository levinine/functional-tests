package com.levi9.functionaltests.rest.service.randomdogimage;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.RandomDogRestClient;
import com.levi9.functionaltests.rest.data.randomdogimage.RandomDogImageDSO;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class RandomDogImageService {

	public static final String REST_PATH = "/woof.json";
	private final RandomDogRestClient randomDogRestClient;

	@Autowired
	public RandomDogImageService(final RandomDogRestClient randomDogRestClient) {
		this.randomDogRestClient = randomDogRestClient;
	}

	/**
	 * Get random image url of a dog
	 *
	 * @return image url
	 */
	public String getRandomDogImageUrl() {
		Response response;
		do {
			response = randomDogRestClient.get(null, REST_PATH);
		} while (response.as(RandomDogImageDSO.class).getUrl().contains(".mp4"));
		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("Image cannot be fetched. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		return response.as(RandomDogImageDSO.class).getUrl();
	}
}
