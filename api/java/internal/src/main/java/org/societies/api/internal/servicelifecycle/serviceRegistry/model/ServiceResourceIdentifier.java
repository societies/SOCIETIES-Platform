package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.net.URI;

/**
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.59
 */
public class ServiceResourceIdentifier {

	private String hash;
	private int lifetime;
	private URI identifier;

	public ServiceResourceIdentifier(){

	}

	public void finalize() throws Throwable {

	}

}