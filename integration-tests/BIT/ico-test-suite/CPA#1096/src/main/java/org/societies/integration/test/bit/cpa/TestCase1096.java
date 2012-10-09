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
import org.societies.api.internal.orchestration.ICPA;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1096 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1096.class);
    public static ICisManager cisManager;
    public static ICommManager commManager;
    public static ICPA cpa;
    public static ICisDataCollector cisDataCollector;
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


    public void setCisDataCollector(ICisDataCollector cisDataCollector) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCollector()");
        TestCase1096.cisDataCollector = cisDataCollector;
    }

    public void setCpa(ICPA cpa) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCpa()");
        TestCase1096.cpa = cpa;
    }

    public void setCommManager(ICommManager commManager) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCommManager()");
        this.commManager = commManager;
    }


    public  void setCisManager(ICisManager cisManager) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCisManager()");
        this.cisManager = cisManager;
    }
    public void destroy(){

    }

	
}
