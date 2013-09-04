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

import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CISNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CSSNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceUpdateException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;

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
	 * Description: Based on a CIS identifier this method returns all services that are shared by a CIS 
	 * @param CISID that represents the identifier for CIS (this is the String returned by method getJid() of class org.societies.comm.xmpp.datatypes.Identity)
	 * @return a List of services retrieved
	 * @throws ServiceRetrieveException
	 */
	public List<Service> retrieveServicesSharedByCIS (String CISID) throws ServiceRetrieveException;
	
    /**
     * Description: Based on a CIS identifier this method is used to notify to the Service Registry that a Service is shared in a CIS
	 * @param CISID that represents the identifier for CIS that shares the Service (this is the String returned by method getJid() of class org.societies.comm.xmpp.datatypes.Identity)
	 * @param serviceIdentifier the unique identifier for the shared service
	 * @throws ServiceSharingNotificationException
     */
	public void notifyServiceIsSharedInCIS(ServiceResourceIdentifier serviceIdentifier, String CISID) throws ServiceSharingNotificationException;
	
	/**
     * Description: Based on a CIS identifier this method is used to notify to the Service Registry that a Service is removed from a CIS
	 * @param CISID that represents the identifier for the CIS (this is the String returned by method getJid() of class org.societies.comm.xmpp.datatypes.Identity)
	 * @param serviceIdentifier the unique identifier for the service
	 * @throws ServiceSharingNotificationException
     */

	 
	public void removeServiceSharingInCIS(ServiceResourceIdentifier serviceIdentifier, String CISID) throws ServiceSharingNotificationException;
	
	
	/**
	* Description: Based on a Filter this method returns the services list
	* matching that particular filter. Null attributes are excluded and associations are ignored.
	* @param the object used as filter for the query
	* @return the list of services that match the filter
	* @throws ServiceRetrieveException
	*/
	public List<Service> findServices (Service filter) throws ServiceRetrieveException;
	
	/**
	* Description: Based on a service unique identifier this method returns the associated Service
	* @param serviceIdentifier the unique identifier for the Service
	* @return the corresponding Service
	* @throws ServiceRetrieveException
	*/
	public Service retrieveService(ServiceResourceIdentifier serviceIdentifier) throws ServiceRetrieveException;
	
	/**
	 * 
	 * @param serviceIdentifier the unique identifier for the Service
	 * @param serviceStatus the new status for the service
	 * @return true if all is performed correctly
	 * @throws ServiceNotFoundException
	 */
	public boolean changeStatusOfService(ServiceResourceIdentifier serviceIdentifier, ServiceStatus serviceStatus) throws ServiceNotFoundException;
	
	/**
	 * Description: Based on the CSSId this method delete all service that belong to the CSS and that are registered in the ServiceRegistry.
	 * @param CSSId 
	 * @return true if all is performed correctly
	 * @throws CSSNotFoundException
	 */
	public boolean deleteServiceCSS(String CSSId) throws CSSNotFoundException;
	
	/**
	 * Description: Based on the CISId this method clear the list of services shared inside a CIS.
	 * @param CISId
	 * @return true if all is performed correctly
	 * @throws CISNotFoundException
	 */
	
	public boolean clearServiceSharedCIS(String CISId) throws CISNotFoundException;
	
	
	/**
	 * Description: Update a service record inside the Registry with the new Service object. Attributes set to null are ignored.
	 * @param service
	 * @return
	 * @throws ServiceUpdateException
	 */
	public boolean updateRegisteredService(Service service) throws ServiceUpdateException;
	
	/**
	 * Description: Retrieve the list of CIS where the service is shared.
	 * @param serviceIdentifier
	 * @return the list of CIS where the service is shared.
	 */
	public List<String> retrieveCISSharedService(ServiceResourceIdentifier serviceIdentifier);

	/**
	* Description: Based on a Filter this method returns the services list
	* matching that particular filter. Null attributes are excluded and associations are ignored.
	* @param the object used as filter for the query
	* @param the ID of the CIS
	* @return the list of services that match the filter
	* @throws ServiceRetrieveException
	*/
	public List<Service> findServices(Service filter, String cisId)
			throws ServiceRetrieveException;

	/**
	 * @param CSSID
	 * @return
	 * @throws ServiceRetrieveException
	 */
	public List<Service> retrieveServicesInCSS(String CSSID)
			throws ServiceRetrieveException;

	/**
	 * @param CSSID
	 * @return
	 * @throws ServiceRetrieveException
	 */
	public List<Service> retrieveServicesInCSSNode(String CSSID)
			throws ServiceRetrieveException;
}