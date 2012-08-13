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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class ServiceClientJarAccessTest {

	ServiceClientJarAccess classUnderTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		classUnderTest = new ServiceClientJarAccess();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ServiceClientJarAccess#addKey(String, String)}.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws URISyntaxException 
	 */
//	@Test
//	public void testValidKey() throws InterruptedException, ExecutionException, URISyntaxException {
//		
//		URI hostname = new URI("http://www.example.com:8080");
//		String filePath = "foo.jar";
//		UrlBean result;
//		String key;
//		String url;
//		
//		result = classUnderTest.addKey(hostname, filePath).get();
//		assertTrue(result.isSuccess());
//		assertEquals("www.example.com", result.getUrl().getHost());
//		assertEquals(8080, result.getUrl().getPort(), 0.0);
//		
//		String start = hostname + "/rest/webresources/serviceclient/" + filePath + "?key=";
//		url = result.getUrl().toString();
//		assertTrue(url.contains("?key="));
//		assertTrue(url.startsWith(start));
//		assertTrue(url.length() > start.length());
//		
//		key = url.replace(start, "");
//		assertTrue(ServiceClientJarAccess.isKeyValid(filePath, key));
//	}

	/**
	 * Test method for {@link ServiceClientJarAccess#isKeyValid(String, String)}.
	 */
//	@Test
//	public void testInvalidKey() {
//		
//		String filePath = "foo.jar";
//		String key = "d2nuvo";
//		assertTrue(!ServiceClientJarAccess.isKeyValid(filePath, key));
//	}
}
