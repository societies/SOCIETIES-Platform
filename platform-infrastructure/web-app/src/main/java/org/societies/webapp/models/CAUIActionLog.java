package org.societies.webapp.models;

import java.io.Serializable;
import java.util.Map;

import org.societies.api.internal.context.model.CtxAttributeTypes;

public class CAUIActionLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7025793588385591524L;
	
	String performedAction ="";
	String predictedAction ="";
	String ctxAction ="";
	String time ="";
	
	public CAUIActionLog(String performedAction,String predictedAction, Map<String,Serializable>  ctxActionMap, String time ){
		
		this.performedAction = 	performedAction;
		this.predictedAction = predictedAction;
		this.time = time;
		
		if(ctxActionMap.get(CtxAttributeTypes.LOCATION_SYMBOLIC) != null ){
			ctxAction = "location:"+  ctxActionMap.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
		} else {
			ctxAction = "location: ,";
		}
		
		if(ctxActionMap.get(CtxAttributeTypes.HOUR_OF_DAY) != null ){
			ctxAction = ctxAction +" HOUR_OF_DAY:"+  ctxActionMap.get(CtxAttributeTypes.HOUR_OF_DAY);
		}else {
			ctxAction = ctxAction + "HOUR_OF_DAY: ,";
		}
		
		if(ctxActionMap.get(CtxAttributeTypes.DAY_OF_WEEK) != null ){
			ctxAction = ctxAction +", DAY_OF_WEEK:"+  ctxActionMap.get(CtxAttributeTypes.DAY_OF_WEEK);
		} else {
			ctxAction = ctxAction +"DAY_OF_WEEK: ";
		}
	}
		
	
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
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

	public String getCtxAction() {
		return ctxAction;
	}

	public void setCtxAction(String ctxAction) {
		this.ctxAction = ctxAction;
	}

}