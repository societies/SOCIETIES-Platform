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
package org.societies.platform.servicelifecycle.servicediscovery;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.platform.servicelifecycle.servicediscovery.ServiceDiscovery;

/**
 * Describe your class here...
 *
 * @author mmanniox
 *
 */
public class ServiceDiscoveryUnitTest {
	static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryUnitTest.class);


	private ServiceDiscovery classUnderTest;


	private IServiceRegistry mockedServiceReg ; 
	private ICommManager mockedCommManager;
	private IIdentityManager mockedIdentityManager;

	private IIdentity mockedRemote; 
	private INetworkNode mockedHost;
	private String hostJid;
	private String remoteJid;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		//create the mocked classes
		mockedServiceReg = mock(IServiceRegistry.class); 
		mockedCommManager = mock(ICommManager.class);
		mockedIdentityManager = mock(IIdentityManager.class);
		mockedRemote = mock(IIdentity.class);
		mockedHost = mock(INetworkNode.class);
		
		hostJid = new String("testnode.societies.local");
		remoteJid = new String("othernode.societies.local");

		//create an instance of your tested class
		classUnderTest = new ServiceDiscovery();
		//Initialize the set method (normally called by spring */
		classUnderTest.setServiceReg(mockedServiceReg) ;
		classUnderTest.setCommMngr(mockedCommManager) ;


	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		mockedServiceReg = null;
		mockedCommManager = null;

		mockedHost = null; 
		mockedRemote= null; 

	}

	/**
	 * Test method for {@link org.societies.platform.servicelifecycle.servicediscovery.ServiceDiscovery#getServices()}.
	 */
	@Test
	public void testGetServicesLocalNoneAvailable() {
		//List<Service>  retList = new ArrayList<Service>();

		Future<List<Service>> asyncResult = null;

		try
		{
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveServicesInCSSNode(mockedHost.getJid())).toReturn(null);
			stub(mockedHost.getType()).toReturn(IdentityType.CSS);
			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			

			asyncResult = classUnderTest.getLocalServices();

			assertNull(asyncResult.get());


		} catch (Exception e)
		{
			logger.error("Shouldn't be here! "+e.getMessage(), e);
			fail("Shouldn't be here! "+e.getMessage());
		};	

	}

	/**
	 * Test method for {@link org.societies.platform.servicelifecycle.servicediscovery.ServiceDiscovery#getServices()}.
	 */
	@Test
	public void testGetServicesLocalOneAvailable() {
		//List<Service>  retList = new ArrayList<Service>();

		Future<List<Service>> asyncResult = null;
		List<Service> testLocalServiceList = new ArrayList<Service>();
		Service testService = new Service();
		testLocalServiceList.add(testService);
		try
		{
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveServicesInCSSNode(mockedHost.getJid())).toReturn(testLocalServiceList);
			stub(mockedHost.getType()).toReturn(IdentityType.CSS);
			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);

			asyncResult = classUnderTest.getLocalServices();

			assertNotNull(asyncResult.get());
			assertTrue(asyncResult.get().size() == 1);


		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Shouldn't be here!");
		}
	}


	/**
	 * Test method for {@link org.societies.platform.servicelifecycle.servicediscovery.ServiceDiscovery#getServices(org.societies.api.comm.xmpp.datatypes.Identity)}.
	 */
	@Test
	public void testGetServicesIdentity() {
		Future<List<Service>> asyncResult = null;
		List<Service> testLocalServiceList = new ArrayList<Service>();
		Service testService = new Service();
		testLocalServiceList.add(testService);
		try
		{
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveServicesInCSSNode(mockedHost.getJid())).toReturn(testLocalServiceList);
			stub(mockedServiceReg.retrieveServicesInCSS(mockedHost.getBareJid())).toReturn(testLocalServiceList);
			stub(mockedHost.getType()).toReturn(IdentityType.CSS);
			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);

			asyncResult = classUnderTest.getServices(mockedHost);

			assertNotNull(asyncResult.get());
			assertTrue(asyncResult.get().size() == 1);


		} catch (Exception e)
		{
			e.printStackTrace();
			fail("Shouldn't be here!");
		};	
	}

}
