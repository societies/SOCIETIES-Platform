package org.societies.personalisation.CAUIDiscovery.test;

import java.util.List;

public class MockHistoryData {

	List<String> context;
	String actionValue;
	
	MockHistoryData(String action, List<String> context){
		this.actionValue = action;
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
