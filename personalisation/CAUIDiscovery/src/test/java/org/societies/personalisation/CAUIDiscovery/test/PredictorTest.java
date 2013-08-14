package org.societies.personalisation.CAUIDiscovery.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.springframework.scheduling.annotation.AsyncResult;

public class PredictorTest {

	int predictionRequestsCounter = 0;
	int discoveryThreshold = 8;
	boolean modelExist = false;
	private Boolean enablePrediction = true;  
	
	private List<IAction> lastMonitoredActions = new ArrayList<IAction>();
	private List<IUserIntentAction> lastPredictedActions = new ArrayList<IUserIntentAction>();
	
	PredictorTest(){
				
	}
		
	public void setTaskModelManager(){
	
	}
		
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor,
			IAction action) {

		long startTime = System.currentTimeMillis();

		predictionRequestsCounter = predictionRequestsCounter +1;
		this.recordMonitoredAction(action);


		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();
	
/*
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
		} else {
			LOG.info("no CAUI model exist yet ");
		}
		//LOG.info(" getPrediction(IIdentity requestor, IAction action) "+ results);

		if(results.size()>0){
			for(IUserIntentAction predAction : results){
				this.recordPrediction(predAction);		
			}

			long endTime = System.currentTimeMillis();
			this.predictionPerformanceLog(endTime-startTime);

		}
*/
	//LOG.info("getPrediction based on action: "+ action+" identity requestor:"+requestor+" results:"+results);
		return new AsyncResult<List<IUserIntentAction>>(results);
	}


	private void recordMonitoredAction(IAction action){

		if(this.lastMonitoredActions.size()>100){
			this.lastMonitoredActions.remove(0);
		}
		this.lastMonitoredActions.add(action);
	}


}
