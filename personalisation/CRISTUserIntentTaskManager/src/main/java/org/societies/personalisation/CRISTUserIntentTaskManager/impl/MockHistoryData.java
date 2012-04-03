package org.societies.personalisation.CRISTUserIntentTaskManager.impl;

import java.util.ArrayList;

public class MockHistoryData {

	ArrayList<String> context;
	String actionValue;
	String situationValue;

	MockHistoryData(String action, String situation, ArrayList<String> context) {
		this.actionValue = action;
		this.situationValue = situation;
		this.context = context;
	}

	public ArrayList<String> getContext() {
		return context;
	}

	public void setContext(ArrayList<String> context) {
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
