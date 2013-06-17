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
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultIIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.ChannelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataTransmissionLogEntry;
import org.societies.identity.IdentityImpl;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;
import org.societies.privacytrust.privacyprotection.assessment.logic.DataTransferAnalyzer;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class DataTransferAnalyzerTest {

	private PrivacyLog privacyLog;
	private DataTransferAnalyzer dataTransferAnalyzer;

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
		privacyLog.append(new DataAccessLogEntry(time7, null, null, null, null, null, -1));

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
		privacyLog.append(new DataTransmissionLogEntry(null, time9, null, null, null, null, bundles1, -1, ChannelType.XMPP));
		
		dataTransferAnalyzer = new DataTransferAnalyzer(privacyLog);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link DataTransferAnalyzer#estimatePrivacyBreach(IIdentity)}.
	 * @throws AssessmentException 
	 */
	@Test
	public void testEstimatePrivacyBreachIIdentity() throws AssessmentException {

		AssessmentResultIIdentity[] result = new AssessmentResultIIdentity[4];
		
		result[0] = estimatePrivacyBreachIIdentity(id1, time2, 10);
		result[1] = estimatePrivacyBreachIIdentity(id2, time8, 1);
		result[2] = estimatePrivacyBreachIIdentity(id3, time2, 0);  // any valid past time for 0 transmissions
		result[3] = estimatePrivacyBreachIIdentity(id4, time2, 0);  // any valid past time for 0 transmissions
		
		// Check correlations with all data access events
		// - Zero or greater than zero
		assertTrue(result[0].getCorrWithDataAccessByAll() > 0);
		assertTrue(result[1].getCorrWithDataAccessByAll() > 0);
		assertEquals(result[2].getCorrWithDataAccessByAll(), 0, 0);
		assertEquals(result[3].getCorrWithDataAccessByAll(), 0, 0);
		// - Relations
		assertTrue(result[0].getCorrWithDataAccessByAll() > result[1].getCorrWithDataAccessByAll());
		assertTrue(result[1].getCorrWithDataAccessByAll() > result[2].getCorrWithDataAccessByAll());

		// Check correlations with those data access events that were done by sender
		// - Zero or greater than zero
		assertTrue(result[0].getCorrWithDataAccessByAll() > 0);
		assertTrue(result[1].getCorrWithDataAccessByAll() > 0);
		assertEquals(result[2].getCorrWithDataAccessBySender(), 0, 0);
		assertEquals(result[3].getCorrWithDataAccessBySender(), 0, 0);
		// - Relations
		assertTrue(result[0].getCorrWithDataAccessBySender() > result[1].getCorrWithDataAccessBySender());
		assertTrue(result[1].getCorrWithDataAccessBySender() > result[2].getCorrWithDataAccessBySender());
		
		// Verify correlation with all data access events is bigger (unless both are 0)
		assertTrue(result[0].getCorrWithDataAccessByAll() > result[0].getCorrWithDataAccessBySender());
		assertTrue(result[1].getCorrWithDataAccessByAll() > result[1].getCorrWithDataAccessBySender());
	}
	
	private AssessmentResultIIdentity estimatePrivacyBreachIIdentity(IIdentity sender, Date firstTransmission,
			long numPacketsTransmitted) throws AssessmentException {

		AssessmentResultIIdentity result;
		double expected;

		result = dataTransferAnalyzer.estimatePrivacyBreach(sender, null, null);
		
		// Check number and frequency of transmissions
		assertEquals(numPacketsTransmitted, result.getNumAllPackets());
		expected = new Date().getTime() - firstTransmission.getTime();
		expected /= 1000 * 60 * 60 * 24 * 30.5;
		expected = numPacketsTransmitted / expected;
		assertEquals(expected, result.getNumPacketsPerMonth(), 0.05 * expected);
		
		// Check sender
		assertEquals(sender, result.getSender());
		assertEquals(sender.getJid(), result.getSender().getJid());
		
		return result;
	}

	/**
	 * Test method for {@link DataTransferAnalyzer#estimatePrivacyBreach(String)}.
	 * @throws AssessmentException 
	 */
	@Test
	public void testEstimatePrivacyBreachString() throws AssessmentException {
		
		AssessmentResultClassName[] result = new AssessmentResultClassName[4];

		result[0] = estimatePrivacyBreachString(class1, time4, 3);
		result[1] = estimatePrivacyBreachString(class2, time2, 2);
		result[2] = estimatePrivacyBreachString(class3, time4, 3);
		result[3] = estimatePrivacyBreachString(class4, time3, 3);

		// Check correlations with all data access events
		// - Zero or greater than zero
		assertTrue(result[0].getCorrWithDataAccessByAll() > 0);
		assertTrue(result[1].getCorrWithDataAccessByAll() > 0);
		assertTrue(result[2].getCorrWithDataAccessByAll() > 0);
		assertTrue(result[3].getCorrWithDataAccessByAll() > 0);
		// - Relations
		assertTrue(result[2].getCorrWithDataAccessByAll() > result[1].getCorrWithDataAccessByAll());
		assertTrue(result[3].getCorrWithDataAccessByAll() > result[1].getCorrWithDataAccessByAll());

		// Check correlations with those data access events that were done by sender
		// - Zero or greater than zero
		assertTrue(result[0].getCorrWithDataAccessBySender() > 0);
		assertTrue(result[1].getCorrWithDataAccessBySender() > 0);
		assertTrue(result[2].getCorrWithDataAccessBySender() > 0);
		assertEquals(result[3].getCorrWithDataAccessBySender(), 0, 0);
		// - Relations
		assertTrue(result[0].getCorrWithDataAccessBySender() > result[1].getCorrWithDataAccessBySender());
		assertTrue(result[2].getCorrWithDataAccessBySender() > result[1].getCorrWithDataAccessBySender());
		assertTrue(result[1].getCorrWithDataAccessBySender() > result[3].getCorrWithDataAccessBySender());
		
		// Verify correlation with all data access events is bigger (unless both are 0)
		assertTrue(result[0].getCorrWithDataAccessByAll() > result[0].getCorrWithDataAccessBySender());
		assertTrue(result[1].getCorrWithDataAccessByAll() > result[1].getCorrWithDataAccessBySender());
		assertTrue(result[2].getCorrWithDataAccessByAll() > result[2].getCorrWithDataAccessBySender());
		assertTrue(result[3].getCorrWithDataAccessByAll() > result[3].getCorrWithDataAccessBySender());
	}
	
	private AssessmentResultClassName estimatePrivacyBreachString(String sender, Date firstTransmission,
			long numPacketsTransmitted) throws AssessmentException {

		AssessmentResultClassName result;
		double expected;

		result = dataTransferAnalyzer.estimatePrivacyBreach(sender, null, null);

		// Check number and frequency of transmissions
		assertEquals(numPacketsTransmitted, result.getNumAllPackets());
		expected = new Date().getTime() - firstTransmission.getTime();
		expected /= 1000 * 60 * 60 * 24 * 30.5;
		expected = numPacketsTransmitted / expected;
		assertEquals(expected, result.getNumPacketsPerMonth(), 0.05 * expected);
		
		// Check sender
		assertEquals(sender, result.getSender());
		
		return result;
	}
}
