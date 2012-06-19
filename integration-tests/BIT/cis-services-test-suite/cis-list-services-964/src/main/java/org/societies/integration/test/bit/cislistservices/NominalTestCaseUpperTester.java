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
package org.societies.integration.test.bit.cislistservices;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;

/**
 * Upper Tester for the Nominal Test Case of #714
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
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 */
	@BeforeClass
	public static void initialization() {
		
		LOG.info("[#714] Initialization");
		LOG.info("[#714] Prerequisite: The CSS is created");
		LOG.info("[#714] Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = NominalTestCaseUpperTester.class.getClassLoader().getResource(SERVICE_PATH);
		
		assertNotNull(serviceBundleUrl);
		assertNotNull(TestCase964.getServiceControl());
	}
	
	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		if(LOG.isDebugEnabled()) LOG.debug("[#714] NominalTestCaseUpperTester::setUp");
		
		try{
			Future<ServiceControlResult> installResultFuture = TestCase964.getServiceControl().installService(serviceBundleUrl);
			ServiceControlResult installResult = installResultFuture.get();
			if (!installResult.getMessage().equals(ResultMessage.SUCCESS)) {
				LOG.error("[#714] Preamble Install Service: Couldn't install");
				fail("[#714] Preamble Install Service: Couldn't install");
				return;
			}
			
			testServiceId =installResult.getServiceId();
			
		} catch(Exception ex){
			LOG.error("[#714] Preamble Install Service: Exception occured: " + ex);
			fail("[#714] Preamble Install Service: Exception occured: " + ex);
			return;
		}
	}
	
	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		if(LOG.isDebugEnabled()) LOG.debug("[#714] NominalTestCaseUpperTester:: teardown");

		try{
			Future<ServiceControlResult> uninstallResultFuture =TestCase964.getServiceControl().uninstallService(testServiceId);
			ServiceControlResult uninstallResult = uninstallResultFuture.get();
			if (!uninstallResult.getMessage().equals(ResultMessage.SUCCESS)) {
				LOG.error("[#714] Teardown UnInstall Service: Couldn't uninstall");
				fail("[#714] Preamble UnInstall Service: Couldn't uninstall");
				return;
			}
			
		} catch(Exception ex){
			LOG.error("[#714] Preamble Uninstall Service: Exception occured: " + ex);
			fail("[#714] Preamble Uninstall Service: Exception occured: " + ex);
			return;
		}
		
	}
	
	/**
	 * Do the test
	 */
	@Test
	public void testStartStop(){
		
		LOG.info("[#714] Testing Start-Stop!");
		
		//STEP 1: Get the Service
		Service serviceUnderTest = null;
			
		try {
			if(LOG.isDebugEnabled())
				LOG.debug("[#714] Selecting service to Start & Stop!");
				
			serviceUnderTest = TestCase964.getServiceRegistry().retrieveService(testServiceId);
		
		} catch (ServiceRetrieveException e) {
			LOG.error("[#714] Couldn't retrieve the service: exception: " + e);
			fail("[#714] Couldn't retrieve the service: exception: " + e);
			e.printStackTrace();
		}
		
		if(serviceUnderTest == null){
			if(LOG.isDebugEnabled()) LOG.debug("[#714] Couldn't get the service!");
			fail("[#714] Couldn't retrieve the service!");
			return;
		}
		
		if(LOG.isDebugEnabled())
			LOG.debug("[#714] Service selected: " + serviceUnderTest.getServiceEndpoint());
		
		try {
			
			if(serviceUnderTest.getServiceStatus().equals(ServiceStatus.STARTED)){
				
				stopService(serviceUnderTest);
				Thread.sleep(1000);
				checkIfStopped(serviceUnderTest);
				startService(serviceUnderTest);
				Thread.sleep(1000);
				checkIfStarted(serviceUnderTest);
				
			} else 
				if(serviceUnderTest.getServiceStatus().equals(ServiceStatus.STOPPED)){
					
					startService(serviceUnderTest);
					Thread.sleep(1000);
					//Need to sleep to wait for container
					checkIfStarted(serviceUnderTest);
					stopService(serviceUnderTest);
					Thread.sleep(1000);
					checkIfStopped(serviceUnderTest);
					
				} else{
					fail("Unrecognized state");
				}
			
		
		} catch(Exception ex){
			LOG.error("Error while running test: " + ex);
			ex.printStackTrace();
			fail("Exception occured");
		}		
	}

	private void stopService(Service serviceUnderTest) {
		try{
			
			if(LOG.isDebugEnabled()) LOG.debug("[#714] Service " + serviceUnderTest.getServiceName() + " is started, we shall stop it");
			
			Future<ServiceControlResult> stopResultFuture = TestCase964.getServiceControl().stopService(serviceUnderTest.getServiceIdentifier());
			ServiceControlResult stopResult = stopResultFuture.get();
			
			Assert.assertEquals("[#714] Service was not stopped correctly!", ResultMessage.SUCCESS, stopResult.getMessage());
			
		} catch(Exception ex){
			LOG.error("[#714] Error while running test: " + ex);
			ex.printStackTrace();
			fail("[#714] Exception occured");
		}
	}
	
	private void checkIfStopped(Service serviceUnderTest) {
		if(LOG.isDebugEnabled()) LOG.debug("[#714] checkIfStopped");
		
		try{
			
			Service testService = TestCase964.getServiceRegistry().retrieveService(serviceUnderTest.getServiceIdentifier());

			if(testService == null){
				fail("[#714] Couldn't find the service");
				return;
			}
			
			LOG.info("[#714] Service " + testService.getServiceName() + " is " + testService.getServiceStatus());

			Assert.assertEquals("[#714] Service is not in the correct state!", ServiceStatus.STOPPED, testService.getServiceStatus());
			
		} catch(Exception ex){
			LOG.error("[#714] Error while running test: " + ex);
			ex.printStackTrace();
			fail("[#714] Exception occured");
		}

	}
	
	private void startService(Service serviceUnderTest) {
		if(LOG.isDebugEnabled()) LOG.debug("[#714] startService");
		
		try{
			
			LOG.info("[#714] Service " + serviceUnderTest.getServiceName() + " is stopped, we shall start it");
			
			Future<ServiceControlResult> startResultFuture = TestCase964.getServiceControl().startService(serviceUnderTest.getServiceIdentifier());
			ServiceControlResult startResult = startResultFuture.get();
			
			Assert.assertEquals("[#714] Service was not started correctly", ResultMessage.SUCCESS, startResult.getMessage());
			
			
		} catch(Exception ex){
			LOG.error("[#714] Error while running test: " + ex);
			ex.printStackTrace();
			fail("[#714] Exception occured");
		}

	}
	
	
	private void checkIfStarted(Service serviceUnderTest) {
		if(LOG.isDebugEnabled()) LOG.debug("[#714] checkIfStarted");
			
		try{
			
			Service testService = TestCase964.getServiceRegistry().retrieveService(serviceUnderTest.getServiceIdentifier());

			if(testService == null){
				fail("[#714] Couldn't find the service");
				return;
			}

			LOG.info("[#714] Service " + testService.getServiceName() + " is " + testService.getServiceStatus());

			Assert.assertEquals("[#714] Service is not in the correct state!", ServiceStatus.STARTED, testService.getServiceStatus());
			
		} catch(Exception ex){
			LOG.error("[#714] Error while running test: " + ex);
			ex.printStackTrace();
			fail("[#714] Exception occured");
		}
	}
}
