package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ActionDictObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	int totalOccurences;
	//List<String> contextData = new ArrayList<String>();
	
	//[C1]=1
	//key:list(C1,C2) value(int)
	HashMap<List<String>,Integer> locationContextMap = new HashMap<List<String>,Integer>(); 
	HashMap<List<String>,Integer> dayOfWeekContextMap = new HashMap<List<String>,Integer>();
	HashMap<List<String>,Integer> hourOfDayContextMap = new HashMap<List<String>,Integer>();
	
	public HashMap<List<String>, Integer> getLocationContextMap() {
		return locationContextMap;
	}

	public void setLocationContextMap(
			HashMap<List<String>, Integer> locationContextMap) {
		this.locationContextMap = locationContextMap;
	}
	
	public HashMap<List<String>, Integer> getDayOfWeekContextMap() {
		return dayOfWeekContextMap;
	}

	public void setDayOfWeekContextMap(HashMap<List<String>, Integer> dayOfWeekContextMap) {
		this.dayOfWeekContextMap = dayOfWeekContextMap;
	}

	public HashMap<List<String>, Integer> getHourOfDayContextMap() {
		return hourOfDayContextMap;
	}

	public void setHourOfDayContextMap(HashMap<List<String>, Integer> hourOfDayContextMap) {
		this.hourOfDayContextMap = hourOfDayContextMap;
	}
	
	public int getTotalOccurences() {
		return totalOccurences;
	}

	public void setTotalOccurences(int totalOccurences) {
		this.totalOccurences = totalOccurences;
	}

	public String toString(){
		String result = null;
		result= totalOccurences +" location:"+getLocationContextMap() +" DayOfWeek:"+getDayOfWeekContextMap()+" hourOfDay"+getHourOfDayContextMap();
		
		return result;
	}

	ActionDictObject(){

	}
}
