package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.net.URI;

/**
 * This Class represents the output object contained in the List returned after a
 * query to the ServiceRegistry.
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.58
 */
public class RegistryEntryOut {

	private String serviceName;
	private String version;
	private String serviceDescription;
	private org.societies.comm.xmpp.Endpoint CSSInstalled;
	private org.societies.comm.xmpp.Endpoint CSSNodeInstalled;
	private URI serviceURI;
	private String organizationId;
	private ServiceResourceIdentifier serviceIdentifier;
	private String authorSignature;

	public RegistryEntryOut(){

	}

	public void finalize() throws Throwable {

	}

}