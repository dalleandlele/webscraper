package it.web.utility;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UtilityParser {
	
	public static String findChildsTextNode(Node n) {

		boolean stop = false;
		
		while(!stop) {
			System.out.println("n --> " + n.getNodeName() + "  " + n.getNodeValue());
			if(n != null && n.getFirstChild() != null && n.getFirstChild().getNodeType() == Node.TEXT_NODE && !n.getFirstChild().getNodeValue().isEmpty()) {
				//inutile
				stop = true;
				return n.getFirstChild().getNodeValue();
				
			}
			if(n == null)
				System.out.println("problema...");
			if(n.getNodeType() == Node.COMMENT_NODE) {
				n = n.getNextSibling();
			} else
				n = n.getFirstChild();
		}
		
		return null;
	}
	
	
	public static String getAttribute(Node n, String nameAttribute) {
		NamedNodeMap map = n.getAttributes();
		for(int i=0;i<map.getLength();i++) {
			if(map.item(i).getNodeName().equals(nameAttribute)) {
				return map.item(i).getNodeValue();
			}
		}
		return null;
	}
	
	
	public static ArrayList<Node> getNodeChildWithName(NodeList childs, String name) {
		ArrayList<Node> toReturn = new ArrayList<Node>();
		for(int i=0;i<childs.getLength();i++) {
			if(childs.item(i).getNodeName().equals(name)) {
				toReturn.add(childs.item(i));
			}
		}
		return toReturn;
	}
	
	public static boolean isNodeWithAttribute(Node n, String nameAttribute,
			String valueAttribute) {
		NamedNodeMap mapAttribute = n.getAttributes();
		for (int i = 0; i < mapAttribute.getLength(); i++) {
			Node tmp = mapAttribute.item(i);
			if (tmp.getNodeName().equals(nameAttribute)
					&& tmp.getNodeValue().equals(valueAttribute))
				return true;
		}

		return false;
	}

	public static Node getNodeWithAttribute(Document dom, String tagname,
			String nameAttribute, String valueAttribute) {
		NodeList list = dom.getElementsByTagName(tagname);
		for (int i = 0; i < list.getLength(); i++) {
			// cerca l'attributo passato come argomento
			if (isNodeWithAttribute(list.item(i), nameAttribute, valueAttribute))
				return list.item(i);
		}
		return null;
	}
	

}
