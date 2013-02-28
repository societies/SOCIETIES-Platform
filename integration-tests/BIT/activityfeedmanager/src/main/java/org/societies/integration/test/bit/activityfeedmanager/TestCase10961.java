/**
 * 
 */
package org.societies.integration.test.bit.activityfeedmanager;

/**
 * The test case 10961: Testing ActivityFeedManager
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase10961 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase10961.class);
    public static ICisManager cisManager;
    public static IActivityFeedManager activityFeedManager;
    public static ICommManager commManager;

    /**
	 * Privacy Log Appender (injected)
	 */

	public TestCase10961() {
		super(10961, new Class[] {NominalTestCase.class});
		NominalTestCase.testCaseNumber = testCaseNumber;
	}

    public  void setCisManager(ICisManager cisManager) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCisManager()");
        this.cisManager = cisManager;
    }

    public  void setActivityFeedManager(IActivityFeedManager activityFeedManager) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setActivityFeedManager()");
        this.activityFeedManager = activityFeedManager;
    }

    public void setCommManager(ICommManager commManager) {
        LOG.debug("[TEST CASE #"+testCaseNumber+"] setCommManager()");
        this.commManager = commManager;
    }

    public void destroy(){

    }



}
