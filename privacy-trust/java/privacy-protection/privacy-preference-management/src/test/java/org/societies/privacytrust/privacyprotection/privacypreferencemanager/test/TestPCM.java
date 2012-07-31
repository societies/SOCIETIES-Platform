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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
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
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.ppnp.PPNMonitor;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestPCM {


	private CtxAttributeIdentifier ctxResourceId;
	private IIdentity userId;
	private CtxEntity userCtxEntity;
	private CtxAssociation hasPrivacyPreferences;
	private CtxEntity privacyPreferenceEntity;
	private CtxAttribute ppnp_1_CtxAttribute;
	private CtxAttribute ctxResource;
	private PrivacyPreference privacyPreference;
	private PrivacyPreferenceManager privacyPreferenceManager;
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	private ICtxBroker ctxBroker;
	private ITrustBroker trustBroker;
	private MockDataManagerInternal privacyDataManagerInternal;
	private CtxAttribute registryCtxAttribute;
	private PPNMonitor ppnpMonitor;
	private PPNPreferenceDetails details;

	@Before
	public void setup(){
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");

		this.privacyPreferenceManager = new PrivacyPreferenceManager();
		commsMgr = Mockito.mock(ICommManager.class);
		idMgr = Mockito.mock(IIdentityManager.class);
		Mockito.when(commsMgr.getIdManager()).thenReturn(idMgr);

		privacyPreferenceManager.setCommsMgr(commsMgr);
		ctxBroker = Mockito.mock(ICtxBroker.class);
		privacyPreferenceManager.setCtxBroker(ctxBroker);
		privacyDataManagerInternal = new MockDataManagerInternal();
		privacyPreferenceManager.setprivacyDataManagerInternal(privacyDataManagerInternal);
		trustBroker = Mockito.mock(ITrustBroker.class);
		privacyPreferenceManager.setTrustBroker(trustBroker);
		setupContext();
		setupPrivacyPreference();
		try{
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			Mockito.when(ctxBroker.retrieve(ctxResourceId)).thenReturn(new AsyncResult<CtxModelObject>(this.ctxResource));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());

			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAssociation(CtxTypes.HAS_PRIVACY_PREFERENCES)).thenReturn(new AsyncResult<CtxAssociation>(this.hasPrivacyPreferences));
			Mockito.when(ctxBroker.createEntity(CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<CtxEntity>(privacyPreferenceEntity));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), "ppnp_preference_1")).thenReturn(new AsyncResult<CtxAttribute>(ppnp_1_CtxAttribute));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
		}
		catch(CtxException e){

		}
		privacyPreferenceManager.initialisePrivacyPreferenceManager();
		privacyPreferenceManager.storePPNPreference(details, privacyPreference);
		this.ppnpMonitor = privacyPreferenceManager.getPCM().getPpnMonitor();



	}


	@Test
	public void testPCM(){

		CtxChangeEvent ctxEvent;
		ctxEvent = new CtxChangeEvent(ctxResourceId);
		this.ppnpMonitor.onModification(ctxEvent);
		ResponseItem rItem1 = this.privacyDataManagerInternal.getResponseItem(0);
		TestCase.assertNotNull(rItem1);
		TestCase.assertEquals(rItem1.getDecision(), Decision.DENY);

		ctxResource.setStringValue("work");
		
		this.privacyPreferenceManager.getContextCache().getContextCacheUpdater().onModification(ctxEvent);
		this.ppnpMonitor.onModification(ctxEvent);
		
		ResponseItem rItem2 = this.privacyDataManagerInternal.getResponseItem(1);
		TestCase.assertNotNull(rItem2);
		
		TestCase.assertEquals(rItem2.getDecision(), Decision.PERMIT);
		
	}


	private void setupPrivacyPreference(){

		details = new PPNPreferenceDetails(this.ctxResourceId.getType());
		details.setAffectedCtxID(ctxResourceId);
		RuleTarget targetBLOCK;
		List<Requestor> subjects = new ArrayList<Requestor>();
		subjects = new ArrayList<Requestor>();
		subjects.add(this.getRequestorService());
		Resource resource = new Resource(ctxResourceId);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.READ));
		targetBLOCK = new RuleTarget(subjects, resource, actions);
		List<Condition> conditions = new ArrayList<Condition>();

		RuleTarget targetALLOW;
		targetALLOW = new RuleTarget(subjects, resource, actions);
		try {
			PPNPOutcome outcomeBLOCK = new PPNPOutcome(PrivacyOutcomeConstants.BLOCK, targetBLOCK, conditions);

			PrivacyPreference blockPreference = new PrivacyPreference(outcomeBLOCK);

			ContextPreferenceCondition conditionHome = new ContextPreferenceCondition(ctxResourceId, OperatorConstants.EQUALS, "home");

			PrivacyPreference homeConditionPreference = new PrivacyPreference(conditionHome);

			homeConditionPreference.add(blockPreference);

			PPNPOutcome outcomeALLOW = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, targetALLOW, conditions);

			PrivacyPreference allowPreference = new PrivacyPreference(outcomeALLOW);

			ContextPreferenceCondition conditionWork = new ContextPreferenceCondition(ctxResourceId, OperatorConstants.EQUALS, "work");

			PrivacyPreference workConditionPreference = new PrivacyPreference(conditionWork);

			workConditionPreference.add(allowPreference);

			privacyPreference = new PrivacyPreference();
			privacyPreference.add(workConditionPreference);
			privacyPreference.add(homeConditionPreference);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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


	private void setupContext() {
		//entity Person
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);

		//Attribute symbolic location - used both as a resource as well as a condition in the preference
		this.ctxResourceId = new CtxAttributeIdentifier(ctxId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(4));
		this.ctxResource = new CtxAttribute(ctxResourceId);
		this.ctxResource.setStringValue("home");

		//hasPrivacyPreferences association
		this.hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));

		//entity PRIVACY_PREFERENCE 
		CtxEntityIdentifier preferenceEntityId_ppnp1 = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));
		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_ppnp1);

		//attribute ppnp_preference_1 (normally generated dynamically)
		this.ppnp_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), "ppnp_preference_1", new Long(5)));

		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));

	}
}
