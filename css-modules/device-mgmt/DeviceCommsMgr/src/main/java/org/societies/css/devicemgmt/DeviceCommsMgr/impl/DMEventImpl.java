package org.societies.css.devicemgmt.DeviceCommsMgr.impl;


public class DMEventImpl{

	private String deviceId="";
	private String description="";
	
	public DMEventImpl(){}
	
	public DMEventImpl(String deviceId, String description){
		this.deviceId = deviceId;
		this.description = description;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
