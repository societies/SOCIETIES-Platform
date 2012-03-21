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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.monitoring.UserPreferenceConditionMonitor;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.springframework.scheduling.annotation.AsyncResult;

public class UserPreferenceManagementTest  {

	UserPreferenceConditionMonitor pcm ;
	IInternalPersonalisationManager persoMgr = Mockito.mock(IInternalPersonalisationManager.class);
	ICtxBroker broker = Mockito.mock(ICtxBroker.class);
	
	private IIdentity mockId;
	@Before
	public void Setup(){
		pcm = new UserPreferenceConditionMonitor();
		persoMgr = new MockPersoMgr();
		pcm.initialisePreferenceManagement(broker, persoMgr);
		mockId = new MyIdentity(IdentityType.CSS, "myId", "domain");
	}

	
	@Test
	public void TestgetOutcomeWithCtxEvent() {
		try{
		CtxEntityIdentifier ctxEntityId = new CtxEntityIdentifier(mockId, "Person", new Long(1));
		CtxEntity ctxEntity = new CtxEntity(ctxEntityId);
		CtxAttributeIdentifier ctxAttrId = new CtxAttributeIdentifier(ctxEntityId, "location", new Long(1));
		CtxAttribute attr = new CtxAttribute(ctxAttrId);
		attr.setStringValue("home");
		
		Mockito.when(broker.retrieve(ctxEntityId)).thenReturn(new AsyncResult(ctxEntity));
		Mockito.when(broker.retrieve(ctxAttrId)).thenReturn(new AsyncResult(attr));
		List<CtxIdentifier> tempEntityList = new ArrayList<CtxIdentifier>();
		tempEntityList.add(ctxEntityId);
		Mockito.when(broker.lookup(CtxModelType.ENTITY, "Person")).thenReturn(new AsyncResult(tempEntityList));
		List<CtxIdentifier> tempAttributeList = new ArrayList<CtxIdentifier>();
		tempAttributeList.add(ctxAttrId);
		Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "location")).thenReturn(new AsyncResult(tempAttributeList));
		
		Callback callback = new Callback();
		pcm.getOutcome(mockId, attr, callback);
		
		
		if (callback.getReturnedOutcome()==null){
			TestCase.fail("Test Failed: getOutcome(Identity arg0, CtxAttribute arg1, IPersonalisationInternalCallback arg2)");
		}else{
			System.out.println("Successful test: getOutcome(Identity arg0, CtxAttribute arg1, IPersonalisationInternalCallback arg2)");
		}
		}catch (CtxException ctxE){
			TestCase.fail();
			ctxE.printStackTrace();
		}
		
		
	}

	@Test
	public void TestgetOutcomeWithActionEvent() {
		
		try {
			IAction action = new Action("volume","10");
			ServiceResourceIdentifier sId = new ServiceResourceIdentifier();
			sId.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
			
			action.setServiceID(sId);
			action.setServiceType("media");
			
			Callback callback = new Callback();
			
			
			pcm.getOutcome(mockId, action, callback);
			
			if (callback.getReturnedOutcome()==null){
				TestCase.fail("Test Failed: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			}else{
				System.out.println("Successful test: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			}
			
		} catch (URISyntaxException e) {
			TestCase.fail("Test Failed: getOutcome(Identity arg0, IAction arg1, IPersonalisationInternalCallback arg2)");
			e.printStackTrace();
		}
		
		
	}

	
}
