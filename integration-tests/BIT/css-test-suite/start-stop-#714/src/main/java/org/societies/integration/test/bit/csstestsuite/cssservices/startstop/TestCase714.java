/**
 * 
 */
package org.societies.integration.test.bit.csstestsuite.cssservices.startstop;

/**
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;


public class TestCase714 {

	private static IServiceControl serviceControl;
	private static IServiceDiscovery serviceDiscovery; 
	private static Logger LOG = LoggerFactory.getLogger(TestCase714.class);
	private String results = new String();

	
	private JUnitCore jUnitCore;
		
	public TestCase714() {
		if(LOG.isDebugEnabled()) LOG.debug("TestCase714 Constructor");
	}
	
	public IServiceControl getServiceControl() {
		return serviceControl;
	}

	public void setServiceControl(IServiceControl serviceControlStuff) {
		serviceControl = serviceControlStuff;
	}

	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscoveryStuff) {
		serviceDiscovery= serviceDiscoveryStuff;
	}
	
	private void startTest() {
		if(LOG.isDebugEnabled()) LOG.debug("###714... startTest");
		
		jUnitCore = new JUnitCore();
		Result res = jUnitCore.run(NominalCase.class);
		
		
		String testClass = "Class: ";
        String testFailCt = "Failure Count: ";
        String testFailures = "Failures: ";
        String testRunCt = "Runs: ";
        String testRunTm = "Run Time: ";
        String testSuccess = "Success: ";
        String newln = "\n ";
        results += testClass + NominalCase.class.getName() + newln;
        results += testFailCt + res.getFailureCount() + newln;
        results += testFailures + newln;
        List<Failure> failures = res.getFailures();
        int i = 0;
        for (Failure x: failures)
        {
            i++;
            results += i +": " + x + newln;
        }
        results += testRunCt + res.getRunCount() + newln;
        results += testRunTm + res.getRunTime() + newln;
        results += testSuccess + res.wasSuccessful() + newln;

		LOG.info("###714 " + results);
	}
	
	static public class NominalCase {
		
		private Service serviceUnderTest;

		public NominalCase(){
			
		}
		
		@Before
		public void setUp() {
			if(LOG.isDebugEnabled()) LOG.debug("###714 Tests... setUp");
			serviceUnderTest = null;
		}

		@Test
		public void testBody() {
			if(LOG.isDebugEnabled()) LOG.debug("###714... testBody");
			
			try {
				Future<List<Service>> futureServices = serviceDiscovery.getLocalServices();
					
				List<Service> services = futureServices.get();
				
				if(services.isEmpty()){
					fail("No services");
					return;
				} else
					serviceUnderTest = services.get(0);
				
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
				
				if(LOG.isDebugEnabled()) LOG.debug("Service " + serviceUnderTest.getServiceName() + " is started, we shall stop it");
				
				Future<ServiceControlResult> stopResultFuture = serviceControl.stopService(serviceUnderTest.getServiceIdentifier());
				ServiceControlResult stopResult = stopResultFuture.get();
				
				Assert.assertEquals(ServiceControlResult.SUCCESS, stopResult);
				
			} catch(Exception ex){
				LOG.error("Error while running test: " + ex);
				ex.printStackTrace();
				fail("Exception occured");
			}
		}
		
		private void checkIfStopped(Service serviceUnderTest) {
			if(LOG.isDebugEnabled()) LOG.debug("checkIfStopped");
			
			try{
				
				Future<List<Service>> futureServices = serviceDiscovery.getLocalServices();
				
				List<Service> services = futureServices.get();
				
				if(services.isEmpty()){
					fail("No services");
					return;
				}
				
				Service testService = null;
				// We get a service that is started
				for(Service service: services){
					if(service.getServiceIdentifier().getIdentifier().equals(serviceUnderTest.getServiceIdentifier().getIdentifier())){
						testService = service;
						break;
					}	
				}
				
				if(testService == null){
					fail("Couldn't find the service");
					return;
				}
				
				LOG.info("Service " + testService.getServiceName() + " is " + testService.getServiceStatus());

				Assert.assertEquals(ServiceStatus.STOPPED, testService.getServiceStatus());
				
			} catch(Exception ex){
				LOG.error("Error while running test: " + ex);
				ex.printStackTrace();
				fail("Exception occured");
			}

		}
		
		private void startService(Service serviceUnderTest) {
			if(LOG.isDebugEnabled()) LOG.debug("startService");
			
			try{
				
				LOG.info("Service " + serviceUnderTest.getServiceName() + " is stopped, we shall start it");
				
				Future<ServiceControlResult> startResultFuture = serviceControl.startService(serviceUnderTest.getServiceIdentifier());
				ServiceControlResult startResult = startResultFuture.get();
				
				Assert.assertEquals(ServiceControlResult.SUCCESS, startResult);
				
				
			} catch(Exception ex){
				LOG.error("Error while running test: " + ex);
				ex.printStackTrace();
				fail("Exception occured");
			}

		}
		
		
		private void checkIfStarted(Service serviceUnderTest) {
			if(LOG.isDebugEnabled()) LOG.debug("checkIfStarted");
				
			try{
				
				Future<List<Service>> futureServices = serviceDiscovery.getLocalServices();
				
				List<Service> services = futureServices.get();
				
				if(services.isEmpty()){
					fail("No services");
					return;
				}
				
				Service testService = null;
				// We get a service that is started
				for(Service service: services){
					if(service.getServiceIdentifier().getIdentifier().equals(serviceUnderTest.getServiceIdentifier().getIdentifier())){
						testService = service;
						break;
					}	
				}
				
				if(testService == null){
					fail("Couldn't find the service");
					return;
				}
				
				LOG.info("Service " + testService.getServiceName() + " is " + testService.getServiceStatus());

				Assert.assertEquals(ServiceStatus.STARTED, testService.getServiceStatus());
				
			} catch(Exception ex){
				LOG.error("Error while running test: " + ex);
				ex.printStackTrace();
				fail("Exception occured");
			}
		}

		
		@After
		public void tearDown() {
			if(LOG.isDebugEnabled()) LOG.debug("###714... tearDown");
		}
		
	}
}