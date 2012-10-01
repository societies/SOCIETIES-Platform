package org.societies.webapp.models;

import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class CisDirectoryCombinedDetails {
	CisAdvertisementRecord adrecord = new CisAdvertisementRecord();
	String cssownername = new String();
	String cssownerid = new String();
		
	public CisAdvertisementRecord getAdrecord() {
		return adrecord;
	}
	public void setAdrecord(CisAdvertisementRecord adrecord) {
		this.adrecord = adrecord;
	}
	public String getCssownername() {
		return cssownername;
	}
	public void setCssownername(String cssownername) {
		this.cssownername = cssownername;
	}
	
	public String getCssownerid() {
		return cssownerid;
	}
	
	public void setCssownerid(String cssownerid) {
		this.cssownerid = cssownerid;
	}
	
	public CisDirectoryCombinedDetails(CisAdvertisementRecord adrecord,
			String cssownername, String cssownerid) {
		super();
		this.adrecord = adrecord;
		this.cssownername = cssownername;
		this.cssownerid = cssownerid;
	}
	
	public CisDirectoryCombinedDetails() {
		super();
	}
	
	
	
	
}
