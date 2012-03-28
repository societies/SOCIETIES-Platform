package org.societies.personalisation.CRISTUserIntentDiscovery.impl;

import java.util.List;

public class MockHistoryData {

	List<String> context;
	String actionValue;
	String situationValue;

	MockHistoryData(String action, String situation, List<String> context) {
		this.actionValue = action;
		this.situationValue = situation;
		this.context = context;
	}

	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getSituationValue() {
		return situationValue;
	}
	
	public void setSituationValue(String situationValue) {
		this.situationValue = situationValue;
	}
	
	public String toString() {
		String string = this.actionValue + " " + this.situationValue + " " + this.context;
		return string;
	}
}
