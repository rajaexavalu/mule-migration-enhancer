package com.exavalu.migration.enhancer.rest.main;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exavalu.migration.enhancer.components.idempotent.IdempotentXMLModifier;
import com.exavalu.migration.enhancer.flowcontrol.roundrobin.RoundRobinXMLModifier;
import com.exavalu.migration.enhancer.gitTemplateClonner.GitProjectClonnerMain;
import com.exavalu.migration.enhancer.global.declaration.AppGlobalDeclaration;
import com.exavalu.migration.enhancer.inboundoutboundproperties.RemoveInboundOutboundProperties;
import com.exavalu.migration.enhancer.mmaConversion.MMAExecutible;
import com.exavalu.migration.enhancer.mule3apikitexceptiontoerrorhandler.Mule3ExceptionToErrorMappingLogic;
import com.exavalu.migration.enhancer.objectstoremodify.ConfigureManagedStore;
import com.exavalu.migration.enhancer.objectstoremodify.ConfigureObjectStore;
import com.exavalu.migration.enhancer.propertymodifier.ModifyProperty;
import com.exavalu.migration.enhancer.replicatefilestotemplate.ReplicateFiles;
import com.exavalu.migration.enhancer.setsessionvariable.SetSessionVariableMigration;
import com.exavalu.migration.enhancer.uploadsourcefiles.ExtractZipSourceFiles;
import com.exavalu.migration.enhancer.validationXmlModifier.ModifyValidator;
import com.exavalu.migration.enhancer.vmqueue.VmQueueModifier;
import com.exavalu.migration.enhancer.xmlcommentremover.XMLCommentRemover;

@RestController
@RequestMapping("/v1")
public class UploadSourceFiles {
	private static final Logger log = LoggerFactory.getLogger(UploadSourceFiles.class);

	@Value("${gitRepositoryUrl}")
	public String gitRepositoryUrl;
	@Value("${userName}")
	public String userName;
	@Value("${token}")
	public String token;

	@Value("${rootDirectory}")
	private String rootDirectory;

	@Value("${scopes.mule3ApikitExceptionToErrorHandler}")
	private boolean mule3ApikitExceptionToErrorHandler;

	@Value("${scopes.inboundOutboundProperties}")
	private boolean inboundOutboundProperties;

	@Value("${scopes.idempotentXMLModifier}")
	private boolean idempotentXMLModifier;

	@Value("${scopes.roundrobinXMlModifier}")
	private boolean roundrobinXMlModifier;

	@Value("${scopes.objectStoreModify.configureManagedStore}")
	private boolean configureManagedStore;

	@Value("${scopes.objectStoreModify.configureObjectStore}")
	private boolean configureObjectStore;

	@Value("${scopes.propertyModifier}")
	private boolean propertyModifier;

	@Value("${scopes.setSessionVariableToSetVariable}")
	private boolean setSessionVariableToSetVariable;

	@Value("${scopes.validationXMLModifier}")
	private boolean validationXMLModifier;

	@Value("${scopes.vmqueue}")
	private boolean vmqueue;

	@Value("${scopes.xmlCommentRemover}")
	private boolean xmlCommentRemover;

	// Endpoint for uploading and extracting a Mule3 ZIP file
	@PostMapping("/upload-mule3-zip")
	public ResponseEntity<String> cloneGitProject(@RequestBody MultipartFile file,
			@RequestHeader("targetAPIName") String targetAPIName,
			@RequestHeader("userInputFileName") String userInputFileName) {

		callSetAppGlobalDeclaration();

		if (file.isEmpty()) {
			// Return a BAD_REQUEST response if no file is provided
			return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
		}

		// call mma conversion class
		String mule3Projectpath = ExtractZipSourceFiles.extractZip(file, rootDirectory);
		String mule4MigratedPath = mule3Projectpath.replace("_mule3", "_mule4");
		//migration of mule 3 project
		MMAExecutible.migrationHelper(mule3Projectpath, mule4MigratedPath);

		// template clonning process
		String templateClonePath = mule3Projectpath.replace("_mule3", "_final-template");
		GitProjectClonnerMain cloneMethod = new GitProjectClonnerMain();
		String destinationOfCloneTemplate = cloneMethod.cloneMethodHandler(gitRepositoryUrl, userName, token,
				targetAPIName, templateClonePath);
		// replication of xml files under src\\main\\mule process
		// destination directory string
		String sourceDirectoryForReplication = "";
		boolean isReplicationSuccessfull = false;
		
		if (destinationOfCloneTemplate != "unsuccessfull") {
			destinationOfCloneTemplate = destinationOfCloneTemplate + "\\src\\main\\mule";
			// source directory String
			sourceDirectoryForReplication = mule4MigratedPath + "\\src\\main\\mule";
			isReplicationSuccessfull = ReplicateFiles.replicateFilesFromMule4ToTemplateMain(
					sourceDirectoryForReplication, destinationOfCloneTemplate, userInputFileName);
		}else {
			return new ResponseEntity<>("git template clonning unsuccessfull, please see the logs!", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info("mule3Projectpath: " + mule3Projectpath);
		log.info("Replication successfull: " + isReplicationSuccessfull);
		if (isReplicationSuccessfull) {
			try {
				// give the path of xml files location eg: ....src/main/mule where all xml files
				// are present
				Path inputFolder = Paths.get(destinationOfCloneTemplate);
				// traverse over each xml file under given folder path or directory path
				Files.walkFileTree(inputFolder, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						String xmlFilePath = file.toString();
						if (file.toString().toLowerCase().endsWith(".xml")) {
							// functions to call for eaxh xml file
							callOperationsOverXML(xmlFilePath);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				log.error(e.toString());
			}
		}else {
			return new ResponseEntity<>("Replication unsuccessFull please see the logs!", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return new ResponseEntity<>("process completed successfully!", HttpStatus.OK);
	}

	// helper function to set the variable in APPGlobalDeclaration
	public void callSetAppGlobalDeclaration() {
		AppGlobalDeclaration.setMule3ApikitExceptionToErrorHandler(mule3ApikitExceptionToErrorHandler);
		AppGlobalDeclaration.setIdempotentXMLModifier(idempotentXMLModifier);
		AppGlobalDeclaration.setRoundrobinXMlModifier(roundrobinXMlModifier);
		AppGlobalDeclaration.setConfigureManagedStore(configureManagedStore);
		AppGlobalDeclaration.setConfigureObjectStore(configureObjectStore);
		AppGlobalDeclaration.setPropertyModifier(propertyModifier);
		AppGlobalDeclaration.setSetSessionVariableToSetVariable(setSessionVariableToSetVariable);
		AppGlobalDeclaration.setValidationXMLModifier(validationXMLModifier);
		AppGlobalDeclaration.setVmqueue(vmqueue);
		AppGlobalDeclaration.setXmlCommentRemover(xmlCommentRemover);
		AppGlobalDeclaration.setInboundOutboundProperties(inboundOutboundProperties);
	}

	public void callOperationsOverXML(String xmlFilePath) {
		if (AppGlobalDeclaration.isMule3ApikitExceptionToErrorHandler()) {
			Mule3ExceptionToErrorMappingLogic.createErrorHandlerElement(xmlFilePath);
		}
		if (AppGlobalDeclaration.isInboundOutboundProperties()) {
			RemoveInboundOutboundProperties.inboundOutboundRemover(xmlFilePath);
		}
		if (AppGlobalDeclaration.isIdempotentXMLModifier()) {
			IdempotentXMLModifier.idempotentmodifier(xmlFilePath);
		}
		if (AppGlobalDeclaration.isRoundrobinXMlModifier()) {
			RoundRobinXMLModifier.roundrobinmodifier(xmlFilePath);
		}
		if (AppGlobalDeclaration.isConfigureManagedStore()) {
			ConfigureManagedStore.managedstoremodifier(xmlFilePath);
		}
		if (AppGlobalDeclaration.isConfigureObjectStore()) {
			ConfigureObjectStore.configObjectStore(xmlFilePath);
		}
		if (AppGlobalDeclaration.isPropertyModifier()) {
			ModifyProperty.modifyProperty(xmlFilePath);
		}
		if (AppGlobalDeclaration.isSetSessionVariableToSetVariable()) {
			SetSessionVariableMigration.setSessionVariableReplacerLogic(xmlFilePath);
		}
		if (AppGlobalDeclaration.isValidationXMLModifier()) {
			ModifyValidator.modifyXml(xmlFilePath);
		}
		if (AppGlobalDeclaration.isVmqueue()) {
//			VmQueueModifier.vmQueueModifier(xmlFilePath, "");
		}
		if (AppGlobalDeclaration.isXmlCommentRemover()) {
			XMLCommentRemover.removeXMLComments(xmlFilePath);
		}
	}

}
