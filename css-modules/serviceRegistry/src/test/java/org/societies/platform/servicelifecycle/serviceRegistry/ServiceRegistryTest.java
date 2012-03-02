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
package org.societies.platform.servicelifecycle.serviceRegistry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceImplementation;
import org.societies.api.internal.servicelifecycle.model.ServiceInstance;
import org.societies.api.internal.servicelifecycle.model.ServiceLocation;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.model.ServiceStatus;
import org.societies.api.internal.servicelifecycle.model.ServiceType;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import static org.junit.Assert.*;

/**
 * 
 * 
 * @author solutanet
 * 
 */
@ContextConfiguration(locations = { "../../../../../META-INF/ServiceRegistryTest-context.xml" })
public class ServiceRegistryTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private ServiceRegistry serReg;
	private String serviceUri = "testURI";
	private List<Service> servicesList = generateServiceList(5);

	@Test
	@Rollback(false)
	public void testRegisterService() {

		try {
			serReg.registerServiceList(servicesList);

		} catch (ServiceRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@ExpectedException(ServiceRegistrationException.class)
	@Rollback(false)
	public void testDuplicateServiceRegistration()
			throws ServiceRegistrationException {

		serReg.registerServiceList(servicesList);

	}

	@Test
	@Rollback(false)
	public void retrieveService() {
		Service retrievedService = null;
		try {
			retrievedService = serReg
					.retrieveService(servicesList.get(0).getServiceIdentifier());
		} catch (ServiceRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		assertTrue(retrievedService.getServiceName().equals(
				servicesList.get(0).getServiceName()));
	}

	@Test
	@ExpectedException(ServiceRetrieveException.class)
	@Rollback(false)
	public void unregisterService() throws ServiceRetrieveException {
		try {
			serReg.unregisterServiceList(servicesList);

			serReg.retrieveService(new ServiceResourceIdentifier(new URI(
					serviceUri + "0"),"0"));

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ServiceRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Utilities methods */
	private List<Service> generateServiceList(int numberOfService) {
		List<Service> returnedServiceList = new ArrayList<Service>();
		for (int i = 0; i < numberOfService; i++) {
			try {
				returnedServiceList.add(new Service(
						
						"serviceEndPoint", "serviceName" + i,
						"serviceDescription" + i, "authorSignaturexx",
						ServiceType.CoreService, ServiceLocation.Local,
						new ServiceInstance("fullJid"+i, "XMPPNode"+i, new ServiceImplementation("net.calendar", "net.soluta", "1.0")),
						ServiceStatus.STARTED));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnedServiceList;
	}

}
