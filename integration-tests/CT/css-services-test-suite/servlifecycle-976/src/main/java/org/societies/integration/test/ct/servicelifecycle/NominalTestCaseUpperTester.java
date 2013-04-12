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
package org.societies.integration.test.ct.servicelifecycle;

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
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;

/**
 * Upper Tester for the Nominal Test Case of #976
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
	 * The other node's JID
	 */
	private static final String REMOTEJID = "othercss.societies.local";
	
	
	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 */
	@BeforeClass
	public static void initialization() {
		
		LOG.info("[#1883] Initialization");
		LOG.info("[#1883] Prerequisite: The CSS is created");
		LOG.info("[#1883] Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = NominalTestCaseUpperTester.class.getClassLoader().getResource(SERVICE_PATH);
		
		assertNotNull(serviceBundleUrl);
	
	}
	
	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		if(LOG.isDebugEnabled()) LOG.debug("[#1883] NominalTestCaseUpperTester::setUp");
		
	}
	
	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		if(LOG.isDebugEnabled()) LOG.debug("[#1883] NominalTestCaseUpperTester:: teardown");
		
	}
	
	/**
	 * Do the test
	 */
	@Test
	public void testServiceLifecycle(){
		
		// Check if we're going to use another JID
		String ourJid = TestCase976.getCommManager().getIdManager().getThisNetworkNode().getJid();
		if(LOG.isDebugEnabled()) LOG.debug("[#1883] Remote JID is " + REMOTEJID + " and local JID is " + ourJid);
		
		if(ourJid.equals(REMOTEJID)){
			fail("Our JID is the Remote JID!");
			return;
		}
		
		// Now we can start!
		LOG.info("[#1883] Testing remote service lifecycle");
		
		try{
		
			testInstallService();
			testStopService();
			testStartService();
			testUninstallService();
			
		} catch(Exception ex){
			LOG.error("Error while running test: " + ex);
			ex.printStackTrace();
			fail("Exception occured");
		}		
	}

	
	/**
	 * Test installing a service
	 */
	private void testInstallService(){
		
		LOG.info("[#1883] Testing installing a service on a remote node!");
		
		try{
			//STEP 1: Get the Services
			if(LOG.isDebugEnabled()) LOG.debug("[#1883] Getting remote Services from " + REMOTEJID);
			
			Future<List<Service>> asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			List<Service> resultList = asyncResult.get();
			int serviceNumber = resultList.size();
			
			// STEP 2: Install the service
			if(LOG.isDebugEnabled()) LOG.debug("[#1883] Installing the service!");
			
			Future<ServiceControlResult> asyncInstallResult = TestCase976.getServiceControl().installService(serviceBundleUrl, REMOTEJID);
			ServiceControlResult installResult = asyncInstallResult.get();
			
			// STEP 3: Is the result success?
			Assert.assertEquals("[#1883] Install not successfull", ResultMessage.SUCCESS, installResult.getMessage());
			
			if(installResult.getMessage() != ResultMessage.SUCCESS){
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was not installed successfully");
				return;
			} else{
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service installed successfully: " + installResult.getServiceId().getServiceInstanceIdentifier());
				testServiceId = installResult.getServiceId();
			}
			
			// STEP 4 & 5: Get the list of services now, should have 1 more
			asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			resultList = asyncResult.get();
			
			Assert.assertEquals("[#1883] Number of services not increased one! ",serviceNumber+1, resultList.size() );
			
			// STEP 6: Check if service was returned and is installed;
			Service installedService = getServiceFromList(resultList);
			
			assertNotNull(installedService);
			if(installedService != null){
				Assert.assertEquals("[#1883] Service is not installed and started!",installedService.getServiceStatus(),ServiceStatus.STARTED);
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service found and in status: " + installedService.getServiceStatus().toString());
			}
			
		} catch(Exception ex){
			LOG.error("[#1883] Exception occurred: " + ex);
			ex.printStackTrace();
			fail("[#1883] Exception occurred: " + ex);
		}
		
	}
	
	/**
	 * Test stopping a service
	 */
	private void testStopService(){
		
		LOG.info("[#1883] Testing stopping service");
		
		try{
			
			// STEP 7: STOP the service
			
			Future<ServiceControlResult> asyncStopResult = TestCase976.getServiceControl().stopService(testServiceId); 
			ServiceControlResult stopResult = asyncStopResult.get();
			
			// STEP 8: Is the result success?
			Assert.assertEquals("[#1883] Stop not successfull", ResultMessage.SUCCESS, stopResult.getMessage());
			
			if(stopResult.getMessage() != ResultMessage.SUCCESS){
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was not stopped successfully");
				return;
			} else{
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was stopped successfully");
			}
			
			// Wait for OSGI to update
			Thread.sleep(1000);
			
			// STEP 9: Get the list of services now
			Future<List<Service>> asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			List<Service> resultList = asyncResult.get();
			
			// STEP 10: Check if service was returned and is installed;
			Service installedService = getServiceFromList(resultList);
			
			assertNotNull(installedService);
			if(installedService != null){
				Assert.assertEquals("[#1883] Service is not stopped!",installedService.getServiceStatus(),ServiceStatus.STOPPED);
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service found and in status: " + installedService.getServiceStatus().toString());
			}
			
		} catch(Exception ex){
			LOG.error("[#1883] Exception occurred: " + ex);
			fail("[#1883] Exception occurred: " + ex);
		}
		
	}

	/**
	 * Test starting a service
	 */
	private void testStartService(){
		
		LOG.info("[#1883] Testing start service");
		
		try{
			
			// STEP 11: START the service
			
			Future<ServiceControlResult> asyncStartResult = TestCase976.getServiceControl().startService(testServiceId); 
			ServiceControlResult startResult = asyncStartResult.get();
			
			// STEP 12: Is the result success?
			Assert.assertEquals("[#1883] Start not successfull", ResultMessage.SUCCESS, startResult.getMessage());
			
			if(startResult.getMessage() != ResultMessage.SUCCESS){
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was not started successfully");
				return;
			} else{
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was started successfully");
			}
			
			// Wait for OSGI to update
			Thread.sleep(1000);
			
			// STEP 13: Get the list of services now
			Future<List<Service>> asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			List<Service> resultList = asyncResult.get();
			
			// STEP 14: Check if service was returned and is installed;
			Service installedService = getServiceFromList(resultList);
			
			assertNotNull(installedService);
			if(installedService != null){
				Assert.assertEquals("[#1883] Service is not started!",installedService.getServiceStatus(),ServiceStatus.STARTED);
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service found and in status: " + installedService.getServiceStatus().toString());
			}
			
		} catch(Exception ex){
			LOG.error("[#1883] Exception occurred: " + ex);
			fail("[#1883] Exception occurred: " + ex);
		}
		
	}
	
	/**
	 * Test uninstalling a service
	 */
	private void testUninstallService(){
		
		LOG.info("[#1883] Testing uninstalling a service on a remote node!");
		
		try{
			//STEP 15.1: Get the Services
			if(LOG.isDebugEnabled()) LOG.debug("[#1883] Getting remote Services from " + REMOTEJID);
			
			Future<List<Service>> asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			List<Service> resultList = asyncResult.get();
			int serviceNumber = resultList.size();
			
			// STEP 15: Uninstall the service
			if(LOG.isDebugEnabled()) LOG.debug("[#1883] Uninstalling the service!");
			
			Future<ServiceControlResult> asyncUninstallResult = TestCase976.getServiceControl().uninstallService(testServiceId);
			ServiceControlResult uninstallResult = asyncUninstallResult.get();
			
			// STEP 16: Is the result success?
			Assert.assertEquals("[#1883] Uninstall not successfull", ResultMessage.SUCCESS, uninstallResult.getMessage());
			
			if(uninstallResult.getMessage() != ResultMessage.SUCCESS){
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service was not uninstalled successfully");
				return;
			} else{
				if(LOG.isDebugEnabled()) LOG.debug("[#1883] Service uninstalled successfully: " + uninstallResult.getServiceId().getServiceInstanceIdentifier());
			}
			
			// Wait for OSGI to update
			Thread.sleep(1000);
			
			// STEP 17 & 18: Get the list of services now, should have 1 less
			asyncResult = TestCase976.getServiceDiscovery().getServices(REMOTEJID);
			resultList = asyncResult.get();
			
			Assert.assertEquals("[#1883] Number of services not diminished one! ",serviceNumber-1, resultList.size() );
			
			// STEP 19: Check if service was returned and is installed;
			
			Assert.assertNull("[#1883] Service still there!", getServiceFromList(resultList));

		} catch(Exception ex){
			LOG.error("[#1883] Exception occurred: " + ex);
			fail("[#1883] Exception occurred: " + ex);
		}
		
	}
	
	private Service getServiceFromList(List<Service> resultList) {
		if(LOG.isDebugEnabled()) LOG.debug("[#1883] Checking for Service...");
		Service result = null;
		
		for(Service service : resultList){
			ServiceResourceIdentifier servId = service.getServiceIdentifier();
			if(servId.getServiceInstanceIdentifier().equals(testServiceId.getServiceInstanceIdentifier()) && servId.getIdentifier().equals(testServiceId.getIdentifier())){
				result = service;
			}
		}
		
		return result;
	}

}
