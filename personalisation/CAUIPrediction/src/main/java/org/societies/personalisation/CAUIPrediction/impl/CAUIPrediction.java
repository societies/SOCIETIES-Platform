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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
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
import org.societies.personalisation.common.api.model.PersonalisationTypes;
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
	private ICommManager commsMgr;

	private Boolean enablePrediction = true;  
	private String [] lastActions = null;

	int predictionRequestsCounter = 0;
	int discoveryThreshold = 19;

	public IIdentity identity = null;
	public CtxAttributeIdentifier locationAttrId;
	public CtxAttributeIdentifier statusAttrId;
	public CtxAttributeIdentifier activityAttrId;
	private IIdentity cssOwnerId;


	public CtxAttributeIdentifier getStatusAttrId() {
		return statusAttrId;
	}

	public void setStatusAttrId(CtxAttributeIdentifier statusAttr) {
		this.statusAttrId = statusAttr;
	}

	public CtxAttributeIdentifier getActivityAttrId() {
		return activityAttrId;
	}

	public void setActivityAttrId(CtxAttributeIdentifier activityAttr) {
		this.activityAttrId = activityAttr;
	}

	public CtxAttributeIdentifier getLocationAttrId() {
		return locationAttrId;
	}

	public void setLocationAttrId(CtxAttributeIdentifier locationAttr) {
		this.locationAttrId = locationAttr;
	}


	public IIdentity getIdentity() {
		return identity;
	}


	public void setIdentity(IIdentity identity) {
		this.identity = identity;
	}


	//Services

	public ICAUIDiscovery getCauiDiscovery() {
		LOG.info(this.getClass().getName()+": Return cauiDiscovery");
		return cauiDiscovery;
	}


	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {
		LOG.info(this.getClass().getName()+": Got cauiDiscovery");
		this.cauiDiscovery = cauiDiscovery;
	}


	public ICtxBroker getCtxBroker() {
		LOG.info(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		LOG.info(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}


	public IInternalPersonalisationManager getPersoMgr() {
		LOG.info(this.getClass().getName()+": Return persoMgr");
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		LOG.info(this.getClass().getName()+": Got persoMgr");
		this.persoMgr = persoMgr;
	}


	public ICAUITaskManager getCauiTaskManager() {
		LOG.info(this.getClass().getName()+": Return cauiTaskManager");
		return cauiTaskManager;
	}


	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		LOG.info(this.getClass().getName()+": Got cauiTaskManager");
		this.cauiTaskManager = cauiTaskManager;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		LOG.info(this.getClass().getName()+": Got commsMgr");
		this.commsMgr = commsMgr;
	}

	public ICommManager getCommsMgr() {
		LOG.info(this.getClass().getName()+": Return CommsMgr");
		return commsMgr;
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
		// 
		return null;
	}

	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		LOG.info("getPrediction based on action: "+ action+" identity requestor:"+requestor+" modelExist"+modelExist);
		predictionRequestsCounter = predictionRequestsCounter +1;


		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
		if(cauiDiscovery != null){
			LOG.info("  Model Discovery Counter:" +predictionRequestsCounter);

			// initiate caui model discovery
			if(predictionRequestsCounter >= discoveryThreshold){
				LOG.info("Start CAUI model generation");
				this.cauiDiscovery.generateNewUserModel();	
				predictionRequestsCounter = 0;
			}
		}

		if(modelExist == true && enablePrediction == true){
			//LOG.info("1. model exists " +modelExist);
			LOG.info("START PREDICTION caui modelExist "+modelExist);
			//UIModelBroker setModel = new UIModelBroker(ctxBroker,cauiTaskManager);	
			//setActiveModel(requestor);
			String par = action.getparameterName();
			String val = action.getvalue();
			LOG.info("2. action perf par:"+ par+" action val:"+val);
			//add code here for retrieving current context;

			HashMap<String,Serializable> currentContext = new HashMap<String,Serializable>();
			//Map<IUserIntentAction, IUserIntentTask> currentActionTask = cauiTaskManager.identifyActionTaskInModel(par, val, currentContext, this.lastActions);
			// identify performed action in model
			List<IUserIntentAction> actionsList = cauiTaskManager.retrieveActionsByTypeValue(par, val);
			LOG.info("3. cauiTaskManager.retrieveActionsByTypeValue(par, val) " +actionsList);
			if(actionsList.size()>0){
				IUserIntentAction currentAction = actionsList.get(0);
				LOG.info("4. currentAction " +currentAction);
				Map<IUserIntentAction,Double> nextActionsMap = cauiTaskManager.retrieveNextActions(currentAction);	
				LOG.info("5. nextActionsMap " +nextActionsMap);
				if(nextActionsMap.size()>0){
					for(IUserIntentAction nextAction : nextActionsMap.keySet()){
						Double doubleConf = nextActionsMap.get(nextAction);
						nextAction.setConfidenceLevel(doubleConf.intValue());
						LOG.info("6. nextActionsMap " +nextAction);
						results.add(nextAction);
						LOG.info(" ****** prediction map created "+ results);
					}
				}			
			}
		} else {
			LOG.info("no CAUI model exist yet ");
		}
		//LOG.info(" getPrediction(IIdentity requestor, IAction action) "+ results);
		return new AsyncResult<List<IUserIntentAction>>(results);
	}


	// based on the current identity, serviceid and actionType what is the predicted action value?
	// concider also context data
	// i.e. userActionType:setDestination --> predict value:office

	@Override
	public Future<IUserIntentAction> getCurrentIntentAction(IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String userActionType) {

		LOG.info("getCurrentIntentAction based on identity and serviceID:"+ serviceID+" identity requestor:"+ownerID+" userActionType:"+userActionType);

		IUserIntentAction predictedAction = null;
		if(modelExist == true && enablePrediction == true){
			List<IUserIntentAction> actionList = cauiTaskManager.retrieveActionsByServiceType(serviceID.getServiceInstanceIdentifier(), userActionType);
			LOG.info("action LIST "+actionList );
			// compare current context and choose proper action
			if(actionList.size()>0) predictedAction = findBestMatchingAction(actionList);

		} else {
			LOG.info("no model exist - predictionRequestsCounter:" +predictionRequestsCounter);
		}

		LOG.info("getCurrentIntentAction based on serviceID and actionType : "+predictedAction );
		return new AsyncResult<IUserIntentAction>(predictedAction);
	}


	/*
	 * Identify best matching action according to operator's current context and predicted actions context
	 *	 
	 */	
	private IUserIntentAction findBestMatchingAction(List<IUserIntentAction> actionList){
		IUserIntentAction bestAction = null;

		HashMap<IUserIntentAction, Integer> actionsScoreMap = new HashMap<IUserIntentAction, Integer>();

		CtxAttribute currentLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
		CtxAttribute currentStatus = retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
		CtxAttribute currentTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);

		for(IUserIntentAction action : actionList ){

			HashMap<String,Serializable> actionCtx = action.getActionContext();

			int actionMatchScore = 0;			

			for(String ctxType : actionCtx.keySet()){
				Serializable ctxValue = actionCtx.get(ctxType);

				if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)&& ctxValue instanceof String){
					String actionLocation = (String) ctxValue;
					LOG.info("String context location value :"+ actionLocation);
					if(currentLocation.getStringValue().equals(actionLocation)) actionMatchScore = actionMatchScore +1;

				} else if(ctxType.equals(CtxAttributeTypes.TEMPERATURE) && ctxValue instanceof Integer ){
					Integer actionTemperature= (Integer) ctxValue;
					LOG.info("Integer context temperature value :"+ actionTemperature);
					if(currentTemp.getIntegerValue().equals(actionTemperature)) actionMatchScore = actionMatchScore +1;

				} else if(ctxType.equals(CtxAttributeTypes.STATUS) && ctxValue instanceof String ){
					String actionStatus = (String) ctxValue;
					LOG.info("String context status value :"+ actionStatus);
					if(currentStatus.getStringValue().equals(actionStatus)) actionMatchScore = actionMatchScore +1;
				} else {
					LOG.info("findBestMatchingAction: context type:"+ctxType +" does not match");
				}
			}	
			actionsScoreMap.put(action, actionMatchScore);
		}

		int maxValueInMap=(Collections.max(actionsScoreMap.values()));  // This will return max value in the Hashmap
		for(IUserIntentAction action  : actionsScoreMap.keySet()){
			if(actionsScoreMap.get(action).equals(maxValueInMap)) bestAction = action;
		}

		LOG.info("best action "+bestAction);

		return bestAction;
	}


	//**********************************
	//*** helper contex class 
	//***********************************

	private CtxAttribute retrieveOperatorsCtx(String type){
		CtxAttribute ctxAttr = null;
		try {
			IndividualCtxEntity operator = this.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0){
				List<CtxAttribute>  ctxAttrList = new ArrayList<CtxAttribute> (ctxAttrSet);
				ctxAttr = ctxAttrList.get(0);
			}

		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		} catch (CtxException e) {
			// 
			e.printStackTrace();
		}

		return ctxAttr;
	}


	// kalite mono otan allazei to attribute 
	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			CtxAttribute contextAttribute) {

		LOG.info("getPrediction based on attr update  contextAttribute"+ contextAttribute.getId().toString()+" identity requestor"+requestor);
		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		IUserIntentAction action = null;

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
			serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}


		if(modelExist == true && enablePrediction == true){
			//fake prediction

			action = cauiTaskManager.createAction(serviceId, serviceId.getServiceInstanceIdentifier(), "fakeType", "fakeAction");
			results.add(action);		
			LOG.info("modelExist getPrediction(IIdentity requestor,	CtxAttribute contextAttribute) create fake prediction "+ results);
		}
		LOG.info("getPrediction(IIdentity requestor,	CtxAttribute contextAttribute)"+ results);		
		return new AsyncResult<List<IUserIntentAction>>(results);
	}


	@Override
	public void receivePredictionFeedback(IAction action) {

	}

	private void registerForNewUiModelEvent(){

		if (this.ctxBroker == null) {
			LOG.error("Could not register context event listener: ctxBroker is not available");
			return;
		}

		CtxAttributeIdentifier uiModelAttributeId = null;
		IndividualCtxEntity operator;
		try {
			//operator = this.ctxBroker.retrieveCssOperator().get();
			final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			this.cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);

			operator = this.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
			//LOG.info("operator retrieved "+operator);

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

			LOG.info("registration for context attribute updates of type CAUI: "+uiModelAttributeId);
		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		} catch (CtxException e) {
			// 
			e.printStackTrace();
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		}			
	}


	public void setActiveModel(UserIntentModelData newUIModelData){

		// retrieve model from Context DB
		// set model as active in CauiTaskManager
		if (newUIModelData != null){
			cauiTaskManager.updateModel(newUIModelData);
			modelExist = true;		 
			LOG.info("caui model created - actions map: "+newUIModelData.getActionModel());
			LOG.info(" modelExist: "+modelExist);
		}
	}



	private class MyCtxChangeEventListener implements CtxChangeEventListener {


		MyCtxChangeEventListener(){
		}

		@Override
		public void onCreation(CtxChangeEvent event) {

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

					setActiveModel(newUIModelData);

					//TODO register with pers manager for location updates.
					//LOG.info("register with pers manager for ctxAttr location update: "+getLocationAttrId());
					//locationAttrId = initialiseAttrId(CtxAttributeTypes.LOCATION_SYMBOLIC);
					//persoMgr.registerForContextUpdate(getIdentity(), PersonalisationTypes.CAUIIntent, locationAttrId);			

				} catch (InterruptedException e) {

					e.printStackTrace();
				} catch (ExecutionException e) {

					e.printStackTrace();
				} catch (CtxException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (ClassNotFoundException e) {

					e.printStackTrace();
				}	
			}
		}

		@Override
		public void onModification(CtxChangeEvent event) {

		}

		@Override
		public void onRemoval(CtxChangeEvent event) {

		}
	}



	/*
	private CtxAttributeIdentifier initialiseAttrId(String attrType){
		CtxAttributeIdentifier attrid = null;
		IndividualCtxEntity operator;

		try {

			//operator = this.ctxBroker.retrieveCssOperator().get();
			final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();

			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
			operator = this.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();

			Set<CtxAttribute> attrSet = operator.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxAttribute> attrList = new ArrayList<CtxAttribute>(attrSet);
			if (attrList.size()>0){
				CtxAttribute attr = attrList.get(0);
				String locationValue = attr.getStringValue();	
			}

		} catch (InterruptedException e) {
			//
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		} catch (CtxException e) {
			// 
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// 
			e.printStackTrace();
		}

		return attrid;	
	}
	 */

}