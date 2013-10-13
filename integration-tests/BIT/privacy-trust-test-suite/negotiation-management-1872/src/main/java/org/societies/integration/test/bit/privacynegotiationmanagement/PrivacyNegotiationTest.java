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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

/**
 * @author Eliza, Olivier Maridat (Trialog)
 *
 */
public class PrivacyNegotiationTest {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyNegotiationTest.class);
	public static int testCaseNumber = 0;

	/**
	 * Asynchronous helper
	 */
	private CountDownLatch lock;
	/**
	 * Received data from the asynchronous call
	 */
	private boolean negotiationResult;
	private EventListener eventListener;
	private String[] eventListened;

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

	//
	//	@BeforeClass
	//	public static void setUpClass() {
	//		String testTitle = new String("setUpClass");
	//		LOG.info("[#"+testCaseNumber+"] "+testTitle);
	//
	//
	//	}
	//
	//	@AfterClass
	//	public static void tearDownClass() {
	//		String testTitle = new String("tearDownClass");
	//		LOG.info("[#"+testCaseNumber+"] "+testTitle);
	//
	//		
	//	}

	@Before
	public void setUp() {
		String testTitle = new String("setUp");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		// -- Verify dependency injection
		if (!TestCase.isDepencyInjectionDone()) {
			LOG.error("[#"+testCaseNumber+"] [Dependency Injection "+TestCase.class.getSimpleName()+" not ready] "+testTitle);
			fail("Dependency Injection "+TestCase.class.getSimpleName()+" not ready: "+testTitle);
		}

		// -- Events
		lock = new CountDownLatch(1);
		negotiationResult = false;
		// -- Subscribe to Negotiation Events
		eventListener = new EventListener() {
			@Override
			public void handleInternalEvent(InternalEvent event) {
				String type = event.geteventType();
				LOG.info("[#"+testCaseNumber+"][Event] Internal event received: "+type);
				negotiationResult = false;
				// Negotiation Finished
				if (type.equals(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT)) {
					PPNegotiationEvent evenInfo = (PPNegotiationEvent) event.geteventInfo();
					LOG.info("[#"+testCaseNumber+"][Event] "+evenInfo.getNegotiationStatus());
					// Success
					if (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus.SUCCESSFUL.name().equals(evenInfo.getNegotiationStatus().name())) {
						negotiationResult = true;
					}
				}
				LOG.info("[#"+testCaseNumber+"][Event] "+negotiationResult);
				lock.countDown();
			}
			@Override
			public void handleExternalEvent(CSSEvent event) { }
		};
		eventListened = new String[] {
				EventTypes.FAILED_NEGOTIATION_EVENT,
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT
		};
		TestCase.eventManager.subscribeInternalEvent(eventListener, eventListened, null);

		// -- Add privacy policies
		try {
			// 3P service privacy policy
			requestorService = getRequestorService();
			servicePrivacyPolicy = getServicePolicy();
			TestCase.privacyPolicyManager.updatePrivacyPolicy(servicePrivacyPolicy);
			// CIS privacy policy
			requestorCis = getRequestorCis();
			cisPrivacyPolicy = getCisPolicy();
			TestCase.privacyPolicyManager.updatePrivacyPolicy(cisPrivacyPolicy);
			RequestPolicy updatedPrivacyPolicy = TestCase.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			assertNotNull("Retrieved privacy policy should not be null", updatedPrivacyPolicy);
			assertNotNull("Retrieved privacy policy items should not be null", updatedPrivacyPolicy.getRequests());
			assertTrue("Retrieved privacy policy items should not be empty", updatedPrivacyPolicy.getRequests().size() > 0);
//			RequestPolicy updatedPrivacyPolicy = TestCase.privacyPolicyManagerRemote.getPrivacyPolicy(requestorCis);
//			assertNotNull("Retrieved privacy policy should not be null", updatedPrivacyPolicy);
//			assertNotNull("Retrieved privacy policy items should not be null", updatedPrivacyPolicy.getRequests());
//			assertTrue("Retrieved privacy policy items should not be empty", updatedPrivacyPolicy.getRequests().size() > 0);
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
			TestCase.privacyPolicyManager.deletePrivacyPolicy(requestorService);
			TestCase.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		}

		// -- Unlisten events
		TestCase.eventManager.unSubscribeInternalEvent(eventListener, eventListened, null);
	}


	@Test
	public void testStartNegotiationCis() {
		String testTitle = new String("Start Negotiation CIS: nominal");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);

		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			// -- Launch negotiation
			int negotiationId = new Random().nextInt();
			TestCase.privacyPolicyNegotiationManager.negotiateCISPolicy(new NegotiationDetails(requestorCis, negotiationId));

			// -- Test
			// Negotiation Result
			LOG.info("[#"+testCaseNumber+"] Waiting for "+TestCase.getTimeout()+"ms");
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				// Do stuff
				fail("Timeout");
			}
			assertTrue("Negotiation should have succeed", negotiationResult);

			// Check Agreement
			retrievedPrivacyAgreement = TestCase.privacyAgreementManager.getAgreement(requestorCis);
			assertNotNull("Privacy agreement should not be null", retrievedPrivacyAgreement);
			assertNotNull("Privacy agreement (agreement) should not be null", retrievedPrivacyAgreement.getAgreement());
			assertNotNull("Privacy agreement response items should not be null", retrievedPrivacyAgreement.getAgreement().getRequestedItems());
			LOG.debug("[#"+testCaseNumber+"] Agreement: "+ResponseItemUtils.toXmlString(retrievedPrivacyAgreement.getAgreement().getRequestedItems()));
			//			assertTrue("Privacy agreement response items should not be empty", retrievedPrivacyAgreement.getAgreement().getRequestedItems().size() > 0);
			//			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItem : retrievedPrivacyAgreement.getAgreement().getRequestedItems()) {
			//				assertTrue("Element rejected: "+ResourceUtils.toString(responseItem.getRequestItem().getResource()), DecisionUtils.equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision.PERMIT, responseItem.getDecision()));
			//			}
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
	}

	@Test
	public void testStartNegotiationCisRefused() {
		String testTitle = new String("Start Negotiation CIS: refused");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);

		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			// -- Mock Userfeedback
			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy responsePolicy = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy();
			responsePolicy.setRequestor(RequestorUtils.toRequestorBean(requestorCis));
			responsePolicy.setNegotiationStatus(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus.FAILED);
			List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem>();
			responsePolicy.setResponseItems(responseItems);
			UserFeedbackMockResult mockResult = new UserFeedbackMockResult(1, responsePolicy);
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.PRIVACY_NEGOTIATION, mockResult);

			// -- Launch negotiation
			int negotiationId = new Random().nextInt();
			TestCase.privacyPolicyNegotiationManager.negotiateCISPolicy(new NegotiationDetails(requestorCis, negotiationId));

			// -- Test
			// Negotiation Result
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				// Do stuff
				fail("Timeout");
			}
			assertFalse("Negotiation should have failled", negotiationResult);

			// Check Agreement
			retrievedPrivacyAgreement = TestCase.privacyAgreementManager.getAgreement(requestorCis);
			assertNull("Privacy agreement should be null", retrievedPrivacyAgreement);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
	}

	@Test
	@Ignore
	public void testStartNegotiationCisFaillure() {
		String testTitle = new String("Start Negotiation CIS: faillure");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);

		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			// -- Mock Userfeedback
			//			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy responsePolicy = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy();
			//			responsePolicy.setRequestor(RequestorUtils.toRequestorBean(requestorCis));
			//			responsePolicy.setNegotiationStatus(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus.SUCCESSFUL);
			//			List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> responseItems = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem>();
			//			responsePolicy.setResponseItems(responseItems);
			UserFeedbackMockResult mockResult = new UserFeedbackMockResult(1);
			mockResult.addResultIndexes(1);
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.PRIVACY_NEGOTIATION, mockResult);

			// -- Launch negotiation
			int negotiationId = new Random().nextInt();
			TestCase.privacyPolicyNegotiationManager.negotiateCISPolicy(new NegotiationDetails(requestorCis, negotiationId));

			// -- Test
			// Negotiation Result
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				// Do stuff
				fail("Timeout");
			}
			assertFalse("Negotiation should have failled", negotiationResult);

			// Check Agreement
			retrievedPrivacyAgreement = TestCase.privacyAgreementManager.getAgreement(requestorCis);
			assertNull("Privacy agreement should be null", retrievedPrivacyAgreement);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		}
		catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
	}

	@Test
	@Ignore
	public void testStartNegotiationService() {
		String testTitle = new String("Start Negotiation Service: nominal");
		LOG.info("[#"+testCaseNumber+"][Test] "+testTitle);

		AgreementEnvelope retrievedPrivacyAgreement = null;
		try {
			// -- Launch negotiation
			int negotiationId = new Random().nextInt();
			TestCase.privacyPolicyNegotiationManager.negotiateServicePolicy(new NegotiationDetails(requestorService, negotiationId));

			// -- Test
			LOG.info("[#"+testCaseNumber+"] Waiting for "+TestCase.getTimeout()+"ms");
			// Negotiation Result
			boolean releaseBeforeTimeout = lock.await(TestCase.getTimeout(), TimeUnit.MILLISECONDS);
			// Check timeout
			if (!releaseBeforeTimeout) {
				// Do stuff
				fail("Timeout");
			}
			assertTrue("Negotiation should have succeed", negotiationResult);

			// Check Agreement
			retrievedPrivacyAgreement = TestCase.privacyAgreementManager.getAgreement(requestorService);
			assertNotNull("Privacy agreement should not be null", retrievedPrivacyAgreement);
			assertNotNull("Privacy agreement (agreement) should not be null", retrievedPrivacyAgreement.getAgreement());
			assertNotNull("Privacy agreement response items should not be null", retrievedPrivacyAgreement.getAgreement().getRequestedItems());
			LOG.debug("[#"+testCaseNumber+"] Agreement: "+ResponseItemUtils.toXmlString(retrievedPrivacyAgreement.getAgreement().getRequestedItems()));
			//						assertTrue("Privacy agreement response items should not be empty", retrievedPrivacyAgreement.getAgreement().getRequestedItems().size() > 0);
			//						for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem responseItem : retrievedPrivacyAgreement.getAgreement().getRequestedItems()) {
			//							assertTrue("Element rejected: "+ResourceUtils.toString(responseItem.getRequestItem().getResource()), DecisionUtils.equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision.PERMIT, responseItem.getDecision()));
			//						}
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [Error PrivacyException] "+testTitle, e);
			fail("Error PrivacyException: "+e.getMessage()+" - "+testTitle);
		} catch (Exception e) {
			LOG.error("[#"+testCaseNumber+"] [Error Exception] "+testTitle, e);
			fail("Error Exception: "+e.getMessage()+" - "+testTitle);
		}
	}



	/* **************
	 *    Tools     *
	 * **************/

	private RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException {
		IIdentity requestorId = TestCase.commManager.getIdManager().fromJid("red.societies.local.macs.hw.ac.uk");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
		serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
		return new RequestorService(requestorId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException {
		IIdentity requestorId = TestCase.commManager.getIdManager().getThisNetworkNode();
		int randomInt = new Random().nextInt();
		IIdentity cisId =TestCase.commManager.getIdManager().fromJid("cis-"+randomInt+".societies.local.macs.hw.ac.uk");
		return new RequestorCis(requestorId, cisId);
	}

	private RequestPolicy getServicePolicy(){
		/*
		 * location requestItem
		 */
		Resource rLocation = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "1"));
		RequestItem itemLocation = new RequestItem(rLocation, actions, conditions);


		/*
		 * status requestItem
		 */

		Resource rStatus = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.STATUS);
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.READ));
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions1.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditions1.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		conditions1.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "1"));
		RequestItem itemStatus = new RequestItem(rStatus, actions1, conditions1, true);

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
		Resource rLocation = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "1"));
		RequestItem itemLocation = new RequestItem(rLocation, actions, conditions, true);


		/*
		 * status requestItem
		 */
		Resource rStatus = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.STATUS);
		List<Action> actions1 = new ArrayList<Action>();
		actions1.add(new Action(ActionConstants.READ));
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions1.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditions1.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
		conditions1.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "1"));
		RequestItem itemStatus = new RequestItem(rStatus, actions1, conditions1, true);

		/*
		 * birthday requestItem
		 */
		Resource rBirthday = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.BIRTHDAY);
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.WRITE));
		actions2.add(new Action(ActionConstants.CREATE));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions2.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditions2.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "0"));
		conditions2.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "1"));
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
		userId = TestCase.commManager.getIdManager().getThisNetworkNode();
		try {
			userCtxEntity   = TestCase.ctxBroker.retrieveIndividualEntity(userId).get();
			List<CtxIdentifier> lookupSymLocAttributes = TestCase.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if (lookupSymLocAttributes.size()==0){
				this.locationAttribute = TestCase.ctxBroker.createAttribute(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			}else{
				this.locationAttribute = (CtxAttribute) TestCase.ctxBroker.retrieve(lookupSymLocAttributes.get(0)).get();
			}
			this.locationAttribute.setStringValue("home");
			this.ctxLocationAttributeId = this.locationAttribute.getId();
			TestCase.ctxBroker.update(locationAttribute);

			List<CtxIdentifier> list = TestCase.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS).get();
			if (list.size()==0){
				this.statusAttribute = TestCase.ctxBroker.createAttribute(userCtxEntity.getId(), CtxAttributeTypes.STATUS).get();
			}else{
				this.statusAttribute = (CtxAttribute) TestCase.ctxBroker.retrieve(list.get(0)).get();
			}

			this.statusAttribute.setStringValue("busy");
			this.ctxStatusAttributeId = this.statusAttribute.getId();
			TestCase.ctxBroker.update(statusAttribute);


		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void deleteContext() {
	}
}
