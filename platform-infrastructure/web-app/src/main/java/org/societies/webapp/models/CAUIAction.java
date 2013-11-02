package org.societies.webapp.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.societies.api.internal.context.model.CtxAttributeTypes;


public class CAUIAction implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String sourceAction ="";
	String targetAction ="";
	String ctxAction ="";
	
	HashMap<String, Double>  targetActionMap = new HashMap<String, Double>();
	
	Double trans = 0.0;
	
	public CAUIAction(String sourceAction, HashMap<String, Double>  targetActionMap, Map<String,Serializable>  ctxActionMap){
	
		this.sourceAction = sourceAction;
		this.targetActionMap = targetActionMap;	
		
		
		for( String tAct :targetActionMap.keySet()){
			Double transProb = targetActionMap.get(tAct);
			this.targetAction = targetAction +" {"+tAct+":"+transProb+"} ";
		}
		
		if(ctxActionMap.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null ){
			ctxAction = "location:"+  ctxActionMap.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
		} else {
			ctxAction = "location: n/a";
		
		}
		
		 
		if(ctxActionMap.get(CtxAttributeTypes.HOUR_OF_DAY) != null ){
			ctxAction = ctxAction +", HOUR_OF_DAY:"+  ctxActionMap.get(CtxAttributeTypes.HOUR_OF_DAY);
		} else {
			ctxAction = ctxAction +", HOUR_OF_DAY: n/a";
		}
		
		if(ctxActionMap.get(CtxAttributeTypes.DAY_OF_WEEK) != null ){
			ctxAction = ctxAction +", DAY_OF_WEEK:"+  ctxActionMap.get(CtxAttributeTypes.DAY_OF_WEEK);
		} else {
			ctxAction = ctxAction +", DAY_OF_WEEK: n/a";
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
	
	
	public String getCtxAction() {
		return ctxAction;
	}



	public void setCtxAction(String ctxAction) {
		this.ctxAction = ctxAction;
	}
}
