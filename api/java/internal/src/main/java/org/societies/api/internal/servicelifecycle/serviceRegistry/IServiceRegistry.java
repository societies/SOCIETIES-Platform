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
package org.societies.api.internal.servicelifecycle.serviceRegistry;

import java.util.List;

import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.QuerySubjectType;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntryOut;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.ServiceResourceIdentifier;


import org.societies.api.internal.servicelifecycle.model.Service;

/**
 * @author Antonio Panazzolo, Massimo Mazzariol (SN)
 */
public interface IServiceRegistry {

	/**
	 * Description: This method provides the interface to add a new list of services.
	 * 				List services can be composed at least by only service at time
	 * @param servicesList
	 * @throws ServiceRegistrationException
	 */
	public void registerServiceList (List<Service> servicesList) throws ServiceRegistrationException;
	
	/**
	 * Description: This method permits you to unregister a services list
	 * @param servicesList
	 * @throws ServiceRegistrationException
	 */
	public void unregisterServiceList (List<Service> servicesList) throws ServiceRegistrationException;

	/**
	 * Description: Based on a CSS identifier this method returns all services shared by CSS to other CSS or CIS 
	 * @param CSSID that represents the identifier for CSS, 		  
	 * @return a List of services retrieved
	 */
	public List<Service> retrieveServicesSharedByCSS (String CSSID);
	
	
	/**
	 * Description: Based on a CIS identifier this method returns all services that are shared by a CIS 
	 * @param CISID that represents the identifier for CIS
	 * @return a List of services retrieved
	 */
	public List<Service> retrieveServicesSharedByCIS (String CISID);
	
    /**
     * Description: Based on a CIS identifier this method is used to notify to the Service Registry that a Service is shared in a CIS
	 * @param CISID that represents the identifier for CIS that shares the Service
	 * @param serviceEndpointURI the unique identifier for the shared service
	 * @throws ServiceSharingNotificationException
     */
	public void notifyServiceIsSharedInCIS(String serviceEndpointURI, String CISID) throws ServiceSharingNotificationException;
	
	/**
     * Description: Based on a CIS identifier this method is used to notify to the Service Registry that a Service is removed from a CIS
	 * @param CISID that represents the identifier for the CIS 
	 * @param serviceEndpointURI the unique identifier for the service
	 * @throws ServiceSharingNotificationException
     */

	 
	public void removeServiceSharingInCIS(String serviceEndpointURI, String CISID) throws ServiceSharingNotificationException;
	
	
	/**
	* Description: Based on a Filter this method returns the services list
	* matching that particular filter
	* @param the object used as filter for the query
	* @return the list of services that match the filter
	*/
	public List<Service> findServices (Object filter);
	
	/**
	* Description: Based on a service unique identifier this method returns the associated Service
	* @param the unique identifier for the Service
	* @return the corresponding Service
	*/
	public Service retrieveService(String serviceEnpointURI);
}