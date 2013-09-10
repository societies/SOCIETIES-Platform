package org.societies.integration.tests.bit.webapp;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.integration.api.selenium.SeleniumTest;
import org.societies.integration.api.selenium.components.UFNotificationPopup;
import org.societies.integration.api.selenium.pages.IndexPage;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestWebappUserFeedback extends SeleniumTest {

    private static final Logger log = LoggerFactory.getLogger(TestWebappUserFeedback.class);

    private static final String USERNAME = "paddy";
    private static final String PASSWORD = "p";

    private IndexPage indexPage;

    private IUserFeedback userFeedback;

    public TestWebappUserFeedback() {
        log.debug("TestWebappUserFeedback constructor");
    }

    @Before
    public void setupTest() {
        log.debug("Setting up test");

        this.userFeedback = UFTestInit.getUserFeedback();
        userFeedback.clear();

        indexPage = new IndexPage(getDriver());

        log.debug("Finished setting up test");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void clearUserFeedbackMethod_sendsCorrectEvents_andWebappControllerListensCorrectly() {
        indexPage.doLogin(USERNAME, PASSWORD);

        indexPage.verifyNumberInNotificationsBubble(0);

        ExpProposalContent content = new ExpProposalContent("Pick a button", new String[]{"Yes", "No"});
        userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content);
        userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, content);
        userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, content);

        indexPage.verifyNumberInNotificationsBubble(3);

        userFeedback.clear();

        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void eventsAppearAtLogin_andCanBeAccepted() throws InterruptedException {
        ExpProposalContent content = new ExpProposalContent("Pick a button", new String[]{"Yes", "No"});

        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content);
        Future<List<String>> result2 = userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, content);
        Future<List<String>> result3 = userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, content);

        indexPage.doLogin(USERNAME, PASSWORD);

        indexPage.verifyNumberInNotificationsBubble(3);

        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        Assert.assertFalse("Expected event 1 NOT to be marked done", result1.isDone());
        Assert.assertFalse("Expected event 2 NOT to be marked done", result2.isDone());
        Assert.assertFalse("Expected event 3 NOT to be marked done", result3.isDone());

        popup.answerAckNackRequest("No");

        // TODO: wait until popup is stale, instead of waiting a set amount of time
        Thread.sleep(2000);

        popup.answerSelectOneRequest("Yes");

        // TODO: wait until popup is stale, instead of waiting a set amount of time
        Thread.sleep(2000);

        popup.answerSelectManyRequest(new String[]{"Yes", "No"});

        // TODO: wait until popup is stale, instead of waiting a set amount of time
        Thread.sleep(2000);

        log.debug("Finished responding to requests");

        Assert.assertTrue("Expected event 1 to be marked done", result1.isDone());
        Assert.assertTrue("Expected event 2 to be marked done", result2.isDone());
        Assert.assertTrue("Expected event 3 to be marked done", result3.isDone());

        indexPage.verifyNumberInNotificationsBubble(0);

        log.debug("Test complete");
    }

    @Test
    public void sendAckNack_completeEventViaPopup_ensureDataUpdated() throws ExecutionException, InterruptedException {

        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ExpProposalContent content = new ExpProposalContent("Pick a button", new String[]{"Yes", "No"});
        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
        popup.answerAckNackRequest("No");
        popup.close();

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Value not updated in AckNack Future object", 1, result1.get().size());
        Assert.assertEquals("Wrong value in Future object", "No", result1.get().get(0));
        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void sendSelectOne_completeEventViaPopup_ensureDataUpdated() throws ExecutionException, InterruptedException {
        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ExpProposalContent content = new ExpProposalContent("Pick one", new String[]{"Yes", "No", "Maybe", "Sometimes", "Dont know", "Dont care"});
        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
        popup.answerSelectOneRequest("Maybe");
        popup.close();

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Value not updated in SelectOne Future object", 1, result1.get().size());
        Assert.assertEquals("Wrong value in Future object", "Maybe", result1.get().get(0));
        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void sendSelectMany_completeEventViaPopup_ensureDataUpdated() throws ExecutionException, InterruptedException {
        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ExpProposalContent content = new ExpProposalContent("Pick two", new String[]{"Yes", "No", "Maybe", "Sometimes", "Dont know", "Dont care"});
        Future<List<String>> result1 = userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
        popup.answerSelectManyRequest(new String[]{"Maybe", "Dont know", "Dont care"});
        popup.close();

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Value not updated in SelectMany Future object", 3, result1.get().size());
        Assert.assertTrue("Wrong values in Future object", result1.get().contains("Maybe"));
        Assert.assertTrue("Wrong values in Future object", result1.get().contains("Dont know"));
        Assert.assertTrue("Wrong values in Future object", result1.get().contains("Dont care"));
        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void sendTimedAbort_acceptEventViaPopup_ensureDataUpdated() throws ExecutionException, InterruptedException {
        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ImpProposalContent content = new ImpProposalContent("Accept me", 30000);
        Future<Boolean> result1 = userFeedback.getImplicitFBAsync(ImpProposalType.TIMED_ABORT, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
//        fail("need to accept the request");
        popup.acceptTimedAbortRequest();
        popup.close();

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Value not updated in TimedAbort Future object", Boolean.TRUE, result1.get());
        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void sendTimedAbort_abortEventViaPopup_ensureDataUpdated() throws ExecutionException, InterruptedException {
        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ImpProposalContent content = new ImpProposalContent("Abort me", 30000);
        Future<Boolean> result1 = userFeedback.getImplicitFBAsync(ImpProposalType.TIMED_ABORT, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);
        UFNotificationPopup popup = indexPage.clickNotificationBubble();

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request
//        fail("need to abort the request");
        popup.abortTimedAbortRequest();
        popup.close();

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Value not updated in TimedAbort Future object", Boolean.FALSE, result1.get());
        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void sendTimedAbort_ignoreEvent_ensureDataUpdated() throws ExecutionException, InterruptedException {
        indexPage.doLogin(USERNAME, PASSWORD);

        // ensure we're starting from a clean slate
        indexPage.verifyNumberInNotificationsBubble(0);

        // send a request
        ImpProposalContent content = new ImpProposalContent("Accept me", 5000);
        Future<Boolean> result1 = userFeedback.getImplicitFBAsync(ImpProposalType.TIMED_ABORT, content);

        // ensure request has been sent
        indexPage.verifyNumberInNotificationsBubble(1);

        // ensure request hasn't been completed
        Assert.assertFalse(result1.isDone());

        // respond to request (by ignoring it)
        Thread.sleep(5000);

        // ensure data has been updated
        Date timeout = new Date(new Date().getTime() + 10000);
        while (timeout.after(new Date())) {
            if (result1.isDone())
                break;

            Thread.sleep(100);
        }

        Assert.assertTrue("Future object not updated after 10000ms", result1.isDone());
        Assert.assertEquals("Incorrect value updated in Future object", Boolean.TRUE, result1.get());
        indexPage.verifyNumberInNotificationsBubble(0);
    }

}
