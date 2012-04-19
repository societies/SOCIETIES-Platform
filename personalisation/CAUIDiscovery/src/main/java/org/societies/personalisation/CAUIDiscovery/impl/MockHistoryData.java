package org.societies.personalisation.CAUIDiscovery.impl;

import java.util.List;

public class MockHistoryData {

	String context;
	String actionValue;
	
	public MockHistoryData(String action, String context){
		this.actionValue = action;
		this.context = context;
	}

	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String toString(){
		String string = this.actionValue+" "+this.context;
		return string; 
	}
}
