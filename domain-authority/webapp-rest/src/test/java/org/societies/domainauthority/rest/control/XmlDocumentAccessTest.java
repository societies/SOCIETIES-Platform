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
package org.societies.domainauthority.rest.control;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class XmlDocumentAccessTest {

	private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
			+ "<meeting Id=\"Board001\">aadsads</meeting>"
			+ "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"Signature-a81f5f9a-f0db-4785-960d-509d966df19c\">"
			+ "		<ds:SignedInfo>"
			+ "			<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>"
			+ "			<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>"
			+ "			<ds:Reference URI=\"#Board001\">"
			+ "				<ds:Transforms>"
			+ "					<ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/>"
			+ "				</ds:Transforms>"
			+ "				<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>"
			+ "				<ds:DigestValue>hd+XVXONfE3eO68HZ95hZG+aQvI=</ds:DigestValue>"
			+ "			</ds:Reference>"
			+ "		</ds:SignedInfo>"
			+ "	<ds:SignatureValue>"
			+ "		AZ+EP4WlvNOynU6DzGCKDVU0zbdIzdYIn7fxF+KaWKrbgELSxey+YU+Q1eqqOhfkUVCJyqoqwttm"
			+ "		qRpR+JJE2vFYinGrX4C6wvyzziIDrVB/J9ITIwSKeNS2i8fyvFqLHady10b7CE4mOTFYPovMue2/"
			+ "		7yPl3BgffxVIkUsGXXw="
			+ "	</ds:SignatureValue>"
			+ "	<ds:KeyInfo>"
			+ "		<ds:X509Data>"
			+ "			<ds:X509Certificate>"
			+ "MIICJzCCAZACCQDD70Cjg6UUNTANBgkqhkiG9w0BAQUFADBYMQswCQYDVQQGEwJTSTERMA8GA1UE"
			+ "CAwIU2xvdmVuaWExEjAQBgNVBAcMCUxqdWJsamFuYTEPMA0GA1UECgwGU0VUQ0NFMREwDwYDVQQL"
			+ "DAhSZXNlYXJjaDAeFw0xMjEyMjAxNDEwNDlaFw0xMzEyMjAxNDEwNDlaMFgxCzAJBgNVBAYTAlNJ"
			+ "MREwDwYDVQQIDAhTbG92ZW5pYTESMBAGA1UEBwwJTGp1YmxqYW5hMQ8wDQYDVQQKDAZTRVRDQ0Ux"
			+ "ETAPBgNVBAsMCFJlc2VhcmNoMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnBqyJTi/ofCRq"
			+ "oOgzXy3LlKNSv96UwPi6fnUhZZuDgIQhjclOagpbHu3NDmmj+7TSSaYzhTmALm+WKKlQXsB5Y1EE"
			+ "IfyLzcbBKKLSBCqJRJck8m71ZhgwGPxzOrabI81K+df+J1G+vPNr3r1dGzEiLuu3XePaP3l57TjS"
			+ "nlXLYQIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAAvnG2/hjrkXdgbg4d1FakM6uRdEHmlbGe2SStEY"
			+ "aAsWoqQLZ7daly1aVEmaflswvFdmuMiz95IMcKlIEQOx75kHrafa15uxWqXMyxu7OTrmmPrgo1Vn"
			+ "s+/R+TlrE076R6TlWiaecJnRfzsR/NM4at0sEUnd2Ae8nWZJStTuvxDf"
			+ "</ds:X509Certificate>"
			+ "		</ds:X509Data>"
			+ "	</ds:KeyInfo>"
			+ "</ds:Signature>"
			+ "</xml>";
	
	Document doc;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		if (!Init.isInitialized()) {
			Init.init();
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExtractSignatures() throws Exception {
		
		List<XMLSignature> signatures = XmlDocumentAccess.extractSignatures(doc);
		
		assertNotNull(signatures);
		assertEquals(1, signatures.size());
		XMLSignature signature = signatures.get(0);
		KeyInfo keyInfo = signature.getKeyInfo();
		assertTrue(keyInfo.containsX509Data());
		assertFalse(keyInfo.isEmpty());
		X509Certificate cert = keyInfo.getX509Certificate();
		assertNotNull(cert);
		String cn = cert.getSubjectX500Principal().getName().replaceFirst(".*CN=", "").replaceFirst(",.*", "");
		System.out.println("Subject: " + cn);
	}

}
