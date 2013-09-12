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
package org.societies.personalisation.CAUITaskManager.test;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

/**
 * CAUITaskManagerTest  tests CAUITaskManager methods.
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManagerTest {


	ICAUITaskManager modelManager;
	String taskID = "";
	
	CAUITaskManagerTest(){
		modelManager = new CAUITaskManager();
		createModel();
		retrieveTests();
	}


	private void  retrieveTests(){

		IUserIntentAction retrievedAction = modelManager.retrieveAction("css://nikosk@societies.org/HelloEarth#A-homePc=off/0");
		System.out.println("retrievedAction "+ retrievedAction.getparameterName()+" "+retrievedAction.getvalue());

		List<IUserIntentAction> resultsType = modelManager.retrieveActionsByType("A-homePc");
		System.out.println("getActionsByType(homePC) " + resultsType);

		List<IUserIntentAction> resultsTypeValue = modelManager.retrieveActionsByTypeValue("A-homePc","on");
		System.out.println("getActionsByType(homePC,on) " + resultsTypeValue);

		List<IUserIntentAction> resultsTypeValue2 = modelManager.retrieveActionsByTypeValue("A-homePc","off");
		System.out.println("getActionsByType(homePC,off) " + resultsTypeValue2);

		UserIntentModelData model = modelManager.retrieveModel();
		System.out.println(model.getActionModel());
		System.out.println(model.getTaskModel());
		
		IUserIntentTask task = modelManager.retrieveTask(taskID);
		System.out.println("task retrieved:"+task);
		System.out.println("task retrieved:"+task.getActions());
		//modelManager.identifyActionTaskInModel("A-homePc", "on", null,null);
		modelManager.retrieveNextActions(retrievedAction);
		System.out.println("next action : " +modelManager.retrieveNextActions(retrievedAction));
		
	}

	private void createModel(){

		
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));
			serviceId.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//create Task A
		IUserIntentAction userActionA = modelManager.createAction(serviceId,"ServiceType","A-homePc","off");
		
		HashMap<String,Serializable> contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"earth");
		contextMap.put(CtxAttributeTypes.STATUS,"free");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionA.setActionContext(contextMap);
		
		IUserIntentAction userActionB = modelManager.createAction(serviceId,"ServiceType","B-tv","on");
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"moon");
		contextMap.put(CtxAttributeTypes.STATUS,"free");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionB.setActionContext(contextMap);
		
		IUserIntentAction userActionC = modelManager.createAction(serviceId,"ServiceType","C-radio","mute");
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"mars");
		contextMap.put(CtxAttributeTypes.STATUS,"free");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionC.setActionContext(contextMap);
		
		modelManager.setActionLink(userActionA, userActionB, 0.82);
		modelManager.setActionLink(userActionB, userActionC, 0.42);
		modelManager.setActionLink(userActionA, userActionC, 0.18);
	
		LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction, Double>> actions = new LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();
		
		HashMap<IUserIntentAction, Double> targetActionsMapActionA = new HashMap<IUserIntentAction, Double>();
		targetActionsMapActionA.put(userActionB, 0.82);
		targetActionsMapActionA.put(userActionC, 0.18);
		actions.put(userActionA,targetActionsMapActionA);
		
		HashMap<IUserIntentAction, Double> targetActionsMapActionB = new HashMap<IUserIntentAction, Double>();
		targetActionsMapActionB.put(userActionC, 0.82);
		actions.put(userActionB,targetActionsMapActionB);
		
		//actions.put(userActionA, userActionB, 0.82)
		
		IUserIntentTask task = modelManager.createTask("TaskA", actions);
	    taskID = task.getTaskID() ;
	}

	public static void main(String[] args) {
		new CAUITaskManagerTest();
	}
}
