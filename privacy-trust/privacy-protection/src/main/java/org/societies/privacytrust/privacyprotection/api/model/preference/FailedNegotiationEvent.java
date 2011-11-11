package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public class FailedNegotiationEvent implements Serializable{

	private ServiceResourceIdentifier serviceID;
	public FailedNegotiationEvent(ServiceResourceIdentifier serviceID){
		this.setServiceID(serviceID);
	}
	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}
	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}
}
