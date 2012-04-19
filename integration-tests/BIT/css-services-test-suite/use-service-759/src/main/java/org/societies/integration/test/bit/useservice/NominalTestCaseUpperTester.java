package org.societies.integration.test.bit.useservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class NominalTestCaseUpperTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseUpperTester.class);


	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		LOG.info("###759... NominalTestCaseUpperTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 * stop and uninstall the Calculator service
	 */
	@After
	public void tearDown() {
		LOG.info("###759.. tearDown");

		// TODO: uninstall and stop the Calculator service
		// At the moment Calculator service can't stop without crashing the system, so this must wait.
		
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
	}


	/**
	 * Try to consume the calculator service
	 */
	@Test
	public void bodyUseService() {
		LOG.info("###759... bodyUseService part 2");

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
}