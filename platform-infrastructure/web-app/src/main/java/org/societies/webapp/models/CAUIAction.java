package org.societies.webapp.models;

import java.io.Serializable;
import java.util.HashMap;


public class CAUIAction implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String sourceAction ="";
	String targetAction ="";
	
	HashMap<String, Double>  targetActionMap = new HashMap<String, Double>();
	
	Double trans = 0.0;
	
	public CAUIAction(String sourceAction, HashMap<String, Double>  targetActionMap){
	
		this.sourceAction = sourceAction;
		this.targetActionMap = targetActionMap;	
		
		
		for( String tAct :targetActionMap.keySet()){
			Double transProb = targetActionMap.get(tAct);
			this.targetAction = targetAction +tAct+":"+transProb+",";
			
		}
	}

	
	
	public String getSourceAction() {
		return this.sourceAction;
	}

	public void setSourceAction(String sourceAction) {
		this.sourceAction = sourceAction;
	}

	public String getTargetAction() {
		return this.targetAction;
	}

	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}

	public Double getTrans() {
		return this.trans;
	}

	public void setTrans(Double trans) {
		this.trans = trans;
	}

	
	public HashMap<String, Double> getTargetActionMap() {
		return targetActionMap;
	}

	public void setTargetActionMap(
			HashMap<String, Double> targetActionMap) {
		this.targetActionMap = targetActionMap;
	}

	@Override
	public String toString() {
		
		String stringValue = sourceAction;
		
		return stringValue;
	}
	
}
