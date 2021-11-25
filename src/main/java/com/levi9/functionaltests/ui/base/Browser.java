package com.levi9.functionaltests.ui.base;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * Browser Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum Browser {

	CHROME("webdriver.chrome.driver"),
	FIREFOX("webdriver.gecko.driver");

	@Getter
	private final String systemVariable;

	Browser(final String systemVariable) {
		this.systemVariable = systemVariable;
	}

	/**
	 * Gets Browser option from String. If Browser is not found for passed string, default one (CHROME) is returned.
	 *
	 * @param browser
	 *
	 * @return {@link Browser}
	 */
	public static Browser getEnum(@Nullable final String browser) {
		if (!StringUtils.isBlank(browser)) {
			return (null != Browser.valueOf(browser.toUpperCase())) ? Browser.valueOf(browser.toUpperCase()) : CHROME;
		} else {
			return CHROME;
		}
	}

}