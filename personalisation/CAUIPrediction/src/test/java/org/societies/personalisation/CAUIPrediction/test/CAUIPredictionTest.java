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
package org.societies.personalisation.CAUIPrediction.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUIPrediction.impl.CAUIPrediction;

//import org.societies.context.broker.impl.InternalCtxBroker;
//import org.societies.context.user.db.impl.UserCtxDBMgr;
//import org.societies.context.userHistory.impl.UserContextHistoryManagement;
//import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;
//import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;



/**
 * CAUIPredictionTest  tests CAUIPrediction methods.
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUIPredictionTest {


	CAUIPrediction prediction = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		/*
		prediction = new CAUIPrediction();
		prediction.setCtxBroker(new InternalCtxBroker());
		prediction.setCauiTaskManager(new CAUITaskManager());
		prediction.setCauiDiscovery(new CAUIDiscovery());
*/
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		prediction = null;
	}


	/**
	 * Test method for {@link org.societies.personalisation.CAUIPrediction#getPrediction(}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Ignore
	@Test
	public void testGetPrediction() {
		
		System.out.println("testGetPrediction ");
		
		ICtxBroker ctxBroker = prediction.getCtxBroker();
		try {
			IndividualCtxEntity operator = ctxBroker.retrieveCssOperator().get();
			
			System.out.println(" Return operator " + operator.getType());
			
			IIdentity identity = new MockIdentity(IdentityType.CSS, "nikos", "societies.org");
			ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
			try {
				serviceId.setIdentifier(new URI("testServiceId"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			IAction action1 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action1);
			IAction action2 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action2);
			IAction action3 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action3);
			IAction action4 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action4);
			// learn model
			IAction action5 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action5);		
			
			IAction action6 = new Action(serviceId, "testService", "volume", "high");
			prediction.getPrediction(identity, action6);		
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
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