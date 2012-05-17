package org.societies.personalisation.CAUIDiscovery.test;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;


import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUIDiscovery.impl.ActionDictObject;
import org.societies.personalisation.CAUIDiscovery.impl.ConstructUIModel;
import org.societies.personalisation.CAUIDiscovery.impl.MockHistoryData;
import org.societies.personalisation.CAUIDiscovery.impl.TransitionProbabilitiesCalc;

public class CAUIDiscoveryLearningTest {


	private static Long nextValue = 0L;

	private CtxEntity operator = null;
	CAUIDiscovery discover = null;

	Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();


	CAUIDiscoveryLearningTest(){
		discover = new  CAUIDiscovery();
		operator = createOperator();

	}

	private void  startTesting(){

		//use option 1 or 2

		//1. create mock data sets
		//List<MockHistoryData> historySet = createMockHistorySet();
		//System.out.println("createHistorySet() size "+historySet.size());

		//2.create mock data sets based on History Attribute Tuples
		//List<MockHistoryData> historySet = convertHistoryData(createContextHistoryAttributesSet());

		//3.create mock data sets based on History Attribute Tuples containing Actions
		System.out.println("create history with actions");
		//Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> historyTuples = createContextHistoryAttributesSet();
		
		createContextHistoryAttributesSet();
		System.out.println(this.mapHocData);

		System.out.println("2. Convert History Data");
		List<MockHistoryData> mockData = discover.convertHistoryData(this.mapHocData);

		System.out.println("mockData "+mockData);

		LinkedHashMap<List<String>,ActionDictObject> currentActCtxDictionary = discover.generateTransitionsDictionary(mockData);
		System.out.println("currentActCtxDictionary "+currentActCtxDictionary);


		TransitionProbabilitiesCalc transProb  = new TransitionProbabilitiesCalc(currentActCtxDictionary);

		LinkedHashMap<String,HashMap<String,Double>> trans2ProbDictionary = transProb.calcTrans2Prob();	
		//printTransProbDictionary(trans2ProbDictionary);
		
		LinkedHashMap<String,HashMap<String,Double>> trans3ProbDictionary = transProb.calcTrans3Prob();
		//printTransProbDictionary(trans3ProbDictionary);
		
		
		System.out.println("5. Generate UserIntentModelData");
		ConstructUIModel cmodel = new ConstructUIModel(discover.getCauiTaskManager(),null); 
		UserIntentModelData modelData = cmodel.constructModel(trans2ProbDictionary);


		/*
		int i = 0;
		for(List<String> actionList :currentActCtxDictionary.keySet()){

			System.out.println("size "+actionList.size());
			if(actionList.size()==1){
				String action1 = actionList.get(0);
				String [] action = action1.split("\\/");
				System.out.println (i+ " paramName: "+action[0]+" paramValue: "+action[1]);
			}

			if(actionList.size()==2){
				String action1 = actionList.get(0);
				String [] actionAr1 = action1.split("\\/");
				System.out.println (i+ " paramName: "+actionAr1[0]+" paramValue: "+actionAr1[1]);

				String action2 = actionList.get(1);
				String [] actionAr2 = action2.split("\\/");
				System.out.println (i+ " paramName: "+actionAr2[0]+" paramValue: "+actionAr2[1]);
			}

		i++;
		}
		 */
		//	LinkedHashMap<List<String>,ActionDictObject> model = discover.generateTransitionsDictionary(historySet);

		//LinkedHashMap<List<String>,ActionDictObject> model = discover.getDictionary();
		//	System.out.println("discover.printDictionary(model)  ");
		//	discover.printDictionary(model);

		//LinkedHashMap<String,ActionDictObject> results = discover.getSeqs(18);
		/*
		discover.storeDictionary(model);
		discover.clearActiveDictionary();
		model = null;

		LinkedHashMap<String,DictObject> retrievedModel = discover.retrieveModel();
		discover.printDictionary(retrievedModel);
		discover.setActiveDictionary(retrievedModel);
		discover.generateNewUserModel(historySet);
		LinkedHashMap<String,DictObject> newModel = discover.getDictionary();
		discover.printDictionary(newModel);
		 */
	}

	private CtxAttributeValueType findAttributeValueType(Serializable value) {
		if (value == null)
			return CtxAttributeValueType.EMPTY;
		else if (value instanceof String)
			return CtxAttributeValueType.STRING;
		else if (value instanceof Integer)
			return CtxAttributeValueType.INTEGER;
		else if (value instanceof Double)
			return CtxAttributeValueType.DOUBLE;
		else if (value instanceof byte[])
			return CtxAttributeValueType.BINARY;
		else
			throw new IllegalArgumentException(value + ": Invalid value type");
	}


	public void printTransProbDictionary (LinkedHashMap<String,HashMap<String,Double>> transProbDictionary){

		System.out.println ("**** total number of entries:" + transProbDictionary.size());
		for(String actions : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(actions);
			System.out.println("Action:"+actions+ "| target: "+transTargets);
		}
	}

	
	public void createContextHistoryAttributesSet(){

		//Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		//create actions
		IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
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
	
		for (int i=0; i<=2; i++){

			monitorAction(action1,"home","free",10);
			monitorAction(action2,"office","busy",15);
			monitorAction(action3,"park","away",25);
			monitorAction(actionX,"park","away",25);
			monitorAction(actionY,"park","away",25);
			monitorAction(action4,"park","away",25);
			monitorAction(action5,"park","away",25);
			monitorAction(actionY,"park","away",25);
			monitorAction(actionX,"park","away",25);
			monitorAction(action1,"home","free",10);
			monitorAction(action2,"office","busy",15);
			monitorAction(action3,"park","away",25);
			monitorAction(actionY,"park","away",25);
			monitorAction(actionY,"park","away",25);
			monitorAction(action1,"home","free",10);
			monitorAction(action2,"office","busy",15);
			monitorAction(action3,"park","away",25);
			monitorAction(actionX,"park","away",25);
			monitorAction(action4,"park","away",25);
			monitorAction(action5,"park","away",25);
		}
	}

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
	
	public List<MockHistoryData> createMockHistorySet(){
		List<MockHistoryData>  data = new ArrayList<MockHistoryData>();
		//MockHistoryData(String action, String parameterName, Map<String,String> context){
		Map<String,String> context = new HashMap<String,String>();
		context.put("temperature","hot");
		context.put("SymLoc","free");
		Date date = new Date();

		data.add(new MockHistoryData("volume","mute",context,date));
		data.add(new MockHistoryData("paramA","valueA",context,date));
		data.add(new MockHistoryData("paramB","valueB",context,date));
		data.add(new MockHistoryData("paramC","valueC",context,date));
		data.add(new MockHistoryData("paramX","valueX",context,date));
		data.add(new MockHistoryData("paramY","valueY",context,date));
		data.add(new MockHistoryData("paramA","valueA",context,date));
		data.add(new MockHistoryData("paramB","valueB",context,date));
		data.add(new MockHistoryData("paramC","valueC",context,date));
		data.add(new MockHistoryData("paramO","valueO",context,date));
		data.add(new MockHistoryData("paramP","valueP",context,date));
		data.add(new MockHistoryData("paramA","valueA",context,date));
		data.add(new MockHistoryData("paramB","valueB",context,date));
		data.add(new MockHistoryData("paramC","valueC",context,date));
		data.add(new MockHistoryData("paramL","valueL",context,date));
		data.add(new MockHistoryData("paramA","valueA",context,date));
		data.add(new MockHistoryData("paramB","valueB",context,date));
		data.add(new MockHistoryData("paramC","valueC",context,date));

		List<MockHistoryData> newSet = new ArrayList<MockHistoryData>();

		for(int i=0; i<1; i++){
			newSet.addAll(data);
		}
		return newSet;
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

	//IAction action = (IAction)SerialisationHelper.deserialise(primary.getBinaryValue(),this.getClass().getClassLoader());

	private CtxEntity createOperator(){
		CtxEntityIdentifier ctxEntId = new CtxEntityIdentifier("operatorID","person",getNextValue());
		CtxEntity ctxEntity = new CtxEntity(ctxEntId);
		setOperatorEntity(ctxEntity);
		return ctxEntity;
	}

	private void setOperatorEntity(CtxEntity entity){
		operator = entity;
	}

	private CtxEntity getOperatorEntity(){
		return operator;
	}


	public static Long getNextValue() {

		return nextValue++;
	}



	public static void main(String[] args) {
		CAUIDiscoveryLearningTest cdt = new CAUIDiscoveryLearningTest();
		cdt.startTesting();
	}

	//*******************************************************
	//***** dead code ***************************************

	/*
	public List<MockHistoryData> retrieveHistorySet(){
		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();

		for(MockHistoryData mockData :historySet ){
			//System.out.println(mockData);
		}
		//System.out.println("historySet "+historySet);
		//System.out.println("historySet.size() "+historySet.size());

		return historySet;
	}
	 */

	/*
	private CtxAttribute setContext(String type, Serializable value){

		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = ctxBroker.retrieveCssOperator().get();

			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = ctxBroker.createAttribute(operator.getId(), type).get();
				attr = ctxBroker.updateAttribute(attr.getId(),value).get();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attr;
	}

	 */
	/*
	private List<MockHistoryData> createNormalSeq(){
		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();

		for(int j =0 ; j<1 ; j++){
			for(int i =0 ; i<10 ; i++){
				String context = new String();
				context = "C"+i;
				String a = "A"+i;
				MockHistoryData hoc = new MockHistoryData(a,context);	
				historySet.add(hoc);
			}
		}
		return historySet;
	}
	 */

	/*
	protected List<MockHistoryData> convertHistoryData (Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){

		List<MockHistoryData> result = new ArrayList<MockHistoryData>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> ctxHocTuples = mapHocData;

		for(CtxHistoryAttribute primaryHocAttr: ctxHocTuples.keySet()){
			String primaryCtxValue = primaryHocAttr.getStringValue();
			List<CtxHistoryAttribute> listHocAttrs = ctxHocTuples.get(primaryHocAttr);
			//assume that only one escorting context object exists 
			CtxHistoryAttribute escortingHocAttr = listHocAttrs.get(0);
			String escortingHocAttrValue = escortingHocAttr.getStringValue();

			MockHistoryData mockHocData = new MockHistoryData(primaryCtxValue,escortingHocAttrValue);
			result.add(mockHocData);
		}

		return result;
	}
	 */
}