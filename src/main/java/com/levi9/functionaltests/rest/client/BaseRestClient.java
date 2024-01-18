package com.levi9.functionaltests.rest.client;

import static io.restassured.RestAssured.given;
import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RedirectConfig.redirectConfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Map;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base Rest Client class which is used as parent for all rest clients.
 */
@Slf4j
public abstract class BaseRestClient {

	// HTTP Timeouts in milliseconds
	private static final int HTTP_CONNECTION_TIMEOUT = 180000; // the time to establish the connection with the remote host
	private static final int HTTP_SOCKET_TIMEOUT = 600000; // the time waiting for data – after the connection was established; maximum time of inactivity between two data packets
	private static final int HTTP_CONNECTION_MANAGER_TIMEOUT = 10000; // the time to wait for a connection from the connection manager/pool

	@Getter(AccessLevel.PRIVATE)
	final ObjectMapperConfig objectMapperConfig = new ObjectMapperConfig().jackson2ObjectMapperFactory((aClass, s) -> {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.registerModule(new SimpleModule().addSerializer(BigInteger.class, new ToStringSerializer()));
		objectMapper.registerModule(new JodaModule());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd[ HH:mm:ss]"));
		return objectMapper;
	});
	@Getter(AccessLevel.PRIVATE)
	private final SessionFilter sessionFilter = new SessionFilter();
	@Getter(AccessLevel.PRIVATE)
	private final CookieFilter cookieFilter = new CookieFilter();
	@Getter(AccessLevel.PRIVATE)
	private final ErrorLoggingFilter errorLoggingFilter = new ErrorLoggingFilter(getLogPrintStream());
	@Getter(AccessLevel.PRIVATE)
	private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter(LogDetail.ALL, true, getLogPrintStream());
	@Getter(AccessLevel.PRIVATE)
	private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter(LogDetail.ALL, true, getLogPrintStream());

	/**
	 * Default Request Specification will be used in all REST calls of specified client.
	 * Everything added to Default Request Specification build will be added to Default Request Specification and will be executed with all REST calls.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final RequestSpecBuilder defaultRequestSpecBuilder = new RequestSpecBuilder();

	protected BaseRestClient(final String baseUrl) {
		setBaseUri(baseUrl);
		configSetup();
		setCommonHeaders();
	}

	/**
	 * Set Base URI to default request specification.
	 *
	 * @param baseUri base URI
	 */
	private void setBaseUri(final String baseUri) {
		getDefaultRequestSpecBuilder().setBaseUri(baseUri);
	}

	private void configSetup() {
		getDefaultRequestSpecBuilder().setConfig(
			RestAssuredConfig.config().logConfig(logConfig().enablePrettyPrinting(true))
				.encoderConfig(encoderConfig().defaultContentCharset("UTF-8"))
				.decoderConfig(decoderConfig().defaultContentCharset("UTF-8"))
				.objectMapperConfig(getObjectMapperConfig())
				.redirect(redirectConfig().followRedirects(false))
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.connection.timeout", HTTP_CONNECTION_TIMEOUT)
					.setParam("http.socket.timeout", HTTP_SOCKET_TIMEOUT)
					.setParam("http.connection-manager.timeout", HTTP_CONNECTION_MANAGER_TIMEOUT)));
	}

	/**
	 * Set Commonly user headers in default request specification.
	 */
	private void setCommonHeaders() {
		getDefaultRequestSpecBuilder()
			.setContentType(ContentType.JSON);
		getDefaultRequestSpecBuilder()
			.setAccept(ContentType.JSON)
			.setAccept(ContentType.TEXT)
			.setAccept(ContentType.ANY);
	}

	/**
	 * Execute HTTP POST method.
	 *
	 * @param body       request payload
	 * @param parameters request query parameters
	 * @param path       endpoint path
	 *
	 * @return response as {@link Response}
	 */
	public Response post(final Object body, final Map<String, String> parameters, final String path) {
		final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
			.addRequestSpecification(getDefaultRequestSpecBuilder().build());
		setBody(body, requestSpecificationBuilder);
		setParameters(parameters, requestSpecificationBuilder);
		// @formatter:off
		return given()
					.spec(requestSpecificationBuilder.build())
					.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
				.when()
					.post(path)
				.then()
				.extract()
					.response();
		// @formatter:on
	}

	/**
	 * Execute HTTP PUT method.
	 *
	 * @param body       request payload
	 * @param parameters request query parameters
	 * @param path       endpoint path
	 *
	 * @return response as {@link Response}
	 */
	public Response put(final Object body, final Map<String, String> parameters, final String path) {
		final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
			.addRequestSpecification(getDefaultRequestSpecBuilder().build());
		setBody(body, requestSpecificationBuilder);
		setParameters(parameters, requestSpecificationBuilder);
		// @formatter:off
		return given()
					.spec(requestSpecificationBuilder.build())
					.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
				.when()
					.put(path)
				.then()
				.extract()
					.response();
		// @formatter:on
	}

	/**
	 * Execute HTTP GET method.
	 *
	 * @param parameters request query parameters
	 * @param path       endpoint path
	 *
	 * @return response as {@link Response}
	 */
	public Response get(final Map<String, String> parameters, final String path) {
		final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
			.addRequestSpecification(getDefaultRequestSpecBuilder().build());
		setParameters(parameters, requestSpecificationBuilder);
		// @formatter:off
		return given()
					.spec(requestSpecificationBuilder.build())
					.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
				.when()
					.get(path)
				.then()
				.extract()
					.response();
		// @formatter:on
	}

	/**
	 * Execute HTTP DELETE method.
	 *
	 * @param parameters request query parameters
	 * @param path       endpoint path
	 *
	 * @return response as {@link Response}
	 */
	public Response delete(final Map<String, String> parameters, final String path) {
		final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
			.addRequestSpecification(getDefaultRequestSpecBuilder().build());
		setParameters(parameters, requestSpecificationBuilder);
		// @formatter:off
		return given()
					.spec(requestSpecificationBuilder.build())
					.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
				.when()
					.delete(path)
				.then()
				.extract()
					.response();
		// @formatter:on
	}

	/**
	 * Upload file using HTTP POST method. Content type will be set to multipart/form-data
	 *
	 * @param file       file which will be uploaded
	 * @param parameters request query parameters
	 * @param path       endpoint path
	 *
	 * @return response as {@link Response}
	 */
	public Response uploadFile(final File file, final Map<String, String> parameters, final String path) {
		final RequestSpecBuilder requestSpecificationBuilder = new RequestSpecBuilder()
			.addRequestSpecification(getDefaultRequestSpecBuilder().build()).setContentType("multipart/form-data");
		setParameters(parameters, requestSpecificationBuilder);
		// @formatter:off
		return given()
					.spec(requestSpecificationBuilder.build())
					.filters(getSessionFilter(), getCookieFilter(), getRequestLoggingFilter(), getResponseLoggingFilter(), getErrorLoggingFilter())
					.multiPart(file)
				.when()
					.post(path)
				.then()
				.extract()
					.response();
		// @formatter:on
	}

	/**
	 * Set Query Params to Request Specification.
	 *
	 * @param parameters                  parameters
	 * @param requestSpecificationBuilder request specification builder
	 */
	private void setParameters(final Map<String, String> parameters, final RequestSpecBuilder requestSpecificationBuilder) {
		if (null != parameters) {
			requestSpecificationBuilder.addQueryParams(parameters);
		}
	}

	/**
	 * Sets Body to Request Specification.
	 *
	 * @param body                        payload body
	 * @param requestSpecificationBuilder request specification builder
	 */
	private void setBody(final Object body, final RequestSpecBuilder requestSpecificationBuilder) {
		if (null != body) {
			requestSpecificationBuilder.setBody(body);
		}
	}

	/**
	 * Get default print stream used to send REST-Assured logs to it.
	 *
	 * @return {@link PrintStream}
	 */
	private PrintStream getLogPrintStream() {
		final OutputStream output = new OutputStream() {
			StringBuilder myStringBuilder = new StringBuilder();

			@Override
			public void write(final int b) {
				this.myStringBuilder.append((char) b);
			}

			@Override
			public void flush() {
				log.debug(myStringBuilder.toString());
				myStringBuilder = new StringBuilder();
			}
		};
		return new PrintStream(output, true);
	}
}