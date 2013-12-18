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
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;

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
	private ICACIDiscovery caciDiscovery;
	private IServiceDiscovery serviceDiscovery;



	private ICommManager commsMgr;

	private Boolean enableCauiPrediction = true;  

	// maintains the last 100 actions
	private List<IAction> lastMonitoredActions = new ArrayList<IAction>();
	private List<IUserIntentAction> lastPredictedActions = new ArrayList<IUserIntentAction>();

	// Coupled of String representation of performed action and respective predicted action   
	public static java.util.List<java.util.Map.Entry<String,String>> predictionPairList= new java.util.ArrayList<java.util.Map.Entry<String,String>>();

	int predictionRequestsCounter = 0;
	int discoveryThreshold = 6;
	public boolean cauiModelExist = false;


	// caci variables
	public boolean enableCACIPrediction = true;
	public static boolean  caciModelExist = false;
	//boolean caciFreshness = true;

	protected CtxAttribute currentCaciModelAttr;
	private CACIPrediction caciPredictor = null;

	private IIdentity cssOwnerId;
	private CtxEntityIdentifier operatorEntId;

	public UserIntentModelData currentUIModelData;


	// constructor
	public void initialiseCAUIPrediction(){
		//if (LOG.isDebugEnabled())LOG.debug("CAUIPrediction initialised");
		//if (LOG.isDebugEnabled())LOG.debug("registerForNewUiModelEvent");

		//set caui Active model if exists in ctx DB
		retrieveCAUIModelDB();


		// creates (if don't exist) caui and caci(local) attributes 
		// registers for modifications events of both 
		registerForCAUI_CACI_ModelEvent();


		this.caciPredictor = new CACIPrediction(this.ctxBroker, this.caciTaskManager, this.commsMgr, this.serviceDiscovery);
		//set caui Active model if exists in ctx DB
		retrieveCACIModelDB();

	}


	public CAUIPrediction(){

	}


	@Override
	public void enableUserPrediction(Boolean bool) {
		if (LOG.isDebugEnabled())LOG.debug("user prediction enabled:"+ bool);
		this.enableCauiPrediction = bool;
	}

	@Override
	public Boolean isUserPredictionEnabled() {
		return  this.enableCauiPrediction ;
	}


	@Override
	public Boolean isCommunityPredictionEnabled() {
		return  this.enableCACIPrediction ;
	}

	@Override
	public void enableCommPrediction(Boolean bool) {
		if (LOG.isDebugEnabled())LOG.debug("community prediction enabled:"+ bool);
		enableCACIPrediction = bool;
	}

	public void setCaciModelExist(Boolean bool) {
		caciModelExist = bool;
	}


	@Override
	public List<List<String>> getPredictionHistory() {


		List<List<String>> result = new ArrayList<List<String>>();
		//java.util.List<java.util.Map.Entry<String,String>> predictionPairList

		if(! predictionPairList.isEmpty()){

			for(java.util.Map.Entry<String,String> listEntry : predictionPairList){
				String perfAction = listEntry.getKey();
				String predictedAction = listEntry.getKey();

				List<String> coupleEntry = new ArrayList<String>();
				coupleEntry.add(perfAction);
				coupleEntry.add(predictedAction);
				result.add(coupleEntry);
			}
		}
		return result;
	}

	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		long startTime = System.currentTimeMillis();

		if (LOG.isDebugEnabled())LOG.debug("getPrediction user prediction enabled:"+ enableCauiPrediction);
		predictionRequestsCounter = predictionRequestsCounter +1;
		this.recordMonitoredAction(action);


		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
		if(cauiDiscovery != null){
			if (LOG.isInfoEnabled())
				LOG.info("  CAUI Model Discovery Counter:" +predictionRequestsCounter);

			// initiate caui model discovery
			if(predictionRequestsCounter >= discoveryThreshold){
				//LOG.info("Start CAUI model generation");
				this.cauiDiscovery.generateNewUserModel();	
				predictionRequestsCounter = 0;
			}
		}

		if(cauiModelExist == true && enableCauiPrediction == true){

			if(currentUIModelData != null ) this.cauiTaskManager.updateModel(currentUIModelData);

			//	LOG.debug("caui model to be used for prediction: "+ this.cauiTaskManager.getCAUIActiveModel() );

			String par = action.getparameterName();
			String val = action.getvalue();
			//LOG.info("2. action perf par:"+ par+" action val:"+val);

			// identify performed action in model
			List<IUserIntentAction> actionsList = this.cauiTaskManager.retrieveActionsByTypeValue(par, val);
			//	LOG.debug("1. CAUIMODEL TaskManager.retrieveActionsByTypeValue(par, val) " +actionsList);

			if(actionsList.size()>0){

				// improve this to also use context for action identification
				//IUserIntentAction currentAction = actionsList.get(0);

				IUserIntentAction currentAction = findBestMatchingAction(actionsList);

				//	LOG.debug("2. CAUIMODEL currentAction " +currentAction);
				Map<IUserIntentAction,Double> nextActionsMap = this.cauiTaskManager.retrieveNextActions(currentAction);	
				//LOG.info("5. nextActionsMap " +nextActionsMap);

				// no context
				if(nextActionsMap.size()>0){
					for(IUserIntentAction nextAction : nextActionsMap.keySet()){
						Double doubleConf = nextActionsMap.get(nextAction);
						doubleConf = doubleConf*100;
						//doubleConf = 70.0;
						nextAction.setConfidenceLevel(doubleConf.intValue());
						//LOG.info("6. nextActionsMap " +nextAction);
						results.add(nextAction);
						this.predictionConfLevelPerformanceLog(nextAction.getConfidenceLevel());
						//LOG.info(" ****** prediction map created "+ results);
					}
				}			
			}
		} else if(enableCACIPrediction == true && caciModelExist == true) {
			if (LOG.isInfoEnabled())LOG.info("CAUI predictor not able to perform prediction ... community model to be used");

			results = this.caciPredictor.getPrediction(requestor, action);

		} else {
			if (LOG.isInfoEnabled())LOG.info("neither caci, nor caui are able to perform prediction based on action update");
		}

		String predictedActString = "";
		if(results.size()>0){
			//String predictedActString = "";

			for(IUserIntentAction predAction : results){
				this.recordPrediction(predAction);		
				predictedActString = predAction.toString()+"/confidence:"+predAction.getConfidenceLevel() +"," +predictedActString; 
			}

			java.util.Map.Entry<String,String> predictionPair = new java.util.AbstractMap.SimpleEntry<String,String>(action.toString(),predictedActString);
			predictionPairList.add(predictionPair);

			long endTime = System.currentTimeMillis();
			this.predictionPerformanceLog(endTime-startTime);
		}

		List<IUserIntentAction> clearedResults = new ArrayList<IUserIntentAction>(results);
		
		for(IUserIntentAction actionResult : clearedResults){
			if(!actionResult.isImplementable()){
				if (LOG.isDebugEnabled())LOG.debug("action: "+actionResult+" is not implementable and will be removed");
				results.remove(actionResult);
			}
		}
		
		if (LOG.isInfoEnabled())LOG.info("getPrediction based on action: "+ action+" identity requestor:"+requestor+" results:"+predictedActString);
		return new AsyncResult<List<IUserIntentAction>>(results);
	}

	
	
	

	@Override
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor, CtxAttribute contextAttribute) {

		if (LOG.isInfoEnabled())LOG.info("getPrediction based on attr update  contextAttribute : "+ contextAttribute.getId().toString()+" identity requestor"+requestor);
		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		long startTime = System.currentTimeMillis();

		CtxAttribute currentLocation = null;
		CtxAttribute currentDow = null;
		CtxAttribute currentHod = null;


		if(contextAttribute.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			currentLocation = contextAttribute;					 
		} else {
			currentLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
		}

		if(contextAttribute.getType().equals(CtxAttributeTypes.DAY_OF_WEEK)){
			currentDow = contextAttribute;					 
		} else {
			currentDow = retrieveOperatorsCtx(CtxAttributeTypes.DAY_OF_WEEK);
		}

		if(contextAttribute.getType().equals(CtxAttributeTypes.HOUR_OF_DAY)){
			currentHod = contextAttribute;					 
		} else {
			currentHod = retrieveOperatorsCtx(CtxAttributeTypes.HOUR_OF_DAY);
		}

		Map<String,Serializable> situationContext = new HashMap<String,Serializable>(); 

		if(currentLocation != null) {
			if(currentLocation.getStringValue() != null) situationContext.put(CtxAttributeTypes.LOCATION_SYMBOLIC, currentLocation.getStringValue());
		}

		if(currentDow != null) {
			if(currentDow.getStringValue() != null) situationContext.put(CtxAttributeTypes.DAY_OF_WEEK, currentDow.getStringValue());
		}

		if(currentHod != null) {
			if(currentHod.getIntegerValue() != null) {
				situationContext.put(CtxAttributeTypes.HOUR_OF_DAY, currentHod.getIntegerValue());
			}
		}



		if(cauiModelExist == true && enableCauiPrediction == true){

			if(currentUIModelData != null ) 	{
				this.cauiTaskManager.updateModel(currentUIModelData);

				// move that to web app 
				if (LOG.isDebugEnabled())LOG.debug("****************** actions and escorting context ");
				for (IUserIntentAction userAction : currentUIModelData.getActionModel().keySet()){
					if (LOG.isDebugEnabled())LOG.debug("action: "+userAction.getActionID() +" ctx:"+userAction.getActionContext());
				}				

			}

			results = cauiTaskManager.retrieveActionsByContext(situationContext);

			if(results != null ) {



				// prediction performance log
				long endTime = System.currentTimeMillis();
				this.predictionPerformanceLog(endTime-startTime);
				for(IUserIntentAction action : results) {
					int confLevel = action.getConfidenceLevel();
					this.predictionConfLevelPerformanceLog(confLevel);	
				}

			}
			if (LOG.isInfoEnabled())LOG.info("context updated :"+ contextAttribute.getId());
			if (LOG.isInfoEnabled())LOG.info("situation context :"+ situationContext);
			if (LOG.isInfoEnabled())LOG.info("action prediction based on ctx update: "+ results);	

			List<IUserIntentAction> clearedResults = new ArrayList<IUserIntentAction>(results);
			for(IUserIntentAction actionResult : clearedResults){
				if(!actionResult.isImplementable()){
					if (LOG.isDebugEnabled())LOG.debug("action: "+actionResult+" is not implementable and will be removed");
					results.remove(actionResult);
				}
			}

			return new AsyncResult<List<IUserIntentAction>>(results);

			
			
		}else if(enableCACIPrediction == true && caciModelExist == true) {

			if (LOG.isDebugEnabled())LOG.debug("CAUI predictor not able to perform prediction ... CACI model is not supporting context based prediction");
			//results = this.caciPredictor.getPrediction(requestor, action);
			List<IUserIntentAction> clearedResults = new ArrayList<IUserIntentAction>(results);
			
			for(IUserIntentAction actionResult : clearedResults){
			
				if(!actionResult.isImplementable()){
					if (LOG.isDebugEnabled())LOG.debug("action: "+actionResult+" is not implementable and will be removed");
					results.remove(actionResult);
				}
			}
			
			return new AsyncResult<List<IUserIntentAction>>(results);

		} else if (LOG.isInfoEnabled())LOG.info("neither caci, nor caui are able to perform prediction based on ctx updated");


		if (LOG.isInfoEnabled())LOG.info("context updated :"+ contextAttribute.getId());
		if (LOG.isInfoEnabled())LOG.info("situation context :"+ situationContext);
		if (LOG.isInfoEnabled())LOG.info("action prediction based on ctx update: "+ results);	
		
		List<IUserIntentAction> clearedResults = new ArrayList<IUserIntentAction>(results);
		for(IUserIntentAction actionResult : clearedResults){
			if(!actionResult.isImplementable()){
				if (LOG.isDebugEnabled())LOG.debug("action: "+actionResult+" is not implementable and will be removed");
				results.remove(actionResult);
			}
		}
		
		return new AsyncResult<List<IUserIntentAction>>(results);
	}


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

			// compare current context and choose proper action
			if(actionList.size()>0) {

				predictedAction = findBestMatchingAction(actionList);
			}

		} else {
			if (LOG.isInfoEnabled())LOG.info("no model exist or caui prediction is disabled - predictionRequestsCounter:" +predictionRequestsCounter);
		}

		if(predictedAction != null)	{
			long endTime = System.currentTimeMillis();

			this.predictionPerformanceLog(endTime - startTime);
			this.predictionConfLevelPerformanceLog(predictedAction.getConfidenceLevel());
			this.recordPrediction(predictedAction);		

		}
		if (LOG.isInfoEnabled())LOG.info("getCurrentIntentAction based on serviceID and actionType : "+predictedAction );
		return new AsyncResult<IUserIntentAction>(predictedAction);
	}


	//the list should also consider the perso feedback  
	private void recordPrediction(IUserIntentAction predAction){

		//LOG.info("predicted actions log: " +this.lastPredictedActions);

		if(this.lastPredictedActions.size()>100){
			this.lastPredictedActions.remove(0);
		}

		this.lastPredictedActions.add(predAction);
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
		CtxAttribute currentDow = retrieveOperatorsCtx(CtxAttributeTypes.DAY_OF_WEEK);
		CtxAttribute currentHod = retrieveOperatorsCtx(CtxAttributeTypes.HOUR_OF_DAY);

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

							if(currentLocation != null){
								if(currentLocation.getStringValue() != null){
									if(currentLocation.getStringValue().equals(actionLocation)) actionMatchScore = actionMatchScore +1;	
								}
							}					
						}
						else if(ctxType.equals(CtxAttributeTypes.TIME_OF_DAY) && ctxValue instanceof Integer ){
							Integer actionTod= (Integer) ctxValue;

							if(currentHod.getIntegerValue().equals(actionTod)) actionMatchScore = actionMatchScore +1;
						}
						else if(ctxType.equals(CtxAttributeTypes.DAY_OF_WEEK) && ctxValue instanceof String ){
							String actionDow = (String) ctxValue;

							if(currentDow != null ){
								if(currentDow.getStringValue() != null){
									if( currentDow.getStringValue().equals(actionDow)) actionMatchScore = actionMatchScore +1;	
								}
							}

						} else {
							if (LOG.isDebugEnabled())LOG.debug("findBestMatchingAction: context type:"+ctxType +" does not match");
						}
					} 
				}

				actionsScoreMap.put(action, actionMatchScore);
				if (LOG.isDebugEnabled())LOG.debug("actionsScoreMap  " +actionsScoreMap);
			}
		}

		int maxValueInMap=(Collections.max(actionsScoreMap.values()));  // This will return max value in the Hashmap
		for(IUserIntentAction action  : actionsScoreMap.keySet()){

			if(actionsScoreMap.get(action).equals(maxValueInMap)) bestAction = action;
		}

		if (LOG.isDebugEnabled())LOG.debug("best action "+bestAction + " with score:"+ actionsScoreMap.get(bestAction));

		return bestAction;
	}


	@Override
	public void receivePredictionFeedback(FeedbackEvent feedbackEvent) {

		if (LOG.isDebugEnabled())LOG.debug("receivePredictionFeedback parameterName:"+ feedbackEvent.getAction().getparameterName()+" getResult:"+feedbackEvent.getResult());

	}

	private void registerForCAUI_CACI_ModelEvent(){

		if (this.ctxBroker == null) {
			LOG.error("Could not register context event listener: ctxBroker is not available");
			return;
		}


		CtxAttributeIdentifier uiModelAttributeId = null;
		CtxAttributeIdentifier caciModelAttributeId = null;

		IndividualCtxEntity operator;
		try {
			//operator = this.ctxBroker.retrieveCssOperator().get();
			final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			this.cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);

			operator = this.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			//LOG.info("operator retrieved "+operator);
			this.operatorEntId = operator.getId();


			///register for caui model
			List<CtxIdentifier> ls = this.ctxBroker.lookup(this.operatorEntId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();

			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(this.operatorEntId, CtxAttributeTypes.CAUI_MODEL).get();
				uiModelAttributeId = attr.getId();
			}

			if (uiModelAttributeId != null){
				this.ctxBroker.registerForChanges(new MyCtxUIModelChangeEventListener(),this.operatorEntId, CtxAttributeTypes.CAUI_MODEL);	
				if (LOG.isDebugEnabled())LOG.debug("registration for context attribute updates of type CACI ");
			}		

		} catch (Exception e) {
			LOG.error("Exception while registering for CAUI model context updates " +e.getLocalizedMessage()) ; 
			e.printStackTrace();
		}


		try {
			List<CtxIdentifier> lsCaci = this.ctxBroker.lookup(this.operatorEntId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
			if (lsCaci.size()>0) {
				caciModelAttributeId = (CtxAttributeIdentifier) lsCaci.get(0);

			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(this.operatorEntId, CtxAttributeTypes.CACI_MODEL).get();
				caciModelAttributeId = attr.getId();
			}

			if (caciModelAttributeId != null){
				this.ctxBroker.registerForChanges(new MyCtxCACIIModelChangeEventListener(), this.operatorEntId, CtxAttributeTypes.CACI_MODEL);
				if (LOG.isDebugEnabled())LOG.debug("registration for context attribute updates of type CACI ");	
			}

		} catch (Exception e) {
			LOG.error("Exception while registering for CACI model context updates " +e.getLocalizedMessage()) ; 
			e.printStackTrace();
		}
	}


	public void setCAUIActiveModel(UserIntentModelData newUIModelData){

		if (newUIModelData != null){
			this.cauiTaskManager.updateModel(newUIModelData);
			cauiModelExist = true;		 
			this.currentUIModelData = newUIModelData;
			if (LOG.isInfoEnabled())LOG.info("caui model set - actions map: "+newUIModelData.getActionModel());
		}
	}

	public void setCACIActiveModel (UserIntentModelData newUIModelData){

		if (newUIModelData != null){
			this.caciPredictor.setCACIActiveModel(newUIModelData);
			caciModelExist = true;	

			if (LOG.isInfoEnabled())LOG.info("caci model set - actions map: "+newUIModelData.getActionModel());
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
			if (LOG.isDebugEnabled())LOG.debug(event.getId() + ": *** Update event *** new User Intentn model stored in ctxDB");

			CtxIdentifier uiModelAttrID = event.getId();

			if(uiModelAttrID instanceof CtxAttributeIdentifier){
				CtxAttribute uiModelAttr;
				try {
					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(uiModelAttrID).get();
					UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());

					setCAUIActiveModel(newUIModelData);

					//LOG.info("register with pers manager for ctxAttr  update");
					if(retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC) != null){
						CtxAttribute ctxAttrLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrLocation.getId());	
						if (LOG.isDebugEnabled())LOG.debug("register with pers manager for ctxAttr LOCATION_SYMBOLIC update");
					}
					if(retrieveOperatorsCtx(CtxAttributeTypes.DAY_OF_WEEK) != null){
						CtxAttribute ctxAttrStatus = retrieveOperatorsCtx(CtxAttributeTypes.DAY_OF_WEEK);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrStatus.getId());	
						if (LOG.isDebugEnabled())LOG.debug("register with pers manager for ctxAttr DAY_OF_WEEK update");
					}


					if(retrieveOperatorsCtx(CtxAttributeTypes.HOUR_OF_DAY) != null){
						CtxAttribute ctxAttrHod = retrieveOperatorsCtx(CtxAttributeTypes.HOUR_OF_DAY);
						persoMgr.registerForContextUpdate(getOwnerId(), PersonalisationTypes.CAUIIntent, ctxAttrHod.getId());	
						if (LOG.isDebugEnabled())LOG.debug("register with pers manager for ctxAttr HOUR_OF_DAY update");
					}

				} catch (Exception e) {
					LOG.error("Exception while registering with pers manager for ctx update events "+ e.getLocalizedMessage());
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
			if (LOG.isDebugEnabled())LOG.debug(event.getId() + ": *** Update event *** new Community Intent model stored in ctxDB");

			CtxIdentifier caciModelAttrID = event.getId();

			if(caciModelAttrID instanceof CtxAttributeIdentifier){
				CtxAttribute uiModelAttr;
				try {
					uiModelAttr = (CtxAttribute) ctxBroker.retrieve(caciModelAttrID).get();
					UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());

					setCACIActiveModel(newCACIModelData);

				} catch (Exception e) {
					LOG.error("Exception while handling new CACI model update event " +e.getLocalizedMessage());
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

	private UserIntentModelData retrieveCAUIModelDB(){

		UserIntentModelData newUIModelData = null;
		try {
			List<CtxIdentifier>	listModels = this.ctxBroker.lookup(this.getOwnerId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();

			if(listModels != null && !listModels.isEmpty() ){
				CtxAttribute modelAttr = (CtxAttribute) this.ctxBroker.retrieve(listModels.get(0)).get();
				if(modelAttr.getBinaryValue() != null ){
					newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(modelAttr.getBinaryValue(), this.getClass().getClassLoader());
					setCAUIActiveModel(newUIModelData);
				}
			}
		} catch (Exception e) {
			LOG.error("Exception when retrieving CtxAttribute of type CAUI model from local Context DB "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return newUIModelData;
	}


	private void retrieveCACIModelDB(){

		try {
			IIdentity identity = getOwnerId();

			List<CtxIdentifier>	listModels = this.ctxBroker.lookup(identity, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
			if(!listModels.isEmpty()){
				CtxAttribute modelAttr = (CtxAttribute) this.ctxBroker.retrieve(listModels.get(0)).get();
				if(modelAttr.getBinaryValue() != null){
					UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(modelAttr.getBinaryValue(), this.getClass().getClassLoader());
					if(!newUIModelData.getActionModel().isEmpty()){
						setCACIActiveModel(newUIModelData);	
					} else {
						throw new NullPointerException("CACI model is corrupted "+ newUIModelData.getActionModel());

					}
				}				
			}
		} catch (Exception e) {
			LOG.error("Exception when retrieving CtxAttribute of type CACI model from local Context DB "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}


	private CtxAttribute retrieveOperatorsCtx(String type){

		CtxAttribute ctxAttr = null;

		try {

			CtxEntityIdentifier operatorCtxID = this.ctxBroker.retrieveIndividualEntityId(null, getOwnerId()).get();
			List<CtxIdentifier> attrList = this.ctxBroker.lookup(operatorCtxID, CtxModelType.ATTRIBUTE, type).get();

			if(!attrList.isEmpty()){
				ctxAttr = (CtxAttribute) this.ctxBroker.retrieve(attrList.get(0)).get();
			}

		} catch (Exception e) {
			LOG.error("Exception when retrieving CtxAttribute of type CACI model from local Context DB "+e.getLocalizedMessage());
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



	@Override
	public HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getCAUIActiveModel() {

		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> activeCAUIModel = new HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>(); 

		if (LOG.isDebugEnabled())LOG.debug("getCAUIActiveModel cauipred from task manager: " +this.cauiTaskManager.getCAUIActiveModel() );
		UserIntentModelData model = retrieveCAUIModelDB();
		if( model != null){
			activeCAUIModel = model.getActionModel();
			this.setCAUIActiveModel(model);
		}


		if(this.cauiTaskManager.getCAUIActiveModel() != null ){
			activeCAUIModel = this.cauiTaskManager.getCAUIActiveModel();	
		}
		return activeCAUIModel;
	}

	@Override
	public HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getCACIActiveModel() {

		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> activeCACIModel = new HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>(); 
		if(this.caciTaskManager.getCAUIActiveModel() != null ){
			activeCACIModel = this.caciTaskManager.getCAUIActiveModel();	
		}			
		return activeCACIModel;
	}

	@Override
	public void generateNewUserModel() {

		this.cauiDiscovery.generateNewUserModel();

	}

	@Override
	public void generateNewCommunityModel(IIdentity cisId) {

		//LOG.debug("generateNewCommunityModel 1 "+ cisId );

		// change association type used... 
		//try {

		if(cisId != null){
			this.caciDiscovery.generateNewCommunityModel(cisId);
		}
		/*
			if(cisId == null){

				//	List<CtxIdentifier> commEntList = this.ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.COMMUNITY).get();
				List<CtxIdentifier> isMemberOfAssocList =	this.ctxBroker.lookup(cisId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
				//	LOG.debug("generateNewCommunityModel 2 "+ isMemberOfAssocList );
				if (!isMemberOfAssocList.isEmpty() ) {

					CtxAssociationIdentifier isMemberCISsID = (CtxAssociationIdentifier) isMemberOfAssocList.get(0);
					CtxAssociation assoc;

					assoc = (CtxAssociation) this.ctxBroker.retrieve(isMemberCISsID).get();

					Set<CtxEntityIdentifier> cisEntIdSet = assoc.getChildEntities();
					//	LOG.debug("generateNewCommunityModel 3 "+ cisEntIdSet );

					//TODO fix this for cases that belongs to more than one cis
					for(CtxEntityIdentifier cisEntityID : cisEntIdSet){
						cisId = this.commsMgr.getIdManager().fromJid(cisEntityID.getOwnerId()); 
						//	LOG.debug("generateNewCommunityModel 4 "+ cisId );
					}				

					//LOG.debug("generateNewCommunityModel 5 "+ cisId );
					this.caciDiscovery.generateNewCommunityModel(cisId);
					return;
				}

			} else {
				//LOG.debug("generateNewCommunityModel 6 "+ cisId );
				this.caciDiscovery.generateNewCommunityModel(cisId);	
			}
		 */
		//	} 	catch (Exception e) {

		//		LOG.error("Could not start CACI learning '"
		//					+ e.getLocalizedMessage(), e);
		//		}

	}


	//Services registration
	public ICAUIDiscovery getCauiDiscovery() {
		//LOG.debug(this.getClass().getName()+": Return cauiDiscovery");
		return cauiDiscovery;
	}


	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {
		//LOG.debug(this.getClass().getName()+": Got cauiDiscovery");
		this.cauiDiscovery = cauiDiscovery;
	}


	public ICtxBroker getCtxBroker() {
		//LOG.debug(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		//LOG.debug(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}


	public IInternalPersonalisationManager getPersoMgr() {
		//LOG.debug(this.getClass().getName()+": Return persoMgr");
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		//LOG.debug(this.getClass().getName()+": Got persoMgr");
		this.persoMgr = persoMgr;
	}


	public ICAUITaskManager getCaciTaskManager() {
		//LOG.debug(this.getClass().getName()+": Return caciTaskManager");
		return caciTaskManager;
	}


	public void setCaciTaskManager(ICAUITaskManager caciTaskManager) {
		//LOG.debug(this.getClass().getName()+": Got caciTaskManager");
		this.caciTaskManager = caciTaskManager;
	}


	public ICAUITaskManager getCauiTaskManager() {
		//LOG.debug(this.getClass().getName()+": Return cauiTaskManager");
		return this.cauiTaskManager;
	}


	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		//LOG.debug(this.getClass().getName()+": Got cauiTaskManager");
		this.cauiTaskManager = cauiTaskManager;
	}



	public void setCommsMgr(ICommManager commsMgr) {
		//LOG.debug(this.getClass().getName()+": Got commsMgr");
		this.commsMgr = commsMgr;
	}

	public ICommManager getCommsMgr() {
		//LOG.debug(this.getClass().getName()+": Return CommsMgr");
		return this.commsMgr;
	}

	public ICACIDiscovery getCaciDiscovery() {
		//LOG.debug(this.getClass().getName()+": Got caciDiscovery");
		return this.caciDiscovery;
	}

	public void setCaciDiscovery(ICACIDiscovery caciDiscovery) {
		this.caciDiscovery = caciDiscovery;
	}

	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}


	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	@Override
	public CtxAttribute retrieveCACIModel(IIdentity cisID) {

		CtxAttribute caciAttr = null;

		CtxEntityIdentifier entID = null;

		try {
			//TODO remove this option
			if( cisID == null ){
				List<CtxEntityIdentifier> commEntIDList = retrieveOwningCIS();
				entID = commEntIDList.get(0);
			} else {
				//entID = this.ctxBroker.retrieveIndividualEntityId(null, cisID).get();
				entID = this.ctxBroker.retrieveCommunityEntityId(cisID).get();
			}

			List<CtxIdentifier> caciModelList = this.ctxBroker.lookup(entID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();

			if(!caciModelList.isEmpty()){
				caciAttr = (CtxAttribute) this.ctxBroker.retrieve(caciModelList.get(0)).get();

				if(caciAttr.getBinaryValue() != null){
					//currentCaciModelAttr = caciAttr;
					UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciAttr.getBinaryValue(), this.getClass().getClassLoader());
					//LOG.debug("retrieveCACIModel commEntIDList  4  caciAttr "+newCACIModelData );
					storeCaciModelDB(newCACIModelData);
					setCACIActiveModel(newCACIModelData);	
				}
			}
		} catch (Exception e) {
			LOG.error("CtxAttribute of type "+CtxAttributeTypes.CACI_MODEL+ " was not retrieved with caci model "+e.getLocalizedMessage());
			e.printStackTrace();
		} 
		return caciAttr;
	}


	public void storeCaciModelDB(UserIntentModelData modelData){

		final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
		CtxAttribute attr = null;

		try {
			IIdentity cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
			CtxEntityIdentifier indiEntId = this.ctxBroker.retrieveIndividualEntityId(null, cssOwnerId).get();

			List<CtxIdentifier> lsCaci = this.ctxBroker.lookup(indiEntId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
			CtxAttributeIdentifier caciModelAttributeId = null;

			if (lsCaci.size()>0) {
				caciModelAttributeId = (CtxAttributeIdentifier) lsCaci.get(0);
				attr = (CtxAttribute) this.ctxBroker.retrieve(caciModelAttributeId).get();
				byte[] binaryModel = SerialisationHelper.serialise(modelData);

				this.ctxBroker.updateAttribute(attr.getId(), binaryModel).get();
			}

		} catch (Exception e) {
			LOG.error("CtxAttribute of type "+CtxAttributeTypes.CACI_MODEL+ "was not updated with caci model "+e.getLocalizedMessage());
			e.printStackTrace();
		} 
	}


	

	public List<CtxEntityIdentifier> retrieveOwningCIS(){

		List<CtxEntityIdentifier> commEntIDList = new ArrayList<CtxEntityIdentifier>();

		List<CtxIdentifier> listAdminOf = new ArrayList<CtxIdentifier>();

		final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
		try {
			this.cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
			//listISMemberOf = this.ctxBroker.lookup(this.cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			listAdminOf = this.ctxBroker.lookup(this.cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_ADMIN_OF).get();
			if (LOG.isDebugEnabled())LOG.debug("........assoc is admin of ...." +listAdminOf);

			if(!listAdminOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.ctxBroker.retrieve(listAdminOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commsMgr.getIdManager().fromJid(entId.getOwnerId());

					CtxEntityIdentifier commId = this.ctxBroker.retrieveCommunityEntityId(cisId).get();
					commEntIDList.add(commId);
				}
			}
			if (LOG.isDebugEnabled())LOG.debug("is admin of cis ids : "+commEntIDList );
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return commEntIDList;
	}


	public List<CtxEntityIdentifier> retrieveMyCIS(){

		if (LOG.isDebugEnabled())LOG.debug(".............retrieveMyCIS................." );
		List<CtxEntityIdentifier> commEntIDList = new ArrayList<CtxEntityIdentifier>();
		List<CtxEntityIdentifier> result = new ArrayList<CtxEntityIdentifier>();

		final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();

		try {
			this.cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
			commEntIDList = this.retrieveOwningCIS();
			//TODO remove this line and add code to check if cis is local
			// done
			result.addAll(commEntIDList);

			if (LOG.isDebugEnabled())LOG.debug(".............retrieveMyCIS..commEntIDList " +commEntIDList);
			/*
			if(!commEntIDList.isEmpty() ){
				for(CtxEntityIdentifier entId :commEntIDList){
					LOG.debug(".............retrieveMyCIS..entId.getOwnerId().." +entId.getOwnerId());
					LOG.debug(".............retrieveMyCIS..this.cssOwnerId.toString().." +this.cssOwnerId.toString());

					if(entId.getOwnerId().equals(this.cssOwnerId.toString())){
						LOG.debug("community entity is local ");
						result.add(entId);
					}
				}
			}
			 */
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 
		if (LOG.isDebugEnabled())LOG.debug(".............retrieveMyCIS..result.." +result);
		return result;
	}

	@Override
	public java.util.List<java.util.Map.Entry<String, String>> getPredictionPairLog() {
		return predictionPairList;
	}


}