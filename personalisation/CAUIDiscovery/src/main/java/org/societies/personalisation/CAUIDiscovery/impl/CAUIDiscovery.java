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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;


public class CAUIDiscovery implements ICAUIDiscovery{

	private ICAUITaskManager cauiTaskManager;
	private ICtxBroker ctxBroker;

	LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary = null;
	LinkedHashMap<String,HashMap<String,Double>> transProb = null;



	List<String> charList = null;
	List<MockHistoryData> historyList = null;

	public CAUIDiscovery(){
		actCtxDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
	}

	public ICAUITaskManager getCauiTaskManager() {
		System.out.println(this.getClass().getName()+": Return cauiTaskManager");

		return cauiTaskManager;
	}

	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		System.out.println(this.getClass().getName()+": Got cauiTaskManager");
		this.cauiTaskManager = cauiTaskManager;
	}

	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName()+": Return ctxBroker");
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		System.out.println(this.getClass().getName()+": Got ctxBroker");
		this.ctxBroker = ctxBroker;
	}


	// constructor
	public void initialiseCAUIDiscovery(){
		actCtxDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
	}


	@Override
	public void generateNewUserModel() {

		/*
		ConstructUIModel cmodel = new ConstructUIModel(cauiTaskManager,ctxBroker); 
		// null will be substituted by the transition probability dictionary produced by populateActionCtxDictionary
		cmodel.constructModel(null);
		 */
	}

	public void generateNewUserModel(List<MockHistoryData> data) {

		this.historyList = data;
		this.actCtxDictionary = getDictionary(); 
		this.actCtxDictionary = populateActionCtxDictionary();
		this.setActiveDictionary(actCtxDictionary);
		printDictionary(actCtxDictionary);
		TransitionProbabilitiesCalc transProb  = new TransitionProbabilitiesCalc(actCtxDictionary);
		LinkedHashMap<String,HashMap<String,Double>> trans2ProbDictionary = transProb.calcTrans2Prob();	
		printTransProbDictionary(trans2ProbDictionary);

		LinkedHashMap<String,HashMap<String,Double>> trans3ProbDictionary = transProb.calcTrans3Prob();	
		printTransProbDictionary(trans3ProbDictionary);
		//TaskDiscovery taskDisc = new TaskDiscovery(actCtxDictionary);
		//taskDisc.populateTaskDictionary();


		// runs only in virgo
		/*
		ConstructUIModel cmodel = new ConstructUIModel(cauiTaskManager,ctxBroker); 
		cmodel.constructModel(null);
		 */
	}

	public LinkedHashMap<List<String>,ActionDictObject> getDictionary(){
		return this.actCtxDictionary;
	}

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


	public void setActiveDictionary(LinkedHashMap<List<String>,ActionDictObject> model){
		this.actCtxDictionary = model;
		System.out.println("model imported #size : "+this.actCtxDictionary.size());
	}

	public void clearActiveDictionary(){
		this.actCtxDictionary = null;
		System.out.println("model cleared "+this.actCtxDictionary);
	}


	public void storeDictionary(LinkedHashMap<List<String>,ActionDictObject> actCtxDictionary ){

		System.out.println("storing to file 'taskModel' ");
		this.actCtxDictionary = actCtxDictionary;
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

	public LinkedHashMap<List<String>,ActionDictObject>  populateActionCtxDictionary(){

		//LinkedHashMap<String,DictObject> actCtxDictionary = new LinkedHashMap<String,DictObject>();
		///	LinkedHashMap<List<String>,ActionDictObject> newActCtxDictionary = this.actCtxDictionary;
		int historySize = this.historyList.size();
		System.out.println("historySize "+historySize);
		System.out.println(this.historyList);


		List<String> currentActPhrase = null;
		List<String> currentCtxPhrase = null;

		// j is the step and the longest phrase has 3 actions
		for (int j=1; j<5; j++) {

			for (int i = 0; i < historySize ; i++) {
				MockHistoryData currentHocData =  this.historyList.get(i);
				List<String> actionNameObjTemp = new ArrayList<String>();
				String actionName = currentHocData.getActionValue();
				//	System.out.println("prin "+actionNameObjTemp);
				//	System.out.println(j+" "+ i+" "+actionName);
				actionNameObjTemp.add(actionName);
				//	System.out.println("meta "+actionNameObjTemp);


				LinkedList<String> ctxObjTemp = new LinkedList<String>();
				ctxObjTemp.add(currentHocData.getContext());
				//System.out.println("j="+j+" i="+i+" actionName "+actionName+" context"+currentHocData.getContext());
				MockHistoryData tempHocData = null;

				for (int k=1; k<j; k++){
					//avoid null pointer at end of file
					if( i+k < historySize ){
						tempHocData = this.historyList.get(i+k);
						String tempNextActName = tempHocData.getActionValue();
						String tempNextCtx = tempHocData.getContext();
						//System.out.println(".."+tempNextActName);
						actionNameObjTemp.add(tempNextActName);
						//nextActName.add(tempNextActName);
						ctxObjTemp.add(tempNextCtx);
						//	System.out.println("k="+k+" nextActName "+nextActName);
					}
				}
				//actionNameObjTemp.addAll(nextActName);
				//currentActPhrase = actionNameObjTemp.toString();
				currentActPhrase = actionNameObjTemp;
				currentCtxPhrase = ctxObjTemp;


				if (actionNameObjTemp.size() == j){
					//System.out.println("j="+j+" i="+i+" actionName "+actionName+ " phrase "+currentActPhrase +" context"+currentHocData.getContext()+" ");

					if(actCtxDictionary.containsKey(currentActPhrase)){
						//	HashMap<String,Integer> container = dictionary.get(currentChar);

						//get current dictObj
						ActionDictObject dicObj = actCtxDictionary.get(currentActPhrase);
						//update total score
						Integer previousScore = dicObj.getTotalOccurences();
						dicObj.setTotalOccurences(previousScore+1);

						//update context map
						HashMap<List<String>,Integer> currentCtxMap  = dicObj.getContextMap();
						HashMap<List<String>,Integer> updatedMap = mergeCtxMaps(currentCtxPhrase,currentCtxMap);

						dicObj.setContextMap(updatedMap);
						//System.out.println("k="+dicObj);
						//add updated data to dictionary
						actCtxDictionary.put(currentActPhrase,dicObj);
						//System.out.println("actCtxDictionary.put 1"+ actCtxDictionary);
					} else {
						ActionDictObject newDictObj = new ActionDictObject();
						newDictObj.setTotalOccurences(1);
						HashMap<List<String>,Integer> newCtxMap =  new HashMap<List<String>,Integer>();
						newCtxMap.put(currentCtxPhrase, 1);
						newDictObj.setContextMap(newCtxMap);
						actCtxDictionary.put(currentActPhrase,newDictObj);
						//System.out.println("actCtxDictionary.put 2"+ actCtxDictionary);
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

	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		System.out.println ("**** printing dictionary contents *****");
		System.out.println ("**** total number of entries:" + dictionary.size());

		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();

			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.getContextMap());
		}
	}


	//LinkedHashMap<String,HashMap<String,Double>> transProbDictionary
	public void printTransProbDictionary (LinkedHashMap<String,HashMap<String,Double>> dictionary){

		System.out.println ("**** printing TransProbDictionary contents *****");
		//System.out.println ("**** total number of entries:" + dictionary.size());

		for(String actions : dictionary.keySet()){
			HashMap<String,Double> transTargets = dictionary.get(actions);

			System.out.println("Action:"+actions+ "| target: "+transTargets);
		}
	}

	public LinkedHashMap<List<String>,ActionDictObject> getSeqs(int score){
		LinkedHashMap<List<String>,ActionDictObject> results = new LinkedHashMap<List<String>,ActionDictObject>();
		LinkedHashMap<List<String>,ActionDictObject> dict = getDictionary();

		for (List<String> act : dict.keySet()){
			int totalOccur = dict.get(act).getTotalOccurences();
			if( totalOccur > score) results.put(act, dict.get(act));	
		}

		System.out.println("total entries in model "+dict.size()); 
		System.out.println("entries occured more than "+score); 
		return results;
	}


	// steps are string characters and will change to real actions

	private Integer retrieveTransNum(int steps){
		int total2Trans = 0; 
		LinkedHashMap<List<String>,ActionDictObject> dict = getDictionary();

		for (List<String> act : dict.keySet()){
			if(act.size() == steps){
				ActionDictObject actDictObj = dict.get(act);	
				total2Trans = total2Trans+actDictObj.getTotalOccurences();
			}
		}		
		return total2Trans;
	}

}