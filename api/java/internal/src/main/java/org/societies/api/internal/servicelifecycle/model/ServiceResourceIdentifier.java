package org.societies.api.internal.servicelifecycle.model;

import java.io.Serializable;
import java.net.URI;

import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

/**
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.59
 */


public class ServiceResourceIdentifier implements IServiceResourceIdentifier, Serializable {

	/**
	 * @param identifier
	 */
	public ServiceResourceIdentifier(URI identifier) {
		super();
		this.identifier = identifier;
	}

	URI identifier;
	
	public URI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}
}