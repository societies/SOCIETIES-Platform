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
package org.societies.integration.test.ct.discoverservices;


import static org.junit.Assert.fail;
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
import org.societies.api.schema.servicelifecycle.model.ServiceType;


/**
 * Upper Tester for the Nominal Test Case of #728
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class NominalTestCaseUpperTester {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseUpperTester.class);
	
	/**
	 * The other node's JID
	 */
	private static final String REMOTEJID = "othercss.societies.local";
		
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	
	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 */
	@BeforeClass
	public static void initialization() {
		
		LOG.info("[#1882] Initialization");
		LOG.info("[#1882] Prerequisite: The CSS is created");
		LOG.info("[#1882] Prerequisite: The user is logged to the CSS");

	}
	
	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		if(LOG.isDebugEnabled()) LOG.debug("[#1882] NominalTestCaseUpperTester::setUp");
		
	}
	
	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		if(LOG.isDebugEnabled()) LOG.debug("[#1882] NominalTestCaseUpperTester:: teardown");
		
	}
	
	/**
	 * Do the test
	 */
	@Test
	public void testDiscoverServices(){
		
		LOG.info("[#1882] Testing Remote Discover Services");
			
		
		try{
			//STEP 1: Get the Services
			if(LOG.isDebugEnabled()) LOG.debug("[#1882] Getting remote Services from " + REMOTEJID);
			
			Future<List<Service>> asyncResult = TestCase728.getServiceDiscovery().getServices(REMOTEJID);
			List<Service> resultList = asyncResult.get();
			
			//STEP 2: Check if we retrieved two services
			if(LOG.isDebugEnabled()) LOG.debug("[#1882] Retrieved " + resultList.size() + " services.");
			Assert.assertTrue("[#1882]  Checking if we retrieved at least two services services!", resultList.size() >= 2);
			
			
			//STEP 3 & 4: Check if our current node isn't othercss.societies.local
			String localJid = TestCase728.getCommManager().getIdManager().getThisNetworkNode().getJid();
			if(LOG.isDebugEnabled()) LOG.debug("[#1882] Current JID is " + localJid);
			
			Assert.assertFalse("[#1882] Current JID is same as Remote Jid!", localJid.equals(REMOTEJID));
			int serviceCount = 0;
			//STEP 5, 6, 7 & 8
			for(Service service: resultList){
				String serviceJid = service.getServiceInstance().getFullJid();
				if(LOG.isDebugEnabled()) LOG.debug("[#1882] Service " + service.getServiceName() + " has jid " + serviceJid);
				if(!service.getServiceType().equals(ServiceType.DEVICE)){
					Assert.assertEquals("[#1882] Service JID is not the correct one!", REMOTEJID, serviceJid);
					Assert.assertTrue(service.getServiceName().equals("Calculator Service") || service.getServiceName().equals("FortuneCookie Service") || service.getServiceName().equals("RFiD System"));
					serviceCount++;
				}

			}
			
			
		} catch(Exception ex){
			LOG.error("Error while running test: " + ex);
			ex.printStackTrace();
			fail("Exception occured");
		}		
	}


}
