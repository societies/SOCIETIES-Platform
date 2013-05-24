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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationClient;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.accessCtrl.AccCtrlMonitor;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestAccCtrlMonitorEmpty {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IPrivacyDataManagerInternal privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private IPrivacyAgreementManager agreementMgr = Mockito.mock(IPrivacyAgreementManager.class);
	private IEventMgr eventManager = Mockito.mock(IEventMgr.class);
	private PrivacyPreferenceManager privPrefMgr;
	private ArrayList<Action> actions;
	private ArrayList<Condition> conditions;
	private Resource resourceWithoutID;
	private AccCtrlMonitor accCtrlMonitor;
	private RequestorCis requestorCis;
	private AgreementEnvelope envelope;
	private RequestorCisBean requestorCisBean;
	private RequestPolicy requestPolicy;
	private ArrayList<ResponseItem> responseItems;
	private MyIdentity userId;
	private CtxEntity userCtxEntity;
	private CtxAttribute locationAttribute;
	
	@Before
	public void setup(){
		this.privPrefMgr  = new PrivacyPreferenceManager();
		this.privPrefMgr.setCommsMgr(commsMgr);
		this.privPrefMgr.setIdMgr(idMgr);
		this.privPrefMgr.setCtxBroker(ctxBroker);
		this.privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		this.privPrefMgr.setTrustBroker(trustBroker);
		this.privPrefMgr.setUserFeedback(userFeedback);
		this.privPrefMgr.setAgreementMgr(agreementMgr);
		this.privPrefMgr.setEventMgr(eventManager);
		this.setupRequestor();
		this.setupPolicyDetails();
		this.setupPolicy();
		this.setupEnvelope();
		this.setupContext();
		this.mockCalls();
		privPrefMgr.initialisePrivacyPreferenceManager();
		accCtrlMonitor = privPrefMgr.getAccCtrlMonitor();

	}
	
	

	


	private void mockCalls() {
		try {
			/**
			 * identity
			 */
			Mockito.when(this.commsMgr.getIdManager()).thenReturn(this.idMgr);
			Mockito.when(this.privPrefMgr.getCommsMgr().getIdManager()).thenReturn(this.idMgr);
			Mockito.when(this.idMgr.fromJid(this.requestorCisBean.getRequestorId())).thenReturn(this.requestorCis.getRequestorId());
			Mockito.when(this.idMgr.fromJid(this.requestorCisBean.getCisRequestorId())).thenReturn(this.requestorCis.getCisRequestorId());
			
			/**
			 * context
			 */
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}






	@Test
	public void testEmpty(){
		
		try {
			accCtrlMonitor.handleInternalEvent(this.createSuccessfulNegotiationEvent(this.envelope));
			CtxChangeEvent event = new CtxChangeEvent(this.locationAttribute.getId());
			accCtrlMonitor.onModification(event);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Error sending internal event");
		}
	}
	
	private InternalEvent createSuccessfulNegotiationEvent(
			AgreementEnvelope envelope) throws InvalidFormatException {
		NegotiationDetails details = new NegotiationDetails(requestorCis, 0);
		PPNegotiationEvent event = new PPNegotiationEvent(
				AgreementUtils.toAgreement(envelope.getAgreement(), this.idMgr),
				NegotiationStatus.SUCCESSFUL, details);
		InternalEvent iEvent = new InternalEvent(
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT, "",
				INegotiationClient.class.getName(), event);
		this.logging.debug("Successfull negotiation Event: \n" + "EventName: "
				+ iEvent.geteventName() + "\nEventSource:"
				+ iEvent.geteventSource() + "\nEventType"
				+ iEvent.geteventType() + "\nEventInfo:"
				+ iEvent.geteventInfo());
		return iEvent;
	}
	
	private void setupRequestor() {
		this.requestorCisBean = new RequestorCisBean();
		IIdentity requestorIdentity = new MyIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		
		this.requestorCisBean.setRequestorId(requestorIdentity.getJid());
		IIdentity cisIdentity = new MyIdentity(IdentityType.CIS, "myCis","ict-societies.eu");
		this.requestorCisBean.setCisRequestorId(cisIdentity.getJid());
		
		requestorCis = new RequestorCis(requestorIdentity, cisIdentity);
	}
	
	private void setupPolicyDetails() {
		this.actions = new ArrayList<Action>();
		Action read = new Action();
		read.setActionConstant(ActionConstants.READ);
		Action write = new Action();
		write.setActionConstant(ActionConstants.WRITE);
		Action create = new Action();
		create.setActionConstant(ActionConstants.CREATE);
		Action delete = new Action();
		delete.setActionConstant(ActionConstants.DELETE);
		
		this.actions.add(read);
		this.actions.add(create);
		this.actions.add(write);
		this.actions.add(delete);
		
		
		this.conditions = new ArrayList<Condition>();
		
		Condition shareWithCisAdmin = new Condition();
		shareWithCisAdmin.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY);
		shareWithCisAdmin.setValue("Yes");
		shareWithCisAdmin.setOptional(false);
		
		Condition shareWithCisMembers = new Condition();
		shareWithCisMembers.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY);
		shareWithCisMembers.setValue("Yes");
		shareWithCisMembers.setOptional(true);
		
		Condition dataRetentionHours = new Condition();
		dataRetentionHours.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		dataRetentionHours.setValue("24");
		dataRetentionHours.setOptional(false);
		
		conditions.add(dataRetentionHours);
		conditions.add(shareWithCisMembers);
		conditions.add(shareWithCisAdmin);
		
		this.resourceWithoutID = new Resource();
		this.resourceWithoutID.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.resourceWithoutID.setScheme(DataIdentifierScheme.CONTEXT);
		
	}
	
	private void setupPolicy() {
		this.setupPolicyDetails();
		this.requestPolicy = new RequestPolicy();
		
		this.requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.CIS);
		
		List<String> dataTypes = new ArrayList<String>();
		dataTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		dataTypes.add(CtxAttributeTypes.NAME);
		dataTypes.add(CtxAttributeTypes.AGE);
		dataTypes.add(CtxAttributeTypes.STATUS);
		dataTypes.add("activityfeed");
		List<RequestItem> requestItems = this.createRequestItems(dataTypes);
		this.requestPolicy.setRequestItems(requestItems);
		this.requestPolicy.setRequestor(requestorCisBean);
		
	}
	
	private List<RequestItem> createRequestItems(List<String> dataTypes){
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for (String type : dataTypes){
			RequestItem item = new RequestItem();
			item.setActions(actions);
			item.setConditions(conditions);
			item.setOptional(false);
			Resource resource = new Resource();
			resource.setDataType(type);
			if (type.equalsIgnoreCase("activityfeed")){
				resource.setScheme(DataIdentifierScheme.CIS);
			}else{
				resource.setScheme(DataIdentifierScheme.CONTEXT);
			}
			item.setResource(resource);
			requestItems.add(item);
		}
		this.responseItems = new ArrayList<ResponseItem>();
		
		for (int i=0; i<requestItems.size(); i++){
			RequestItem item = requestItems.get(i);
			ResponseItem respItem = new ResponseItem();
			respItem.setRequestItem(item);
			
			if (i%2==0){
				respItem.setDecision(Decision.DENY);
			}else{
				respItem.setDecision(Decision.PERMIT);
			}
			this.responseItems.add(respItem);			
		}

		return requestItems;
	}
	
	private void setupEnvelope() {
		this.envelope = new AgreementEnvelope();
		Agreement agreement = new Agreement();
		
		agreement.setRequestedItems(this.responseItems);
		agreement.setRequestor(requestorCisBean);
		this.envelope.setAgreement(agreement);
		
		
	}
	
	private void setupContext() {
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		CtxAttributeIdentifier id = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		this.locationAttribute = new CtxAttribute(id);
		this.locationAttribute.setStringValue("home");
		this.locationAttribute.setValueType(CtxAttributeValueType.STRING);
		this.locationAttribute.getQuality().setOriginType(CtxOriginType.SENSED);
	}
}
