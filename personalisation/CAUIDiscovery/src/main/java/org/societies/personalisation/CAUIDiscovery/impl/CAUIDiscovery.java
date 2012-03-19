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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
//import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUIDiscovery.test.MockHistoryData;


public class CAUIDiscovery implements ICAUIDiscovery{

	private ICAUITaskManager cauiTaskManager;
	private ICtxBroker ctxBroker;

	public CAUIDiscovery(){
		dictionary = new LinkedHashMap<String,Integer>();
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
		dictionary = new LinkedHashMap<String,Integer>();
	}

	LinkedHashMap<String,Integer> dictionary = null;
	List<String> charList = null;
	List<MockHistoryData> historyList = null;

	@Override
	public void generateNewUserModel() {
	}

	public void generateNewUserModel(List data) {
		this.historyList = data;
		populateDictionary();
	}

	public void  populateDictionary(){

		int historySize = this.historyList.size();
		String currentPhrase = null;

		// j is the step and the longest phrase has 5 actions
		for (int j=1; j<5; j++) {
		//	System.out.println("dictionary "+this.dictionary);
		//	System.out.println("-------");

			for (int i = 0; i < historySize ; i++) {
				//	currentPhrase = array.substring(i, i+j);
				LinkedList<String> actionNameObj = new LinkedList<String>();
				MockHistoryData currentHocData =  this.historyList.get(i);
				String actionName = currentHocData.getActionValue();
				actionNameObj.add(actionName);
		//		System.out.println("j="+j+" i="+i+" actionName "+actionName);

				MockHistoryData tempHocData = null;
				LinkedList<String> nextActName = new LinkedList<String>();

				for (int k=1; k<j; k++){
					if( i+k < historySize ){ 
						tempHocData = this.historyList.get(i+k);
						String tempNextActName = tempHocData.getActionValue();
						//nextActName = nextActName + tempNextActName;
						nextActName.add(tempNextActName);
			//			System.out.println("k="+k+" nextActName "+nextActName);
					}
				}
				actionNameObj.addAll(nextActName);
				//currentPhrase = actionName + nextActName;
		//		System.out.println("j="+j+" i="+i+" currentPhrase "+actionNameObj);
				currentPhrase = actionNameObj.toString();
		//		System.out.println(" actionNameObj.toString "+currentPhrase);
				if (actionNameObj.size() == j){
					if( this.dictionary.containsKey(currentPhrase)){
						//	HashMap<String,Integer> container = dictionary.get(currentChar);
						Integer previousScore = this.dictionary.get(currentPhrase);
						this.dictionary.put(currentPhrase,previousScore+1);
					} else {
						this.dictionary.put(currentPhrase, 1);
					}
				}
			}
		}
		System.out.println("dictionary "+this.dictionary);
	}

	public LinkedHashMap<String,Integer> getDictionary(){
		return this.dictionary;
	}

	public void findOccurences(int step, String array){

		int arraySize = array.length();
		String currentPhrase = null;

		for (int i = 0; i < arraySize-step ; i++){
			currentPhrase = array.substring(i, i+step);
			System.out.println(" currentChar "+currentPhrase);

			if(this.dictionary.containsKey(currentPhrase)){
				//HashMap<String,Integer> container = dictionary.get(currentChar);
				Integer previousScore = this.dictionary.get(currentPhrase);
				this.dictionary.put(currentPhrase,previousScore+1);
			} else {
				this.dictionary.put(currentPhrase, 1);
			}
		}
	}
	
	public LinkedHashMap<String,Integer> getMaxFreqSeq(int score){
		LinkedHashMap<String,Integer> results = new LinkedHashMap<String,Integer>();
		LinkedHashMap<String,Integer> dic = getDictionary();
		
		for (String act : dic.keySet()){
		if(dic.get(act) > score) results.put(act, dic.get(act));	
		}
		return results;
	}

}