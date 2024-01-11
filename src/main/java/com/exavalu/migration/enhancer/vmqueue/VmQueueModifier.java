package com.exavalu.migration.enhancer.vmqueue;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class VmQueueModifier {
    private static final Logger log = LoggerFactory.getLogger(VmQueueModifier.class);

    /**
     * Modifies Mule 4 XML based on Mule 3 XML configuration.
     *
     * @param mule3path Path to the Mule 3 XML file.
     * @param mule4Path Path to the Mule 4 XML file.
     */
    public static void vmQueueModifier(String mule3path, String mule4Path) {

        String destDirectory = "E:\\OfficeWork\\customqueue.xml";
        try {
            // Parse Mule 4 XML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document mule4xml = docBuilder.parse(mule4Path);

            // Parse Mule 3 XML
            Document mule3xml = docBuilder.parse(mule3path);

            // Get the root elements
            Element rootElement = mule4xml.getDocumentElement();
            Element mule3root = mule3xml.getDocumentElement();

            // Find all <vm:queues> elements in Mule 4 XML
            NodeList vmQueueList = rootElement.getElementsByTagName("vm:queues");

            // Find all <vm:queue-profile> elements in Mule 3 XML
            NodeList vmQueueProfile = mule3root.getElementsByTagName("vm:queue-profile");

            for (int i = 0; i < vmQueueProfile.getLength(); i++) {
                Node vmMule3config = vmQueueProfile.item(i);
                Element vmMule3configElement = (Element) vmMule3config;
                // Get the vmConfig doc name
                String docName = ((Element) vmMule3configElement.getParentNode()).getAttribute("doc:name");
                String type = (vmMule3configElement.getChildNodes().item(1)).getNodeName();

                for (int j = 0; j < vmQueueList.getLength(); j++) {
                    Element vmQueue = (Element) vmQueueList.item(j);
                    // Get the vmConfig doc name in mma converted flow
                    String configName = ((Element) vmQueue.getParentNode()).getAttribute("name");
                    Element vmQueueElement = (Element) vmQueue.getChildNodes().item(1);

                    // Check if Mule 3 config name matches Mule 4 config name
                    if (configName.equals(docName)) {
                        if (vmQueueElement.getNodeType() == Node.ELEMENT_NODE && !type.contains("in-memory")) {
                            // Update queueType to "PERSISTENT" if not in-memory
                            vmQueueElement.setAttribute("queueType", "PERSISTENT");

                        } 
                    }
                }
            }

            // Transform and save the updated Mule 4 XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(mule4xml);
            StreamResult result = new StreamResult(new File(destDirectory));
            transformer.transform(source, result);

        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            log.error("Error during Transformer Configuration: " + e.toString());
        }
    }
}
