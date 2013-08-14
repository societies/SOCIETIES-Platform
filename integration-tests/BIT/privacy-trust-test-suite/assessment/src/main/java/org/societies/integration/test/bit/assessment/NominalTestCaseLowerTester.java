package org.societies.integration.test.bit.assessment;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.AssessmentResultClassName;
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
		
		LOG.info("[#1870] Initialization");
		LOG.info("[#1870] Prerequisite: The CSS is created");
		LOG.info("[#1870] Prerequisite: The user is logged to the CSS");

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
		LOG.info("[#1870] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#1870] tearDown");
	}

	@Test
	public void testSpeedOfExecution() {
		
		LOG.info("[#1870] testSpeedOfExecution()");

		IIdentity owner = new MockIdentity("owner.a@a.com");
		//IIdentity requestorId = new MockIdentity("requestor.a@a.com");
		IIdentity requestorId = new MockIdentity("from.a@a.com");
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
		LOG.debug("[#1870] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = Calendar.getInstance().getTimeInMillis();
		privacyLogAppender.logContext(requestor, owner, 6543);
		end = Calendar.getInstance().getTimeInMillis();
		dt = end - start;
		LOG.debug("[#1870] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		start = Calendar.getInstance().getTimeInMillis();
		privacyLogAppender.logCommsFw(fromIdentity, toIdentity, payload);
		privacyLogAppender.logCommsFw(identityManager.getThisNetworkNode(), identityManager.getThisNetworkNode(), payload);
		end = Calendar.getInstance().getTimeInMillis();
		dt = end - start;
		LOG.debug("[#1870] testSpeedOfExecution(): invocation took " + dt + " ms");
		assertTrue(dt < PRIVACY_LOGGER_MAX_EXECUTION_TIME_IN_MS);
		
		LOG.info("[#1870] testSpeedOfExecution(): FINISHED");
	}
	
	@Test
	public void testContextBrokerInternalLogging() throws CtxException, InterruptedException, ExecutionException {

		LOG.info("[#1870] testContextBrokerInternalLogging()");

		CtxBrokerInternalHelper ctx = new CtxBrokerInternalHelper(ctxBrokerInternal);
		long num1;
		long num2;
		
		num1 = assessment.getNumDataAccessEvents(null, null);
		LOG.debug("[#1870] testContextBrokerInternalLogging() 1");
		ctx.retrieveCssOperator();

		num2 = assessment.getNumDataAccessEvents(null, null);
		assertEquals("ctx.retrieveCssOperator()", num1 + 1, num2);
		
		LOG.debug("[#1870] testContextBrokerInternalLogging() 2");
		ctx.createContext();
		LOG.debug("[#1870] testContextBrokerInternalLogging() 3");
		
		num1 = num2;
		num2 = assessment.getNumDataAccessEvents(null, null);
		assertEquals("Number of data access events not same after ctx.createContext()", num1, num2);

		ctx.retrieveContext();
		LOG.debug("[#1870] testContextBrokerInternalLogging() 4");
		num1 = num2;
		num2 = assessment.getNumDataAccessEvents(null, null);
		LOG.debug("[#1870] testContextBrokerInternalLogging(): Number of data access events: before access = " +
				num1 + ", after access = " + num2);
		assertEquals("Number of data access events not increased properly after ctx.retrieveContext()", num1 + 2, num2);
	}
	
	@Test
	public void testContextBrokerExternalLogging() throws CtxException, InterruptedException, ExecutionException {

		LOG.info("[#1870] testContextBrokerExternalLogging()");

		IIdentity requestor = identityManager.getThisNetworkNode();
		CtxBrokerExternalHelper ctx = new CtxBrokerExternalHelper(ctxBrokerExternal, requestor);
		long num1;
		long num2;
		
		num1 = assessment.getNumDataAccessEvents(null, null);
		
		LOG.debug("[#1870] testContextBrokerExternalLogging() 2");
		ctx.createContext();
		LOG.debug("[#1870] testContextBrokerExternalLogging() 3");
		
		num2 = assessment.getNumDataAccessEvents(null, null);
		assertEquals("Number of data access events not same after ctx.createContext()", num1, num2);

		ctx.retrieveContext();
		LOG.debug("[#1870] testContextBrokerExternalLogging() 4");
		num1 = num2;
		num2 = assessment.getNumDataAccessEvents(null, null);
		LOG.debug("[#1870] testContextBrokerExternalLogging(): Number of data access events: before access = " +
				num1 + ", after access = " + num2);
		assertEquals("Number of data access events not increased properly after ctx.retrieveContext()", num1 + 2, num2);
	}

	@Test
	public void testCommsManagerLogging() throws CommunicationException {
		
		LOG.info("[#1870] testCommsManagerLogging()");

		IIdentity to = identityManager.getThisNetworkNode();
		Stanza stanza = new Stanza(to);
		
		stanza.setId("001");
		
		ProviderBean payload = new ProviderBean();
		payload.setMethod(MethodType.ACCEPT_POLICY_AND_GET_SLA);
		payload.setServiceId("service-1");
		payload.setSessionId(1);
		payload.setSignedPolicyOption("<sla/>");
		payload.setModified(false);

		LOG.debug("[#1870] testCommsManagerLogging(): from identity = " + stanza.getFrom());
		LOG.debug("[#1870] testCommsManagerLogging(): to identity = " + stanza.getTo());
		
		LOG.debug("[#1870] testCommsManagerLogging() 1");
		long num1 = assessment.getNumDataTransmissionEvents(null, null);
		LOG.debug("[#1870] testCommsManagerLogging() 2");
		commManager.sendMessage(stanza, payload);
		LOG.debug("[#1870] testCommsManagerLogging() 3");
		commManager.sendIQGet(stanza, payload, null);
		LOG.debug("[#1870] testCommsManagerLogging() 4");
		long num2 = assessment.getNumDataTransmissionEvents(null, null);
		LOG.debug("[#1870] testCommsManagerLogging() 5");
		
		LOG.debug("[#1870] testCommsManagerLogging(): Number of data transmission events: before transmission = " +
				num1 + ", after transmission = " + num2);
		
		assertEquals(num1 + 2, num2);
	}
	
	@Test
	public void testCorrelationBySenderClass() throws Exception {
		
		LOG.info("[#1870] testCorrelationByClass()");

		List<HashMap<String, Double>> corrs = new ArrayList<HashMap<String, Double>>();
		HashMap<String, AssessmentResultClassName> result;
		
		assessment.assessAllNow(null, null);
		result = assessment.getAssessmentAllClasses(true, null, null);
		HashMap<String, Double> corrs0 = new HashMap<String, Double>();
		for (String key : result.keySet()) {
			corrs0.put(key, result.get(key).getCorrWithDataAccessBySender());
		}
		corrs.add(corrs0);

		accessContext();
		Thread.sleep(100);
		transmitData(false);
		Thread.sleep(100);

		assessment.assessAllNow(null, null);
		result = assessment.getAssessmentAllClasses(true, null, null);
		HashMap<String, Double> corrs1 = new HashMap<String, Double>();
		for (String key : result.keySet()) {
			corrs1.put(key, result.get(key).getCorrWithDataAccessBySender());
		}
		corrs.add(corrs1);

		transmitData(true);
		Thread.sleep(100);
		
		assessment.assessAllNow(null, null);
		result = assessment.getAssessmentAllClasses(true, null, null);
		HashMap<String, Double> corrs2 = new HashMap<String, Double>();
		for (String key : result.keySet()) {
			corrs2.put(key, result.get(key).getCorrWithDataAccessBySender());
		}
		corrs.add(corrs2);
		
		for (String key : corrs0.keySet()) {
			LOG.debug("Verifying correlation by class for {}", key);
			if (key.equals(getClass().getName())) {
				LOG.debug("Correlations for this class ({}) should have increased", key);
				LOG.debug("Correlations for this class: {}, {}, " + corrs2.get(key), corrs0.get(key), corrs1.get(key));
				assertTrue(corrs0.get(key) < corrs1.get(key));
				assertTrue(corrs1.get(key) < corrs2.get(key));
			}
			else {
				LOG.debug("Correlations for other class ({}) should have remained the same", key);
				LOG.debug("Correlations for other class: {}, {}, " + corrs2.get(key), corrs0.get(key), corrs1.get(key));
				assertEquals(corrs0.get(key), corrs1.get(key), 0.0);
				assertEquals(corrs1.get(key), corrs2.get(key), 0.0);
			}
		}
	}
	
	private void accessContext() throws Exception {
		
		IIdentity requestor = identityManager.getThisNetworkNode();
		CtxBrokerExternalHelper ctx = new CtxBrokerExternalHelper(ctxBrokerExternal, requestor);
		
		ctx.createContext();
		ctx.retrieveContext();
	}
	
	/**
	 * 
	 * @param returnValue true to send asynchronous message with callback, false to send one-way message
	 * @throws CommunicationException
	 */
	private void transmitData(boolean returnValue) throws CommunicationException {
		
		IIdentity to = identityManager.getThisNetworkNode();
		Stanza stanza = new Stanza(to);
		
		ProviderBean payload = new ProviderBean();
		payload.setMethod(MethodType.ACCEPT_POLICY_AND_GET_SLA);
		payload.setServiceId("service-1");
		payload.setSessionId(1);
		payload.setSignedPolicyOption("<sla/>");
		payload.setModified(false);

		if (returnValue) {
			commManager.sendMessage(stanza, payload);
		}
		else {
			commManager.sendIQGet(stanza, payload, null);
		}
	}
}
