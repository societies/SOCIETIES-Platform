package org.societies.webapp.controller;

import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.entity.NotificationQueueItem;
import org.societies.webapp.service.UserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import java.util.*;

@Controller
@Scope("session")
@Named("notifications")
@ManagedBean(name = "notifications", eager = true)
@SessionScoped
public class NotificationsController extends BasePageController {

    public static final String ABORT_STRING = "abort";

    private class PubSubListener implements Subscriber {
        //pubsub event schemas
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
                addGlobalMessage("Error subscribing to pubsub schema classes",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error subscribing to pubsub schema classes", e);
            }

            for (String eventType : EVENT_TYPES) {
                try {
                    pubsubClient.subscriberSubscribe(userService.getIdentity(),
                            eventType,
                            this);

                    if (log.isDebugEnabled())
                        log.debug("Subscribed to " + eventType + " events");

                } catch (Exception e) {
                    addGlobalMessage("Error subscribing to pubsub notifications",
                            e.getMessage(),
                            FacesMessage.SEVERITY_ERROR);
                    log.error("Error subscribing to pubsub notifications (id=" + userService.getIdentity() + " event=" + eventType, e);
                }
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
//            if (log.isDebugEnabled()) {
//                log.debug("pubsubEvent(): node=" + node + " item=" + item);
//            }

            // get the policy from the event payload
//            if (item instanceof UserFeedbackPrivacyNegotiationEvent) {
//                UserFeedbackPrivacyNegotiationEvent privEvent = (UserFeedbackPrivacyNegotiationEvent) item;
//
//            }

            if (item == null) {
                log.warn("Notification payload was null, not recording");
                return;
            }

            if (log.isDebugEnabled()) {
                String fmt = "Event type %s with ID %s";
                log.debug(String.format(fmt, item.getClass().getSimpleName(), itemId));
            }

            // create the correct notification type for the incoming event
            if (EventTypes.UF_PRIVACY_NEGOTIATION.equals(node)) {
                UserFeedbackPrivacyNegotiationEvent ppn = (UserFeedbackPrivacyNegotiationEvent) item;

                negotiationQueue.add(NotificationQueueItem.forPrivacyPolicyNotification(pubsubService, node, itemId, ppn));
                numUnreadNotifications++;

            } else if (UserFeedbackEventTopics.REQUEST.equals(node)) {

                UserFeedbackBean bean = (UserFeedbackBean) item;

                String proposalText = bean.getProposalText();
                String[] options = bean.getOptions().toArray(new String[bean.getOptions().size()]);

                NotificationQueueItem newItem;

                if (bean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {
                    switch (bean.getType()) {
                        case ExpProposalType.ACKNACK:
                            // This is an AckNack notification
                            newItem = NotificationQueueItem.forAckNack(pubsubService, node, itemId, proposalText);
                            break;

                        case ExpProposalType.CHECKBOXLIST:
                            // This is a select-many notification
                            newItem = NotificationQueueItem.forSelectMany(pubsubService, node, itemId, proposalText, options);
                            break;

                        case ExpProposalType.RADIOLIST:
                            // This is a select-one notification
                            newItem = NotificationQueueItem.forSelectOne(pubsubService, node, itemId, proposalText, options);
                            break;

                        default:
                            log.error("Unknown UserFeedbackBean type = " + bean.getType());
                            return;
                    }

                } else if (bean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
                    // This is a timed abort
                    Date timeout = new Date(new Date().getTime() + bean.getTimeout());

                    newItem = NotificationQueueItem.forTimedAbort(pubsubService, node, itemId, proposalText, timeout);

                    // add to the list of timed aborts for the watcher thread
                    synchronized (timedAbortsToWatch) {
                        timedAbortsToWatch.add(newItem);
                    }

                } else if (bean.getMethod() == FeedbackMethodType.SHOW_NOTIFICATION) {
                    // This is a simple (no response required) notification

                    newItem = NotificationQueueItem.forNotification(pubsubService, node, itemId, proposalText);

                } else {
                    log.error("Cannot handle UserFeedbackBean with method " + bean.getMethod().toString());
                    return;
                }

                negotiationQueue.add(newItem);
                numUnreadNotifications++;

            } else if (EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE.equals(node)
                    || EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP.equals(node)
                    || EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE.equals(node)
                    || EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP.equals(node)
                    || UserFeedbackEventTopics.EXPLICIT_RESPONSE.equals(node)
                    || UserFeedbackEventTopics.IMPLICIT_RESPONSE.equals(node)) {

                for (NotificationQueueItem nqi : negotiationQueue) {
                    if (!nqi.getItemId().equals(itemId)) continue;

                    if (log.isDebugEnabled()) {
                        String fmt = "Removing notification item of type %s with ID %s";
                        log.debug(String.format(fmt, item.getClass().getSimpleName(), itemId));
                    }

                    numUnreadNotifications--;
                    negotiationQueue.remove(nqi);
                    break;
                }

                // remove any timed aborts
                synchronized (timedAbortsToWatch) {
                    for (NotificationQueueItem nqi : timedAbortsToWatch) {
                        if (!nqi.getItemId().equals(itemId)) continue;

                        timedAbortsToWatch.remove(nqi);
                        break;
                    }
                }


            } else {
                if (log.isDebugEnabled()) {
                    String fmt = "Unknown event type %s with ID %s";
                    log.debug(String.format(fmt, item.getClass().getSimpleName(), itemId));
                }
            }


            // notify the user
            // TODO: Fix PrimeFaces push
//            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//            pushContext.push("/notifications", "");

            if (log.isDebugEnabled()) {
                log.debug("numUnreadNotifications=" + numUnreadNotifications);
            }
        }

        public void sendExplicitResponse(ExpFeedbackResultBean responseBean) {
            if (log.isTraceEnabled())
                log.trace("sendExplicitResponse()");

            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(),
                        UserFeedbackEventTopics.EXPLICIT_RESPONSE,
                        responseBean.getRequestId(),
                        responseBean);

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.EXPLICIT_RESPONSE + " with ID " + responseBean.getRequestId());
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed explicit UF request",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of completed explicit UF request", e);
            }
        }

        public void sendImplicitResponse(ImpFeedbackResultBean responseBean) {
            if (log.isTraceEnabled())
                log.trace("sendImplicitResponse()");

            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(),
                        UserFeedbackEventTopics.IMPLICIT_RESPONSE,
                        responseBean.getRequestId(),
                        responseBean);

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.IMPLICIT_RESPONSE + " with ID " + responseBean.getRequestId());
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed implicit UF request",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of completed implicit UF request", e);
            }
        }
    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
            if (log.isDebugEnabled())
                log.debug("userLoggedIn()");

            pubSubListener.registerForEvents();
        }

        @Override
        public void userLoggedOut() {
            if (log.isDebugEnabled())
                log.debug("userLoggedOut()");
        }
    }

    private class TimedAbortProcessor implements Runnable {

        private boolean abort = false;
        private final List<NotificationQueueItem> timedAbortsToWatch;

        public TimedAbortProcessor(List<NotificationQueueItem> timedAbortsToWatch) {
            this.timedAbortsToWatch = timedAbortsToWatch;
        }

        @Override
        public void run() {
            while (!abort) {
                try {
                    processTimedAborts();
                } catch (Exception ex) {
                    log.error("Error on timed abort processing thread", ex);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    log.error("Error sleeping on timed abort processing thread", ex);
                }
            }
        }

        public void stop() {
            abort = true;
        }

        private void processTimedAborts() {
            synchronized (timedAbortsToWatch) {
                for (int i = 0; i < timedAbortsToWatch.size(); i++) {
                    NotificationQueueItem ta = timedAbortsToWatch.get(i);

                    // check if this TA has expired
                    if (!new Date().after(ta.getTimeoutTime())) continue;

                    // the TA has expired, send the response
                    ta.setResult(null); // anything other than 'ABORT' is considered 'continue'
                    submitItem(ta.getItemId());

                    // remove from watch list
                    timedAbortsToWatch.remove(i);
                    i--;
                }
            }
        }

    }

    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();
    private final Thread timedAbortProcessorThread;

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final List<NotificationQueueItem> timedAbortsToWatch = new ArrayList<NotificationQueueItem>();
    private final Queue<NotificationQueueItem> negotiationQueue = new LinkedList<NotificationQueueItem>();
    private int numUnreadNotifications;


    public NotificationsController() {
        log.trace("NotificationsController ctor()");

        timedAbortProcessorThread = new Thread(new TimedAbortProcessor(timedAbortsToWatch));
        timedAbortProcessorThread.setName("TimedAbortProcessor");
        timedAbortProcessorThread.setDaemon(true);
        timedAbortProcessorThread.start();
    }

    @SuppressWarnings("UnusedDeclaration")
    public PubsubClient getPubsubClient() {
        return pubsubClient;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPubsubClient(PubsubClient pubsubClient) {
        this.pubsubClient = pubsubClient;
    }

    @SuppressWarnings("UnusedDeclaration")
    public UserService getUserService() {
        return userService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserService(UserService userService) {
        if (log.isDebugEnabled())
            log.debug("setUserService() = " + userService);

        if (this.userService != null) {
            this.userService.removeLoginListener(loginListener);
        }

        this.userService = userService;
        this.userService.addLoginListener(loginListener);
    }

    public int getNumUnreadNotifications() {
        return numUnreadNotifications;
    }

    public Queue<NotificationQueueItem> getNegotiationQueue() {
        return negotiationQueue;
    }

    public void submitItem(String itemId) {
        // TODO: this should probably be refactored into a UserFeedbackEventController
        // or something similar, in order to reduce coupling

        log.debug("submitItem() id=" + itemId);

        if (itemId == null) {
            log.warn("Null itemId when calling submitItem(), cannot continue");
            return;
        }

        // find the item
        NotificationQueueItem selectedItem = null;
        for (NotificationQueueItem item : negotiationQueue) {
            if (itemId.equals(item.getItemId())) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem == null) {
            log.warn("selected ID not found when calling submitItem(), cannot continue");
            return;
        }


        if (selectedItem.getType().equals(NotificationQueueItem.TYPE_ACK_NACK)
                || selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_ONE)
                || selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)) {
            ExpFeedbackResultBean responseBean = new ExpFeedbackResultBean();
            responseBean.setRequestId(selectedItem.getItemId());

            List<String> feedback = new ArrayList<String>();
            if (selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)) {
                // add all results
                Collections.addAll(feedback, selectedItem.getResults());
            } else {
                // add one result
                feedback.add(selectedItem.getResult());
            }
            responseBean.setFeedback(feedback);

            pubSubListener.sendExplicitResponse(responseBean);

        } else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_TIMED_ABORT)) {

            ImpFeedbackResultBean responseBean = new ImpFeedbackResultBean();
            responseBean.setRequestId(selectedItem.getItemId());
            responseBean.setAccepted(ABORT_STRING.equals(selectedItem.getResult()));

            pubSubListener.sendImplicitResponse(responseBean);

        } else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_NOTIFICATION)) {
            // no response is required
            // but we must manually remove item from queue
            numUnreadNotifications--;
            negotiationQueue.remove(selectedItem);

        }

    }
}
