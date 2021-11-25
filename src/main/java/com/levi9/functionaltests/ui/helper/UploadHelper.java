/**
 *
 */
package com.levi9.functionaltests.ui.helper;

import java.io.File;
import java.net.URISyntaxException;

import org.openqa.selenium.By;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import groovy.util.logging.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class UploadHelper {

	private final EventFiringWebDriver driver;

	public UploadHelper(final EventFiringWebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Method for selecting file for upload with relative path. <br>
	 * This method will just select element for uploading, triggering upload is not done by this method.
	 *
	 * @param clazz            the class
	 * @param xpath            the xpath of upload element (type = 'file') as a String
	 * @param relativeFilePath the relative path to the file
	 * @throws URISyntaxException
	 */
	public void selectFile(final Class<?> clazz, final String xpath, final String relativeFilePath) throws URISyntaxException {
		final File file = new File(clazz.getClassLoader().getResource(relativeFilePath).toURI());
		driver.executeScript(
			"var element = document.evaluate('" + xpath.replace("'", "\"") +
				"', document, null, 9, null).singleNodeValue; element.style.visibility='visible';");
		driver.findElement(By.xpath(xpath)).sendKeys(file.getAbsolutePath());
	}

}
