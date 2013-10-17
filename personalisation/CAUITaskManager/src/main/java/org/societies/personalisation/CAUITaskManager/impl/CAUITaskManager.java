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
import java.util.Collections;
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
import org.societies.api.internal.context.model.CtxAttributeTypes;

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
		//LOG.debug(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		//LOG.debug(this.getClass().getName()+": Got ctxBroker");
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

		
		if (serviceID == null) {
			throw new NullPointerException("serviceID can't be null");
		}
		
		if (serviceType == null) {
			throw new NullPointerException("serviceType can't be null");
		}
		
		if (par == null) {
			throw new NullPointerException("action parameter can't be null");
		}
		
		if (val == null) {
			throw new NullPointerException("action value can't be null");
		}
		   
		IUserIntentAction action = new UserIntentAction (serviceID, serviceType, par,val, UIModelObjectNumberGenerator.getNextValue());

		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		actionsMap.put(action,null);
		model.setActionModel(actionsMap);
		updateModel(model);
		//this.activeModel.setActionModel(this.actionModel);
	
		//System.out.println("creating Action:"+ action.getActionID() );
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
			if (LOG.isDebugEnabled())LOG.debug("Doesn't exists in model, ACTION:"+sourceAction.getActionID());
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
			if (action.getServiceID().getIdentifier().toString().equals(serviceId) && action.getparameterName().equals(actionType)) actionResult.add(action);
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

		return false;
	}


	@Override
	public boolean actionBelongsToModel(IUserIntentAction arg0) {


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
		//LOG.debug("model "+ model);

		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();
		//LOG.debug("actionsMap "+ actionsMap);
		//LOG.debug("currentAction "+ currentAction);
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
		this.activeUserIntentModel = new UserIntentModelData(); 
		this.activeUserIntentModel = model;
		if(model!=null){
			LOG.debug("updating active model : "+model.getActionModel() );	
		}

	}

	@Override
	public UserIntentModelData createModel() {
		this.activeUserIntentModel =  new UserIntentModelData();
		return this.activeUserIntentModel;
	}

	@Override
	public HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getCAUIActiveModel(){

		if ( this.activeUserIntentModel != null){
			return this.activeUserIntentModel.getActionModel();
		}
		return null;
	}

	@Override
	public List<IUserIntentAction> retrieveActionsByContext(
			Map<String, Serializable> situationConext) {

		List<IUserIntentAction> actionListResult = new ArrayList<IUserIntentAction>();

		UserIntentModelData model = retrieveModel();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> actionsMap = model.getActionModel();

		List<IUserIntentAction> actionList = new ArrayList<IUserIntentAction>(actionsMap.keySet());
		actionListResult = this.findBestMatchingAction(actionList, situationConext);

		return actionListResult;
	}


	/*
	 * find best matching from the actions contained in list  actionList given the situation context map
	 */
	public List<IUserIntentAction> findBestMatchingAction(List<IUserIntentAction> actionList, Map<String, Serializable> situationConext ){

		List<IUserIntentAction> bestActionList = new ArrayList<IUserIntentAction>();
		HashMap<IUserIntentAction, Integer> actionsScoreMap = new HashMap<IUserIntentAction, Integer>();
		
		//System.out.println("findBestMatchingAction situationConext "+situationConext);
		
		String currentLocationValue = "null";
		String currentDayOfWeekValue = "null";
		Integer currentHourOfDayValue = 0;

		if( situationConext.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null){
			if(situationConext.get(CtxAttributeTypes.LOCATION_SYMBOLIC) instanceof String){
				currentLocationValue = (String) situationConext.get(CtxAttributeTypes.LOCATION_SYMBOLIC);	
			}
		}

		if( situationConext.get(CtxAttributeTypes.DAY_OF_WEEK) != null){
			if(situationConext.get(CtxAttributeTypes.DAY_OF_WEEK) instanceof String){
				currentDayOfWeekValue = (String) situationConext.get(CtxAttributeTypes.DAY_OF_WEEK);	
			}
		}  

		if( situationConext.get(CtxAttributeTypes.HOUR_OF_DAY) != null){
			//System.out.println("situationConext.get(CtxAttributeTypes.HOUR_OF_DAY)");
			if(situationConext.get(CtxAttributeTypes.HOUR_OF_DAY) instanceof Integer){
				
				currentHourOfDayValue = (Integer) situationConext.get(CtxAttributeTypes.HOUR_OF_DAY);
				//System.out.println("situationConext.get(CtxAttributeTypes.HOUR_OF_DAY) currentHourOfDayValue "+currentHourOfDayValue);
			}
		}  


		//System.out.println("currentDayOfWeekValue: "+currentDayOfWeekValue);
		//System.out.println("currentLocationValue: "+currentLocationValue);
		//System.out.println("currentHourOfDayValue: "+currentHourOfDayValue );
		//if(currentHourOfDayValue instanceof Integer )System.out.println("currentHourOfDayValue is integer"); 

		for(IUserIntentAction action : actionList ){

			HashMap<String,Serializable> actionCtx = action.getActionContext();

			//LOG.debug("1 String action :"+ action+" actionCtx:"+actionCtx);

			for(String ctxType : actionCtx.keySet()){
				int actionMatchScore = 0;	
				Serializable ctxValue ;

				if(actionCtx.get(ctxType) != null) 	{

					ctxValue = actionCtx.get(ctxType);

					//LOG.debug("2 String ctxType type :"+ ctxType+" ctxValue:"+ctxValue);

					if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC) && ctxValue instanceof String){
						String actionLocation = (String) ctxValue;
						//LOG.debug("3 actionLocation :"+ actionLocation);
						//System.out.println("3 actionLocation :"+ actionLocation);
						if(currentLocationValue.equalsIgnoreCase(actionLocation)){
							actionMatchScore = actionMatchScore +1;
							//LOG.debug("loc matches :"+ actionMatchScore);
							//System.out.println("loc matches :"+ actionMatchScore);
						}

					}  else if(ctxType.equals(CtxAttributeTypes.HOUR_OF_DAY) && ctxValue instanceof Integer ){

						//LOG.debug("hod matches ctxValue :"+ ctxValue);
						Integer actionHod = (Integer) ctxValue;
						//LOG.debug("hod matches currentHourOfDayValue :"+ currentHourOfDayValue);
						//LOG.debug("hod matches actionHod :"+ actionHod);
						
						if(currentHourOfDayValue.equals(actionHod)) {
							actionMatchScore = actionMatchScore +1;
							//LOG.debug("hod matches :"+ actionMatchScore);
							//System.out.println("hod matches :"+ actionMatchScore);
						}


					}  else if(ctxType.equals(CtxAttributeTypes.DAY_OF_WEEK) && ctxValue instanceof String ){
						String actionDow = (String) ctxValue;
					//	LOG.debug("5 actionDow :"+ actionDow);
						//System.out.println("5 actionDow :"+ actionDow);
						if(currentDayOfWeekValue.equalsIgnoreCase(actionDow)) {
							actionMatchScore = actionMatchScore +1;
						//	LOG.debug("dow matches :"+ actionMatchScore);
						//	System.out.println("dow matches :"+ actionMatchScore);
						}

					} else {
						if (LOG.isDebugEnabled())LOG.debug("findBestMatchingAction: context type:"+ctxType +" does not match");
					}
					//System.out.println("String type :"+ ctxType+" ctxValue:"+ctxValue);
				}


				if(actionsScoreMap.get(action) == null){
					actionsScoreMap.put(action, actionMatchScore);
				} else {
					Integer oldScore =	actionsScoreMap.get(action);
					actionsScoreMap.put(action, oldScore+actionMatchScore);
				}  
				//LOG.debug("actionsScoreMap temp: " +actionsScoreMap);
				//System.out.println("actionsScoreMap temp: " +actionsScoreMap);
			}// end of for loop	

		}
		//System.out.println("actionsScoreMap  " +actionsScoreMap);
		//LOG.debug("actionsScoreMap  " +actionsScoreMap);

		if(!actionsScoreMap.values().isEmpty()){

			// check if no context matches, return empty set
			boolean allValuesZero = true;
			for(Integer value : actionsScoreMap.values()){
				if (value != 0)  allValuesZero = false;
			}
			if(allValuesZero) return bestActionList;

			int maxValueInMap=(Collections.max(actionsScoreMap.values()));  // This will return max value in the Hashmap
			for(IUserIntentAction action  : actionsScoreMap.keySet()){
				if(actionsScoreMap.get(action).equals(maxValueInMap)) {

					if(maxValueInMap == 3 )action.setConfidenceLevel(100);
					if(maxValueInMap == 2 )action.setConfidenceLevel(66);
					if(maxValueInMap == 1 )action.setConfidenceLevel(52);

					bestActionList.add(action);
				}
			}

		} else {
			for(IUserIntentAction action  : actionsScoreMap.keySet()){
				bestActionList.add(action);	
			}
		}


		if (LOG.isDebugEnabled())LOG.debug("best action list: "+bestActionList );
		for(IUserIntentAction action  : bestActionList){
			if (LOG.isDebugEnabled())LOG.debug("action conf level "+action.getConfidenceLevel() );
		}

		return bestActionList;
	}
}
