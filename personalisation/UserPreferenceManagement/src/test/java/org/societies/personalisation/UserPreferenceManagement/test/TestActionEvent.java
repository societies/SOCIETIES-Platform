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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Learning;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestActionEvent {

	private CtxEntity personEntity;
	private MockIdentity mockId;
	private CtxAttribute statusAttribute;
	private CtxAttribute symLocAttribute;
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private ServiceResourceIdentifier serviceId;
	private final String VOLUME = "volume";
	private IPreference preference;
	UserPreferenceConditionMonitor pcm ;
	private UserPreferenceManagement upm;
	private PreferenceDetails details;
	private final String Service_Type = "media";
	private CtxAttribute preferenceAttribute;
	private CtxEntity preferenceEntity;
	private String uuid = UUID.randomUUID().toString();

	@Ignore
	@Test
	public void TestActionTriggeredPersonalisation(){

		
		try {
			
			/*
			 * case 1
			 */
			this.changeContext("home", "free");
			Future<List<IPreferenceOutcome>> futureOutcomes = pcm.getOutcome(mockId, new Action(this.serviceId, Service_Type, VOLUME, "0"), this.uuid);
			List<IPreferenceOutcome> outcomes = futureOutcomes.get();
			if (outcomes.size()==0){
				Assert.fail("PCM didn't return an outcome");
			}
			if (outcomes.size()>1){
				Assert.fail("PCM returned "+outcomes.size()+" instead of 1");
			}
			IPreferenceOutcome outcome = outcomes.get(0);
	
			if (!outcome.getvalue().equalsIgnoreCase("100")){
				Assert.fail("PCM returned wrong outcome: "+outcome.getvalue()+". Correct outcome is: 100\nSymLoc: "+this.symLocAttribute.getStringValue()+"\tStatus: "+this.statusAttribute.getStringValue());
			}
			
			
			
			
			/*
			 * case 2
			 */
			this.changeContext("work","busy");
			futureOutcomes = pcm.getOutcome(mockId, new Action(this.serviceId, Service_Type, VOLUME, "0"), this.uuid);
			outcomes = futureOutcomes.get();
			if (outcomes.size()==0){
				Assert.fail("PCM didn't return an outcome");
			}
			if (outcomes.size()>1){
				Assert.fail("PCM returned "+outcomes.size()+" instead of 1");
			}
			outcome = outcomes.get(0);
			
			if (!outcome.getvalue().equalsIgnoreCase("0")){
				Assert.fail("PCM returned wrong outcome: "+outcome.getvalue()+". Correct outcome is: 0\nSymLoc: "+this.symLocAttribute.getStringValue()+"\tStatus: "+this.statusAttribute.getStringValue());
			}
			
			
			/*
			 * case 3
			 */
			this.changeContext("", "");
			futureOutcomes = pcm.getOutcome(mockId, new Action(this.serviceId, Service_Type, VOLUME, "0"), this.uuid);
			outcomes = futureOutcomes.get();
			if (outcomes.size()==0){
				Assert.fail("PCM didn't return an outcome");
			}
			if (outcomes.size()>1){
				Assert.fail("PCM returned "+outcomes.size()+" instead of 1");
			}
			outcome = outcomes.get(0);
			
			
			if (!outcome.getvalue().equalsIgnoreCase("50")){
				Assert.fail("PCM returned wrong outcome: "+outcome.getvalue()+". Correct outcome is: 50\nSymLoc: "+this.symLocAttribute.getStringValue()+"\tStatus: "+this.statusAttribute.getStringValue());
			}
	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private boolean matches(IAction action, IAction action2){
		if (action.getServiceID().equals(action2.getServiceID())){
			if (action.getServiceType().equals(action2.getServiceType())){
				if (action.getparameterName().equals(action2.getparameterName())){
					if (action.getvalue().equals(action2.getvalue())){
						return true;
					}
				}
			}
		}
		
		return true;
	}
	@Before
	public void setUp(){
		pcm = new UserPreferenceConditionMonitor();
		pcm.setCtxBroker(ctxBroker);
		pcm.setEventMgr(Mockito.mock(IEventMgr.class));
		pcm.setPersoMgr(Mockito.mock(IInternalPersonalisationManager.class));
		pcm.setUserPrefLearning(Mockito.mock(IC45Learning.class));
		pcm.initialisePreferenceManagement();
		setupContext();
		upm = pcm.getPreferenceManager();
		details = new PreferenceDetails(Service_Type, serviceId, VOLUME);
		upm.storePreference(mockId, details, preference);
		pcm.processServiceStarted(mockId, Service_Type, serviceId);
	}
	
	private void setupContext() {
		this.createPersonEntity();
		this.createSymLocAttribute();
		this.createStatusAttribute();
		this.createServiceID();
		this.createPreference();
		this.setupMockito();
	}


	private void setupMockito() {
		try {
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, "PREFERENCE_REGISTRY")).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			List<CtxIdentifier> prefCtxIdlist = new ArrayList<CtxIdentifier>();
			prefCtxIdlist.add(this.preferenceEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(prefCtxIdlist));
			Mockito.when(ctxBroker.createAttribute((CtxEntityIdentifier) Mockito.eq(preferenceEntity.getId()), Mockito.anyString())).thenReturn(new AsyncResult<CtxAttribute>(this.preferenceAttribute));
			List<CtxIdentifier> personList = new ArrayList<CtxIdentifier>();
			personList.add(this.personEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, "PERSON")).thenReturn(new AsyncResult<List<CtxIdentifier>>(personList));
			Mockito.when(ctxBroker.retrieve(Mockito.eq(personEntity.getId()))).thenReturn(new AsyncResult<CtxModelObject>(personEntity));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(personEntity.getId());
			Mockito.when(ctxBroker.retrieveCssOperator()).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.doNothing().when(ctxBroker.update(this.preferenceAttribute));
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void createStatusAttribute() {
		CtxAttributeIdentifier ctxStatusAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		statusAttribute = new CtxAttribute(ctxStatusAttributeId);
		statusAttribute.setStringValue("free");
		
		
	}

	private void createSymLocAttribute() {
		CtxAttributeIdentifier ctxSymLocationAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		symLocAttribute = new CtxAttribute(ctxSymLocationAttributeId);
		symLocAttribute.setStringValue("home");
	}

	private void createPersonEntity() {
		mockId = new MockIdentity(IdentityType.CSS, "myId", "domain");
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		
	}
	
	private void createServiceID(){
		serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createPreference() {
		IPreferenceOutcome outcome0 = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "0");
		IPreferenceOutcome outcome50  = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "50");
		IPreferenceOutcome outcome100  = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "100");
		System.out.println(this.symLocAttribute.getId().toUriString());
		System.out.println(this.statusAttribute.getId().toUriString());
		
		IPreferenceCondition locationHomeCondition = new ContextPreferenceCondition(this.symLocAttribute.getId(), OperatorConstants.EQUALS, "home", this.symLocAttribute.getType());
		System.out.println(locationHomeCondition.getCtxIdentifier().toUriString());
		IPreferenceCondition locationWorkCondition = new ContextPreferenceCondition(this.symLocAttribute.getId(), OperatorConstants.EQUALS, "work", this.symLocAttribute.getType());
		IPreferenceCondition statusFreeCondition = new ContextPreferenceCondition(this.statusAttribute.getId(), OperatorConstants.EQUALS, "free", this.statusAttribute.getType());
		IPreferenceCondition statusBusyCondition = new ContextPreferenceCondition(this.statusAttribute.getId(), OperatorConstants.EQUALS, "busy", this.statusAttribute.getType());
		
		/*
		 * creating preference:
		 * IF (location==home) AND (status==free)
		 * THEN volume=100
		 * ELSE IF (location==home) AND (status==busy)
		 * THEN volume=50
		 * ELSE IF (location==work) AND (status == free)
		 * THEN volume=50
		 * ELSE IF (location==work) AND (status==busy)
		 * THEN volume=0
		 * ELSE
		 * volume=50
		 */
		
		/*
		 * top node:
		 */
		preference = new PreferenceTreeNode();
		
		/*
		 * condition nodes:
		 */
		PreferenceTreeNode homeLocNode = new PreferenceTreeNode(locationHomeCondition);
		PreferenceTreeNode workLocNode = new PreferenceTreeNode(locationWorkCondition);
		PreferenceTreeNode statusFreeNode = new PreferenceTreeNode(statusFreeCondition);
		PreferenceTreeNode statusBusyNode = new PreferenceTreeNode(statusBusyCondition);
		
		PreferenceTreeNode statusFreeNode1 = new PreferenceTreeNode(statusFreeCondition);
		PreferenceTreeNode statusBusyNode1 = new PreferenceTreeNode(statusBusyCondition);
		/*
		 * IF (location==home) AND (status==free) 
		 * THEN volume=100
		 */
		
		statusFreeNode.add(new PreferenceTreeNode(outcome100));
		homeLocNode.add(statusFreeNode);
		
		/*
		 * ELSE IF (location==home) AND (status==busy)
		 * THEN volume=50
		 */
		statusBusyNode.add(new PreferenceTreeNode(outcome50));
		homeLocNode.add(statusBusyNode);
		
		/*
		 * ELSE IF (location==work) AND (status == free)
		 * THEN volume=50
		 */
		statusFreeNode1.add(new PreferenceTreeNode(outcome50));
		workLocNode.add(statusFreeNode1);
		
		/*
		 * ELSE IF (location==work) AND (status==busy)
		 * THEN volume=0
		 */
		
		statusBusyNode1.add(new PreferenceTreeNode(outcome0));
		workLocNode.add(statusBusyNode1);
		
		/*
		 * ELSE
		 * volume=50
		 */
		this.preference.add(new PreferenceTreeNode(outcome50));
		
		this.preference.add(homeLocNode);
		this.preference.add(workLocNode);
		
		CtxEntityIdentifier ctxPreferenceEntityID = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		preferenceEntity = new CtxEntity(ctxPreferenceEntityID);
		CtxAttributeIdentifier preferenceAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), "PREFERENCE", new Long(1));
		preferenceAttribute = new CtxAttribute(preferenceAttributeId);
		
		System.out.println(preference.toString());
		
	}
	
	
	private void changeContext(String symloc, String status){
		this.symLocAttribute.setStringValue(symloc);
		this.statusAttribute.setStringValue(status);
		this.pcm.getPreferenceManager().getPrivateContextCache().updateCache(symLocAttribute);
		this.pcm.getPreferenceManager().getPrivateContextCache().updateCache(statusAttribute);
	
	}
	
}
