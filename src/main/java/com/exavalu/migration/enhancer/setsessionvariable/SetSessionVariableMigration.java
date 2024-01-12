package com.exavalu.migration.enhancer.setsessionvariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.exavalu.migration.enhancer.mule3apikitexceptiontoerrorhandler.ExceptionToErrorHandlingHelperFunction;

@RestController
public class SetSessionVariableMigration {

	private static final Logger log = LoggerFactory.getLogger(SetSessionVariableMigration.class);

	@PostMapping("/set-session-variable")
	public static void callSetSessionVaribaleReplacerMethod() {

		log.info("setSessionVaribaleReplacerMethod called!");

		// give upto folder path under which all xml are present eg: ...src\main\mule
		setSessionVariableReplacerMainMethod("E:\\xmlFiles");
	}

	// give the path of xml files location eg: ....src/main/mule where all xml files
	// are present
	public static void setSessionVariableReplacerMainMethod(String muleXMLfolderPath) {

		Path inputFolder = Paths.get(muleXMLfolderPath);

		try {
			// traverse over each xml file under given folder path or directory path
			Files.walkFileTree(inputFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					if (file.toString().toLowerCase().endsWith(".xml")) {
						setSessionVariableReplacerLogic(file);
						log.info("xml file processed: " + file.toString());
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	// this method replaces set-session-variable to set-variable from mule4 migrated
	// xml file
	private static void setSessionVariableReplacerLogic(Path muleXmlFilePath) {

		try {
			File xmlFile = muleXmlFilePath.toFile();
			// File xmlFile = new File(muleXmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);

			// Normalize the document to correctly handle whitespaces
			doc.getDocumentElement().normalize();

			// Get the root element of the XML document
			Element rootElement = doc.getDocumentElement();
			// Define an array of element names to iterate over
			String[] elementNames = { "flow", "sub-flow" };

			// Iterate through each "<flow>" and "<sub-flow>" element
			for (String elementName : elementNames) {
				NodeList flowElements = rootElement.getElementsByTagName(elementName);
				// Iterate through each "<flow>" element
				for (int i = 0; i < flowElements.getLength(); i++) {
					Element flowElement = (Element) flowElements.item(i);
					NodeList setSessionVariableElements = flowElement
							.getElementsByTagName("compatibility:set-session-variable");
					// Collect the elements to replace in a separate list
					ArrayList<Element> elementsToReplace = new ArrayList<>();
					for (int j = 0; j < setSessionVariableElements.getLength(); j++) {
						elementsToReplace.add((Element) setSessionVariableElements.item(j));
					}
					// Iterate over the list and perform the replacements
					for (Element setSessionVariableElement : elementsToReplace) {
						// Rename the element to "set-variable"
						Element setVariableElement = doc.createElement("set-variable");
						NamedNodeMap attributes = setSessionVariableElement.getAttributes();
						for (int k = 0; k < attributes.getLength(); k++) {
							Attr attribute = (Attr) attributes.item(k);
							setVariableElement.setAttribute(attribute.getName(), attribute.getValue());
						}
						// Replace the old element with the new one
						flowElement.replaceChild(setVariableElement, setSessionVariableElement);
					}
				}
			}
			// save the xml file
			ExceptionToErrorHandlingHelperFunction.saveXMLFile(doc, xmlFile);

		} catch (DOMException | ParserConfigurationException | SAXException | IOException | TransformerException e) {
			log.error(e.toString());
		}
	}
}
