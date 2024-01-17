package com.exavalu.migration.enhancer.replicatefilestotemplate;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReplicateFiles {
	
	@Value("${rootDirectory}")
	public String rootDirectory;
	
	private static final Logger log = LoggerFactory.getLogger(ReplicateFiles.class);
	@PostMapping("/modify")
	public void callReplicateFilesFromMule4ToTemplateMain(@RequestHeader String userInputFileName) {
		String sourceDirectory = "D:\\AnypointStudio1\\studio-workspace-migrated\\aspire-system-api-selfserve\\src\\main\\mule";
		String destination = rootDirectory + "mule-template-starter"+ "\\src\\main\\mule";
		replicateFilesFromMule4ToTemplateMain(sourceDirectory, destination, userInputFileName);
	}

	public static boolean replicateFilesFromMule4ToTemplateMain(String sourceDirectory, String destinationDirectory,
			String userInputFileName) {
		
		HashMap<String, String> newFileName = new HashMap<>();
		try {
			newFileName = extractValuesFromHeader(userInputFileName);
			createDestinationDirectories(sourceDirectory, destinationDirectory);
			replicateFilesToTemplateProject(sourceDirectory, destinationDirectory, newFileName);
			return true;
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
	}

	private static void createDestinationDirectories(String sourceDirectory, String destinationDirectory) {
		try {
			Files.walkFileTree(Paths.get(sourceDirectory), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
								throws IOException {
							Path relativePath = Paths.get(sourceDirectory).relativize(dir);
							Path destinationDir = Paths.get(destinationDirectory, relativePath.toString());
							Files.createDirectories(destinationDir);
							return FileVisitResult.CONTINUE;
						}
					});
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	private static void replicateFilesToTemplateProject(String sourceDirectory, String destinationDirectory,
			HashMap<String, String> newFileName) {
		try {
			Files.walkFileTree(Paths.get(sourceDirectory), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
					new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (file.toString().toLowerCase().endsWith(".xml")) {
								Path relativePath = Paths.get(sourceDirectory).relativize(file);

								// Check if the parent is null
								Path parentPath = relativePath.getParent();
								String parentString = (parentPath != null) ? parentPath.toString() : "";

								Path destinationFile = Paths.get(destinationDirectory, parentString, newFileName
										.getOrDefault(file.getFileName().toString(), file.getFileName().toString()));

								Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
				
								log.info("File copied: " + file + " to " + destinationFile);

								// Rename the file if necessary
								if (!newFileName.containsKey(file.getFileName().toString())) {
									// If the key is not present, copy with the original name
									log.info("Renaming file to: " + destinationFile.getFileName());
									Files.move(destinationFile, destinationFile.resolveSibling(file.getFileName()),
											StandardCopyOption.REPLACE_EXISTING);
								}
							}
							return FileVisitResult.CONTINUE;
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> extractValuesFromHeader(String input) {
		HashMap<String, String> fileMap = new HashMap<>();

		// Ensure the input string has a length greater than 2
		if (input.length() < 3) {
			log.info("Invalid input format: " + input);
			return fileMap;
		}

		// Remove brackets and split the string into individual mappings
		String[] mappings = input.substring(1, input.length() - 1).split(",");

		// Iterate through each mapping and extract values
		for (String mapping : mappings) {
			// Split each mapping into key-value pair
			String[] keyValue = mapping.replaceAll("\"", "").split("->");

			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				fileMap.put(key, value);

				
			} else {
				log.warn("Invalid mapping: " + mapping);
			}
		}
		return fileMap;
	}
}
