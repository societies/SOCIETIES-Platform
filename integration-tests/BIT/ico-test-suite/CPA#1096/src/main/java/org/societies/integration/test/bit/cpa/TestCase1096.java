/**
 * 
 */
package org.societies.integration.test.bit.cpa;

/**
 * The test case 713 aims to test 3P service installation.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.orchestration.api.ISuggestedCommunityAnalyser;

public class TestCase1096 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1096.class);
    public static ICommManager commManager;
    public static ICisManager cisManager;
    public static ISuggestedCommunityAnalyser SCA;
	/**
	 * Privacy Log Appender (injected)
	 */

	public TestCase1096() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});

		super(1096, new Class[] {NominalTestCase.class});
		NominalTestCase.testCaseNumber = testCaseNumber;
	}


    public void setCommManager(ICommManager commManager) {
        LOG.debug("[#"+testCaseNumber+"] setCommManager()");
        this.commManager = commManager;
    }

    public  void setCisManager(ICisManager cisManager) {
        LOG.debug("[#"+testCaseNumber+"] setCisManager()");
        this.cisManager = cisManager;
    }

	
}
