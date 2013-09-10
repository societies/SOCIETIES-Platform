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
package org.societies.privacytrust.privacyprotection.assessment.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLogAppender;
import org.societies.privacytrust.privacyprotection.assessment.util.ServiceResolver;

/**
 * Test case for Privacy Assessment
 *
 * @author Mitja Vardjan (SETCCE)
 *
 */
public class PrivacyLogAppenderTest {

	private static Logger LOG = LoggerFactory.getLogger(PrivacyLogAppenderTest.class.getSimpleName());
	
	private PrivacyLogAppender privacyLogAppender;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		privacyLogAppender = new PrivacyLogAppender();
		PrivacyLog privacyLog = new PrivacyLog();
		privacyLogAppender.setPrivacyLog(privacyLog);
		privacyLogAppender.setServiceResolver(new ServiceResolver());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		privacyLogAppender = null;
	}

	@Test
	public void testLogDataAccess() {
		
		LOG.debug("testLogDataAccess()");
		
		DataAccessLogEntry entry = mock(DataAccessLogEntry.class);
		
		privacyLogAppender.log(entry);
	}

	@Test
	public void testLogDataTransmission() {
		
		LOG.debug("testLogDataTransmission()");
		
		DataTransmissionLogEntry entry = mock(DataTransmissionLogEntry.class);
		boolean result;
		
		result = privacyLogAppender.log(entry);
		assertTrue(result);
	}

	@Test
	public void testLogCommsFw() {
		
		LOG.debug("testLogCommsFw()");
		
		IIdentity sender = mock(IIdentity.class);
		IIdentity receiver = mock(IIdentity.class);
		Object payload = "abcdef";
		boolean result;
		
		result = privacyLogAppender.logCommsFw(sender, receiver, payload);
		assertTrue(result);
		result = privacyLogAppender.logCommsFw(null, receiver, payload);
		assertTrue(result);
		result = privacyLogAppender.logCommsFw(sender, null, payload);
		assertTrue(result);
		result = privacyLogAppender.logCommsFw(sender, receiver, null);
		assertTrue(result);
	}

	@Test
	public void testLogContext() {
		
		LOG.debug("testLogContext()");
		
		Requestor requestor = new Requestor(mock(IIdentity.class));
		assertNotNull(requestor.getRequestorId());
		IIdentity owner = mock(IIdentity.class);
		
		privacyLogAppender.logContext(requestor, owner);
		privacyLogAppender.logContext(null, owner);
		privacyLogAppender.logContext(requestor, null);
		privacyLogAppender.logContext(null, null);
	}

	@Test
	public void testLogContextSize() {
		
		LOG.debug("testLogContext()");
		
		Requestor requestor = new Requestor(mock(IIdentity.class));
		assertNotNull(requestor.getRequestorId());
		IIdentity owner = mock(IIdentity.class);
		
		privacyLogAppender.logContext(requestor, owner, 1000);
		privacyLogAppender.logContext(null, owner, 1000);
		privacyLogAppender.logContext(requestor, null, 1000);
		privacyLogAppender.logContext(null, null, 1000);
		
		privacyLogAppender.logContext(requestor, owner, -1);
		privacyLogAppender.logContext(null, owner, -1);
		privacyLogAppender.logContext(requestor, null, -1);
		privacyLogAppender.logContext(null, null, -1);
	}
}
