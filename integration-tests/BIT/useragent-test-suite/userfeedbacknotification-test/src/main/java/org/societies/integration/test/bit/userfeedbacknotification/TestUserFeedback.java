package org.societies.integration.test.bit.userfeedbacknotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.integration.test.IntegrationTestCase;

public class TestUserFeedback extends IntegrationTestCase {

    private static final Logger log = LoggerFactory.getLogger(TestUserFeedback.class);

    private static IUserFeedback userFeedback;
    private static ICommManager commManager;

    public TestUserFeedback() {
        super(2074, Tester.class);
        log.debug("Starting TestUserFeedback");

    }

    public static IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(IUserFeedback userFeedback) {
    	TestUserFeedback.userFeedback = userFeedback;
    }
    
    public static ICommManager getCommManager() {
        return commManager;
    }

    public void setCommManager(ICommManager commManager) {
    	TestUserFeedback.commManager = commManager;
    }

}
