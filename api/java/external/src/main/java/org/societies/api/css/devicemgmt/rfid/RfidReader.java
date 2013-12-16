package org.societies.api.css.devicemgmt.rfid;

import java.io.Serializable;
import java.util.ArrayList;

public class RfidReader implements Serializable {
	
	private int readerNumber;
	private ArrayList<String> wakeupUnits;
	private String ipAddress;
	
	public RfidReader() {
		
	}

	public int getReaderNumber() {
		return readerNumber;
	}

	public void setReaderNumber(int readerNumber) {
		this.readerNumber = readerNumber;
	}
	public ArrayList<String> getWakeupUnits() {
		return wakeupUnits;
	}

	public void setWakeupUnits(ArrayList<String> wakeupUnits) {
		this.wakeupUnits = wakeupUnits;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
