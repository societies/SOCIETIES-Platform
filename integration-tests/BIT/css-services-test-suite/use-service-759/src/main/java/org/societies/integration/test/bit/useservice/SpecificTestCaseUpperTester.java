package org.societies.integration.test.bit.useservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.servicelifecycle.ServiceControlException;
import org.societies.example.calculator.ICalc;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * This test case aims to verify that a service
 * - nor installed neither started can't be used
 * - installer but not started can't be used
 * @author Olivier Maridat (Trialog)
 *
 */
public class SpecificTestCaseUpperTester {
	private static Logger LOG = LoggerFactory.getLogger(SpecificTestCaseUpperTester.class);

	/**
	 * Injection of ICalc interface
	 */
	public static ICalc calculatorService;
	/**
	 * URL of the JAR of the Calculator 3P service Bundle
	 */
	private static String serviceBundleUrl;


	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 */
	@BeforeClass
	public static void initialization() {
		LOG.info("###759... Initialization");
		LOG.info("###759... Prerequisite: The CSS is created");
		LOG.info("###759... Prerequisite: The user is logged to the CSS");

		serviceBundleUrl = "file:C:/Application/Virgo/repository/usr/Calculator.jar";
	}

	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		LOG.info("###759... SpecificTestCaseUpperTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("###759.. tearDown");
	}


	/**
	 * Verify that the service is nor installed nor started
	 * and try to consume the calculator service
	 * 
	 */
	@Test(expected=NullPointerException.class)
	public void bodyUseNotAvailableService() {
		LOG.info("###759... bodyUseNotAvailableService");

		// TODO: Verify that the service is nor installed nor started
		// At the moment Calculator service can't stop without crashing the system
		// So we can't be sure that this service is not already started...

		//		Future<ServiceControlResult> asynchResult = null;
		//		ServiceControlResult scresult = null;
		//		try {
		//			LOG.info("###759... Stop the service");
		//			asynchResult = TestCase759.serviceControl.stopService(NominalTestCaseLowerTester.calculatorServiceId);
		//			scresult = asynchResult.get();
		//			if (!scresult.equals(ServiceControlResult.SUCCESS)) {
		//				throw new Exception("Can't stop the service. Returned value: "+scresult.value());
		//			}
		//
		//			LOG.info("###759... Uninstall the service");
		//			asynchResult = TestCase759.serviceControl.uninstallService(NominalTestCaseLowerTester.calculatorServiceId);
		//			scresult = asynchResult.get();
		//			if (!scresult.equals(ServiceControlResult.SUCCESS)) {
		//				throw new Exception("Can't uninstall the service. Returned value: "+scresult.value());
		//			}
		//		}
		//		catch (Exception e)
		//		{
		//			LOG.info("###759... Unknown Exception", e);
		//			fail("###759.. Unknown Exception: "+e.getMessage());
		//			return;
		//		}

		// -- Consume the service
		int expected = 3;
		int actual = 0;
		try {
			actual = NominalTestCaseLowerTester.calculatorService.Add(1, 2).get();
			LOG.info("###759... Consume Calculator Service 1+2="+actual);
			assertEquals("###759... Consume Calculator Service", expected, actual);
		} catch (InterruptedException e) {
			LOG.info("###759... InterruptedException", e);
			fail("###759.. InterruptedException: "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.info("###759... ExecutionException", e);
			fail("###759.. ExecutionException: "+e.getMessage());
		}
	}

	/**
	 * Verify that the service is installed but not started
	 * and try to consume the calculator service
	 */
	@Test(expected=NullPointerException.class)
	public void bodyUseStillNotAvailableService() {
		LOG.info("###759... bodyUseStillNotAvailableService");

		try {
			// -- Install the service
			LOG.info("###759... Preamble: Install the service");
			URL serviceUrl = new URL(serviceBundleUrl);
			Future<ServiceControlResult> asyncInstallResult = TestCase759.serviceControl.installService(serviceUrl, "");
			ServiceControlResult installResult = asyncInstallResult.get();
			if (!installResult.equals(ServiceControlResult.SUCCESS)) {
				LOG.info("###759... Can't install the service. Returned value: "+installResult.value());
				return;
			}

			// -- Verify that the service is not started
			// TODO: verify that the service is not started
			// This is not possible at the moment because stopping the Calculator service kill the system 

			// -- Consume the service
			int actual = 0;
			actual = NominalTestCaseLowerTester.calculatorService.Add(1, 2).get();
			LOG.info("###759... Consume Calculator Service 1+2="+actual);
		} catch (InterruptedException e) {
			LOG.info("###759... InterruptedException", e);
			fail("###759.. InterruptedException: "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.info("###759... ExecutionException", e);
			fail("###759.. ExecutionException: "+e.getMessage());
		} catch (MalformedURLException e) {
			LOG.info("###759... MalformedURLException", e);
			fail("###759.. MalformedURLException: "+e.getMessage());
		} catch (ServiceControlException e) {
			LOG.info("###759... ServiceControlException", e);
			fail("###759.. ServiceControlException: "+e.getMessage());
		}

		// TODO: uninstall the Calculator service
		// At the moment Calculator service can't stop without crashing the system, so this must wait.

		//				Future<ServiceControlResult> asynchResult = null;
		//				ServiceControlResult scresult = null;
		//				try {
		//					LOG.info("###759... Uninstall the service");
		//					asynchResult = TestCase759.serviceControl.uninstallService(NominalTestCaseLowerTester.calculatorServiceId);
		//					scresult = asynchResult.get();
		//					if (!scresult.equals(ServiceControlResult.SUCCESS)) {
		//						throw new Exception("Can't uninstall the service. Returned value: "+scresult.value());
		//					}
		//				}
		//				catch (Exception e)
		//				{
		//					LOG.info("###759... Unknown Exception", e);
		//					fail("###759.. Unknown Exception: "+e.getMessage());
		//					return;
		//				}
	}

	public void setCalculatorService(ICalc calculatorService) {
		LOG.info("###759... Calculator Service injected");
		this.calculatorService = calculatorService;
	}
}