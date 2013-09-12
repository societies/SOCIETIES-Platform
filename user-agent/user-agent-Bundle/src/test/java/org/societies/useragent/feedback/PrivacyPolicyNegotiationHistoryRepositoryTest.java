package org.societies.useragent.feedback;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.FeedbackStage;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PrivacyPolicyNegotiationHistoryRepositoryTest {

    private PrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationRepository;

    @Before
    public void setupTest() {
        privacyPolicyNegotiationRepository = new PrivacyPolicyNegotiationHistoryRepository();
        privacyPolicyNegotiationRepository.setSessionFactory(HibernateUtil.getSessionFactory());
        privacyPolicyNegotiationRepository.truncate();
    }

    @After
    public void tearDownTest() {
//        privacyPolicyNegotiationRepository.truncate();
    }

    @Test
    @Ignore("Ignore until we fix issue with hibernate mappings")
    public void ignore_create_get_create_list_delete_get_list() throws Exception {
//    public void create_get_create_list_delete_get_list() throws Exception {
        RequestorBean requestorBean1 = new RequestorBean();
        requestorBean1.setRequestorId("req1");

        SecureRandom random = new SecureRandom();
        String guid1 = new BigInteger(130, random).toString(32);

        ResponsePolicy responsePolicy1 = buildResponsePolicy(guid1, requestorBean1);

        NegotiationDetailsBean negotiationDetails1 = new NegotiationDetailsBean();
        negotiationDetails1.setRequestor(requestorBean1);
        negotiationDetails1.setNegotiationID(new BigInteger(130, random).intValue());

        UserFeedbackPrivacyNegotiationEvent event1 = new UserFeedbackPrivacyNegotiationEvent();
        event1.setNegotiationDetails(negotiationDetails1);
        event1.setRequestId(guid1);
        event1.setResponsePolicy(responsePolicy1);
        event1.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event1.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event1.setType(ExpProposalType.PRIVACY_NEGOTIATION);


        RequestorBean requestorBean2 = new RequestorBean();
        requestorBean2.setRequestorId("req2");

        String guid2 = new BigInteger(230, random).toString(32);

        ResponsePolicy responsePolicy2 = buildResponsePolicy(guid2, requestorBean2);

        NegotiationDetailsBean negotiationDetails2 = new NegotiationDetailsBean();
        negotiationDetails2.setRequestor(requestorBean2);
        negotiationDetails2.setNegotiationID(new BigInteger(130, random).intValue());

        UserFeedbackPrivacyNegotiationEvent event2 = new UserFeedbackPrivacyNegotiationEvent();
        event2.setNegotiationDetails(negotiationDetails2);
        event2.setRequestId(guid2);
        event2.setResponsePolicy(responsePolicy2);
        event2.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event2.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event2.setType(ExpProposalType.PRIVACY_NEGOTIATION);


        // create
        privacyPolicyNegotiationRepository.insert(event1);
        Assert.assertEquals(guid1, event1.getRequestId());

        // get
        final UserFeedbackPrivacyNegotiationEvent returnedEvent1 = privacyPolicyNegotiationRepository.getByRequestId(guid1);
        Assert.assertNotNull(returnedEvent1);
        compareItems(event1, returnedEvent1);

        // create
        privacyPolicyNegotiationRepository.insert(event2);
        Assert.assertEquals(guid2, event2.getRequestId());

        // get
        final UserFeedbackPrivacyNegotiationEvent returnedEvent2 = privacyPolicyNegotiationRepository.getByRequestId(guid2);
        Assert.assertNotNull(returnedEvent2);
        compareItems(event2, returnedEvent2);

        // list
        final List<UserFeedbackPrivacyNegotiationEvent> list1 = privacyPolicyNegotiationRepository.listIncomplete();
        Assert.assertNotNull(list1);
        Assert.assertEquals(2, list1.size());

    }

    private static void compareItems(UserFeedbackPrivacyNegotiationEvent expectedEvent, UserFeedbackPrivacyNegotiationEvent actualEvent) {
        Assert.assertEquals(expectedEvent.getMethod(), actualEvent.getMethod());
        Assert.assertEquals(expectedEvent.getType(), actualEvent.getType());
        Assert.assertEquals(expectedEvent.getRequestId(), actualEvent.getRequestId());

        ResponsePolicy expectedPolicy = expectedEvent.getResponsePolicy();
        ResponsePolicy actualPolicy = actualEvent.getResponsePolicy();

        Assert.assertNotNull(expectedPolicy);
        Assert.assertNotNull(actualPolicy);

        Assert.assertEquals(expectedPolicy.getNegotiationStatus(), actualPolicy.getNegotiationStatus());
        Assert.assertEquals(expectedPolicy.getResponsePolicyId(), actualPolicy.getResponsePolicyId());
        Assert.assertEquals(expectedPolicy.getResponseItems().size(), actualPolicy.getResponseItems().size());

        NegotiationDetailsBean expectedNegotiation = expectedEvent.getNegotiationDetails();
        NegotiationDetailsBean actualNegotiation = actualEvent.getNegotiationDetails();

        Assert.assertNotNull(expectedNegotiation);
        Assert.assertNotNull(actualNegotiation);

        Assert.assertEquals(expectedNegotiation.getNegotiationID(), actualNegotiation.getNegotiationID());
    }

    private static ResponsePolicy buildResponsePolicy(String guid, RequestorBean requestorBean) {
        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("http://this.is.a.win/", "Location"));
        responseItems.add(buildResponseItem("http://paddy.rules/", "Status"));
        responseItems.add(buildResponseItem("http://something.something.something/", "Hair colour"));

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);
        responsePolicy.setResponsePolicyId(guid);
        return responsePolicy;
    }

    private static ResponseItem buildResponseItem(String uri, String dataType) {
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action2 = new Action();
        action2.setActionConstant(ActionConstants.DELETE);
        action2.setOptional(false);
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("1");
        condition1.setOptional(false);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("2");
        condition2.setOptional(true);
        Condition condition3 = new Condition();
        condition3.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
        condition3.setValue("3");
        condition3.setOptional(false);
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

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);
        return responseItem;
    }


}
