package org.societies.integration.tests.bit.webapp;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.integration.api.selenium.SeleniumTest;
import org.societies.integration.api.selenium.components.UFNotificationPopup;
import org.societies.integration.api.selenium.pages.IndexPage;
import org.societies.integration.api.selenium.pages.PrivacyPolicyNegotiationRequestPage;
import org.societies.integration.api.selenium.pages.TestPage;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestWebappPrivacyNegotiation extends SeleniumTest {

    private static final Logger log = LoggerFactory.getLogger(TestWebappPrivacyNegotiation.class);

    private static final String USERNAME = "paddy";
    private static final String PASSWORD = "p";

    private IndexPage indexPage;

    private IUserFeedback userFeedback;

    private final SecureRandom random = new SecureRandom();

    public TestWebappPrivacyNegotiation() {
        log.debug("TestWebappUserFeedback ctor()");
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
    public void eventsAppearAtLogin() {
        String requestId = UUID.randomUUID().toString();

        Future<ResponsePolicy> responsePolicyFuture = buildAndSendPPN(requestId);

        Assert.assertFalse(responsePolicyFuture.isDone());

        indexPage.doLogin(USERNAME, PASSWORD);

        int number = indexPage.getNumberInNotificationsBubble();
        // there may already be some notifications in the bubble
        Assert.assertTrue(number >= 1);

        indexPage.clickNotificationBubble();

        Assert.assertFalse(responsePolicyFuture.isDone());

        log.debug("Test complete");
    }

    @Test
    public void canAcceptPPN() throws ExecutionException, InterruptedException {

        String requestId = UUID.randomUUID().toString();

        // login, check no notifications existing
        indexPage.doLogin(USERNAME, PASSWORD);

        TestPage testPage = indexPage.navigateToTestPage();
        testPage.clickResetUFButton();

        testPage.verifyNumberInNotificationsBubble(0);

        // send PPN
        Future<ResponsePolicy> responsePolicyFuture = buildAndSendPPN(requestId);
        Assert.assertFalse(responsePolicyFuture.isDone());

        // ensure notification displayed
        testPage.verifyNumberInNotificationsBubble(1);

        // switch to the notification page
        UFNotificationPopup ufNotificationPopup = indexPage.clickNotificationBubble();
        PrivacyPolicyNegotiationRequestPage ppnPage = ufNotificationPopup.clickPPNLink(requestId);

        Assert.assertFalse(responsePolicyFuture.isDone());

        ppnPage.clickAcceptPpnButton();

        Assert.assertTrue(responsePolicyFuture.isDone());

        ResponsePolicy policy = responsePolicyFuture.get();
        Assert.assertEquals(NegotiationStatus.ONGOING, policy.getNegotiationStatus());

        testPage.verifyNumberInNotificationsBubble(0);

        log.debug("Test complete");
    }

    @Test
    public void canCancelPPN() throws ExecutionException, InterruptedException {

        String requestId = UUID.randomUUID().toString();

        // login, check no notifications existing
        indexPage.doLogin(USERNAME, PASSWORD);

        TestPage testPage = indexPage.navigateToTestPage();
        testPage.clickResetUFButton();

        testPage.verifyNumberInNotificationsBubble(0);

        // send PPN
        Future<ResponsePolicy> responsePolicyFuture = buildAndSendPPN(requestId);
        Assert.assertFalse(responsePolicyFuture.isDone());

        // ensure notification displayed
        testPage.verifyNumberInNotificationsBubble(1);

        // switch to the notification page
        UFNotificationPopup ufNotificationPopup = indexPage.clickNotificationBubble();
        PrivacyPolicyNegotiationRequestPage ppnPage = ufNotificationPopup.clickPPNLink(requestId);

        Assert.assertFalse(responsePolicyFuture.isDone());

        ppnPage.clickCancelPpnButton();

        Assert.assertTrue(responsePolicyFuture.isDone());

        ResponsePolicy policy = responsePolicyFuture.get();
        Assert.assertEquals(NegotiationStatus.FAILED, policy.getNegotiationStatus());

        testPage.verifyNumberInNotificationsBubble(0);

        log.debug("Test complete");
    }

    private Future<ResponsePolicy> buildAndSendPPN(String requestID) {
        String requestorId = UUID.randomUUID().toString();
        int negotiationId = new BigInteger(130, random).intValue();

        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId(requestorId);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(negotiationId);

        ResponsePolicy responsePolicy = buildResponsePolicy(requestorBean);

        return userFeedback.getPrivacyNegotiationFBAsync(requestID, responsePolicy, negotiationDetails, null);
    }

    private static ResponsePolicy buildResponsePolicy(RequestorBean requestorBean) {
        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("http://this.is.a.win/", "winning"));
        responseItems.add(buildResponseItem("http://paddy.rules/", "paddy"));
        responseItems.add(buildResponseItem("http://something.something.something/", "dark side"));

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);
        return responsePolicy;
    }

    private static ResponseItem buildResponseItem(String uri, String dataType) {
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action2 = new Action();
        action2.setActionConstant(ActionConstants.DELETE);
        action2.setOptional(true);
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(true);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("1");
        condition1.setOptional(true);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("2");
        condition2.setOptional(true);
        Condition condition3 = new Condition();
        condition3.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
        condition3.setValue("3");
        condition3.setOptional(true);
        Condition condition4 = new Condition();
        condition4.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
        condition4.setValue("4");
        condition4.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri(uri);
        resource.setDataType(dataType);

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action2);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);
        requestItem.getConditions().add(condition3);
        requestItem.getConditions().add(condition4);

        requestItem.setOptional(true);
        requestItem.setResource(resource);

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);
        return responseItem;
    }


}
