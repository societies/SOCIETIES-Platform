package org.societies.integration.test.bit.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IAssessment;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1055 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1055.class);

	/**
	 * Privacy Log Appender (injected)
	 */
	private static IPrivacyLogAppender privacyLogAppender;
	private static IIdentityManager identityManager;
	private static ICommManager commsManager;
	private static ICtxBroker ctxBrokerExternal;
	private static org.societies.api.internal.context.broker.ICtxBroker ctxBrokerInternal;
	private static IAssessment assessment;

	public TestCase1055() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1055, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1055;
	}

	// Getters and setters for beans
	protected static IPrivacyLogAppender getPrivacyLogAppender() {
		return privacyLogAppender;
	}
	public void setPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender) {
		LOG.debug("[#1055] setPrivacyLogAppender()");
		TestCase1055.privacyLogAppender = privacyLogAppender;
	}
	protected static IIdentityManager getIdentityManager() {
		return identityManager;
	}
	public void setIdentityManager(IIdentityManager identityManager) {
		LOG.debug("[#1055] setIdentityManager()");
		TestCase1055.identityManager = identityManager;
	}
	protected static ICommManager getCommManager() {
		return commsManager;
	}
	public void setCommManager(ICommManager commsManager) {
		LOG.debug("[#1055] setCommManager()");
		TestCase1055.commsManager = commsManager;
	}
	protected static ICtxBroker getCtxBrokerExternal() {
		return ctxBrokerExternal;
	}
	public void setCtxBrokerExternal(ICtxBroker ctxBrokerExternal) {
		LOG.debug("[#1055] setCtxBrokerExternal()");
		TestCase1055.ctxBrokerExternal = ctxBrokerExternal;
	}
	protected static org.societies.api.internal.context.broker.ICtxBroker getCtxBrokerInternal() {
		return ctxBrokerInternal;
	}
	public void setCtxBrokerInternal(org.societies.api.internal.context.broker.ICtxBroker ctxBrokerInternal) {
		LOG.debug("[#1055] setCtxBrokerInternal()");
		TestCase1055.ctxBrokerInternal = ctxBrokerInternal;
	}
	protected static IAssessment getAssessment() {
		return assessment;
	}
	public void setAssessment(IAssessment assessment) {
		LOG.debug("[#1055] setAssessment()");
		TestCase1055.assessment = assessment;
	}
}
