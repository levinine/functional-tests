package com.levi9.functionaltests.ui.base;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * Grid Enum.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public enum Grid {

	NONE(""),
	REMOTE("http://hub:4444/wd/hub");

	@Getter
	public final String url;

	Grid(final String gridUrl) {
		this.url = gridUrl;
	}

	/**
	 * Gets Browser option from String. If Browser is not found for passed string, default one (NONE) is returned.
	 *
	 * @param grid
	 *
	 * @return {@link Browser}
	 */
	public static Grid getEnum(@Nullable final String grid) {
		if (!StringUtils.isBlank(grid)) {
			return (null != Grid.valueOf(grid.toUpperCase())) ? Grid.valueOf(grid.toUpperCase()) : NONE;
		} else {
			return NONE;
		}
	}
}