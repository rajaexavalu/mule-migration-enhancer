package com.exavalu.migration.enhancer.gitTemplateClonner;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;

public class ModifyClonnedTemplateName {

	private static final Logger log = LoggerFactory.getLogger(ModifyClonnedTemplateName.class);

	public static boolean modifyPOMxml(String pomFilePath, String targetAPIName) {
		log.info("pomFilePath : " + pomFilePath);
		boolean response = false;
		try {
			// Load and parse the XML file
			File xmlFile = new File(pomFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);

			// Normalize the document to correctly handle white spaces
			doc.getDocumentElement().normalize();

			// Get the element to update
			Element artifactId = (Element) doc.getElementsByTagName("artifactId").item(0);
			Element name = (Element) doc.getElementsByTagName("name").item(0);

			// Update the element value
			artifactId.setTextContent(targetAPIName.toLowerCase());
			name.setTextContent(targetAPIName.toLowerCase());

			// Save the changes to the XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);

			log.info("XML file updated successfully!");
			response = true;

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			log.error(e.toString());
		}
		return response;
	}
}
