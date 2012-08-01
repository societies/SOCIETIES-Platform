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
package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.IAction;
//import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
// remove after testing
//import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;



public class CAUIDiscovery implements ICAUIDiscovery{

	public static final Logger LOG = LoggerFactory.getLogger(CAUIDiscovery.class);

	public ICAUITaskManager cauiTaskManager;
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	
	//LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary = null;
	LinkedHashMap<String,HashMap<String,Double>> transProb = null;
	HashMap<String,List<String>> contextActionsMap = new HashMap<String,List<String>>();


	List<String> charList = null;
	//List<MockHistoryData> historyList = null;

	public CAUIDiscovery(){
		//actCtxDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
		//remove after testing
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

	

	// constructor
	public void initialiseCAUIDiscovery(){
		//actCtxDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
	}

	@Override
	public void generateNewUserModel() {

		LOG.debug("start model generation");
	
		if (retrieveHistoryTupleData(CtxAttributeTypes.LAST_ACTION) != null ){

			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = retrieveHistoryTupleData(CtxAttributeTypes.LAST_ACTION);

			//LOG.info("2. Convert History Data");
			List<MockHistoryData> mockData = convertHistoryData(mapHocData);

			//LOG.info("3. Generate Transition Dictionary");
			LinkedHashMap<List<String>,ActionDictObject> currentActCtxDictionary = generateTransitionsDictionary(mockData);
					
			//LOG.info("4. Generate Transition Propability Dictionary (step2)");
			TransitionProbabilitiesCalc transProb  = new TransitionProbabilitiesCalc();
			LinkedHashMap<String,HashMap<String,Double>> trans2ProbDictionary = transProb.calcTrans2Prob(currentActCtxDictionary);	
			//printTransProbDictionary(trans2ProbDictionary);
			//LOG.info("5. Assign context to actions");
			
			HashMap<String,List<String>> ctxActionsMap =  assignContextToAction(currentActCtxDictionary);
			//LOG.info("5. Generate UserIntentModelData");
	
			ConstructUIModel cmodel = new ConstructUIModel(cauiTaskManager,ctxBroker); 
			//LOG.info("5a trans2ProbDictionary "+ trans2ProbDictionary);
			//LOG.info("5a ctxActionsMap "+ ctxActionsMap);
			UserIntentModelData modelData = cmodel.constructNewModel(trans2ProbDictionary,ctxActionsMap);

			//LOG.info("6. result "+modelData.getActionModel());

			//LOG.info("7. Store UserIntentModelData to ctx DB");

			CtxAttribute ctxAttr = storeModelCtxDB(modelData);
			LOG.info("model stored under attribute id: "+ctxAttr.getId());
			LOG.info("modelData "+ modelData.getActionModel());
			
		}else LOG.info("not enough history data");
	}

	private Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTupleData(String attributeType){

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		try {
			results = ctxBroker.retrieveHistoryTuples(attributeType, listOfEscortingAttributeIds, null, null).get();

		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	private Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTupleData(CtxAttributeIdentifier primaryAttrID){

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		try {
			//if( ctxBroker.retrieveHistoryTuples(primaryAttrID, listOfEscortingAttributeIds, null, null) != null)	
			mapHocData = ctxBroker.retrieveHistoryTuples(primaryAttrID, listOfEscortingAttributeIds, null, null).get();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapHocData;
	}
	 */

	public LinkedHashMap<List<String>,ActionDictObject> generateTransitionsDictionary(List<MockHistoryData> data) {

		LinkedHashMap<List<String>,ActionDictObject> actCtxDictionaryAll = new LinkedHashMap<List<String>,ActionDictObject>();

		actCtxDictionaryAll = populateActionCtxDictionary(data);
		//this.setActiveDictionary(actCtxDictionary);
		//printDictionary(actCtxDictionary);
		//	LinkedHashMap<String,HashMap<String,Double>> trans3ProbDictionary = transProb.calcTrans3Prob();	
		//	printTransProbDictionary(trans3ProbDictionary);
		//	TaskDiscovery taskDisc = new TaskDiscovery(actCtxDictionary);
		//	taskDisc.populateTaskDictionary();

		return actCtxDictionaryAll;
	}

	/*
	public LinkedHashMap<List<String>,ActionDictObject> getDictionary(){
		return this.actCtxDictionary;
	}


	public void setActiveDictionary(LinkedHashMap<List<String>,ActionDictObject> model){
		this.actCtxDictionary = model;
		//System.out.println("setActiveDictionary : ");
	}


	public void clearActiveDictionary(){
		this.actCtxDictionary = null;
		LOG.info("model cleared "+this.actCtxDictionary);
	}
*/



	public LinkedHashMap<List<String>,ActionDictObject>  populateActionCtxDictionary(List<MockHistoryData> historyData){

		LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
		
		int historySize = historyData.size();
		LOG.debug("historySize "+historySize);

		List<String> currentActPhrase = null;
		List<String> currentCtxPhraseLocation = null;
		List<String> currentCtxPhraseStatus = null;
		List<String> currentCtxPhraseTemperature = null;

		// j is the step and the longest phrase has 3 actions
		for (int j=1; j<4; j++) {

			for (int i = 0; i < historySize ; i++) {
				MockHistoryData currentHocData =  historyData.get(i);
				List<String> actionNameObjTemp = new ArrayList<String>();
				String actionName = currentHocData.getServiceId()+"#"+currentHocData.getParameterName()+"#"+currentHocData.getActionValue();
				//LOG.info("action name "+actionName);
				actionNameObjTemp.add(actionName);

				//context
				LinkedList<String> ctxObjTempLocation = new LinkedList<String>();
				LinkedList<String> ctxObjTempStatus = new LinkedList<String>();
				LinkedList<String> ctxObjTempTemperature = new LinkedList<String>();

				ctxObjTempLocation.add(currentHocData.getContextValue(CtxAttributeTypes.LOCATION_SYMBOLIC));
				ctxObjTempStatus.add(currentHocData.getContextValue(CtxAttributeTypes.STATUS));
				ctxObjTempTemperature.add(currentHocData.getContextValue(CtxAttributeTypes.TEMPERATURE));

				MockHistoryData tempHocData = null;
				//get next action
				for (int k=1; k<j; k++){
					//avoid null pointer at end of file
					if( i+k < historySize ){
						tempHocData = historyData.get(i+k);
						//String tempNextActName = tempHocData.getActionValue();
						String tempNextActName = tempHocData.getServiceId()+"#"+tempHocData.getParameterName()+"#"+tempHocData.getActionValue();
						
						actionNameObjTemp.add(tempNextActName);

						//context
						String tempNextCtxLoc = tempHocData.getContextValue(CtxAttributeTypes.LOCATION_SYMBOLIC);
						String tempNextCtxStatus = tempHocData.getContextValue(CtxAttributeTypes.STATUS);
						String tempNextCtxTemperature = tempHocData.getContextValue(CtxAttributeTypes.TEMPERATURE);

						ctxObjTempLocation.add(tempNextCtxLoc);
						ctxObjTempStatus.add(tempNextCtxStatus);
						ctxObjTempTemperature.add(tempNextCtxTemperature);
					}
				}

				currentActPhrase = actionNameObjTemp;
				//context
				currentCtxPhraseLocation = ctxObjTempLocation;
				currentCtxPhraseStatus = ctxObjTempStatus;
				currentCtxPhraseTemperature = ctxObjTempTemperature;

				List<List<String>> currentCtxPhraseList = new ArrayList<List<String>>();
				currentCtxPhraseList.add(0,currentCtxPhraseLocation );
				currentCtxPhraseList.add(1,currentCtxPhraseStatus );
				currentCtxPhraseList.add(2,currentCtxPhraseTemperature );

				if (actionNameObjTemp.size() == j){
					//System.out.println("j="+j+" i="+i+" actionName "+actionName+ " phrase "+currentActPhrase +" context"+currentHocData.getContext()+" ");

					if(actCtxDictionary.containsKey(currentActPhrase)){

						//get current dictObj
						ActionDictObject dicObj = actCtxDictionary.get(currentActPhrase);
						//update total action score
						Integer previousScore = dicObj.getTotalOccurences();
						dicObj.setTotalOccurences(previousScore+1);

						//update context map
						HashMap<List<String>,Integer> currentCtxMapLocation = new HashMap<List<String>,Integer>();
						currentCtxMapLocation = dicObj.getLocationContextMap();
						HashMap<List<String>,Integer> updatedMapLoc = mergeCtxMaps(currentCtxPhraseLocation, currentCtxMapLocation);
						dicObj.setLocationContextMap(updatedMapLoc);

						HashMap<List<String>,Integer> currentCtxMapStatus = new HashMap<List<String>,Integer>();
						currentCtxMapStatus = dicObj.getStatusContextMap();
						HashMap<List<String>,Integer> updatedMapStatus = mergeCtxMaps(currentCtxPhraseStatus, currentCtxMapStatus);
						dicObj.setStatusContextMap(updatedMapStatus);

						HashMap<List<String>,Integer> currentCtxMapTemperature = new HashMap<List<String>,Integer>();
						currentCtxMapTemperature = dicObj.getTemperatureContextMap();
						HashMap<List<String>,Integer> updatedMapTemperature = mergeCtxMaps(currentCtxPhraseTemperature, currentCtxMapTemperature);
						dicObj.setTemperatureContextMap(updatedMapTemperature);

						//add updated data to dictionary
						actCtxDictionary.put(currentActPhrase,dicObj);

					} else {
						ActionDictObject newDictObj = new ActionDictObject();
						newDictObj.setTotalOccurences(1);

						//create context map
						HashMap<List<String>,Integer> newCtxMapLocation =  new HashMap<List<String>,Integer>();
						newCtxMapLocation.put(currentCtxPhraseLocation, 1);
						newDictObj.setLocationContextMap(newCtxMapLocation);

						HashMap<List<String>,Integer> newCtxMapStatus =  new HashMap<List<String>,Integer>();
						newCtxMapStatus.put(currentCtxPhraseStatus, 1);
						newDictObj.setStatusContextMap(newCtxMapStatus);

						HashMap<List<String>,Integer> newCtxMapTemperature =  new HashMap<List<String>,Integer>();
						newCtxMapTemperature.put(currentCtxPhraseTemperature, 1);
						newDictObj.setTemperatureContextMap(newCtxMapTemperature);

						actCtxDictionary.put(currentActPhrase,newDictObj);
					}
				}
			}
		}
		return actCtxDictionary;
	}



	private HashMap<List<String>,Integer> mergeCtxMaps(List<String> newCtxPhrase, HashMap<List<String>,Integer> oldMap){

		HashMap<List<String>,Integer> results = oldMap;
		if(results.containsKey(newCtxPhrase)){
			Integer value = results.get(newCtxPhrase);
			results.put(newCtxPhrase, value+1);
		}else{
			results.put(newCtxPhrase,1);
		}	
		return results;
	}




	public HashMap<String,List<String>> assignContextToAction(LinkedHashMap<List<String>,ActionDictObject> dictionaryFull){

		//key:ActionName value:[home,free,10]
		HashMap<String,List<String>> results = new HashMap<String,List<String>>();

		TransitionProbabilitiesCalc transProb  = new TransitionProbabilitiesCalc();
		LinkedHashMap<List<String>,ActionDictObject> dic = transProb.getStepDict(dictionaryFull, 1);
		//System.out.println("dic "+dic);
		String action = "";
		ActionDictObject dicObj;
		for(List<String> actList :dic.keySet()){
			if(actList.size()==1){
				action = actList.get(0);
				dicObj = dic.get(actList);
				int actionOccurences = dicObj.getTotalOccurences();
				List<String> contextList = new ArrayList<String>();
			
				if(dicObj.getLocationContextMap() != null){
					HashMap<List<String>,Integer> locMap = dicObj.getLocationContextMap();  
					for(List<String> locationValues : locMap.keySet()){
						if(locationValues.size()== 1){
							String location = locationValues.get(0);
							int locValueOccurences = locMap.get(locationValues);
							if(locValueOccurences/actionOccurences > 0.5){
								contextList.add("location="+location);
							}
						}
					}
				}
				if(dicObj.getStatusContextMap() != null){
					HashMap<List<String>,Integer> statusMap = dicObj.getStatusContextMap();  
					for(List<String> statusValues : statusMap.keySet()){
						if(statusValues.size()== 1){
							String status = statusValues.get(0);
							int statusValueOccurences = statusMap.get(statusValues);
							if(statusValueOccurences/actionOccurences > 0.5){
								contextList.add("status="+status);
							}
						}
					}
				}
				if(dicObj.getTemperatureContextMap() != null){
					HashMap<List<String>,Integer> temperatureMap = dicObj.getTemperatureContextMap();  
					for(List<String> tempValues : temperatureMap.keySet()){
						if(tempValues.size()== 1){
							String temperature = tempValues.get(0);
							int temperatureValueOccurences = temperatureMap.get(tempValues);
							if(temperatureValueOccurences/actionOccurences > 0.5){
								contextList.add("temperature="+temperature);
							}
						}
					}
				}
				results.put(action, contextList);
			
			}
		}
		return results;
	}




	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		LOG.debug ("**** printing dictionary contents *****");
		LOG.debug ("**** total number of entries:" + dictionary.size());
		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();
			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.toString());
		}
	}



	public void printTransProbDictionary (LinkedHashMap<String,HashMap<String,Double>> transProbDictionary){

		for(String actions : transProbDictionary.keySet()){
			HashMap<String,Double> transTargets = transProbDictionary.get(actions);
			System.out.println("Action:"+actions+ "| target: "+transTargets);
		}
	}

	public LinkedHashMap<List<String>,ActionDictObject> getSeqs(LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary, int score){
		LinkedHashMap<List<String>,ActionDictObject> results = new LinkedHashMap<List<String>,ActionDictObject>();
		//LinkedHashMap<List<String>,ActionDictObject> dict = getDictionary();

		for (List<String> act : actCtxDictionary.keySet()){
			int totalOccur = actCtxDictionary.get(act).getTotalOccurences();
			if( totalOccur > score) results.put(act, actCtxDictionary.get(act));	
		}
		LOG.debug("total entries in model "+actCtxDictionary.size()); 
		LOG.debug("entries occured more than "+score); 
		return results;
	}


	// steps are string characters and will change to real actions
	private Integer retrieveTransNum(LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary, int steps){
		int total2Trans = 0; 
		//LinkedHashMap<List<String>,ActionDictObject> dict = getDictionary();

		for (List<String> act : actCtxDictionary.keySet()){
			if(act.size() == steps){
				ActionDictObject actDictObj = actCtxDictionary.get(act);	
				total2Trans = total2Trans+actDictObj.getTotalOccurences();
			}
		}		
		return total2Trans;
	}


	/*
	 *  Converts history data to a temporary list of mockHistoryData in order to be processed and stored in dictionary and model
	 *  Escorting context values are converted to string objects. 
	 */
	
	public List<MockHistoryData> convertHistoryData (Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){

		List<MockHistoryData> result = new ArrayList<MockHistoryData>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> ctxHocTuples = mapHocData;
		for(CtxHistoryAttribute primaryHocAttr: ctxHocTuples.keySet()){
			//String primaryCtxValue = primaryHocAttr.getStringValue();
			try {
				IAction retrievedAction = (IAction) SerialisationHelper.deserialise(primaryHocAttr.getBinaryValue(), this.getClass().getClassLoader());
				String serviceIdentString = retrievedAction.getServiceID().getServiceInstanceIdentifier();
			
				List<CtxHistoryAttribute> listHocAttrs = ctxHocTuples.get(primaryHocAttr);
						
				Map<String,String> context = new HashMap<String,String>();
				for(int i=0; i<listHocAttrs.size(); i++){
					CtxHistoryAttribute escortingHocAttr = listHocAttrs.get(i);
					String value = castAttrValuetoString(escortingHocAttr);
					context.put(escortingHocAttr.getType(), value);
				}
				MockHistoryData mockHocData = new MockHistoryData(retrievedAction.getparameterName(), retrievedAction.getvalue(), context,primaryHocAttr.getLastModified(),serviceIdentString);
				result.add(mockHocData);
				
			}  catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}


	protected String castAttrValuetoString(CtxHistoryAttribute attr){

		String valueStr = "";
		if (attr.getStringValue() != null) {
			valueStr = attr.getStringValue();
		} else if (attr.getIntegerValue() != null) {
			valueStr = attr.getIntegerValue().toString();
		} else if (attr.getDoubleValue() != null) {
			valueStr = attr.getDoubleValue().toString();
		} 
		
		return valueStr;
	}


	protected CtxAttribute lookupAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			// !! use ctxBroker method that searches entities and attributes	
			List<CtxIdentifier> tupleAttrList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size()>0){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) this.ctxBroker.retrieve(ctxId).get();	
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
		return ctxAttr;
	}



	private CtxAttribute storeModelCtxDB(UserIntentModelData modelData){

		CtxAttribute ctxAttrCAUIModel = null;
		try {
			byte[] binaryModel = SerialisationHelper.serialise(modelData);
				
			//CtxEntity operator = ctxBroker.retrieveCssOperator().get();
			
			final INetworkNode cssNodeId = commsMgr.getIdManager().getThisNetworkNode();
			
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = commsMgr.getIdManager().fromJid(cssOwnerStr);
			
			//LOG.info("cssOwnerId "+cssOwnerId);
			IndividualCtxEntity operator = ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
			//LOG.info("discovery operator retrieved "+operator);
			
			ctxAttrCAUIModel = lookupAttrHelp(CtxAttributeTypes.CAUI_MODEL);
			if(ctxAttrCAUIModel != null){

				ctxAttrCAUIModel = ctxBroker.updateAttribute(ctxAttrCAUIModel.getId(), binaryModel).get();
			} else {
				ctxAttrCAUIModel = ctxBroker.createAttribute(operator.getId(),CtxAttributeTypes.CAUI_MODEL).get();
				ctxAttrCAUIModel = ctxBroker.updateAttribute(ctxAttrCAUIModel.getId(), binaryModel).get();
			}
		
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxAttrCAUIModel;
	}


	//*************** Model storage to hard drive *****************
	/*
	public LinkedHashMap<String,ActionDictObject> retrieveModel(){
		LinkedHashMap<String,ActionDictObject> model = new  LinkedHashMap<String,ActionDictObject>();
		System.out.println("retrieve file 'taskModel' ");

		File file = new File("taskModel");  
		FileInputStream f;
		try {
			f = new FileInputStream(file);
			ObjectInputStream s = new ObjectInputStream(f);  
			model = (LinkedHashMap<String,ActionDictObject>)s.readObject();         
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return model;
	}




	public void storeDictionary(LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary ){

		System.out.println("storing to file 'taskModel' ");
		//this.actCtxDictionary = actCtxDictionary;
		File file = new File("taskModel");  
		FileOutputStream f;
		try {
			f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);          
			s.writeObject(actCtxDictionary);
			s.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
*/
}