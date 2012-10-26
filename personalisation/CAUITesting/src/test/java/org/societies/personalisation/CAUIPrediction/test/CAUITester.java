package org.societies.personalisation.CAUIPrediction.test;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUIPrediction.impl.CAUIPrediction;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;


/**
 * CAUITesting
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITester {



	private CAUITaskManager cauiTaskManager;
	private CAUIDiscovery cauiDiscovery;
	private CAUIPrediction cauiPrediction;
	ImportHistorySet historyService = new ImportHistorySet();

	public CAUITester(){


		cauiTaskManager = new CAUITaskManager();
		cauiPrediction = new CAUIPrediction();
		cauiDiscovery = new  CAUIDiscovery();
		
		cauiPrediction.setCauiTaskManager(cauiTaskManager);
		cauiDiscovery.setCauiTaskManager(cauiTaskManager);
	}

	public static void main(String[] args) {
		System.out.println("start 1 ");
		CAUITester cauiTest = new CAUITester();
		cauiTest.startTesting();
	}

	public void startTesting(){

		historyService.createContextHistoryAttributesSet();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = historyService.getHistory();
		historyService.printHistory(history);

		UserIntentModelData modelData = cauiDiscovery.generateNewUserModel(history);
		
		cauiTaskManager.updateModel(modelData);
		cauiPrediction.enablePrediction(true);
		
		print("modelData: "+modelData.getActionModel());


		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId1.setServiceInstanceIdentifier("http://testService1");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		
		System.out.println("par "+action1.getparameterName());
		System.out.println("val "+action1.getvalue());
		
		

		System.out.println("action1 "+action1);
		List<IUserIntentAction> actionsPredicted = cauiPrediction.performPrediction(null, action1);
		System.out.println("actions list "+actionsPredicted.size());
		//System.out.println("action predicted "+ actionsPredicted.toString());
		IUserIntentAction action = actionsPredicted.get(0);
		
		System.out.println("action predicted "+ action);
		System.out.println("action predicted conf level:"+ action.getConfidenceLevel());
		}

	private void print(String msg ){
		System.out.println(msg);
	}

}
