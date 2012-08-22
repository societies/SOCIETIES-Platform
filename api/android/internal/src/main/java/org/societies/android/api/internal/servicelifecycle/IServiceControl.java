/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.internal.servicelifecycle;

import java.net.URL;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;

/**
 * The interface class for remote calls to the Service Control component. It permits a caller to tell the SLM to
 * start a service, to stop a service, to install a new service and to uninstall a service.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public interface IServiceControl {

	public String methodsArray[] = {"startService(String client, ServiceResourceIdentifier serviceId, String identity)", 
								    "stopService(String client, ServiceResourceIdentifier serviceId, String identity)",
								    "installService(String client, URL bundleLocation, String identity)",
								    "shareService(String client, Service service, String identity)",
								    "unshareService(String client, Service service, String identity)"
								   };
	
	/**
	 * This method starts the service that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @param identity The target node where service is installed
	 */
	
	public ServiceControlResult startService(String client, ServiceResourceIdentifier serviceId, String identity);

	
	/**
	 * This method stops the service running in the container that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @param identity The target node where service is installed
	 */
	public ServiceControlResult stopService(String client, ServiceResourceIdentifier serviceId, String identity);
	
	/**
	 * This method install a new service into the container
	 * 
	 * @param bundleLocation the URL of the bundle to install
	 * @param identity The target node where service is to be installed
	 */
	public ServiceControlResult installService(String client, URL bundleLocation, String identity);

	
	/**
	 * This method installs a shared service into the container
	 * 
	 * @param service the Service to install
	 * @param callback The callback object
	 */
	//public void installService(String client, Service service, IIdentity node);

	/**
	 * This method removes a service from the container.
	 * 
	 * @param serviceId unique service identifier
	 * @param callback The callback object
	 */
	//public void uninstallService(String client, ServiceResourceIdentifier serviceId, IIdentity node);


	/**
	 * This method shares a service with a given CSS or CIS
	 * 
	 * @param service the Service to share
	 * @param identity The target node where service is to be shared
	 */
	public ServiceControlResult shareService(String client, Service service, String identity);

	/**
	 * This method removes the sharing of a service with a given CSS or CIS
	 * 
	 * @param service the Service to share
	 * @param identity The target node where service is to be unshared
	 */
	public ServiceControlResult unshareService(String client, Service service, String identity);
	
	/**
	 * This method is used to register a new CIS Endpoint with Service Control. It will allow service control to receive
	 * meant for a particular CIS
	 * 
	 * @param endpoint
	 */
	//public void registerCISEndpoint(ICommManager endpoint);	
}
