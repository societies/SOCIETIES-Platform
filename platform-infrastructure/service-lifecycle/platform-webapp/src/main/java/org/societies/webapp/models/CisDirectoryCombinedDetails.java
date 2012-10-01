package org.societies.webapp.models;

import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class CisDirectoryCombinedDetails {
	CisAdvertisementRecord adrecord = new CisAdvertisementRecord();
	String cssownername = new String();
	
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
	
	public CisDirectoryCombinedDetails(CisAdvertisementRecord adrecord,
			String cssownername) {
		super();
		this.adrecord = adrecord;
		this.cssownername = cssownername;
	}
	
	public CisDirectoryCombinedDetails() {
		super();
	}
	
	
	
	
}
