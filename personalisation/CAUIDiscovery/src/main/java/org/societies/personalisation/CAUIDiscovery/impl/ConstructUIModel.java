package org.societies.personalisation.CAUIDiscovery.impl;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
//import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class ConstructUIModel {

	private static Logger LOG = LoggerFactory.getLogger(ConstructUIModel.class);

	private ICAUITaskManager cauiTaskManager;
	//private ICtxBroker ctxBroker;

	public ConstructUIModel(ICAUITaskManager cauiTaskManager,ICtxBroker ctxBroker ){
		this.cauiTaskManager =  cauiTaskManager;
		//this.ctxBroker = ctxBroker;
	}

	public ConstructUIModel(ICAUITaskManager cauiTaskManager ){
		this.cauiTaskManager =  cauiTaskManager;
		
	}
	
	public LinkedHashMap<String,HashMap<String,Double>> filterDictionary(LinkedHashMap<String,HashMap<String,Double>> dictionary, Double limit){

		LinkedHashMap<String,HashMap<String,Double>> filtered = new LinkedHashMap<String,HashMap<String,Double>>();
		for(String actions : dictionary.keySet()){
			HashMap<String,Double> transTargets = dictionary.get(actions);
			//System.out.println("Action:"+actions+ "| target: "+transTargets);
			for(String targetAction : transTargets.keySet()){
				if(transTargets.get(targetAction) >= limit){
					filtered.put(actions, transTargets);
				}
			}
		}
		//System.out.println("filterDictionary results for limit "+limit+ "| filtered: "+filtered);
		LOG.debug("filterDictionary results for limit "+limit+ "| filtered: "+filtered);
		
		return filtered;
	}


	public UserIntentModelData constructNewModel(LinkedHashMap<List<String>,HashMap<String,Double>> transDictionaryAll, HashMap<String,List<String>> ctxActionsMap,
			Map<String , ServiceResourceIdentifier> sriMap, List<MockHistoryData> mockDataList ){

		if (LOG.isDebugEnabled())LOG.debug("constructNewModel ... cauiTaskManager "+cauiTaskManager);
		UserIntentModelData modelData = cauiTaskManager.createModel();
		
		//create all actions and assign context
		for (List<String> actionTempList : transDictionaryAll.keySet()){
			String actionTemp  = actionTempList.get(0);
			
			String [] action = actionTemp.split("\\#");
			// add here the serviceID /
			//LOG.info("action details > serviceID: "+action[0]+" paramName: "+action[1]+" paramValue:"+action[2]);
			String serviceStringId = action[0];
			//System.out.println("serviceId="+serviceId);
			
			ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
			
			if(!sriMap.isEmpty() && sriMap.containsKey(serviceStringId)){
				sri  = sriMap.get(serviceStringId);
			}
			
			IUserIntentAction userAction = cauiTaskManager.createAction(sri,action[3],action[1],action[2]);
			LOG.debug("userAction created "+userAction);
			LOG.debug("userAction service id "+userAction.getServiceID());
			LOG.debug("userAction service instance id "+userAction.getServiceID().getServiceInstanceIdentifier());
			LOG.debug("userAction service id "+userAction.getServiceID().getIdentifier());
			//LOG.debug("userAction ctxActionsMap"+ctxActionsMap);
			LOG.debug("userAction service id "+userAction.getServiceID().getIdentifier());
			
			//System.out.println("userAction created "+userAction);
			//System.out.println("userAction service id "+userAction.getServiceID().getIdentifier());
			
			for(MockHistoryData mockHocData : mockDataList  ){
				
				//System.out.println("mockHocData Action: "+mockHocData);
				//System.out.println("mockHocData Action serviceID: "+mockHocData.getServiceId());
				
				if( mockHocData.getParameterName().equals(userAction.getparameterName()) && mockHocData.getActionValue().equals(userAction.getvalue())
						&& mockHocData.getServiceId().equals(userAction.getServiceID().getServiceInstanceIdentifier())){
							
							if(mockHocData.getIsImplementable()) {
								userAction.setImplementable(true);
								LOG.debug("IMPLEMENTABLE SET true");
								//System.out.println("IMPLEMENTABLE SET true "+ userAction.isImplementable());
							}	else if (!mockHocData.getIsImplementable()){
								LOG.debug("IMPLEMENTABLE SET false");
								userAction.setImplementable(false);
								//System.out.println("IMPLEMENTABLE SET false "+ userAction.isImplementable());
							}
							
							if(mockHocData.getIsProactive()) {
								userAction.setProactive(true);
								LOG.debug("Proactive SET true");
								//System.out.println("IMPLEMENTABLE SET true "+ userAction.isImplementable());
							}	else if (!mockHocData.getIsProactive()){
								LOG.debug("Proactive SET false");
								userAction.setProactive(false);
								//System.out.println("IMPLEMENTABLE SET false "+ userAction.isImplementable());
							}
							
				break;
				}
			}
			
			
			if(ctxActionsMap.get(actionTemp)!=null){
				List<String> contexValuesStringList = ctxActionsMap.get(actionTemp);
				HashMap<String,Serializable> context = new HashMap<String,Serializable>();
				for(String contextTypeValueConc : contexValuesStringList){
					String [] contextTypeValue = contextTypeValueConc.split("=");
					//LOG.info("constructNewModel "+contextTypeValue);
					if(contextTypeValue.length == 2 ){
						String contextType = contextTypeValue[0];
						String contextValue = contextTypeValue[1];
						//LOG.info("constructNewModel type:"+contextType+" value:"+contextValue);
						// if value is hod , store it as integer
						if(contextType.equals(CtxAttributeTypes.HOUR_OF_DAY)){
							Integer hodIntValue = Integer.parseInt(contextValue);
							context.put(contextType, hodIntValue);
						}else context.put(contextType, contextValue);
					}
				}		
				userAction.setActionContext(context);	
			}
			//	LOG.info("3 act id :"+userAction.getActionID()+" context :"+userAction.getActionContext());
		}

		// set links among actions
		//	LOG.info("4 set links among actions");
		for (List<String> sourceActionConcList : transDictionaryAll.keySet()){
			//System.out.println("sourceActionConcList "+sourceActionConcList);
			
			String sourceActionConc = sourceActionConcList.get(0);
			//System.out.println("sourceActionConc "+sourceActionConc);
			
			String [] sourceAction = sourceActionConc.split("\\#");
			
			
			List<IUserIntentAction> sourceActionList = cauiTaskManager.retrieveActionsByTypeValue(sourceAction[1],sourceAction[2]);
		//	System.out.println("sourceActionList "+sourceActionList);
			//	LOG.info("5 sourceActionList "+ sourceActionList);
			IUserIntentAction sourceActionObj = sourceActionList.get(0);
			
		//	System.out.println("sourceActionObj "+sourceActionObj);
			
			HashMap<String,Double> targetActionsMap = transDictionaryAll.get(sourceActionConcList);
			
			
			//System.out.println("transDictionaryAll "+transDictionaryAll);
			//System.out.println("targetActionsMap "+targetActionsMap);
			//	LOG.info("6 targetActionsMap "+ targetActionsMap);
			for(String targetActionString : targetActionsMap.keySet()){
				String [] actionStringTarg = targetActionString.split("\\#");

				Double transProb = targetActionsMap.get(targetActionString);
				//	LOG.info("7a actionStringTarg[0] "+ actionStringTarg[0]);
				//		LOG.info("7b actionStringTarg[1] "+ actionStringTarg[1]);

				List<IUserIntentAction> actionObjTargetList = cauiTaskManager.retrieveActionsByTypeValue(actionStringTarg[1],actionStringTarg[2]);
				// more than one target action might exist with the same param and value!!
				//	LOG.info("8 actionObjTargetList "+ actionObjTargetList);

				if(actionObjTargetList.size()>0){

					IUserIntentAction targetActionObj = actionObjTargetList.get(0);
					//		LOG.info("9 targetActionObj "+ targetActionObj);
					cauiTaskManager.setActionLink(sourceActionObj, targetActionObj, transProb);	
				}
			}
		}		 
		
		modelData  = cauiTaskManager.retrieveModel();
		//LOG.info("10 modelData action model:"+ modelData.getActionModel());
		return modelData;
	}

	
	/*
	 * debugging helper class
	 */
	public void printTransProbDictionary (LinkedHashMap<String,HashMap<String,Double>> transProbDictionary){

		//System.out.println ("** ConstructUIModel ** total number of entries: " + transProbDictionary.size());
		for(String actions : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(actions);
			System.out.println("Action:"+actions+ "| target: "+transTargets);
		}
	}	

}