package org.societies.personalisation.CAUIDiscovery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TransitionProbabilitiesCalc {
	
	LinkedHashMap<List<String>,ActionDictObject> dictionary; 

	public TransitionProbabilitiesCalc(LinkedHashMap<List<String>,ActionDictObject> dict){
		//System.out.println("TransitionProbabilitiesCalc");
		dictionary = dict;
	}
	
	public TransitionProbabilitiesCalc(){
		//System.out.println("TransitionProbabilitiesCalc");
		//dictionary = dict;
	}
	
	public LinkedHashMap<String,HashMap<String,Double>> calcTrans2Prob(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		// {A--> [B, 0.5],[A,0.4],[C,0.1]}
		LinkedHashMap<String,HashMap<String,Double>> results = new  LinkedHashMap<String,HashMap<String,Double>>();
		
		LinkedHashMap<List<String>,ActionDictObject> step2Dict = new  LinkedHashMap<List<String>,ActionDictObject>();
	    step2Dict = getStepDict(dictionary,2);
	   // System.out.println("***************************");
	   // printDictionary(step2Dict);
	   // System.out.println("***************************");

		for (List<String> act : step2Dict.keySet()){
			HashMap<String,Double> transResultsForAct = new HashMap<String,Double>();
			HashMap<String,Integer> tempTransList = new HashMap<String,Integer>();
			
			String currentfirstAct = act.get(0);
			String candSecAct = "";

			for (List<String> othersCandActs :step2Dict.keySet()){
	
				String candFirstAct  = othersCandActs.get(0);

				if(currentfirstAct.equals(candFirstAct)){
					candSecAct = othersCandActs.get(1);
					ActionDictObject dicObj = step2Dict.get(othersCandActs);
					tempTransList.put(candSecAct,dicObj.getTotalOccurences());
				}				
			}

			double totalOtherOcc = 0.0 ;
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				totalOtherOcc =  currentActOccurences + totalOtherOcc;
			}
		
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				double transProb = ((float)currentActOccurences/totalOtherOcc);
				transResultsForAct.put(transAct,transProb);
			}
			
			results.put(currentfirstAct, transResultsForAct);
			
		}
				
		return results;
	}
		
	public List<LinkedHashMap<String,HashMap<String,Double>>> calcTrans2ProbTasks(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		// {A--> [B, 0.5],[A,0.4],[C,0.1]}
		List<LinkedHashMap<String,HashMap<String,Double>>> allTasks = new ArrayList<LinkedHashMap<String,HashMap<String,Double>>>();
		
		LinkedHashMap<String,HashMap<String,Double>> task = new LinkedHashMap<String,HashMap<String,Double>>();
		
		LinkedHashMap<List<String>,ActionDictObject> step2Dict = new  LinkedHashMap<List<String>,ActionDictObject>();
	    step2Dict = getStepDict(dictionary,2);
	   // System.out.println("***************************");
	   // printDictionary(step2Dict);
	   // System.out.println("***************************");
	    
		for (List<String> act : step2Dict.keySet()){
			HashMap<String,Double> transResultsForAct = new HashMap<String,Double>();
			HashMap<String,Integer> tempTransList = new HashMap<String,Integer>();
			
			String currentfirstAct = act.get(0);
			String candSecAct = "";

			for (List<String> othersCandActs :step2Dict.keySet()){
	
				String candFirstAct  = othersCandActs.get(0);
				if(currentfirstAct.equals(candFirstAct)){
					candSecAct = othersCandActs.get(1);
					ActionDictObject dicObj = step2Dict.get(othersCandActs);
					tempTransList.put(candSecAct,dicObj.getTotalOccurences());
				}				
			}

			double totalOtherOcc = 0.0 ;
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				totalOtherOcc =  currentActOccurences + totalOtherOcc;
			}
		
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				double transProb = ((float)currentActOccurences/totalOtherOcc);
				transResultsForAct.put(transAct,transProb);
			}
			
			task.put(currentfirstAct, transResultsForAct);
			
		}
				
		return allTasks;
	}
	
	
	
	
	public LinkedHashMap<String,HashMap<String,Double>> calcTrans3Prob(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		// {A--> [B, 0.5],[A,0.4],[C,0.1]}
		LinkedHashMap<String,HashMap<String,Double>> results = new  LinkedHashMap<String,HashMap<String,Double>>();
		
		LinkedHashMap<List<String>,ActionDictObject> step3Dict = new  LinkedHashMap<List<String>,ActionDictObject>();
	    step3Dict = getStepDict(dictionary, 3);
      //  printDictionary(step3Dict);

		for (List<String> act : step3Dict.keySet()){
			HashMap<String,Double> transResultsForAct = new HashMap<String,Double>();
			HashMap<String,Integer> tempTransList = new HashMap<String,Integer>();
			
			String currentfirstAct = act.get(0);
			String candSecAct = "";

			for (List<String> othersCandActs :step3Dict.keySet()){
	
				String candFirstAct  = othersCandActs.get(0);

				if(currentfirstAct.equals(candFirstAct)){
					candSecAct = othersCandActs.get(1);
					ActionDictObject dicObj = step3Dict.get(othersCandActs);
					tempTransList.put(candSecAct,dicObj.getTotalOccurences());
				}				
			}

			double totalOtherOcc = 0.0 ;
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				totalOtherOcc =  currentActOccurences + totalOtherOcc;
			}
			for(String transAct : tempTransList.keySet()){
				int currentActOccurences = tempTransList.get(transAct);
				double transProb = ((float)currentActOccurences/totalOtherOcc);
				transResultsForAct.put(transAct,transProb);
			}
			results.put(currentfirstAct, transResultsForAct);
		}
	//	System.out.println("step3Dict results "+results);
		
		return results;
	}

	public LinkedHashMap<List<String>,ActionDictObject> getStepDict(LinkedHashMap<List<String>,ActionDictObject> dictionaryFull,  int steps){
		
		LinkedHashMap<List<String>,ActionDictObject> stepDict = new  LinkedHashMap<List<String>,ActionDictObject>();
		for (List<String> act : dictionaryFull.keySet()){
			if(act.size() == steps){
				stepDict.put(act, dictionaryFull.get(act));
			}
		}	
		return stepDict;
	}

		
	/*
	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		System.out.println ("**** printing step2 dictionary contents *****");
	//	System.out.println ("**** total number of entries:" + dictionary.size());

		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();

			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.toString());
		}
	}
*/
}