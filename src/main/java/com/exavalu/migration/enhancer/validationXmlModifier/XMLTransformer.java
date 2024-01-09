package com.exavalu.migration.enhancer.validationXmlModifier;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;

public class XMLTransformer {
	// Helper class for writing the modified XML back to the file
	public static void writeXmlFile(Document document, String filePath) throws IOException {

		// Use a Transformer for output
		try {
			// Save the modified document to the file
			javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory
					.newInstance();
			javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
			javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(document);
			javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(
					new File(filePath));
			transformer.transform(source, result);

		} catch (javax.xml.transform.TransformerException e) {
			e.printStackTrace();
		}
	}

}
