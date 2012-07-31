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
package org.societies.personalisation.CAUITaskManager.impl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;

import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.UIModelObjectNumberGenerator;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;

import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * CAUITaskManager
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManager implements ICAUITaskManager{

	public static final Logger LOG = LoggerFactory.getLogger(CAUITaskManager.class);
	private ICtxBroker ctxBroker;

	//key: sourceAction value: (key:targetAction value:transProb)
	//HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> actionModel = new  HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

	UserIntentModelData activeUserIntentModel = new UserIntentModelData();

	public ICtxBroker getCtxBroker() {
		LOG.debug(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		LOG.debug(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}


	public void initialiseCAUITaskManager(){

	}

	public CAUITaskManager(){
		//UserIntentModelData activeModel = new UserIntentModelData();
	}

/*
	public HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getActionModel() {
		return actionModel;
	}

	public void setActionModel(
			HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> actionModel) {
		this.actionModel = actionModel;
	}
*/

	//******************************************
	// interface implementation
	//******************************************	

	//Action management

	@Override
	public IUserIntentAction createAction(ServiceResourceIdentifier serviceID, String serviceType, String par, String val) {
		
		IUserIntentAction action = new UserIntentAction (serviceID, serviceType, par,val, UIModelObjectNumberGenerator.getNextValue());
		
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		actionsMap.put(action,null);
		model.setActionModel(actionsMap);
		updateModel(model);
		//this.activeModel.setActionModel(this.actionModel);
		return action;
	}

	@Override
	public void setActionLink(IUserIntentAction sourceAction ,IUserIntentAction targetAction, Double transProb){
		
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		if(actionsMap.keySet().contains(sourceAction)){

			HashMap<IUserIntentAction,Double> targetActions2 = null; 
			if (actionsMap.keySet().contains(sourceAction)  && actionsMap.get(sourceAction) == null){
				//System.out.println("1"+sourceAction+" "+transProb);
				targetActions2 = new HashMap<IUserIntentAction,Double>();
				targetActions2.put(targetAction, transProb);    
				actionsMap.put(sourceAction,targetActions2);
			}else if (actionsMap.keySet().contains(sourceAction) && actionsMap.get(sourceAction) != null){
				targetActions2 = actionsMap.get(sourceAction);
				targetActions2.put(targetAction, transProb); 
				actionsMap.put(sourceAction,targetActions2);
			}
		}
		if(!actionsMap.keySet().contains(sourceAction)){
			LOG.debug("Doesn't exists in model, ACTION:"+sourceAction.getActionID());
		}
		model.setActionModel(actionsMap);
		updateModel(model);
	}

	@Override
	public IUserIntentAction retrieveAction(String actID) {
		
		if( actID == null ) throw new NullPointerException("actID can't be null" );
		
		IUserIntentAction actionResult = null;
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getActionID().equals(actID)) actionResult=action;
		}		
		return actionResult;
	}


	@Override
	public List<IUserIntentAction> retrieveActionsByTypeValue(String actionType, String actionValue) {

		if( actionType == null ||actionValue == null) throw new NullPointerException("actionType:"+actionType+" actionValue:"+ actionValue+" can't be null");
		
		
		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getparameterName().equals(actionType) && action.getvalue().equals(actionValue) ) actionResult.add(action);
		}
		return actionResult;
	}

	@Override
	public List<IUserIntentAction> retrieveActionsByServiceTypeValue(String serviceId,String actionType, String actionValue) {

		if(serviceId ==  null || actionType == null ||actionValue == null) throw new NullPointerException("serviceId:"+serviceId + "actionType:"+actionType+" actionValue:"+ actionValue+" can't be null");
		
		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getServiceID().getServiceInstanceIdentifier().equals(serviceId) && action.getparameterName().equals(actionType) && action.getvalue().equals(actionValue) ) actionResult.add(action);
		}
		return actionResult;
	}
	
	@Override
	public List<IUserIntentAction> retrieveActionsByServiceType (String serviceId,String actionType) {

		//if(serviceId ==  null || actionType == null) throw new NullPointerException("serviceId, actionType can't be null");
		if(serviceId ==  null || actionType == null ) throw new NullPointerException("serviceId:"+serviceId + "actionType:"+actionType+" can't be null");
		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();
		UserIntentModelData model = retrieveModel();
		
		if(model == null || model.getActionModel().size() == 0 ) throw new NullPointerException("UserIntentModelData is null");
		
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getServiceID().getServiceInstanceIdentifier().equals(serviceId) && action.getparameterName().equals(actionType)) actionResult.add(action);
		}
		return actionResult;
	}
	
	@Override
	public List<IUserIntentAction> retrieveActionsByType(String actionType) {

		if( actionType == null) throw new NullPointerException("serviceId or actionType can't be null");
		
		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getparameterName().equals(actionType) ) actionResult.add(action);
		}		
		return actionResult;
	}

		
	@Override
	public UserIntentAction retrieveCurrentIntentAction(IIdentity arg0,
			IIdentity arg1, ServiceResourceIdentifier arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Task management
	@Override
	public IUserIntentTask createTask(String taskName, LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction, Double>> actions) {

		IUserIntentTask userTask = new UserIntentTask(taskName,UIModelObjectNumberGenerator.getNextValue(), actions) ;
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentTask, HashMap<IUserIntentTask,Double>> taskMap = model.getTaskModel();
		taskMap.put(userTask,null);
		model.setTaskModel(taskMap);
		updateModel(model);
		
		return userTask;
	}


	@Override
	public IUserIntentTask retrieveTask(String taskID) {
		IUserIntentTask taskResult = null;
		
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentTask, HashMap<IUserIntentTask,Double>> taskMap = model.getTaskModel();
		
		for(IUserIntentTask task : taskMap.keySet()){
			if (task.getTaskID().equals(taskID)) taskResult=task;
		}
		 
		return taskResult;
	}

	@Override
	public boolean taskBelongsToModel(IUserIntentTask arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public boolean actionBelongsToModel(IUserIntentAction arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * need further implementation
	 * 
	 * @see org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager#identifyActionTaskInModel(java.lang.String, java.lang.String, java.util.HashMap, java.lang.String[])
	 */
	
	@Override
	public Map<IUserIntentAction, IUserIntentTask> identifyActionTaskInModel(
			String actionType, String actionValue, HashMap<String, Serializable> context,
			String[] lastAction) {

		Map<IUserIntentAction, IUserIntentTask> results = new HashMap<IUserIntentAction, IUserIntentTask>();
		
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		// !!! add code that identifies task
		for(IUserIntentAction action : actionsMap.keySet()){
			if (action.getparameterName().equals(actionType) && action.getvalue().equals(actionValue) ) results.put(action,null);
		}
	
		return results;
	}

	@Override
	public Map<IUserIntentAction, Double> retrieveNextActions(IUserIntentAction currentAction){
		 
		Map<IUserIntentAction, Double> results = new HashMap<IUserIntentAction, Double>();
		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		if(actionsMap.keySet().contains(currentAction)){
			results = actionsMap.get(currentAction);
		}		 
		return results;
	}

	//*********************************************
	// model management
	//*********************************************

	@Override
	public UserIntentModelData retrieveModel() {
		return activeUserIntentModel;
	}

	@Override
	public void updateModel(UserIntentModelData model) {
		activeUserIntentModel = model;
	}

	@Override
	public UserIntentModelData createModel() {
		activeUserIntentModel =  new UserIntentModelData();
		return activeUserIntentModel;
	}


	//*********************************************
	// visualisation classes
	//*********************************************

	public void displayTask (IUserIntentTask task) {
		/*
		Double [][] matrix = task.getMatrix();
		List<IUserIntentAction> actionList = task.getActions();
		System.out.print(actionList+"\n");
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				System.out.print(" "+matrix[i][j]+"                 ");
			}
			System.out.println();
		}
	*/
	}


	public void displayModel (UserIntentModelData model) {

		/*
		Double [][] matrix = model.getMatrix();
		List<IUserIntentTask> taskList = model.getTaskList();
		System.out.print(taskList+"\n");
		//	System.out.print(matrix+"\n");
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				System.out.print(" "+matrix[i][j]+"                 ");
			}
			System.out.println();
		}
		 */
	}

}