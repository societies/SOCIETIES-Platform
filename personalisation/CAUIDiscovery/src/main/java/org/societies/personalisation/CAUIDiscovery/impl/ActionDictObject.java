package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionDictObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	int totalOccurences;

	//key = object
	//(status=free)
	//(temp=hot)
	
	List<CtxAttribute> contextData = new ArrayList<Map<String,String>>();
	
	//[C1]=1
	//key:list(C1,C2) value(int)
	//key:contextData value:int
	HashMap<List<Map<String,String>>,Integer> contextMap = new HashMap<List<Map<String,String>>,Integer>(); 

	
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
