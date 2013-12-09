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
package org.societies.personalisation.CACIDiscovery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

//remove after testing
//import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;


public class CACIDiscovery implements ICACIDiscovery{

	public static final Logger LOG = LoggerFactory.getLogger(CACIDiscovery.class);

	public ICAUITaskManager cauiTaskManager;
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private IServiceDiscovery serviceDiscovery;

	private IIdentity cisIdentifier ;


	public CACIDiscovery(){
		//remove after testing
		//cauiTaskManager = new CAUITaskManager();	
	}


	public void initialiseCACIDiscovery(){

	}


	public IServiceDiscovery getServiceDiscovery() {

		LOG.debug(this.getClass().getName()+": Return IServiceDiscovery");
		return serviceDiscovery;
	}


	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		LOG.debug(this.getClass().getName()+": Got serviceDiscovery" +serviceDiscovery );
		this.serviceDiscovery = serviceDiscovery;
	}


	public ICAUITaskManager getCauiTaskManager() {
		//System.out.println(this.getClass().getName()+": Return cauiTaskManager");
		return cauiTaskManager;
	}

	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		//System.out.println(this.getClass().getName()+": Got cauiTaskManager");
		this.cauiTaskManager = cauiTaskManager;


	}

	public ICtxBroker getCtxBroker() {
		//System.out.println(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		//System.out.println(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}

	public void setCommsMgr(ICommManager commsMgr) {
		//LOG.info(this.getClass().getName()+": Got commsMgr");
		this.commsMgr = commsMgr;
	}

	public ICommManager getCommsMgr() {
		//LOG.info(this.getClass().getName()+": Return CommsMgr");
		return commsMgr;
	}


	@Override
	public void generateNewCommunityModel(IIdentity cisId) {

		this.cisIdentifier = cisId;

		//LOG.info("Discovering new CACI model for "+ this.cisIdentifier);

		List<UserIntentModelData> userModelList = new ArrayList<UserIntentModelData>();
		userModelList = retrieveUIModels(cisId);

		if(userModelList.size()>0){
			generateNewCommunityModel(userModelList);	
		}
	}


	/*
	 * Merge individual user intent models to a community model
	 */
	public UserIntentModelData mergeModels(List<UserIntentModelData> userModelList){

		//CACI model
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> communityActionsMap = new HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>>();
		//System.out.println("mergeModels 1 ");
		// create translation map
		// translate identical actions of various user models to a unique actions that will be part of the community  model
		// key:commAction -- value: set of user actions
		Map<IUserIntentAction, List<IUserIntentAction>> translationMap =  createTranslationMap (userModelList);
		//printTranlationMap(translationMap);

		List<UserIntentModelData> userModelsComActionsList = convertUserToCommModels(userModelList,translationMap);

		// A) create a map (communityActionsMap) with key each individual userAction and B) value a map with all possible targets 

		// get each user intent model
		for(UserIntentModelData userModel : userModelsComActionsList){
			// get actions for this model
			HashMap<IUserIntentAction,HashMap<IUserIntentAction, Double>> actionsMap = userModel.getActionModel();
			//LOG.debug(" initial actionsMap : "+actionsMap);
			// add source actions from all models to map
			for(IUserIntentAction communitySourceAction : actionsMap.keySet()){
				//if(!mapContainsKeyAction(communityActionsMap,action)){
				if(!communityActionsMap.keySet().contains(communitySourceAction)){
					communityActionsMap.put(communitySourceAction, new HashMap<IUserIntentAction,Double>());
				} 
			}
		}

		//System.out.println("mergeModels 2 communityActionsMap "+communityActionsMap);

		//int i =0;
		for(UserIntentModelData userModel : userModelList){

			//i++;
			//System.out.println("model i= "+i );
			HashMap<IUserIntentAction,  HashMap<IUserIntentAction,Double>> uiModelActions = userModel.getActionModel();

			for (IUserIntentAction sourceUserAct  :uiModelActions.keySet()){
				//LOG.debug("sourceUserAct " +sourceUserAct);
				//iterate through commActions and find a commAction similar with user action
				for(IUserIntentAction sourceComAct : communityActionsMap.keySet()){

					if(this.areSimilarActions(sourceUserAct, sourceComAct)){

						// get targets for this userAct if exist (e.g. if it is the last action of uimodel no target exist)
						if(uiModelActions.get(sourceUserAct) != null) {

							HashMap<IUserIntentAction,Double> targetUserActions = uiModelActions.get(sourceUserAct);
							// translate list of targets user action to commAction 
							//System.out.println("sourceUserAct "+sourceUserAct);
							//System.out.println("targetUserActions "+targetUserActions);
							HashMap<IUserIntentAction,Double> targetComActionNew =  translateUsertoComActionMap(translationMap, targetUserActions);
							//LOG.debug("targetComActionNew " +targetComActionNew);

							// add translated source community action along with user action to output 
							// if entry don't exist yet
							if(communityActionsMap.get(sourceComAct) == null || communityActionsMap.get(sourceComAct).size() == 0){
								communityActionsMap.put(sourceComAct, targetComActionNew);
							}  
							// if entry already exists
							if(communityActionsMap.get(sourceComAct).size() >0 ){

								HashMap<IUserIntentAction,Double> targetComActionExisting = communityActionsMap.get(sourceComAct);
								HashMap<IUserIntentAction,Double> updatedTargetComMap = mergeTargetMaps(targetComActionNew,targetComActionExisting);
								communityActionsMap.put(sourceComAct, updatedTargetComMap);
							}


						} else {
							// this is a final community source action, no target actions exist
							if(communityActionsMap.get(sourceComAct) == null || communityActionsMap.get(sourceComAct).size() == 0){
								communityActionsMap.put(sourceComAct, null);
								//System.out.println("**** adding null target for "+sourceComAct);
							} // else {
							// if community model already contains an edge for sourceCommAct to some target
							// but the newly merged model doesn't contain this link recalculate and drop trans prob

							//}
						}

					}
				}
			}
			//this.printCACIModel(communityActionsMap);
		}


		UserIntentModelData communityModel = new UserIntentModelData();
		communityModel.setActionModel(communityActionsMap);

		this.printCACIModel(communityActionsMap);

		return communityModel;
	}

	@Override
	public void generateNewCommunityModel(List<UserIntentModelData> userModelList) {

		UserIntentModelData communityModel = mergeModels(userModelList);

		storeModelCtxDB(communityModel);
	}


	/*
	 * store model to ctx DB as a community ctx Attribute of community Entity defined by cis
	 */

	private CtxAttribute storeModelCtxDB(UserIntentModelData modelData){

		if(modelData.getActionModel().isEmpty()) {
			if (LOG.isDebugEnabled())LOG.debug("No community actions in CACI model , couldn't store model to Ctx DB");
			return null;
		}

		if( this.cisIdentifier == null) {
			if (LOG.isDebugEnabled())LOG.debug("CIS identifier is not defined");
			return null;
		} 		
		//LOG.debug("community model "+modelData.getActionModel() );

		CtxAttribute ctxAttrCACIModel = null;
		try {
			byte[] binaryModel = SerialisationHelper.serialise(modelData);

			CtxEntityIdentifier communityEntID = this.ctxBroker.retrieveCommunityEntityId(this.cisIdentifier).get();

			CtxAttributeIdentifier uiModelAttributeId = null;
			List<CtxIdentifier> ls = this.ctxBroker.lookup(communityEntID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();

			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
				ctxAttrCACIModel = ctxBroker.updateAttribute(uiModelAttributeId, binaryModel).get();
			} else {
				CtxAttribute attr = this.ctxBroker.createAttribute(communityEntID, CtxAttributeTypes.CACI_MODEL).get();
				uiModelAttributeId = attr.getId();
				ctxAttrCACIModel = ctxBroker.updateAttribute(uiModelAttributeId, binaryModel).get();
			}			
			if (LOG.isDebugEnabled())LOG.debug("CACI Model stored in community ctx DB" + ctxAttrCACIModel.getId());

		}  catch (Exception e) {
			LOG.error("Exception while storing CACI model in context DB" + e.getLocalizedMessage());
			e.printStackTrace();
		} 
		return ctxAttrCACIModel;
	}


	/*
	protected CtxAttribute lookupAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {

			List<CtxIdentifier> tupleAttrList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size()>0){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) this.ctxBroker.retrieve(ctxId).get();	
			}		
		} catch (Exception e) {
			LOG.error("Exception while looking up Attribute of type: "+type+",  "+e.getLocalizedMessage());
			e.printStackTrace();
		} 
		return ctxAttr;
	}

	 */

	/*
	 * merges the target action transition probs
	 * {(d,0.4),(b,0.6)} + {(c,0.4),(b,0.6)} = {( b,0.6),(c,0.2),(d,0.2)}
	 */
	public HashMap<IUserIntentAction,Double> mergeTargetMaps(HashMap<IUserIntentAction,Double> targetComActionNew, 
			HashMap<IUserIntentAction,Double> targetComActionExisting){

		HashMap<IUserIntentAction,Double> result = new HashMap<IUserIntentAction,Double>();

		if( targetComActionExisting.size() == 0 ) {
			return targetComActionNew;
		}
		Set<IUserIntentAction> allKeys = new HashSet<IUserIntentAction>();
		allKeys.addAll(targetComActionNew.keySet());
		allKeys.addAll(targetComActionExisting.keySet());

		for(IUserIntentAction keyAction : allKeys){
			Double valueA = 0.0;
			Double valueB = 0.0;

			if( targetComActionExisting.get(keyAction) != null){
				valueA = targetComActionExisting.get(keyAction);
			}

			if( targetComActionNew.get(keyAction) != null){
				valueB = targetComActionNew.get(keyAction);
			}
			Double average =  (valueA+valueB)/2 ;
			result.put(keyAction,average);
		}

		return result;
	}


	/*
	 * convert target user actions in map to community user actions in map
	 */
	private HashMap<IUserIntentAction,Double> translateUsertoComActionMap(Map<IUserIntentAction, List<IUserIntentAction>> translationMap ,HashMap<IUserIntentAction,Double> targetUserActions){

		HashMap<IUserIntentAction,Double> targetComActions = new HashMap<IUserIntentAction,Double>();  

		for(IUserIntentAction userActions : targetUserActions.keySet()){

			if(findComAction(translationMap, userActions) != null ){
				IUserIntentAction communityAction = findComAction(translationMap, userActions);
				Double transProb = targetUserActions.get(userActions);
				targetComActions.put(communityAction, transProb);
			}			
		}
		return targetComActions;
	}


	/*
	 * checks if the map contain a specific action
	 * special implementation of map.contains because of actions similarity issue

	private Boolean mapContainsKeyAction(Map<IUserIntentAction, HashMap<IUserIntentAction,Double>> map, IUserIntentAction action){

		for(IUserIntentAction actionKey : map.keySet()){
			if(equalActions (actionKey,action) ) return true;
		}

		return false;
	}
	 */


	private Boolean mapContainsSimilarKeyAction(Map<IUserIntentAction, ?> tranlationMap,  IUserIntentAction action){

		for(IUserIntentAction actionKey : tranlationMap.keySet()){
			if(actionKey == null) {
				return false;
			} else {
				//System.out.println("action 1 "+action +" action 2:"+ actionKey);	
				if(areSimilarActions (actionKey,action) ){
					//System.out.println("EQUAL");
					return true;
				}
			}
		}

		return false;
	}



	public Boolean areSimilarActions(IUserIntentAction actionA, IUserIntentAction actionB){

		// ignore ServiceInstanceIdentifier, using only service type
		/*	
		if( actionA == null){
			LOG.debug("action A null");
			return false;
		}

		if(actionB == null){
			LOG.debug("action B null");
			return false;
		}
		 */


		if( actionA != null){
		//	LOG.debug("action A "+actionA);
		//	LOG.debug("action A serviceID:"+actionA.getServiceID());

		}
		if( actionB != null){
		//	LOG.debug("action B "+actionB);
		//	LOG.debug("action B serviceID:"+actionA.getServiceID());

		}

		if(actionA.getparameterName().equals(actionB.getparameterName()) && actionA.getvalue().equals(actionB.getvalue())) {
			//LOG.debug( actionA +" and "+ actionB+"!!!!! are similar");
			return true;
		} else return false;
	}




	public Boolean compareModels(UserIntentModelData modelA, UserIntentModelData modelB){

		return false;
	}




	/*
	 * This method translates actions of various user intent model to community actions based on a translation map created by
	 * method discovery.createTranslationMap(uiModelList)
	 * Actions similarity is based on action parameters (serviceType, parameter, value)
	 * It converts same actions of different ui models to unique actions for all models  
	 *  
	 *  e.g. communityActA = {userActA1,userActA2,userActA3}
	 *  userActionModel {http://testService2#volume=low/1={http://testService1#volume=mute/2=1.0}, http://testService1#YYYY=YYYY/4={http://testService1#XXXX=XXXX/3=0.5, http://testService2#colour=blue/5=0.5}, http://testService1#XXXX=XXXX/3={http://testService1#YYYY=YYYY/4=0.5714285714285714, http://testService1#volume=high/0=0.42857142857142855}, http://testService1#volume=high/0={http://testService2#volume=low/1=1.0}, http://testService2#colour=green/6={http://testService1#YYYY=YYYY/4=1.0}, http://testService2#colour=blue/5={http://testService2#colour=green/6=1.0}, http://testService1#volume=mute/2={http://testService1#XXXX=XXXX/3=1.0}}
	 *  communityActionModel {http://testService2#volume=low/17={http://testService1#volume=mute/23=1.0}, http://testService1#YYYY=YYYY/18={http://testService1#XXXX=XXXX/19=0.5, http://testService2#colour=blue/22=0.5}, http://testService1#XXXX=XXXX/19={http://testService1#YYYY=YYYY/18=0.5714285714285714, http://testService1#volume=high/20=0.42857142857142855}, http://testService1#volume=high/20={http://testService2#volume=low/17=1.0}, http://testService2#colour=green/21={http://testService1#YYYY=YYYY/18=1.0}, http://testService2#colour=blue/22={http://testService2#colour=green/21=1.0}, http://testService1#volume=mute/23={http://testService1#XXXX=XXXX/19=1.0}}
	 */
	public List<UserIntentModelData> convertUserToCommModels (List<UserIntentModelData> userModelList, Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap){

		List<UserIntentModelData> translatedModels = new ArrayList<UserIntentModelData>();


		// translate userActionModel to community action model
		for (UserIntentModelData userModel : userModelList){

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> userActionModel = userModel.getActionModel();
			//LOG.debug("convertUserToCommModels userActionModel 1 " + userActionModel);

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> communityActionModel =
					new HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

			for(IUserIntentAction userAction  : userActionModel.keySet()){
				IUserIntentAction commAction = translateUserToComAction(tranlationMap,userAction);
				HashMap<IUserIntentAction,Double> communityTargetMap = new HashMap<IUserIntentAction,Double>();

				HashMap<IUserIntentAction,Double> userTargetMap = userActionModel.get(userAction);
				if(userTargetMap != null  ){
					for(IUserIntentAction userTargetAction : userTargetMap.keySet()){
						IUserIntentAction commTargetAction = translateUserToComAction(tranlationMap,userTargetAction);
						communityTargetMap.put(commTargetAction, userTargetMap.get(userTargetAction));
					}
					communityActionModel.put(commAction, communityTargetMap);

				} if(userTargetMap == null ){
					communityActionModel.put(commAction, null);
				}
			}
			//LOG.debug("convertUserToCommModels userActionModel 1 communityActionModel " + communityActionModel);

			UserIntentModelData communityModel = new UserIntentModelData();
			communityModel.setActionModel(communityActionModel);
			translatedModels.add(communityModel);
		}
		// translate userActionModel to community action model, finished

		return translatedModels ;
	}



	// translate identical actions of various user models to a unique actions that will be part of the community  model
	// key:commAction -- value: set of user actions
	public Map<IUserIntentAction, List<IUserIntentAction>> createTranslationMap (List<UserIntentModelData> userModelList){

		Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap = new HashMap<IUserIntentAction, List<IUserIntentAction>>();
		//int i =0;
		List<UserIntentModelData> userModelListNewInst = new ArrayList<UserIntentModelData>();
		userModelListNewInst.addAll(userModelList);

		for (UserIntentModelData userModel : userModelListNewInst){

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> userActionModel = new HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();
			userActionModel = userModel.getActionModel();
			//i++;
			//System.out.println("1 createTranslationMap current model: "+i + "  "+userModel.getActionModel().keySet()); 

			for(IUserIntentAction userAction  : userActionModel.keySet()){
				//		System.out.println("loop userAction "+userAction); 

				if(! mapContainsSimilarKeyAction(tranlationMap,userAction)){	

					IUserIntentAction commAction = createCommunityAction(userAction);
					List<IUserIntentAction> newUserActionList = new ArrayList<IUserIntentAction>();
					newUserActionList.add(userAction);
					tranlationMap.put(commAction, newUserActionList);

				} else {

					IUserIntentAction commAction = findComAction(tranlationMap, userAction);
					if(tranlationMap.get(commAction) != null ){
						List<IUserIntentAction> currentActionList  = tranlationMap.get(commAction);
						//System.out.println("currentActionSet " + currentActionSet);
						currentActionList.add(userAction);
						tranlationMap.put(commAction, currentActionList);	
					}
				}

			}// action list for loop 	
		}// model list

		// translation map created 
		return tranlationMap;
	}



	private IUserIntentAction translateUserToComAction(Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap,IUserIntentAction userAction ){

		IUserIntentAction communityAction = null;

		for(IUserIntentAction communityActionTemp : tranlationMap.keySet()){
			List<IUserIntentAction> actionSet = tranlationMap.get(communityActionTemp);
			if(actionSet.contains(userAction)) return communityActionTemp;
		}

		return communityAction; 
	}



	/*
	 * retrieves a community action identical with the user action declared 
	 */
	protected IUserIntentAction findComAction(Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap, IUserIntentAction userAction ){

		//	IUserIntentAction communityAction = null;

		for(IUserIntentAction communityAction : tranlationMap.keySet() ){
			if(this.areSimilarActions(communityAction, userAction)) {
				return communityAction;
			}
		}
		return null;
	}

	
	/*
	 * creates a new community action based on the details of the userAction 
	 */
	private IUserIntentAction createCommunityAction (IUserIntentAction userAction){

		IUserIntentAction commAction = null;

		cauiTaskManager.createModel();

		if(userAction.getServiceID() != null && userAction.getparameterName() != null 
				&& userAction.getvalue() != null && userAction.getServiceType() != null){

			commAction = cauiTaskManager.createAction(userAction.getServiceID(), userAction.getServiceType(), 
					userAction.getparameterName(), userAction.getvalue());
			commAction.setCommunity(true);
			//  set action context 
		} else {
			System.out.println(" ****** CAUI User Action not valid " +  userAction.toString());
			LOG.error(" CAUI User Action not valid , action ID:"+  userAction.toString());
			throw new NullPointerException(" CAUI User Action not valid , action ID:"+  userAction.toString());
		}

		//System.out.println(" ****** created comm action: "+commAction+" based on user action:" + userAction.toString());
		return commAction;
	}


	/*
	 * print methods  
	 */
	void printTranlationMap(Map<IUserIntentAction, List<IUserIntentAction>> tranlationMap){

		for(IUserIntentAction action : tranlationMap.keySet()){

			//System.out.println("com act: "+action +" /user act->"+tranlationMap.get(action));
			if (LOG.isDebugEnabled())LOG.debug(("com act: "+action +" /user act->"+tranlationMap.get(action)));
		}	

	}

	/*
	private void printModels(List<UserIntentModelData> modelList){

		for(UserIntentModelData uimodel : modelList){
			System.out.println(uimodel.getActionModel());
		}
	}
	 */

	private void printCACIModel(Map<IUserIntentAction, HashMap<IUserIntentAction,Double>> map){

		//System.out.println("caci model");
		if (LOG.isInfoEnabled()){ 
			LOG.info("printing community model");

			for( IUserIntentAction sourceAct : map.keySet()){
				//System.out.println("sourceAct "+ sourceAct +" target "+map.get(sourceAct) );
				LOG.info("sourceAct "+ sourceAct +" target "+map.get(sourceAct) );
				HashMap<IUserIntentAction, Double> targetActions = map.get(sourceAct);

				for(IUserIntentAction actionTarget : targetActions.keySet()){
					//System.out.println("--> targetID:"+actionTarget.getActionID() +"confidence level: "+actionTarget.getConfidenceLevel());	
					LOG.info("--> targetID:"+actionTarget.getActionID() +"confidence level: "+actionTarget.getConfidenceLevel());
				}

			}
		}
	}	


	@Override
	public void generateNewCommunityModel() {

	}


	private List<UserIntentModelData> retrieveUIModels (IIdentity cisId)  {

		List<UserIntentModelData> userModelList = new ArrayList<UserIntentModelData>();
		//LOG.debug(" retrieving cauis for cisID "+ cisId);
		CtxEntityIdentifier commEntID;
		CommunityCtxEntity commEnt = null;
		List<CtxIdentifier> modelAttrIDList = new ArrayList<CtxIdentifier>();
		try{
			commEntID = this.ctxBroker.retrieveCommunityEntityId(cisId).get();
			commEnt = (CommunityCtxEntity) this.ctxBroker.retrieve(commEntID).get();
		} catch (Exception e) {
			LOG.error("Exception while retrieving community entity ID "+e.getLocalizedMessage());
			e.printStackTrace();
		}

		if(commEnt!=null ){

			Set<CtxEntityIdentifier> membersIDSet = commEnt.getMembers();

			for(CtxEntityIdentifier entityId  : membersIDSet){
				if (LOG.isDebugEnabled())LOG.debug(" retrieving entityIds for cisID "+ entityId);

				try{
					modelAttrIDList = this.ctxBroker.lookup(entityId, CtxModelType.ATTRIBUTE,CtxAttributeTypes.CAUI_MODEL).get();
					if (LOG.isDebugEnabled())LOG.debug(" retrieving modelAttrIDList for cisID "+ modelAttrIDList);

					if(modelAttrIDList.size()>0){
						for(CtxIdentifier attrID : modelAttrIDList){
						
							if (LOG.isDebugEnabled())LOG.debug(" retrieving uiModelAttr  "+ attrID);
							CtxAttribute uiModelAttr = (CtxAttribute) this.ctxBroker.retrieve(attrID).get();	
							
							//LOG.info(" retrieving attrID  "+ attrID);
							//CtxAttribute uiModelAttr = (CtxAttribute) this.ctxBroker.retrieve(attrID).get();	
							//getIdMananager.getThisNetworkNode().getBareJid()
							
						//	final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
						//	final String cssOwnerStr = cssNodeId.getBareJid();
						//	IIdentity cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
							
						//	RequestorCis requestor = new RequestorCis(cssOwnerId, cisId);
						//	CtxAttribute uiModelAttr = (CtxAttribute) this.ctxBroker.retrieve(requestor, attrID).get();
							

							if(uiModelAttr.getBinaryValue() != null){
								UserIntentModelData newUIModelData;
								try {
									newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());
									userModelList.add(newUIModelData);
								} catch (Exception e) {
									LOG.error("Exception while deserializing CAUI model for attrID:"+uiModelAttr.getId()+"."+ e.getLocalizedMessage());
									e.printStackTrace();
								}

							}
						}
					}


				} catch (Exception e) {
					LOG.error("Exception while retrieving CAUI model from : "+entityId +" "+e.getLocalizedMessage());
					e.printStackTrace();
				}

			}
		}
		if (LOG.isDebugEnabled())LOG.debug(" Retrieved CAUI Models:   "+ userModelList);
		return userModelList;
	}

}