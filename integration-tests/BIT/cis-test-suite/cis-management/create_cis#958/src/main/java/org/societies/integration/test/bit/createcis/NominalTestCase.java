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
package org.societies.integration.test.bit.createcis;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;


/**
 * @author Rafik, Olivier
 */
public class NominalTestCase {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class.getSimpleName());
	public static Integer testCaseNumber;

	private String privacyPolicyWithoutRequestor;
	private String cssId;
	private String cisId;
	private String cssPassword;
	private String cisName;
	private String cisDescription;
	private String cisType;
	private Hashtable<String, MembershipCriteria> cisMembershipCriteria;


	@Before
	public void setUp() {
		LOG.info("[#"+testCaseNumber+"] setUp");

		cssId = TestCase958.commManager.getIdManager().getThisNetworkNode().getJid();
		cssPassword = "password.societies.local";
		cisName = "CisTest";
		cisDescription = "CIS to Test CIS Creation";
		cisType = "trialog";
		cisMembershipCriteria = new Hashtable<String, MembershipCriteria>();
		privacyPolicyWithoutRequestor = "<RequestPolicy>" +
				"<Target>" +
				"<Resource>" +
				"<Attribute AttributeId=\"contextType\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">" +
				"<AttributeValue>fdsfsf</AttributeValue>" +
				"</Attribute>" +
				"</Resource>" +
				"<Action>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants\">" +
				"<AttributeValue>WRITE</AttributeValue>" +
				"</Attribute>" +
				"<optional>false</optional>" +
				"</Action>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">" +
				"<AttributeValue DataType=\"SHARE_WITH_3RD_PARTIES\">dfsdf</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<Condition>" +
				"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">" +
				"<AttributeValue DataType=\"DATA_RETENTION_IN_MINUTES\">412</AttributeValue>" +
				"</Attribute>" +
				"<optional>true</optional>" +
				"</Condition>" +
				"<optional>false</optional>" +
				"</Target>" +
				"</RequestPolicy>";
	}

	@After
	public void tearDown() {
		LOG.info("[#"+testCaseNumber+"] tearDown");
		if (null != cisId && !"".equals(cisId)) {
			TestCase958.cisManager.deleteCis(cisId);
		}
	}


	@Test
	public void testCreateCisWithoutPrivacyPolicyCreation() {
		String testTitle = "testCreateCisWithoutPrivacyPolicyCreation: create a CIS without create of its privacy policy";
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		// Create CIS
		LOG.info("############## CSS Id:"+cssId+" ("+cssPassword+")");
		Future<ICisOwned> futureCis = TestCase958.cisManager.createCis(cisName, cisType, cisMembershipCriteria, cisDescription);
		ICisOwned newCis = null;
		assertNotNull("Future new CIS is null", futureCis);
		
		// Retrieve future CIS
		try {
			newCis = futureCis.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException e)  {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error InterruptedException] "+testTitle);
		} catch (ExecutionException e) {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error ExecutionException] "+testTitle);
		} catch (TimeoutException e) {	
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error TimeoutException] "+testTitle);
		}
		assertNotNull("New CIS is null", newCis);

		// Retrieve CIS id
		cisId =  newCis.getCisId();
		assertNotNull("New CIS id is null", cisId);

		// Check if the CIS is on the CIS Management registry
		ICis cisRetrieved =  TestCase958.cisManager.getCis(cisId);
		assertNotNull("New CIS is not stored", cisRetrieved);
		assertEquals("New CIS and retrived CIS should be the same but are not", newCis, cisRetrieved);
	}
	
	@Test
	public void testCreateCis() {
		String testTitle = "testCreateCis: create a CIS";
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		// Create CIS
		Future<ICisOwned> futureCis = TestCase958.cisManager.createCis(cisName, cisType, cisMembershipCriteria, cisDescription, privacyPolicyWithoutRequestor);
		ICisOwned newCis = null;
		
		assertNotNull("Future new CIS is null", futureCis);
		
		// Retrieve future CIS
		try {
			newCis = futureCis.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException e)  {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error InterruptedException] "+testTitle);
		} catch (ExecutionException e) {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error ExecutionException] "+testTitle);
		} catch (TimeoutException e) {	
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error TimeoutException] "+testTitle);
		}
		assertNotNull("New CIS is null", newCis);

		// Retrieve CIS id
		cisId =  newCis.getCisId();
		
		assertNotNull("New CIS id is null", cisId);

		// Check if the CIS is on the CIS Management registry
		ICis cisRetrieved =  TestCase958.cisManager.getCis(cisId);
		
		
		assertNotNull("New CIS is not stored", cisRetrieved);
		
		assertTrue("New CIS and retrived CIS should be the same but are not", newCis.equals(cisRetrieved));
		

		RequestPolicy expectedPrivacyPolicy = null;
		RequestPolicy retrievedPrivacyPolicy = null;
		try {
			
			// Retrieve privacy policy owner id
			IIdentity cisIdentity = TestCase958.commManager.getIdManager().fromJid(cisId);
			
			
			RequestorCis requestorCis = new RequestorCis(TestCase958.commManager.getIdManager().getThisNetworkNode(), cisIdentity);
			
			
			expectedPrivacyPolicy = TestCase958.privacyPolicyManager.fromXMLString(privacyPolicyWithoutRequestor);
			
			
			expectedPrivacyPolicy.setRequestor(requestorCis);
			
			// Retrieve privacy policy
			retrievedPrivacyPolicy =  TestCase958.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			
		} catch (PrivacyException e) {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error PrivacyException] "+testTitle);
		} catch (InvalidFormatException e) {
			LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
			fail("[Error InvalidFormatException] "+testTitle);
		}

		LOG.info(retrievedPrivacyPolicy.toXMLString());
		assertNotNull("CIS Privacy policy is null but it should not", retrievedPrivacyPolicy);
		
		//Modified by Rafik
		//Before:
		//assertEquals("CIS privacy policy retrieved is not the one that has been sent", expectedPrivacyPolicy, retrievedPrivacyPolicy);
		//After:
		assertEquals("CIS privacy policy retrieved is not the one that has been sent", expectedPrivacyPolicy.toXMLString(), retrievedPrivacyPolicy.toXMLString());
	}
}
