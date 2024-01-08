package com.levi9.functionaltests.exceptions;

import java.io.Serial;

import org.slf4j.helpers.MessageFormatter;

/**
 * Functional Test Exception.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public class FunctionalTestsException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Functional Test Exception with specified message.
	 *
	 * @param pattern some information regarding why this exception is thrown
	 */
	public FunctionalTestsException(final String pattern, final Object... arguments) {
		super(MessageFormatter.arrayFormat(pattern, arguments).getMessage());
	}

	/**
	 * Functional Test Exception with specified exception
	 *
	 * @param exception exception which will be re-thrown
	 */
	public FunctionalTestsException(final Exception exception) {
		super(exception);
	}

}