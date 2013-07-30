/**
 * 
 */
package org.societies.webapp.controller.rfid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eliza
 *
 */
public class RfidBean {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private String rfidTag = "";
	private String password = "";
	private String userIdentity = "";
	private String wakeupUnit = "";
	private String symLoc = "";
	
	public String getSymLoc() {
		return symLoc;
	}
	public void setSymLoc(String symLoc) {
		this.symLoc = symLoc;
	}
	public String getWakeupUnit() {
		return wakeupUnit;
	}
	public void setWakeupUnit(String wakeupUnit) {
		this.wakeupUnit = wakeupUnit;
	}
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRfidTag() {
		return rfidTag;
	}
	public void setRfidTag(String rfidTag) {
		this.logging.debug("Setting rfidTag: "+rfidTag);
		this.rfidTag = rfidTag;
	}
}
