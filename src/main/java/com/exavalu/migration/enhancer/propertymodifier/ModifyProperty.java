package com.exavalu.migration.enhancer.propertymodifier;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.exavalu.migration.enhancer.validationXmlModifier.XMLTransformer;

public class ModifyProperty {
	private static final Logger log = LoggerFactory.getLogger(ModifyProperty.class);

	public static boolean modifyProperty(String xmlPath) {
		boolean response = false;
		try {

			// Parse the XML file
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlPath);

			// Get the root element
			Element rootElement = doc.getDocumentElement();

			// Find all <compatibility:set-property> elements
			NodeList compatibilitySetPropertyElements = rootElement.getElementsByTagName("compatibility:set-property");
			// Iterate through each <compatibility:set-property> element
			for (int i = 0; i < compatibilitySetPropertyElements.getLength(); i++) {
				Node compatibilitySetPropertyNode = compatibilitySetPropertyElements.item(i);

				// Create a new <set-variable> element
				Element setVariableElement = doc.createElement("set-variable");
				setVariableElement.setAttribute("doc:name",
						compatibilitySetPropertyNode.getAttributes().getNamedItem("doc:name").getNodeValue());
				setVariableElement.setAttribute("variableName",
						compatibilitySetPropertyNode.getAttributes().getNamedItem("propertyName").getNodeValue());
				setVariableElement.setAttribute("value",
						compatibilitySetPropertyNode.getAttributes().getNamedItem("value").getNodeValue());

				// Replace <compatibility:set-property> with <set-variable>
				compatibilitySetPropertyNode.getParentNode().replaceChild(setVariableElement,
						compatibilitySetPropertyNode);

			}
			// Find all <compatibility:copy-properties> elements
			NodeList compatibilityCopyPropertyElements = rootElement
					.getElementsByTagName("compatibility:copy-properties");
			// Iterate through each <compatibility:copy-properties> element
			for (int i = 0; i < compatibilityCopyPropertyElements.getLength(); i++) {
				Node compatibilityCopyPropertyNode = compatibilityCopyPropertyElements.item(i);

				// Create a new <set-variable> element
				Element setVariableElement = doc.createElement("set-variable");
				setVariableElement.setAttribute("doc:name",
						compatibilityCopyPropertyNode.getAttributes().getNamedItem("doc:name").getNodeValue());
				setVariableElement.setAttribute("variableName",
						compatibilityCopyPropertyNode.getAttributes().getNamedItem("doc:name").getNodeValue());
				setVariableElement.setAttribute("value", ("#[attributes.headers."
						+ compatibilityCopyPropertyNode.getAttributes().getNamedItem("propertyName").getNodeValue()
						+ "]"));

				// Replace <compatibility:set-property> with <set-variable>
				compatibilityCopyPropertyNode.getParentNode().replaceChild(setVariableElement,
						compatibilityCopyPropertyNode);

			}
			doc.normalize();
			XMLTransformer.writeXmlFile(doc, xmlPath);

			log.info("XML file has been modified successfully.");
			response = true;

		} catch (Exception e) {
			// TODO: handle exception

			log.error(e.toString());
		}

		return response;

	}

}
