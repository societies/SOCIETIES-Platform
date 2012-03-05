package org.societies.api.servicelifecycle.model;

import java.io.Serializable;
import java.net.URI;



/**
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.59
 */


public class ServiceResourceIdentifier implements IServiceResourceIdentifier, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This attribute represents the id for the service implementation. This attribute must be set using the ServiceImplementation getServiceImplementationId method.
	 */
	private URI identifier;
	/**
	 * This attribute represents the id for a service instance. This attribute must be set using the ServiceInstance getServiceInstanceId method.
	 */
	private String serviceInstanceIdentifier;
	/**
	 * no- arg constructor is needed for xml to object mapping
	 */
	public ServiceResourceIdentifier() {
		super();		
	}
	/**
	 * @param identifier
	 * @param serviceInstanceIdentifier
	 */
	public ServiceResourceIdentifier(URI identifier,
			String serviceInstanceIdentifier) {
		super();
		this.identifier = identifier;
		this.serviceInstanceIdentifier = serviceInstanceIdentifier;
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
	public String getServiceInstanceIdentifier() {
		return serviceInstanceIdentifier;
	}
	public void setServiceInstanceIdentifier(String serviceInstanceIdentifier) {
		this.serviceInstanceIdentifier = serviceInstanceIdentifier;
	}
}