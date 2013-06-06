package org.societies.integration.tests.bit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.useragent.api.feedback.IUserFeedbackHistoryRepository;

public class TestInit extends IntegrationTestCase {

    private static final Logger log = LoggerFactory.getLogger(TestInit.class);

    private static PubsubClient pubsub;
    private static ICommManager commsMgr;
    private static IUserFeedback userFeedback;
    private static IUserFeedbackHistoryRepository userFeedbackHistoryRepository;

    public TestInit() {
        super(99999, TestIndexPage.class, TestExamplePage.class, TestProfileSettings.class);
        log.debug("Starting TestInit");

    }

    public static PubsubClient getPubsub() {
        return pubsub;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setPubsub(PubsubClient pubsub) {
        TestInit.pubsub = pubsub;
    }

    public static IIdentityManager getIdMgr() {
        return TestInit.commsMgr.getIdManager();
    }

    public static ICommManager getCommsMgr() {
        return TestInit.commsMgr;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setCommsMgr(ICommManager commsMgr) {
        TestInit.commsMgr = commsMgr;
    }

    public static IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setUserFeedback(IUserFeedback userFeedback) {
        TestInit.userFeedback = userFeedback;
    }

    @SuppressWarnings("MethodMayBeStatic")
    public void setUserFeedbackHistoryRepository(IUserFeedbackHistoryRepository userFeedbackHistoryRepository) {
        TestInit.userFeedbackHistoryRepository = userFeedbackHistoryRepository;
    }

    public static IUserFeedbackHistoryRepository getUserFeedbackHistoryRepository() {
        return userFeedbackHistoryRepository;
    }
}
