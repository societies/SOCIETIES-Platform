package org.societies.api.internal.servicelifecycle.model;

/**
 * The Service class represents a generic Service with appropriate attributes
 * 
 * @author mmazzariol
 * @version 1.0
 * @created 21-dic-2011 17.18.32
 */

public class Service {

	/**
	 * Unique identifier for a single instance of a service.
	 */
	private ServiceResourceIdentifier serviceIdentifier;

	/**
	 * It represents the CSS Id where the service is physically installed.
	 */
	private String CSSIDInstalled;
	/**
	 * The version of the service, it must be updated by developer
	 */
	private String version;

	/**
	 * An alias name for the service
	 */
	private String serviceName;
	/**
	 * A "long" description of the service
	 */
	private String serviceDescription;
	/**
	 * The signature of the author
	 */
	private String authorSignature;

	/**
	 * @param serviceIdentifier
	 * @param cSSIDInstalled
	 * @param version
	 * @param serviceName
	 * @param serviceDescription
	 * @param authorSignature
	 */
	public Service(ServiceResourceIdentifier serviceIdentifier,
			String cSSIDInstalled, String version, String serviceName,
			String serviceDescription, String authorSignature) {
		super();
		this.serviceIdentifier = serviceIdentifier;
		this.CSSIDInstalled = cSSIDInstalled;
		this.version = version;
		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
	}
	
	public Service() {
		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getAuthorSignature() {
		return authorSignature;
	}

	public void setAuthorSignature(String authorSignature) {
		this.authorSignature = authorSignature;
	}

	public String getCSSIDInstalled() {
		return CSSIDInstalled;
	}

	public void setCSSIDInstalled(String CSSIDInstalled) {
		this.CSSIDInstalled = CSSIDInstalled;
	}

	public ServiceResourceIdentifier getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(ServiceResourceIdentifier serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}
}