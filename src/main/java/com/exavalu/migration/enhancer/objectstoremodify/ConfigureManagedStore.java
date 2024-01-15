package com.exavalu.migration.enhancer.objectstoremodify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

public class ConfigureManagedStore {

	private static final Logger log = LoggerFactory.getLogger(ConfigureManagedStore.class);

	public static void managedstoremodifier(String xmlpath) {
//		String filePath = "D:\\MULESOFT\\MuleMigration\\DEMOPATH\\storefile.xml"; // Replace with the actual file path

		String filePath = xmlpath;

		try {
			// Read XML content from the file
			File inputFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(inputFile);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			// Find all <objectstore:all-keys> elements
			NodeList allKeysList = document.getElementsByTagName("objectstore:all-keys");
			if (allKeysList.getLength() > 0) {
				for (int i = 0; i < allKeysList.getLength(); i++) {
					Element allKeysNode = (Element) allKeysList.item(i);
					Element parentElement = (Element) allKeysNode.getParentNode();

					String objectStoreName = allKeysNode.getAttribute("config-ref");

					// Create a new <os:retrieve-all-keys> element
					Element retrieveAllKeysElement = document.createElement("os:retrieve-all-keys");
					// retrieveAllKeysElement.setAttribute("xmlns:objectstore",
					// "http://www.mulesoft.org/schema/mule/objectstore");
					retrieveAllKeysElement.setAttribute("objectStore", objectStoreName);
					retrieveAllKeysElement.setAttribute("doc:name", "retrieveAllKeys");

					// Replace <objectstore:all-keys> with <os:retrieve-all-keys>
					parentElement.replaceChild(retrieveAllKeysElement, allKeysNode);
				}

				// Save the modified document back to the file
				saveModifiedDocument(document, filePath);
			} else {
				log.warn("No <objectstore:all-keys> elements found in the XML.");
			}

			// Code for modifying objectstore:dual-store

			NodeList dualStoreList = document.getElementsByTagName("objectstore:dual-store");
			if (dualStoreList.getLength() > 0) {
				for (int i = 0; i < dualStoreList.getLength(); i++) {
					Element dualStoreNode = (Element) dualStoreList.item(i);
					Element parentElement = (Element) dualStoreNode.getParentNode();

					String objectStoreName = dualStoreNode.getAttribute("config-ref");
					String key = dualStoreNode.getAttribute("key");
					String value = dualStoreNode.getAttribute("value-ref");

					// Create the first <os:store> element for dual-store
					Element osStoreElement1 = document.createElement("os:store");
					// retrieveAllKeysElement.setAttribute("xmlns:objectstore",
					// "http://www.mulesoft.org/schema/mule/objectstore");
					osStoreElement1.setAttribute("objectStore", objectStoreName);
					osStoreElement1.setAttribute("doc:name", "");
					osStoreElement1.setAttribute("failIfPresent", "true");
					osStoreElement1.setAttribute("key", key);

					Element osValueElement = document.createElement("os:value");
					osValueElement.setTextContent(value);

					osStoreElement1.appendChild(osValueElement);

					// Insert the first <os:store> element after the dual-store element
					parentElement.insertBefore(osStoreElement1, dualStoreNode.getNextSibling());

					// Create the second <os:store> element for dual-store
					Element osStoreElement2 = document.createElement("os:store");
					osStoreElement2.setAttribute("objectStore", objectStoreName);
					osStoreElement2.setAttribute("doc:name", "");
					osStoreElement2.setAttribute("failIfPresent", "true");
					osStoreElement2.setAttribute("key", value);

					Element osValueElement2 = document.createElement("os:value");
					osValueElement2.setTextContent(key);

					osStoreElement2.appendChild(osValueElement2);

					// Insert the second <os:store> element after the first <os:store> element
					parentElement.insertBefore(osStoreElement2, osStoreElement1.getNextSibling());

					// Remove the <objectstore:dual-store> element
					parentElement.removeChild(dualStoreNode);

				}
				// Save the modified document back to the file
				saveModifiedDocument(document, filePath);
			} else {
				log.warn("No <objectstore:dual-store> elements found in the XML.");
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	private static void saveModifiedDocument(Document document, String filePath) {
		try {
			// Use a Transformer for output
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);

			// Specify the output file
			FileOutputStream outputStream = new FileOutputStream(filePath);

			transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "xml");

			// Transform and save the document
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source, result);

			// Close the output stream
			outputStream.close();

			log.info("XML file has been updated with os:retrieve-all-keys successfully.");

		} catch (Exception e) {
			log.error("Error modifying XML:", e.toString());
		}
	}
}
