package org.societies.personalisation.CAUIDiscovery.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUIDiscovery.impl.ActionDictObject;
import org.societies.personalisation.CAUIDiscovery.impl.MockHistoryData;

public class CAUIDiscoveryLearningTest {


	private static Long nextValue = 0L;

	CAUIDiscovery discover = null;

	CAUIDiscoveryLearningTest(){
		discover = new  CAUIDiscovery();
	}

	private void  startTesting(){

		//use option 1 or 2
		
		//1. create mock data sets
		List<MockHistoryData> historySet = createHistorySet();
		System.out.println("createHistorySet() size "+historySet.size());

		//2.create mock data sets based on History Attribute Tuples
		//List<MockHistoryData> historySet = convertHistoryData(createContextHistoryAttributesSet());
	
		
		System.out.println("generateNewUserModel");
		discover.generateTransitionsDictionary(historySet);

		LinkedHashMap<List<String>,ActionDictObject> model = discover.getDictionary();

		//discover.printDictionary(model);

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



	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> createContextHistoryAttributesSet(){
	
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		//String operatorId, String type, Long objectNumber
		CtxHistoryAttribute mockPrimaryHocAttrA = createMockHocAttr("action","A");
		CtxHistoryAttribute mockEscortingHocAttrA = createMockHocAttr("location","C1");
		List<CtxHistoryAttribute> escortingCtxDataA = new ArrayList<CtxHistoryAttribute>();
		escortingCtxDataA.add(mockEscortingHocAttrA);
		mapHocData.put(mockPrimaryHocAttrA, escortingCtxDataA);
		
		CtxHistoryAttribute mockPrimaryHocAttrB = createMockHocAttr("action","B");
		CtxHistoryAttribute mockEscortingHocAttrB = createMockHocAttr("location","C1");
		List<CtxHistoryAttribute> escortingCtxDataB = new ArrayList<CtxHistoryAttribute>();
		escortingCtxDataB.add(mockEscortingHocAttrB);
		mapHocData.put(mockPrimaryHocAttrB, escortingCtxDataB);
		
		System.out.println("mapHocData "+ mapHocData);
		System.out.println("mapHocData "+ mapHocData.size());
		
		return mapHocData;
	}


	public List<MockHistoryData> createHistorySet(){
		List<MockHistoryData>  data = new ArrayList<MockHistoryData>();
		//MockHistoryData(String action, String parameterName, Map<String,String> context){
		Map<String,Serializable> context = new HashMap<String,Serializable>();
		context.put("temperature","hot");
		context.put("SymLoc","free");
		//Map<String,Serializable> context
		data.add(new MockHistoryData("volume","mute",context));
		data.add(new MockHistoryData("paramA","valueA",context));
		data.add(new MockHistoryData("paramB","valueB",context));
		data.add(new MockHistoryData("paramC","valueC",context));
		data.add(new MockHistoryData("paramX","valueX",context));
		data.add(new MockHistoryData("paramY","valueY",context));
		data.add(new MockHistoryData("paramA","valueA",context));
		data.add(new MockHistoryData("paramB","valueB",context));
		data.add(new MockHistoryData("paramC","valueC",context));
		data.add(new MockHistoryData("paramO","valueO",context));
		data.add(new MockHistoryData("paramP","valueP",context));
		data.add(new MockHistoryData("paramA","valueA",context));
		data.add(new MockHistoryData("paramB","valueB",context));
		data.add(new MockHistoryData("paramC","valueC",context));
		data.add(new MockHistoryData("paramL","valueL",context));
		data.add(new MockHistoryData("paramA","valueA",context));
		data.add(new MockHistoryData("paramB","valueB",context));
		data.add(new MockHistoryData("paramC","valueC",context));
		
		List<MockHistoryData> newSet = new ArrayList<MockHistoryData>();

		for(int i=0; i<1; i++){
			newSet.addAll(data);
		}
		return newSet;
	}

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

	private CtxHistoryAttribute createMockHocAttr(String ctxAttrType, String ctxAttrValue){

		CtxEntityIdentifier ctxEntId = new CtxEntityIdentifier("operatorID","person",getNextValue());
		CtxAttributeIdentifier ctxAttrID = new CtxAttributeIdentifier(ctxEntId,ctxAttrType,getNextValue());
		CtxAttribute ctxAttr = new CtxAttribute(ctxAttrID);
		ctxAttr.setStringValue(ctxAttrValue);
		CtxHistoryAttribute ctxHocAttr = new CtxHistoryAttribute(ctxAttr,getNextValue());

		return ctxHocAttr;
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
}