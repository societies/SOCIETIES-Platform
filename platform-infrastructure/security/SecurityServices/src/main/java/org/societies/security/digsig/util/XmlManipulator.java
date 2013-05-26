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
package org.societies.security.digsig.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Miroslav Pavleski, Mitja Vardjan
 */
public class XmlManipulator {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private XPath xpath;

	private Map<String, XPathExpression> expCache;
	private Document doc;
	private DOMSource docSource;

	private Transformer transformer;

	public void setDocument(Document inDoc) {
		doc = inDoc;

		expCache = new HashMap<String, XPathExpression>();
		if (xpath == null) {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			xpath = xpathFactory.newXPath();
		}

		docSource = new DOMSource(doc);
	}

	public void load(InputStream fis) {
		doc = null;
		try {
			if (factory == null) {
				factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);

				builder = factory.newDocumentBuilder();
				expCache = new HashMap<String, XPathExpression>();
			}

			if (xpath == null) {
				XPathFactory xpathFactory = XPathFactory.newInstance();
				xpath = xpathFactory.newXPath();
			}

			doc = builder.parse(fis);
			docSource = new DOMSource(doc);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse XML.", e);
		}
	}

	public void load(String xmlStr) {
		try {
			load(new ByteArrayInputStream(xmlStr.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// should not happen
		}
	}

	public void load(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to load file!", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
	}

	public Document getDocument() {
		return doc;
	}

	public Element getDocumentElement() {
		return doc.getDocumentElement();
	}

	public Node getNode(Node start, String strXPath) {
		XPathExpression exp = getExpression(strXPath);

		try {
			return (Node) exp.evaluate(start, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("GetNode failed", e);
		}
	}

	public Node getNode(String strXPath) {
		return getNode(doc.getDocumentElement(), strXPath);
	}

	public NodeList getNodes(Node start, String strXPath) {
		XPathExpression exp = getExpression(strXPath);

		try {
			return (NodeList) exp.evaluate(start, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("GetNodes failed", e);
		}
	}

	public NodeList getNodes(String strXPath) {
		return getNodes(doc.getDocumentElement(), strXPath);
	}

	/**
	 * Gets the String content of the element at the specified XPath
	 * 
	 * @param strXPath
	 * @return content of the specified XML element
	 */
	public String getElementContent(Node start, String strXPath) {
		XPathExpression exp = getExpression(strXPath);

		try {
			return exp.evaluate(start);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("getElementContent failed", e);
		}
	}

	public String getElementContent(String strXPath) {
		return getElementContent(doc.getDocumentElement(), strXPath);
	}

	/**
	 * Sets the content of the element elementName, at strXPath parent. If
	 * replace is true then the first found elementName under the parent element
	 * is replaced with the content. Otherwise a new element is always created.
	 * 
	 * @param strXPath
	 * @param elementName
	 * @param content
	 * @param replace
	 */
	public void setElementContent(String strXPath, String elementName,
			String content, boolean replace) {
		Element parentElement = (Element) getNode(strXPath);
		Element reqElement = null;
		if (replace) {
			reqElement = (Element) parentElement.getElementsByTagName(
					elementName).item(0);
			if (reqElement == null) {
				reqElement = doc.createElement(elementName);
				parentElement.appendChild(reqElement);
			}
		} else {
			reqElement = doc.createElement(elementName);
			parentElement.appendChild(reqElement);
		}
		reqElement.setTextContent(content);
	}

	/**
	 * Imports the XML content at the location specified by the XPath
	 * expression. If replace is true and the specified elementName is child of
	 * the XPath specified element, the first such element content is replaced
	 * with the imported XML.
	 * 
	 * @param strXPath
	 * @param elementName
	 * @param content
	 * @param replace
	 */
	public void importElementContent(String strXPath, String elementName,
			String content, boolean replace) {
		Element element = (Element) getNode(strXPath);

		NodeList elements = element.getElementsByTagName(elementName);
		Element reqElement = (Element) ((elements != null && elements
				.getLength() > 0) ? elements.item(0) : null);

		try {
			Document importedDoc = builder.parse(new InputSource(
					new StringReader(content)));
			Node imported = doc.importNode(importedDoc.getDocumentElement(),
					true);

			if (replace && reqElement != null)
				element.replaceChild(imported, reqElement);
			else
				element.appendChild(imported);
		} catch (Exception e) {
			throw new RuntimeException("importElementContent failed", e);
		}
	}

	public void writeTo(OutputStream os) {
		OutputStreamWriter writer = new OutputStreamWriter(os);
		Result result = new StreamResult(writer);

		transform(result);
	}

	public void writeTo(File f) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			writeTo(fos);
		} catch (Exception e) {
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
		}
	}

	public void writeTo(String fn) {
		writeTo(new File(fn));
	}

	public byte[] getDocumentAsBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		Result result = new StreamResult(writer);
		transform(result);
		return os.toByteArray();
	}

	public String getDocumentAsString() {
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		transform(result);
		return stringWriter.getBuffer().toString();
	}

	/**
	 * Gets the cached compiled XPath expression from cache, or compiles a new
	 * one and puts it in the cache.
	 * 
	 * @param strXPath
	 * @return
	 */
	private XPathExpression getExpression(String strXPath) {
		XPathExpression exp = expCache.get(strXPath);
		if (exp == null) { // expresion not found in cache
			try {
				exp = xpath.compile(strXPath);
			} catch (XPathExpressionException e) {
				throw new RuntimeException("getExpression failed", e);
			}
			expCache.put(strXPath, exp);
		}
		return exp;
	}

	private void transform(Result target) {
		if (transformer == null) {
			TransformerFactory factory = TransformerFactory.newInstance();
			try {
				transformer = factory.newTransformer();
			} catch (TransformerConfigurationException e) {
				throw new RuntimeException("transform failed", e);
			}
		}

		try {
			transformer.transform(docSource, target);
		} catch (TransformerException e) {
			throw new RuntimeException("transform failed", e);
		}
	}
}
