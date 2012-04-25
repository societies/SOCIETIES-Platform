package org.societies.integration.test.bit.installservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseUpperTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseUpperTester.class);

	/**
	 * Id of the Calculator 3P service
	 */
	public static ServiceResourceIdentifier calculatorServiceId;
	

	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		LOG.info("[#713] NominalTestCaseUpperTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 * stop and uninstall the Calculator service
	 */
	@After
	public void tearDown() {
		LOG.info("[#713] tearDown");

		// TODO: uninstall and stop the Calculator service
		// At the moment Calculator service can't stop without crashing the system, so this must wait.
		
		Future<ServiceControlResult> asynchResult = null;
		ServiceControlResult scresult = null;
		try {
			LOG.info("[#713] Stop the service: "+calculatorServiceId.getIdentifier());
			asynchResult = TestCase713.serviceControl.stopService(calculatorServiceId);
			scresult = asynchResult.get();
			if (!scresult.equals(ServiceControlResult.SUCCESS)) {
				throw new Exception("Can't stop the service. Returned value: "+scresult.value());
			}

			LOG.info("[#713] Uninstall the service: "+calculatorServiceId.getIdentifier());
			asynchResult = TestCase713.serviceControl.uninstallService(calculatorServiceId);
			scresult = asynchResult.get();
			if (!scresult.equals(ServiceControlResult.SUCCESS)) {
				throw new Exception("Can't uninstall the service. Returned value: "+scresult.value());
			}
		}
		catch (Exception e)
		{
			LOG.info("[#713] Unknown Exception", e);
			fail("[#713] Unknown Exception: "+e.getMessage());
			return;
		}
	}


	/**
	 * Try to consume the calculator service
	 */
	@Test
	public void bodyUseService() {
		LOG.info("[#713] bodyUseService part 2");

		// -- Consume the service
		int expected = 3;
		int actual = 0;
		try {
			actual = NominalTestCaseLowerTester.calculatorService.Add(1, 2).get();
			LOG.info("[#713] Consume Calculator Service 1+2="+actual);
			assertEquals("[#713] Consume Calculator Service", expected, actual);
		} catch (InterruptedException e) {
			LOG.info("[#713] InterruptedException", e);
			fail("[#713] InterruptedException: "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.info("[#713] ExecutionException", e);
			fail("[#713] ExecutionException: "+e.getMessage());
		}
	}
}