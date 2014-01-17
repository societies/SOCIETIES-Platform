package org.societies.orchestration.sca.model;

import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;

public class SCAInvitation {
	
	private String cisID;
	private String cisName;
	private String fromJID;
	private SCASuggestedMethodType methodType;
	private boolean forceAction;
	
	public SCAInvitation(String cisID, String cisName, String fromJID, SCASuggestedMethodType methodType, boolean forceAction) {
		this.cisID = cisID;
		this.cisName = cisName;
		this.fromJID = fromJID;
		this.methodType = methodType;
		this.forceAction = forceAction;
	}
	
	public SCAInvitation(String cisID, String cisName, String fromJID, SCASuggestedMethodType methodType) {
		this.cisID = cisID;
		this.cisName = cisName;
		this.fromJID = fromJID;
		this.methodType = methodType;
		this.forceAction = false;
	}
	
	public String getCisID() {
		return cisID;
	}
	public void setCisID(String cisID) {
		this.cisID = cisID;
	}
	public String getCisName() {
		return cisName;
	}
	public void setCisName(String cisName) {
		this.cisName = cisName;
	}
	public String getFromJID() {
		return fromJID;
	}
	public void setFromJID(String fromJID) {
		this.fromJID = fromJID;
	}
	public SCASuggestedMethodType getMethodType() {
		return methodType;
	}
	public void setMethodType(SCASuggestedMethodType methodType) {
		this.methodType = methodType;
	}

	/**
	 * @return the forceAction
	 */
	public boolean isForceAction() {
		return forceAction;
	}

	/**
	 * @param forceAction the forceAction to set
	 */
	public void setForceAction(boolean forceAction) {
		this.forceAction = forceAction;
	}
	
	
	
	
}
