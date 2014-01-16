package org.societies.orchestration.sca.model;

import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;

public class SCAInvitation {
	
	private String cisID;
	private String cisName;
	private String fromJID;
	private SCASuggestedMethodType methodType;
	
	public SCAInvitation(String cisID, String cisName, String fromJID, SCASuggestedMethodType methodType) {
		this.cisID = cisID;
		this.cisName = cisName;
		this.fromJID = fromJID;
		this.methodType = methodType;
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
	
	
}
