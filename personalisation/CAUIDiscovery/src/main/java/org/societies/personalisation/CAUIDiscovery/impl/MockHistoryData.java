package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MockHistoryData {

	
	Date timestamp;
	Map<String,String> contextMap = new HashMap<String,String>();
	String contextValue = "";

	String parameterName;
	String actionValue;

	public MockHistoryData(String parameterName,String actionValue, Map<String,String> context, Date timestamp){
		this.parameterName = parameterName;
		this.actionValue = actionValue;
		this.contextMap = context;
		this.timestamp = timestamp;
	}

	public Map<String,String> getContext() {
		return contextMap;
	}

	public void setContext(Map<String,String> context) {
		this.contextMap = context;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getParameterName() {
		return parameterName;
	}


	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getContextValue(String type) {
		if(contextMap.containsKey(type)){
			Serializable valueSerial = contextMap.get(type);
			if (valueSerial instanceof String) {
				String value = (String) valueSerial;	
				contextValue = value;
			}
		}
		return contextValue;
	}

	public void setSymLoc() {
		
	}

	public String toString(){
		String string = this.parameterName+" "+this.actionValue+" "+this.timestamp.getTime()+" "+this.contextMap ;
		return string; 
	}
}