package com.levi9.functionaltests.typeregistry;

import io.cucumber.java.ParameterType;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
public class ParameterTypes {

	@ParameterType("red|blue|yellow")
	public Color color(final String color) {
		return new Color(color);
	}

}
