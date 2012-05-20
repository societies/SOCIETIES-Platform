package org.societies.personalisation.CAUIDiscovery.impl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class ConstructUIModel {

	private static Logger LOG = LoggerFactory.getLogger(ConstructUIModel.class);

	private ICAUITaskManager cauiTaskManager;
	//private ICtxBroker ctxBroker;

	public ConstructUIModel(ICAUITaskManager cauiTaskManager,ICtxBroker ctxBroker ){
		this.cauiTaskManager =  cauiTaskManager;
		//this.ctxBroker = ctxBroker;
	}


	private LinkedHashMap<String,HashMap<String,Double>> filterDictionary(LinkedHashMap<String,HashMap<String,Double>> dictionary, Double limit){

		LinkedHashMap<String,HashMap<String,Double>> filtered = new LinkedHashMap<String,HashMap<String,Double>>();
		for(String actions : dictionary.keySet()){
			HashMap<String,Double> transTargets = dictionary.get(actions);
			System.out.println("Action:"+actions+ "| target: "+transTargets);
			for(String targetAction : transTargets.keySet()){
				if(transTargets.get(targetAction) >= limit){
					filtered.put(actions, transTargets);
				}
			}
		}
		System.out.println("filterDictionary results for limit "+limit+ "| filtered: "+filtered);
		return filtered;
	}


	public UserIntentModelData constructNewModel(LinkedHashMap<String,HashMap<String,Double>> transDictionaryAll, HashMap<String,List<String>> ctxActionsMap){

		UserIntentModelData modelData = null;
		//create all actions and assign context
		for (String actionTemp : transDictionaryAll.keySet()){
			String [] action = actionTemp.split("\\/");
			//System.out.println ("paramName: "+action[0]+"paramValue: "+action[1]);
			IUserIntentAction userAction = cauiTaskManager.createAction(null,"ServiceType",action[0],action[1]);
	
			if(ctxActionsMap.get(actionTemp)!=null){
				List<String> contexValuesStringList = ctxActionsMap.get(actionTemp);
				HashMap<String,Serializable> context = new HashMap<String,Serializable>();
				for(String contextTypeValueConc : contexValuesStringList){
					String [] contextTypeValue = contextTypeValueConc.split("=");
					String contextType = contextTypeValue[0];
					String contextValue = contextTypeValue[1];
					context.put(contextType, contextValue);
				}		
				userAction.setActionContext(context);	
			}
			//System.out.println ("act id :"+userAction.getActionID()+" context :"+userAction.getActionContext());
		}

		// set links among actions
		for (String sourceActionConc : transDictionaryAll.keySet()){

			String [] sourceAction = sourceActionConc.split("\\/");
			List<IUserIntentAction> sourceActionList = cauiTaskManager.retrieveActionsByTypeValue(sourceAction[0],sourceAction[1]);
		    //System.out.println(" List<IUserIntentAction> actionList1 "+ actionList1);
			IUserIntentAction sourceActionObj = sourceActionList.get(0);
			
			HashMap<String,Double> targetActionsMap = transDictionaryAll.get(sourceActionConc);
			for(String targetActionString : targetActionsMap.keySet()){
				String [] actionStringTarg = targetActionString.split("\\/");
								
				Double transProb = targetActionsMap.get(targetActionString);
		
				List<IUserIntentAction> actionObjTargetList = cauiTaskManager.retrieveActionsByTypeValue(actionStringTarg[0],actionStringTarg[1]);
				// more than one target action might exist with the same param and value!!
				IUserIntentAction targetActionObj = actionObjTargetList.get(0);
				cauiTaskManager.setActionLink(sourceActionObj, targetActionObj, transProb);	
			}
		}		 
		modelData  = cauiTaskManager.retrieveModel();
	
		return modelData;
	}

	public void printTransProbDictionary (LinkedHashMap<String,HashMap<String,Double>> transProbDictionary){

		System.out.println ("** ConstructUIModel ** total number of entries: " + transProbDictionary.size());
		for(String actions : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(actions);
			System.out.println("Action:"+actions+ "| target: "+transTargets);
		}
	}	
	
}