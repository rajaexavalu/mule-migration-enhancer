package com.exavalu.migration.enhancer.mmaConversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MMAExecutible {
	private static final Logger log = LoggerFactory.getLogger(MMAExecutible.class);

	public static String migrationHelper(String mule3ProjectLocation, String migratedProjectLocation) {
		try {
			// Specify paths
			String resourceName = "mma_ executable_file";

			// Create a ClassPathResource
			Resource resource = new ClassPathResource(resourceName);

			// Obtain the absolute file path
			File file = resource.getFile();
			String absolutePath = file.getAbsolutePath();

			// Command to execute Mule Migration Assistant
			String[] command = { "java", "-jar", "mule-migration-assistant-runner-1.4.1-SNAPSHOT.jar", "-muleVersion",
					"4.4.0", "-projectBasePath", mule3ProjectLocation, "-destinationProjectBasePath",
					migratedProjectLocation };

			// Run the command
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(new File(absolutePath)); // Set the working directory
			Process process = processBuilder.start();

			// Capture output streams
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			// Read output streams
			readStream(inputStream);
			readStream(errorStream);

			// Wait for the process to complete
			int exitCode = process.waitFor();

			// Check the exit code to verify if the process completed successfully
			if (exitCode == 0) {
				log.info("Migration successful.");
			} else {
				log.info("Migration failed. Check the logs for details.");
			}
		} catch (IOException | InterruptedException e) {

			log.error(e.toString());
		}
		return "migration executed see the logs!";
	}

	private static void readStream(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			log.info(line);
		}
	}

}
