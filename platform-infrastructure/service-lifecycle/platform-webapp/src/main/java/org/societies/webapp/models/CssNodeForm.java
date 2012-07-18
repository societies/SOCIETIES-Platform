package org.societies.webapp.models;

public class CssNodeForm {
	
	private String method; 
	private String cssNodeId;
	private int cssNodeStatus;
	private int cssNodeType;
	private String cssNodeMAC;
	private boolean Interactable;
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * @return the cssNodeId
	 */
	public String getCssNodeId() {
		return cssNodeId;
	}
	/**
	 * @param cssNodeId the cssNodeId to set
	 */
	public void setCssNodeId(String cssNodeId) {
		this.cssNodeId = cssNodeId;
	}
	
	/**
	 * @return the Node Status
	 */
	public int getcssNodeStatus() {
		return cssNodeStatus;
	}
	/**
	 * @param deviceName the Node Status to set
	 */
	public void setcssNodeStatus(int cssNodeStatus) {
		this.cssNodeStatus = cssNodeStatus;
	}
	/**
	 * @return the Node Type
	 */
	public int getcssNodeType() {
		return cssNodeType;
	}
	/**
	 * @param deviceType the Node Type to set
	 */
	public void setcssNodeType(int cssNodeType) {
		this.cssNodeType = cssNodeType;
	}
	
	/**
	 * @return the deviceID
	 */
	public String getcssNodeMAC() {
		return cssNodeMAC;
	}
	/**
	 * @param deviceID the deviceID to set
	 */
	public void setcssNodeMAC(String cssNodeMAC) {
		this.cssNodeMAC = cssNodeMAC;
	}
	/**
	 * @return the contextSource
	 */
	public boolean isInteractable() {
		return Interactable;
	}
	/**
	 * @param contextSource the contextSource to set
	 */
	public void setInteractable(boolean Interactable) {
		this.Interactable = Interactable;
	}

}