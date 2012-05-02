package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MockHistoryData {

	Map<String,Serializable> context = new HashMap<String,Serializable>();
	String contextValue = "";

	String parameterName;
	String actionValue;

	public MockHistoryData(String parameterName,String actionValue, Map<String,Serializable> context){
		this.parameterName = parameterName;
		this.actionValue = actionValue;
		this.context = context;
	}

	public Map<String,Serializable> getContext() {
		return context;
	}

	public void setContext(Map<String,Serializable> context) {
		this.context = context;
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
		if(context.containsKey(type)){
			Serializable valueSerial = context.get(type);
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
		String string = this.parameterName+" "+this.actionValue+" "+this.context;
		return string; 
	}
}
