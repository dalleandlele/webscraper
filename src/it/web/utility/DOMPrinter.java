package it.web.utility;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;


public class DOMPrinter {
	public static void printNode(Node node, String indent) {
		switch (node.getNodeType()) {
		case Node.DOCUMENT_NODE:
			System.out.println(indent + "<?xml version=\"1.0\"?>");

			NodeList nodes = node.getChildNodes();

			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					printNode(nodes.item(i), "");
				}
			}

			break;

		case Node.ELEMENT_NODE:

			String name = node.getNodeName();
			System.out.print(indent + "<" + name);

			NamedNodeMap attributes = node.getAttributes();

			for (int i = 0; i < attributes.getLength(); i++) {
				Node current = attributes.item(i);
				System.out.print(" " + current.getNodeName() +
				        "=\"" + current.getNodeValue() + "\"");
			}

			System.out.println(">");

			NodeList children = node.getChildNodes();

			if (children != null) {
				for (int i = 0; i < children.getLength();
					        i++) {
					printNode(children.item(i),
					        indent + "  ");
				}
			}

			System.out.println(indent + "</" + name + ">");

			break;

		case Node.TEXT_NODE:
		case Node.CDATA_SECTION_NODE:
			System.out.println(indent + node.getNodeValue());

			break;

		case Node.PROCESSING_INSTRUCTION_NODE:
			System.out.println(indent + "<?" + node.getNodeName() +
			        " " + node.getNodeValue() + " ?>");

			break;

		case Node.ENTITY_REFERENCE_NODE:
			System.out.println("&" + node.getNodeName() + ";");

			break;

		case Node.DOCUMENT_TYPE_NODE:

			DocumentType docType = (DocumentType) node;
			System.out.print("<!DOCTYPE " + docType.getName());

			if (docType.getPublicId() != null) {
				System.out.print("PUBLIC \"" +
				        docType.getPublicId() + "\"");
			} else {
				System.out.print(" SYSTEM ");
			}

			System.out.println("\"" + docType.getSystemId() +
			        "\" >");

			break;
		}
	}

	public void performDemo(Document doc) {
		printNode(doc, "");
	}

	
}
