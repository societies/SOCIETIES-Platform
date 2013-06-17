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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.PrivacyLogFilter;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;

/**
 * Test case for Privacy Assessment
 *
 * @author Mitja Vardjan (SETCCE)
 *
 */
public class PrivacyLogTest {
	
	private static Logger LOG = LoggerFactory.getLogger(PrivacyLogTest.class.getSimpleName());
	
	private PrivacyLog privacyLog;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		privacyLog = new PrivacyLog();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		privacyLog = null;
	}

	@Test
	public void testGetAll() {
		
		LOG.debug("testGetAll()");
		
		List<DataTransmissionLogEntry> result;
		
		result = privacyLog.getAll();
		assertNotNull(result);
		result = privacyLog.getDataTransmission();
		assertNotNull(result);
	}

	@Test
	public void testGetDataAccess() {
		
		LOG.debug("testGetDataAccess()");
		
		int size1;
		int size2;
		
		DataAccessLogEntry entry = new DataAccessLogEntry(new Date(), null, "", null, null, null, 100);
		
		size1 = privacyLog.getDataAccess().size();
		privacyLog.append(entry);
		size2 = privacyLog.getDataAccess().size();
		assertEquals(size1 + 1, size2);
	}

	@Test
	public void testSearch() {
		
		PrivacyLogFilter filter;
		List<DataTransmissionLogEntry> result;

		int initialSize = privacyLog.getAll().size();
		
		// TODO: use different implementations when available (Redmine #1124)
		IIdentity receiver1 = mock(IIdentity.class);
		IIdentity sender1 = mock(IIdentity.class);
		IIdentity receiver2 = mock(IIdentity.class);
		IIdentity sender2 = mock(IIdentity.class);
		
		Date date1 = new Date(1000000);
		Date date2 = new Date(2000000);
		Date date3 = new Date(3000000);
		
		List<String> stack1 = new ArrayList<String>();
		List<String> stack2 = new ArrayList<String>();
		List<String> bundles1 = new ArrayList<String>();
		List<String> bundles2 = new ArrayList<String>();
		stack1.add("senderClass1");
		stack2.add("senderClass2");

		DataTransmissionLogEntry entry1 = new DataTransmissionLogEntry(
				"dataType1", date1, receiver1, sender1, "senderClass1", stack1, bundles1, 861, ChannelType.XMPP);
		DataTransmissionLogEntry entry2 = new DataTransmissionLogEntry(
				"dataType1", date2, receiver2, sender1, "senderClass1", stack1, bundles2, 691, ChannelType.XMPP);
		DataTransmissionLogEntry entry3 = new DataTransmissionLogEntry(
				"dataType2", date3, receiver2, sender2, "senderClass2", stack2, bundles1, 121, ChannelType.FACEBOOK);
		
		privacyLog.append(entry1);
		privacyLog.append(entry2);
		privacyLog.append(entry3);
		
		// Filters with one single parameter set to non-null
		
		filter = new PrivacyLogFilter();
		result = privacyLog.search(filter);
		assertEquals(initialSize + 3, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setChannelId(new ChannelType[] {ChannelType.XMPP} );
		result = privacyLog.search(filter);
		assertEquals(initialSize + 2, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setDataType(new String[] {"dataType1"});
		result = privacyLog.search(filter);
		assertEquals(initialSize + 2, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setEnd(new Date(1500000));
		result = privacyLog.search(filter);
		assertEquals(initialSize + 1, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setReceiver(receiver1);
		result = privacyLog.search(filter);
		assertEquals(initialSize + 1, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setSender(sender1);
		result = privacyLog.search(filter);
		assertEquals(initialSize + 2, result.size());
		
		filter = new PrivacyLogFilter();
		filter.setSenderClass("senderClass1");
		result = privacyLog.search(filter);
		assertEquals(initialSize + 2, result.size());
		
//		filter = new PrivacyLogFilter();
//		filter.setSentToGroup(true);
//		result = privacyLog.search(filter);
//		assertEquals(initialSize + , result.size());
		
//		filter = new PrivacyLogFilter();
//		filter.setSentToLocalCss(false);
//		result = privacyLog.search(filter);
//		assertEquals(initialSize + , result.size());
		
		filter = new PrivacyLogFilter();
		filter.setStart(new Date(1500000));
		result = privacyLog.search(filter);
		assertEquals(initialSize + 2, result.size());
		
		// Filters with 2 or more parameters
		
		filter = new PrivacyLogFilter();
		filter.setSenderClass("senderClass1");
		filter.setStart(new Date(1500000));
		result = privacyLog.search(filter);
		assertEquals(initialSize + 1, result.size());
	}
}
