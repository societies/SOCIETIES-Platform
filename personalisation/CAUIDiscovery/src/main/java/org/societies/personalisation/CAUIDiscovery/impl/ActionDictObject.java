package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionDictObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	int totalOccurences;
	List<String> contextData = new ArrayList<String>();
	
	//[C1]=1
	//key:list(C1,C2) value(int)
	HashMap<List<String>,Integer> contextMap = new HashMap<List<String>,Integer>(); 

	
	public int getTotalOccurences() {
		return totalOccurences;
	}

	public void setTotalOccurences(int totalOccurences) {
		this.totalOccurences = totalOccurences;
	}

	public HashMap<List<String>,Integer> getContextMap() {
		return contextMap;
	}

	public void setContextMap(HashMap<List<String>,Integer> contextMap) {
		this.contextMap = contextMap;
	}


	public String toString(){
		String result = null;
		result= totalOccurences +" context:"+contextMap;
		
		return result;
	}

	ActionDictObject(){

	}
}
