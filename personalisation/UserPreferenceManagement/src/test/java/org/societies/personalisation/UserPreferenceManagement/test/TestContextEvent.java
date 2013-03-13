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
package org.societies.personalisation.UserPreferenceManagement.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.UserPreferenceManagement.impl.management.Registry;
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

public class TestContextEvent  {

	UserPreferenceConditionMonitor pcm ;
	IInternalPersonalisationManager persoMgr = Mockito.mock(IInternalPersonalisationManager.class);
	ICtxBroker broker = Mockito.mock(ICtxBroker.class);
	
	private IIdentity mockId;
	private CtxEntityIdentifier ctxEntityId;
	private CtxEntity ctxEntity;
	private CtxEntityIdentifier ctxPrefEntityId;
	private Registry registry;
	private CtxAttribute prefAttribute;
	private PreferenceDetails details;
	private ServiceResourceIdentifier serviceID;
	private IPreference preference;
	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxAttribute locationAttribute;
	
	@Before
	public void Setup(){

		pcm = new UserPreferenceConditionMonitor();
		pcm.setCtxBroker(broker);
		pcm.setEventMgr(Mockito.mock(IEventMgr.class));
		pcm.setPersoMgr(persoMgr);
		pcm.setUserPrefLearning(Mockito.mock(IC45Learning.class));
		pcm.initialisePreferenceManagement();

		mockId = new MyIdentity(IdentityType.CSS, "myId", "domain");
		ctxEntityId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		ctxEntity = new CtxEntity(ctxEntityId);	
		serviceID = new ServiceResourceIdentifier();
		try {
			serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserPreferenceManagement upm = pcm.getPreferenceManager();
		details = new PreferenceDetails();
		details.setPreferenceName("volume");
		details.setServiceID(serviceID);
		details.setServiceType("media");
		this.setupContextAttributes();
		this.setupPreference(details, "100", "10");
		this.setupRegistry(details, preference);
	}

	
	private void setupContextAttributes() {
		ctxLocationAttributeId = new CtxAttributeIdentifier(ctxEntityId, "location", new Long(1));
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");
		
	}


	@Test
	public void TestgetOutcomeWithCtxEvent() {
		try{

		
		
		Mockito.when(broker.retrieve(ctxEntityId)).thenReturn(new AsyncResult(ctxEntity));
		Mockito.when(broker.retrieve(ctxLocationAttributeId)).thenReturn(new AsyncResult(locationAttribute));
		List<CtxIdentifier> tempEntityList = new ArrayList<CtxIdentifier>();
		tempEntityList.add(ctxEntityId);
		Mockito.when(broker.lookup(CtxModelType.ENTITY, "Person")).thenReturn(new AsyncResult(tempEntityList));
		List<CtxIdentifier> tempAttributeList = new ArrayList<CtxIdentifier>();
		tempAttributeList.add(ctxLocationAttributeId);
		Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "location")).thenReturn(new AsyncResult(tempAttributeList));	
		Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "PREFERENCE_REGISTRY")).thenReturn(new AsyncResult(this.registry));
		List<CtxIdentifier> tempPrefEntityIDsList = new ArrayList<CtxIdentifier>();		
		Mockito.when(broker.lookup(CtxModelType.ENTITY, "PREFERENCE")).thenReturn(new AsyncResult(tempPrefEntityIDsList));
		pcm.processServiceStarted(mockId, "media", serviceID);
		
		Future<List<IPreferenceOutcome>> futureOutcomes = pcm.getOutcome(mockId, locationAttribute);
		List<IPreferenceOutcome> outcomes = futureOutcomes.get();
		
		if (outcomes==null){
			TestCase.fail("Test Failed: getOutcome(Identity arg0, CtxAttribute arg1, IPersonalisationInternalCallback arg2)");
		}else{
			System.out.println("Successful test: getOutcome(Identity arg0, CtxAttribute arg1, IPersonalisationInternalCallback arg2)");
		}
		}catch (CtxException ctxE){
			TestCase.fail();
			ctxE.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void setupRegistry(PreferenceDetails detail, IPreference pref){
		
		registry = new Registry();
		try {
		ctxPrefEntityId = new CtxEntityIdentifier(mockId.getJid(), "PREFERENCE", new Long(3));
		CtxEntity ctxPrefEntity = new CtxEntity(ctxPrefEntityId);
		
		String preferenceContextType = registry.getNameForNewPreference();
		CtxAttributeIdentifier ctxPrefAttrId = new CtxAttributeIdentifier(ctxPrefEntityId, preferenceContextType , new Long(4));
		prefAttribute = new CtxAttribute(ctxPrefAttrId);
		
		prefAttribute.setBinaryValue(SerialisationHelper.serialise(pref));
		
		registry.addPreference(detail , ctxPrefAttrId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void setupPreference(PreferenceDetails details, String value, String otherValue){
		IPreferenceOutcome outcome = new PreferenceOutcome(details.getServiceID(), details.getServiceType(), details.getPreferenceName(), value);
		IPreferenceCondition condition = new ContextPreferenceCondition(locationAttribute.getId(), OperatorConstants.EQUALS, "home", locationAttribute.getType());
		IPreferenceOutcome defaultOutcome = new PreferenceOutcome(details.getServiceID(), details.getServiceType(), details.getPreferenceName(), otherValue);
		
		preference = new PreferenceTreeNode();
		IPreference conditionNode = new PreferenceTreeNode(condition);
		
		
		IPreference outcomeNode = new PreferenceTreeNode(outcome);
		conditionNode.add(outcomeNode);
		preference.add(conditionNode);
		IPreference defaultOutcomeNode = new PreferenceTreeNode(defaultOutcome);
		
		preference.add(defaultOutcomeNode);
		
		
		
	
	}
	@Test
	public void TestgetOutcomeWithActionEvent() {
		
		try {
			
			ServiceResourceIdentifier sId = new ServiceResourceIdentifier();
			sId.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
			IAction action = new Action(sId, "media", "volume","10");
			action.setServiceID(sId);
			action.setServiceType("media");			
			
			Future<List<IPreferenceOutcome>> futureOutcomes = pcm.getOutcome(mockId, action);
			List<IPreferenceOutcome> outcomes = futureOutcomes.get();
			
			if (outcomes==null){
				TestCase.fail("Test Failed: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			}else{
				System.out.println("Successful test: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			}
			
		} catch (URISyntaxException e) {
			TestCase.fail("Test Failed: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
