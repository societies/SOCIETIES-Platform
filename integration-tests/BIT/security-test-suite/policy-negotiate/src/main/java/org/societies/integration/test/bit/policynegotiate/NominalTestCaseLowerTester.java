package org.societies.integration.test.bit.policynegotiate;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static INegotiation negotiator;
	
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
	
	private boolean success = false;


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
		
		LOG.info("[#1001] Initialization");
		LOG.info("[#1001] Prerequisite: The CSS is created");
		LOG.info("[#1001] Prerequisite: The user is logged to the CSS");

		negotiator = TestCase1001.getNegotiator();
		
		assertNotNull(negotiator);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#1001] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#1001] tearDown");
	}


	/**
	 * Try to consume the service
	 * Part 1: select the service and start it if necessary
	 * @throws InterruptedException 
	 */
	@Test
	public void testNegotiation() throws InterruptedException {
		
		LOG.info("[#1001] testNegotiation()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity provider = idMgr.getThisNetworkNode();
		negotiator.startNegotiation(provider, "service-123", new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey) {
				LOG.info("onNegotiationComplete({})", agreementKey);
				assertNotNull(agreementKey);
				success = true;
			}
		});
		
		Thread.sleep(5000);
		LOG.info("[#1001] testNegotiation(): checking if successful");
		assertTrue(success);
		LOG.info("[#1001] testNegotiation(): SUCCESS");
	}
}