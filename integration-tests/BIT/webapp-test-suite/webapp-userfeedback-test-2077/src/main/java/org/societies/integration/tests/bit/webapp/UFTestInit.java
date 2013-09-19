package org.societies.integration.tests.bit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.integration.test.IntegrationTestCase;

@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class UFTestInit extends IntegrationTestCase {

    private static final Logger log = LoggerFactory.getLogger(UFTestInit.class);

    private static PubsubClient pubsub;
    private static ICommManager commsMgr;
    private static IUserFeedback userFeedback;

    public UFTestInit() {
        super(2077,
                TestWebappUserFeedback.class,
                TestWebappPrivacyNegotiation.class
        );
        log.debug("Starting UFTestInit");

    }

    public static PubsubClient getPubsub() {
        return pubsub;
    }

    public void setPubsub(PubsubClient pubsub) {
        UFTestInit.pubsub = pubsub;
    }

    public static IIdentityManager getIdMgr() {
        return UFTestInit.commsMgr.getIdManager();
    }

    public static ICommManager getCommsMgr() {
        return UFTestInit.commsMgr;
    }

    public void setCommsMgr(ICommManager commsMgr) {
        UFTestInit.commsMgr = commsMgr;
    }

    public static IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(IUserFeedback userFeedback) {
        UFTestInit.userFeedback = userFeedback;
    }

}
