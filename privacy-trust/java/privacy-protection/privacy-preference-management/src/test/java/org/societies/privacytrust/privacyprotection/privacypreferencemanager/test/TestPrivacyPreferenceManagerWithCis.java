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

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
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
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestPrivacyPreferenceManagerWithCis {
	/*
	PrivacyPreferenceManager privPrefMgr;
	private DObfPreferenceDetailsBean dobfDetails;
	private IDSPreferenceDetailsBean idsDetails;
	
	private DObfPreferenceTreeModel dobfModel;
	private IDSPrivacyPreferenceTreeModel idsModel;
	private PPNPrivacyPreferenceTreeModel ppnModel;
	
	private RequestorCisBean requestorCis;
	
	private RequestPolicy requestPolicy;
	private List<Action> actions;
	
	private Resource resourceWithoutID;
	private Resource resourceWithID;
	
	private NegotiationAgreement agreement;
	private List<IIdentity> identities;
	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IPrivacyDataManagerInternal privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private List<Condition> conditions;
	private CtxAttribute locationAttribute;
	private MyIdentity userId;
	private CtxEntity userCtxEntity;
	private CtxAssociation hasPrivacyPreferences;
	private AccessControlPreferenceTreeModel accCtrlmodel;
	private AccessControlPreferenceDetailsBean accCtrlDetails;
	
	@Before
	public void setUp(){
		this.privPrefMgr  = new PrivacyPreferenceManager();
		this.privPrefMgr.setCommsMgr(commsMgr);
		this.privPrefMgr.setIdMgr(idMgr);
		this.privPrefMgr.setCtxBroker(ctxBroker);
		this.privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		this.privPrefMgr.setTrustBroker(trustBroker);
		this.privPrefMgr.setUserFeedback(userFeedback);
		
		
		
		this.setupRequestor();
		this.setupContext();
		this.setupPolicy();
		this.setupDetails();
		this.setupAgreement();
		this.setupModels();
		this.mockAllIntegration();
		this.privPrefMgr.initialisePrivacyPreferenceManager();
	}
	

	private void mockAllIntegration(){
		try {
			Mockito.when(commsMgr.getIdManager()).thenReturn(idMgr);
			Mockito.when(idMgr.getThisNetworkNode()).thenReturn((INetworkNode) userId);
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<CtxAssociation>(this.hasPrivacyPreferences));
			//Mockito.when(ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<CtxEntity>(privacyPreferenceEntity));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupModels() {
		this.setupPPNModel();
		this.setupIDSModel();
		this.setupDObfModel();
		this.setupAccCtrlModel();
		
		

		IPrivacyPreference dobfPreference = new PrivacyPreference();
		this.dobfModel = new DObfPreferenceTreeModel(dobfDetails, dobfPreference);
		
		

		
		IPrivacyPreference accCtrlPreference = new PrivacyPreference();
		this.accCtrlmodel = new AccessControlPreferenceTreeModel(accCtrlDetails, accCtrlPreference);
		
	}

	private void setupAccCtrlModel() {
		AccessControlOutcome outcomeAllow  = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
		AccessControlOutcome outcomeBlock = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);
		
		IPrivacyPreference preferenceAllow = new PrivacyPreference(outcomeAllow);
		IPrivacyPreference preferenceBlock = new PrivacyPreference(outcomeBlock);
		
		
		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			conditionPreference.add(preferenceAllow);
			preferenceAllow = conditionPreference;
		}
		
		
		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			if (cond.getConditionConstant().equals(ConditionConstants.DATA_RETENTION_IN_HOURS)){
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("12");
			}else{
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("No");
			}
			conditionPreference.add(preferenceBlock);
			preferenceBlock = conditionPreference;
		}
		
		IPrivacyPreference privacyPreference = new PrivacyPreference();
		privacyPreference.add(preferenceAllow);
		privacyPreference.add(preferenceBlock);
		
		this.accCtrlmodel = new AccessControlPreferenceTreeModel(accCtrlDetails, privacyPreference);
		
	}


	private void setupDObfModel() {
		// TODO Auto-generated method stub
		
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

	private void setupContext() {
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		this.identities = new ArrayList<IIdentity>();
		this.identities.add(this.userId);
		this.identities.add(new MyIdentity(IdentityType.CSS, "xcmanager1", "societies.local"));
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		CtxAttributeIdentifier id = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		this.locationAttribute = new CtxAttribute(id);
		hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));
	}

	private void setupRequestor() {
		this.requestorCis = new RequestorCisBean();
		this.requestorCis.setCisRequestorId("myCis.ict-societies.eu");
		this.requestorCis.setRequestorId("myCisAdmin.ict-societies.eu");
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
		this.requestPolicy.setRequestor(requestorCis);
		
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
		
		this.resourceWithID = new Resource();
		this.resourceWithID.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.resourceWithID.setDataIdUri(this.locationAttribute.getId().getUri());
		this.resourceWithID.setScheme(DataIdentifierScheme.CONTEXT);
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

	private void setupDetails() {


		this.ppNetails = new PPNPreferenceDetailsBean();
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		this.ppNetails.setAction(action);
		this.ppNetails.setRequestor(requestorCis);
		this.ppNetails.setResource(resourceWithoutID);

		this.dobfDetails  = new DObfPreferenceDetailsBean();
		this.dobfDetails.setRequestor(requestorCis);
		this.dobfDetails.setResource(resourceWithID);

		
		this.idsDetails = new IDSPreferenceDetailsBean();
		this.idsDetails.setAffectedIdentity(this.userId.getJid());
		this.idsDetails.setRequestor(requestorCis);

		
		this.accCtrlDetails  = new AccessControlPreferenceDetailsBean();
		this.accCtrlDetails.setAction(action);
		this.accCtrlDetails.setRequestor(requestorCis);
		this.accCtrlDetails.setResource(resourceWithID);
		
	}


	@Test
	@Ignore
	public void testAllMethods(){

		privPrefMgr.storeAccCtrlPreference(accCtrlDetails, accCtrlmodel);
		privPrefMgr.storeDObfPreference(dobfDetails, dobfModel);
		privPrefMgr.storeIDSPreference(idsDetails, idsModel);
		privPrefMgr.storePPNPreference(ppNetails, ppnModel);
		
		try {
			privPrefMgr.checkPermission(requestorCis, ResourceUtils.getDataIdentifier(resourceWithID), actions);
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			privPrefMgr.checkPermission(requestorCis, ResourceUtils.getDataIdentifier(resourceWithID), actions);
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		privPrefMgr.deleteAccCtrlPreference(accCtrlDetails);
		privPrefMgr.deleteDObfPreference(dobfDetails);
		privPrefMgr.deleteIDSPreference(idsDetails);
		privPrefMgr.deletePPNPreference(ppNetails);
		
		//privPrefMgr.evaluateAccCtrlPreference(accCtrlDetails);
		privPrefMgr.evaluateDObfPreference(dobfDetails);
		privPrefMgr.evaluateIDSPreference(idsDetails);
		privPrefMgr.evaluateIDSPreferences(agreement, identities);
		privPrefMgr.evaluatePPNPreferences(requestPolicy);
		
		privPrefMgr.getAccCtrlPreference(accCtrlDetails);
		privPrefMgr.getIDSPreference(idsDetails);
		privPrefMgr.getDObfPreference(dobfDetails);
		privPrefMgr.getPPNPreference(ppNetails);
		
		
		privPrefMgr.getAccCtrlPreferenceDetails();
		privPrefMgr.getDObfPreferenceDetails();
		privPrefMgr.getIDSPreferenceDetails();
		privPrefMgr.getPPNPreferenceDetails();


	}
*/
}
