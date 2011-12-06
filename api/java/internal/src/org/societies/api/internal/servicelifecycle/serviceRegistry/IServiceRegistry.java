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

import org.societies.api.internal.servicelifecycle.serviceRegistry.model.QuerySubjectType;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntryOut;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.ServiceResourceIdentifier;
import org.societies.comm.identity.Identity;

import com.sun.servicetag.Registry;
/**
 * @author Antonio Panazzolo, Massimo Mazzariol (SN)
 */
public interface IServiceRegistry {

	/**
	 * Description: This method provides the interface to add a new list of services.
	 * 				List services can be composed at least by only service at time
	 * @param servicesList
	 * @return the list of services registered correctly
	 */
	public List<ServiceResourceIdentifier> registerServiceList (List<RegistryEntry> servicesList);

	
	/**
	 * Description: This method permits you to unregister a services list
	 * @param servicesList
	 * @return the list of services removed correctly
	 */
	public List<ServiceResourceIdentifier> unregisterServiceList (List<ServiceResourceIdentifier> servicesList);


	/**
	 * Description: This method syncs a remote registry with societies
	 * @param CSSID
	 * @return true if the remote registry was successfully synced
	 */
	public boolean syncRemoteCSSRegistry (Identity CSSID);


	/**
	 * Description: Based on a CSS identifier this method returns all services shared by CSS to other CSS or CIS 
	 * @param CSS that represents the identifier for CSS, 
	 * 		  type declare if the return list contains services shared to CIS or to CSS
	 * @return a List of services retrieved
	 */
	public List<RegistryEntryOut> retrieveServicesSharedByCSS (Identity CSS, QuerySubjectType type);
	
	/**
	 * Description: Based on a CSS identifier this method returns all services that are available within the specified CSS 
	 * @param CSS that represents the identifier for CSS
	 * @return a List of services retrieved
	 */
	public List<RegistryEntryOut> retrieveServicesInCSS (Identity CSS);
	
	/**
	 * Description: Based on a CSS identifier this method returns all services that are available within the specified CIS 
	 * @param CIS that represents the identifier for CIS
	 * @return a List of services retrieved
	 */
	public List<RegistryEntryOut> retrieveServicesInCIS (Identity CIS);
	
	/**
	 * Description: Based on a CSS identifier this method returns all services shared to CSS by other CSS or CIS 
	 * @param CSS that represents the identifier for CSS, 
	 * 		  type declare if the return list contains services shared by CIS or by CSS
	 * @return a List of services retrieved
	 */
	public List<RegistryEntryOut> retrieveServicesSharedToCSS (Identity CSS, QuerySubjectType type);
	
	
	
}