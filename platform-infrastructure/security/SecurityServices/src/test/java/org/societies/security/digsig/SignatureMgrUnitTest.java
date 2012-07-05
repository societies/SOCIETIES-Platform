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
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.societies.api.identity.IIdentity;
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
//	 */
//	@Test
//	public void testSignXml() {
//		
//		String xml = "<?xml version=\"1.0\"?><aaa><bbb>text</bbb></aaa>";
//		String xmlNodeId = "nodeA";
//		IIdentity identity = null;  // FIXME
//		String result;
//		
//		result = classUnderTest.signXml(xml, xmlNodeId, identity);
//		assertEquals(xml, result);  // TODO
//	}
//
//
//	/**
//	 * Test method for {@link ISignatureMgr#verifyXml(String)}.
//	 */
//	@Test
//	public void testVerify() {
//		
//		// TODO: use real signatures. Now the test only shows the SignatureMgr is initialized and doesn't crash
//		String xmlWithValidSig = "<?xml version=\"1.0\"?><aaa><bbb>text</bbb></aaa>";
////		String xmlWithInvalidSig = "<?xml version=\"1.0\"?><aaa><bbb>text</bbb></aaa>";
//		boolean result;
//		
//		result = classUnderTest.verifyXml(xmlWithValidSig);
//		assertTrue(result);
//		
////		result = classUnderTest.verify(xmlWithInvalidSig);
////		assertFalse(result);
//	}
//}
