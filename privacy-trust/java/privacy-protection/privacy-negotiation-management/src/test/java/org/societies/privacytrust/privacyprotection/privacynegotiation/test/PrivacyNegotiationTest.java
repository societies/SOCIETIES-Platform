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
package org.societies.privacytrust.privacyprotection.privacynegotiation.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.privacytrust.privacyprotection.remote.INegotiationAgentRemote;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentityOption;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.api.identity.ILinkabilityDetail;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;
import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client.AgreementFinaliser;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PrivacyNegotiationTest {

	private IUserPreferenceManagement prefMgr = Mockito.mock(IUserPreferenceManagement.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IEventMgr eventMgr = Mockito.mock(IEventMgr.class);
	private IIdentityManager idm = Mockito.mock(IIdentityManager.class);
	private IPrivacyPreferenceManager privacyPreferenceManager = Mockito.mock(IPrivacyPreferenceManager.class);
	private PrivacyPolicyNegotiationManager negotiationMgr;
	private IPrivacyDataManagerInternal privacyDataManager = Mockito.mock(IPrivacyDataManagerInternal.class);
	private IPrivacyAgreementManagerInternal policyAgreementMgr = Mockito.mock(IPrivacyAgreementManagerInternal.class);
	private INegotiationAgentRemote negAgent = Mockito.mock(INegotiationAgentRemote.class);
	private IIdentitySelection ids = Mockito.mock(IIdentitySelection.class);
	private IPrivacyPolicyManager privacyPolicyManager = Mockito.mock(IPrivacyPolicyManager.class);
	private RequestorServiceBean requestorServiceBean;
	private RequestorCisBean requestorCisBean;
	private RequestorService requestorService;
	private RequestorCis requestorCis;
	private RequestPolicy servicePolicy;
	private RequestPolicy cisPolicy;
	private INetworkNode userId;
	private IndividualCtxEntity userCtxEntity;
	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxAttribute locationAttribute;
	private CtxAttributeIdentifier ctxStatusAttributeId;
	private CtxAttribute statusAttribute;
	private ResponsePolicy serviceResponsePolicy;
	private AgreementEnvelope negotiationAgreementEnvelope;
	private Agreement agreement;
	private PPNPOutcome locationOutcomeForService;
	private PPNPOutcome statusOutcomeForService;
	private ResponsePolicy cisResponsePolicy;
	private PPNPOutcome locationOutcomeForCis;
	private PPNPOutcome statusOutcomeForCis;
	private PPNegotiationEvent successfulEvent;
	private FailedNegotiationEvent failedEvent;
	private List<Action> actions;
	private List<Condition> conditions;
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);

	@Before
	public void setUp() throws CtxException{
		setupContext();
		this.setupRequestorServiceBean();
		this.setupRequestorCisBean();
		this.setupRequestorCisObject();
		this.setupRequestorServiceObject();

		this.setupPolicyDetails();

		this.servicePolicy = this.getServicePolicy();
		this.cisPolicy = this.getCisPolicy();
		this.serviceResponsePolicy = this.getServiceResponsePolicy();
		this.cisResponsePolicy = this.getCisResponsePolicy();

		this.createNegotiationAgreement();

		this.negotiationMgr = new PrivacyPolicyNegotiationManager();
		this.negotiationMgr.setCtxBroker(ctxBroker);
		this.negotiationMgr.setEventMgr(eventMgr);
		this.negotiationMgr.setIdentitySelection(Mockito.mock(IIdentitySelection.class));
		this.negotiationMgr.setIdm(idm);
		this.negotiationMgr.setPrefMgr(prefMgr);
		this.negotiationMgr.setPrivacyDataManagerInternal(privacyDataManager);
		this.negotiationMgr.setPrivacyAgreementManagerInternal(policyAgreementMgr );
		this.negotiationMgr.setNegotiationAgentRemote(negAgent);
		this.negotiationMgr.setPrivacyPreferenceManager(privacyPreferenceManager);
		this.negotiationMgr.setPrivacyPolicyManager(privacyPolicyManager);
		this.negotiationMgr.setUserFeedback(userFeedback);
		this.setupMockito();

		this.negotiationMgr.initialisePrivacyPolicyNegotiationManager();
	}


	private void setupMockito() throws CtxException{
		
		
		Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(this.userCtxEntity));
		Mockito.when(negAgent.getPolicy((RequestorBean) Mockito.anyObject())).thenReturn(new AsyncResult<RequestPolicy>(servicePolicy));
		//Mockito.when(negAgent.getPolicy(Mockito.eq(requestorCisBean))).thenReturn(new AsyncResult<RequestPolicy>(cisPolicy));
		
		Mockito.when(userFeedback.getPrivacyNegotiationFB((ResponsePolicy)Mockito.anyObject(), (NegotiationDetailsBean) Mockito.anyObject())).thenReturn(new AsyncResult<ResponsePolicy>(serviceResponsePolicy));


		Mockito.when(idm.getThisNetworkNode()).thenReturn(this.userId);
		Mockito.when(negAgent.negotiate((RequestorBean) Mockito.anyObject(), (ResponsePolicy) Mockito.anyObject())).thenReturn(new AsyncResult<ResponsePolicy>(serviceResponsePolicy));
		//Mockito.when(negAgent.negotiate(Mockito.eq(requestorCisBean), (ResponsePolicy) Mockito.anyObject())).thenReturn(new AsyncResult<ResponsePolicy>(cisResponsePolicy));
		Mockito.when(negAgent.acknowledgeAgreement((AgreementEnvelope) Mockito.anyObject())).thenReturn(new AsyncResult<Boolean>(true));
		Mockito.when(ids.processIdentityContext((IAgreement) Mockito.anyObject())).thenReturn(this.getIdOptions());


	}



	private void setupPolicyDetails() {
		this.setupActions();
		this.setupConditions();

	}


	private void setupActions() {
		this.actions= new ArrayList<Action>();
		Action actionRead = new Action();
		actionRead.setActionConstant(ActionConstants.READ);
		Action actionWrite = new Action();
		actionWrite.setActionConstant(ActionConstants.WRITE);
		Action actionCreate = new Action();
		actionCreate.setActionConstant(ActionConstants.CREATE);
		Action actionDelete = new Action();
		actionDelete.setActionConstant(ActionConstants.DELETE);
	}





	private void setupConditions() {

		Condition conditionDataRetention = new Condition();
		conditionDataRetention.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		conditionDataRetention.setValue("48");

		Condition conditionShare3p  = new Condition();
		conditionShare3p.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		conditionShare3p.setValue("Yes");

		Condition conditionRightToOptOut = new Condition();
		conditionRightToOptOut.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
		conditionRightToOptOut.setValue("Yes");


		Condition conditionStoreSecure = new Condition();
		conditionStoreSecure.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
		conditionStoreSecure.setValue("Yes");

		this.conditions  = new ArrayList<Condition>();
		this.conditions.add(conditionStoreSecure);
		this.conditions.add(conditionShare3p);
		this.conditions.add(conditionDataRetention);
		this.conditions.add(conditionRightToOptOut);
	}





	private void createNegotiationAgreement() {
		AgreementFinaliser finaliser = new AgreementFinaliser();
		agreement = new Agreement();
		agreement.setRequestedItems(serviceResponsePolicy.getResponseItems());
		agreement.setRequestor(requestorServiceBean);
		byte[] signature = finaliser.signAgreement(agreement);
		Key publicKey = finaliser.getPublicKey();
		try {
			this.negotiationAgreementEnvelope = new AgreementEnvelope();
			this.negotiationAgreementEnvelope.setAgreement(agreement);
			this.negotiationAgreementEnvelope.setPublicKey(SerialisationHelper.serialise(publicKey));
			this.negotiationAgreementEnvelope.setSignature(signature);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ResponsePolicy getCisResponsePolicy() {
		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.cisPolicy.getRequestItems()){
			ResponseItem responseItem = new ResponseItem();
			responseItem.setDecision(Decision.PERMIT);
			responseItem.setRequestItem(requestItem);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy();
		response.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);
		response.setRequestor(this.requestorCisBean);
		response.setResponseItems(responses);
		return response;
	}
	private ResponsePolicy getServiceResponsePolicy() {

		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.servicePolicy.getRequestItems()){
			ResponseItem responseItem = new ResponseItem();
			responseItem.setDecision(Decision.PERMIT);
			responseItem.setRequestItem(requestItem);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy();
		response.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);
		response.setRequestor(this.requestorServiceBean);
		response.setResponseItems(responses);
		return response;
	}



	public List<IIdentityOption> getIdOptions(){
		ArrayList<IIdentityOption> idOptions = new ArrayList<IIdentityOption>();

		IIdentityOption option = new IIdentityOption() {

			@Override
			public IIdentity getReferenceIdentity() {
				// TODO Auto-generated method stub
				return userId;
			}

			@Override
			public List<ILinkabilityDetail> getOrderedLinkabilityDetailList() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<IIdentity, ILinkabilityDetail> getLinkabilityDetailMap() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getIdentityContextMatch() {
				// TODO Auto-generated method stub
				return 0;
			}
		};

		idOptions.add(option);
		return idOptions;
	}
	@Test
	public void TestStartNegotiation(){

		NegotiationDetails serviceDetails = new NegotiationDetails(this.requestorService, 0);
		NegotiationDetails cisDetails = new NegotiationDetails(requestorCis, 0);		
		try {
			this.negotiationMgr.negotiateServicePolicy(serviceDetails);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
		try {
			this.negotiationMgr.negotiateCISPolicy(cisDetails);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		}
	}


	/*	@Test
	public void TestGetProviderPolicy(){

	}

	@Test
	public void TestNegotiate(){

	}
	@Test
	public void TestAcknowledgeAgreement(){

	}*/


	private void setupRequestorServiceBean(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.requestorServiceBean = new RequestorServiceBean();
		requestorServiceBean.setRequestorId(requestorId.getJid());
		requestorServiceBean.setRequestorServiceId(serviceId);

	}

	private void setupRequestorServiceObject(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.requestorService = new RequestorService(requestorId, serviceId);

	}

	private void setupRequestorCisBean(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "me","domain.com");
		IIdentity cisId = new MyIdentity(IdentityType.CIS, "Holidays", "domain.com");
		this.requestorCisBean = new RequestorCisBean();
		requestorCisBean.setRequestorId(requestorId.getJid());
		requestorCisBean.setCisRequestorId(cisId.getJid());

	}

	private void setupRequestorCisObject(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "me","domain.com");
		IIdentity cisId = new MyIdentity(IdentityType.CIS, "Holidays", "domain.com");
		this.requestorCis = new RequestorCis(requestorId,cisId);


	}
	private RequestPolicy getServicePolicy(){

		RequestPolicy policy = this.getPolicyWithoutRequestor();
		policy.setRequestor(requestorServiceBean);

		return policy;

	}

	private RequestPolicy getCisPolicy(){
		RequestPolicy policy = this.getPolicyWithoutRequestor();
		policy.setRequestor(requestorCisBean);

		return policy;
	}
	private RequestPolicy getPolicyWithoutRequestor(){
		/*
		 * location requestItem
		 */
		Resource rLocation = new Resource();
		rLocation.setScheme(DataIdentifierScheme.CONTEXT);
		rLocation.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		RequestItem itemLocation = new RequestItem();
		itemLocation.setResource(rLocation);
		itemLocation.setActions(actions);
		itemLocation.setConditions(conditions);


		/*
		 * status requestItem
		 */

		Resource rStatus = new Resource();
		rStatus.setScheme(DataIdentifierScheme.CONTEXT);
		rStatus.setDataType(CtxAttributeTypes.STATUS);

		RequestItem itemStatus = new RequestItem();
		itemStatus.setResource(rStatus);
		itemStatus.setActions(actions);
		itemStatus.setConditions(conditions);

		/* ----------------------------------------------------*/


		List<RequestItem> requests = new ArrayList<RequestItem>();
		requests.add(itemLocation);
		requests.add(itemStatus);
		RequestPolicy policy = new RequestPolicy();
		policy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
		policy.setRequestItems(requests);
		return policy;
	}


	private void setupContext(){
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new IndividualCtxEntity(ctxId);


		ctxLocationAttributeId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		ctxLocationAttributeId.setScheme(DataIdentifierScheme.CONTEXT);
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");


		ctxStatusAttributeId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		ctxStatusAttributeId.setScheme(DataIdentifierScheme.CONTEXT);
		statusAttribute = new CtxAttribute(ctxStatusAttributeId);
		statusAttribute.setStringValue("busy");

		userCtxEntity.addAttribute(locationAttribute);
		userCtxEntity.addAttribute(statusAttribute);

	}
}
