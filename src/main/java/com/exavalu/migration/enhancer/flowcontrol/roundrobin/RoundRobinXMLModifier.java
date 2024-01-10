package com.exavalu.migration.enhancer.flowcontrol.roundrobin;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RoundRobinXMLModifier {

	private static final Logger log = LoggerFactory.getLogger(RoundRobinXMLModifier.class);

	public static void roundrobinmodifier(String xmlpath) {

		String filePath = xmlpath;

		try {
			// Read XML content from the file
			File inputFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(inputFile);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			// Find the <round-robin> element
			NodeList roundRobinList = document.getElementsByTagName("round-robin");
			if (roundRobinList.getLength() > 0) {
				Element roundRobinElement = (Element) roundRobinList.item(0);

				// Get the child nodes of <round-robin>
				NodeList childNodes = roundRobinElement.getChildNodes();

				// Check if the child elements are already wrapped with <route>
				boolean alreadyWrapped = true;
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node childNode = childNodes.item(i);
					if (childNode.getNodeType() == Node.ELEMENT_NODE && !"route".equals(childNode.getNodeName())) {
						alreadyWrapped = false;
						break;
					}
				}

				// Wrap each existing child node with <route> element only if not already
				// wrapped
				if (!alreadyWrapped) {
					Element newRoundRobinElement = document.createElement("round-robin");
					newRoundRobinElement.setAttribute("doc:name", "Round Robin");

					for (int i = 0; i < childNodes.getLength(); i++) {
						Node childNode = childNodes.item(i);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							Element routeElement = document.createElement("route");
							routeElement.appendChild(document.importNode(childNode, true));
							newRoundRobinElement.appendChild(routeElement);
						}
					}

					// Replace the old <round-robin> with the new one
					Node parentNode = roundRobinElement.getParentNode();
					parentNode.replaceChild(newRoundRobinElement, roundRobinElement);

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
						log.info("Modified XML written to the file successfully.");
					}
				} else {
					log.warn("Child elements are already wrapped with <route>.");
				}
			}
		} catch (Exception e) {
			log.error("Error modifying XML:", e.toString());
		}
	}
}
