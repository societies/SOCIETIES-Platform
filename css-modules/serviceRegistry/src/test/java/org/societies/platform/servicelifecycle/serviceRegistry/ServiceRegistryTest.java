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

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceLocation;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

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
	private static final int _numberOfServiceCreated=10; 
	private List<Service> servicesList = generateServiceList(_numberOfServiceCreated);

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
	@Rollback(false)
	public void retrieveServiceUsingTemplate() throws ServiceRetrieveException{
		Service tmpServiceFilter=new Service();
	    tmpServiceFilter.setServiceName("%");
		List<Service> returnedList=serReg.findServices(tmpServiceFilter);
		assert(returnedList.size()==_numberOfServiceCreated);
		tmpServiceFilter=servicesList.get(0);
		returnedList=serReg.findServices(tmpServiceFilter);
		assert(returnedList.get(0).getServiceName().equals(tmpServiceFilter.getServiceName()));
	}
	

	@Test
	@Rollback(false)
	public void changeServcieStatus() throws Exception{
		boolean isOk=serReg.changeStatusOfService(servicesList.get(0).getServiceIdentifier(), ServiceStatus.STOPPED);
		assert(isOk);
		Service service=serReg.retrieveService(servicesList.get(0).getServiceIdentifier());
		assert(service.getServiceStatus().equals(ServiceStatus.STOPPED));
		
	}
	
	
	@Test
	@Rollback(false)
	public void notifyServiceSharedCIS() throws Exception{
		for (Service service : servicesList) {
			serReg.notifyServiceIsSharedInCIS(service.getServiceIdentifier(), "CISid");
			serReg.notifyServiceIsSharedInCIS(service.getServiceIdentifier(), "CISid1");
		}
	}
	
	@Test
	@Rollback(false)
	public void removeNotifyServiceSharedCIS() throws Exception{
		
			for (Service service : servicesList) {
				serReg.removeServiceSharingInCIS(service.getServiceIdentifier(), "CISid1");
				
			}
	}
	
	@Test
	@Rollback(false)
	public void retrieveServicesSharedCIS() throws Exception{
		List<Service> returnedList=serReg.retrieveServicesSharedByCIS("CISid");
	}
	
	
	@Test
	@Rollback(false)
	public void retrieveServicesSharedCSS() throws Exception{
		List<Service> returnedServiceList=serReg.retrieveServicesSharedByCSS(servicesList.get(0).getServiceInstance().getFullJid());
	assert(returnedServiceList.get(0).getServiceName().equals(servicesList.get(0).getServiceName()));
	}
	
	
	@Test
	@ExpectedException(ServiceRetrieveException.class)
	@Rollback(false)
	public void unregisterService() throws ServiceRetrieveException {
		try {
			serReg.unregisterServiceList(servicesList.subList(0, 4));
			ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
			sri.setIdentifier(new URI(serviceUri + "0"));
			sri.setServiceInstanceIdentifier("0");
			serReg.retrieveService(sri);
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
		Service result = null;
		ServiceInstance si = null;
		ServiceImplementation servImpl = null;
		for (int i = 0; i < numberOfService; i++) {
			try {
				result = new Service();
				ServiceResourceIdentifier sid = new ServiceResourceIdentifier();
				sid.setIdentifier(new URI("societies","the/path/of/the/service/v"+i,null));
				sid.setServiceInstanceIdentifier("instance_"+i);
				result.setServiceIdentifier(sid);
				result.setAuthorSignature("authorSignaturexx");
				result.setServiceDescription("serviceDescription" + i);
				result.setServiceEndpoint("serviceEndPoint");
				result.setServiceName("serviceName" + i);
				result.setServiceType(ServiceType.CORE_SERVICE);
				result.setServiceLocation(ServiceLocation.LOCAL);
				result.setServiceStatus(ServiceStatus.STARTED);
				si = new ServiceInstance();
				si.setFullJid("fullJid"+i);
				si.setXMPPNode("XMPPNode"+i);
				servImpl = new ServiceImplementation();
				servImpl.setServiceNameSpace("net.calendar");
				servImpl.setServiceProvider("net.soluta");
				servImpl.setServiceVersion("1.0");
				si.setServiceImpl(servImpl);
				result.setServiceInstance(si);
				returnedServiceList.add(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnedServiceList;
	}

}
