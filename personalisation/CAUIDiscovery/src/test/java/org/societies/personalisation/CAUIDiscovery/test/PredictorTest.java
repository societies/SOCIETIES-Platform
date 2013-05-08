package org.societies.personalisation.CAUIDiscovery.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;


public class PredictorTest {

	ICAUITaskManager cauiTaskManager = null;
	
	int predictionRequestsCounter = 0;
	int discoveryThreshold = 8;
	boolean modelExist = true;
	private Boolean enablePrediction = true;  

	CtxAttribute ctxAttrLocCurrent = null;
	CtxAttribute ctxAttrStatusCurrent = null;
	CtxAttribute ctxAttrTemperatureCurrent = null;
	
	
	private List<IAction> lastMonitoredActions = new ArrayList<IAction>();
	private List<IUserIntentAction> lastPredictedActions = new ArrayList<IUserIntentAction>();

	PredictorTest(){

	}

	public void setTaskModelManager(ICAUITaskManager taskManager){
		this.cauiTaskManager = taskManager;
	}

	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		predictionRequestsCounter = predictionRequestsCounter +1;
		this.recordMonitoredAction(action);

		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		if(modelExist == true && enablePrediction == true){

			//LOG.info("1. model exists " +modelExist);
			//LOG.info("START PREDICTION caui modelExist "+modelExist);
			//UIModelBroker setModel = new UIModelBroker(ctxBroker,cauiTaskManager);	
			//setActiveModel(requestor);
			String par = action.getparameterName();
			String val = action.getvalue();
			//LOG.info("2. action perf par:"+ par+" action val:"+val);
			//add code here for retrieving current context;

			// identify performed action in model
			List<IUserIntentAction> actionsList = this.cauiTaskManager.retrieveActionsByTypeValue(par, val);
			System.out.println("3. cauiTaskManager.retrieveActionsByTypeValue(par, val) " +actionsList);

			if(actionsList.size()>0){
	
				
				IUserIntentAction currentAction = findBestMatchingAction(actionsList);

				System.out.println("4. currentAction " +currentAction);
				Map<IUserIntentAction,Double> nextActionsMap = cauiTaskManager.retrieveNextActions(currentAction);	
				//LOG.info("5. nextActionsMap " +nextActionsMap);

				// no context
				if(nextActionsMap.size()>0){
					for(IUserIntentAction nextAction : nextActionsMap.keySet()){
						Double doubleConf = nextActionsMap.get(nextAction);
						//doubleConf = doubleConf*100;
						//doubleConf = 70.0;
						nextAction.setConfidenceLevel(doubleConf.intValue());
						System.out.println("6. nextActionsMap " +nextAction+" conf:"+doubleConf);
						
						results.add(nextAction);
						//LOG.info(" ****** prediction map created "+ results);
					}
				}			
			}
		} else {
			System.out.println("no CAUI model exist yet ");
		}
		
		//System.out.println(" getPrediction(IIdentity requestor, IAction action) "+ results);

		if(results.size()>0){
			for(IUserIntentAction predAction : results){
				this.recordPrediction(predAction);		
			}
		}

		//LOG.info("getPrediction based on action: "+ action+" identity requestor:"+requestor+" results:"+results);
		return new AsyncResult<List<IUserIntentAction>>(results);
	}

	/*
	 * Identify best matching action according to operator's current context and predicted actions context
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
							System.out.println("findBestMatchingAction: context type:"+ctxType +" does not match");
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


	
	
	
	private void recordPrediction(IUserIntentAction predAction){

		//LOG.info("predicted actions log: " +this.lastPredictedActions);

		if(this.lastPredictedActions.size()>100){
			this.lastPredictedActions.remove(0);
		}

		this.lastPredictedActions.add(predAction);
		//	LOG.info("store predicted action in log: " +predAction);
		//LOG.info("predicted actions log: " +this.lastPredictedActions);
	}



	private void recordMonitoredAction(IAction action){

		if(this.lastMonitoredActions.size()>100){
			this.lastMonitoredActions.remove(0);
		}
		this.lastMonitoredActions.add(action);
	}


	
	public CtxAttribute setOperatorsCtx(String type, Serializable value){
	
		CtxIdentifier id = null;
		CtxAttribute ctxAttr = null;
		
		try {
			if(type.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
				id = CtxIdentifierFactory.getInstance().fromString("context://university.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/locationSymbolic/7");	
				ctxAttrLocCurrent = CtxModelObjectFactory.getInstance().createAttribute((CtxAttributeIdentifier) id, new Date(), new Date(), value);
			} if (type.equals(CtxAttributeTypes.STATUS)){
				id = CtxIdentifierFactory.getInstance().fromString("context://university.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/status/8");
				ctxAttrStatusCurrent = CtxModelObjectFactory.getInstance().createAttribute((CtxAttributeIdentifier) id, new Date(), new Date(), value);
			} if(type.equals(CtxAttributeTypes.TEMPERATURE)){
				id = CtxIdentifierFactory.getInstance().fromString("context://university.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/temperature/9");
				ctxAttrTemperatureCurrent = CtxModelObjectFactory.getInstance().createAttribute((CtxAttributeIdentifier) id, new Date(), new Date(), value);
			}
					
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxAttr;
	}

	
	private CtxAttribute retrieveOperatorsCtx(String type){
		
		if(type.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			return ctxAttrLocCurrent;
		} if (type.equals(CtxAttributeTypes.STATUS)){
			return ctxAttrStatusCurrent;
		} if(type.equals(CtxAttributeTypes.TEMPERATURE)){
			return ctxAttrTemperatureCurrent; 
		}
			
		return null;
	}

}