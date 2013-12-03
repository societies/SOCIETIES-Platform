///**
// * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
// * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
// * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
// * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
// * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
// * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
// * conditions are met:
// *
// * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// *
// * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
// *    disclaimer in the documentation and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
// * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
// * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package org.societies.security.digsig;
//
//import static org.junit.Assert.*;
//
//import java.security.cert.X509Certificate;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.societies.api.identity.IIdentity;
//import org.societies.api.security.digsig.DigsigException;
//import org.societies.api.security.digsig.ISignatureMgr;
//import org.societies.security.digsig.main.SignatureMgr;
//
///**
// * @author Mitja Vardjan
// */
//public class SignatureMgrUnitTest {
//
//	private SignatureMgr classUnderTest;
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//		classUnderTest = new SignatureMgr();
//		classUnderTest.init();
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//		classUnderTest = null;
//	}
//
//	/**
//	 * Test method for {@link ISignatureMgr#signXml(String, String)}.
//	 * @throws DigsigException 
//	 */
//	@Test
//	public void testSignXml() throws DigsigException {
//		
//		String xml = "<?xml version=\"1.0\"?><aaa Id=\"nodeId\"><bbb>text</bbb></aaa>";
//		String xmlNodeId = "nodeA";
//		IIdentity identity = null;
//		String result;
//		
//		result = classUnderTest.signXml(xml, xmlNodeId, identity);
//		assertTrue(result.length() > xml.length() + 10);
//	}
//
//
//	/**
//	 * Test method for {@link ISignatureMgr#verifyXml(String)}.
//	 * @throws DigsigException 
//	 */
//	@Test
//	public void testVerifyXml() throws DigsigException {
//		
//		String xml = "<?xml version=\"1.0\"?><aaa><bbb Id=\"nodeId\">text</bbb></aaa>";
//		String xmlNodeId = "nodeA";
//		IIdentity identity = null;
//		
//		String xmlWithValidSig = classUnderTest.signXml(xml, xmlNodeId, identity);
//		
//		String signatureNode = "<ds:SignatureValue>";
//		int index = xmlWithValidSig.indexOf(signatureNode);
//		int offset = 17;
//		int junkIndex = index + signatureNode.length() + offset;
//		char ch = xmlWithValidSig.charAt(junkIndex);
//		if (ch != 'a') {
//			ch = 'a';
//		}
//		else {
//			ch = 'b';
//		}
//		String xmlWithInvalidSig =
//				xmlWithValidSig.substring(0, junkIndex) +
//				ch +
//				xmlWithValidSig.substring(junkIndex + 1);
//		
//		Map<String, X509Certificate> result;
//		
//		result = classUnderTest.verifyXml(xmlWithValidSig);
//		assertEquals(1.0, result.size(), 0.0);
//		
//		try {
//			result = classUnderTest.verifyXml(xmlWithInvalidSig);
//			fail("DigsigException expected.");
//		} catch (DigsigException e) {
//			// OK, this exception is supposed to be thrown
//		}
//	}
//}
