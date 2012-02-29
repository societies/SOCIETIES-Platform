package org.societies.slm.servicemanagment.schema.model;

import java.net.URI;

/**
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.59
 */


public class ServiceResourceIdentifier implements IServiceResourceIdentifier {
	
	private URI identifier;

	public ServiceResourceIdentifier() {
		super();		
	}
	
	public URI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String toString() {
		return identifier.toString();
	}
}