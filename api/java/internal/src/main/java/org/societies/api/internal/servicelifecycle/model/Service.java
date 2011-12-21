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
	 * It represents the CSS endpoint where the service is physically installed.
	 */
	private org.societies.comm.xmpp.Endpoint CSSInstalled;
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
	private int lifetime;
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
	 * Constructor for a new Service object
	 * 
	 * @param version
	 * @param CSSInstalled
	 * @param name
	 */
	public void Service(String version, org.societies.comm.xmpp.Endpoint CSSInstalled, String name){

	}
}