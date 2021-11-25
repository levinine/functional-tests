package com.levi9.functionaltests.rest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Component
@Scope("cucumber-glue")
public class PetStoreRestClient extends BaseRestClient {

	public PetStoreRestClient(@Value("${service.petstore.url}") final String serviceUrl) {
		super(serviceUrl);
	}
}