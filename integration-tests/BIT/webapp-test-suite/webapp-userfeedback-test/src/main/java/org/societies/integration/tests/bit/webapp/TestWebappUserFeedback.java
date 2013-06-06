package org.societies.integration.tests.bit.webapp;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.integration.api.selenium.SeleniumTest;
import org.societies.integration.api.selenium.components.UFNotificationPopup;
import org.societies.integration.api.selenium.pages.IndexPage;
import org.societies.useragent.api.feedback.IUserFeedbackHistoryRepository;

import java.util.List;
import java.util.concurrent.Future;

public class TestWebappUserFeedback extends SeleniumTest {

    private static final Logger log = LoggerFactory.getLogger(TestWebappUserFeedback.class);

    private static final String USERNAME = "paddy";
    private static final String PASSWORD = "paddy";

    private IndexPage indexPage;

    private PubsubClient pubsubClient;
    private IIdentityManager idMgr;
    private IIdentity userID;
    private IUserFeedback userFeedback;
    private IUserFeedbackHistoryRepository userFeedbackHistoryRepository;

    public TestWebappUserFeedback() {
        log.debug("TestWebappUserFeedback constructor");
    }

    @Before
    public void setupTest() {
        log.debug("Setting up test");

        this.pubsubClient = UFTestInit.getPubsub();
        this.idMgr = UFTestInit.getIdMgr();
        this.userID = this.idMgr.getThisNetworkNode();
        this.userFeedback = UFTestInit.getUserFeedback();
        this.userFeedbackHistoryRepository = UFTestInit.getUserFeedbackHistoryRepository();

        indexPage = new IndexPage(getDriver());

        log.debug("Finished setting up test");
    }

    @After
    public void tearDown() {
//        pubSubListener.unregisterForEvents();
    }

//    @Test
    public void eventsAppearAtLogin_andCanBeAccepted() {
        ExpProposalContent content = new ExpProposalContent("Pick a button", new String[]{"Yes", "No"});

        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content);
        Future<List<String>> result2 = userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, content);
        Future<List<String>> result3 = userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, content);

        indexPage.doLogin(USERNAME, PASSWORD);

        indexPage.verifyNumberInNotificationsBubble(3);

        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        Assert.assertFalse(result1.isDone());
        Assert.assertFalse(result2.isDone());
        Assert.assertFalse(result3.isDone());

        popup.answerAckNackRequest("No");
        popup.answerSelectOneRequest("Yes");
        popup.answerSelectManyRequest(new String[]{"Yes", "No"});

        log.debug("Finished responding to requests");

        Assert.assertTrue(result1.isDone());
        Assert.assertTrue(result2.isDone());
        Assert.assertTrue(result3.isDone());

        indexPage.verifyNumberInNotificationsBubble(0);

        log.debug("Test complete");
    }

    @Test
    public void sendAckNack_completeEvent_ensureDataUpdated() {
        indexPage.doLogin(USERNAME, PASSWORD);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // clear any existing data
        popup.answerAllOutstandingRequestsWithAnyOption();
        popup.close();

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ExpProposalContent content = new ExpProposalContent("Pick a button", new String[]{"Yes", "No"});
        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
        popup.answerAckNackRequest("No");
        popup.close();

        // ensure data has been updated
        Assert.assertTrue(result1.isDone());
        indexPage.verifyNumberInNotificationsBubble(0);

    }


}
