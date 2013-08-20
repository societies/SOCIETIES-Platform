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
package org.societies.integration.test.bit.installservicehwudisplay;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Stuart(HWU)
 *
 */
public class InstallServiceHWUDisplay {
	private static Logger LOG = LoggerFactory.getLogger(InstallServiceHWUDisplay.class);

	private String serviceName = "My test service";
	private myTestService service;
	private URL serviceURL;


	@Before
	public void setUp() {
		LOG.info("SET UP");

		// -- Verify dependency injection
		if (!TestCase.isDepencyInjectionDone()) {
			LOG.info("Dependency Injection Failed");
			fail("Dependency Injection "+TestCase.class.getSimpleName()+" not ready: ");

		}
		else
		{
			LOG.info("Dependency Injection Success");
			//SET UP SERVICE HERE?
			service = new myTestService();
			try {
				serviceURL = new URL("http://www.google.com");
			} catch (MalformedURLException e1) {
				fail("Bad URL");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TestCase.displayDriver.registerDisplayableService(service, serviceName, serviceURL, false);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOG.debug("Test Service Registered");
		}

	}

	@After
	public void tearDown() {
		//UNREGISTER SERVICE HERE?
	}


	@Test
	public void testDisplayService(){
		//ONCE SERVICE IS REGISTERED NEED TO SLEEP TO ALLOW TIME TO RUN SERVICE? 

		//TEST STUFF HERE SUCH AS....
		TestCase.displayDriver.sendNotification(serviceName, "My Message");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			TestCase.displayDriver.displayImage(serviceName, new String("C:/Users/sww2/test/image.png"));

		Assert.assertTrue(true);

	}


}
