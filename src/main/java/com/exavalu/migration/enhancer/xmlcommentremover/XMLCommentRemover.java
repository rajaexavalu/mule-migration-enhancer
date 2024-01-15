package com.exavalu.migration.enhancer.xmlcommentremover;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XMLCommentRemover {
	private static final Logger log = LoggerFactory.getLogger(XMLCommentRemover.class);

	public static void removeComments(String muleProjectPathUptoSrcMainMule) {
		// give the path of xml files location eg: ....src/main/mule where all xml files
		// are present
		Path inputFolder = Paths.get(muleProjectPathUptoSrcMainMule);

		try {
			// traverse over each xml file under given folder path or directory path
			Files.walkFileTree(inputFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					if (file.toString().toLowerCase().endsWith(".xml")) {
//						removeXMLComments(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	public static void removeXMLComments(String inputFile) {

		try {
			File xmlFile = new File(inputFile);
			Path xmlFilePath =  xmlFile.toPath();
			log.info("Processing file: " + xmlFilePath);

			String content = Files.readString(xmlFilePath);
			// this expression remove multi line xml comments
			content = content.replaceAll("<!--(.*?)-->", "");

			Files.write(xmlFilePath, content.getBytes());
			log.info("Finished processing: " + inputFile);
		} catch (IOException e) {
			log.error(e.toString());
		}
	}
}
