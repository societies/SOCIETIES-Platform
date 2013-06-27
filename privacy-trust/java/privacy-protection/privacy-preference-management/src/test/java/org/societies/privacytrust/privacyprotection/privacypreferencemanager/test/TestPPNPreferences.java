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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationClient;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
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
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.AccessControlPreferenceCreator;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestPPNPreferences {

	private static final String ppn_Preference_Name1 = "ppnp_preference_1";
	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IPrivacyDataManagerInternal privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private IPrivacyAgreementManager agreementMgr = Mockito.mock(IPrivacyAgreementManager.class);
	private IEventMgr eventMgr = Mockito.mock(IEventMgr.class);
	private PrivacyPreferenceManager privPrefMgr;
	private PPNPreferenceDetailsBean ppNetails;
	private RequestorCisBean requestorCisBean;
	private RequestPolicy requestPolicy;
	private PPNPrivacyPreferenceTreeModel ppnModel;
	private ArrayList<Action> actions;
	private ArrayList<Condition> conditions;
	private Resource resourceWithoutID;
	private INetworkNode userId;
	private CtxEntity privacyPreferenceEntity;
	private CtxAttribute ppn_1_CtxAttribute;
	private CtxEntity userCtxEntity;
	private CtxAttribute registryCtxAttribute;
	private Requestor requestorCis;

	@Before
	public void setUp(){
		this.privPrefMgr  = new PrivacyPreferenceManager();
		this.privPrefMgr.setCommsMgr(commsMgr);
		this.privPrefMgr.setIdMgr(idMgr);
		this.privPrefMgr.setCtxBroker(ctxBroker);
		this.privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		this.privPrefMgr.setTrustBroker(trustBroker);
		this.privPrefMgr.setUserFeedback(userFeedback);
		this.privPrefMgr.setAgreementMgr(agreementMgr);
		this.privPrefMgr.setEventMgr(eventMgr);
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		
		this.setupRequestor();
		this.setupPolicyDetails();
		this.setupDetails();
		this.setupPolicy();
		this.setupPPNModel();
		this.setupContext();
		this.mockAllIntegration();
		this.privPrefMgr.initialisePrivacyPreferenceManager();
	}
	
	private void mockAllIntegration() {
		Mockito.when(commsMgr.getIdManager()).thenReturn(idMgr);
		Mockito.when(idMgr.getThisNetworkNode()).thenReturn((INetworkNode) userId);
		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			List<CtxIdentifier> preferenceEntityList = new ArrayList<CtxIdentifier>();
			preferenceEntityList.add(this.privacyPreferenceEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(preferenceEntityList));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) this.privacyPreferenceEntity.getId(), ppn_Preference_Name1)).thenReturn(new AsyncResult<CtxAttribute>(this.ppn_1_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(ppn_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(this.ppn_1_CtxAttribute));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(registryCtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.retrieve(this.ppn_1_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.ppn_1_CtxAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Test
	public void testAllMethods(){
		boolean storePPNPreference = this.privPrefMgr.storePPNPreference(ppNetails, ppnModel);

		Assert.assertTrue(storePPNPreference);
		
		List<PPNPreferenceDetailsBean> ppnPreferenceDetails = this.privPrefMgr.getPPNPreferenceDetails();
		Assert.assertNotNull(ppnPreferenceDetails);
		Assert.assertEquals(1, ppnPreferenceDetails.size());
		Assert.assertEquals(ppNetails, ppnPreferenceDetails.get(0));
		
		PPNPrivacyPreferenceTreeModel getPPNPreferenceModel = this.privPrefMgr.getPPNPreference(ppNetails);
		
		Assert.assertNotNull(getPPNPreferenceModel);
		Assert.assertNotNull(getPPNPreferenceModel.getRootPreference());
		Assert.assertSame(getPPNPreferenceModel, ppnModel);
		
		Assert.assertNotNull(requestPolicy);
		HashMap<RequestItem, ResponseItem> evaluatePPNPreferences = this.privPrefMgr.evaluatePPNPreferences(requestPolicy);
		Assert.assertNotNull(evaluatePPNPreferences);
		Assert.assertTrue(evaluatePPNPreferences.size()>0);
		
		boolean deletePPNPreference = this.privPrefMgr.deletePPNPreference(ppNetails);
		Assert.assertTrue(deletePPNPreference);
		ppnPreferenceDetails = this.privPrefMgr.getPPNPreferenceDetails();
		Assert.assertNotNull(ppnPreferenceDetails);
		Assert.assertEquals(0, ppnPreferenceDetails.size());
		
		Assert.assertNull(this.privPrefMgr.getPPNPreference(ppNetails));
		

		
	}
	

	private void setupContext(){
		
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		
		
		CtxEntityIdentifier preferenceEntityId_ = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));

		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_);
		
		this.ppn_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ppn_Preference_Name1, new Long(5)));
		
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
	}
	private void setupDetails() {

		this.ppNetails = new PPNPreferenceDetailsBean();
		this.ppNetails.setRequestor(requestorCisBean);
		this.ppNetails.setResource(resourceWithoutID);
	}
	
	private void setupRequestor() {
		this.requestorCisBean = new RequestorCisBean();
		IIdentity requestorIdentity = new MyIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		
		this.requestorCisBean.setRequestorId(requestorIdentity.getJid());
		IIdentity cisIdentity = new MyIdentity(IdentityType.CIS, "myCis","ict-societies.eu");
		this.requestorCisBean.setCisRequestorId(cisIdentity.getJid());
		this.requestorCis = new RequestorCis(requestorIdentity, cisIdentity);
	}
	
	private void setupPPNModel() {
		PPNPOutcome outcome = new PPNPOutcome(Decision.PERMIT);
		
		IPrivacyPreference preference = new PrivacyPreference(outcome);
		
		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			conditionPreference.add(preference);
			preference = conditionPreference;
		}
		
		
		this.ppnModel = new PPNPrivacyPreferenceTreeModel(this.ppNetails, preference.getRoot());
		
	}
	private void setupPolicy() {
		
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
		
		return requestItems;
	}
	
}
