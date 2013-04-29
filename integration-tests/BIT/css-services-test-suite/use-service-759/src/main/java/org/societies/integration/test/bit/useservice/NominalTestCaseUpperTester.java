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
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.integration.example.service.api.IAddService;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class NominalTestCaseUpperTester {
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseUpperTester.class);

	/**
	 * Id of the Calculator 3P service
	 */
	public static ServiceResourceIdentifier calculatorServiceId;
	private static IAddService addService;

	public void readyForUpper(){
		LOG.info("[#1866] I'm ready to start upper testing!");
		//NominalTestCaseLowerTester.integrationTestUtils.run(NominalTestCaseLowerTester.testCaseNumber, NominalTestCaseUpperTester.class);

	}
	/**
	 * This method is called before every @Test methods.
	 */
	@Before
	public void setUp() {
		LOG.info("[#1866] NominalTestCaseUpperTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 * stop and uninstall the Calculator service
	 */
	@After
	public void tearDown() {
		LOG.info("[#1866] tearDown");

		// TODO: uninstall and stop the Calculator service
		// At the moment Calculator service can't stop without crashing the system, so this must wait.
		
		Future<ServiceControlResult> asynchResult = null;
		ServiceControlResult scresult = null;
		try {
			LOG.info("[#1866] Stop the service: "+calculatorServiceId.getIdentifier());
			asynchResult = TestCase759.serviceControl.stopService(calculatorServiceId);
			scresult = asynchResult.get();
			if (!scresult.getMessage().equals(ResultMessage.SUCCESS)) {
				throw new Exception("Can't stop the service. Returned value: "+scresult.getMessage());
			}

			LOG.info("[#1866] Uninstall the service: "+calculatorServiceId.getIdentifier());
			asynchResult = TestCase759.serviceControl.uninstallService(calculatorServiceId);
			scresult = asynchResult.get();
			if (!scresult.getMessage().equals(ResultMessage.SUCCESS)) {
				throw new Exception("Can't uninstall the service. Returned value: "+scresult.getMessage());
			}
		}
		catch (Exception e)
		{
			LOG.info("[#1866] Unknown Exception", e);
			fail("[#1866] Unknown Exception: "+e.getMessage());
			return;
		}
	}


	/**
	 * Try to consume the calculator service
	 */
	@Test
	public void bodyUseService() {
		LOG.info("[#1866] bodyUseService part 2");

		// -- Consume the service
		int expected = 3;
		int actual = 0;
		try {
			actual = addService.addNumbers(1, 2).get();
			LOG.info("[#1866] Consume Calculator Service 1+2="+actual);
			assertEquals("[#1866] Consume Calculator Service", expected, actual);
		} catch (InterruptedException e) {
			LOG.info("[#1866] InterruptedException", e);
			fail("[#1866] InterruptedException: "+e.getMessage());
		} catch (ExecutionException e) {
			LOG.info("[#1866] ExecutionException", e);
			fail("[#1866] ExecutionException: "+e.getMessage());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setAddService(IAddService addService) {
		LOG.info("[#1866] Calculator Service injected");
		this.addService = addService;

	}
}