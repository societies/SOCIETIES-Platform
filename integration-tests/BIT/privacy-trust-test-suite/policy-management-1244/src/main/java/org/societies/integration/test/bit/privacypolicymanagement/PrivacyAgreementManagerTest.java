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
package org.societies.integration.test.bit.privacypolicymanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Test list:
 * - retrieve not existing cis and 3P service privacy agreements
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyAgreementManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyAgreementManagerTest.class.getSimpleName());
	
	public static int testCaseNumber = 0;
	
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private AgreementEnvelope agreementCis;
	private AgreementEnvelope agreementService;

	@Before
	public void setUp() throws Exception {
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase1244.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyAgreementManagerTest not ready");
		}
		
		// Data
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		agreementCis = getAgreementEnveloppe(requestorCis);
		agreementService = getAgreementEnveloppe(requestorService);
	}
	


	@Test
	public void testGetCisPrivacyAgreementNotExisting() {
		String testTitle = new String("testGetCisPrivacyAgreementNotExisting: retrieve a non-existing privacy agreement");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		AgreementEnvelope expectedPrivacyAgreement = null;
		AgreementEnvelope privacyAgreement = null;
		try {
			privacyAgreement = TestCase1244.privacyAgreementManager.getAgreement(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by Rafik
		//before:
		//assertEquals("Expected null privacy Agreement, but it is not", privacyAgreement, expectedPrivacyAgreement);
		//After:
		assertNull("Expected null privacy Agreement, but it is not", privacyAgreement);
	}
	
	@Test
	public void testGetServicePrivacyAgreementNotExisting() {
		String testTitle = new String("testGetServicePrivacyAgreementNotExisting: retrieve a non-existing privacy agreement");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		AgreementEnvelope expectedPrivacyAgreement = null;
		AgreementEnvelope privacyAgreement = null;
		try {
			privacyAgreement = TestCase1244.privacyAgreementManager.getAgreement(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Test PrivacyException] "+testTitle, e);
			fail("Privacy error");
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Test Exception] "+testTitle, e);
			fail("Error");
		}
		
		//Modified by Rafik
		//before:
		//assertEquals("Expected null privacy Agreement, but it is not", privacyAgreement, expectedPrivacyAgreement);
		//After:
		assertNull("Expected null privacy Agreement, but it is not", privacyAgreement);
	}


	/* --- Tools --- */
	private RequestorService getRequestorService() throws InvalidFormatException{
		IIdentity requestorId = TestCase1244.commManager.getIdManager().fromJid("red@societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
		} catch (URISyntaxException e) {
			LOG.error("Can't create the service ID", e);
		}
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		IIdentity otherCssId = TestCase1244.commManager.getIdManager().fromJid("red@societies.local");
		IIdentity cisId = TestCase1244.commManager.getIdManager().fromJid("onecis.societies.local");
		return new RequestorCis(otherCssId, cisId);
	}

	private AgreementEnvelope getAgreementEnveloppe(Requestor requestor) throws IOException {
//		List<ResponseItem> responseItems = getResponseItems();
//		NegotiationAgreement agreement = new NegotiationAgreement(responseItems);
//		agreement.setRequestor(requestor);
//		AgreementFinaliser finaliser = new AgreementFinaliser();
//		byte[] signature = finaliser.signAgreement(agreement);
//		Key publicKey = finaliser.getPublicKey();
//		AgreementEnvelope agreementEnveloppe = new AgreementEnvelope(agreement, SerialisationHelper.serialise(publicKey), signature);
//		return agreementEnveloppe;
		return null;
	}

	private List<ResponseItem> getResponseItems() {
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		Resource locationResource = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
		responseItems.add(new ResponseItem(rItem, Decision.PERMIT));
		Resource someResource = new Resource("someResource");
		List<Condition> extendedConditions = new ArrayList<Condition>();
		extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"NO"));
		extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "YES"));
		List<Action> extendedActions = new ArrayList<Action>();
		extendedActions.add(new Action(ActionConstants.READ));
		extendedActions.add(new Action(ActionConstants.CREATE));
		extendedActions.add(new Action(ActionConstants.WRITE));
		extendedActions.add(new Action(ActionConstants.DELETE));
		RequestItem someItem = new RequestItem(someResource, extendedActions, extendedConditions, false);
		responseItems.add(new ResponseItem(someItem, Decision.DENY));
		return responseItems;
	}
}
