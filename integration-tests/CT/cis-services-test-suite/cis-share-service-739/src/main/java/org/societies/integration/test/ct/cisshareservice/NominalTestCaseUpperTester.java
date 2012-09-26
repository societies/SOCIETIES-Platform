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
package org.societies.integration.test.ct.cisshareservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;

/**
 * Upper Tester for the Nominal Test Case of #739
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class NominalTestCaseUpperTester {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseUpperTester.class);
	
	/**
	 * The service under test
	 */
	private static ServiceResourceIdentifier testServiceId;
	
	/**
	 * URL of the JAR of the 3P service Bundle
	 */
	private static URL serviceBundleUrl;
	
	/**
	 * Test case number
	 */
	public static int testCaseNumber;

	/**
	 * Relative path to the jar file in resources folder
	 */
	private static final String SERVICE_PATH = "IntegrationTestService-0.1.jar";
	
	/**
	 * Our CIS
	 */
	private static ICisOwned myCis;
	
	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 */
	@BeforeClass
	public static void initialization() {
		
		LOG.info("[#739] Initialization");
		LOG.info("[#739] Prerequisite: The CSS is created");
		LOG.info("[#739] Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = NominalTestCaseUpperTester.class.getClassLoader().getResource(SERVICE_PATH);
		
		assertNotNull("[#739] ServiceBundle is null",serviceBundleUrl);
		assertNotNull("[#739] getServiceControl is null",TestCase739.getServiceControl());
		assertNotNull("[#739] getCommManager is null",TestCase739.getCommManager());
		assertNotNull("[#739] getCisManager is null",TestCase739.getCisManager());
		assertNotNull("[#739] getServiceRegistry is null",TestCase739.getServiceRegistry());
	}

	
	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		if(LOG.isDebugEnabled()) LOG.debug("[#739] NominalTestCaseUpperTester::setUp");
		
		try{
			Future<ServiceControlResult> installResultFuture = TestCase739.getServiceControl().installService(serviceBundleUrl);
			ServiceControlResult installResult = installResultFuture.get();
			if (!installResult.getMessage().equals(ResultMessage.SUCCESS)) {
				LOG.error("[#739] Preamble Install Service: Couldn't install");
				fail("[#739] Preamble Install Service: Couldn't install");
				return;
			}
			
			testServiceId =installResult.getServiceId();
			
			if(LOG.isDebugEnabled()){
				LOG.debug("[#739] Our CSS Id is " + TestCase739.getCommManager().getIdManager().getThisNetworkNode().getJid());
			}
			
			int mode = 1;
			String cisType = "Test";
			String cisName ="TestCIS";
			String cssPassword = "testPassword";
			String cssId = TestCase739.getCommManager().getIdManager().getThisNetworkNode().getJid();
			Future<ICisOwned> asyncCis = TestCase739.getCisManager().createCis(cisName, cisType, null, cssPassword);
			myCis = asyncCis.get();
			
			assertNotNull("[#739] CIS is null! Failed creating!",myCis);
			
			if(LOG.isDebugEnabled() && myCis != null) {
				LOG.debug("[#739] myCis.getCisId(): " + myCis.getCisId());
				LOG.debug("[#739] myCis.getCisType(): " + myCis.getCisType());
				LOG.debug("[#739] myCis.getOwnerId(): " + myCis.getOwnerId());
				LOG.debug("[#739] myCis.getName(): " + myCis.getName());
				LOG.debug("[#739] myCis.getDescription(): " + myCis.getDescription());
			} 
			
		} catch(Exception ex){
			ex.printStackTrace();
			LOG.error("[#739] Preamble Service: Exception occured: " + ex);
			fail("[#739] Preamble Service: Exception occured: " + ex);
			return;
		}
	}
	
	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		if(LOG.isDebugEnabled()) LOG.debug("[#739] NominalTestCaseUpperTester:: teardown");

		try{
			Future<ServiceControlResult> uninstallResultFuture =TestCase739.getServiceControl().uninstallService(testServiceId);
			ServiceControlResult uninstallResult = uninstallResultFuture.get();
			if (!uninstallResult.getMessage().equals(ResultMessage.SUCCESS)) {
				LOG.error("[#739] Teardown Uninstall Service: Couldn't uninstall");
				fail("[#739] Preamble Uninstall Service: Couldn't uninstall");
				return;
			}
			
			boolean result = TestCase739.getCisManager().deleteCis(myCis.getCisId());
		
			if(!result){
				LOG.error("[#739] Teardown delete CIS: Couldn't delete!");
				fail("[#739] Teardown delete CIS: Couldn't delete!");
			}
			
		} catch(Exception ex){
			LOG.error("[#739] Preamble Uninstall Service: Exception occured: " + ex);
			fail("[#739] Preamble Uninstall Service: Exception occured: " + ex);
			return;
		}
		
	}
	
	/**
	 * Do the test
	 */
	@Test
	public void testShareService(){
		
		LOG.info("[#739] Testing Local CIS Share Service!");
		
		//STEP 1: Get the Service
		Service serviceUnderTest = null;
			
		try {
			if(LOG.isDebugEnabled())
				LOG.debug("[#739] Selecting service to Share!");
				
			serviceUnderTest = TestCase739.getServiceRegistry().retrieveService(testServiceId);
		
		} catch (ServiceRetrieveException e) {
			LOG.error("[#739] Couldn't retrieve the service: exception: " + e);
			fail("[#739] Couldn't retrieve the service: exception: " + e);
			e.printStackTrace();
		}
		
		if(serviceUnderTest == null){
			if(LOG.isDebugEnabled()) LOG.debug("[#739] Couldn't get the service!");
			fail("[#739] Couldn't retrieve the service!");
			return;
		}
		
		if(LOG.isDebugEnabled())
			LOG.debug("[#739] Service selected: " + serviceUnderTest.getServiceEndpoint());
		
		
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("[#739] Share service with CIS!");
			
			TestCase739.getServiceRegistry().notifyServiceIsSharedInCIS(testServiceId, myCis.getCisId());
			
			if(LOG.isDebugEnabled()) LOG.debug("[#739] Now checking if service is shared on that CIS!");
			
			List<Service> serviceList = TestCase739.getServiceRegistry().retrieveServicesSharedByCIS(myCis.getCisId());
			
			Assert.assertNotNull(serviceList);
			Assert.assertEquals(1, serviceList.size());
			
			Service myService= serviceList.get(0);
			
			if(LOG.isDebugEnabled()) 
				LOG.debug("[#739] Service shared is: " + myService.getServiceName());
			
			Assert.assertEquals(myService.getServiceIdentifier().getServiceInstanceIdentifier(),testServiceId.getServiceInstanceIdentifier());
			Assert.assertEquals(myService.getServiceIdentifier().getIdentifier(),testServiceId.getIdentifier());
			
			// Next we remove the service
			if(LOG.isDebugEnabled()) 
				LOG.debug("[#739] Attempting to remove the service from sharing!");
			
			TestCase739.getServiceRegistry().removeServiceSharingInCIS(testServiceId, myCis.getCisId());

			if(LOG.isDebugEnabled()) LOG.debug("[#739] Now checking if service no longer shared on that CIS!");
			
			serviceList = TestCase739.getServiceRegistry().retrieveServicesSharedByCIS(myCis.getCisId());
			
			Assert.assertNotNull(serviceList);
			Assert.assertTrue(serviceList.isEmpty());
			
			if(LOG.isDebugEnabled()) LOG.debug("[#739] CIS now has " + serviceList.size() + " services shared!");

		
		} catch(Exception ex){
			LOG.error("Error while running test: " + ex);
			ex.printStackTrace();
			fail("Exception occured");
		}		
	}

}
