package com.levi9.functionaltests.rest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Component
@Scope("cucumber-glue")
public class RandomDogRestClient extends BaseRestClient {

	public RandomDogRestClient(@Value("${random.dog.url}") final String serviceUrl) {
		super(serviceUrl);
	}
}
