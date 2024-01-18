package com.levi9.functionaltests.ui.helpers;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import groovy.util.logging.Slf4j;
import lombok.Getter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@SuppressWarnings({ "unused" })
@Slf4j
@Getter
public class UploadHelper {

	private final WebDriver driver;
	private final JavascriptExecutor js;

	public UploadHelper(final WebDriver driver) {
		this.driver = driver;
		this.js = (JavascriptExecutor) driver;
	}

	/**
	 * Method for selecting file for upload with relative path. <br>
	 * This method will just select element for uploading, triggering upload is not done by this method.
	 *
	 * @param clazz            the class
	 * @param xpath            the xpath of upload element (type = 'file') as a String
	 * @param relativeFilePath the relative path to the file
	 */
	public void selectFile(final Class<?> clazz, final String xpath, final String relativeFilePath) throws URISyntaxException {
		final File file = new File(Objects.requireNonNull(clazz.getClassLoader().getResource(relativeFilePath)).toURI());
		getJs().executeScript("var element = document.evaluate('" + xpath.replace("'", "\"") +
			"', document, null, 9, null).singleNodeValue; element.style.visibility='visible';");
		driver.findElement(By.xpath(xpath)).sendKeys(file.getAbsolutePath());
	}

}
