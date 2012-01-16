package org.societies.api.internal.servicelifecycle.model;

/**
 * The Service class represents a generic Service with appropriate attributes
 * @author mmazzariol
 * @version 1.0
 * @created 21-dic-2011 17.18.32
 */

public class Service {

	/**
	 * Unique identifier for a single instance of a service.
	 */
	private String serviceEndpointURI;
	/**
	 * It represents the CSS Id where the service is physically installed.
	 */
	 private String CSSIDInstalled;
	/**
	 * The version of the service, it must be updated by developer
	 */
	private String version;
	/**
	 * <No description from WP3>
	 */
	private String hash;
	/**
	 * <No description from WP3>
	 */
	private long lifetime;
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
	
	
	public String getServiceEndpointURI() {
		return serviceEndpointURI;
	}
	
	public void setServiceEndpointURI(String serviceEndpointURI) {
		this.serviceEndpointURI = serviceEndpointURI;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public long getLifetime() {
		return lifetime;
	}
	
	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
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

	public String getCSSIDInstalled(){
		return CSSIDInstalled;
	}
	public void setCSSIDInstalled(String CSSIDInstalled){
		this.CSSIDInstalled=CSSIDInstalled;
	}
}