package com.exavalu.migration.enhancer.validationXmlModifier;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModifyValidator {
	public static boolean modifyXml(String xmlPath) {
		boolean response = false;
		try {
			// Specify the path to your XML file
			String filePath = xmlPath;

			// Parse the XML file
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filePath);

			// Get the root element
			Element rootElement = doc.getDocumentElement();

			// Find all <when> elements
			NodeList whenElements = rootElement.getElementsByTagName("when");

			for (int i = 0; i < whenElements.getLength(); i++) {
				Node node = whenElements.item(i);

				// Check if the node is an element
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element whenElement = (Element) node;

					// Find the expression attribute and update its value if it contains
					// "validator.validateEmail"
					Node expressionNode = whenElement.getAttributes().getNamedItem("expression");
					if (expressionNode != null) {
						String expressionValue = expressionNode.getNodeValue();

						// Replace only if the expression contains "validator.validateEmail"
						if (expressionValue.contains("mel:validator.validateEmail")) {
							String newValue = expressionValue.replace("mel:validator.validateEmail",
									"Validation::isEmail");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.validateUrl")) {
							String newValue = expressionValue.replace("mel:validator.validateUrl", "Validation::isUrl");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.validateIp")) {
							String newValue = expressionValue.replace("mel:validator.validateIp", "Validation::isIp");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.isTime")) {
							String newValue = expressionValue.replace("mel:validator.isTime", "Validation::isTime");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.notEmpty")) {
							String newValue = expressionValue.replace("mel:validator.notEmpty", "!isEmpty");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("validator.isEmpty")) {
							String newValue = expressionValue.replace("validator.isEmpty", "isEmpty");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.isNull")) {
							String newValue = expressionValue.replace("mel:validator.isNull", "isEmpty");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
						if (expressionValue.contains("mel:validator.isNotNull")) {
							String newValue = expressionValue.replace("mel:validator.isNotNull", "!isEmpty");
							expressionNode.setNodeValue(newValue);

							System.out.println("Modified expression from: " + expressionValue + " to: " + newValue);
						}
					}
				}
			}

			// Save the changes back to the XML file
			// Note: This may overwrite the original file, so be careful
			// You can save it to a new file if needed
			// For simplicity, I'm using the same file here
			doc.normalize();
			XMLTransformer.writeXmlFile(doc, filePath);

			System.out.println("XML file has been modified successfully.");
			response = true;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}

}
