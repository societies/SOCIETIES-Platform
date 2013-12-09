/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.CommunityPreferenceManagement.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
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
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CommunityPreferenceManagement.impl.CommunityPreferenceManagement;
import org.societies.personalisation.CommunityPreferenceManagement.impl.comms.CommunityPreferenceManagementClient;
import org.societies.personalisation.CommunityPreferenceManagement.impl.management.CtxModelTypes;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;
import org.springframework.scheduling.annotation.AsyncResult;

public class CommunityPreferenceManagementTest {

	private static final String preferenceName = "volume";
	CommunityPreferenceManagement communityPrefMgr;
	private ICisManager cisManager;
	private ICommManager commsMgr;
	private CommunityPreferenceManagementClient communityPreferenceManagementClient;
	private ICtxBroker ctxBroker;
	private IIdentity cisID;
	private List<IPreferenceTreeModel> models;
	private ServiceResourceIdentifier serviceID;
	private CtxAttribute locationAttribute;
	private CtxAttribute statusAttribute;
	private IIdentity userId;
	private IndividualCtxEntity ctxEntity;
	private PreferenceTreeModel preferenceTreeModel;
	private CtxEntity preferenceCtxEntity;
	private CtxAssociation hasPreferences;
	private CtxAttribute preferenceRegistryAttribute;
	private CtxAttribute preferenceAttribute1;
	private CommunityCtxEntity communityCtxEntity;
	private final static String preferenceKey1 = "community_preference_1";
	private PreferenceDetails preferenceDetails;
	private PreferenceTreeModel newPreferenceTreeModel;
	
	public CommunityPreferenceManagementTest() {
		cisID = new MockIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		userId = new MockIdentity(IdentityType.CSS, "user", "ict-societies.eu");
		this.cisManager = Mockito.mock(ICisManager.class);
		this.commsMgr = Mockito.mock(ICommManager.class);
		this.communityPreferenceManagementClient = Mockito.mock(CommunityPreferenceManagementClient.class);
		this.ctxBroker = Mockito.mock(ICtxBroker.class);
		communityPrefMgr = new CommunityPreferenceManagement();
		communityPrefMgr.setCisManager(cisManager);
		communityPrefMgr.setCommsMgr(commsMgr);
		communityPrefMgr.setCommunityPreferenceManagementClient(communityPreferenceManagementClient);
		communityPrefMgr.setCtxBroker(ctxBroker);
		communityPrefMgr.initialiseCommunityPreferenceManagement();
	}
	
	@Before
	public void setup(){
		this.setupContext();
		this.setupModels();
		this.setupNewModelForMerging();
		setupPreferenceCtxAttributes();
		List<CtxIdentifier> preferenceEntityList = new ArrayList<CtxIdentifier>();
		preferenceEntityList.add(preferenceCtxEntity.getId());
		List<ICisOwned> ownedCiss = new ArrayList<ICisOwned>();
		ICisOwned ownedCis = new CisOwned(cisID);
		ownedCiss.add(ownedCis);
		Mockito.when(this.cisManager.getListOfOwnedCis()).thenReturn(ownedCiss);
		try {
			Mockito.when(ctxBroker.retrieveCommunityEntityId(cisID)).thenReturn(new AsyncResult<CtxEntityIdentifier>(this.communityCtxEntity.getId()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxModelTypes.PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(preferenceEntityList));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) this.preferenceCtxEntity.getId(), preferenceKey1)).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceAttribute1));
			Mockito.when(ctxBroker.update(preferenceAttribute1)).thenReturn(new AsyncResult<CtxModelObject>(preferenceAttribute1));
			Mockito.when(ctxBroker.lookup(this.communityCtxEntity.getId(), CtxModelType.ATTRIBUTE, CtxModelTypes.PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.createAttribute(communityCtxEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceRegistryAttribute));
			Mockito.when(ctxBroker.update(this.preferenceRegistryAttribute)).thenReturn(new AsyncResult<CtxModelObject>(this.preferenceRegistryAttribute));
			Mockito.when(ctxBroker.retrieve(this.preferenceAttribute1.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.preferenceAttribute1));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupContext() {
		
		CtxEntityIdentifier ctxEntityId = new CtxEntityIdentifier(userId.getJid(), CtxEntityTypes.PERSON, new Long(1));
		ctxEntity = new IndividualCtxEntity(ctxEntityId);	
		
		CtxEntityIdentifier communityCtxEntityId = new CtxEntityIdentifier(this.cisID.getJid(), CtxEntityTypes.COMMUNITY, new Long(3));
		this.communityCtxEntity = new CommunityCtxEntity(communityCtxEntityId);
		
		CtxEntityIdentifier preferenceCtxEntityId = new CtxEntityIdentifier(userId.getJid(), CtxModelTypes.PREFERENCE, new Long(1));
		preferenceCtxEntity = new CtxEntity(preferenceCtxEntityId);
		
		CtxAssociationIdentifier assocID = new CtxAssociationIdentifier(userId.getJid(), CtxModelTypes.HAS_PREFERENCES, new Long(1));
		hasPreferences = new CtxAssociation(assocID);
		hasPreferences.setParentEntity(ctxEntityId);
		hasPreferences.addChildEntity(preferenceCtxEntityId);
		
		/*
		 * symbolic location attribute
		 */
		CtxAttributeIdentifier ctxLocationAttributeId = new CtxAttributeIdentifier(ctxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");
		
		/*
		 * status attribute
		 */
		
		CtxAttributeIdentifier statusAttributeId = new CtxAttributeIdentifier(ctxEntityId, CtxAttributeTypes.STATUS, new Long(2));
		statusAttribute = new CtxAttribute(statusAttributeId);
		statusAttribute.setStringValue("busy");
		
		ctxEntity.addAttribute(locationAttribute);
		ctxEntity.addAttribute(statusAttribute);
		
		
		
	}


	@Test
	@Ignore
	public void test(){
		List<IPreferenceTreeModel> allCommunityPreferences = this.communityPrefMgr.getAllCommunityPreferences(cisID);
		Assert.assertNotNull(allCommunityPreferences);
		Assert.assertEquals(0, allCommunityPreferences.size());
		this.communityPrefMgr.uploadUserPreferences(cisID, models);
		
		allCommunityPreferences = this.communityPrefMgr.getAllCommunityPreferences(cisID);
		Assert.assertNotNull(allCommunityPreferences);
		Assert.assertEquals(1, allCommunityPreferences.size());		
		
		
		List<PreferenceDetails> details = new ArrayList<PreferenceDetails>();
		details.add(preferenceDetails);
		List<IPreferenceTreeModel> communityPreferences = this.communityPrefMgr.getCommunityPreferences(cisID, details);
		Assert.assertEquals(1, communityPreferences.size());
		IPreferenceTreeModel iPreferenceTreeModel = communityPreferences.get(0);
		Assert.assertTrue(ServiceModelUtils.compare(this.preferenceDetails.getServiceID(), iPreferenceTreeModel.getPreferenceDetails().getServiceID()));
		
		List<IPreferenceTreeModel> newModels = new ArrayList<IPreferenceTreeModel>();
		newModels.add(this.newPreferenceTreeModel);
		this.communityPrefMgr.uploadUserPreferences(cisID, newModels);
		
		allCommunityPreferences = this.communityPrefMgr.getAllCommunityPreferences(cisID);
		Assert.assertNotNull(allCommunityPreferences);
		Assert.assertEquals(1, allCommunityPreferences.size());
		
		communityPreferences = this.communityPrefMgr.getCommunityPreferences(cisID, details);
		Assert.assertEquals(1, communityPreferences.size());
		
		iPreferenceTreeModel = communityPreferences.get(0);
		Assert.assertTrue(ServiceModelUtils.compare(this.preferenceDetails.getServiceID(), iPreferenceTreeModel.getPreferenceDetails().getServiceID()));
		
		System.out.println(iPreferenceTreeModel.getRootPreference().toTreeString());
		
		IPreference rootPreference = iPreferenceTreeModel.getRootPreference();
		Assert.assertNull(rootPreference.getUserObject());
		
		Assert.assertEquals(2, rootPreference.getChildCount());
	}
	
	
	private void setupModels(){
		
		try {
			this.serviceID = new ServiceResourceIdentifier();
			this.serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
			this.serviceID.setServiceInstanceIdentifier("MediaPlayer");
			preferenceDetails = new PreferenceDetails();
			preferenceDetails.setPreferenceName(preferenceName);
			preferenceDetails.setServiceID(serviceID);
			preferenceDetails.setServiceType("");
			
			
			PreferenceTreeNode outcomePreference = new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", this.preferenceName , "10")); 
			PreferenceTreeNode symlocConditionPreference = new PreferenceTreeNode(new ContextPreferenceCondition(statusAttribute.getId(), OperatorConstants.EQUALS, "busy", statusAttribute.getType()));
			symlocConditionPreference.add(outcomePreference);

			preferenceTreeModel = new PreferenceTreeModel(preferenceDetails, symlocConditionPreference);
			this.models = new ArrayList<IPreferenceTreeModel>();
			this.models.add(preferenceTreeModel);
			
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail("Could not create preference");
		}
		
		
		
	}
	
	private void setupNewModelForMerging(){
		

			
			PreferenceTreeNode outcomePreference = new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", this.preferenceName , "60")); 
			PreferenceTreeNode symlocConditionPreference = new PreferenceTreeNode(new ContextPreferenceCondition(this.locationAttribute.getId(), OperatorConstants.EQUALS, "home", this.locationAttribute.getType()));
			symlocConditionPreference.add(outcomePreference);


			this.newPreferenceTreeModel = new PreferenceTreeModel(preferenceDetails, symlocConditionPreference);

			System.out.println(symlocConditionPreference.toTreeString());
		
		
	}
	
	private void setupPreferenceCtxAttributes(){
		
		CtxAttributeIdentifier preferenceRegistryAttributeId = new CtxAttributeIdentifier(this.ctxEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY, new Long(1));
		this.preferenceRegistryAttribute = new CtxAttribute(preferenceRegistryAttributeId);
		
		
		CtxAttributeIdentifier prefAttr1 = new CtxAttributeIdentifier(this.preferenceCtxEntity.getId(), preferenceKey1, new Long(1));
		this.preferenceAttribute1 = new CtxAttribute(prefAttr1);
		
	}
}
