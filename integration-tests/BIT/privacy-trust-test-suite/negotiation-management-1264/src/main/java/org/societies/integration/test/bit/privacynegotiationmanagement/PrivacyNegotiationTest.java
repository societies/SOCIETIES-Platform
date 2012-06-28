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
package org.societies.integration.test.bit.privacynegotiationmanagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationStatus;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Eliza, Olivier Maridat (Trialog)
 *
 */
public class PrivacyNegotiationTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyNegotiationTest.class.getSimpleName());
	public static Integer testCaseNumber = 0;

	private RequestorService requestorService;
	private RequestorCis requestorCis;
	private RequestPolicy servicePrivacyPolicy;
	private RequestPolicy cisPrivacyPolicy;
	private ResponsePolicy serviceResponsePolicy;
	private ResponsePolicy cisResponsePolicy;

	private IIdentity userId;
	private IndividualCtxEntity userCtxEntity;
	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxAttribute locationAttribute;
	private CtxAttributeIdentifier ctxStatusAttributeId;
	private CtxAttribute statusAttribute;



	@Before
	public void setUp() {
		String testTitle = new String("setUp");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		// -- Verify dependency injection
		if (!TestCase1264.isDepencyInjectionDone()) {
			LOG.error("[#"+testCaseNumber+"] [Dependency Injection "+TestCase1264.class.getSimpleName()+" not ready] "+testTitle);
			fail("Dependency Injection "+TestCase1264.class.getSimpleName()+" not ready: "+testTitle);
		}

		// -- Add privacy policies
		try {
			// 3P service privacy policy
			requestorService = getRequestorService();
			servicePrivacyPolicy = getServicePolicy();
			TestCase1264.privacyPolicyManager.updatePrivacyPolicy(servicePrivacyPolicy);
			// CIS privacy policy
			requestorCis = getRequestorCis();
			cisPrivacyPolicy = getCisPolicy();
			TestCase1264.privacyPolicyManager.updatePrivacyPolicy(cisPrivacyPolicy);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getLocalizedMessage()+" - "+testTitle);
		} catch (InvalidFormatException e) {
			LOG.error("[#"+testCaseNumber+"] [Error InvalidFormatException when generating the CIS id] "+testTitle, e);
			fail("Error InvalidFormatException when generating the CIS id: "+e.getMessage()+" - "+testTitle);
		} catch (URISyntaxException e) {
			LOG.error("[#"+testCaseNumber+"] [Error URISyntaxException when generating the service id] "+testTitle, e);
			fail("Error URISyntaxException  when generating the service id: "+e.getMessage()+" - "+testTitle);
		}

		// -- Generate expected ResponsePolicy
		this.serviceResponsePolicy = this.getServiceResponsePolicy();
		this.cisResponsePolicy = this.getCisResponsePolicy();

		// -- Add types to context
		setupContext();
	}

	@After
	public void tearDown() {
		String testTitle = new String("tearDown");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		// -- Delete privacy policies
		try {
			deleteContext();
			TestCase1264.privacyPolicyManager.deletePrivacyPolicy(requestorService);
			TestCase1264.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		}
	}



	@Test
	public void testStartNegotiationCis() {
		String testTitle = new String("testStartNegotiationCis");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		//		AgreementEnvelope expectedPrivacyAgreement = null;
		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			TestCase1264.privacyPolicyNegotiationManager.negotiateCISPolicy(requestorCis);
			LOG.info("[#"+testCaseNumber+"] "+testTitle+": CIS Privacy Policy Negotiation finished");
			retrievedPrivacyAgreement = TestCase1264.privacyAgreementManager.getAgreement(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
		assertNotNull("Privacy agreement is null: the negotiation has failed", retrievedPrivacyAgreement);
	}

	@Test
	@Ignore
	public void testStartNegotiationService() {
		String testTitle = new String("testStartNegotiationService");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		//		AgreementEnvelope expectedPrivacyAgreement = null;
		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			TestCase1264.privacyPolicyNegotiationManager.negotiateServicePolicy(requestorService);
			LOG.info("[#"+testCaseNumber+"] "+testTitle+": CIS Privacy Policy Negotiation finished");
			retrievedPrivacyAgreement = TestCase1264.privacyAgreementManager.getAgreement(requestorService);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
		assertNotNull("Privacy agreement is null: the negotiation has failed", retrievedPrivacyAgreement);
	}



	/* **************
	 *    Tools     *
	 * **************/

	private RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException {
		IIdentity requestorId = TestCase1264.commManager.getIdManager().fromJid("red@societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
		serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException {
		IIdentity requestorId = TestCase1264.commManager.getIdManager().getThisNetworkNode();
		IIdentity cisId =TestCase1264.commManager.getIdManager().fromJid("lions.societies.local");
		return new RequestorCis(requestorId, cisId);
	}

	private RequestPolicy getServicePolicy(){
		/*
		 * location requestItem
		 */
		Resource rLocation = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemLocation = new RequestItem(rLocation, actions, conditions);


		/*
		 * status requestItem
		 */

		Resource rStatus = new Resource(CtxAttributeTypes.STATUS);
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.READ));
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions1.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions1.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions1.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemStatus = new RequestItem(rStatus, actions1, conditions1);

		/* ----------------------------------------------------*/


		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(itemLocation);
		requests.add(itemStatus);
		RequestPolicy policy = new RequestPolicy(this.requestorService, requests);


		return policy;

	}

	private ResponsePolicy getCisResponsePolicy() {
		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.cisPrivacyPolicy.getRequests()){
			ResponseItem responseItem = new ResponseItem(requestItem, Decision.PERMIT);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy(this.requestorCis, responses, NegotiationStatus.SUCCESSFUL);
		return response;
	}
	private ResponsePolicy getServiceResponsePolicy() {

		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.servicePrivacyPolicy.getRequests()){
			ResponseItem responseItem = new ResponseItem(requestItem, Decision.PERMIT);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy(this.requestorService, responses, NegotiationStatus.SUCCESSFUL);
		return response;
	}


	private RequestPolicy getCisPolicy(){
		/*
		 * location requestItem
		 */
		Resource rLocation = new Resource(CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemLocation = new RequestItem(rLocation, actions, conditions, true);


		/*
		 * status requestItem
		 */
		Resource rStatus = new Resource(CtxAttributeTypes.STATUS);
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.READ));
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions1.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions1.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions1.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemStatus = new RequestItem(rStatus, actions1, conditions1, true);

		/*
		 * birthday requestItem
		 */
		Resource rBirthday = new Resource(CtxAttributeTypes.BIRTHDAY);
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.WRITE));
		actions2.add(new Action(ActionConstants.CREATE));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions2.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions2.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "NO"));
		conditions2.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemBirthday = new RequestItem(rBirthday, actions2, conditions2, true);

		/* ----------------------------------------------------*/

		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(itemLocation);
		requests.add(itemStatus);
		requests.add(itemBirthday);
		RequestPolicy policy = new RequestPolicy(this.requestorCis, requests);
		return policy;
	}

	private void setupContext() {
		userId = TestCase1264.commManager.getIdManager().getThisNetworkNode();
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		userCtxEntity = new IndividualCtxEntity(ctxId);

		ctxLocationAttributeId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");

		ctxStatusAttributeId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		statusAttribute = new CtxAttribute(ctxStatusAttributeId);
		statusAttribute.setStringValue("busy");

		userCtxEntity.addAttribute(locationAttribute);
		userCtxEntity.addAttribute(statusAttribute);
	}

	private void deleteContext() {
	}
}
