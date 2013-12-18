package org.societies.personalisation.CACIDiscovery.test;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CACIDiscovery.impl.CACIDiscovery;
import org.societies.personalisation.CACIDiscovery.impl.UIModelSimilarityEval;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;


public class CACIDiscoveryNonJunitTests {

	CACIDiscovery discovery = null;
	static ICAUITaskManager cauiTaskManager = null;

	private static final String SERVICE_SRI1 = "css://requestor.societies.org/HelloWorld";
	private static final String SERVICE_TYPE = "radio_service";
	private static ServiceResourceIdentifier serviceSri1;

	//private static final String SERVICE_SRI2 = "css://requestor.societies.org/HelloWorld";
	//private static ServiceResourceIdentifier serviceSri2;


	CACIDiscoveryNonJunitTests() throws URISyntaxException{

		serviceSri1 = new ServiceResourceIdentifier();
		serviceSri1.setServiceInstanceIdentifier(SERVICE_SRI1);
		serviceSri1.setIdentifier(new URI(SERVICE_SRI1));

		/*
		serviceSri2 = new ServiceResourceIdentifier();
		serviceSri2.setServiceInstanceIdentifier(SERVICE_SRI2);
		serviceSri2.setIdentifier(new URI(SERVICE_SRI2));
		 */
		discovery = new CACIDiscovery();
		cauiTaskManager = discovery.getCauiTaskManager();
	}

	public void testCommunityModelGeneration() {

		UserIntentModelData modelA = createCAUIModelA();
		UserIntentModelData modelB = createCAUIModelB();

		System.out.println("printModel(modelA)");
		printModel(modelA);
		System.out.println("printModel(modelB)");
		printModel(modelB);

		List<UserIntentModelData> uiModelList = new ArrayList<UserIntentModelData>();
		uiModelList.add(modelA);
		uiModelList.add(modelB);
		//	uiModelList.add(modelC);

		//discovery.generateNewCommunityModel(uiModelList);
		UserIntentModelData merged = discovery.mergeModels(uiModelList);
		//System.out.println("1 merged:"+ merged);
		//System.out.println("2 merged:"+ merged.getActionModel().keySet());
		//System.out.println("3 merged:"+ merged.getActionModel());

		System.out.println("printModel(merged)");
		printModel(merged);


		System.out.println("context merge");



	}




	public static UserIntentModelData createCAUIModelA() {



		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context1.put(CtxAttributeTypes.HOUR_OF_DAY, 1);
		context1.put(CtxAttributeTypes.DAY_OF_WEEK, "monday");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context2.put(CtxAttributeTypes.HOUR_OF_DAY, 2);
		context2.put(CtxAttributeTypes.DAY_OF_WEEK, "tuesday");


		HashMap<String,Serializable> context3 = new HashMap<String,Serializable>();
		context3.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context3.put(CtxAttributeTypes.HOUR_OF_DAY, 3);
		context3.put(CtxAttributeTypes.DAY_OF_WEEK, "tuesday");


		UserIntentModelData modelDataA = cauiTaskManager.createModel();


		//IUserIntentAction userActionOn1 = new UserIntentAction(serviceSri ,SERVICE_TYPE,"A","on",10L);
		IUserIntentAction userActionOn1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"A","A");
		userActionOn1.setActionContext(context1);

		//IUserIntentAction userActionSetVol = new UserIntentAction(serviceSri ,SERVICE_TYPE,"SetVolume","medium",11L);
		IUserIntentAction userActionSetVol = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"B","B");
		userActionSetVol.setActionContext(context1);

		//IUserIntentAction userActionSetChannel = new UserIntentAction(serviceSri ,SERVICE_TYPE,"SetChannel","radio1",11L);
		IUserIntentAction userActionSetChannel = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"C","C");
		userActionSetChannel.setActionContext(context3);

		//IUserIntentAction userActionOff1 = new UserIntentAction(serviceSri ,SERVICE_TYPE,"A","off",12L);
		IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"D","D");
		userActionOff1.setActionContext(context2);



		//On --> setVol 0.5
		cauiTaskManager.setActionLink(userActionOn1, userActionSetVol, 0.3d);	
		cauiTaskManager.setActionLink(userActionOn1, userActionSetChannel, 0.7d);
		cauiTaskManager.setActionLink(userActionSetVol, userActionOff1, 1.0d);	
		cauiTaskManager.setActionLink(userActionSetChannel, userActionOff1, 1.0d);	

		modelDataA  = cauiTaskManager.retrieveModel();
		System.out.println("A CAUI modelData ::"+modelDataA.getActionModel());

		return modelDataA;
	}




	public static UserIntentModelData createCAUIModelB(){


		HashMap<String,Serializable> context1 = new HashMap<String,Serializable>();
		context1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		context1.put(CtxAttributeTypes.HOUR_OF_DAY, 2);
		context1.put(CtxAttributeTypes.DAY_OF_WEEK, "tuesday");

		HashMap<String,Serializable> context2 = new HashMap<String,Serializable>();
		context2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		context2.put(CtxAttributeTypes.HOUR_OF_DAY, 1);
		context2.put(CtxAttributeTypes.DAY_OF_WEEK, "monday");

		UserIntentModelData modelData = cauiTaskManager.createModel();

		IUserIntentAction actA = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"A","A");
		actA.setActionContext(context1);

		//IUserIntentAction userActionOff1 = cauiTaskManager.createAction(serviceSri ,SERVICE_TYPE,"B","off");
		IUserIntentAction actB = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"B","B");
		actB.setActionContext(context1);

		IUserIntentAction actE = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"E","E");

		IUserIntentAction actD = cauiTaskManager.createAction(serviceSri1 ,SERVICE_TYPE,"D","D");
		actD.setActionContext(context1);

		//On --> setVol 0.5
		cauiTaskManager.setActionLink(actA, actB, 0.8d);	
		cauiTaskManager.setActionLink(actA, actE, 0.2d);
		cauiTaskManager.setActionLink(actE, actD, 1.0d);
		cauiTaskManager.setActionLink(actB, actE, 1.0d);

		modelData  = cauiTaskManager.retrieveModel();
		System.out.println("B CAUI modelData ::"+modelData.getActionModel());
		//printModel(modelData);

		return modelData;
	}



	private static void printModel(UserIntentModelData model){

		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> allActions = new HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();
		allActions = model.getActionModel();
		//System.out.println("------------printModel start ---------------");
		for(IUserIntentAction action : allActions.keySet()){
			System.out.println("source:"+action.getparameterName()+"/"+action.getvalue() +" ctx:"+ action.getActionContext());

			if(allActions.get(action)!=null){


				HashMap<IUserIntentAction, Double> targetActions = allActions.get(action);
				for(IUserIntentAction actionTarget : targetActions.keySet()){
					System.out.println("--> target:"+actionTarget.getparameterName()+"/"+actionTarget.getvalue()+"/"+ targetActions.get(actionTarget)/*+" ctx:"+ actionTarget.getActionContext()*/);	
				}
			}
		}
		System.out.println("------------printModel end ---------------");
	}




	public static void main(String[] args) throws URISyntaxException {
		CACIDiscoveryNonJunitTests cdt = new CACIDiscoveryNonJunitTests();
		cdt.testCommunityModelGeneration();
	}


}
