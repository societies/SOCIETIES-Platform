package org.societies.integration.test.bit.assessment;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.identity.IdentityImpl;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static final long PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS = 100;
	
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
		
		LOG.info("[#1055] Initialization");
		LOG.info("[#1055] Prerequisite: The CSS is created");
		LOG.info("[#1055] Prerequisite: The user is logged to the CSS");

		privacyLogAppender = TestCase1055.getPrivacyLogAppender();
		
		assertNotNull(privacyLogAppender);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#1055] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#1055] tearDown");
	}

	@Test
	public void testSpeedOfExecution() throws InterruptedException {
		
		LOG.info("[#1055] testSpeedOfExecution()");

		IIdentity owner = new IdentityImpl("owner-1@a.com");
		IIdentity requestorId = new IdentityImpl("requestor-1@a.com");
		Requestor requestor = new Requestor(requestorId);
		
		IIdentity fromIdentity = new IdentityImpl("from-1@a.com");
		IIdentity toIdentity = new IdentityImpl("to-1@a.com");
		Object payload = "dada";
		
		Calendar cal = Calendar.getInstance();
		long start;
		long end;
		
		start = cal.getTimeInMillis();
		privacyLogAppender.logContext(requestor, owner);
		end = cal.getTimeInMillis();
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + (end - start) + " ms");
		assertTrue(end - start < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = cal.getTimeInMillis();
		privacyLogAppender.logContext(requestor, owner, 6543);
		end = cal.getTimeInMillis();
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + (end - start) + " ms");
		assertTrue(end - start < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = cal.getTimeInMillis();
		privacyLogAppender.logCommsFw(fromIdentity, toIdentity, payload);
		end = cal.getTimeInMillis();
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + (end - start) + " ms");
		assertTrue(end - start < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		LOG.info("[#1055] testSpeedOfExecution(): FINISHED");
	}
}