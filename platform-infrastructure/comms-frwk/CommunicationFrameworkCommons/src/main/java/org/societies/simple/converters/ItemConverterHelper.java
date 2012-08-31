package org.societies.simple.converters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
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
//		AnnotationStrategy stragegy = new AnnotationStrategy();
//		this.serializer = new Persister(stragegy);
		this.serializer = serializer;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public org.jabber.protocol.pubsub.Item readToElement(InputNode node) throws Exception {
		org.jabber.protocol.pubsub.Item item = new org.jabber.protocol.pubsub.Item();
		if (node.getAttribute("id")!=null)
			item.setId(node.getAttribute("id").getValue());
		InputNode payloadNode = node.getNext();
		Document d = builder.newDocument();
		Element e = getElement(d, payloadNode);
		item.setAny(e);
		return item;
	}
	
	public org.jabber.protocol.pubsub.event.Item readEventItemAnyToElement(InputNode node) throws Exception {
		org.jabber.protocol.pubsub.event.Item item = new org.jabber.protocol.pubsub.event.Item();
		if (node.getAttribute("id")!=null)
			item.setId(node.getAttribute("id").getValue());
		InputNode payloadNode = node.getNext();
		Document d = builder.newDocument();
		Element e = getElement(d, payloadNode);
		item.setAny(e);
		return item;
	}
	
	public org.jabber.protocol.pubsub.event.Item defaultRead(InputNode node) throws Exception {
		return serializer.read(org.jabber.protocol.pubsub.event.Item.class, node);
	}

	public void write(OutputNode node, org.jabber.protocol.pubsub.Item value) throws Exception {
		System.out.println("!!!!!!!!!! ItemConverterHelper.write pubsub.Item");
		node.setAttribute("id", value.getId());
		Object anyObject = value.getAny();
		
		if (anyObject!=null) {
			Root rootAnnotation = anyObject.getClass().getAnnotation(Root.class);
			Namespace namespaceAnnotation = anyObject.getClass().getAnnotation(Namespace.class);
			if (rootAnnotation!=null && namespaceAnnotation!=null) {
				System.out.println("!!!!!!!!!! anyObject is annotated");
				System.out.println(anyObject.toString());
				// simplexml annotated class
				serializer.write(anyObject, node);
			} else if (anyObject instanceof Element) {
				System.out.println("!!!!!!!!!! anyObject is Element");
				// generic xml container
				writeElement(node,(Element) anyObject);
			} else {
				System.out.println("BODE A ESCREVER!");
			}
		}
	}
	
	public void write(OutputNode node, org.jabber.protocol.pubsub.event.Item value) throws Exception {
		System.out.println("!!!!!!!!!! ItemConverterHelper.write event.Item");
		node.setAttribute("id", value.getId());
		Object anyObject = value.getAny();
		
		if (anyObject!=null) {
			Root rootAnnotation = anyObject.getClass().getAnnotation(Root.class);
			Namespace namespaceAnnotation = anyObject.getClass().getAnnotation(Namespace.class);
			if (rootAnnotation!=null && namespaceAnnotation!=null) {
				System.out.println("!!!!!!!!!! anyObject is annotated");
				System.out.println(anyObject.toString());
				// simplexml annotated class
				serializer.write(anyObject, node);
			} else if (anyObject instanceof Element) {
				System.out.println("!!!!!!!!!! anyObject is Element");
				// generic xml container
				writeElement(node,(Element) anyObject);
			} else {
				System.out.println("BODE A ESCREVER!");
			}
		}
	}
	
	private void writeElement(OutputNode parentNode, Element anyElement) throws Exception {
		OutputNode thisNode = parentNode.getChild(anyElement.getLocalName());
		if (!getCurrentNamespace(parentNode).equals(anyElement.getNamespaceURI().toString())){
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
				writeElement(thisNode, (Element) n);
			}
		}
		
		NamedNodeMap attrs = anyElement.getAttributes();
		for (int i=0; i<attrs.getLength(); i++) {
			Node n = attrs.item(i);
			if (n instanceof Attr) {
				thisNode.setAttribute(n.getNodeName(), n.getNodeValue());
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
