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

import java.util.Date;

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

	PrivacyLog privacyLog;
	DataTransferAnalyzer dataTransferAnalyzer;

	Date time1 = new Date(1000000);
	Date time2 = new Date(2000000);
	Date time3 = new Date(3000000);
	Date time4 = new Date(4000000);
	Date time5 = new Date(5000000);
	Date time6 = new Date(6000000);
	Date time7 = new Date(7000000);
	Date time8 = new Date(8000000);
	Date time9 = new Date(9000000);
	
	IIdentity id1 = new IdentityImpl(IdentityType.CSS_RICH, "identifier1", "domain1");
	IIdentity id2 = new IdentityImpl(IdentityType.CSS_LIGHT, "identifier2", "domain1");
	IIdentity id3 = new IdentityImpl(IdentityType.CSS, "identifier3", "domain1");
	IIdentity id4 = new IdentityImpl(IdentityType.CIS, "identifier4", "domain2");
	
	String class1 = "class1";
	String class2 = "class2";
	String class3 = "class3";
	String class4 = "class4";
	
	String dataType1 = "dataType1";
	String dataType2 = "dataType2";
	String dataType3 = "dataType3";
	String dataType4 = "dataType4";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		privacyLog = new PrivacyLog();
		
		privacyLog.append(new DataAccessLogEntry(time1, id1, class1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time2, id1, class2, id2, -1));
		privacyLog.append(new DataAccessLogEntry(time3, id1, class3, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time4, id1, class3, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time5, id1, class1, id3, -1));
		privacyLog.append(new DataAccessLogEntry(time6, id2, class1, id4, -1));
		privacyLog.append(new DataAccessLogEntry(time7, id2, class1, id1, -1));

		privacyLog.append(new DataTransmissionLogEntry(dataType1, time2, id1, id1, class2, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time3, id2, id1, class4, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time4, id3, id1, class1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType4, time4, id4, id1, class3, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType1, time5, id1, id1, class3, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time6, id2, id1, class2, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time7, id3, id1, class4, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType4, time8, id4, id1, class1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType1, time8, id1, id2, class4, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType2, time9, id2, id1, class1, -1, ChannelType.XMPP));
		privacyLog.append(new DataTransmissionLogEntry(dataType3, time9, id3, id1, class3, -1, ChannelType.XMPP));
		
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

		estimatePrivacyBreachIIdentity(id1, time2, 10);
		estimatePrivacyBreachIIdentity(id2, time8, 1);
		estimatePrivacyBreachIIdentity(id3, time2, 0);  // any valid past time for 0 transmissions
		estimatePrivacyBreachIIdentity(id4, time2, 0);  // any valid past time for 0 transmissions
	}
	
	private void estimatePrivacyBreachIIdentity(IIdentity sender, Date firstTransmission,
			long numPacketsTransmitted) throws AssessmentException {

		AssessmentResultIIdentity result;
		double expected;

		result = dataTransferAnalyzer.estimatePrivacyBreach(sender);
		
		// Check correlations
		result.getCorrWithDataAccessByAll();  // TODO
		result.getCorrWithDataAccessByAllDev();  // TODO
		result.getCorrWithDataAccessBySender();  // TODO
		result.getCorrWithDataAccessBySenderDev();  // TODO

		// Check number and frequency of transmissions
		assertEquals(numPacketsTransmitted, result.getNumAllPackets());
		expected = new Date().getTime() - firstTransmission.getTime();
		expected /= 1000 * 60 * 60 * 24 * 30.5;
		expected = numPacketsTransmitted / expected;
		assertEquals(expected, result.getNumPacketsPerMonth(), 0.05 * expected);
		
		// Check sender
		assertEquals(sender, result.getSender());
		assertEquals(sender.getJid(), result.getSender().getJid());
	}

	/**
	 * Test method for {@link DataTransferAnalyzer#estimatePrivacyBreach(String)}.
	 * @throws AssessmentException 
	 */
	@Test
	public void testEstimatePrivacyBreachString() throws AssessmentException {

		estimatePrivacyBreachString(class1, time4, 3);
		estimatePrivacyBreachString(class2, time2, 2);
		estimatePrivacyBreachString(class3, time4, 3);
		estimatePrivacyBreachString(class4, time3, 3);
	}
	
	private void estimatePrivacyBreachString(String sender, Date firstTransmission,
			long numPacketsTransmitted) throws AssessmentException {

		AssessmentResultClassName result;
		double expected;

		result = dataTransferAnalyzer.estimatePrivacyBreach(sender);
		
		// Check correlations
		result.getCorrWithDataAccessByAll();  // TODO
		result.getCorrWithDataAccessByAllDev();  // TODO
		result.getCorrWithDataAccessBySender();  // TODO
		result.getCorrWithDataAccessBySenderDev();  // TODO

		// Check number and frequency of transmissions
		assertEquals(numPacketsTransmitted, result.getNumAllPackets());
		expected = new Date().getTime() - firstTransmission.getTime();
		expected /= 1000 * 60 * 60 * 24 * 30.5;
		expected = numPacketsTransmitted / expected;
		assertEquals(expected, result.getNumPacketsPerMonth(), 0.05 * expected);
		
		// Check sender
		assertEquals(sender, result.getSender());
	}
}
