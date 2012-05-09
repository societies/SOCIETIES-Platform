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

package org.societies.personalisation.CRISTUserIntentPrediction.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRISTUserIntentPrediction.impl.CRISTUserIntentPrediction;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.CRISTUserIntentTaskManager;

/**
 * This is a JUnit 4 test for CRISTUserIntentPrediction's methods:
 * public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
 *			CtxAttribute ctxAttribute)
 * public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
 *			IAction action)
 * public Future<CRISTUserAction> getCurrentUserIntentAction(
 *			IIdentity ownerID, ServiceResourceIdentifier serviceID, String parameterName)
 * @author Zhiyong YU
 * @created 3-May-2012 7:15:15 PM
 */


public class TestCRISTUserIntentPrediction {

	private ServiceResourceIdentifier serviceID;
	private CRISTUserIntentPrediction cristPredictor;
	private IIdentity myID;
	private CtxAttribute myCtx;
	private IAction myAction;
	
	/*
	@Before
	public void setUp() throws Exception {
		
		cristPredictor = new CRISTUserIntentPrediction();
		CRISTUserIntentTaskManager cristTaskManager = (CRISTUserIntentTaskManager) cristPredictor.getCristTaskManager();
		
		//mock data
		cristTaskManager.initialiseCRISTUserIntentManager();
		//myID = ;//How to construct?
		//serviceID = 
		//CtxEntityIdentifier ctxEntityIdentifier = new CtxEntityIdentifier("UserSurrounding"); 
		//myCtx = new CtxAttribute(new CtxAttributeIdentifier(ctxEntityIdentifier, "Light", 123456789888L));
		//myCtx.setStringValue("30");//dark
		//cristPredictor.enableCRISTPrediction(true);

	}

	
	@Ignore
	@Test
	public void testGetCRISTPredictionIIdentityCtxAttribute() {
		Future<List<CRISTUserAction>> results = cristPredictor
				.getCRISTPrediction(myID, myCtx);
		
		//fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetCRISTPredictionIIdentityIAction() {
		Future<List<CRISTUserAction>> results = cristPredictor
				.getCRISTPrediction(myID, myAction);
		
	}

	@Ignore("Not yet implemented")
	@Test
	public void testGetCurrentUserIntentAction() {
		Future<CRISTUserAction> result = cristPredictor
				.getCurrentUserIntentAction(myID, 
						serviceID, "volume");
		fail("Not yet implemented");
	}
*/
}
