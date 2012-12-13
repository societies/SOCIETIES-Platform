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
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationStatus;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.remote.INegotiationAgentRemote;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentityOption;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.api.identity.ILinkabilityDetail;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
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
	private IAgreement agreement;
	private PPNPOutcome locationOutcomeForService;
	private PPNPOutcome statusOutcomeForService;
	private ResponsePolicy cisResponsePolicy;
	private PPNPOutcome locationOutcomeForCis;
	private PPNPOutcome statusOutcomeForCis;
	private PPNegotiationEvent successfulEvent;
	private FailedNegotiationEvent failedEvent;
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);

	@Before
	public void setUp(){
		setupContext();
		requestorService = this.getRequestorService();
		requestorCis = this.getRequestorCis();
		this.servicePolicy = this.getServicePolicy();
		this.cisPolicy = this.getCisPolicy();
		this.serviceResponsePolicy = this.getServiceResponsePolicy();
		this.cisResponsePolicy = this.getCisResponsePolicy();
		this.createNegotiationAgreement();
		this.setupPPNPOutcomes();
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
	
	



	private void setupPPNPOutcomes() {
		List<Requestor> subjectsService = new ArrayList<Requestor>();
		subjectsService.add(requestorService);		
		List<Requestor> subjectsCis = new ArrayList<Requestor>();
		subjectsCis.add(requestorCis);	
		Resource rLocation = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "48"));
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		
		RuleTarget targetLocationForService = new RuleTarget(subjectsService, rLocation, actions);
		
		
		Resource rStatus = new Resource(DataIdentifierScheme.CONTEXT,CtxAttributeTypes.STATUS);
		
		RuleTarget targetStatusForService = new RuleTarget(subjectsService, rStatus, actions);
		
		RuleTarget targetLocationForCis = new RuleTarget(subjectsCis, rLocation, actions);
		
		RuleTarget targetStatusForCis = new RuleTarget(subjectsCis, rStatus, actions);
		try {
			locationOutcomeForService = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, targetLocationForService, conditions);
			
			locationOutcomeForCis = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, targetLocationForCis, conditions);
			statusOutcomeForService = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, targetStatusForService, conditions);
			statusOutcomeForCis = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, targetStatusForCis, conditions);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void createNegotiationAgreement() {
		AgreementFinaliser finaliser = new AgreementFinaliser();
		agreement = new NegotiationAgreement(serviceResponsePolicy);
		byte[] signature = finaliser.signAgreement(agreement);
		Key publicKey = finaliser.getPublicKey();
		try {
			this.negotiationAgreementEnvelope = new AgreementEnvelope(agreement, SerialisationHelper.serialise(publicKey), signature);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ResponsePolicy getCisResponsePolicy() {
		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.cisPolicy.getRequests()){
			ResponseItem responseItem = new ResponseItem(requestItem, Decision.PERMIT);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy(this.requestorCis, responses, NegotiationStatus.SUCCESSFUL);
		return response;
	}
	private ResponsePolicy getServiceResponsePolicy() {
		
		List<ResponseItem> responses = new ArrayList<ResponseItem>();
		for (RequestItem requestItem : this.servicePolicy.getRequests()){
			ResponseItem responseItem = new ResponseItem(requestItem, Decision.PERMIT);
			responses.add(responseItem);
		}
		ResponsePolicy response = new ResponsePolicy(this.requestorService, responses, NegotiationStatus.SUCCESSFUL);
		return response;
	}


	private void setupMockito(){
		Mockito.when(negAgent.getPolicy(requestorService)).thenReturn(new AsyncResult<RequestPolicy>(servicePolicy));
		Mockito.when(negAgent.getPolicy(requestorCis)).thenReturn(new AsyncResult<RequestPolicy>(cisPolicy));
		

		try {
			Mockito.when(idm.getThisNetworkNode()).thenReturn(this.userId);
			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(userCtxEntity));
			Mockito.when(ctxBroker.retrieveIndividualEntity(userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(userCtxEntity));
			List<IPrivacyOutcome> locationOutcomes = new ArrayList<IPrivacyOutcome>();
			locationOutcomes.add(locationOutcomeForService);
			locationOutcomes.add(locationOutcomeForCis);
			List<IPrivacyOutcome> statusOutcomes = new ArrayList<IPrivacyOutcome>();
			statusOutcomes.add(statusOutcomeForService);
			statusOutcomes.add(statusOutcomeForCis);
			Mockito.when(privacyPreferenceManager.evaluatePPNPreference(CtxAttributeTypes.LOCATION_SYMBOLIC)).thenReturn(locationOutcomes);
			Mockito.when(privacyPreferenceManager.evaluatePPNPreference(CtxAttributeTypes.STATUS)).thenReturn(statusOutcomes);
			Mockito.when(negAgent.negotiate(Mockito.eq(requestorService), (ResponsePolicy) Mockito.anyObject())).thenReturn(new AsyncResult<ResponsePolicy>(serviceResponsePolicy));
			Mockito.when(negAgent.negotiate(Mockito.eq(requestorCis), (ResponsePolicy) Mockito.anyObject())).thenReturn(new AsyncResult<ResponsePolicy>(cisResponsePolicy));
			Mockito.when(negAgent.acknowledgeAgreement((IAgreementEnvelope) Mockito.anyObject())).thenReturn(new AsyncResult<Boolean>(true));
			Mockito.when(ids.processIdentityContext((IAgreement) Mockito.anyObject())).thenReturn(this.getIdOptions());
			
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

		this.negotiationMgr.negotiateServicePolicy(requestorService);
		this.negotiationMgr.negotiateCISPolicy(requestorCis);
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
	
	
	private RequestorService getRequestorService(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "eliza","societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new RequestorService(requestorId, serviceId);
	}
	
	private RequestorCis getRequestorCis(){
		IIdentity requestorId = new MyIdentity(IdentityType.CSS, "me","domain.com");
		IIdentity cisId = new MyIdentity(IdentityType.CIS, "Holidays", "domain.com");
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
		conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "YES"));
		conditions.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "YES"));
		conditions.add(new Condition(ConditionConstants.RIGHT_TO_OPTOUT, "YES"));
		RequestItem itemLocation = new RequestItem(rLocation, actions, conditions);
		
		
		/*
		 * status requestItem
		 */
		
		Resource rStatus = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.STATUS);
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
	
	
	private RequestPolicy getCisPolicy(){
		/*
		 * location requestItem
		 */
		Resource rLocation = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
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
		
		Resource rStatus = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.STATUS);
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
		RequestPolicy policy = new RequestPolicy(this.requestorCis, requests);
		
		
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
