package com.levi9.functionaltests.rest.service.restfulbooker;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.rest.client.RestfulBookerRestClient;
import com.levi9.functionaltests.rest.data.restfulbooker.LoginDSO;

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
public class AuthService {

	public static final String REST_PATH = "auth/";

	private final RestfulBookerRestClient restfulBookerRestClient;

	@Autowired
	public AuthService(final RestfulBookerRestClient restfulBookerRestClient) {
		this.restfulBookerRestClient = restfulBookerRestClient;
	}

	/**
	 * Login via API.
	 *
	 * @param username {@link String}
	 * @param password {@link String}
	 */
	public void login(final String username, final String password) {

		final LoginDSO body = LoginDSO.builder()
			.username(username)
			.password(password)
			.build();

		final Response response = restfulBookerRestClient.post(body, null, REST_PATH + "login/");

		if (response.statusCode() != HttpStatus.SC_OK) {
			throw new FunctionalTestsException("User is not logged in. Expected {}, but actual {}. Response message: {}", HttpStatus.SC_OK,
				response.statusCode(), response.getBody().prettyPrint());
		}
		log.info("Login via API with username: '{}' and password: '{}'", username, password);

	}
}
