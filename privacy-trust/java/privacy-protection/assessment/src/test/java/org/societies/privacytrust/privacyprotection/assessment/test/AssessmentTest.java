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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.identity.IdentityImpl;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;
import org.societies.privacytrust.privacyprotection.assessment.logic.Assessment;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class AssessmentTest {

	private Date time1 = new Date(1000000);
	private Date time2 = new Date(2000000);
	private Date time3 = new Date(3000000);
	private Date time4 = new Date(4000000);
	private Date time5 = new Date(5000000);
	private Date time6 = new Date(6000000);
	private Date time7 = new Date(7000000);
	private Date time8 = new Date(8000000);
	private Date time9 = new Date(9000000);
	
	private IIdentity id1 = new IdentityImpl(IdentityType.CSS_RICH, "identifier1", "domain1");
	private IIdentity id2 = new IdentityImpl(IdentityType.CSS_LIGHT, "identifier2", "domain1");
	private IIdentity id3 = new IdentityImpl(IdentityType.CSS, "identifier3", "domain1");
	private IIdentity id4 = new IdentityImpl(IdentityType.CIS, "identifier4", "domain2");
	
	private String class1 = "class1";
	private String class2 = "class2";
	private String class3 = "class3";
	private String class4 = "class4";
	
	private List<String> stack1 = new ArrayList<String>();
	private List<String> stack2 = new ArrayList<String>();
	private List<String> stack3 = new ArrayList<String>();
	private List<String> stack4 = new ArrayList<String>();
	
	private String bundle1 = "bundle1";
	private String bundle2 = "bundle2";
	private String bundle3 = "bundle3";
	private String bundle4 = "bundle4";

	private List<String> bundles1 = new ArrayList<String>();
	private List<String> bundles2 = new ArrayList<String>();
	private List<String> bundles3 = new ArrayList<String>();
	private List<String> bundles4 = new ArrayList<String>();

	private String dataType1 = "dataType1";
	private String dataType2 = "dataType2";
	private String dataType3 = "dataType3";
	private String dataType4 = "dataType4";

	PrivacyLog privacyLog;
	Assessment assessment;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		stack1.add(class1);
		stack2.add(class2);
		stack3.add(class3);
		stack4.add(class4);
		
		bundles1.add(bundle1);
		bundles2.add(bundle2);
		bundles3.add(bundle3);
		bundles4.add(bundle2);
		bundles4.add(bundle4);
		
		privacyLog = new PrivacyLog();
		privacyLog.append(new DataAccessLogEntry(time1, id1, class1, stack1, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time2, id1, class2, stack2, bundles1, id2, -1));
		privacyLog.append(new DataAccessLogEntry(time3, id1, class3, stack3, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time4, id1, class3, stack3, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time5, id1, class1, stack1, bundles1, id3, -1));
		privacyLog.append(new DataAccessLogEntry(time6, id2, class1, stack1, bundles1, id4, -1));
		privacyLog.append(new DataAccessLogEntry(time7, id2, class1, stack1, bundles1, id1, -1));

		privacyLog.append(new DataTransmissionLogEntry(dataType1, time2, id1, id1, class2, stack2, bundles1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time3, id2, id1, class4, stack4, bundles2, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time4, id3, id1, class1, stack1, bundles3, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType4, time4, id4, id1, class3, stack3, bundles4, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType1, time5, id1, id1, class3, stack3, bundles1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time6, id2, id1, class2, stack2, bundles2, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time7, id3, id1, class4, stack4, bundles3, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType4, time8, id4, id1, class1, stack1, bundles4, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType1, time8, id1, id2, class4, stack4, bundles1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time9, id2, id1, class1, stack1, bundles2, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time9, id3, id1, class3, stack3, bundles3, -1, ChannelType.XMPP));
		
		assessment = new Assessment();
		assessment.setPrivacyLog(privacyLog);
		assessment.init();
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link Assessment#setAutoPeriod(int)}.
	 */
	@Test
	public void testSetAutoPeriod() {
		// TODO
	}

	/**
	 * Test method for {@link Assessment#getAssessmentAllIds()}.
	 */
	@Test
	public void testGetAssessmentAllIds() {
		
//		HashMap<IIdentity, AssessmentResultIIdentity> result1;
		HashMap<IIdentity, AssessmentResultIIdentity> result2;
		
//		result1 = assessment.getAssessmentAllIds();
//		assessment.assessAllNow();
		result2 = assessment.getAssessmentAllIds(null, null);
		
//		assertEquals(0, result1.size());
		assertEquals(2, result2.size());
	}

	/**
	 * Test method for {@link Assessment#getAssessmentAllClasses()}.
	 */
	@Test
	public void testGetAssessmentAllClasses() {
		
//		HashMap<String, AssessmentResultClassName> result1;
		HashMap<String, AssessmentResultClassName> result2;
		
//		result1 = assessment.getAssessmentAllClasses();
//		assessment.assessAllNow();
		result2 = assessment.getAssessmentAllClasses(true, null, null);
		
//		assertEquals(0, result1.size());
		assertEquals(4, result2.size());
	}

	/**
	 * Test method for {@link Assessment#getAssessment(IIdentity)}.
	 */
	@Test
	public void testGetAssessmentIIdentity() {
		assessment.assessAllNow(null, null);
	}

	/**
	 * Test method for {@link Assessment#getAssessment(String)}.
	 */
	@Test
	public void testGetAssessmentString() {
		assessment.assessAllNow(null, null);
	}

	/**
	 * Test method for {@link Assessment#getNumDataAccessEvents()}.
	 */
	@Test
	public void testGetNumDataAccessEvents() {
		
		long initial = 7;
		int additional = Math.abs(new Random().nextInt()) % 8;
		long transmissionEvents = 11;
		
		assertEquals(initial, assessment.getNumDataAccessEvents(null, null));
		assertEquals(transmissionEvents, assessment.getNumDataTransmissionEvents(null, null));
		
		appendDataAccessEvents(additional);
		
		assertEquals(initial + additional, assessment.getNumDataAccessEvents(null, null));
		assertEquals(transmissionEvents, assessment.getNumDataTransmissionEvents(null, null));
	}

	/**
	 * Test method for {@link Assessment#getNumDataTransmissionEvents()}.
	 */
	@Test
	public void testGetNumDataTransmissionEvents() {
		
		long initial = 11;
		int additional = Math.abs(new Random().nextInt()) % 8;
		long accessEvents = 7;
		
		assertEquals(initial, assessment.getNumDataTransmissionEvents(null, null));
		assertEquals(accessEvents, assessment.getNumDataAccessEvents(null, null));
		
		appendDataTransmissionEvents(additional);
		
		assertEquals(initial + additional, assessment.getNumDataTransmissionEvents(null, null));
		assertEquals(accessEvents, assessment.getNumDataAccessEvents(null, null));
	}
	
	private void appendDataAccessEvents(int num) {
		for (int k = 0; k < num; k++) {
			privacyLog.append(new DataAccessLogEntry(time7, id2, class1, stack1, bundles1, id1, -1));
		}
	}
	
	private void appendDataTransmissionEvents(int num) {
		for (int k = 0; k < num; k++) {
			privacyLog.append(new DataTransmissionLogEntry(dataType1, time2, id1, id1, class2, stack2, bundles1, -1, ChannelType.XMPP));
		}
	}
}
