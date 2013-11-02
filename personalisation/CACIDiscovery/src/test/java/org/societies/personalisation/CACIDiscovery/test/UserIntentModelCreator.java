package org.societies.personalisation.CACIDiscovery.test;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUIDiscovery.impl.ActionDictObject;
import org.societies.personalisation.CAUIDiscovery.impl.ConstructUIModel;
import org.societies.personalisation.CAUIDiscovery.impl.MockHistoryData;
import org.societies.personalisation.CAUIDiscovery.impl.TransProbCalculator;


//remove after testing
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;


public class UserIntentModelCreator {

	private static Long nextValue = 0L;
	Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
	private CtxEntity operator = null;

	CAUIDiscovery discover = null;


	UserIntentModelCreator(){

		System.out.println("creating user intent model ");

		operator = createOperator();
		discover = new  CAUIDiscovery();
	}


	/*
	 * creates a user intent model based on input history
	 */
	
	public UserIntentModelData  createModel(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history){


		UserIntentModelData intentModelA = null;

		//System.out.println("1. create history");
		createContextHistoryAttributesSet();
		//	printHistory(this.mapHocData);


		//	System.out.println("2. Convert History Data");
		List<MockHistoryData> mockData = discover.convertHistoryData(history);

		//	System.out.println("Converted Data: "+mockData);

		//	System.out.println("3. Perform learning");
		HashMap<Integer,LinkedHashMap<List<String>,ActionDictObject>> dictionary = discover.generateTransitionsDictionaryAll(mockData);

		for(int i=1; i<= dictionary.size(); i++) {
			//		System.out.println("Print dictionary, step: "+i+" size:"+dictionary.get(i).size());
			//		printDictionary(dictionary.get(i));
		}

		HashMap<String,List<String>> ctxActionsMap =  discover.assignContextToAction(dictionary.get(1));
		//	System.out.println("4. assignContextToAction");
		//	System.out.println(ctxActionsMap);


		//	System.out.println("5. Calculate trans probabilites");
		//	// add context calculation in calcTrans2Prob
		//	System.out.println("5. step 2 ");

		try {
			TransProbCalculator transProb = new TransProbCalculator();
			LinkedHashMap<List<String>, HashMap<String, Double>> trans2ProbDictionary = transProb.calcTrans2Prob(dictionary.get(2));
			//		printTransProbDictionary(trans2ProbDictionary);
			//		System.out.println("5. step 3 ");
			//	LinkedHashMap<List<String>,HashMap<String,Double>> trans3ProbDictionary = transProb.calcTrans3Prob(dictionary.get(3));
			//		printTransProbDictionary(trans3ProbDictionary);

			//		System.out.println("6. Generate UserIntentModelData");
			ConstructUIModel cmodel = new ConstructUIModel(discover.getCauiTaskManager(),null); 
			//intentModelA = cmodel.constructNewModel(trans2ProbDictionary,ctxActionsMap,discover.getSriMap());
			intentModelA = cmodel.constructNewModel(trans2ProbDictionary,ctxActionsMap,discover.getSriMap(),mockData);
			//		System.out.println("*********** model created *******"+ intentModelA.getActionModel());
			/*
				for( IUserIntentAction userAction  : modelData.getActionModel().keySet()){
						System.out.println(userAction);
						//System.out.println(userAction.getActionContext());
				}
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		// model discovered

		return  intentModelA;
	}




	public void createContextHistoryAttributesSet(){

		//create actions
		//IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
			serviceId1.setServiceInstanceIdentifier("http://testService1");
			serviceId2.setServiceInstanceIdentifier("http://testService2");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId2, "testService", "volume", "low");
		IAction action3 = new Action(serviceId1, "testService", "volume", "mute");
		IAction actionX = new Action(serviceId1, "testService", "XXXX", "XXXX");
		IAction action4 = new Action(serviceId2, "testService", "colour", "blue");
		IAction action5 = new Action(serviceId2, "testService", "colour", "green");
		IAction actionY = new Action(serviceId1, "testService", "YYYY", "YYYY");
		//System.out.println ("action service ID "+actionY.getServiceID().getServiceInstanceIdentifier());
		for (int i=0; i<4; i++){

			monitorAction(action1,"country","free",10);
			monitorAction(action2,"office","busy",15);
			monitorAction(action3,"park","away",25);

			monitorAction(actionX,"park","away",25);
			monitorAction(actionY,"park","away",25);

			monitorAction(action4,"park","away",25);
			monitorAction(action5,"park","away",25);

			monitorAction(actionY,"park","away",25);
			monitorAction(actionX,"park","away",25);

		}
	}

	//******************************************************************
	// helper methods
	//******************************************************************

	private void monitorAction(IAction action, String location, String status, Integer temperature){

		CtxHistoryAttribute mockPrimaryHocActionAttrX = createMockHocActionAttr(action);
		List<CtxHistoryAttribute> escortingCtxDataX = new ArrayList<CtxHistoryAttribute>();
		CtxHistoryAttribute attrLocationX = createMockHocAttr(CtxAttributeTypes.LOCATION_SYMBOLIC,location);
		CtxHistoryAttribute attrStatusX = createMockHocAttr(CtxAttributeTypes.STATUS,status);
		CtxHistoryAttribute attrTemperatureX = createMockHocAttr(CtxAttributeTypes.TEMPERATURE,temperature);
		escortingCtxDataX.add(attrLocationX);
		escortingCtxDataX.add(attrStatusX);
		escortingCtxDataX.add(attrTemperatureX);
		this.mapHocData.put(mockPrimaryHocActionAttrX, escortingCtxDataX);

	}


	private CtxHistoryAttribute createMockHocAttr(String ctxAttrType, Serializable ctxAttrValue){

		CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),ctxAttrType.toString(),getNextValue());
		CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);

		if(ctxAttrValue instanceof String){
			ctxAttr.setStringValue(ctxAttrValue.toString());
		} else if (ctxAttrValue instanceof Double){
			ctxAttr.setDoubleValue((Double)ctxAttrValue);
		}else if (ctxAttrValue instanceof Integer){
			ctxAttr.setIntegerValue((Integer)ctxAttrValue);
		}
		CtxHistoryAttribute ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		return ctxHocAttr;
	}

	private CtxHistoryAttribute createMockHocActionAttr(IAction action){
		CtxHistoryAttribute ctxHocAttr = null;
		try {
			CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(operator.getId(),CtxAttributeTypes.LAST_ACTION,getNextValue());
			CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);
			ctxAttr.setBinaryValue(SerialisationHelper.serialise(action));
			ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxHocAttr;
	}

	private CtxEntity createOperator(){
		CtxEntityIdentifier ctxEntId = new CtxEntityIdentifier("operatorID","person",getNextValue());
		CtxEntity ctxEntity = new CtxEntity(ctxEntId);
		setOperatorEntity(ctxEntity);
		return ctxEntity;
	}

	private void setOperatorEntity(CtxEntity entity){
		operator = entity;
	}

	public static Long getNextValue() {
		return nextValue++;
	}

	//******************************************************************
	// printing methods
	//******************************************************************

	private void printHistory(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){
		int i = 0;
		for(CtxHistoryAttribute ctxHocAttr :mapHocData.keySet()){

			try {
				IAction action = (IAction)SerialisationHelper.deserialise(ctxHocAttr.getBinaryValue(), this.getClass().getClassLoader());
				List<CtxHistoryAttribute> escortingAttrList = mapHocData.get(ctxHocAttr);
				System.out.println(i+" primary Attr: {"+action.getparameterName() +" "+action.getvalue()+"} escorting: {" +escortingAttrList.get(0).getStringValue()+" "+escortingAttrList.get(1).getStringValue()+" "+escortingAttrList.get(2).getStringValue()+"}");
				i++;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		System.out.println ("**** printing dictionary contents *****");
		System.out.println ("**** total number of entries:" + dictionary.size());

		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();

			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.toString());
		}
	}


	public void printTransProbDictionary (LinkedHashMap<List<String>,HashMap<String,Double>> transProbDictionary){
		System.out.println("printing transition probabilites ");
		//System.out.println ("**** total number of entries:" + transProbDictionary.size());
		for(List<String> action : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(action);
			System.out.println("Action:"+action+ "| target: "+transTargets);
		}
	}

}