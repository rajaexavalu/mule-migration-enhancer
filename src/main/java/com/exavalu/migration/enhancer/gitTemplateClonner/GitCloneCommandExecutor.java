package com.exavalu.migration.enhancer.gitTemplateClonner;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GitCloneCommandExecutor {

	private static final Logger log = LoggerFactory.getLogger(GitCloneCommandExecutor.class);
    public static boolean cloneRepository(String repositoryUrl, String destinationPath, String userName, String token) {
    	
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(destinationPath))
                    .setCredentialsProvider(getCredentialsProvider(userName, token));

            Git git = cloneCommand.call();
            log.info("Repository cloned successfully to: " + git.getRepository().getDirectory());
            return true;
        } catch (Exception e) {
        	log.error(e.toString());
           
            return false;
        }
    }
	 private static CredentialsProvider getCredentialsProvider(String userName, String token) {
	        // This uses JschConfigSessionFactory to pick up credentials from the Windows Credential Manager
	        return new UsernamePasswordCredentialsProvider(userName, token);
	    }
}
