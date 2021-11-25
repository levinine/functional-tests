package com.levi9.functionaltests.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@UtilityClass
public class FileUtil {

	/**
	 * Create new directories and new html file with content in it
	 *
	 * @param filePath     path of directory where html file will be placed
	 * @param htmlFileName html file name
	 * @param htmlContent  html file content
	 */
	public void writeHtmlFile(final String filePath, final String htmlFileName, final String htmlContent) {

		final File directory = new File(filePath);

		if (!directory.exists()) {
			final boolean directoryCreated = directory.mkdirs();
			log.info("New directory " + filePath + " created: " + directoryCreated);
		}

		final File file = new File(String.format("%s/%s", filePath, htmlFileName));
		try (final FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
			 final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
			bufferedWriter.write(htmlContent);
		} catch (final IOException e) {
			log.error(Arrays.toString(e.getStackTrace()));
		}
	}
}
