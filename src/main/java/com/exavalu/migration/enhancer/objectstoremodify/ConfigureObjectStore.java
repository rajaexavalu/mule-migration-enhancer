package com.exavalu.migration.enhancer.objectstoremodify;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ConfigureObjectStore {
	private static final Logger log = LoggerFactory.getLogger(ConfigureObjectStore.class);

	public static void configObjectStore(String mule4ProjectLocation) {
		try {
            // Parse the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(mule4ProjectLocation);

            // Find all nodes with the name "compatibility:attributes-to-inbound-properties"
            NodeList osList = doc.getElementsByTagName("os:object-store");

            // Iterate through the found nodes in reverse order
            for (int i = osList.getLength() - 1; i >= 0; i--) {
            	Element node = (Element) osList.item(i);

                // Get the parent node of "compatibility:attributes-to-inbound-properties"
            	node.setAttribute("entryTtlUnit", "MILLISECONDS");
            	node.setAttribute("expirationIntervalUnit", "MILLISECONDS");
                System.out.println(node.getNodeName());

            }
            

            // Save the changes to the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(mule4ProjectLocation);
            transformer.transform(source, result);

            System.out.println("XML file updated successfully!");

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
			log.error("Error casused during updation of Object Store Config :" + e.toString());
		}
	}
}
