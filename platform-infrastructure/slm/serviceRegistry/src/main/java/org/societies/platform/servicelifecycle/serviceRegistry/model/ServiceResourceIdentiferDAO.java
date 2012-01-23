package org.societies.platform.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;

public class ServiceResourceIdentiferDAO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7982137477351378779L;

	private String identifier;

	public ServiceResourceIdentiferDAO() {
		
	}

	public ServiceResourceIdentiferDAO(String identifier) {
		super();
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
