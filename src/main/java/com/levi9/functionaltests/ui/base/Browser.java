package com.levi9.functionaltests.ui.base;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

/**
 * Browser Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum Browser {

	CHROME,
	FIREFOX;

	/**
	 * Gets Browser option from String. If Browser is not found for passed string, default one (CHROME) is returned.
	 *
	 * @param browser browser
	 *
	 * @return {@link Browser}
	 */
	public static Browser getEnum(@Nullable final String browser) {
		if (!StringUtils.isBlank(browser)) {
			return Browser.valueOf(browser.toUpperCase());
		} else {
			return CHROME;
		}
	}
}