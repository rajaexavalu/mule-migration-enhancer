package com.exavalu.migration.enhancer.expressioncomponentmodifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.exavalu.migration.enhancer.validationXmlModifier.XMLTransformer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Expression {
	private static final Logger log = LoggerFactory.getLogger(Expression.class);

	
	public boolean modifyExpression(String xmlpath) {
		boolean response = false;
		try {
			// Specify the path to your XML file
			String filePath = xmlpath;

			// Parse the XML file
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filePath);

			// Get the root element
			Element rootElement = doc.getDocumentElement();
			String[] elementNames = { "flow", "sub-flow" };
			for (String elementName : elementNames) {
				NodeList flowElements = rootElement.getElementsByTagName(elementName);
				for (int j = 0; j < flowElements.getLength(); j++) {
					// Find all <expression-component> elements
					NodeList expressionComponentElements = rootElement.getElementsByTagName("expression-component");
					// Iterate through each <expression-component> element
					for (int i = 0; i < expressionComponentElements.getLength(); i++) {
						Element expressionComponentNode = (Element) expressionComponentElements.item(i);

						// Check if the "file" attribute is absent
						if (!expressionComponentNode.hasAttribute("file")) {
							// Create a new <ee:transform> element
							Element transformElement = doc.createElement("ee:transform");
							transformElement.setAttribute("doc:name", expressionComponentNode.getAttribute("doc:name"));

							// Create the nested <ee:message> element
							Element messageElement = doc.createElement("ee:message");

							// Create the nested <ee:set-payload> element with CDATA
							Element setPayloadElement = doc.createElement("ee:set-payload");
							setPayloadElement
									.appendChild(doc.createCDATASection(expressionComponentNode.getTextContent()));

							// Append elements to construct the new structure
							messageElement.appendChild(setPayloadElement);
							transformElement.appendChild(messageElement);

							// Replace the old element with the new <ee:transform> structure
							expressionComponentNode.getParentNode().replaceChild(transformElement,
									expressionComponentNode);
						}
						if (expressionComponentNode.hasAttribute("file")) {
							Element transformElement = doc.createElement("ee:transform");
							transformElement.setAttribute("doc:name", expressionComponentNode.getAttribute("doc:name"));
//                        transformElement.setAttribute("doc:id", expressionComponentNode.getAttributes().getNamedItem("doc:id").getNodeValue());

							// Create the nested <ee:message> element
							Element messageElement = doc.createElement("ee:message");

							// Create the nested <ee:set-payload> element with CDATA
							Element setPayloadElement = doc.createElement("ee:set-payload");
							setPayloadElement.appendChild(
									doc.createCDATASection("file:" + expressionComponentNode.getAttribute("file")));

							// Append elements to construct the new structure
							messageElement.appendChild(setPayloadElement);
							transformElement.appendChild(messageElement);

							// Replace the old element with the new <ee:transform> structure
							expressionComponentNode.getParentNode().replaceChild(transformElement,
									expressionComponentNode);

						}
					}

				}
			}

			doc.normalize();
			XMLTransformer.writeXmlFile(doc, filePath);

			log.info("XML file has been modified successfully.");
			response = true;

		} catch (Exception e) {
			log.error("Error modifying XML file", e);
		}

		return response;
	}
}
