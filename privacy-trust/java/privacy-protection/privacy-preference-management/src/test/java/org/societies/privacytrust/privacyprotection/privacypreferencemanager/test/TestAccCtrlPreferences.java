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

import javax.swing.JOptionPane;

import org.apache.commons.collections.ListUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationClient;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
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
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.AccessControlPreferenceCreator;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestAccCtrlPreferences {

	
	private static final String ACC_CTRL_PREFERENCE_1 = "accCtrl_preference_1";
	private static final String ACC_CTRL_PREFERENCE_2_AUTO = "accCtrl_preference_2";
	private static final String ACC_CTRL_PREFERENCE_3_AUTO = "accCtrl_preference_3";
	private static final String ACC_CTRL_PREFERENCE_4_AUTO = "accCtrl_preference_4";
	private static final String ACC_CTRL_PREFERENCE_5_AUTO = "accCtrl_preference_5";
	private AccessControlPreferenceDetailsBean accCtrlDetails;
	private AccessControlPreferenceTreeModel accCtrlmodel;
	private RequestorCisBean requestorCisBean;

	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private IPrivacyDataManagerInternal privacyDataManagerInternal = Mockito.mock(IPrivacyDataManagerInternal.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private IPrivacyAgreementManager agreementMgr = Mockito.mock(IPrivacyAgreementManager.class);
	private IEventMgr eventManager = Mockito.mock(IEventMgr.class);
	private PrivacyPreferenceManager privPrefMgr;
	private Resource resourceWithID;
	private List<Action> actions;
	private ArrayList<Condition> conditions;
	private Resource resourceWithoutID;
	private IIdentity userId;
	private CtxEntity userCtxEntity;
	private CtxAttribute locationAttribute;
	private CtxAssociation hasPrivacyPreferences;
	private CtxEntity privacyPreferenceEntity;
	private CtxAttribute accCtrl_1_CtxAttribute;
	private CtxAttribute accCtrl_2_CtxAttribute;
	private CtxAttribute accCtrl_3_CtxAttribute;
	private CtxAttribute accCtrl_4_CtxAttribute;
	private CtxAttribute accCtrl_5_CtxAttribute;
	private CtxAttribute registryCtxAttribute;
	private NegotiationAgreement agreement;
	private RequestPolicy requestPolicy;
	private RequestorCis requestorCis;
	private CtxAttribute nameAttribute;
	private CtxAttribute ageAttribute;
	private CtxAttribute statusAttribute;
	

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
		this.privPrefMgr.setEventMgr(eventManager);
		this.setupRequestor();
		this.setupContext();
		this.setupPolicyDetails();
		this.setupPolicy();
		this.setupAgreement();
		this.setupDetails();
		this.setupAccCtrlModel();
		this.mockAllIntegration();
		this.privPrefMgr.initialisePrivacyPreferenceManager();
	}
	
	
	private void mockAllIntegration(){
		try {
			Mockito.when(commsMgr.getIdManager()).thenReturn(idMgr);
			Mockito.when(idMgr.getThisNetworkNode()).thenReturn((INetworkNode) userId);
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			List<CtxIdentifier> preferenceEntityList = new ArrayList<CtxIdentifier>();
			preferenceEntityList.add(this.privacyPreferenceEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(preferenceEntityList));
			Mockito.when(ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<CtxAssociation>(this.hasPrivacyPreferences));
			Mockito.when(ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<CtxEntity>(privacyPreferenceEntity));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_1)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			//Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_2_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(registryCtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(registryCtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_2_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_2_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_3_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_3_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_4_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_4_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_5_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_5_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_1)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2_AUTO)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_2_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_3_AUTO)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_3_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_4_AUTO)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_4_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_5_AUTO)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_5_CtxAttribute));
			
			Mockito.when(ctxBroker.retrieve(locationAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.locationAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_1_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_2_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_2_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_3_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_3_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_4_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_4_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_5_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_5_CtxAttribute));
			AgreementEnvelope agreementEnvelope;
			agreementEnvelope = new AgreementEnvelope(agreement, new byte[]{}, new byte[]{});
			Mockito.when(agreementMgr.getAgreement((Requestor) Mockito.anyObject())).thenReturn(agreementEnvelope);
/*			String allow  = "Allow";
			String deny = "Deny";
			List<String> response = new ArrayList<String>();
			response.add(allow);*/
			org.societies.api.privacytrust.privacy.model.privacypolicy.Action action = new org.societies.api.privacytrust.privacy.model.privacypolicy.Action(org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants.READ);
			List<String> actionObjList = new ArrayList<String>();
			actionObjList.add(action.getActionConstants().name());
			Mockito.when(userFeedback.getExplicitFB(Mockito.eq(ExpProposalType.CHECKBOXLIST), (ExpProposalContent) Mockito.anyObject())).thenReturn(new AsyncResult<List<String>>(actionObjList));
			Class<List<AccessControlResponseItem>> listClass = (Class<List<AccessControlResponseItem>>)(Class)List.class;
			final ArgumentCaptor<List<AccessControlResponseItem>> argument = ArgumentCaptor.forClass(listClass);
			
			Mockito.when(userFeedback.getAccessControlFB((Requestor) Mockito.anyObject(), argument.capture())).thenAnswer(new Answer(){

				@Override
				public Object answer(InvocationOnMock invocation)
						throws Throwable {
					List<AccessControlResponseItem> items = argument.getValue();
					for (AccessControlResponseItem item : items){
						item.setDecision(Decision.PERMIT);
						
					}
					return new AsyncResult<List<AccessControlResponseItem>>(items);
				}
				
			});
			

			List<String> optionsList = new ArrayList<String>();
			optionsList.add("Allow");
			Mockito.when(userFeedback.getExplicitFB(Mockito.eq(ExpProposalType.ACKNACK), (ExpProposalContent) Mockito.anyObject())).thenReturn(new AsyncResult<List<String>>(optionsList));

			List<CtxIdentifier> locationCtxIds = new ArrayList<CtxIdentifier>();
			locationCtxIds.add(this.locationAttribute.getId());
			Mockito.when(this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, locationAttribute.getType())).thenReturn(new AsyncResult<List<CtxIdentifier>>(locationCtxIds));
			
			List<CtxIdentifier> ageCtxIds = new ArrayList<CtxIdentifier>();
			ageCtxIds.add(this.ageAttribute.getId());
			Mockito.when(this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, ageAttribute.getType())).thenReturn(new AsyncResult<List<CtxIdentifier>>(ageCtxIds));
			Mockito.when(this.ctxBroker.retrieve(ageAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.ageAttribute));
			
			List<CtxIdentifier> nameCtxIds = new ArrayList<CtxIdentifier>();
			nameCtxIds.add(this.nameAttribute.getId());
			Mockito.when(this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, nameAttribute.getType())).thenReturn(new AsyncResult<List<CtxIdentifier>>(nameCtxIds));
			Mockito.when(this.ctxBroker.retrieve(nameAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.nameAttribute));
			
			List<CtxIdentifier> statusCtxIds = new ArrayList<CtxIdentifier>();
			statusCtxIds.add(this.statusAttribute.getId());
			Mockito.when(this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, statusAttribute.getType())).thenReturn(new AsyncResult<List<CtxIdentifier>>(statusCtxIds));
			Mockito.when(this.ctxBroker.retrieve(statusAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.statusAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testAllMethods() throws MalformedCtxIdentifierException, PrivacyException{

		boolean stored = privPrefMgr.storeAccCtrlPreference(accCtrlDetails, accCtrlmodel);
		Assert.assertTrue(stored);
		
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails = privPrefMgr.getAccCtrlPreferenceDetails();
		
		Assert.assertNotNull(accCtrlPreferenceDetails);
		
		Assert.assertTrue(accCtrlPreferenceDetails.size()==1);
		
		Assert.assertEquals(this.accCtrlDetails, accCtrlPreferenceDetails.get(0));
		
		AccessControlPreferenceTreeModel accCtrlPreferenceModel = privPrefMgr.getAccCtrlPreference(accCtrlDetails);
		Assert.assertNotNull(accCtrlPreferenceModel);
		Assert.assertEquals(this.accCtrlmodel, accCtrlPreferenceModel);

		Assert.assertEquals(ResourceUtils.getDataIdentifier(resourceWithID), this.locationAttribute.getId());
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		dataIds.add(this.locationAttribute.getId());
		List<ResponseItem> items = privPrefMgr.checkPermission(requestorCisBean, dataIds, actions);
		Assert.assertNotNull(items);
		Assert.assertTrue(items.size() > 0);
		
		Assert.assertNotNull(items.get(0).getDecision());
		
		Assert.assertEquals(Decision.PERMIT, items.get(0).getDecision());
		
		/**
		 * to be removed after refactoring of obj model
		 */
		org.societies.api.privacytrust.privacy.model.privacypolicy.Action actionRead = new org.societies.api.privacytrust.privacy.model.privacypolicy.Action(org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants.READ);
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actionObjList = new ArrayList<org.societies.api.privacytrust.privacy.model.privacypolicy.Action>();
		actionObjList.add(actionRead);

		List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> items2 = privPrefMgr.checkPermission(requestorCis, this.locationAttribute.getId(), actionObjList);
		
		/**
		 * end of to be removed test
		 */
		Assert.assertNotNull(items2);
		Assert.assertTrue(items2.size() > 0);
		Assert.assertNotNull(items2.get(0).getDecision());
		Assert.assertEquals(Decision.PERMIT.name(), items2.get(0).getDecision().name());

		ResponseItem evRespItem = privPrefMgr.evaluateAccCtrlPreference(accCtrlDetails);
		Assert.assertNotNull(evRespItem);
		Assert.assertNotNull(evRespItem.getDecision());
		Assert.assertEquals(Decision.PERMIT.name(), evRespItem.getDecision().name());
		
		
		boolean deleted = privPrefMgr.deleteAccCtrlPreference(accCtrlDetails);

		Assert.assertTrue(deleted);
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails1 = privPrefMgr.getAccCtrlPreferenceDetails();
		
		Assert.assertNotNull(accCtrlPreferenceDetails1);
		
		Assert.assertTrue(accCtrlPreferenceDetails1.size()==0);
		
		
		
		AccessControlPreferenceTreeModel accCtrlPreferenceModel1 = privPrefMgr.getAccCtrlPreference(accCtrlDetails);
		Assert.assertNull(accCtrlPreferenceModel1);


		AccessControlPreferenceCreator accCtrlPrefCreator = privPrefMgr.getAccCtrlPreferenceCreator();
		NegotiationDetails negDetails = new NegotiationDetails(this.requestorCis, 0);
		List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		
		IAgreement agreement = new NegotiationAgreement(this.createResponseItems());
		PPNegotiationEvent negEvent  = new PPNegotiationEvent(agreement, NegotiationStatus.SUCCESSFUL, negDetails);
		InternalEvent event = new InternalEvent(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT, "", INegotiationClient.class.getName(), negEvent);
		accCtrlPrefCreator.handleInternalEvent(event);
		
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails2 = privPrefMgr.getAccCtrlPreferenceDetails();
		Assert.assertNotNull(accCtrlPreferenceDetails2);
		Assert.assertTrue(accCtrlPreferenceDetails2.size()>0);
		

		for (AccessControlPreferenceDetailsBean   det: accCtrlPreferenceDetails2){
			System.out.println("************************"+ResourceUtils.toString(det.getResource()));
			privPrefMgr.deleteAccCtrlPreference(det);
		}
		
		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails3 = privPrefMgr.getAccCtrlPreferenceDetails();
		Assert.assertTrue(accCtrlPreferenceDetails3.size()==0);
	
		org.societies.api.privacytrust.privacy.model.privacypolicy.Action actionCreate = new org.societies.api.privacytrust.privacy.model.privacypolicy.Action(org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants.CREATE);
		actionObjList.add(actionCreate);
		
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> checkPermissionNotExistModels = privPrefMgr.checkPermission(requestorCis, this.locationAttribute.getId(), actionObjList);
		Assert.assertNotNull(checkPermissionNotExistModels);
		Assert.assertTrue(checkPermissionNotExistModels.size() > 0);
		Assert.assertFalse(checkPermissionNotExistModels.get(0).getRequestItem().getActions().contains(actionCreate));
		Assert.assertTrue(checkPermissionNotExistModels.get(0).getRequestItem().getActions().contains(actionRead));
		
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
	private void setupRequestor() {
		this.requestorCisBean = new RequestorCisBean();
		IIdentity requestorIdentity = new MyIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		
		this.requestorCisBean.setRequestorId(requestorIdentity.getJid());
		IIdentity cisIdentity = new MyIdentity(IdentityType.CIS, "myCis","ict-societies.eu");
		this.requestorCisBean.setCisRequestorId(cisIdentity.getJid());
		
		requestorCis = new RequestorCis(requestorIdentity, cisIdentity);
	}
	
	private  void setupDetails(){
		
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		this.accCtrlDetails  = new AccessControlPreferenceDetailsBean();
		this.accCtrlDetails.setAction(action);
		this.accCtrlDetails.setRequestor(requestorCisBean);
		this.accCtrlDetails.setResource(resourceWithID);
		
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
			Condition copiedCondition = new Condition();
			copiedCondition.setConditionConstant(cond.getConditionConstant());
			copiedCondition.setOptional(cond.isOptional());
			copiedCondition.setValue(cond.getValue());
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(copiedCondition));
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
		
		System.out.println(((PrivacyPreference) privacyPreference).toString());
		this.accCtrlmodel = new AccessControlPreferenceTreeModel(accCtrlDetails, privacyPreference);
		
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
/*		this.actions.add(create);
		this.actions.add(write);
		this.actions.add(delete);*/
		
		
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
	private void setupContext() {
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		CtxAttributeIdentifier id = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		this.locationAttribute = new CtxAttribute(id);
		this.locationAttribute.setStringValue("home");
		this.locationAttribute.setValueType(CtxAttributeValueType.STRING);
		this.locationAttribute.getQuality().setOriginType(CtxOriginType.SENSED);
		
		CtxAttributeIdentifier nameId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.NAME, new Long(1));
		this.nameAttribute = new CtxAttribute(nameId);
		this.nameAttribute.setValueType(CtxAttributeValueType.STRING);
		this.nameAttribute.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		
		CtxAttributeIdentifier ageId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.AGE, new Long(1));
		this.ageAttribute = new CtxAttribute(ageId);
		this.ageAttribute.setValueType(CtxAttributeValueType.STRING);
		this.ageAttribute.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		
		CtxAttributeIdentifier statusId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		this.statusAttribute = new CtxAttribute(statusId);
		this.statusAttribute.setValueType(CtxAttributeValueType.STRING);
		this.statusAttribute.getQuality().setOriginType(CtxOriginType.INFERRED);
		
		
		hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));
		CtxEntityIdentifier preferenceEntityId_ = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));

		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_);
		
		this.accCtrl_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_1, new Long(5)));
		this.accCtrl_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2_AUTO, new Long(5)));
		this.accCtrl_3_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_3_AUTO, new Long(5)));
		this.accCtrl_4_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_4_AUTO, new Long(5)));
		this.accCtrl_5_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_5_AUTO, new Long(5)));
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
		//this.accCtrl_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2, new Long(6)));
	}

}
