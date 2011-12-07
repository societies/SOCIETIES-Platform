package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.net.URI;
import java.util.List;

//TODO : temporary mock package import to solve missing package in API folder
import org.societies.api.internal.mock.EndPoint;

/**
 * This is the Class accepted by the ServiceRegistry when a service wants to
 * register.
 * This Object contains attributes used to retrieve services shared from/to a
 * CSS/CIS and also information to retrieve organization that has developed the
 * service.
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.57
 */
public class RegistryEntry {

	private String serviceName;
	private String version;
	private String serviceDescription;
	// BJB : remove reference to org.societies.comm.xmpp
	private List<EndPoint> sharedWithCSS;
	private List<EndPoint> sharedWithCIS;
	private EndPoint CSSInstalled;
	private EndPoint CSSNodeInstalled;
	/* old
	private List<org.societies.comm.xmpp.Endpoint> sharedWithCSS;
	private List<org.societies.comm.xmpp.Endpoint> sharedWithCIS;
	private org.societies.comm.xmpp.Endpoint CSSInstalled;
	private org.societies.comm.xmpp.Endpoint CSSNodeInstalled;
	end old */
	private URI serviceURI;
	private String organizationId;
	private ServiceResourceIdentifier serviceIdentifier;
	private String authorSignature;

	public RegistryEntry(){

	}

	public void finalize() throws Throwable {

	}

}