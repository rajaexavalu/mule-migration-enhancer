package com.exavalu.migration.enhancer.xmlcommentremover;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class XMLCommentRemover {
	public static void removeComments(String muleProjectPathUptoSrcMainMule) {
		// give the path of xml files location eg: ....src/main/mule where all xml files
		// are present
		Path inputFolder = Paths.get(muleProjectPathUptoSrcMainMule);

		try {
			//traverse over each xml file under given folder path or directory path
			Files.walkFileTree(inputFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toString().toLowerCase().endsWith(".xml")) {
						removeXMLComments(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void removeXMLComments(Path inputFile) throws IOException {
		System.out.println("Processing file: " + inputFile);

		String content = Files.readString(inputFile);
		// this expression remove multi line xml comments
		content = content.replaceAll("(?s)<!--(.*?)-->", "");

		Files.write(inputFile, content.getBytes());

		System.out.println("Finished processing: " + inputFile);
	}

}
