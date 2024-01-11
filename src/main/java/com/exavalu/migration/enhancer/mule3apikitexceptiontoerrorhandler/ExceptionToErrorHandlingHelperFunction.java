package com.exavalu.migration.enhancer.mule3apikitexceptiontoerrorhandler;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExceptionToErrorHandlingHelperFunction {
	private static final Logger log = LoggerFactory.getLogger(ExceptionToErrorHandlingHelperFunction.class);

	// Helper function to remove an element
	public static void removeElement(Element rootElement, String elementName) {
		NodeList nodeList = rootElement.getElementsByTagName(elementName);
		if (nodeList.getLength() > 0) {
			Element elementToRemove = (Element) nodeList.item(0);
			if (elementToRemove != null) {
				rootElement.removeChild(elementToRemove);
			}
		}
	}

	// Helper function to check if "error-handler" element is present
	public static boolean isErrorHandlerPresent(Element rootElement) {
		NodeList errorHandlerElements = rootElement.getElementsByTagName("error-handler");
		return errorHandlerElements.getLength() > 0;
	}

	// Helper function to save changes back to the XML file
	public static void saveXMLFile(Document doc, File xmlFile) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
	}

	// Helper function to get attribute value from an element
	public static String getAttributeFromElement(Element rootElement, String elementName, String attributeName) {
		NodeList nodeList = rootElement.getElementsByTagName(elementName);
		if (nodeList.getLength() > 0) {
			Element element = (Element) nodeList.item(0);
			return element.getAttribute(attributeName);
		}
		return null;
	}

	public static String errorTypeBasedOnStatusCode(Element mappingElement, String attributeName) {

		String type = "";
		switch (mappingElement.getAttribute(attributeName)) {
		case "400":
			type = "APIKIT:BAD_REQUEST";
			break;
		case "404":
			type = "APIKIT:NOT_FOUND";
			break;
		case "405":
			type = "APIKIT:METHOD_NOT_ALLOWED";
			break;
		case "406":
			type = "APIKIT:NOT_ACCEPTABLE";
			break;
		case "415":
			type = "APIKIT:UNSUPPORTED_MEDIA_TYPE";
			break;
		case "501":
			type = "APIKIT:NOT_IMPLEMENTED";
			break;
		default:
			type = "ANY";
			break;
		}
		log.info("errorType: " + type);
		return type;
	}
}
