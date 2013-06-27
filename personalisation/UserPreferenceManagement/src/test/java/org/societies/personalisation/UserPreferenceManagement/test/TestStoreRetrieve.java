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
package org.societies.personalisation.UserPreferenceManagement.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
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
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.management.CtxModelTypes;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestStoreRetrieve {

	UserPreferenceConditionMonitor pcm ;
	ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	ICommManager commManager = Mockito.mock(ICommManager.class);
	UserPreferenceManagement prefMgr;
	private IIdentity mockId;
	private IndividualCtxEntity ctxEntity;
	private CtxAttribute locationAttribute;
	private ServiceResourceIdentifier serviceID;
	private String preferenceName = "volume";
	private CtxAttribute statusAttribute;
	private PreferenceTreeNode preference;
	private CtxEntity preferenceCtxEntity;
	private CtxAssociation hasPreferences;
	private CtxAttribute preferenceAttribute1;
	private CtxAttribute preferenceAttribute2;
	private CtxAttribute preferenceRegistryAttribute;
	private PreferenceDetails preferenceDetails;
	private final static String preferenceKey1 = "preference_1";
	private final static String preferenceKey2 = "preference_2";
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	@Before
	public void setUp(){
		pcm = new UserPreferenceConditionMonitor();
		pcm.setCtxBroker(ctxBroker);
		pcm.setEventMgr(Mockito.mock(IEventMgr.class));
		pcm.setPersoMgr(Mockito.mock(IInternalPersonalisationManager.class));
		pcm.setUserPrefLearning(Mockito.mock(IC45Learning.class));
		pcm.setCommManager(commManager);
		
		
		setupContextAttributes();
		setupPreferenceCtxAttributes();
		this.createPreference();
		this.setupMocks();
		pcm.initialisePreferenceManagement();	
		this.prefMgr = pcm.getPreferenceManager();
	}
	
	private void setupMocks(){
		List<CtxIdentifier> preferenceEntityList = new ArrayList<CtxIdentifier>();
		preferenceEntityList.add(preferenceCtxEntity.getId());
		try {
			Mockito.when(commManager.getIdManager()).thenReturn(idMgr);
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(preferenceEntityList));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) this.preferenceCtxEntity.getId(), preferenceKey1)).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceAttribute1));
			Mockito.when(ctxBroker.update(preferenceAttribute1)).thenReturn(new AsyncResult<CtxModelObject>(preferenceAttribute1));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) this.preferenceCtxEntity.getId(), preferenceKey2)).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceAttribute2));
			Mockito.when(ctxBroker.update(preferenceAttribute2)).thenReturn(new AsyncResult<CtxModelObject>(preferenceAttribute2));
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxModelTypes.PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.mockId)).thenReturn(new AsyncResult<IndividualCtxEntity>(this.ctxEntity));
			Mockito.when(ctxBroker.createAttribute(ctxEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceRegistryAttribute));
			Mockito.when(ctxBroker.update(this.preferenceRegistryAttribute)).thenReturn(new AsyncResult<CtxModelObject>(this.preferenceRegistryAttribute));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void Test(){
		preferenceDetails = new PreferenceDetails("", this.serviceID, this.preferenceName);
		Assert.assertEquals(true, this.prefMgr.storePreference(mockId, preferenceDetails, this.preference));
		
		IPreferenceTreeModel preferenceModel = this.prefMgr.getModel(mockId, preferenceDetails);
		Assert.assertNotNull(preferenceModel);
		Assert.assertEquals(preferenceModel.getRootPreference(), this.preference);
		
		
	}
	
	
	
	private void createPreference(){
		
		try {
			this.serviceID = new ServiceResourceIdentifier();
			this.serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
			this.serviceID.setServiceInstanceIdentifier("MediaPlayer");
			
			
			PreferenceTreeNode outcomePreference = new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", this.preferenceName , "10")); 
			PreferenceTreeNode symlocConditionPreference = new PreferenceTreeNode(new ContextPreferenceCondition(this.locationAttribute.getId(), OperatorConstants.EQUALS, "home", this.locationAttribute.getType()));
			symlocConditionPreference.add(outcomePreference);
			PreferenceTreeNode outcomePreference2 = new PreferenceTreeNode(new PreferenceOutcome(this.serviceID, "", this.preferenceName, "0"));
			PreferenceTreeNode statusConditionPreference = new PreferenceTreeNode(new ContextPreferenceCondition(statusAttribute.getId(), OperatorConstants.EQUALS, "busy", statusAttribute.getType()));
			statusConditionPreference.add(outcomePreference2);
			preference = new PreferenceTreeNode();
			preference.add(statusConditionPreference);
			preference.add(symlocConditionPreference);
			System.out.println(preference.toTreeString());
			
			Assert.assertEquals(preference.getChildCount(), 2);
			PreferenceTreeNode newConditionPreference = new PreferenceTreeNode(new ContextPreferenceCondition(locationAttribute.getId(), OperatorConstants.EQUALS, "work", locationAttribute.getType()));
			
			statusConditionPreference.removeFromParent();
			
			Enumeration<IPreference> prefEnum = preference.children(); 
			while (prefEnum.hasMoreElements()){
				Assert.assertNotSame(prefEnum.nextElement(), statusConditionPreference);
			}
			newConditionPreference.add(statusConditionPreference);
			
			preference.add(newConditionPreference);
			
			System.out.println(preference.toTreeString());
			Assert.assertEquals(preference.getChildCount(), 2);
			
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail("Could not create preference");
		}
		
		
		
	}

	private void setupContextAttributes() {
		mockId = new MockIdentity(IdentityType.CSS, "myId", "domain");
		CtxEntityIdentifier ctxEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PERSON, new Long(1));
		ctxEntity = new IndividualCtxEntity(ctxEntityId);	
		
		/*
		 * preference CtxEntity:
		 */
		
		CtxEntityIdentifier preferenceCtxEntityId = new CtxEntityIdentifier(mockId.getJid(), CtxEntityTypes.PREFERENCE, new Long(1));
		preferenceCtxEntity = new CtxEntity(preferenceCtxEntityId);
		
		
		CtxAssociationIdentifier assocID = new CtxAssociationIdentifier(mockId.getJid(), CtxAssociationTypes.HAS_PREFERENCES, new Long(1));
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
	
	private void setupPreferenceCtxAttributes(){
		
		CtxAttributeIdentifier preferenceRegistryAttributeId = new CtxAttributeIdentifier(this.ctxEntity.getId(), CtxModelTypes.PREFERENCE_REGISTRY, new Long(1));
		this.preferenceRegistryAttribute = new CtxAttribute(preferenceRegistryAttributeId);
		
		
		CtxAttributeIdentifier prefAttr1 = new CtxAttributeIdentifier(this.preferenceCtxEntity.getId(), preferenceKey1, new Long(1));
		preferenceAttribute1 = new CtxAttribute(prefAttr1);
		
		CtxAttributeIdentifier prefAttr2 = new CtxAttributeIdentifier(this.preferenceCtxEntity.getId(), preferenceKey2, new Long(1));
		preferenceAttribute2 = new CtxAttribute(prefAttr2);
	}
}
