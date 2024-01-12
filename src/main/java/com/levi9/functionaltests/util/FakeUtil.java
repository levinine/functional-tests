package com.levi9.functionaltests.util;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@UtilityClass
public class FakeUtil {

	/**
	 * Get valid Random Email Address.
	 *
	 * @return valid email as a {@link String}
	 */
	public String getRandomEmail() {
		return RandomStringUtils.randomAlphabetic(3) + RandomStringUtils.randomAlphanumeric(6) + "@mail.com";
	}

	/**
	 * Get Random Phone Number.
	 *
	 * @return phone number as a {@link String}
	 */
	public String getRandomPhoneNumber() {
		return RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(4);
	}

}
