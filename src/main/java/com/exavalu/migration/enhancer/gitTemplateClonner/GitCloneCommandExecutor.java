package com.exavalu.migration.enhancer.gitTemplateClonner;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitCloneCommandExecutor {
	private static final Logger log = LoggerFactory.getLogger(GitCloneCommandExecutor.class);
	public static boolean cloneRepository(String gitCommand, String repositoryUrl, String destination) {
		ProcessBuilder processBuilder = new ProcessBuilder(gitCommand, "clone", repositoryUrl, destination);
		boolean result = false;

		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				result = true;

			} else {
				log.info("Error while cloning repository. Exit code: " + exitCode);
			}
		} catch (IOException | InterruptedException e) {
			log.error("Getting ERROR From clonning from gitHub :: " + e.toString());
		}
		return result;
	}

}
