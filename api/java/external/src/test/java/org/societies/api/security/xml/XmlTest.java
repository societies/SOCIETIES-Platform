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
package org.societies.api.security.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.security.xml.Xml;
import org.w3c.dom.NodeList;

public class XmlTest extends XMLTestCase {

	private Xml classUnderTest;
	private Xml classUnderTestMoreNodes;
	
	private static final String XML_SIGNATURE_XPATH =
			"//*[local-name()='Signature'"
			+ " and "
			+ "namespace-uri()='http://www.w3.org/2000/09/xmldsig#']";

	@Before
	public void setUp() throws Exception {

		classUnderTest = new Xml(getSampleXml(false));
		assertXMLEqual(getSampleXmlString(false), classUnderTest.toString());
		
		classUnderTestMoreNodes = new Xml(getSampleXml(true));
		assertXMLEqual(getSampleXmlString(true), classUnderTestMoreNodes.toString());
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
		classUnderTestMoreNodes = null;
	}

	@Test
	public void testGetNodes() throws Exception {
		
		String XML_SIGNATURE_XPATH =
				"//*[local-name()='Signature'"
				+ " and "
				+ "namespace-uri()='http://www.w3.org/2000/09/xmldsig#']";
		
		NodeList nodes;
		
		nodes = Xml.getNodes(classUnderTest.getDocument(), XML_SIGNATURE_XPATH);
		assertEquals(0, nodes.getLength());
		
		nodes = classUnderTest.getNodes(XML_SIGNATURE_XPATH);
		assertEquals(0, nodes.getLength());
		
		nodes = Xml.getNodes(classUnderTestMoreNodes.getDocument(), XML_SIGNATURE_XPATH);
		assertEquals(3, nodes.getLength());
		
		nodes = classUnderTestMoreNodes.getNodes(XML_SIGNATURE_XPATH);
		assertEquals(3, nodes.getLength());
	}

	@Test
	public void testAddNodeRecursively() throws Exception {
		
		InputStream newXml = getSampleXml(true);
		int numNodes = classUnderTest.addNodeRecursively(newXml, XML_SIGNATURE_XPATH);
		
		assertEquals(3, numNodes, 0.0);
		assertXMLEqual(getSampleXmlString(true), classUnderTest.toString());
	}

	@Test
	public void testToOutputStream() throws Exception {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		classUnderTestMoreNodes.toOutputStream(os);
		byte[] bytes = os.toByteArray();
		String bytesStr = new String(bytes);
		
		assertXMLEqual(bytesStr, classUnderTestMoreNodes.toString());
		assertXMLNotEqual(bytesStr, classUnderTest.toString());
	}
	
	@Test
	public void testResources() throws Exception {
		assertXMLNotEqual(getSampleXmlString(false), getSampleXmlString(true));
	}
	
	private InputStream getSampleXml(boolean signed) {
		
		String resourceName = signed ? "meeting-minutes-signed.xml" : "meeting-minutes.xml";

		return getClass().getClassLoader().getResourceAsStream(resourceName);
	}
	
	private String getSampleXmlString(boolean signed) throws IOException {
		
		InputStream is = getSampleXml(signed);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		
		return writer.toString();
	}
	
}
