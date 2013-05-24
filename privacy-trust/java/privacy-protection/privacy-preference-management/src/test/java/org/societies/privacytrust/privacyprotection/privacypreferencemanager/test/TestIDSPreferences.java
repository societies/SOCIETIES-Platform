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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.IEventMgr;
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
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestIDSPreferences {

	private static final String ids_Preference_Name1 = "ids_preference_1";
	PrivacyPreferenceManager privPrefMgr;
	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IPrivacyDataManagerInternal privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private IEventMgr eventMgr = Mockito.mock(IEventMgr.class);
	private ArrayList<Action> actions;
	private ArrayList<Condition> conditions;
	private IDSPrivacyPreferenceTreeModel idsModel;
	private IDSPreferenceDetailsBean idsDetails;
	private IIdentity userId;
	private ArrayList<IIdentity> identities;
	private RequestorCisBean requestorCisBean;
	private RequestPolicy requestPolicy;
	private NegotiationAgreement agreement;
	private CtxEntity userCtxEntity;
	private CtxEntity privacyPreferenceEntity;
	private CtxAttribute ids_1_CtxAttribute;
	private CtxAttribute registryCtxAttribute;

	@Before
	public void setUp(){
		this.privPrefMgr  = new PrivacyPreferenceManager();
		this.privPrefMgr.setCommsMgr(commsMgr);
		this.privPrefMgr.setIdMgr(idMgr);
		this.privPrefMgr.setCtxBroker(ctxBroker);
		this.privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		this.privPrefMgr.setTrustBroker(trustBroker);
		this.privPrefMgr.setUserFeedback(userFeedback);
		this.privPrefMgr.setEventMgr(eventMgr);
		
		
		this.setupRequestor();
		this.setupIdentities();
		this.setupDetails();
		this.setupPolicy();
		this.setupIDSModel();
		this.setupContext();
		this.setupAgreement();
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
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) this.privacyPreferenceEntity.getId(), ids_Preference_Name1)).thenReturn(new AsyncResult<CtxAttribute>(this.ids_1_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(ids_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(this.ids_1_CtxAttribute));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(registryCtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.retrieve(this.ids_1_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.ids_1_CtxAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testAllMethods(){
		boolean storeIDSPreference = this.privPrefMgr.storeIDSPreference(idsDetails, idsModel);

		Assert.assertTrue(storeIDSPreference);
		
		List<IDSPreferenceDetailsBean> idsPreferenceDetails = this.privPrefMgr.getIDSPreferenceDetails();
		Assert.assertNotNull(idsPreferenceDetails);
		Assert.assertEquals(1, idsPreferenceDetails.size());
		Assert.assertEquals(idsDetails, idsPreferenceDetails.get(0));
		
		IDSPrivacyPreferenceTreeModel getIDSPreferenceModel = this.privPrefMgr.getIDSPreference(idsDetails);
		
		Assert.assertNotNull(getIDSPreferenceModel);
		Assert.assertNotNull(getIDSPreferenceModel.getRootPreference());
		Assert.assertSame(idsModel, getIDSPreferenceModel);
		
		Assert.assertNotNull(requestPolicy);
		IIdentity selectedIdentity = this.privPrefMgr.evaluateIDSPreferences(agreement, identities);
		Assert.assertNotNull(selectedIdentity);
		
		boolean deleteIDSPreference = this.privPrefMgr.deleteIDSPreference(idsDetails);
		Assert.assertTrue(deleteIDSPreference);
		idsPreferenceDetails = this.privPrefMgr.getIDSPreferenceDetails();
		Assert.assertNotNull(idsPreferenceDetails);
		Assert.assertEquals(0, idsPreferenceDetails.size());
		
		Assert.assertNull(this.privPrefMgr.getIDSPreference(idsDetails));
	}
	private void setupRequestor() {
		this.requestorCisBean = new RequestorCisBean();
		IIdentity requestorIdentity = new MyIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		
		this.requestorCisBean.setRequestorId(requestorIdentity.getJid());
		IIdentity cisIdentity = new MyIdentity(IdentityType.CIS, "myCis","ict-societies.eu");
		this.requestorCisBean.setCisRequestorId(cisIdentity.getJid());
	}
	private void setupIDSModel() {
		IdentitySelectionPreferenceOutcome idsOutcome = new IdentitySelectionPreferenceOutcome(identities.get(1));
		idsOutcome.setShouldUseIdentity(true);
		IdentitySelectionPreferenceOutcome idsOutcome2 = new IdentitySelectionPreferenceOutcome(identities.get(1));
		idsOutcome2.setShouldUseIdentity(true);
		
		IPrivacyPreference idsOutcomePreference1 = new PrivacyPreference(idsOutcome);
		
		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			conditionPreference.add(idsOutcomePreference1);
			idsOutcomePreference1 = conditionPreference;
		}
		IPrivacyPreference idsOutcomePreference2 = new PrivacyPreference(idsOutcome2);

		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			if (cond.getConditionConstant().equals(ConditionConstants.DATA_RETENTION_IN_HOURS)){
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("12");
			}else{
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("No");
			}
			conditionPreference.add(idsOutcomePreference2);
			idsOutcomePreference2 = conditionPreference;
		}
		
		IPrivacyPreference idsPreference = new PrivacyPreference();
		idsPreference.add(idsOutcomePreference1);
		idsPreference.add(idsOutcomePreference2);
		this.idsModel = new IDSPrivacyPreferenceTreeModel(idsDetails, idsPreference);
		
	}
	
	private void setupDetails(){
		this.idsDetails = new IDSPreferenceDetailsBean();
		this.idsDetails.setAffectedIdentity(this.userId.getJid());
		this.idsDetails.setRequestor(requestorCisBean);
	}

	private void setupIdentities(){
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		this.identities = new ArrayList<IIdentity>();
		this.identities.add(this.userId);
		this.identities.add(new MyIdentity(IdentityType.CSS, "xcmanager1", "societies.local"));
		this.identities.add(new MyIdentity(IdentityType.CSS, "xcmanager2", "societies.local"));
		
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
		
	}
	private void setupAgreement() {
		
		this.agreement = new NegotiationAgreement(this.createResponseItems());
		
	}
	private List<ResponseItem> createResponseItems() {
		List<ResponseItem> respItems = new ArrayList<ResponseItem>();
		for (RequestItem reqItem : this.requestPolicy.getRequestItems()){
			ResponseItem respItem = new ResponseItem();
			respItem.setDecision(Decision.PERMIT);
			respItem.setRequestItem(reqItem);
			respItems.add(respItem);
		}
		
		return respItems;
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
		
		return requestItems;
	}

	private void setupContext(){
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		
		
		CtxEntityIdentifier preferenceEntityId_ = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));

		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_);
		
		this.ids_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ids_Preference_Name1, new Long(5)));
		
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
	}
}

