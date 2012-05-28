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
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.schema.security.policynegotiator.ProviderBean;
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

		LOG.info("[#1055] testContextLogging()");

		CtxBrokerHelper ctx = new CtxBrokerHelper(ctxBrokerInternal);
		
		long num1 = assessment.getNumDataAccessEvents();
		LOG.debug("[#1055] testContextLogging() 1");
		ctx.retrieveCssOperator();
		LOG.debug("[#1055] testContextLogging() 2");
		ctx.createContext();
		LOG.debug("[#1055] testContextLogging() 3");
		ctx.retrieveContext();
		LOG.debug("[#1055] testContextLogging() 4");
		long num2 = assessment.getNumDataAccessEvents();
		
		LOG.debug("[#1055] testContextLogging(): Number of data access events: before access = " +
				num1 + ", after access = " + num2);
		
		assertEquals(num1 + 3, num2);
	}

	@Test
	public void testCommsManagerLogging() throws CommunicationException {
		
		LOG.info("[#1055] testCommsManagerLogging()");

		IIdentity from = identityManager.getThisNetworkNode();
		IIdentity to = identityManager.getThisNetworkNode();
		Stanza stanza = new Stanza(to);
		
		stanza.setId("001");
		
		ProviderBean payload = new ProviderBean();
		payload.setMethod(MethodType.ACCEPT_POLICY_AND_GET_SLA);
		payload.setServiceId("service-1");
		payload.setSessionId(1);
		payload.setSignedPolicyOption("<sla/>");
		payload.setModified(false);

		LOG.debug("[#1055] testCommsManagerLogging(): from identity = " + stanza.getFrom());
		LOG.debug("[#1055] testCommsManagerLogging(): to identity = " + stanza.getTo());
		
		LOG.debug("[#1055] testCommsManagerLogging() 1");
		long num1 = assessment.getNumDataTransmissionEvents();
		LOG.debug("[#1055] testCommsManagerLogging() 2");
		commManager.sendMessage(stanza, payload);
		LOG.debug("[#1055] testCommsManagerLogging() 3");
		commManager.sendIQGet(stanza, payload, null);
		LOG.debug("[#1055] testCommsManagerLogging() 4");
		long num2 = assessment.getNumDataTransmissionEvents();
		LOG.debug("[#1055] testCommsManagerLogging() 5");
		
		LOG.debug("[#1055] testCommsManagerLogging(): Number of data transmission events: before transmission = " +
				num1 + ", after transmission = " + num2);
		
		assertEquals(num1 + 2, num2);
	}
}
