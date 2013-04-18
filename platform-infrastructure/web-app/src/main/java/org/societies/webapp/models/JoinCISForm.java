package org.societies.webapp.models;

import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

@Deprecated // No longer used after move from JSP to JSF
public class JoinCISForm {

	private CisAdvertisementRecord cisAdvertisement;
	
	public CisAdvertisementRecord getCisAdvertisement() {
		return cisAdvertisement;
	}

	public void setCisAdvertisement(CisAdvertisementRecord cisAdvertisement) {
		this.cisAdvertisement = cisAdvertisement;
	}

	public JoinCISForm() {
		
	}

}
