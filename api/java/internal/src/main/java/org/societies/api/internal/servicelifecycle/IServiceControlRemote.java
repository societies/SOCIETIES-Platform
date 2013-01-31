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
package org.societies.api.internal.servicelifecycle;

import java.net.URL;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * The interface class for remote calls to the Service Control component. It permits a caller to tell the SLM to
 * start a service, to stop a service, to install a new service and to uninstall a service.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public interface IServiceControlRemote {

	/**
	 * This method starts the service that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @param node The node where the service is located
	 * @param callback The callback object
	 */
	
	public void startService(ServiceResourceIdentifier serviceId, IIdentity node, IServiceControlCallback callback);

	
	/**
	 * This method stops the service running in the container that is identified by the </code>ServiceResourceIdentifier</code>
	 * 
	 * @param serviceId unique service identifier
	 * @param node The node where the service is located
	 * @param callback The callback object
	 */
	public void stopService(ServiceResourceIdentifier serviceId, IIdentity node, IServiceControlCallback callback);
	
	/**
	 * This method install a new service into the container
	 * 
	 * @param bundleLocation the URL of the bundle to install
	 * @param node The node where the service should be installed
	 * @param callback The callback object
	 */
	public void installService(URL bundleLocation, IIdentity node, IServiceControlCallback callback);

	
	/**
	 * This method installs a shared service into the container
	 * 
	 * @param service the Service to install
	 * @param node The node where the service should be installed.
	 * @param callback The callback object
	 */
	public void installService(Service service, IIdentity node, IServiceControlCallback callback);

	/**
	 * This method removes a service from the container.
	 * 
	 * @param serviceId unique service identifier
	 * @param node The node where the service is located
	 * @param callback The callback object
	 */
	public void uninstallService(ServiceResourceIdentifier serviceId, IIdentity node, IServiceControlCallback callback);


	/**
	 * This method shares a service with a given CSS or CIS
	 * 
	 * @param service the Service to share
	 * @param node The node we are sharing with
	 * @param callback The callback object
	 */
	public void shareService(Service service, IIdentity node, IServiceControlCallback callback);

	/**
	 * This method removes the sharing of a service with a given CSS or CIS
	 * 
	 * @param service the Service to share
	 * @param node The node we are sharing with
	 * @param callback The callback object
	 */
	public void unshareService(Service service, IIdentity node, IServiceControlCallback callback);
	
}
