package com.exavalu.migration.enhancer.inboundoutboundproperties;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RemoveInboundOutboundProperties {
	private static final Logger log = LoggerFactory.getLogger(RemoveInboundOutboundProperties.class);

	public static void inboundOutboundRemover(String mule4ProjectLocation) {
		try {
			// Parse the XML file
			File xmlFile = new File(mule4ProjectLocation);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);

			// Find all nodes with the name "compatibility:attributes-to-inbound-properties"
			NodeList inboundList = doc.getElementsByTagName("compatibility:attributes-to-inbound-properties");

			// Find all nodes with the name "compatibility:outbound-properties-to-var"
			NodeList outboundList = doc.getElementsByTagName("compatibility:outbound-properties-to-var");

			// Iterate through the found nodes in reverse order
			for (int i = inboundList.getLength() - 1; i >= 0; i--) {
				Node node = inboundList.item(i);

				// Get the parent node of "compatibility:attributes-to-inbound-properties"
				Node flow = node.getParentNode();
				System.out.println(flow.getNodeName());

				// Remove the "compatibility:attributes-to-inbound-properties" node from its
				// parent
				flow.removeChild(node);
			}

			// Iterate through the found nodes in reverse order
			for (int i = outboundList.getLength() - 1; i >= 0; i--) {
				Node node = outboundList.item(i);

				// Get the parent node of "compatibility:outbound-properties-to-var"
				Node flow = node.getParentNode();
				System.out.println(flow.getNodeName());

				// Remove the "compatibility:outbound-properties-to-var" node from its parent
				flow.removeChild(node);
			}

			// Save the changes to the XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);

			System.out.println("XML file updated successfully!");

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
			log.error("Error casused during removal of inboudoutbound propertoes :" + e.toString());
		}
	}
}
