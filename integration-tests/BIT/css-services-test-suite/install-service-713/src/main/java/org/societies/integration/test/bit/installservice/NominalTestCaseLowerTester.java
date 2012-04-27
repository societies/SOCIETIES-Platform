package org.societies.integration.test.bit.installservice;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static IServiceControl serviceControl;
	
	/**
	 * URL of the JAR of the 3P service Bundle
	 */
	private static URL serviceBundleUrl;

	/**
	 * Relative path to the jar file in resources folder
	 */
	private static final String SERVICE_PATH = "Calculator-0.1.jar";

	/**
	 * Id of the 3P service
	 */
	private static ServiceResourceIdentifier serviceId;

	/**
	 * Tools for integration test
	 */
	private IntegrationTestUtils integrationTestUtils;
	
	/**
	 * Test case number
	 */
	public static int testCaseNumber;


	public NominalTestCaseLowerTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}

	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 * Select the relevant service example: the Calculator
	 */
	@BeforeClass
	public static void initialization() {
		
		LOG.info("[#713] Initialization");
		LOG.info("[#713] Prerequisite: The CSS is created");
		LOG.info("[#713] Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = NominalTestCaseLowerTester.class.getClassLoader().getResource(SERVICE_PATH);
		serviceControl = TestCase713.getServiceControl();
		
		assertNotNull(serviceBundleUrl);
		assertNotNull(serviceControl);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#713] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#713] tearDown");
	}


	/**
	 * Try to consume the service
	 * Part 1: select the service and start it if necessary
	 */
	@Test
	public void testInstallService() throws Exception {
		
		LOG.info("[#713] testInstallService");

		List<Service> servicesBefore;
		List<Service> servicesAfter;
		List<Service> servicesNew;
		
		servicesBefore = getLocalServices();
		serviceId = installService();
		assertNotNull(serviceId);
		servicesAfter = getLocalServices();
		servicesNew = getAdditionalServices(servicesBefore, servicesAfter);
		
		assertEquals("Number of all services not increased by exactly 1", 1, servicesAfter.size() - servicesBefore.size());
		assertEquals("Number of new services not exactly 1", 1, servicesNew.size());

		// -- Find the service
		for (Service service : servicesAfter) {
//			if (service.getServiceIdentifier().equals(serviceId)) {
//				// Mark the service as found
//				LOG.info("[#713] service " + serviceId + "found");
//				break;
//			}
		}
		
		uninstallService(serviceId);
		
		servicesAfter = getLocalServices();
		servicesNew = getAdditionalServices(servicesBefore, servicesAfter);
		assertEquals("Number of all services not same as before installation", 0, servicesAfter.size() - servicesBefore.size());
		assertEquals("Number of new services not exactly 0", 0, servicesNew.size());
	}
	
	/**
	 * Install the service to local node
	 * 
	 * @return ID of the installed service (NOT IMPLEMENTED YET!)
	 * @throws Exception on any error
	 */
	private ServiceResourceIdentifier installService() throws Exception {
		
		Future<ServiceControlResult> asyncResult = null;
		ServiceControlResult result = null;

		// -- Install the service
		LOG.info("[#713] Preamble: Install the service");
		asyncResult = serviceControl.installService(serviceBundleUrl);
		result = asyncResult.get();
		if (!result.equals(ServiceControlResult.SUCCESS)) {
			throw new Exception("Can't install the service. Returned value: " + result.value());
		}
		LOG.debug("[#713] installService(): " + result.value());
		
		//return installResult.getServiceId();
		// FIXME: Return the service ID when ServiceControlResult is expanded. Sancho has already implemented the change but it will be merged in May.
		return new ServiceResourceIdentifier();
	}
	
	/**
	 * Uninstall the service
	 * 
	 * @param serviceId ID of the service to uninstall
	 * @throws Exception on any error
	 */
	private void uninstallService(ServiceResourceIdentifier serviceId) throws Exception {
		
		Future<ServiceControlResult> asyncResult = null;
		ServiceControlResult result = null;

		// -- Install the service
		LOG.info("[#713] Preamble: Uninstall the service");
		asyncResult = serviceControl.uninstallService(serviceId);
		result = asyncResult.get();
		if (!result.equals(ServiceControlResult.SUCCESS)) {
			throw new Exception("Can't uninstall the service. Returned value: " + result.value());
		}
		LOG.debug("[#713] uninstallService(): " + result.value());
	}
	
	private List<Service> getLocalServices() throws ServiceDiscoveryException, InterruptedException, ExecutionException {

		Future<List<Service>> asyncServices = null;
		List<Service> services =  new ArrayList<Service>();

		// -- Search all local services
		asyncServices = TestCase713.getServiceDiscovery().getLocalServices();
		services = asyncServices.get();

		return services;
	}
	
	private List<Service> getAdditionalServices(List<Service> services1, List<Service> services2) {

		List<Service> servicesNew = new ArrayList<Service>();
		
		// -- Find the service
		for (Service service : services2) {
			
			for (Service sBefore : services1) {
				if (sBefore.getServiceIdentifier().equals(service.getServiceIdentifier())) {
					servicesNew.add(sBefore);
				}
			}
		}
		return servicesNew;
	}
}