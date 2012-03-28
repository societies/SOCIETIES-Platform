/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.policynegotiator.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Generic XML support
 * 
 * @author Mitja Vardjan
 */
public class Xml {

	private static Logger Log = LoggerFactory.getLogger(Xml.class);

	private Document doc;
	private XPath xpathObj;

	public Xml(String source) throws XmlException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(source)));
		} catch (Exception ex) {
			throw new XmlException(ex);
		}
		doc.getDocumentElement().normalize();
		xpathObj = XPathFactory.newInstance().newXPath();
	}
	
	public Xml(Document doc) throws XmlException {

		this.doc = doc;
		doc.getDocumentElement().normalize();
		xpathObj = XPathFactory.newInstance().newXPath();
	}

	/**
	 * Get XML node given with XPath and parse its text contents to get double
	 * 
	 * @param xpath
	 *            The XPath expression
	 * @return The value in text contents of the node or -1 if the text contents
	 *         could not be parsed.
	 */
	/*
	 * public double getDouble(String xpath) { String value; double num;
	 * 
	 * value = getValue(xpath); try { num = Double.parseDouble(value); } catch
	 * (NumberFormatException ex) { return -1; } catch (NullPointerException ex)
	 * { return -1; }
	 * 
	 * return num; }
	 */

	public String getValue(String xpath) {
		NodeList nodes;
		String value;

		nodes = getNodes(xpath);
		if (nodes.getLength() == 0) {
			return null;
		} else if (nodes.getLength() > 1) {
			Log.warn("getValue(" + xpath + "Found more than 1 XML node");
		}
		value = nodes.item(0).getTextContent();
		return value;
	}

	/**
	 * Finds XML nodes with given XPath expression.
	 * 
	 * @param xpath
	 *            The XPath expression
	 * @return List of matching nodes or null on error
	 */
	public NodeList getNodes(String xpath) {
		XPathExpression expr;
		Object result;

		try {
			expr = xpathObj.compile(xpath);
			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			Log.warn("getNodes(" + xpath + ")", ex);
			return null;
		}

		return (NodeList) result;
	}

	/**
	 * Create new child node and add it to parent node even if the new node
	 * already exists.
	 * 
	 * @param parent
	 *            the node to append new node to
	 * @param newNode
	 *            Name of the new node. Can be XPath expression and all needed
	 *            nodes will be generated.
	 * @return new node
	 */
	public Node addNode(Node parent, String newNode) {
		Node child;
		String[] name;
		Document ownerDoc = getOwnerDocument(parent);

		if (newNode.startsWith("/")) {
			newNode = newNode.substring(1);
		}
		name = newNode.split("[/]");

		for (int i = 0; i < name.length; i++) {
			try {
				child = ownerDoc.createElement(name[i]);
			} catch (DOMException ex) {
				Log.debug("addNode(): Node name: \"" + name[i] + "\"", ex);
				return null;
			}
			parent.appendChild(child);
			parent = child;
		}
		return parent;
	}

	/**
	 * Remove given XML nodes.
	 * 
	 * @param xpath
	 *            XPath expression
	 */
	public void removeNodes(String xpath) {

		NodeList nodes = getNodes(xpath);

		if (nodes != null) {
			for (int k = 0; k < nodes.getLength(); k++) {
				doc.removeChild(nodes.item(k));
			}
		}
	}

	@Override
	public String toString() {
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException ex) {
			Log.error("toString(): " + ex.getMessage(), ex);
		} catch (TransformerException ex) {
			Log.error("toString(): " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 
	 * @param xpath
	 * @return Contents of given node
	 */
	public String toString(String xpath) {

		Node node;
		NodeList nodes = getNodes(xpath);

		if (nodes == null) {
			Log.error("toString(" + xpath + "): No nodes found");
			return null;
		}
		Log.debug("toString(" + xpath + "): " + nodes.getLength() + " nodes found");
		if (nodes == null || nodes.getLength() != 1) {
			Log.warn("toString(" + xpath + "): Number of nodes not 1, but " + nodes.getLength());
			return null;
		}
		node = nodes.item(0);

		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			String s = stringWriter.getBuffer().toString();
			Log.debug("toString(): " + s);
			return s;
		} catch (TransformerConfigurationException ex) {
			Log.error("toString(): " + ex.getMessage(), ex);
		} catch (TransformerException ex) {
			Log.error("toString(): " + ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 
	 * @param xpath
	 * @return Contents of given node
	 */
	public String toPrettyString(String xpath) {

		Node node;
		NodeList nodes = getNodes(xpath);

		if (nodes == null) {
			Log.error("toString(" + xpath + "): No nodes found");
			return null;
		}
		Log.debug("toString(" + xpath + "): " + nodes.getLength() + " nodes found");
		if (nodes == null || nodes.getLength() != 1) {
			Log.warn("toString(" + xpath + "): Number of nodes not 1, but " + nodes.getLength());
			return null;
		}
		node = nodes.item(0);

		return textRepresentation(node, false);
	}
	
	private String textRepresentation(Node node, boolean includeRootNode) {
		
		StringBuffer buf = new StringBuffer();
		NamedNodeMap attributes;
		String textContent;
		NodeList childNodes;

		Log.debug("textRepresentation(" + node.getNodeName() + ", " + includeRootNode + ")");
		
		if (includeRootNode) {
			buf.append(node.getNodeName());
			attributes = node.getAttributes();
			if (attributes != null && attributes.getLength() > 0) {
				for (int k = 0; k < attributes.getLength(); k++) {
					buf.append(" " + attributes.item(k).getNodeName() + "=" + attributes.item(k).getNodeValue());
				}
			}
		}
		
		childNodes = node.getChildNodes();
		if (childNodes != null && childNodes.getLength() > 0) {
			for (int k = 0; k < childNodes.getLength(); k++) {
				buf.append(textRepresentation(childNodes.item(k), true));
			}
		}
		else {
			textContent = node.getTextContent();
			if (textContent != null && textContent.length() > 0) {
				buf.append(textContent);
			}
		}
		Log.debug("textRepresentation(" +
				node.getNodeName() + ", " + includeRootNode + "): " + buf.toString());
		
		return buf.toString();
	}

	/**
	 * Create new child node and add it to parent node if the new node does not
	 * already exist.
	 * 
	 * @param parent
	 *            the node to append new node to
	 * @param newNode
	 *            Name of the new node. Can have slashes (like XPath) and any
	 *            missing nodes will also be created.
	 * @return new node
	 */
	public static Node addNodeIfNotPresent(Node parent, String newNode) {
		Node child;
		String[] name;

		if (newNode.startsWith("/")) {
			newNode = newNode.substring(1);
		}
		name = newNode.split("[/]");

		for (int i = 0; i < name.length; i++) {
			child = getFirstNode(parent, name[i]);
			if (child == null) {
				child = getOwnerDocument(parent).createElement(name[i]);
				parent.appendChild(child);
			}
			parent = child;
		}
		return parent;
	}

	/**
	 * From the given XML node get XML nodes that satisfy XPath expression. Only
	 * the inner XML part of that node is searched, not the whole XML.
	 * 
	 * @param node
	 * @param xpath
	 *            XPath expression
	 * @return List of nodes that satisfy given XPath expression
	 */
	public static NodeList getNodes(Node node, String xpath) {
		NodeList nodes = null;
		XPathExpression expr;
		Object result;

		try {
			expr = XPathFactory.newInstance().newXPath().compile(xpath);
			result = expr.evaluate(node, XPathConstants.NODESET);
			nodes = (NodeList) result;
		} catch (XPathExpressionException ex) {
		}
		return nodes;
	}

	/**
	 * Calls getNodes() and returns the first node
	 * 
	 * @param node
	 * @param xpath
	 * @return the first node found with getNodes()
	 */
	public static Node getFirstNode(Node node, String xpath) {
		NodeList nodes;

		nodes = getNodes(node, xpath);
		if (nodes == null || nodes.getLength() == 0) {
			return null;
		}
		return nodes.item(0);
	}

	/**
	 * Get owner document for the given node. If the given node is itself
	 * Document, the casted node is returned.
	 * 
	 * @param node
	 * @return Owner document
	 */
	private static Document getOwnerDocument(Node node) {

		Document ownerDoc;

		ownerDoc = node.getOwnerDocument();
		if (ownerDoc == null) {
			ownerDoc = (Document) node;
		}
		return ownerDoc;
	}

	public String getAttributeValue(String xpathToNode, String attribName) {

		Node node;
		NamedNodeMap attribs;
		NodeList nodes = this.getNodes(xpathToNode);
		Log.debug("getAttribute(" + xpathToNode + ", "
				+ attribName + "): " + nodes.getLength() + " nodes found");

		if (nodes.getLength() < 1) {
			return null;
		}
		for (int k = 0; k < nodes.getLength(); k++) {
			node = nodes.item(k);
			attribs = node.getAttributes();
			for (int l = 0; l < attribs.getLength(); l++) {
				Log.debug("getAttribute(" + xpathToNode
						+ ", " + attribName + "): Attribute no. " + l);
				if (attribs.item(l).getNodeName().equals(attribName)) {
					Log.debug("getAttribute(" + xpathToNode
							+ ", " + attribName + "): " + "Found attribute "
							+ attribName + "=" + attribs.item(l).getNodeValue());
					return attribs.item(l).getNodeValue();
				}
			}
		}
		return null;
	}

	public static String getAttribute(Node node, String name) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) {
			return null;
		}
		Node attrib = attributes.getNamedItem(name);
		if (attrib == null) {
			return null;
		}
		return attrib.getNodeValue();
	}
}
