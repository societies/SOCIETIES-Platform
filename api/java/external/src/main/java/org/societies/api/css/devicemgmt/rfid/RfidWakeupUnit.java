package org.societies.api.css.devicemgmt.rfid;

import java.io.Serializable;

public class RfidWakeupUnit implements Serializable {
	
	private String wakeupUnitNumber;
	private String screenID;
	
	public RfidWakeupUnit() {
		
	}
	
	public String getScreenID() {
		return screenID;
	}
	public void setScreenID(String screenID) {
		this.screenID = screenID;
	}
	public String getWakeupUnitNumber() {
		return wakeupUnitNumber;
	}
	public void setWakeupUnitNumber(String wakeupUnitNumber) {
		this.wakeupUnitNumber = wakeupUnitNumber;
	}
	

}
