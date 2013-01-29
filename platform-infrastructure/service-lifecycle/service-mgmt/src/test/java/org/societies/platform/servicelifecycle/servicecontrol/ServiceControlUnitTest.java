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
package org.societies.platform.servicelifecycle.servicecontrol;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.anyObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.internal.servicelifecycle.IServiceControlCallback;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.platform.servicelifecycle.servicecontrol.ServiceControl;

/**
 * Junit Test for Service Control
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceControlUnitTest {

	private ServiceControl classUnderTest;
	
	private FakeServiceRemote fakeServiceRemote;
	
	private IServiceRegistry mockedServiceReg ; 
	private ICommManager mockedCommManager;
	private IIdentityManager mockedIdentityManager;
	private BundleContext mockedBundleContext;
	private Bundle mockedBundle;
	private IIdentity mockedNode; 
	private INetworkNode mockedHost;
	private String hostJid;
	private String remoteJid;
	private Service testService;
	private ServiceResourceIdentifier testServiceId;
	private ServiceResourceIdentifier otherServiceId;


	private URL testUrl;

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		mockedServiceReg = mock(IServiceRegistry.class); 
		mockedCommManager = mock(ICommManager.class);
		mockedIdentityManager = mock(IIdentityManager.class);
		mockedBundleContext = mock(BundleContext.class);
		mockedBundle = mock(Bundle.class);
		mockedNode = mock(IIdentity.class);
		mockedHost = mock(INetworkNode.class);
		
		testService = new Service();
		testService.setAuthorSignature("authorSignature");
		testService.setServiceDescription("Description");
		testService.setServiceLocation("somewhere");
		testService.setContextSource("isContextSource");
		testService.setPrivacyPolicy("privacy");
		testService.setSecurityPolicy("security");
		testService.setServiceCategory("testService");
		testService.setServiceName("ServiceName");
		testService.setServiceType(ServiceType.THIRD_PARTY_SERVER);
		hostJid = new String("testnode.societies.local");
		remoteJid = new String("remotenode.societies.local");
		testService.setServiceEndpoint(hostJid+"/"+testService.getServiceName().replaceAll(" ", ""));
		
		ServiceInstance testServiceInstance = new ServiceInstance();
		testServiceInstance.setFullJid("testnode.societies.local");
		testServiceInstance.setXMPPNode("testnode.societies.local");
		ServiceImplementation testServiceImplementation = new ServiceImplementation();
		testServiceImplementation.setServiceNameSpace("namespace");
		testServiceImplementation.setServiceVersion("version");
		testServiceImplementation.setServiceProvider("SOCIETIES");
		testServiceInstance.setServiceImpl(testServiceImplementation);
		testService.setServiceInstance(testServiceInstance);
		
		testServiceId = new ServiceResourceIdentifier();
		testServiceId.setServiceInstanceIdentifier("111");
		testServiceId.setIdentifier(new URI("http://" + testService.getServiceEndpoint()));
		
		otherServiceId = new ServiceResourceIdentifier();
		otherServiceId.setServiceInstanceIdentifier("115");
		otherServiceId.setIdentifier(new URI("http://other" + testService.getServiceEndpoint()));
		
		testService.setServiceIdentifier(testServiceId);
				
		testUrl = new URL("http://www.testurl.com/teststuff.jar");
		
		fakeServiceRemote = new FakeServiceRemote();
		
		// Now we create the class under test
		classUnderTest = new ServiceControl();
		classUnderTest.setServiceControlRemote(fakeServiceRemote);
		classUnderTest.setServiceReg(mockedServiceReg);
		classUnderTest.setCommMngr(mockedCommManager);
		classUnderTest.setBundleContext(mockedBundleContext);
		
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		mockedServiceReg = null;
		mockedCommManager = null;
		mockedIdentityManager = null;
		mockedBundleContext = null;
		mockedBundle = null;
		
		mockedHost = null;
		mockedNode = null; 
		testService = null;
		testServiceId = null;
		otherServiceId = null;
		testUrl = null;
		fakeServiceRemote = null;

	}

/*
	@Test
	public void testServiceStuff(){
		
		try{
			
			ServiceResourceIdentifier testSRI= new ServiceResourceIdentifier();
			ServiceResourceIdentifier othertestSRI= new ServiceResourceIdentifier();
			testSRI.setServiceInstanceIdentifier("212");
			othertestSRI.setServiceInstanceIdentifier("211");
			testSRI.setIdentifier(new URI("http://manager.local.societies/laptop/CalculatorService"));
			othertestSRI.setIdentifier(new URI("http://manager.local.societies/laptop/CalculatorService"));
			
			if(testSRI.equals(othertestSRI)){
				System.out.println("true!");
			} else
				System.out.println("false!");
			
			System.out.println("getHost()" +testSRI.getIdentifier().getHost());
			System.out.println("getPath()" +testSRI.getIdentifier().getPath());
			System.out.println("getFragment()" +testSRI.getIdentifier().getFragment());
			System.out.println("getScheme()" +testSRI.getIdentifier().getScheme());
			
			assertTrue(testSRI.equals(othertestSRI));
			
		} catch(Exception ex){
			ex.printStackTrace();
			fail();
		}

	}

	@Test
	public void testStartServiceSuccess() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.ACTIVE);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.startService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}


	@Test
	public void testStartServiceRemote() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().fromJid(otherServiceId.getIdentifier().getHost())).toReturn(mockedNode);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.startService(otherServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}
	

	@Test
	public void testStartServiceOsgiProblem() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.startService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.OSGI_PROBLEM));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}
	

	@Test
	public void testStartServiceWrongService() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(null);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.startService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SERVICE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}


	@Test
	public void testStartServiceWrongBundle() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(null);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.startService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.BUNDLE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}


	@Test
	public void testStopServiceSuccess() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);;
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.stopService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}	
	

	@Test
	public void testStopServiceRemote() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().fromJid(otherServiceId.getIdentifier().getHost())).toReturn(mockedNode);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.stopService(otherServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}
	

	@Test
	public void testStopServiceOsgiProblem() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.ACTIVE);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.stopService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.OSGI_PROBLEM));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}	
	
	@Test
	public void testStopServiceWrongService() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(null);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.stopService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SERVICE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}
	
	@Test
	public void testStopServiceWrongBundle() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(null);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.stopService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.BUNDLE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}


	/*
	@Test
	public void testInstallServiceURLSuccess() {
		
		try{

			stub(mockedBundleContext.installBundle(testUrl.toString())).toReturn(mockedBundle);
			stub(mockedBundle.getState()).toReturn(Bundle.ACTIVE);
			stub(mockedBundle.getSymbolicName()).toReturn("mockedBundle");
			Version version = new Version("1.0.2");
			stub(mockedBundle.getVersion()).toReturn(version);
			stub(mockedBundle.getBundleId()).toReturn(new Long(999));
			
			List<Service> serviceTestList = new ArrayList<Service>();
			serviceTestList.add(testService);
			System.out.println(serviceTestList.size());
			stub(mockedServiceReg.findServices((Service) anyObject())).toReturn(serviceTestList);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.installService(testUrl);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
		
	}
	*/

/*
	@Test
	public void testInstallServiceURLProblem() {
		
		try{

			stub(mockedBundleContext.installBundle(testUrl.toString())).toReturn(mockedBundle);
			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			Version version = new Version("1.0.2");
			stub(mockedBundle.getVersion()).toReturn(version);
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.installService(testUrl);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.OSGI_PROBLEM));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
		
	}
	

	@Test
	public void testInstallServiceURLIIdentity() {

		try{
			
			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedIdentityManager.getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedNode.getJid()).toReturn(remoteJid);

			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.installService(testUrl,mockedNode);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}


	@Test
	public void testInstallServiceURLString() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedIdentityManager.getThisNetworkNode()).toReturn(mockedHost);
			
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			
			stub(mockedNode.getJid()).toReturn(remoteJid);
			stub(mockedCommManager.getIdManager().fromJid(remoteJid)).toReturn(mockedNode);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.installService(testUrl,remoteJid);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));

			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	@Test
	public void testUninstallServiceSuccess() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.UNINSTALLED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.uninstallService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}
	

	@Test
	public void testUninstallServiceRemote() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().fromJid(otherServiceId.getIdentifier().getHost())).toReturn(mockedNode);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.uninstallService(otherServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SUCCESS));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}

	}
	

	@Test
	public void testUninstallServiceOsgiProblem() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.ACTIVE);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.uninstallService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.OSGI_PROBLEM));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}	

	@Test
	public void testUninstallServiceWrongService() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(null);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(mockedBundle);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.uninstallService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.SERVICE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}
	

	@Test
	public void testUninstallServiceWrongBundle() {
		
		try{

			stub(mockedCommManager.getIdManager()).toReturn(mockedIdentityManager);
			stub(mockedCommManager.getIdManager().getThisNetworkNode()).toReturn(mockedHost);
			stub(mockedHost.getJid()).toReturn(hostJid);
			stub(mockedCommManager.getIdManager().getThisNetworkNode().getJid()).toReturn(hostJid);
			stub(mockedServiceReg.retrieveService(testServiceId)).toReturn(testService);
			stub(mockedBundleContext.getBundle(Long.parseLong(testService.getServiceIdentifier().getServiceInstanceIdentifier()))).toReturn(null);

			stub(mockedBundle.getState()).toReturn(Bundle.RESOLVED);
			
			Future<ServiceControlResult> futureResult;
			ServiceControlResult result;
			
			futureResult = classUnderTest.uninstallService(testServiceId);
			result = futureResult.get();
			assertTrue(result.getMessage().equals(ResultMessage.BUNDLE_NOT_FOUND));
			
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception occured");
		}
	}
	*/
	private class FakeServiceRemote implements IServiceControlRemote{

		@Override
		public void startService(ServiceResourceIdentifier serviceId,
				IIdentity node, IServiceControlCallback callback) {
			
			ServiceControlResult result = new ServiceControlResult();
			result.setServiceId(serviceId);
			result.setMessage(ResultMessage.SUCCESS);
			callback.setResult(result);
			
		}

		@Override
		public void stopService(ServiceResourceIdentifier serviceId,
				IIdentity node, IServiceControlCallback callback) {
			
			ServiceControlResult result = new ServiceControlResult();
			result.setServiceId(serviceId);
			result.setMessage(ResultMessage.SUCCESS);
			callback.setResult(result);
			
		}

		@Override
		public void installService(URL bundleLocation, IIdentity node,
				IServiceControlCallback callback) {
			
			ServiceControlResult result = new ServiceControlResult();
			result.setMessage(ResultMessage.SUCCESS);
			callback.setResult(result);
			
		}

		@Override
		public void uninstallService(ServiceResourceIdentifier serviceId,
				IIdentity node, IServiceControlCallback callback) {
			
			ServiceControlResult result = new ServiceControlResult();
			result.setServiceId(serviceId);
			result.setMessage(ResultMessage.SUCCESS);
			callback.setResult(result);
			
		}

		@Override
		public void installService(Service service, IIdentity node,
				IServiceControlCallback callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shareService(Service service, IIdentity node,
				IServiceControlCallback callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unshareService(Service service, IIdentity node,
				IServiceControlCallback callback) {
			// TODO Auto-generated method stub
			
		}

		
	}

}
