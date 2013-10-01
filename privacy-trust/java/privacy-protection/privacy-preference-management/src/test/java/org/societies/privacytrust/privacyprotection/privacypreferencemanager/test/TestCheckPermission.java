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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

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
import org.societies.api.context.model.CtxAttributeValueMetrics;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.CtxQuality;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.context.model.CtxQualityBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestCheckPermission {

	ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	IPrivacyDataManagerInternal  privacyDataManager = Mockito.mock(IPrivacyDataManagerInternal.class);
	ICommManager commsManager = Mockito.mock(ICommManager.class);
	IIdentityManager identityManager = Mockito.mock(IIdentityManager.class);
	PrivacyPreferenceManager privPrefMgr = new PrivacyPreferenceManager();
	IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	public static final String preferenceName1 = "accCtrl_preference_1";
	public static final String preferencename2 = "accCtrl_preference_2";
	IIdentity userId;
	
	
	CtxEntity userCtxEntity;
	CtxAssociation hasPrivacyPreferences;
	CtxEntity privacyPreferenceEntity;
	private CtxAttribute accCtrl_1_CtxAttribute;
	private CtxAttribute registryCtxAttribute;
	private CtxAttribute accCtrl_2_CtxAttribute;
	private CtxAttribute locationAttribute;
	
	
	@Before
	public void setUp(){
		Mockito.when(commsManager.getIdManager()).thenReturn(identityManager);
		privPrefMgr.setCtxBroker(ctxBroker);
		privPrefMgr.setCommsMgr(commsManager);
		privPrefMgr.setprivacyDataManagerInternal(privacyDataManager);
		privPrefMgr.setTrustBroker(trustBroker);
		privPrefMgr.setUserFeedback(userFeedback);
		this.setupContext();
		//Mockito setup
		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(identityManager.getThisNetworkNode()).thenReturn((INetworkNode) userId);
			Mockito.when(this.ctxBroker.retrieve(this.locationAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(locationAttribute));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<CtxAssociation>(this.hasPrivacyPreferences));
			Mockito.when(ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<CtxEntity>(privacyPreferenceEntity));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), preferenceName1)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), preferencename2)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_2_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(registryCtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_2_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_2_CtxAttribute));
			String allow  = "Allow";
			String deny = "Deny";
			List<String> response = new ArrayList<String>();
			response.add(allow);
			Mockito.when(userFeedback.getExplicitFB(Mockito.anyInt(), new ExpProposalContent(Mockito.anyString(), new String[]{allow,deny}))).thenReturn(new AsyncResult<List<String>>(response));
			//Mockito.when(JOptionPane.showConfirmDialog(null, Mockito.eq(Mockito.anyString()),Mockito.eq(Mockito.anyString()), JOptionPane.YES_NO_OPTION )).thenReturn(JOptionPane.YES_OPTION);
			//Mockito.doReturn(JOptionPane.showConfirmDialog(null, Mockito.eq(Mockito.anyString()),Mockito.eq(Mockito.anyString()), JOptionPane.YES_NO_OPTION ));
			
			
			
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		privPrefMgr.initialisePrivacyPreferenceManager();

	}
	
	@Test
	@Ignore
	public void TestRetrievePreferenceEmptyDB(){
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		//test with service requestor
		try {
			this.privPrefMgr.checkPermission(this.getRequestorService(), this.locationAttribute.getId(), actions);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail("Exception occured in checkPermission method with RequestorService as parameter");
		}
		
		
		//test with cis requestor
		try {
			this.privPrefMgr.checkPermission(this.getRequestorCis(), this.locationAttribute.getId(), actions);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TestCase.fail("Exception occured in checkPermission method with RequestorCis as parameter");

		}
	}
	
	private void setupContext(){
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));
		CtxEntityIdentifier preferenceEntityId_accCtrl_1 = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));
		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_accCtrl_1);
		this.accCtrl_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), "accCtrl_preference_1", new Long(5)));
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
		this.accCtrl_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), "accCtrl_preference_2", new Long(6)));
		CtxAttributeIdentifier ctxID = new CtxAttributeIdentifier(userCtxEntity.getId(), "location", new Long(1));
		this.locationAttribute = new CtxAttribute(ctxID);
		this.locationAttribute.setStringValue("home");
		this.locationAttribute.setValueType(CtxAttributeValueType.STRING);
		this.locationAttribute.getQuality().setOriginType(CtxOriginType.SENSED);
		
	}
	
	
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
	

	
}
