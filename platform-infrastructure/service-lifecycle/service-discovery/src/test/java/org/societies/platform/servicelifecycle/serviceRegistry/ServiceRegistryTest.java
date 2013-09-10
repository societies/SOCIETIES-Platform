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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CISNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CSSNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;

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
	private final String _cisTestId="CISid";
	private final String _cisTestId1="CISid1";
	
	static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

	
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
	/*
	@Test
	@Rollback(false)
	public void retrieveServiceUsingTemplate() throws ServiceRetrieveException{
		Service tmpServiceFilter= ServiceModelUtils.generateEmptyFilter();
	    tmpServiceFilter.setServiceName("%");
		List<Service> returnedList=serReg.findServices(tmpServiceFilter);
		assertTrue(returnedList.size()==_numberOfServiceCreated);
		tmpServiceFilter=servicesList.get(0);
		returnedList=serReg.findServices(tmpServiceFilter);
		assertTrue(returnedList.get(0).getServiceName().equals(tmpServiceFilter.getServiceName()));
	}
	*/
	@Test
	@Rollback(false)
	public void testForBug1004() throws ServiceRetrieveException{
		Service tmpServiceFilter=new Service();
		ServiceResourceIdentifier sid = new ServiceResourceIdentifier();
		int i = 0;
		try {
			while (i < _numberOfServiceCreated) {
				sid.setIdentifier(new URI("societies", "the/path/of/the/service/v" + i, null));
				sid.setServiceInstanceIdentifier("instance_" + i);
				tmpServiceFilter.setServiceIdentifier(sid);
				List<Service> returnedList = serReg.findServices(tmpServiceFilter);
				assertTrue(returnedList.size() == 1);
				i++;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new ServiceRetrieveException(e);
		}
	}
	

	@Test
	@Rollback(false)
	public void changeServcieStatus() throws Exception{
		boolean isOk=serReg.changeStatusOfService(servicesList.get(0).getServiceIdentifier(), ServiceStatus.STOPPED);
		assertTrue(isOk);
		Service service=serReg.retrieveService(servicesList.get(0).getServiceIdentifier());
		assertTrue(service.getServiceStatus().equals(ServiceStatus.STOPPED));
		
	}
	
	
	
	
	@Test
	@Rollback(false)
	public void updateService() throws Exception{
		Service serviceUpdated=servicesList.get(3);
		serviceUpdated.setAuthorSignature(null);
		//serviceUpdated.setServiceIdentifier(null);
		serviceUpdated.setServiceName("Updated service");
		boolean isOk=serReg.updateRegisteredService(serviceUpdated);
		assertTrue(isOk);
		Service service=serReg.retrieveService(servicesList.get(3).getServiceIdentifier());
		
		assertTrue(service.getServiceName().equals("Updated service"));
		
		
	}
	
	
	@Test
	@Rollback(false)
	public void notifyServiceSharedCIS() throws Exception{
		for (Service service : servicesList) {
			serReg.notifyServiceIsSharedInCIS(service.getServiceIdentifier(), _cisTestId);
			serReg.notifyServiceIsSharedInCIS(service.getServiceIdentifier(), _cisTestId1);
		}
	}
	
	@Test
	@Rollback(false)
	public void testRetrieveCISSharedService() throws Exception{
		Service s = servicesList.get(0);
		List<String> cisIdList = serReg.retrieveCISSharedService(s.getServiceIdentifier());
		assertTrue(cisIdList.size()==2);
		Set<String> expectedResults = new HashSet<String>();
		expectedResults.add(_cisTestId);expectedResults.add(_cisTestId1);
		assertTrue(cisIdList.containsAll(expectedResults));
		s.getServiceIdentifier().setServiceInstanceIdentifier("not_Existing");
		cisIdList = serReg.retrieveCISSharedService(s.getServiceIdentifier());
		assertTrue(cisIdList.isEmpty());
	}
	
	@Test
	@Rollback(false)
	public void removeNotifyServiceSharedCIS() throws Exception{
		
			for (Service service : servicesList) {
				serReg.removeServiceSharingInCIS(service.getServiceIdentifier(), _cisTestId1);
				
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
		List<Service> returnedServiceList=serReg.retrieveServicesInCSSNode((servicesList.get(0).getServiceInstance().getFullJid()));
		assertTrue(returnedServiceList.get(0).getServiceName().equals(servicesList.get(0).getServiceName()));
	}
	
	
	
	
	@Test
	@Rollback(false)
	public void removeServiceCSS() throws CSSNotFoundException{
		serReg.deleteServiceCSS(servicesList.get(9).getServiceInstance().getFullJid());
	}
	
	@Test
	@Rollback(false)
	public void clearServiceCIS() throws CISNotFoundException{
		serReg.clearServiceSharedCIS(_cisTestId);
	}
	
	@Test
	@ExpectedException(NullPointerException.class)
	@Rollback(false)
	public void unregisterService() throws ServiceRetrieveException {
		try {
			servicesList.remove(9);
			serReg.unregisterServiceList(servicesList);
			ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
			sri.setIdentifier(new URI("societies","the/path/of/the/service/v9",null));
			sri.setServiceInstanceIdentifier("instance_9");
			Service retrievedService=serReg.retrieveService(sri);
			retrievedService.getServiceName();
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
				result.setPrivacyPolicy("pathtoprivacypolicy");
				result.setSecurityPolicy("securityPolicystuff");
				result.setServiceCategory("testservice");
				result.setServiceType(ServiceType.THIRD_PARTY_SERVER);
				result.setServiceLocation("Local");
				result.setContextSource("true");
				result.setServiceStatus(ServiceStatus.STARTED);
				si = new ServiceInstance();
				si.setFullJid("fullJid"+i);
				si.setCssJid("cssJid"+i);
				si.setXMPPNode("XMPPNode"+i);
				si.setParentIdentifier(sid);
				servImpl = new ServiceImplementation();
				servImpl.setServiceNameSpace("net.calendar");
				servImpl.setServiceProvider("net.soluta");
				servImpl.setServiceVersion("1.0");
				servImpl.setServiceClient("the/path/of/the/service/client"+i);
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
