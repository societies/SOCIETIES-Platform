package org.societies.comm.xmpp.client.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementConverter implements Converter<Element> {

	private DocumentBuilder builder;
	
	public ElementConverter() throws ParserConfigurationException {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
	
	public Element read(InputNode node) throws Exception {
		Document d = builder.newDocument();
		Element e = getElement(d, node);
		return e;
	}

	public void write(OutputNode node, Element e) throws Exception {
		writeElement(node, e);
	}
	
	private void writeElement(OutputNode thisNode, Element anyElement) throws Exception {
		thisNode.setName(anyElement.getLocalName());
		thisNode.getAttributes().remove("class");
		if (!getCurrentNamespace(thisNode).equals(anyElement.getNamespaceURI().toString())){
			thisNode.setAttribute("xmlns", anyElement.getNamespaceURI().toString());
			thisNode.setReference(anyElement.getNamespaceURI().toString());
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
				Element e = (Element) n;
				writeElement(thisNode.getChild(e.getLocalName()), e);
			}
		}
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
		Element e = d.createElementNS(node.getReference(), node.getName());
		
		for (String attrName : node.getAttributes()) {
			if (!attrName.equals("xmlns"))
				e.setAttribute(attrName, node.getAttribute(attrName).getValue());
		}
		
		InputNode nextNode = node.getNext();
		while (nextNode != null) {
			e.appendChild(getElement(d, nextNode));
			nextNode = node.getNext();
		}
		
		return e;
	}
}
