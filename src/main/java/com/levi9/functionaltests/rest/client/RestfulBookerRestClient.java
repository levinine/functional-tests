package com.levi9.functionaltests.rest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Component
@Scope("cucumber-glue")
public class RestfulBookerRestClient extends BaseRestClient {

	public RestfulBookerRestClient(@Value("${api.restful-booker-platform.url}") final String serviceUrl) {
		super(serviceUrl);
	}

}
