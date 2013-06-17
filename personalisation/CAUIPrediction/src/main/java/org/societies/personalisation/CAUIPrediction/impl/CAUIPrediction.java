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
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
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

	private static final Logger LOG = LoggerFactory.getLogger(CAUIPrediction.class);
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); 
	// to define a dedicated Logger
	IPerformanceMessage m; 
	IPerformanceMessage m2;

	private ICtxBroker ctxBroker;
	private IInternalPersonalisationManager persoMgr;
	private ICAUITaskManager cauiTaskManager;

	private ICAUITaskManager caciTaskManager; 

	private ICAUIDiscovery cauiDiscovery;
	private ICommManager commsMgr;

	private Boolean enableCauiPrediction = true;  

	// maintains the last 100 actions
	private List<IAction> lastMonitoredActions = new ArrayList<IAction>();
	private List<IUserIntentAction> lastPredictedActions = new ArrayList<IUserIntentAction>();

	int predictionRequestsCounter = 0;
	int discoveryThreshold = 6;
	public boolean cauiModelExist = false;


	// caci variables
	public static boolean enableCACIPrediction = true;
	public static boolean  caciModelExist = false;
	//boolean caciFreshness = true;

	protected CtxAttribute currentCaciModelAttr;
	CACIPrediction caciPredictor = null;

	private IIdentity cssOwnerId;
	private CtxEntityIdentifier operatorEntId;

	public UserIntentModelData currentUIModelData;


	// constructor
	public void initialiseCAUIPrediction(){
		LOG.debug("CAUIPrediction initialised");
		LOG.debug("registerForNewUiModelEvent");

		retrieveModelDB();
		registerForNewUiModelEvent();

		LOG.debug( "caci manager: "+ this.caciTaskManager );

		// TODO get new instance of task manager service
		this.caciPredictor = new CACIPrediction(this.ctxBroker, this.caciTaskManager, this.commsMgr);


		try {
			LOG.debug("register for cis join and new community model creation");
			new CommunityJoinMonitor(this.ctxBroker ,this.commsMgr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CAUIPrediction(){

	}


	@Override
	public void enablePrediction(Boolean bool) {
		this.enableCauiPrediction = bool;
	}

	//TODO add to interface
	public void enableCACIPrediction(Boolean bool) {
		enableCACIPrediction = bool;
	}

	public void setCaciModelExist(Boolean bool) {
		caciModelExist = bool;
	}


	@Override
	public List<List<String>> getPredictionHistory() {
		// 
		return null;
	}

	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		long startTime = System.currentTimeMillis();

		predictionRequestsCounter = predictionRequestsCounter +1;
		this.recordMonitoredAction(action);


		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
		if(cauiDiscovery != null){
			LOG.info("  Model Discovery Counter:" +predictionRequestsCounter);

			// initiate caui model discovery
			if(predictionRequestsCounter >= discoveryThreshold){
				//LOG.info("Start CAUI model generation");
				this.cauiDiscovery.generateNewUserModel();	
				predictionRequestsCounter = 0;
			}
		}

		if(cauiModelExist == true && enableCauiPrediction == true){

			//LOG.info("1. model exists " +modelExist);
			//LOG.info("START PREDICTION caui modelExist "+modelExist);
			//UIModelBroker setModel = new UIModelBroker(ctxBroker,cauiTaskManager);	
			//setActiveModel(requestor);
			String par = action.getparameterName();
			String val = action.getvalue();
			//LOG.info("2. action perf par:"+ par+" action val:"+val);
			//add code here for retrieving current context;



			// identify performed action in model
			List<IUserIntentAction> actionsList = cauiTaskManager.retrieveActionsByTypeValue(par, val);
			LOG.debug("3. cauiTaskManager.retrieveActionsByTypeValue(par, val) " +actionsList);

			if(actionsList.size()>0){

				// improve this to also use context for action identification
				//IUserIntentAction currentAction = actionsList.get(0);

				IUserIntentAction currentAction = findBestMatchingAction(actionsList);

				LOG.debug("4. currentAction " +currentAction);
				Map<IUserIntentAction,Double> nextActionsMap = cauiTaskManager.retrieveNextActions(currentAction);	
				//LOG.info("5. nextActionsMap " +nextActionsMap);

				// no context
				if(nextActionsMap.size()>0){
					for(IUserIntentAction nextAction : nextActionsMap.keySet()){
						Double doubleConf = nextActionsMap.get(nextAction);
						//doubleConf = doubleConf*100;
						doubleConf = 70.0;
						nextAction.setConfidenceLevel(doubleConf.intValue());
						//LOG.info("6. nextActionsMap " +nextAction);
						results.add(nextAction);
						this.predictionConfLevelPerformanceLog(nextAction.getConfidenceLevel());
						//LOG.info(" ****** prediction map created "+ results);
					}
				}			
			}
		} else if(enableCACIPrediction == true && caciModelExist == true) {
			LOG.info("no CAUI model exist ... utilize community model ");

			results = this.caciPredictor.getPrediction(requestor, action);

		} else LOG.info("neither caci, nor caui are able to perform prediction");
		//LOG.info(" getPrediction(IIdentity requestor, IAction action) "+ results);


		if(results.size()>0){
			for(IUserIntentAction predAction : results){
				this.recordPrediction(predAction);		
			}

			long endTime = System.currentTimeMillis();
			this.predictionPerformanceLog(endTime-startTime);

		}

		LOG.info("getPrediction based on action: "+ action+" identity requestor:"+requestor+" results:"+results);
		return new AsyncResult<List<IUserIntentAction>>(results);
	}





	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor, CtxAttribute contextAttribute) {

		LOG.debug("getPrediction based on attr update  contextAttribute"+ contextAttribute.getId().toString()+" identity requestor"+requestor);
		LOG.debug("attr string value "+contextAttribute.getStringValue() );
		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		long startTime = System.currentTimeMillis();

		CtxAttribute currentLocation = null;
		CtxAttribute currentStatus = null; 
		retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
		//CtxAttribute currentLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
		if(contextAttribute.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			currentLocation = contextAttribute;					 
		} else {
			currentLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
		}

		if(contextAttribute.getType().equals(CtxAttributeTypes.STATUS)){
			currentStatus = contextAttribute;					 
		} else {
			currentStatus = retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
		}


		//CtxAttribute currentTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);

		Map<String,Serializable> situationContext = new HashMap<String,Serializable>(); 

		if(currentLocation != null) {
			if(currentLocation.getStringValue() != null) situationContext.put(CtxAttributeTypes.LOCATION_SYMBOLIC, currentLocation.getStringValue());
		}

		if(currentStatus != null) {
			if(currentStatus.getStringValue() != null) situationContext.put(CtxAttributeTypes.STATUS, currentStatus.getStringValue());
		}

		LOG.debug("situationContext :"+ situationContext);

		if(cauiTaskManager.retrieveActionsByContext(situationContext) != null ) {
			results = cauiTaskManager.retrieveActionsByContext(situationContext);

			// prediction performance log
			long endTime = System.currentTimeMillis();
			this.predictionPerformanceLog(endTime-startTime);
			for(IUserIntentAction action : results) {
				int confLevel = action.getConfidenceLevel();
				this.predictionConfLevelPerformanceLog(confLevel);	
			}

		}

		LOG.info("action prediction based on ctx update :"+ results);		

		return new AsyncResult<List<IUserIntentAction>>(results);
	}


	/*
	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			CtxAttribute contextAttribute) {

		//LOG.info("getPrediction based on attr update  contextAttribute"+ contextAttribute.getId().toString()+" identity requestor"+requestor);
		//LOG.info("attr string value "+contextAttribute.getStringValue() );

		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
		IAction lastAction = null;
		// TODO use last predicted or performed action?
		// if performed actions also trigger prediction... so no use ?
		IUserIntentAction lastPredictedAction = null;

		if(lastMonitoredActions.size()>0){
			lastAction = lastMonitoredActions.get(lastMonitoredActions.size()-1);	
			//LOG.info("getPrediction: lastAction "+lastAction.getparameterName() +" value "+lastAction.getvalue() );

		}

		if(lastAction != null && modelExist == true && enablePrediction == true){
			String par = lastAction.getparameterName();
			String val = lastAction.getvalue();
			// identify performed action in model
			List<IUserIntentAction> actionsList = cauiTaskManager.retrieveActionsByTypeValue(par, val);

			if(actionsList.size()>0){
				// improve this to also use context for action identification
				IUserIntentAction currentAction = actionsList.get(0);
				Map<IUserIntentAction,Double> nextActionsMap = cauiTaskManager.retrieveNextActions(currentAction);	

				//LOG.info(" getPrediction(IIdentity requestor,CtxAttribute contextAttribute): "+nextActionsMap);

				if(nextActionsMap.size()>0){
					for(IUserIntentAction nextAction : nextActionsMap.keySet()){
						Double doubleConf = nextActionsMap.get(nextAction);
						//doubleConf = doubleConf*100;
						doubleConf = 50.0;
						nextAction.setConfidenceLevel(doubleConf.intValue());
						LOG.info(" conf level in caui pred: "+doubleConf.intValue());
						results.add(nextAction);
					}
				}			
			}
		} else if(lastAction == null && modelExist == true && enablePrediction == true ){
			LOG.info("can not perform secure prediction");
		}



		LOG.info("ctx update based action prediction:"+ results);		
		return new AsyncResult<List<IUserIntentAction>>(results);
	}
	 */
	// based on the current identity, serviceid and actionType what is the predicted action value?
	// consider also context data
	// i.e. userActionType:setDestination --> predict value:office

	@Override
	public Future<IUserIntentAction> getCurrentIntentAction(IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String userActionType) {

		//	LOG.info("getCurrentIntentAction based on identity and serviceID:"+ serviceID.getServiceInstanceIdentifier() +" identity requestor:"+ownerID+" userActionType:"+userActionType);
		long startTime = System.currentTimeMillis();

		IUserIntentAction predictedAction = null;
		if(cauiModelExist == true && enableCauiPrediction == true){
			//LOG.info("serviceID.getIdentifier().toString() "+serviceID.getIdentifier().toString() );
			List<IUserIntentAction> actionList = cauiTaskManager.retrieveActionsByServiceType(serviceID.getIdentifier().toString(), userActionType);
			//LOG.info("action LIST "+actionList );
			// compare current context and choose proper action
			if(actionList.size()>0) {
				predictedAction = findBestMatchingAction(actionList);

			}

		} else {
			LOG.info("no model exist - predictionRequestsCounter:" +predictionRequestsCounter);
		}

		if(predictedAction != null)	{
			long endTime = System.currentTimeMillis();

			this.predictionPerformanceLog(endTime - startTime);
			this.predictionConfLevelPerformanceLog(predictedAction.getConfidenceLevel());
			this.recordPrediction(predictedAction);		

		}
		//LOG.info("getCurrentIntentAction based on serviceID and actionType : "+predictedAction );
		return new AsyncResult<IUserIntentAction>(predictedAction);
	}


	//the list should also consider the perso feedback  
	private void recordPrediction(IUserIntentAction predAction){

		//LOG.info("predicted actions log: " +this.lastPredictedActions);

		if(this.lastPredictedActions.size()>100){
			this.lastPredictedActions.remove(0);
		}

		this.lastPredictedActions.add(predAction);
		//	LOG.info("store predicted action in log: " +predAction);
		//LOG.info("predicted actions log: " +this.lastPredictedActions);
	}


	//the list should also consider the perso feedback  
	private void recordMonitoredAction(IAction action){

		if(this.lastMonitoredActions.size()>100){
			this.lastMonitoredActions.remove(0);
		}
		this.lastMonitoredActions.add(action);
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
		//CtxAttribute currentTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);

		for(IUserIntentAction action : actionList ){

			HashMap<String,Serializable> actionCtx = action.getActionContext();
			int actionMatchScore = 0;

			if( actionCtx != null ){

				actionMatchScore = 0;			

				for(String ctxType : actionCtx.keySet()){
					Serializable ctxValue = actionCtx.get(ctxType);
					if( ctxValue != null){
						if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)&& ctxValue instanceof String){
							String actionLocation = (String) ctxValue;
							//	LOG.info("String context location value :"+ actionLocation);
							if(currentLocation != null){
								if(currentLocation.getStringValue() != null){

									if(currentLocation.getStringValue().equals(actionLocation)) actionMatchScore = actionMatchScore +1;	
								}
							}					

						}
						/*else if(ctxType.equals(CtxAttributeTypes.TEMPERATURE) && ctxValue instanceof Integer ){
					Integer actionTemperature= (Integer) ctxValue;
					LOG.info("Integer context temperature value :"+ actionTemperature);
					if(currentTemp.getIntegerValue().equals(actionTemperature)) actionMatchScore = actionMatchScore +1;
					}*/
						else if(ctxType.equals(CtxAttributeTypes.STATUS) && ctxValue instanceof String ){
							String actionStatus = (String) ctxValue;
							//LOG.info("String context status value :"+ actionStatus);
							if(currentStatus != null ){
								if(currentStatus.getStringValue() != null){
									if( currentStatus.getStringValue().equals(actionStatus)) actionMatchScore = actionMatchScore +1;	
								}
							}
						} else {
							LOG.debug("findBestMatchingAction: context type:"+ctxType +" does not match");
						}
					} 
				}

				actionsScoreMap.put(action, actionMatchScore);
				//System.out.println("actionsScoreMap  " +actionsScoreMap);
			}
		}

		int maxValueInMap=(Collections.max(actionsScoreMap.values()));  // This will return max value in the Hashmap
		for(IUserIntentAction action  : actionsScoreMap.keySet()){

			if(actionsScoreMap.get(action).equals(maxValueInMap)) bestAction = action;
		}

		//LOG.info("best action "+bestAction);

		return bestAction;
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

			operator = this.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			//LOG.info("operator retrieved "+operator);
			operatorEntId = operator.getId();


			///register for caui model
			List<CtxIdentifier> ls = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();

			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(operator.getId(), CtxAttributeTypes.CAUI_MODEL).get();
				uiModelAttributeId = attr.getId();
			}

			if (uiModelAttributeId != null){

				if(uiModelAttributeId instanceof CtxAttributeIdentifier){
					CtxAttribute uiModelAttr;

					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(uiModelAttributeId).get();

					// this is used in case of reboot and model already exist in db
					if(uiModelAttr.getBinaryValue() != null){
						UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());
						setCAUIActiveModel(newUIModelData);	
					}
				}
				this.ctxBroker.registerForChanges(new MyCtxUIModelChangeEventListener(),uiModelAttributeId);	

			}		

			///register for caci model
			List<CtxIdentifier> lsCaci = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, "CAUI_CACI_MODEL").get();

			CtxAttributeIdentifier caciModelAttributeId = null;


			if (lsCaci.size()>0) {
				caciModelAttributeId = (CtxAttributeIdentifier) lsCaci.get(0);
			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(operator.getId(), "CAUI_CACI_MODEL").get();
				caciModelAttributeId = attr.getId();
			}

			if (caciModelAttributeId != null){

				if(caciModelAttributeId instanceof CtxAttributeIdentifier){
					CtxAttribute caciModelAttr;

					caciModelAttr = (CtxAttribute) ctxBroker.retrieve(caciModelAttributeId).get();
					// this is used in case of reboot and model already exist in db
					if(caciModelAttr.getBinaryValue() != null){
						UserIntentModelData newCaciModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciModelAttr.getBinaryValue(), this.getClass().getClassLoader());
						setCACIActiveModel(newCaciModelData);	
					}
				}
				this.ctxBroker.registerForChanges(new MyCtxCACIIModelChangeEventListener(),caciModelAttributeId);	

			}		

			//LOG.info("registration for context attribute updates of type CAUI: "+uiModelAttributeId);
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
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}			
	}

	public void setCAUIActiveModel(UserIntentModelData newUIModelData){

		if (newUIModelData != null){
			cauiTaskManager.updateModel(newUIModelData);
			cauiModelExist = true;		 
			this.currentUIModelData = newUIModelData;
			LOG.info("caui model set - actions map: "+newUIModelData.getActionModel());
		}
	}




	public void setCACIActiveModel (UserIntentModelData newUIModelData){

		if (newUIModelData != null){
			this.caciPredictor.setCACIActiveModel(newUIModelData);
			LOG.info("caci model set - actions map: "+newUIModelData.getActionModel());
		}
	}

	private class MyCtxUIModelChangeEventListener implements CtxChangeEventListener {


		MyCtxUIModelChangeEventListener(){
		}

		@Override
		public void onCreation(CtxChangeEvent event) {

		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			LOG.debug(event.getId() + ": *** Update event *** new User Intentn model stored in ctxDB");

			CtxIdentifier uiModelAttrID = event.getId();

			if(uiModelAttrID instanceof CtxAttributeIdentifier){
				CtxAttribute uiModelAttr;
				try {
					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(uiModelAttrID).get();
					UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());

					setCAUIActiveModel(newUIModelData);

					//TODO register with pers manager for location updates.
					//LOG.info("register with pers manager for ctxAttr  update");
					if(retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC) != null){
						CtxAttribute ctxAttrLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrLocation.getId());	
						LOG.debug("register with pers manager for ctxAttr LOCATION_SYMBOLIC update");
					}
					if(retrieveOperatorsCtx(CtxAttributeTypes.STATUS) != null){
						CtxAttribute ctxAttrStatus = retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrStatus.getId());	
						LOG.debug("register with pers manager for ctxAttr STATUS update");
					}

					/*	
					if(retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE) != null){
						CtxAttribute ctxAttrTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrTemp.getId());	
						//LOG.info("register with pers manager for ctxAttr TEMPERATURE update");
					}
					 */
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
	// end of event listener implementation





	private class MyCtxCACIIModelChangeEventListener implements CtxChangeEventListener {


		MyCtxCACIIModelChangeEventListener(){
		}

		@Override
		public void onCreation(CtxChangeEvent event) {

		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			LOG.debug(event.getId() + ": *** Update event *** new Community Intent model stored in ctxDB");

			CtxIdentifier caciModelAttrID = event.getId();

			if(caciModelAttrID instanceof CtxAttributeIdentifier){
				CtxAttribute uiModelAttr;
				try {
					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(caciModelAttrID).get();
					UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());

					setCACIActiveModel(newCACIModelData);

					//TODO register with pers manager for location updates.
					//LOG.info("register with pers manager for ctxAttr  update");
					/*
					if(retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC) != null){
						CtxAttribute ctxAttrLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrLocation.getId());	
						LOG.debug("register with pers manager for ctxAttr LOCATION_SYMBOLIC update");
					}
					if(retrieveOperatorsCtx(CtxAttributeTypes.STATUS) != null){
						CtxAttribute ctxAttrStatus = retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrStatus.getId());	
						LOG.debug("register with pers manager for ctxAttr STATUS update");
					}


					if(retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE) != null){
						CtxAttribute ctxAttrTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrTemp.getId());	
						//LOG.info("register with pers manager for ctxAttr TEMPERATURE update");
					}
					 */
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



	//**********************************
	//*** helper context class 
	//***********************************

	private void retrieveModelDB(){

		try {
			List<CtxIdentifier>	listModels = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();
			if(!listModels.isEmpty()){
				CtxAttribute modelAttr = (CtxAttribute) this.ctxBroker.retrieve(listModels.get(0)).get();
				UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(modelAttr.getBinaryValue(), this.getClass().getClassLoader());
				setCAUIActiveModel(newUIModelData);
			}

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


	private CtxAttribute retrieveOperatorsCtx(String type){
		CtxAttribute ctxAttr = null;
		try {

			IndividualCtxEntity operator = this.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get();

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

	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId =  this.commsMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}

	void predictionPerformanceLog(long delay){
		m = new PerformanceMessage();
		m.setSourceComponent(this.getClass()+"");
		m.setD82TestTableName("S67");
		m.setTestContext("Personalisation.CAUIUserIntent.IntentPrediction.Delay");
		m.setOperationType("IntentPredictionFromIntentModel");//?
		m.setPerformanceType(IPerformanceMessage.Delay);

		m.setPerformanceNameValue("Delay=" + delay); 
		PERF_LOG.trace(m.toString());
	}

	void predictionConfLevelPerformanceLog(int confLevel){
		m2 = new PerformanceMessage();
		m2.setSourceComponent(this.getClass()+"");
		m2.setD82TestTableName("S24");
		m2.setTestContext("Personalisation.CAUIUserIntent.IntentPrediction.ConfidenceLevel");
		m2.setOperationType("IntentPredictionFromIntentModel");//?
		m2.setPerformanceType(IPerformanceMessage.Accuracy);

		m2.setPerformanceNameValue("Confidence Level=" + confLevel); 
		PERF_LOG.trace(m2.toString());
	}




	//Services registration
	public ICAUIDiscovery getCauiDiscovery() {
		LOG.debug(this.getClass().getName()+": Return cauiDiscovery");
		return cauiDiscovery;
	}


	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {
		LOG.debug(this.getClass().getName()+": Got cauiDiscovery");
		this.cauiDiscovery = cauiDiscovery;
	}


	public ICtxBroker getCtxBroker() {
		LOG.debug(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		LOG.debug(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}


	public IInternalPersonalisationManager getPersoMgr() {
		LOG.debug(this.getClass().getName()+": Return persoMgr");
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		LOG.debug(this.getClass().getName()+": Got persoMgr");
		this.persoMgr = persoMgr;
	}


	public ICAUITaskManager getCaciTaskManager() {
		LOG.debug(this.getClass().getName()+": Return caciTaskManager");
		return caciTaskManager;
	}


	public void setCaciTaskManager(ICAUITaskManager caciTaskManager) {
		LOG.debug(this.getClass().getName()+": Got caciTaskManager");
		this.caciTaskManager = caciTaskManager;
	}


	public ICAUITaskManager getCauiTaskManager() {
		LOG.debug(this.getClass().getName()+": Return cauiTaskManager");
		return cauiTaskManager;
	}


	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		LOG.debug(this.getClass().getName()+": Got cauiTaskManager");
		this.cauiTaskManager = cauiTaskManager;
	}



	public void setCommsMgr(ICommManager commsMgr) {
		LOG.debug(this.getClass().getName()+": Got commsMgr");
		this.commsMgr = commsMgr;
	}

	public ICommManager getCommsMgr() {
		LOG.debug(this.getClass().getName()+": Return CommsMgr");
		return commsMgr;
	}

}