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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlTest extends XMLTestCase {

	private Xml classUnderTest;
	
	private static final String xmlStringSource = "<?xml version=\"1.0\"?><xml><a><b></b></a></xml>";
	
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
		InputStream newXml = new ByteArrayInputStream("<?xml version=\"1.0\"?><xml><x><y>foo</y></x></xml>".getBytes());
		classUnderTest.addNodeRecursively(newXml, "/xml/x/y");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		assertXMLEqual("testAddNodeRecursively", "<?xml version=\"1.0\"?><xml><a><b></b></a><y>foo</y></xml>", classUnderTest.toString());
	}

	@Test
	public void testToOutputStream() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		classUnderTest.toOutputStream(os);
		byte[] bytes = os.toByteArray();
		String bytesStr = new String(bytes);
		System.out.println("XML from byte[]: " + bytesStr);
		System.out.println("XML from toString(): " + classUnderTest.toString());
		assertXMLEqual("testToOutputStream", bytesStr, classUnderTest.toString());
	}
}
