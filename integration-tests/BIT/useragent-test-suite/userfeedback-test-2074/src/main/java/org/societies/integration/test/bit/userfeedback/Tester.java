package org.societies.integration.test.bit.userfeedback;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.model.UserFeedbackEventTopics;

import java.util.*;

public class Tester {

    private static final Logger log = LoggerFactory.getLogger(Tester.class);

    private class PubSubListener implements Subscriber {
        private static final long XMPP_TIMEOUT = 2000;
        public final Queue<PubSubEvent> eventQueue = new LinkedList<PubSubEvent>();

        //pubsubClient event schemas
        private final List<String> EVENT_SCHEMA_CLASSES =
                Collections.unmodifiableList(Arrays.asList(
                        "org.societies.api.schema.useragent.feedback.UserFeedbackBean",
                        "org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean",
                        "org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean",
                        "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
                        "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));
        private final List<String> EVENT_TYPES =
                Collections.unmodifiableList(Arrays.asList(
                        EventTypes.UF_PRIVACY_NEGOTIATION,
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
                        EventTypes.UF_PRIVACY_ACCESS_CONTROL,
                        EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE,
                        EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
                        UserFeedbackEventTopics.EXPLICIT_RESPONSE,
                        UserFeedbackEventTopics.IMPLICIT_RESPONSE,
                        UserFeedbackEventTopics.REQUEST));

        public void registerForEvents() {

            if (log.isDebugEnabled())
                log.debug("registerForEvents()");

            if (pubsubClient == null) {
                log.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
                //register schema classes
                pubsubClient.addSimpleClasses(EVENT_SCHEMA_CLASSES);

            } catch (Exception e) {
                log.error("Error subscribing to pubsubClient schema classes", e);
            }

            for (String eventType : EVENT_TYPES) {
                try {
                    pubsubClient.subscriberSubscribe(userID,
                            eventType,
                            this);

                    if (log.isDebugEnabled())
                        log.debug("Subscribed to " + eventType + " events");

                } catch (Exception e) {
                    log.error("Error subscribing to pubsubClient notifications (id=" + userID + " event=" + eventType, e);
                }
            }
        }

        public void unregisterForEvents() {

            if (log.isDebugEnabled())
                log.debug("unregisterForEvents()");

            if (pubsubClient == null) {
                log.error("PubSubClient was null, cannot unregister for events");
                return;
            }

            for (String eventType : EVENT_TYPES) {
                try {
                    pubsubClient.subscriberUnsubscribe(userID,
                            eventType,
                            this);

                    if (log.isDebugEnabled())
                        log.debug("Unsubscribed from " + eventType + " events");

                } catch (Exception e) {
                    log.error("Error unsubscribing from pubsubClient notifications (id=" + userID + " event=" + eventType, e);
                }
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (log.isDebugEnabled()) {
                String msg = "pubsubEvent(): ID=[%s] node=[%s] item=[%s]";
                log.debug(String.format(msg, itemId, node, item));
            }

            PubSubEvent event = new PubSubEvent(node, itemId, item);
            synchronized (eventQueue) {
                eventQueue.add(event);
            }
        }

        public void sendExplicitResponse(String requestId, List<String> results) {
            if (log.isTraceEnabled())
                log.trace("sendExplicitResponse()");

            final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
            resultBean.setRequestId(requestId);
            resultBean.setFeedback(results);

            try {
                pubsubClient.publisherPublish(userID,
                        UserFeedbackEventTopics.EXPLICIT_RESPONSE,
                        requestId,
                        resultBean);

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.EXPLICIT_RESPONSE + " with ID " + requestId);
            } catch (Exception e) {
                log.error("Error publishing notification of completed explicit UF request", e);
            }
        }

        public void sendImplicitResponse(ImpFeedbackResultBean responseBean) {
            if (log.isTraceEnabled())
                log.trace("sendImplicitResponse()");

            try {
                pubsubClient.publisherPublish(userID,
                        UserFeedbackEventTopics.IMPLICIT_RESPONSE,
                        responseBean.getRequestId(),
                        responseBean);

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.IMPLICIT_RESPONSE + " with ID " + responseBean.getRequestId());
            } catch (Exception e) {
                log.error("Error publishing notification of completed implicit UF request", e);
            }
        }

        public void clear() {
            synchronized (PubSubListener.this) {
                eventQueue.clear();
            }
        }


        private PubSubEvent waitForPubSubEvent(String requestId, String requestType) throws InterruptedException {
            PubSubEvent event = null;
            Date endDate = new Date(new Date().getTime() + XMPP_TIMEOUT);

            while (endDate.after(new Date())) {
                synchronized (eventQueue) {
                    for (PubSubEvent queuedEvent : eventQueue) {
                        if (!requestId.equals(queuedEvent.getItemId())) {
                            log.trace("Queued Event ID [" + requestId + "] does not match target id [" + queuedEvent.getItemId() + "], skipping");
                            continue;
                        }

                        if (!requestType.equals(queuedEvent.getNode())) {
                            String msg = "Found event with ID [%s] but with node [%s], expected [%s], skipping";
                            log.trace(String.format(msg,
                                    requestId,
                                    queuedEvent.getNode(),
                                    requestType));

                            continue;
                        }

                        event = queuedEvent;
                        break;
                    }

                    log.debug("Event ID [" + requestId + "] not found in queue... retrying");
                }

                // try again, giving time for the new event to arrive
                Thread.sleep(XMPP_TIMEOUT / 5L);
            }
            return event;
        }

        public void assertNotificationReceived(String requestId, int type, ExpProposalContent content) throws InterruptedException {
            List<String> expectedOptions = new ArrayList<String>();
            Collections.addAll(expectedOptions, content.getOptions());

            PubSubEvent event = waitForPubSubEvent(requestId, UserFeedbackEventTopics.REQUEST);

            Assert.assertNotNull("Event ID [" + requestId + "] with type [" + UserFeedbackEventTopics.REQUEST + "] not found after " + XMPP_TIMEOUT + "ms",
                    event);

            UserFeedbackBean bean = (UserFeedbackBean) event.getItem();

            Assert.assertEquals("Bean ID [" + requestId + "] had wrong type", type, bean.getType());

            Tester.compareLists(expectedOptions, bean.getOptions());
        }

        public void assertExpResponseReceived(String requestId, int type, List<String> expectedResults) throws InterruptedException {
            PubSubEvent event = waitForPubSubEvent(requestId, UserFeedbackEventTopics.EXPLICIT_RESPONSE);

            if (event == null)
                Assert.fail("Event ID [" + requestId + "] not found after " + XMPP_TIMEOUT + "ms");

            ExpFeedbackResultBean bean = (ExpFeedbackResultBean) event.getItem();

            Tester.compareLists(expectedResults, bean.getFeedback());
        }

        public void assertCompletedReceived(String requestId) throws InterruptedException {
            waitForPubSubEvent(requestId, UserFeedbackEventTopics.COMPLETE);
        }
    }

    private class PubSubEvent {

        private final String node;
        private final String itemId;
        private final Object item;

        public PubSubEvent(String node, String itemId, Object item) {

            this.node = node;
            this.itemId = itemId;
            this.item = item;
        }

        private String getNode() {
            return node;
        }

        private String getItemId() {
            return itemId;
        }

        private Object getItem() {
            return item;
        }
    }

    private PubsubClient pubsubClient;
    private IIdentityManager idMgr;
    private IIdentity userID;
    private IUserFeedback userFeedback;
    private PubSubListener pubSubListener;

    public Tester() {
        log.debug("Tester constructor");
    }

    @Before
    public void setUp() {
        log.debug("Setting up Tester");
        this.pubsubClient = TestUserFeedback.getPubsub();
        this.idMgr = TestUserFeedback.getIdMgr();
        this.userID = this.idMgr.getThisNetworkNode();
        this.userFeedback = TestUserFeedback.getUserFeedback();

        pubSubListener = new PubSubListener();
        pubSubListener.registerForEvents();
        log.debug("Finished setting up tester");
    }

    @After
    public void tearDown() {
        pubSubListener.unregisterForEvents();
    }

//  TODO: This test needs to be fixed, after the hibernate repositories, mysql helper, etc have been removed
//    @Test
//    public void fullTestScript() throws SQLException, InterruptedException, ExecutionException {
//
//        // NB: The following manual script was written to test full integration of UF, Android and the Webapp - only the
//        // UF component will be tested in this automated test class
//
//        //1. Empty database, clean startup
//        //      1. No notifications displayed
//        //2. Send 4x UF Explicit notification - AckNack, SelectOne, 2x SelectMany
//        //      1. Notification stored in DB
//        //      2. EXPLICIT_REQUEST PubSub event sent from server
//        //      3. 2x notifications displayed on Android, T65
//        //3. Accept AckNack notification via T65
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        //      2. Database is updated - negotiation status and results
//        //      3. COMPLETED PubSub event is sent from server
//        //      4. Correct notification disappears on Android, T65
//        //4. Accept SelectOne notification via Android
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        //      2. Database is updated - negotiation status and results
//        //      3. COMPLETED PubSub event is sent from server
//        //      4. Correct notification disappears on Android, T65
//        //5. Restart Android app
//        //      1. 2x SelectMany notification should appear on Android (others may appear, but be marked 'completed')
//        //6. Restart platform, restart Android app
//        //      1. 2x SelectMany notification should appear on Android, T65 (others may appear, but be marked 'completed')
//        //7. Accept 1x SelectMany notification via T65 (note ID)
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        //      2. Database is updated - negotiation status and results
//        //      3. COMPLETED PubSub event is sent from server
//        //      4. Correct notification disappears on Android, T65
//        //8. Accept 1x SelectMany notification via Android
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        //      2. Database is updated - negotiation status and results
//        //      3. COMPLETED PubSub event is sent from server
//        //      4. Correct notification disappears on Android, T65
//
//        //1. Empty database, clean startup
//
//        log.info("Clearing UserFeedback");
//        userFeedback.clear();
//        log.info("Clearing PubSubListener");
//        pubSubListener.clear();
//
//        //2. Send 4x UF Explicit notification - AckNack, SelectOne, 2x SelectMany
//        final ExpProposalContent acknackContent = new ExpProposalContent("AckNack test",
//                new String[]{"Yes", "No"});
//        final Future<List<String>> acknackFB = userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, acknackContent);
//        //      1. Notification stored in DB
//
//        final String acknackRequestId = mySQLHelper.assertNotificationStored(ExpProposalType.ACKNACK, acknackContent);
//        //      2. EXPLICIT_REQUEST PubSub event sent from server
//        pubSubListener.assertNotificationReceived(acknackRequestId, ExpProposalType.ACKNACK, acknackContent);
//
//        final ExpProposalContent selectOneContent = new ExpProposalContent("SelectOne test",
//                new String[]{"Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"});
//        final Future<List<String>> selectOneFB = userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, selectOneContent);
//        //      1. Notification stored in DB
//        final String selectOneRequestId = mySQLHelper.assertNotificationStored(ExpProposalType.CHECKBOXLIST, selectOneContent);
//        //      2. EXPLICIT_REQUEST PubSub event sent from server
//        pubSubListener.assertNotificationReceived(selectOneRequestId, ExpProposalType.CHECKBOXLIST, selectOneContent);
//
//        final ExpProposalContent selectManyContent1 = new ExpProposalContent("SelectMany test 1",
//                new String[]{"red", "orange", "yellow", "green", "blue", "indigo", "violet"});
//        final Future<List<String>> selectManyFB1 = userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, selectManyContent1);
//        //      1. Notification stored in DB
//        final String selectManyRequestId1 = mySQLHelper.assertNotificationStored(ExpProposalType.RADIOLIST, selectManyContent1);
//        //      2. EXPLICIT_REQUEST PubSub event sent from server
//        pubSubListener.assertNotificationReceived(selectManyRequestId1, ExpProposalType.RADIOLIST, selectManyContent1);
//
//        final ExpProposalContent selectManyContent2 = new ExpProposalContent("SelectMany test 2",
//                new String[]{"red2", "orange2", "yellow2", "green2", "blue2", "indigo2", "violet2"});
//        final Future<List<String>> selectManyFB2 = userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, selectManyContent2);
//        //      1. Notification stored in DB
//        final String selectManyRequestId2 = mySQLHelper.assertNotificationStored(ExpProposalType.RADIOLIST, selectManyContent2);
//        //      2. EXPLICIT_REQUEST PubSub event sent from server
//        pubSubListener.assertNotificationReceived(selectManyRequestId2, ExpProposalType.RADIOLIST, selectManyContent2);
//
//
//        //3. Accept AckNack notification
//        Assert.assertFalse(acknackFB.isDone());
//
//        List<String> expectedAcknackResults = new ArrayList<String>();
//        expectedAcknackResults.add("Yes");
//        pubSubListener.sendExplicitResponse(acknackRequestId, expectedAcknackResults);
//
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        pubSubListener.assertExpResponseReceived(acknackRequestId, ExpProposalType.ACKNACK, expectedAcknackResults);
//        //      2. Database is updated - negotiation status and results
//        Assert.assertEquals(FeedbackStage.COMPLETED, acknackFeedbackBean.getStage());
////        compareLists(acknackResults, acknackFeedbackBean.getOptions());
//
//        //      3. Future is updated
//        Assert.assertTrue(acknackFB.isDone());
//        final List<String> acknackResult = acknackFB.get();
//        Tester.compareLists(expectedAcknackResults, acknackResult);
//        //      4. COMPLETED PubSub event is sent from server
//        pubSubListener.assertCompletedReceived(acknackRequestId);
//
//        //4. Accept SelectOne notification
//        Assert.assertFalse(selectOneFB.isDone());
//        final List<String> expectedSelectOneResults = new ArrayList<String>();
//        expectedSelectOneResults.add("Phylum");
//        pubSubListener.sendExplicitResponse(selectOneRequestId, expectedSelectOneResults);
//
//        //      1. EXPLICIT_RESPONSE PubSub event sent from client
//        pubSubListener.assertExpResponseReceived(selectOneRequestId, ExpProposalType.RADIOLIST, expectedSelectOneResults);
//        //      2. Database is updated - negotiation status and results
//        final UserFeedbackBean selectOneFeedbackBean = userFeedbackHistoryRepository.getByRequestId(selectOneRequestId);
//        Assert.assertEquals(FeedbackStage.COMPLETED, selectOneFeedbackBean.getStage());
////        compareLists(selectOneResults, selectOneFeedbackBean.getOptions());
//
//        //      3. Future is updated
//        Assert.assertTrue(selectOneFB.isDone());
//        final List<String> selectOneResult = selectOneFB.get();
//        Tester.compareLists(expectedSelectOneResults, selectOneResult);
//        //      4. COMPLETED PubSub event is sent from server
//        pubSubListener.assertCompletedReceived(selectOneRequestId);
//
//    }

    private static <T> void compareLists(T[] expected, T[] actual) {
        List<T> leftList = new ArrayList<T>();
        Collections.addAll(leftList, expected);
        List<T> rightList = new ArrayList<T>();
        Collections.addAll(rightList, actual);

        compareLists(leftList, rightList);
    }

    private static <T> void compareLists(List<T> expected, T[] actual) {
        List<T> rightList = new ArrayList<T>();
        Collections.addAll(rightList, actual);

        compareLists(expected, rightList);
    }

    private static <T> void compareLists(T[] expected, List<T> actual) {
        List<T> leftList = new ArrayList<T>();
        Collections.addAll(leftList, expected);

        compareLists(leftList, actual);
    }

    public static <T> void compareLists(List<T> expected, List<T> actual) {
        List<T> expectedClone = new ArrayList<T>(expected);

        for (T actualItem : actual) {
            if (!expected.contains(actualItem))
                Assert.fail("Item [" + actualItem + "] found in actual list, but not expected");

            expectedClone.remove(actualItem);
        }

        if (expectedClone.size() > 0)
            Assert.fail("Item [" + expectedClone.get(0) + "] was expected, but not in actual list");
    }

}
