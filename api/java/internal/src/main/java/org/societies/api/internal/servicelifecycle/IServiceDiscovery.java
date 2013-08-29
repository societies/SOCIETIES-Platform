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

import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * 
 * @author mmannion
 *
 */
public interface IServiceDiscovery {
	
	/**
	 * Description: This method returns a service given its ServiceResourceIdentifier
	 * @return the Service that was requested, null if nothing was found
	 * @throws ServiceDiscoveryException
	 */
	public Future<Service> getService(ServiceResourceIdentifier serviceId) throws ServiceDiscoveryException;

	/**
	 * Description: Based on a identify this method returns all services shared by 
	 * the specified CSS/CIS to other CSS's or CIS;s 
	 * @param node IIdentity 		  
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> getServices(IIdentity node) throws ServiceDiscoveryException;

	/**
	 * Description: Based on a jid this method returns all services shared by 
	 * the specified CSS/CIS to other CSS's or CIS;s 
	 * @param String jid 		  
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> getServices(String jid) throws ServiceDiscoveryException;

	/**
	 * Description: This method returns all services for the current node 
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> getLocalServices() throws ServiceDiscoveryException;

	/**
	 * Description: Searches all known services, given a certain criteria filter
	 * 
	 * @param filter the filter to search  
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> searchServices(Service filter) throws ServiceDiscoveryException;

	/**
	 * Description: Searches all known services in a given CSS/CIS, given a certain criteria filter.
	 * 
	 * @param filter the filter to search  
	 * @param node IIdentity 
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> searchServices(Service filter, IIdentity node) throws ServiceDiscoveryException;

	/**
	 * Description: Searches all known services in a given CSS/CIS, given a certain criteria filter.
	 * 
	 * @param filter the filter to search  
	 * @param String jib  
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> searchServices(Service filter, String jid) throws ServiceDiscoveryException;

	/**
	 * Description: Searches all known services in ALL known nodes and CIS, given a certain criteria filter.
	 * 
	 * @param filter the filter to search   
	 * @return a List of services retrieved
	 * @throws ServiceDiscoveryException
	 */
	public Future<List<Service>> searchServicesAll(Service filter)
			throws ServiceDiscoveryException;

}
