package org.societies.integration.test.bit.useservice;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

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
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.integration.example.service.api.IAddService;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class NominalTestCaseLowerTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	/**
	 * URL of the JAR of the Calculator 3P service Bundle
	 */
	private static URL serviceBundleUrl;
	/**
	 * Id of the Calculator 3P service
	 */
	public static ServiceResourceIdentifier calculatorServiceId;
	/**
	 * Injection of IAddService interface
	 */
	public static IAddService addService;
	/**
	 * Tools for integration test
	 */
	public static IntegrationTestUtils integrationTestUtils;
	/**
	 * Test case number
	 */
	public static int testCaseNumber;

	/**
	 * Relative path to the jar file in resources folder
	 */
	private static final String SERVICE_PATH = "IntegrationTestService-0.1.jar";

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
		LOG.info("[#1866] Initialization");
		LOG.info("[#1866] Prerequisite: The CSS is created");
		LOG.info("[#1866] Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = NominalTestCaseLowerTester.class.getClassLoader().getResource(SERVICE_PATH);//NominalTestCaseLowerTester.class.getClassLoader().getResource("Calculator-0.3.jar");//"file:/Calculator-0.3.jar";
		calculatorServiceId = null;
		LOG.info("[#1866] Service location: "+serviceBundleUrl);
		assertNotNull("Can't find the service JAR location", serviceBundleUrl);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the Calculator Service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#1866] NominalTestCaseLowerTester::setUp");

		Future<ServiceControlResult> asyncinstallResult = null;
		ServiceControlResult installResult = null;

		try {
			// -- Install the service
			LOG.info("[#1866] Preamble: Install the service");
			asyncinstallResult = TestCase759.serviceControl.installService(serviceBundleUrl);
			installResult = asyncinstallResult.get();
			if (!installResult.getMessage().equals(ResultMessage.SUCCESS)) {
				throw new Exception("Can't install the service. Returned value: "+installResult.getMessage());
			}
			calculatorServiceId = installResult.getServiceId();
		}
		catch (ServiceDiscoveryException e) {
			LOG.info("[#1866] ServiceDiscoveryException", e);
			fail("[#1866] ServiceDiscoveryException: "+e.getMessage());
			return;
		}
		catch (Exception e) {
			LOG.info("[#1866] Preamble installService: Unknown Exception", e);
			fail("[#1866] Preamble installService: Unknown Exception: "+e.getMessage());
			return;
		}
	}

	/**
	 * This method is called after every @Test methods
	 * Stop and uninstal the Calculator Service
	 */
	@After
	public void tearDown() {
		LOG.info("[#1866] tearDown");
	}


	/**
	 * Try to consume the calculator service
	 * Part 1: select the service and start it if necessary
	 */
	@Test
	public void bodyUseService() {
		LOG.info("[#1866] bodyUseService part 1");

		Future<List<Service>> asyncServices = null;
		List<Service> services =  new ArrayList<Service>();

		try {
			// Start the service
			LOG.info("[#1866] Calculator service starting");
			Future<ServiceControlResult> asyncStartResult = TestCase759.serviceControl.startService(calculatorServiceId);
			ServiceControlResult startResult = asyncStartResult.get();
			// Service can't be started
			if (!startResult.getMessage().equals(ResultMessage.SUCCESS)) {
				throw new Exception("Can't start the service. Returned value: "+startResult.getMessage());
			}
			LOG.info("[#1866] Calculator service started");
			NominalTestCaseUpperTester.calculatorServiceId = calculatorServiceId;

			// -- Test case is now ready to consume the service
			// The injection of ICalc will launch the UpperTester
		}
		catch (ServiceDiscoveryException e) {
			LOG.info("[#1866] ServiceDiscoveryException", e);
			fail("[#1866] ServiceDiscoveryException: "+e.getMessage());
			return;
		}
		catch (Exception e) {
			LOG.info("[#1866] Preamble installService: Unknown Exception", e);
			fail("[#1866] Preamble installService: Unknown Exception: "+e.getMessage());
			return;
		}
	}

	public void setAddService(IAddService addService) {
		LOG.info("[#1866] Calculator Service injected");
		NominalTestCaseLowerTester.addService = addService;
		
		// -- Launch the UpperTester to continue the test case by consuming a service
		//integrationTestUtils.run(testCaseNumber, NominalTestCaseUpperTester.class);
	}
}