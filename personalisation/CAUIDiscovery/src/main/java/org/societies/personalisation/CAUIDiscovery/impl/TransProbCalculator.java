package org.societies.personalisation.CAUIDiscovery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TransProbCalculator {


	//public TransitionProbabilitiesCalc(LinkedHashMap<List<String>,ActionDictObject> dict){	}

	public TransProbCalculator(){

	}


	/*
	 * The method returns a map of the form {A--> [B, 0.5],[A,0.4],[C,0.1]}
	 */
	public LinkedHashMap<List<String>,HashMap<String,Double>> calcTrans2Prob(LinkedHashMap<List<String>,ActionDictObject> step2Dict) throws Exception{

		
		if(step2Dict.size() == 0 || step2Dict == null) {
			throw new Exception("Dictionary can't be null");
		}
		
		LinkedHashMap<List<String>,HashMap<String,Double>> results = new  LinkedHashMap<List<String>,HashMap<String,Double>>();

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
			List<String> currentfirstActList = new ArrayList<String>();
			currentfirstActList.add(currentfirstAct);
			results.put(currentfirstActList, transResultsForAct);
		}

		return results;
	}




	public LinkedHashMap<List<String>,HashMap<String,Double>> calcTrans3Prob(LinkedHashMap<List<String>,ActionDictObject> step3Dict) throws Exception{

		if(step3Dict.size() == 0 || step3Dict == null) {
			throw new Exception("Dictionary can't be null");
		}
		// {A--> [B, 0.5],[A,0.4],[C,0.1]}
		LinkedHashMap<List<String>,HashMap<String,Double>> results = new  LinkedHashMap<List<String>,HashMap<String,Double>>();

		for (List<String> act : step3Dict.keySet()){
			String currentfirstAct = act.get(0);

			List<String> actionsCriteriaList = new ArrayList<String>();
			actionsCriteriaList.add(currentfirstAct);

			// retrieve records that contain only currentfirstAct as first action
			LinkedHashMap<List<String>,ActionDictObject> subRecords = this.retrieveRecords(step3Dict, actionsCriteriaList);

			for ( List<String> subRecordsActions :subRecords.keySet()){

				String currentSecondAct = subRecordsActions.get(1);

				List<String> sourceActions = new ArrayList<String>();
				sourceActions.add(0, currentfirstAct);
				sourceActions.add(1, currentSecondAct);

				HashMap<String,Double> actTargets = new HashMap<String,Double>();
				LinkedHashMap<List<String>,ActionDictObject> subSubRecords = this.retrieveRecords(subRecords, sourceActions);

				double totalOtherOcc = 0.0 ;
				for(List<String> actions : subSubRecords.keySet()){
					//abc
					int currentActOccurences = subSubRecords.get(actions).getTotalOccurences();
					totalOtherOcc =  currentActOccurences + totalOtherOcc;
				}

				for(List<String> actions : subSubRecords.keySet()){
					//abc
					int currentActOccurences = subSubRecords.get(actions).getTotalOccurences();
					double transProb = ((float)currentActOccurences/totalOtherOcc);
					actTargets.put(actions.get(2),transProb);
				}
				results.put(sourceActions, actTargets);	
			}

		}
		//System.out.println("step3Dict results size"+results.size());	
		//System.out.println("step3Dict results "+results);

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

	/*	
	public LinkedHashMap<List<String>,ActionDictObject> getSeqsq(int score){
		LinkedHashMap<List<String>,ActionDictObject> results = new LinkedHashMap<List<String>,ActionDictObject>();
		LinkedHashMap<List<String>,ActionDictObject> dict = dictionary;
		for (List<String> act : dict.keySet()){
			int totalOccur = dict.get(act).getTotalOccurences();
			if( totalOccur > score) results.put(act, dict.get(act));	
		}
		//System.out.println("total entries in model "+dict.size()); 
		//System.out.println("entries occured more than "+score); 
		return results;
	}
	 */


	public void printDictionary(LinkedHashMap<List<String>,ActionDictObject> dictionary){

		System.out.println ("**** printing step2 dictionary contents *****");
		//	System.out.println ("**** total number of entries:" + dictionary.size());

		for(List<String> actions : dictionary.keySet()){
			ActionDictObject dicObj = dictionary.get(actions);
			int occurences = dicObj.getTotalOccurences();

			System.out.println("Action:"+actions+ "# "+occurences+" | context: "+dicObj.toString());
		}
	}
	
	
	/*
	 * The method returns a sub dictionary that contained records start by the specified "actions"
	 */

	public  LinkedHashMap<List<String>,ActionDictObject>  retrieveRecords (LinkedHashMap<List<String>,ActionDictObject> dictionary, List<String> actionsCriteriaList ){

		LinkedHashMap<List<String>,ActionDictObject> subDictionary = new LinkedHashMap<List<String>,ActionDictObject>();
		String actCriteriaString1 = null ;
		String actCriteriaString2 = null ;

		if(actionsCriteriaList.size() == 1 ){
			//subdictionary will have the form 
			actCriteriaString1 = actionsCriteriaList.get(0);

			for (List<String> othersCandActs :dictionary.keySet()){
				String candFirstAct  = othersCandActs.get(0);

				if(actCriteriaString1.equals(candFirstAct)){
					//TODO check for nulls on get(x)
					//candSecAct = othersCandActs.get(1);
					ActionDictObject dicObj = dictionary.get(othersCandActs);
					subDictionary.put(othersCandActs,dicObj);
				}				
			}
		} else if(actionsCriteriaList.size() == 2 ){
			actCriteriaString1 = actionsCriteriaList.get(0);
			actCriteriaString2 = actionsCriteriaList.get(1);

			for (List<String> othersCandActs :dictionary.keySet()){

				if(othersCandActs.get(0) != null && othersCandActs.get(1) != null){
					String candFirstAct  = othersCandActs.get(0);
					String candSecondAct  = othersCandActs.get(1);

					if(actCriteriaString1.equals(candFirstAct) && actCriteriaString2.equals(candSecondAct)){
						subDictionary.put(othersCandActs,dictionary.get(othersCandActs));
					}	
				}
			}	
		}

		return subDictionary;
	}


}