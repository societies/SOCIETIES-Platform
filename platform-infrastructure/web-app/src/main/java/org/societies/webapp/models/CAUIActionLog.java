package org.societies.webapp.models;

import java.io.Serializable;

public class CAUIActionLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7025793588385591524L;
	
	String performedAction ="";
	String predictedAction ="";
	
	public CAUIActionLog(String performedAction,String predictedAction ){
		
		this.performedAction = 	performedAction;
		this.predictedAction = predictedAction;
	}
		
	
	@Override
	public String toString() {
		
		String stringValue =this.performedAction +"=>"+this.predictedAction;
		return stringValue;
	}

	
	public String getPerformedAction() {
		return performedAction;
	}

	public void setPerformedAction(String performedAction) {
		this.performedAction = performedAction;
	}
	
	
	public String getPredictedAction() {
		return predictedAction;
	}

	public void setPredictedAction(String predictedAction) {
		this.predictedAction = predictedAction;
	}
}