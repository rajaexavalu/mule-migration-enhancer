package com.exavalu.migration.enhancer.components.idempotent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IdempotentXMLModifier {
	private static final Logger log = LoggerFactory.getLogger(IdempotentXMLModifier.class);

	public static void idempotentmodifier(String xmlpath) {

		String filePath = xmlpath;

		try {
			// Read XML content from the file
			File inputFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(inputFile);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			// Find the <idempotent-message-validator> element
			NodeList idempotentList = document.getElementsByTagName("idempotent-message-validator");
			if (idempotentList.getLength() > 0) {
				Element idempotentElement = (Element) idempotentList.item(0);

				// Remove the throwOnUnaccepted attribute
				idempotentElement.removeAttribute("throwOnUnaccepted");

				// Output the modified XML content
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StringWriter writer = new StringWriter();
				transformer.transform(source, new StreamResult(writer));
				String modifiedXmlString = writer.toString();

				// Write the modified XML back to the file
				try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
					byte[] bytes = modifiedXmlString.getBytes();
					outputStream.write(bytes);
					log.info("Modified Idempotent XML written to the file successfully.");
				}
			} else {
				log.warn("No <idempotent-message-validator> element found in the XML.");
			}

		} catch (Exception e) {
			log.error("Error modifying XML:", e.toString());
		}
	}
}
