package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MockHistoryData {

	
	Date timestamp;
	Map<String,String> contextMap = new HashMap<String,String>();
	String contextValue = "";
	String serviceId = "";
	
	String parameterName;
	String actionValue;

	public MockHistoryData(String parameterName,String actionValue, Map<String,String> context, Date timestamp, String serviceId){
		this.parameterName = parameterName;
		this.actionValue = actionValue;
		this.contextMap = context;
		this.timestamp = timestamp;
		this.serviceId  = serviceId;
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
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String toString(){
		String string = this.parameterName+" "+this.actionValue+" "+this.timestamp.getTime()+" "+this.contextMap ;
		return string; 
	}
}