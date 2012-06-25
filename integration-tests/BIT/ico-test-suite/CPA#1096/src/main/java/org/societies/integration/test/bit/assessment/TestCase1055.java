/**
 * 
 */
package org.societies.integration.test.bit.assessment;

/**
 * The test case 713 aims to test 3P service installation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1055 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1055.class);

	/**
	 * Privacy Log Appender (injected)
	 */
	private static IPrivacyLogAppender privacyLogAppender;
	private static IIdentityManager identityManager;

	public TestCase1055() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1055, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1055;
	}

	public void setPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender) {
		LOG.debug("[#1055] setPrivacyLogAppender()");
		TestCase1055.privacyLogAppender = privacyLogAppender;
	}
	
	protected static IPrivacyLogAppender getPrivacyLogAppender() {
		return privacyLogAppender;
	}

	protected static IIdentityManager getIdentityManager() {
		return identityManager;
	}

	public void setIdentityManager(IIdentityManager identityManager) {
		LOG.debug("[#1055] setIdentityManager()");
		TestCase1055.identityManager = identityManager;
	}
	
}
