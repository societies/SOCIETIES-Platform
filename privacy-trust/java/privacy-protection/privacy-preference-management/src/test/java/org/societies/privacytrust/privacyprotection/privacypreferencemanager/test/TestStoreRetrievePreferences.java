package org.societies.privacytrust.privacyprotection.privacypreferencemanager.test;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

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
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.springframework.scheduling.annotation.AsyncResult;

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

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestStoreRetrievePreferences {

	ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	IPrivacyDataManagerInternal  privacyDataManager = Mockito.mock(IPrivacyDataManagerInternal.class);
	ICommManager commsManager = Mockito.mock(ICommManager.class);
	IIdentityManager identityManager = Mockito.mock(IIdentityManager.class);
	PrivacyPreferenceManager privPrefMgr = new PrivacyPreferenceManager();
	IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	IIdentity userId;
	CtxEntity userCtxEntity;
	CtxAssociation hasPrivacyPreferences;
	CtxEntity privacyPreferenceEntity;
	private CtxAttribute ppnp_1_CtxAttribute;
	private CtxAttribute registryCtxAttribute;
	private CtxAttribute ppnp_2_CtxAttribute;

	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxAttribute locationAttribute;
	
	
	@Before
	public void setup(){
		Mockito.when(commsManager.getIdManager()).thenReturn(identityManager);
		privPrefMgr.setCtxBroker(ctxBroker);
		privPrefMgr.setCommsMgr(commsManager);
		privPrefMgr.setprivacyDataManagerInternal(privacyDataManager);
		privPrefMgr.setTrustBroker(trustBroker);
		privPrefMgr.setUserFeedback(userFeedback);
		this.setupContext();
		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(identityManager.getThisNetworkNode()).thenReturn((INetworkNode) userId);
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			
			Mockito.when(ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<CtxAssociation>(this.hasPrivacyPreferences));
			Mockito.when(ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<CtxEntity>(privacyPreferenceEntity));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), "ppnp_preference_1")).thenReturn(new AsyncResult<CtxAttribute>(ppnp_1_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), "ppnp_preference_2")).thenReturn(new AsyncResult<CtxAttribute>(ppnp_2_CtxAttribute));
			//Mockito.when(JOptionPane.showConfirmDialog(null, Mockito.eq(Mockito.anyString()),Mockito.eq(Mockito.anyString()), JOptionPane.YES_NO_OPTION )).thenReturn(JOptionPane.YES_OPTION);
			//Mockito.doReturn(JOptionPane.showConfirmDialog(null, Mockito.eq(Mockito.anyString()),Mockito.eq(Mockito.anyString()), JOptionPane.YES_NO_OPTION ));
			//Mockito.when(myMsgBox.showConfirmDialog(Mockito.anyString(),Mockito.anyString(), Mockito.eq(JOptionPane.YES_NO_OPTION))).thenReturn(JOptionPane.YES_OPTION);		
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(ppnp_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(ppnp_1_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(locationAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.locationAttribute));
			Mockito.when(ctxBroker.retrieve(Mockito.eq(ppnp_1_CtxAttribute.getId()))).thenReturn(new AsyncResult<CtxModelObject>(ppnp_1_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(Mockito.eq(ppnp_2_CtxAttribute.getId()))).thenReturn(new AsyncResult<CtxModelObject>(ppnp_2_CtxAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		privPrefMgr.initialisePrivacyPreferenceManager();

	}
	
	
	@Test
	@Ignore
	public void testStoreRetrieve(){
		RequestorBean requestor = getRequestorService();
		PPNPreferenceDetailsBean details = new PPNPreferenceDetailsBean();
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		List<Action> actions = new ArrayList<Action>();
		
		Resource resource = ResourceUtils.create(locationAttribute.getId().getScheme(), locationAttribute.getType());
		details.setResource(resource);
		
		details.setRequestor(requestor);
		
		PPNPrivacyPreferenceTreeModel model = this.getPPNPreference(details, requestor);
		if(model==null){
			TestCase.fail("Error creating ppnPreference");
			return;
		}
		
		
		privPrefMgr.storePPNPreference(details, model);
		
		
		PPNPrivacyPreferenceTreeModel iptm = privPrefMgr.getPPNPreference(details);
		
		if (iptm == null){
			TestCase.fail("Error storing/retrieving PPN preference");
		}
		
		privPrefMgr.storePPNPreference(details, model);
		
		iptm = privPrefMgr.getPPNPreference(details);
		
		if (iptm == null){
			TestCase.fail("Error updating/retrieving PPN preference");
		}
		
		
		
		
		privPrefMgr.deletePPNPreference(details);
		
		iptm = privPrefMgr.getPPNPreference(details);
		
		if (iptm!=null){
			TestCase.fail("Error deleting PPN preference");
		}

	}


	private PPNPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetailsBean details, RequestorBean requestor) {

		IPrivacyPreferenceCondition locationCondition = new ContextPreferenceCondition(locationAttribute.getId(), OperatorConstants.EQUALS, "home");		
		PPNPOutcome outcome = new PPNPOutcome(Decision.PERMIT);
		IPrivacyPreference preference = new PrivacyPreference(outcome);
		IPrivacyPreference conditionPreference = new PrivacyPreference(locationCondition);
		conditionPreference.add(preference);
		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(details, conditionPreference);
		return model;
		
	}


	private void setupContext(){
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));
		CtxEntityIdentifier preferenceEntityId_ppnp1 = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));
		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_ppnp1);
		this.ppnp_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), "ppnp_preference_1", new Long(5)));
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
		this.ppnp_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), "ppnp_preference_2", new Long(6)));
		ctxLocationAttributeId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");		
	}
	private RequestorServiceBean getRequestorService(){
		
		String requestorId = "eliza@societies.org";
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RequestorServiceBean bean = new RequestorServiceBean();
		bean.setRequestorId(requestorId);
		bean.setRequestorServiceId(serviceId);
		return bean;
	}
	

}
