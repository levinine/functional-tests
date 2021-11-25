package com.levi9.functionaltests.stepdefs.rest;

import com.levi9.functionaltests.typeregistry.Color;

import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class CukesStepdef {

	@Given("I have {int} cukes in my belly")
	public void iHaveNCukesInMyBelly(final int cukes) {
		log.info("Cukes in my belly: {}", cukes);
	}

	@Given("I have {float} cukes floated")
	public void iHaveFloatCukesInMyBelly(final float cukes) {
		log.info("Cukes in my belly: {}", cukes);
	}

	@Given("I have {word} cukes worded")
	public void iHaveWordCukesInMyBelly(final String cukes) {
		log.info("Cukes in my belly: {}", cukes);
	}

	@Given("I have {color} cukes")
	public void iHaveCustomCukesInMyBelly(final Color color) {
		log.info("Cukes in my belly with color: {}", color.getValue());
	}

}
