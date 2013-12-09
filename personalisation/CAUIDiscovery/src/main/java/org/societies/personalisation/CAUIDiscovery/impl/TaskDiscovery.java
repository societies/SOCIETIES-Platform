package org.societies.personalisation.CAUIDiscovery.impl;

import java.util.LinkedHashMap;
import java.util.List;

public class TaskDiscovery {

	LinkedHashMap<List<String>,Integer> taskDictionary = null;
	LinkedHashMap<String,ActionDictObject> actCtxModel = new LinkedHashMap<String,ActionDictObject>();



	TaskDiscovery(LinkedHashMap<String,ActionDictObject> actCtxModel){
		taskDictionary = new LinkedHashMap<List<String>,Integer>();
		this.actCtxModel =  actCtxModel;
	}


	public LinkedHashMap<List<String>, Integer> getTaskDictionary() {
		return taskDictionary;
	}

	
	public void setTaskDictionary(LinkedHashMap<List<String>, Integer> taskDictionary) {
		this.taskDictionary = taskDictionary;
	}

	

	public LinkedHashMap<String,ActionDictObject> getSeqs(Double limit){
		LinkedHashMap<String,ActionDictObject> results = new LinkedHashMap<String,ActionDictObject>();
		
		
		return results;
	}

	
	public LinkedHashMap<String,ActionDictObject> getSeqs(int score){
		LinkedHashMap<String,ActionDictObject> results = new LinkedHashMap<String,ActionDictObject>();
		
		for (String act : this.actCtxModel.keySet()){
			int totalOccur = this.actCtxModel.get(act).getTotalOccurences();
			if( totalOccur > score) results.put(act, this.actCtxModel.get(act));	
		}

		//System.out.println("total entries in model "+this.actCtxModel.size()); 
		//System.out.println("entries occured more than "+score); 
		return results;
	}
	
}