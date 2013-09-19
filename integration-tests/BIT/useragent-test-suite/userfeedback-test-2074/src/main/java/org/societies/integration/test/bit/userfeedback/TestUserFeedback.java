package org.societies.integration.test.bit.userfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.integration.test.IntegrationTestCase;

public class TestUserFeedback extends IntegrationTestCase {

    private static final Logger log = LoggerFactory.getLogger(TestUserFeedback.class);

    private static PubsubClient pubsub;
    private static ICommManager commsMgr;
    private static IUserFeedback userFeedback;

    public TestUserFeedback() {
        super(2074, Tester.class);
        log.debug("Starting TestUserFeedback");

    }

    public static PubsubClient getPubsub() {
        return pubsub;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setPubsub(PubsubClient pubsub) {
        TestUserFeedback.pubsub = pubsub;
    }

    public static IIdentityManager getIdMgr() {
        return TestUserFeedback.commsMgr.getIdManager();
    }

    public static ICommManager getCommsMgr() {
        return TestUserFeedback.commsMgr;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setCommsMgr(ICommManager commsMgr) {
        TestUserFeedback.commsMgr = commsMgr;
    }

    public static IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setUserFeedback(IUserFeedback userFeedback) {
        TestUserFeedback.userFeedback = userFeedback;
    }

}
