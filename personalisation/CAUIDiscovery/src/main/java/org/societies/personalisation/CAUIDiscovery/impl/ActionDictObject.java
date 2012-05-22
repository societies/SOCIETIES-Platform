package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionDictObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	int totalOccurences;
	//List<String> contextData = new ArrayList<String>();
	
	//[C1]=1
	//key:list(C1,C2) value(int)
	HashMap<List<String>,Integer> locationContextMap = new HashMap<List<String>,Integer>(); 
	HashMap<List<String>,Integer> statusContextMap = new HashMap<List<String>,Integer>();
	HashMap<List<String>,Integer> temperatureContextMap = new HashMap<List<String>,Integer>();
	
	public HashMap<List<String>, Integer> getLocationContextMap() {
		return locationContextMap;
	}

	public void setLocationContextMap(
			HashMap<List<String>, Integer> locationContextMap) {
		this.locationContextMap = locationContextMap;
	}

	public HashMap<List<String>, Integer> getStatusContextMap() {
		return statusContextMap;
	}

	public void setStatusContextMap(HashMap<List<String>, Integer> statusContextMap) {
		this.statusContextMap = statusContextMap;
	}

	public HashMap<List<String>, Integer> getTemperatureContextMap() {
		return temperatureContextMap;
	}

	public void setTemperatureContextMap(
			HashMap<List<String>, Integer> temperatureContextMap) {
		this.temperatureContextMap = temperatureContextMap;
	}

	
	public int getTotalOccurences() {
		return totalOccurences;
	}

	public void setTotalOccurences(int totalOccurences) {
		this.totalOccurences = totalOccurences;
	}

	public String toString(){
		String result = null;
		result= totalOccurences +" location:"+getLocationContextMap() +" status:"+getStatusContextMap()+" temperature"+getTemperatureContextMap();
		
		return result;
	}

	ActionDictObject(){

	}
}
