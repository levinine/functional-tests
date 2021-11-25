package com.levi9.functionaltests.ui.base;

import org.apache.commons.lang3.SystemUtils;

import lombok.Getter;

/**
 * OS Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum Os {

	WINDOWS32("drivers/win32/", Constants.GECKODRIVER, Constants.CHROMEDRIVER, ".exe"),
	WINDOWS64("drivers/win64/", Constants.GECKODRIVER, Constants.CHROMEDRIVER, ".exe"),
	LINUX32("drivers/linux32/", Constants.GECKODRIVER, Constants.CHROMEDRIVER, ""),
	LINUX64("drivers/linux64/", Constants.GECKODRIVER, Constants.CHROMEDRIVER, ""),
	MACOS("drivers/macos/", Constants.GECKODRIVER, Constants.CHROMEDRIVER, "");

	@Getter
	public final String geckoPath;
	@Getter
	public final String chromePath;
	@Getter
	public final String prefix;
	@Getter
	public final String suffix;

	/**
	 * Instantiates a new operating system.
	 *
	 * @param prefix     the prefix
	 * @param geckoPath  the gecko path
	 * @param chromePath the chrome path
	 * @param suffix     the suffix
	 */
	Os(final String prefix, final String geckoPath, final String chromePath, final String suffix) {
		this.prefix = prefix;
		this.geckoPath = geckoPath;
		this.chromePath = chromePath;
		this.suffix = suffix;
	}

	/**
	 * Determines and returns the operating system on which code is executed.
	 *
	 * @return {@link Os}
	 */
	public static Os getEnum() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return SystemUtils.OS_ARCH.contains("32") ? Os.WINDOWS32 : Os.WINDOWS64;
		} else if (SystemUtils.IS_OS_LINUX) {
			return SystemUtils.OS_ARCH.contains("32") ? Os.LINUX32 : Os.LINUX64;
		} else if (SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_MAC) {
			return Os.MACOS;
		}
		throw new IllegalStateException("Unknown OS: " + SystemUtils.OS_NAME);
	}

	private static class Constants {

		public static final String GECKODRIVER = "geckodriver";
		public static final String CHROMEDRIVER = "chromedriver";
	}
}