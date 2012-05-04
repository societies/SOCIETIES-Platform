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
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
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
		
		LOG.info("[#713] testInstallService()");

		List<Service> servicesBefore;
		List<Service> servicesAfter;
		List<Service> servicesNew;
		
		servicesBefore = getLocalServices();
		LOG.info("[#713] testInstallService 1");

		for (Service service : servicesBefore) {
			LOG.debug("[#713] Before installation: found service " + service.getServiceIdentifier().getServiceInstanceIdentifier());
		}
		
		serviceId = installService();
		LOG.info("[#713] testInstallService 2");
		Thread.sleep(2000);
		assertNotNull("Service ID is null", serviceId.getServiceInstanceIdentifier());
		LOG.info("[#713] testInstallService 3");
		servicesAfter = getLocalServices();
		for (Service service : servicesAfter) {
			LOG.debug("[#713] After installation: found service " + service.getServiceIdentifier().getServiceInstanceIdentifier());
		}
		servicesNew = getAdditionalServices(servicesBefore, servicesAfter);
		int numServices = 0;
		
		LOG.info("[#713] testInstallService 4");
		assertEquals("Number of all services not increased by exactly 1", 1, servicesAfter.size() - servicesBefore.size());
		LOG.info("[#713] testInstallService 5");
		assertEquals("Number of new services not exactly 1", 1, servicesNew.size());
		LOG.info("[#713] testInstallService 6");
		assertEquals("Incorrect service ID", servicesNew.get(0).getServiceIdentifier().getServiceInstanceIdentifier(),
				serviceId.getServiceInstanceIdentifier());
		LOG.info("[#713] testInstallService 7");

		// -- Find the service
		for (Service service : servicesAfter) {
			if (service.getServiceIdentifier().getServiceInstanceIdentifier().equals(serviceId.getServiceInstanceIdentifier())) {
				// Mark the service as found
				LOG.info("[#713] service " + serviceId.getServiceInstanceIdentifier() + " found");
				++numServices;
				break;
			}
		}
		LOG.info("[#713] testInstallService 7.1");
		assertEquals("Number of services with ID " + serviceId.getServiceInstanceIdentifier() + " not exactly 1", 1, numServices);
		LOG.info("[#713] testInstallService 7.2");
		
		uninstallService(serviceId);
		Thread.sleep(2000);
		
		servicesAfter = getLocalServices();
		servicesNew = getAdditionalServices(servicesBefore, servicesAfter);
		assertEquals("Number of all services not same as before installation", 0, servicesAfter.size() - servicesBefore.size());
		LOG.info("[#713] testInstallService 8");
		assertEquals("Number of new services not exactly 0", 0, servicesNew.size());
		LOG.info("[#713] testInstallService 9");
		
		LOG.info("[#713] testInstallService: SUCCESS");
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
		LOG.debug("[#713] installService()");
		asyncResult = serviceControl.installService(serviceBundleUrl);
		result = asyncResult.get();
		ResultMessage message = result.getMessage();
		
		if (!message.equals(ResultMessage.SUCCESS)) {
			throw new Exception("Can't install the service. Returned value: " + message);
		}
		LOG.debug("[#713] installService(): " + result.getServiceId().getServiceInstanceIdentifier() + ", " + message);
		
		return result.getServiceId();
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
		LOG.debug("[#713] Preamble: Uninstall the service");
		asyncResult = serviceControl.uninstallService(serviceId);
		result = asyncResult.get();
		ResultMessage message = result.getMessage();

		if (!message.equals(ResultMessage.SUCCESS)) {
			throw new Exception("Can't uninstall the service. Returned value: " + message);
		}
		LOG.debug("[#713] uninstallService(): " + result.getServiceId().getServiceInstanceIdentifier() + ", " + message);
	}
	
	private List<Service> getLocalServices() throws ServiceDiscoveryException, InterruptedException, ExecutionException {

		Future<List<Service>> asyncServices = null;
		List<Service> services =  new ArrayList<Service>();

		// -- Search all local services
		LOG.debug("[#713] getLocalServices() 1");
		asyncServices = TestCase713.getServiceDiscovery().getLocalServices();
		LOG.debug("[#713] getLocalServices() 2");
		services = asyncServices.get();
		LOG.debug("[#713] getLocalServices() 3");

		return services;
	}
	
	private List<Service> getAdditionalServices(List<Service> services1, List<Service> services2) {

		List<Service> servicesNew = new ArrayList<Service>();
		String id1;
		String id2;
		
		// -- Find the service
		for (Service service : services2) {
			id2 = service.getServiceIdentifier().getServiceInstanceIdentifier();
			LOG.debug("id2 = " + id2);
			for (Service sBefore : services1) {
				id1 = sBefore.getServiceIdentifier().getServiceInstanceIdentifier();
				LOG.debug("id1 = " + id1);
				if (id1.equals(id2)) {
					break;
				}
				servicesNew.add(sBefore);
				LOG.debug("Added = " + id1);
			}
		}
		return servicesNew;
	}
}