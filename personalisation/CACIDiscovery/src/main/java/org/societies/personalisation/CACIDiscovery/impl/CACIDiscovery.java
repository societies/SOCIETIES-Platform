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
import org.societies.api.internal.context.broker.ICtxBroker;
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

	public CACIDiscovery(){
		//cauiTaskManager = new CAUITaskManager();	
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
		LOG.info(this.getClass().getName()+": Got commsMgr");
		this.commsMgr = commsMgr;
	}

	public ICommManager getCommsMgr() {
		LOG.info(this.getClass().getName()+": Return CommsMgr");
		return commsMgr;
	}




	@Override
	public void generateNewCommunityModel(List<UserIntentModelData> userModelList) {

		//CACI model
		HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> communityActionsMap = new HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>>();

		// create translation map
		// translate actions of various user models to user models containing the same set of actions
		// key:commAction -- value: set of user actions
		Map<IUserIntentAction, Set<IUserIntentAction>> translationMap =  createTranslationMap (userModelList);
		List<UserIntentModelData> userModelsComActionsList = convertUserToCommModels(userModelList,translationMap);

		// A) create a map (communityActionsMap) with key each individual userAction and B) value a map with all possible targets 

		// A)
		// get each user intent model
		for(UserIntentModelData userModel : userModelsComActionsList){

			// get actions for this model
			HashMap<IUserIntentAction,HashMap<IUserIntentAction, Double>> actionsMap = userModel.getActionModel();

			// add source actions from all models to map
			for(IUserIntentAction communitySourceAction : actionsMap.keySet()){
				//if(!mapContainsKeyAction(communityActionsMap,action)){
				if(!communityActionsMap.keySet().contains(communitySourceAction)){
					communityActionsMap.put(communitySourceAction, new HashMap<IUserIntentAction,Double>());
				} 
			}
		}

		//System.out.println(" initial communityActionsMap : "+communityActionsMap);
		this.printCACIModel(communityActionsMap);		
		//		printModels(userModelList);

		
		int i =0;
		for(UserIntentModelData userModel : userModelList){
			i++;
			System.out.println(" " );
			System.out.println("model i= "+i );
			HashMap<IUserIntentAction,  HashMap<IUserIntentAction,Double>> uiModelActions = userModel.getActionModel();

			for (IUserIntentAction sourceUserAct  :uiModelActions.keySet()){
				System.out.println("sourceUserAct " +sourceUserAct);
				
				//iterate through commActions and find a commAction similar with user action
				for(IUserIntentAction sourceComAct : communityActionsMap.keySet()){

					if(this.equalActions(sourceUserAct, sourceComAct)){
						// get targets for this userAct
						HashMap<IUserIntentAction,Double> targetUserActions = uiModelActions.get(sourceUserAct);
						// translate targets to commAct ?
						HashMap<IUserIntentAction,Double> targetComActionNew =  translateUsertoComActionMap(translationMap, targetUserActions);
						System.out.println("targetComActionNew " +targetComActionNew);
						
						// if null or map is empty
						if(communityActionsMap.get(sourceComAct) == null || communityActionsMap.get(sourceComAct).size() == 0){
							communityActionsMap.put(sourceComAct, targetComActionNew);

						} if(communityActionsMap.get(sourceComAct).size() >0 ){
							HashMap<IUserIntentAction,Double> targetComActionExisting = communityActionsMap.get(sourceComAct);
						
							HashMap<IUserIntentAction,Double> updatedTargetComMap = mergeTargetMaps(targetComActionNew,targetComActionExisting);
							communityActionsMap.put(sourceComAct, updatedTargetComMap);
						}

					}

				}
			}
			this.printCACIModel(communityActionsMap);
		}
	
		this.printCACIModel(communityActionsMap);
		UserIntentModelData communityModel = new UserIntentModelData();
		communityModel.setActionModel(communityActionsMap);
		storeModel(communityModel);
		
	
	}

	
	/*
	 * store model to ctx DB as a community ctx Attribute of community Entity defined by cis
	 */
	
	private void storeModel(UserIntentModelData communityModel){
	//TODO store model 	
	}

	/*
	 * merges the target action transition probs
	 */
	public HashMap<IUserIntentAction,Double> mergeTargetMaps(HashMap<IUserIntentAction,Double> targetComActionNew, 
			HashMap<IUserIntentAction,Double>targetComActionExisting){

		HashMap<IUserIntentAction,Double> result = new HashMap<IUserIntentAction,Double>();
		result.putAll(targetComActionExisting);

		if( targetComActionExisting.size() == 0 ) {
			return targetComActionNew;
		}

		for(IUserIntentAction newMapAction : targetComActionNew.keySet()){
			
			System.out.println("current Action " +newMapAction);
			if(targetComActionExisting.get(newMapAction) != null){
				System.out.println("result" +result);
				Double existingTransProb = targetComActionExisting.get(newMapAction);
				Double newTransProb = targetComActionNew.get(newMapAction);
				Double updatedTrandProb = (existingTransProb+newTransProb);
				result.put(newMapAction, updatedTrandProb);
				
				
			} else  {
				System.out.println("result" +result);
				Double newTransProb = targetComActionNew.get(newMapAction);
				result.put(newMapAction, newTransProb);
				
			}
		//TODO fix probabilities 
		// should sum to 1
			for(IUserIntentAction actionTemp :result.keySet()){
				Double currentProb = result.get(actionTemp);
				result.put(actionTemp, currentProb/2);
			}
		}
		return result;
	}


	/*
	 * convert target user actions in map to community user actions in map
	 */
	private HashMap<IUserIntentAction,Double> translateUsertoComActionMap(Map<IUserIntentAction, Set<IUserIntentAction>> translationMap ,HashMap<IUserIntentAction,Double> targetUserActions){

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


	private Boolean mapContainsKeyAction(Map<IUserIntentAction, ?> tranlationMap,  IUserIntentAction action){

		for(IUserIntentAction actionKey : tranlationMap.keySet()){
			//	System.out.println("action 1 "+action +" action 2"+ actionKey);	
			if(equalActions (actionKey,action) ){
				//		System.out.println("EQUAL");
				return true;
			}
		}

		return false;
	}



	private Boolean equalActions(IUserIntentAction actionA, IUserIntentAction actionB){

		//	System.out.println("A "+actionA.getServiceID().getServiceInstanceIdentifier().toString() +" b "+actionB.getServiceID().getServiceInstanceIdentifier().toString());
		//	System.out.println("A "+actionA.getparameterName() +" b "+actionB.getparameterName());
		//	System.out.println("A "+actionA.getvalue() +" b "+actionB.getvalue());

		if(actionA.getServiceID().getServiceInstanceIdentifier().toString().equals(actionB.getServiceID().getServiceInstanceIdentifier().toString()) && actionA.getparameterName().equals(actionB.getparameterName()) 
				&& actionA.getvalue().equals(actionB.getvalue())) {
			//		System.out.println( "!!!!!!!!!! MATCH");
			return true;
		}
		return false;
	}




	public Boolean compareModels(UserIntentModelData modelA, UserIntentModelData modelB){

		return false;
	}




	/*
	 *  need to add a method that translates actions with same serviceId, par,val to a unique community action.
	 *  converts same actions of different ui models to unique actions for all models  
	 *  
	 *  e.g. communityActA = {userActA1,userActA2,userActA3}
	 *  userActionModel {http://testService2#volume=low/1={http://testService1#volume=mute/2=1.0}, http://testService1#YYYY=YYYY/4={http://testService1#XXXX=XXXX/3=0.5, http://testService2#colour=blue/5=0.5}, http://testService1#XXXX=XXXX/3={http://testService1#YYYY=YYYY/4=0.5714285714285714, http://testService1#volume=high/0=0.42857142857142855}, http://testService1#volume=high/0={http://testService2#volume=low/1=1.0}, http://testService2#colour=green/6={http://testService1#YYYY=YYYY/4=1.0}, http://testService2#colour=blue/5={http://testService2#colour=green/6=1.0}, http://testService1#volume=mute/2={http://testService1#XXXX=XXXX/3=1.0}}
	 *  communityActionModel {http://testService2#volume=low/17={http://testService1#volume=mute/23=1.0}, http://testService1#YYYY=YYYY/18={http://testService1#XXXX=XXXX/19=0.5, http://testService2#colour=blue/22=0.5}, http://testService1#XXXX=XXXX/19={http://testService1#YYYY=YYYY/18=0.5714285714285714, http://testService1#volume=high/20=0.42857142857142855}, http://testService1#volume=high/20={http://testService2#volume=low/17=1.0}, http://testService2#colour=green/21={http://testService1#YYYY=YYYY/18=1.0}, http://testService2#colour=blue/22={http://testService2#colour=green/21=1.0}, http://testService1#volume=mute/23={http://testService1#XXXX=XXXX/19=1.0}}
	 *
	 */

	public List<UserIntentModelData> convertUserToCommModels (List<UserIntentModelData> userModelList, Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap){

		List<UserIntentModelData> translatedModels = new ArrayList<UserIntentModelData>();

		/*
		Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap = new HashMap<IUserIntentAction, Set<IUserIntentAction>>();
		//create action translator map
		for (UserIntentModelData userModel : userModelList){

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> userActionModel = userModel.getActionModel();
			//	HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> communityActionModel = new HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

			for(IUserIntentAction userAction  : userActionModel.keySet()){

				if(! mapContainsKeyAction(tranlationMap,userAction)){	
					IUserIntentAction commAction = convertAction(userAction);
					//if(!tranlationMap.containsKey(commAction)){
					Set<IUserIntentAction> newUserActionSet = new HashSet<IUserIntentAction>();
					newUserActionSet.add(userAction);
					tranlationMap.put(commAction, newUserActionSet);
				} else {
					//System.out.println("commAction " + commAction);
					IUserIntentAction commAction = findComAction(tranlationMap.keySet(), userAction);
					Set<IUserIntentAction> currentActionSet  = tranlationMap.get(commAction);
					//System.out.println("currentActionSet " + currentActionSet);
					currentActionSet.add(userAction);
					tranlationMap.put(commAction, currentActionSet);
				}
			}
		}		
		printTranlationMap(tranlationMap);
		// translation map created 
		 */
		// translate userActionModel to community action model
		for (UserIntentModelData userModel : userModelList){

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> userActionModel = userModel.getActionModel();
			System.out.println("userActionModel " + userActionModel);

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> communityActionModel =
					new HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

			for(IUserIntentAction userAction  : userActionModel.keySet()){
				IUserIntentAction commAction = translateUserToComAction(tranlationMap,userAction);
				HashMap<IUserIntentAction,Double> userTargetMap = userActionModel.get(userAction);
				HashMap<IUserIntentAction,Double> communityTargetMap = new HashMap<IUserIntentAction,Double>();

				for(IUserIntentAction userTargetAction : userTargetMap.keySet()){
					IUserIntentAction commTargetAction = translateUserToComAction(tranlationMap,userTargetAction);
					communityTargetMap.put(commTargetAction, userTargetMap.get(userTargetAction));
				}
				communityActionModel.put(commAction, communityTargetMap);
			}
			System.out.println("communityActionModel " + communityActionModel);

			UserIntentModelData communityModel = new UserIntentModelData();
			communityModel.setActionModel(communityActionModel);
			translatedModels.add(communityModel);
		}
		// translate userActionModel to community action model, finished

		return translatedModels ;
	}




	public Map<IUserIntentAction, Set<IUserIntentAction>> createTranslationMap (List<UserIntentModelData> userModelList){
		Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap = new HashMap<IUserIntentAction, Set<IUserIntentAction>>();


		//create action translator map
		for (UserIntentModelData userModel : userModelList){

			HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> userActionModel = userModel.getActionModel();
			//	HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> communityActionModel = new HashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

			for(IUserIntentAction userAction  : userActionModel.keySet()){

				if(! mapContainsKeyAction(tranlationMap,userAction)){	
					IUserIntentAction commAction = convertAction(userAction);
					//if(!tranlationMap.containsKey(commAction)){
					Set<IUserIntentAction> newUserActionSet = new HashSet<IUserIntentAction>();
					newUserActionSet.add(userAction);
					tranlationMap.put(commAction, newUserActionSet);
				} else {
					//System.out.println("commAction " + commAction);
					IUserIntentAction commAction = findComAction(tranlationMap, userAction);
					Set<IUserIntentAction> currentActionSet  = tranlationMap.get(commAction);
					//System.out.println("currentActionSet " + currentActionSet);
					currentActionSet.add(userAction);
					tranlationMap.put(commAction, currentActionSet);
				}
			}
		}		
		printTranlationMap(tranlationMap);
		// translation map created 

		return tranlationMap;
	}



	private IUserIntentAction translateUserToComAction(Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap,IUserIntentAction userAction ){

		IUserIntentAction communityAction = null;

		for(IUserIntentAction communityActionTemp : tranlationMap.keySet()){
			Set<IUserIntentAction> actionSet = tranlationMap.get(communityActionTemp);
			if(actionSet.contains(userAction)) return communityActionTemp;
		}

		return communityAction; 
	}



	/*
	 * retrieves a community action identical with the user action declared 
	 */
	protected IUserIntentAction findComAction(Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap, IUserIntentAction userAction ){

		//	IUserIntentAction communityAction = null;

		for(IUserIntentAction communityAction : tranlationMap.keySet() ){
			if(this.equalActions(communityAction, userAction)) {
				return communityAction;
			}
		}
		return null;
	}


	/*
	 * creates a new community action based on the details of the userAction 
	 */
	private IUserIntentAction convertAction (IUserIntentAction userAction){

		IUserIntentAction commAction = null;

		if(userAction.getServiceID() != null && userAction.getparameterName() != null 
				&& userAction.getvalue() != null && userAction.getServiceType()!=null){

			commAction = this.cauiTaskManager.createAction(userAction.getServiceID(), userAction.getServiceType(), 
					userAction.getparameterName(), userAction.getvalue());
			commAction.setCommunity(true);
			// TODO set action context 
		}
		return commAction;
	}


	/*
	 * print methods  
	 */
	void printTranlationMap(Map<IUserIntentAction, Set<IUserIntentAction>> tranlationMap){
		System.out.println("tranlationMap start --- ");
		for(IUserIntentAction action : tranlationMap.keySet()){
			System.out.println("tranlated action "+action +tranlationMap.get(action) );	
		}	
		System.out.println("tranlationMap ends --- ");
	}

/*
	private void printModels(List<UserIntentModelData> modelList){

		for(UserIntentModelData uimodel : modelList){
			System.out.println(uimodel.getActionModel());
		}
	}
*/

	private void printCACIModel(Map<IUserIntentAction, HashMap<IUserIntentAction,Double>> map){

		System.out.println("caci model");
		for( IUserIntentAction sourceAct : map.keySet()){
			System.out.println("sourceAct "+ sourceAct +" target "+map.get(sourceAct) );
		}
	}	



	@Override
	public void generateNewCommunityModel() {

	}

}
