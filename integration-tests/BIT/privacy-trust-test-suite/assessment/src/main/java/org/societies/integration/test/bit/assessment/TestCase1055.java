package org.societies.integration.test.bit.assessment;

import org.junit.runner.Result;
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
	private static ICommManager commManager;
	private static ICtxBroker ctxBrokerExternal;
	private static org.societies.api.internal.context.broker.ICtxBroker ctxBrokerInternal;
	private static IAssessment assessment;

	public TestCase1055() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1870, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1870;
	}

	@Override
	public Result run() {
		identityManager = commManager.getIdManager();
		return super.run();
	}
	
	// Getters and setters for beans
	protected static IPrivacyLogAppender getPrivacyLogAppender() {
		return privacyLogAppender;
	}
	public void setPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender) {
		LOG.debug("[#1870] setPrivacyLogAppender()");
		TestCase1055.privacyLogAppender = privacyLogAppender;
	}
	protected static IIdentityManager getIdentityManager() {
		return identityManager;
	}
	protected static ICommManager getCommManager() {
		return commManager;
	}
	public void setCommManager(ICommManager commManager) {
		LOG.debug("[#1870] setCommManager()");
		TestCase1055.commManager = commManager;
	}
	protected static ICtxBroker getCtxBrokerExternal() {
		return ctxBrokerExternal;
	}
	public void setCtxBrokerExternal(ICtxBroker ctxBrokerExternal) {
		LOG.debug("[#1870] setCtxBrokerExternal()");
		TestCase1055.ctxBrokerExternal = ctxBrokerExternal;
	}
	protected static org.societies.api.internal.context.broker.ICtxBroker getCtxBrokerInternal() {
		return ctxBrokerInternal;
	}
	public void setCtxBrokerInternal(org.societies.api.internal.context.broker.ICtxBroker ctxBrokerInternal) {
		LOG.debug("[#1870] setCtxBrokerInternal()");
		TestCase1055.ctxBrokerInternal = ctxBrokerInternal;
	}
	protected static IAssessment getAssessment() {
		return assessment;
	}
	public void setAssessment(IAssessment assessment) {
		LOG.debug("[#1870] setAssessment()");
		TestCase1055.assessment = assessment;
	}
}
