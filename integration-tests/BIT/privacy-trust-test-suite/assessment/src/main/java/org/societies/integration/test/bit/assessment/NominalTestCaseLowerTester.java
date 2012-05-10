package org.societies.integration.test.bit.assessment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static IPrivacyLogAppender privacyLogAppender;
	
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
		
		LOG.info("[#000] Initialization");
		LOG.info("[#000] Prerequisite: The CSS is created");
		LOG.info("[#000] Prerequisite: The user is logged to the CSS");

		privacyLogAppender = TestCase000.getPrivacyLogAppender();
		
		assertNotNull(privacyLogAppender);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#000] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#000] tearDown");
	}


	@Test
	public void testLogCommsFw() throws InterruptedException {
		
		LOG.info("[#000] testLogCommsFw()");

		privacyLogAppender.logCommsFw(null, null, null);

		LOG.info("[#000] testLogCommsFw(): FINISHED");
	}
}