package org.societies.webapp.models;

public class CssNodeForm {
	
	private String method; 
	private String cssNodeId;
	private int cssNodeStatus;
	private int cssNodeType;
	private String cssNodeMAC;
	private String interactable;
	private String nodetypes;
	private String nodestatus;
	
	
	public String getnodestatus() {
		return nodestatus;
	}
	public void setnodestatus(String nodestatus) {
		this.nodestatus = nodestatus;
	}
	
	public String getnodetypes() {
		return nodetypes;
	}
	public void setnodetypes(String nodetypes) {
		this.nodetypes = nodetypes;
	}
	
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
	 * @return the 
	 */
	public String getInteractable() {
		return interactable;
	}
	/**
	 * @param 
	 */
	public void setInteractable(String interactable) {
		this.interactable = interactable;
	}

}