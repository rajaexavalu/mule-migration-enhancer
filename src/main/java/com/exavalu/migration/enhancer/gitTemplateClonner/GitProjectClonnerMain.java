package com.exavalu.migration.enhancer.gitTemplateClonner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitProjectClonnerMain {
	private static final Logger log = LoggerFactory.getLogger(GitProjectClonnerMain.class);

	@Value("${rootDirectory}")
	public String rootDirectory;
	@Value("${gitRepositoryUrl}")
	public String gitRepositoryUrl;
	@Value("${userName}")
	public String userName;
	@Value("${token}")
	public String token;

	@PostMapping("/clone-mule-template")
	public String cloneMethodHandler(@RequestHeader("targetAPIName") String targetAPIName) {

		// Replace with your repository URL
		String repositoryUrl = gitRepositoryUrl;
		log.info("git repo url:" + repositoryUrl);

		// Replace with your desired destination path
		String destination = rootDirectory + targetAPIName.toLowerCase();

		log.info("Destination: " + destination);

		// Check if the destination folder exists; if not, create it
		File destinationFolder = new File(destination);
		String response = "";
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();

			boolean isClonningSuccessful = GitCloneCommandExecutor.cloneRepository(repositoryUrl, destination, userName,
					token);
			if (isClonningSuccessful) {
				response = "Repository cloned successfully!";
				String pomXMLPath = destination + "\\" + "pom.xml";
				boolean changePOMxmlProjectName = ModifyClonnedTemplateName.modifyPOMxml(pomXMLPath, targetAPIName);
				if (changePOMxmlProjectName) {
					log.info("pom.xml updated");
				} else {
					log.error("failed to update the pom");
				}
				return response;
			} else {
				response = "unsuccessfull! please try again";
				log.error(response);
				return response;
			}

		} else {
			response = "destination path already exist";
			log.error(response);
			return response;
		}
	}
}
