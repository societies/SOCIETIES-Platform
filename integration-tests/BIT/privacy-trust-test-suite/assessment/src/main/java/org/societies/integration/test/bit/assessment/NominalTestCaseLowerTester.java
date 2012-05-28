package org.societies.integration.test.bit.assessment;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static final long PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS = 200;
	
	private static IPrivacyLogAppender privacyLogAppender;
	private static IAssessment assessment;
	private static IIdentityManager identityManager;
	private static ICommManager commManager;
	private static ICtxBroker ctxBrokerExternal;
	private static org.societies.api.internal.context.broker.ICtxBroker ctxBrokerInternal;

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
		assessment = TestCase1055.getAssessment();
		identityManager = TestCase1055.getIdentityManager();
		commManager = TestCase1055.getCommManager();
		ctxBrokerExternal = TestCase1055.getCtxBrokerExternal();
		ctxBrokerInternal = TestCase1055.getCtxBrokerInternal();
		
		assertNotNull(privacyLogAppender);
		assertNotNull(assessment);
		assertNotNull(identityManager);
		assertNotNull(commManager);
		assertNotNull(ctxBrokerExternal);
		assertNotNull(ctxBrokerInternal);
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
	public void testSpeedOfExecution() {
		
		LOG.info("[#1055] testSpeedOfExecution()");

		IIdentity owner = new MockIdentity("owner.a@a.com");
		IIdentity requestorId = new MockIdentity("requestor.a@a.com");
		Requestor requestor = new Requestor(requestorId);
		
		IIdentity fromIdentity = new MockIdentity("from.a@a.com");
		IIdentity toIdentity = new MockIdentity("to.a@a.com");
		Object payload = "dada";
		
		long start;
		long end;
		long dt;
		
		start = Calendar.getInstance().getTimeInMillis();
		privacyLogAppender.logContext(requestor, owner);
		end = Calendar.getInstance().getTimeInMillis();
		dt = end - start;
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = Calendar.getInstance().getTimeInMillis();
		privacyLogAppender.logContext(requestor, owner, 6543);
		end = Calendar.getInstance().getTimeInMillis();
		dt = end - start;
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = Calendar.getInstance().getTimeInMillis();
		privacyLogAppender.logCommsFw(fromIdentity, toIdentity, payload);
		end = Calendar.getInstance().getTimeInMillis();
		dt = end - start;
		LOG.debug("[#1055] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		LOG.info("[#1055] testSpeedOfExecution(): FINISHED");
	}
	
	@Test
	public void testContextLogging() throws CtxException, InterruptedException, ExecutionException {

		CtxBrokerHelper ctx = new CtxBrokerHelper(ctxBrokerInternal);
		
		long num1 = assessment.getNumDataAccessEvents();
		ctx.retrieveCssOperator();
		ctx.createContext();
		ctx.retrieveContext();
		long num2 = assessment.getNumDataAccessEvents();
		
		assertEquals(num1 + 3, num2);
	}

	@Test
	public void testCommsManagerLogging() throws CommunicationException {
		
		IIdentity from = identityManager.getThisNetworkNode();
		IIdentity to = identityManager.getThisNetworkNode();
		Stanza stanza = new Stanza("001", from, to);
		
		long num1 = assessment.getNumDataTransmissionEvents();
		commManager.sendMessage(stanza, "payload");
		long num2 = assessment.getNumDataTransmissionEvents();
		
		assertEquals(num1 + 1, num2);
	}
}
