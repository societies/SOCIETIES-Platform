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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.security.xml.Xml;

public class XmlTest extends XMLTestCase {

	private Xml classUnderTest;
	
	private static final String xmlStringSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><boo><a><b>ccc</b></a></boo>";
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new Xml(xmlStringSource);
		assertXMLEqual("setUp", xmlStringSource, classUnderTest.toString());
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public void testAddNodeRecursively() throws Exception {

		InputStream newXml = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><boo><x><y>foo</y><y></y></x></boo>".getBytes());

		int numNodes = classUnderTest.addNodeRecursively(newXml, "/boo/x/y");
		
		assertEquals(2, numNodes, 0.0);
		assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?><boo><a><b>ccc</b></a><y>foo</y><y></y></boo>", classUnderTest.toString());
	}

	@Test
	public void testAddNodeRecursivelyWithAttributes() throws Exception {

		InputStream newXml = new ByteArrayInputStream(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><boo><x attr1=\"attr1val\"><y attr2=\"attr2val\">foo</y><y></y></x></boo>"
				.getBytes());
		
		int numNodes = classUnderTest.addNodeRecursively(newXml, "/boo/x/y");
		
		assertEquals(2, numNodes, 0.0);
		assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?><boo><a><b>ccc</b></a><y attr2=\"attr2val\">foo</y><y></y></boo>",
				classUnderTest.toString());
	}

	@Test
	public void testAddNodeRecursivelyWithNamespace() throws Exception {

//		System.out.println("Origin XML: " + classUnderTest.toString());

		String newXmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<boo>"
				+ "<x attr1=\"attr1val\">"
				+ "<ds:y xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" attr2=\"attr2val\">foo</ds:y>"
				+ "<ds:y></ds:y>"
				+ "</x>"
				+ "<x></x>"
				+ "</boo>";
		InputStream newXml = new ByteArrayInputStream(newXmlStr.getBytes());
		
		int numNodes = classUnderTest.addNodeRecursively(newXml, "/boo/x");
		assertEquals(2, numNodes, 0.0);
		
		String merged = classUnderTest.toString();
//		System.out.println("Merged XML: " + merged);

		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<boo>"
				+ "<a><b>ccc</b></a>"
				+ "<x attr1=\"attr1val\">"
				+ "<ds:y xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" attr2=\"attr2val\">foo</ds:y>"
				+ "<ds:y/>"
				+ "</x>"
				+ "<x/>"
				+ "</boo>";
		
//		assertEquals(expected, merged);
//		assertXMLEqual(expected, merged);
	}

	@Test
	public void testToOutputStream() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		classUnderTest.toOutputStream(os);
		byte[] bytes = os.toByteArray();
		String bytesStr = new String(bytes);
		assertXMLEqual(bytesStr, classUnderTest.toString());
	}
}
