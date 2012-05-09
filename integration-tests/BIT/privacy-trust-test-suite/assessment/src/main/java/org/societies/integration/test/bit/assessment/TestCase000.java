/**
 * 
 */
package org.societies.integration.test.bit.assessment;

/**
 * The test case 713 aims to test 3P service installation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.IPrivacyLogAppender;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase000 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase000.class);

	/**
	 * Privacy Log Appender (injected)
	 */
	private static IPrivacyLogAppender privacyLogAppender;

	public TestCase000() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(0, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 0;
	}

	public void setPrivacyLogAppender(IPrivacyLogAppender privacyLogAppender) {
		LOG.debug("[#000] setNegotiator()");
		TestCase000.privacyLogAppender = privacyLogAppender;
	}
	
	protected static IPrivacyLogAppender getPrivacyLogAppender() {
		return privacyLogAppender;
	}
}
