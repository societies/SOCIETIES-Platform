package org.societies.simple.converters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItemConverterHelper {
	
	private Serializer serializer;
	private DocumentBuilder builder;
	
	public ItemConverterHelper(Serializer serializer) throws ParserConfigurationException {
		this.serializer = serializer;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public org.jabber.protocol.pubsub.Item readToElement(InputNode node) throws Exception {
		org.jabber.protocol.pubsub.Item item = new org.jabber.protocol.pubsub.Item();
		if (node.getAttribute("id")!=null)
			item.setId(node.getAttribute("id").getValue());
		InputNode payloadNode = node.getNext();
		if (payloadNode!=null) {
			Document d = builder.newDocument();
			Element e = getElement(d, payloadNode);
			item.setAny(e);
		}
		return item;
	}
	
	public org.jabber.protocol.pubsub.event.Item readEventItemAnyToElement(InputNode node) throws Exception {
		org.jabber.protocol.pubsub.event.Item item = new org.jabber.protocol.pubsub.event.Item();
		if (node.getAttribute("id")!=null)
			item.setId(node.getAttribute("id").getValue());
		InputNode payloadNode = node.getNext(); // not good?
		if (payloadNode!=null) {
			Document d = builder.newDocument();
			Element e = getElement(d, payloadNode);
			item.setAny(e);
		}
		return item;
	}
	
	public org.jabber.protocol.pubsub.event.Item defaultRead(InputNode node) throws Exception {
		return serializer.read(org.jabber.protocol.pubsub.event.Item.class, node);
	}

	public void write(OutputNode node, org.jabber.protocol.pubsub.Item value) throws Exception {
		if (value.getId()!=null)
			node.setAttribute("id", value.getId());
		Object anyObject = value.getAny();
		
		serializeAnyObject(node, anyObject);
	}

	public void write(OutputNode node, org.jabber.protocol.pubsub.event.Item value) throws Exception {
		// when a notification is triggered "id" must be set
		node.setAttribute("id", value.getId());
		Object anyObject = value.getAny();
		
		serializeAnyObject(node, anyObject);
	}
	
	private void serializeAnyObject(OutputNode node, Object anyObject) throws Exception {
		if (anyObject!=null) {
			if (anyObject instanceof Element) {
				// generic xml container
				writeElement(node,(Element) anyObject);
			} else {
				// fallback to default serializer
				serializer.write(anyObject, node);
			}
		}
	}
	
	private void writeElement(OutputNode parentNode, Element anyElement) throws Exception {
		OutputNode thisNode = parentNode.getChild(anyElement.getLocalName());
		if (!getCurrentNamespace(parentNode).equals(anyElement.getNamespaceURI().toString())){
			thisNode.setAttribute("xmlns", anyElement.getNamespaceURI().toString());
			thisNode.setReference(anyElement.getNamespaceURI().toString());
		}
		
		// attributes have to be processed before child elements
		NamedNodeMap attrs = anyElement.getAttributes();
		for (int i=0; i<attrs.getLength(); i++) {
			Node n = attrs.item(i);
			if (n instanceof Attr) {
				thisNode.setAttribute(n.getNodeName(), n.getNodeValue());
			}
		}
		
		NodeList childList = anyElement.getChildNodes();
		boolean childElements = false;
		for (int i=0; i<childList.getLength(); i++) {
			Node n = childList.item(i);
			if (n instanceof Attr) {
				thisNode.setAttribute(n.getNodeName(), n.getNodeValue());
			}
			if (n instanceof Element) {
				childElements = true;
				writeElement(thisNode, (Element) n);
			}
		}
		
		// node value
		if (anyElement.getNodeValue()!=null) {
			thisNode.setValue(anyElement.getNodeValue());
		}
		
		// added to work with harmony ElementImpl... getNodeValue doesn't seem to work!!!
		if (!childElements && anyElement.getTextContent()!=null) {
			thisNode.setValue(anyElement.getTextContent()); 
		}
	}

	private String getCurrentNamespace(OutputNode node) {
		if (node.getReference()!=null)
			return node.getReference();
		else if (node.getParent()!=null)
			return getCurrentNamespace(node.getParent());
		else
		return "";
	}

	private Element getElement(Document d, InputNode node) throws DOMException, Exception {
		String v = node.getValue();
		Element e = d.createElementNS(node.getReference(), node.getName());
		
		for (String attrName : node.getAttributes()) {
			if (!attrName.equals("xmlns")) {
				e.setAttribute(attrName, node.getAttribute(attrName).getValue());
			}
		}
		
		boolean hasChildren=true;
		InputNode nextNode = node.getNext();
		if (nextNode==null)
			hasChildren=false;
		while (nextNode != null) {
			e.appendChild(getElement(d, nextNode));
			nextNode = node.getNext();
		}
		
		if (v!=null && !hasChildren) {
			e.setTextContent(v);
			e.setNodeValue(v);
		}

		return e;
	}

}
