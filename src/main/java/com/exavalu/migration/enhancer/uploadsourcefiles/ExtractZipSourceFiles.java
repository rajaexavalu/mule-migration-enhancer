package com.exavalu.migration.enhancer.uploadsourcefiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.exavalu.migration.enhancer.utils.UniqueStringIdGenerator;

/**
 * Utility class for extracting files from a ZIP archive.
 */
public class ExtractZipSourceFiles {
	private static final Logger log = LoggerFactory.getLogger(ExtractZipSourceFiles.class);

	public static boolean extractZip(MultipartFile file, String destDirectory) {
		byte[] buffer = new byte[1024];

		try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
			// Create the destination directory with unique name
			String formattedDateTime = UniqueStringIdGenerator.uniqueStringGenerator("yyyyMMdd");
			// Created the base path
			String path = destDirectory + File.separator + file.getOriginalFilename().replace(".zip", "_")
					+ formattedDateTime;

			File destDir = new File(path);
			// Check if a project directory already exists
			if (destDir.exists()) {
				formattedDateTime = UniqueStringIdGenerator.uniqueStringGenerator("yyyyMMddHHmmssSSSS");
				path = destDirectory + File.separator + file.getOriginalFilename().replace(".zip", "_")
						+ formattedDateTime;
				destDir = new File(path);
			}
			destDir.mkdirs();

			// Process each entry in the ZIP file
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				// Path for the current ZIP entry
				String filePath = path + File.separator + zipEntry.getName();
				File newFile = new File(filePath);

				// Create necessary directory structure if the entry is a directory
				if (!newFile.getParentFile().exists()) {
					new File(newFile.getParent()).mkdirs();
				}

				// Output the file path for each entry
				if (zipEntry.isDirectory()) {
					// Create directory if the entry is a directory
					newFile.mkdirs();
				} else {
					// Extract the file content if the entry is a file
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zipInputStream.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
			}

			// Indicate completion of extraction
			log.info("ZIP File Upload Extraction Has Been Completed.");

			// Get the list of files and directories in the given path
			Path returnPath = Paths.get(path);
			File[] files = returnPath.toFile().listFiles();

			// Check each file/directory
			if (files != null) {
				for (File contentFile : files) {
					if (contentFile.isDirectory()) {
						// Rename each directory by appending "_mule3"
						File oldfile = new File(contentFile.getPath());
						File modifyFile = new File(contentFile.getPath() + "_mule3");
						oldfile.renameTo(modifyFile);
					}
				}
			}
			return true;

		} catch (IOException ioException) {
			// Log and return false in case of an exception
			log.error("Error during ZIP File Extraction: " + ioException.toString());
			return false;
		}
	}
}
