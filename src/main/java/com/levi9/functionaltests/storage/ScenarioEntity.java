package com.levi9.functionaltests.storage;

import com.levi9.functionaltests.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import io.cucumber.java.Scenario;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ScenarioEntity {

	private String scenarioName;

	private String fileDirectory;

	private String workingDirectory;

	@Getter(AccessLevel.NONE)
	private int screenshotCounter = 0;

	private Scenario scenario;

	/**
	 * Return Test Directory which will be named based on scenario name.
	 *
	 * @return scenario directory
	 */
	private String getScenarioDirectory() {
		return StringUtils.capitalize(getScenarioName()).replaceAll("[^a-zA-Z0-9\\s]", "");
	}

	/**
	 * Get path to Scenario artifacts.
	 * Path is created based on working directory + /target/test-artifacts/ + scenario directory.
	 *
	 * @return scenario location path
	 */
	public String getScenarioLocationPath() {
		return getWorkingDirectory() + "/target/test-artifacts/" + getScenarioDirectory();
	}

	/**
	 * Get location path where all screenshots of Scenario must be saved.
	 *
	 * @return path of scenario screenshots
	 */
	public String getScenarioScreenshotsLocationPath() {
		return getScenarioLocationPath() + "/screenshots/";
	}

	/**
	 * Get Screenshot Counter and increase it by one.
	 *
	 * @return screenshot counter
	 */
	public int getScreenshotCounter() {
		return screenshotCounter++;
	}

	/**
	 * Get location path where all PDFs of Scenario must be saved.
	 *
	 * @return path of scenario screenshots
	 */
	public String getScenarioPdfsLocationPath() {
		return getScenarioLocationPath() + "/pdfs/";
	}

	/**
	 * Get location path where all PDFs of Scenario must be saved.
	 *
	 * @return path of scenario screenshots
	 */
	public String getScenarioHtmlsLocationPath() {
		return getScenarioLocationPath() + "/htmls/";
	}

	/**
	 * Embed picture to a scenario step definition by image url
	 */
	public void embedPicture(final String imageUrl) {
		try (final InputStream in = new BufferedInputStream(URI.create(imageUrl).toURL().openStream());
			 final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			final String mediaType = "image/jpeg";
			final byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			final byte[] response = out.toByteArray();
			log.info("Embedding dog image to step definition");
			getScenario().attach(response, mediaType, "Random embedded dog image to step definition...");
		} catch (final IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Embed html to a scenario step definition
	 */
	public void embedHtml(final String htmlContent) {
		FileUtil.writeHtmlFile(getScenarioLocationPath(), getScenarioName() + ".html", htmlContent);
		try {
			final URI url = Paths.get("target/test-artifacts/" + getScenarioName() + "/", getScenarioName() + ".html").toUri();
			scenario.attach(Files.readAllBytes(Paths.get(url)), "text/html", "html page...");
		} catch (final IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Embed Pdf to Scenario step definition
	 */
	public void embedPdfToScenario() {
		try {
			final URI pdfURI = Objects.requireNonNull(getClass().getClassLoader().getResource("test-data/pdfs/dummy.pdf")).toURI();
			scenario.attach(Files.readAllBytes(Paths.get(pdfURI)), "application/pdf", "Pdf document...");
		} catch (final URISyntaxException | IOException e) {
			log.error(e.getMessage());
		}
	}
}