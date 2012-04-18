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
package org.societies.personalisation.CAUIPrediction.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



/**
 * CAUIPrediction
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUIPrediction implements ICAUIPrediction{

	//CAUIPrediction depends on CauiTaskManager,PersonalisationManager and CtxBroker

	private ICtxBroker ctxBroker;
	private IInternalPersonalisationManager persoMgr;
	private ICAUITaskManager cauiTaskManager;

	private Boolean enablePrediction = true;  
	private String [] lastActions = null;


	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName()+": Return ctxBroker");

		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		System.out.println(this.getClass().getName()+": Got ctxBroker");

		this.ctxBroker = ctxBroker;
	}

	public IInternalPersonalisationManager getPersoMgr() {
		System.out.println(this.getClass().getName()+": Return persoMgr");
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		System.out.println(this.getClass().getName()+": Got persoMgr");
		this.persoMgr = persoMgr;
	}

	public ICAUITaskManager getCauiTaskManager() {
		System.out.println(this.getClass().getName()+": Return cauiTaskManager");

		return cauiTaskManager;
	}

	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		System.out.println(this.getClass().getName()+": Got cauiTaskManager");

		this.cauiTaskManager = cauiTaskManager;
	}




	// constructor
	public void initialiseCAUIPrediction(){

	}

	CAUIPrediction(){

	}

	@Override
	public void enablePrediction(Boolean bool) {
		this.enablePrediction = bool;
	}


	@Override
	public List<List<String>> getPredictionHistory() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Future<IUserIntentAction> getCurrentIntentAction(IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String userActionType) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		if(enablePrediction == true){
			//UIModelBroker setModel = new UIModelBroker(ctxBroker,cauiTaskManager);	
			setActiveModel(requestor);
			String par = action.getparameterName();
			String val = action.getvalue();
			// add code here for retrieving current context;
			HashMap<String,Serializable> currentContext = new HashMap<String,Serializable>();
			//IUserIntentAction uiAction = (IUserIntentAction) action;

			Map<IUserIntentAction, IUserIntentTask> currentActionTask = cauiTaskManager.identifyActionTaskInModel(par, val, currentContext, this.lastActions);
			if (currentActionTask != null){
				for(IUserIntentAction uiAction : currentActionTask.keySet()){
					IUserIntentTask uiTask = currentActionTask.get(uiAction);
					IUserIntentAction resultAction = findNextAction(uiTask, uiAction);
					results.add(resultAction);
				}
			}
		}

		return new AsyncResult<List<IUserIntentAction>>(results);
	}

	// this method is not complete
	private IUserIntentAction findNextAction(IUserIntentTask uiTask,IUserIntentAction uiAction ){
		IUserIntentAction actionResult = null;
		List<IUserIntentAction> actionList = uiTask.getActions();

		int i = 0;
		for(IUserIntentAction action : actionList){
			i++;
			if(action.equals(uiAction) && i+1 < actionList.size()){
				actionResult = actionList.get(i+1);
			}
		}

		return actionResult;
	}

	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			CtxAttribute contextAttribute) {
		// TODO Auto-generated method stub
		return null;
	}




	public void setActiveModel(IIdentity requestor){
		// retrieve model from Context DB
		// set model as active in CauiTaskManager
		// until then create and use a fake model
		createFakeModel();
	}

	private void createFakeModel(){
		//create Task A
		IUserIntentAction userActionA = cauiTaskManager.createAction(null,"ServiceType","A-homePc","off");
		IUserIntentAction userActionB = cauiTaskManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionC = cauiTaskManager.createAction(null,"ServiceType","C-homePc","off");
		IUserIntentAction userActionD = cauiTaskManager.createAction(null,"ServiceType","D-homePc","off");

		List<IUserIntentAction> actionList = new ArrayList<IUserIntentAction>();
		actionList.add(0,userActionA);
		actionList.add(1,userActionB);
		actionList.add(2,userActionC);
		actionList.add(3,userActionD);

		Double [][] actionMatrixA  = new Double[actionList.size()][actionList.size()] ;

		for(int i=0; i<actionList.size();i++){
			for (int j=0; j<actionList.size();j++){
				actionMatrixA[i][j] = 0.0  ;
			}
		}

		actionMatrixA[0][1]=1.0;
		actionMatrixA[1][2]=1.0;
		actionMatrixA[2][3]=1.0;

		IUserIntentTask taskA = cauiTaskManager.createTask("TaskA", actionList, actionMatrixA);

		cauiTaskManager.displayTask(taskA);


		//create Task B
		IUserIntentAction userActionE = cauiTaskManager.createAction(null,"ServiceType","A-homePc","on");
		IUserIntentAction userActionF = cauiTaskManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionG = cauiTaskManager.createAction(null,"ServiceType","G-homePc","off");
		//IUserIntentAction userActionH = modelManager.createAction(null,"ServiceType","H-homePc","off");

		List<IUserIntentAction> actionListB = new ArrayList<IUserIntentAction>();
		actionListB.add(0,userActionE);
		actionListB.add(1,userActionF);
		actionListB.add(2,userActionG);
		//actionListB.add(3,userActionH);
		Double [][] actionMatrixB  = new Double[actionListB.size()][actionListB.size()] ;

		for(int i=0; i<actionListB.size();i++){
			for (int j=0; j<actionListB.size();j++){
				actionMatrixB[i][j] = 0.0  ;
			}
		}

		actionMatrixB[0][1]=0.5;
		actionMatrixB[0][2]=0.5;
		actionMatrixB[1][2]=1.0;
		actionMatrixB[2][1]=1.0;
		IUserIntentTask taskB = cauiTaskManager.createTask("TaskB", actionListB, actionMatrixB);
		cauiTaskManager.displayTask(taskB);

		// create model
		List<IUserIntentTask> taskList = new ArrayList<IUserIntentTask>();
		taskList.add(0,taskA);
		taskList.add(1,taskB);

		Double [][] taskMatrix = new Double[taskList.size()][taskList.size()] ;
		for(int i=0; i<taskList.size();i++){
			for (int j=0; j<taskList.size();j++){
				taskMatrix[i][j] = 0.0  ;
			}
		}
		taskMatrix[0][1] = 1.0;

		UserIntentModelData modelData = cauiTaskManager.createModel(taskList, taskMatrix);
		cauiTaskManager.displayModel(modelData);
		cauiTaskManager.updateModel(modelData);
	}

}