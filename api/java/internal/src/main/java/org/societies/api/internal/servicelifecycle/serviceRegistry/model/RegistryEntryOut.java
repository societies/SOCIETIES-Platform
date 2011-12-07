package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.net.URI;
//TODO : temporary mock package import to solve missing package in API folder
import org.societies.api.internal.mock.EndPoint;

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
	//BJB remove package reference as import as been added.
	private EndPoint CSSInstalled;
	private EndPoint CSSNodeInstalled;
	private URI serviceURI;
	private String organizationId;
	private ServiceResourceIdentifier serviceIdentifier;
	private String authorSignature;

	public RegistryEntryOut(){

	}

	public void finalize() throws Throwable {

	}

}