package com.exavalu.migration.enhancer.mule3apikitexceptiontoerrorhandler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

@RestController
public class Mule3ExceptionToErrorMappingLogic {
	private static final Logger log = LoggerFactory.getLogger(Mule3ExceptionToErrorMappingLogic.class);

	@PostMapping("/error-handler-check")
	public static void createErrorHandler() {
		createErrorHandlerElement("D:\\springTestingFiles\\without-error-handler\\global-exception-handler.xml");
	}

	public static void createErrorHandlerElement(String mule4ErrorXMLFilePath) {
		try {
			// Load and parse the XML file
			File xmlFile = new File(mule4ErrorXMLFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);

			// Normalize the document to correctly handle whitespaces
			doc.getDocumentElement().normalize();

			// Get the root element of the XML document
			Element rootElement = doc.getDocumentElement();

			// Retrieve the "apikit:mapping-exception-strategy" element
			Element mappingExceptionStrategyElement = (Element) rootElement
					.getElementsByTagName("apikit:mapping-exception-strategy").item(0);
			if(mappingExceptionStrategyElement != null) {
				// Check if the "error-handler" element is not present
				if (!ExceptionToErrorHandlingHelperFunction.isErrorHandlerPresent(rootElement)) {

					// Get the value of the "name" attribute from the existing element
					String exceptionStrategyName = ExceptionToErrorHandlingHelperFunction
							.getAttributeFromElement(rootElement, "apikit:mapping-exception-strategy", "name");
					// Create the "error-handler" element
					Element errorHandlerElement = doc.createElement("error-handler");

					errorHandlerElement.setAttribute("name", exceptionStrategyName);

					// Retrieve a NodeList of "apikit:mapping" elements under
					// "apikit:mapping-exception-strategy"
					NodeList mappingElements = mappingExceptionStrategyElement.getElementsByTagName("apikit:mapping");

					// Iterate through each "apikit:mapping" element
					for (int i = 0; i < mappingElements.getLength(); i++) {

						Element mappingElement = (Element) mappingElements.item(i);

						Element onErrorPropagateElement = doc.createElement("on-error-propagate");
						onErrorPropagateElement.setAttribute("type", ExceptionToErrorHandlingHelperFunction
								.errorTypeBasedOnStatusCode(mappingElement, "statusCode"));
						// Retrieve a NodeList of child elements under "apikit:mapping"
						NodeList childElements = mappingElement.getChildNodes();
						// Iterate through each child element and append it to "on-error-propagate"
						for (int j = 0; j < childElements.getLength(); j++) {
							Node childNode = childElements.item(j);

							// Check if the node is an element (e.g., not a text node) and not
							// "apikit:exception"
							if (childNode.getNodeType() == Node.ELEMENT_NODE
									&& !"apikit:exception".equals(childNode.getNodeName())) {
								// Clone the child element and append it to "on-error-propagate"
								Element clonedChildElement = (Element) childNode.cloneNode(true);
								onErrorPropagateElement.appendChild(clonedChildElement);
							}
						}
						// Append "on-error-propagate" to "error-handler"
						errorHandlerElement.appendChild(onErrorPropagateElement);

					}

					// Append the "error-handler" element to the root element
					rootElement.appendChild(errorHandlerElement);
					// Remove "apikit:mapping-exception-strategy" element
					ExceptionToErrorHandlingHelperFunction.removeElement(rootElement, "apikit:mapping-exception-strategy");

					// Save the changes back to the XML file
					ExceptionToErrorHandlingHelperFunction.saveXMLFile(doc, xmlFile);

					log.info("The 'error-handler' element was not present and has been created with name attribute value: "
							+ exceptionStrategyName);
				} else {
					log.warn("The 'error-handler' element is already present in the XML file.");

				}
			}else {
				log.warn("No <apikit:mapping-exception-strategy> element found in the XML: " + mule4ErrorXMLFilePath);
			}
			
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			log.error(e.toString());
		}
	}
}
