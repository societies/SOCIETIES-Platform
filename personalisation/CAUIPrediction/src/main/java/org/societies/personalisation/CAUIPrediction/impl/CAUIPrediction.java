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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.ExecutionException;


/**
 * CAUIPrediction
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUIPrediction implements ICAUIPrediction{

	//CAUIPrediction depends on CauiTaskManager,PersonalisationManager and CtxBroker
	private static final Logger LOG = LoggerFactory.getLogger(CAUIPrediction.class);
	boolean modelExist = false;

	private ICtxBroker ctxBroker;
	private IInternalPersonalisationManager persoMgr;
	private ICAUITaskManager cauiTaskManager;
	private ICAUIDiscovery cauiDiscovery;

	private Boolean enablePrediction = true;  
	private String [] lastActions = null;
	int predictionRequestsCounter = 0;

	public ICAUIDiscovery getCauiDiscovery() {
		System.out.println(this.getClass().getName()+": Return cauiDiscovery");
		return cauiDiscovery;
	}


	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {
		System.out.println(this.getClass().getName()+": Got cauiDiscovery");
		this.cauiDiscovery = cauiDiscovery;
	}


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
		registerForNewUiModelEvent();
	}

	public CAUIPrediction(){

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
		LOG.info("prediction request "+predictionRequestsCounter+" serviceID"+ serviceID+" identity requestor"+ownerID+" userActionType"+userActionType);
		return null;
	}


	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {


		//System.out.println("getPrediction requestor:" + requestor+" action:"+action);
		//System.out.println("modelExists: "+ modelExists+" cauiDiscovery:" +cauiDiscovery);
		LOG.info("prediction request "+predictionRequestsCounter+" action"+ action+" identity requestor"+requestor);
		predictionRequestsCounter = predictionRequestsCounter +1;

		// initiate caui model discovery
		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
		if(modelExist == false && enablePrediction == true && cauiDiscovery != null){
			LOG.info("no model predictionRequestsCounter:" +predictionRequestsCounter);
			if(predictionRequestsCounter >= 4){
				LOG.info("this.cauiDiscovery.generateNewUserModel()");
				this.cauiDiscovery.generateNewUserModel();	
				predictionRequestsCounter = 0;
				//time wait for new model generation
			}
		}

		if(modelExist == true && enablePrediction == true){
			LOG.info("model exists, generateNewUserModel" +modelExist);
			//UIModelBroker setModel = new UIModelBroker(ctxBroker,cauiTaskManager);	
			//setActiveModel(requestor);
			String par = action.getparameterName();
			String val = action.getvalue();
			// add code here for retrieving current context;
			HashMap<String,Serializable> currentContext = new HashMap<String,Serializable>();
			// add current context retrieval code

			//Map<IUserIntentAction, IUserIntentTask> currentActionTask = cauiTaskManager.identifyActionTaskInModel(par, val, currentContext, this.lastActions);
			List<IUserIntentAction> actionsList = cauiTaskManager.retrieveActionsByTypeValue(par, val);
			if(actionsList.size()>0){
				IUserIntentAction currentAction = actionsList.get(0);
				Map<IUserIntentAction,Double> nextActionsMap = cauiTaskManager.retrieveNextActions(currentAction);	
				for(IUserIntentAction nextAction : nextActionsMap.keySet()){
					//Integer confLevel = new Integer(0);
					Double doubleConf = nextActionsMap.get(nextAction);
					nextAction.setConfidenceLevel(doubleConf.intValue());
					results.add(nextAction);
				}			
			}
		}

		return new AsyncResult<List<IUserIntentAction>>(results);
	}

	// this method is not complete
	private IUserIntentAction findNextAction(IUserIntentTask uiTask,IUserIntentAction uiAction ){
		IUserIntentAction actionResult = null;
		/*
		List<IUserIntentAction> actionList = uiTask.getActions();

		int i = 0;
		for(IUserIntentAction action : actionList){
			i++;
			if(action.equals(uiAction) && i+1 < actionList.size()){
				actionResult = actionList.get(i+1);
			}
		}
		 */
		return actionResult;
	}


	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			CtxAttribute contextAttribute) {
		// TODO Auto-generated method stub
		LOG.info("prediction request "+predictionRequestsCounter+" contextAttribute"+ contextAttribute.getId().toString()+" identity requestor"+requestor);
		return null;
	}


	void registerForNewUiModelEvent(){

		if (this.ctxBroker == null) {
			LOG.error("Could not register context event listener: ctxBroker is not available");
			return;
		}

		CtxAttributeIdentifier uiModelAttributeId = null;
		IndividualCtxEntity operator;
		try {
			operator = this.ctxBroker.retrieveCssOperator().get();
			List<CtxIdentifier> ls = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();
			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.CAUI_MODEL).get();
				uiModelAttributeId = attr.getId();
			}
			if (uiModelAttributeId != null){
				this.ctxBroker.registerForChanges(new MyCtxChangeEventListener(),uiModelAttributeId);	
			}		

			LOG.info("registration for context attribute updates of type "+uiModelAttributeId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}


	public void setActiveModel(UserIntentModelData newUIModelData){

		// retrieve model from Context DB
		// set model as active in CauiTaskManager
		if (newUIModelData != null){
			cauiTaskManager.updateModel(newUIModelData);
			modelExist = true;		 
		}
	}


	private class MyCtxChangeEventListener implements CtxChangeEventListener {


		MyCtxChangeEventListener(){
		}

		@Override
		public void onCreation(CtxChangeEvent event) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			LOG.info(event.getId() + ": *** Update event ***");

			CtxIdentifier uiModelAttrID = event.getId();

			if(uiModelAttrID instanceof CtxAttributeIdentifier){
				CtxAttribute uiModelAttr;
				try {
					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(uiModelAttrID).get();
					UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());
					LOG.info("UserIntentModelData "+newUIModelData);
					//	LOG.info("UserIntentModelData matrix"+newUIModelData.getMatrix()+" tasks "+newUIModelData.getTaskList());

					setActiveModel(newUIModelData);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		@Override
		public void onModification(CtxChangeEvent event) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub
		}
	}
}