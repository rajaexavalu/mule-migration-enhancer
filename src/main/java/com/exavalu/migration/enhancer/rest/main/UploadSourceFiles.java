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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exavalu.migration.enhancer.components.idempotent.IdempotentXMLModifier;
import com.exavalu.migration.enhancer.flowcontrol.roundrobin.RoundRobinXMLModifier;
import com.exavalu.migration.enhancer.global.declaration.AppGlobalDeclaration;
import com.exavalu.migration.enhancer.inboundoutboundproperties.RemoveInboundOutboundProperties;
import com.exavalu.migration.enhancer.mmaConversion.MMAExecutible;
import com.exavalu.migration.enhancer.mule3apikitexceptiontoerrorhandler.Mule3ExceptionToErrorMappingLogic;
import com.exavalu.migration.enhancer.objectstoremodify.ConfigureManagedStore;
import com.exavalu.migration.enhancer.objectstoremodify.ConfigureObjectStore;
import com.exavalu.migration.enhancer.propertymodifier.ModifyProperty;
import com.exavalu.migration.enhancer.setsessionvariable.SetSessionVariableMigration;
import com.exavalu.migration.enhancer.uploadsourcefiles.ExtractZipSourceFiles;
import com.exavalu.migration.enhancer.validationXmlModifier.ModifyValidator;
import com.exavalu.migration.enhancer.vmqueue.VmQueueModifier;
import com.exavalu.migration.enhancer.xmlcommentremover.XMLCommentRemover;

@RestController
public class UploadSourceFiles {
	private static final Logger log = LoggerFactory.getLogger(UploadSourceFiles.class);

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
	public ResponseEntity<String> cloneGitProject(@RequestBody MultipartFile file) {

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

		if (file.isEmpty()) {
			// Return a BAD_REQUEST response if no file is provided
			return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
		}

		// call mma conversion class
		String mule3Projectpath = ExtractZipSourceFiles.extractZip(file, rootDirectory);
		String mule4MigratedPath = mule3Projectpath.replace("_mule3", "_mule4");
		System.out.println("mule3Projectpath: " + mule3Projectpath);
		String response = MMAExecutible.migrationHelper(mule3Projectpath, mule4MigratedPath);

		try {
			// give the path of xml files location eg: ....src/main/mule where all xml files
			// are present
			Path inputFolder = Paths.get(mule4MigratedPath + "\\src\\main\\mule");
			// traverse over each xml file under given folder path or directory path
			Files.walkFileTree(inputFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					String xmlFilePath = file.toString();
					if (file.toString().toLowerCase().endsWith(".xml")) {
						// functions to call for eaxh xml file
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
//							VmQueueModifier.vmQueueModifier(xmlFilePath, "");
						}
						if (AppGlobalDeclaration.isXmlCommentRemover()) {
							XMLCommentRemover.removeXMLComments(xmlFilePath);
						}

					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error(e.toString());
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
