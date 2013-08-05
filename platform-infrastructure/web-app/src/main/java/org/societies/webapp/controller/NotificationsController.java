package org.societies.webapp.controller;

import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.useragent.feedback.*;
import org.societies.useragent.api.feedback.IPrivacyPolicyNegotiationHistoryRepository;
import org.societies.useragent.api.feedback.IUserFeedbackHistoryRepository;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.entity.NotificationQueueItem;
import org.societies.webapp.service.UserService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
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
                        UserFeedbackEventTopics.REQUEST,
                        UserFeedbackEventTopics.COMPLETE));

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
                String fmt = "Event type %s, payload type %s, with message ID %s";
                log.debug(String.format(fmt, node, item.getClass().getSimpleName(), itemId));
            }

            // create the correct notification type for the incoming event
            if (EventTypes.UF_PRIVACY_NEGOTIATION.equals(node)) {
                UserFeedbackPrivacyNegotiationEvent ppn = (UserFeedbackPrivacyNegotiationEvent) item;
                NotificationQueueItem newItem = NotificationQueueItem.forPrivacyPolicyNotification(String.valueOf(ppn.getRequestId()), ppn);

                addItemToQueue(newItem);

            } else if (UserFeedbackEventTopics.REQUEST.equals(node)) {

                if (!(item instanceof UserFeedbackBean)) {
                    log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackBean ",
                            node,
                            itemId,
                            item.getClass().getCanonicalName()
                    ));
                    return;
                }

                UserFeedbackBean bean = (UserFeedbackBean) item;
                NotificationQueueItem newItem = createNotificationQueueItemFromUserFeedbackBean(bean);

                // if we get a null item back, something has gone wrong and we've already logged the error
                if (newItem == null)
                    return;

                if (bean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
                    // This is a timed abort - add to the list of timed aborts for the watcher thread
                    synchronized (timedAbortsToWatch) {
                        timedAbortsToWatch.add(newItem);
                    }
                }

                addItemToQueue(newItem);

            } else if (EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE.equals(node)
                    || EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP.equals(node)
                    || EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE.equals(node)
                    || EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP.equals(node)
                    || UserFeedbackEventTopics.EXPLICIT_RESPONSE.equals(node)
                    || UserFeedbackEventTopics.IMPLICIT_RESPONSE.equals(node)
                    || UserFeedbackEventTopics.COMPLETE.equals(node)) {

                if (item instanceof UserFeedbackBean) {
                    String id = ((UserFeedbackBean) item).getRequestId();
                    String[] options = ((UserFeedbackBean) item).getOptions().toArray(new String[((UserFeedbackBean) item).getOptions().size()]);

                    if (log.isDebugEnabled())
                        log.debug(String.format("Received %s event for [%s] with options {%s}",
                                node,
                                id,
                                Arrays.toString(options)));

                    markQueueItemComplete(id, options);
                } else if (item instanceof ExpFeedbackResultBean) {
                    String id = ((ExpFeedbackResultBean) item).getRequestId();
                    String[] options = ((ExpFeedbackResultBean) item).getFeedback().toArray(new String[((ExpFeedbackResultBean) item).getFeedback().size()]);

                    if (log.isDebugEnabled())
                        log.debug(String.format("Received %s event for [%s] with options {%s}",
                                node,
                                id,
                                Arrays.toString(options)));

                    markQueueItemComplete(id, options);
                } else if (item instanceof ImpFeedbackResultBean) {
                    String id = ((ImpFeedbackResultBean) item).getRequestId();
                    String[] options = new String[]{((ImpFeedbackResultBean) item).isAccepted() ? "true" : "false"};
                    markQueueItemComplete(id, options);
                } else if (item instanceof UserFeedbackPrivacyNegotiationEvent) {
                    String id = String.valueOf(((UserFeedbackPrivacyNegotiationEvent) item).getRequestId());

                    if (log.isDebugEnabled())
                        log.debug(String.format("Received %s event for [%s] with options {%s}",
                                node,
                                id,
                                "null"));

                    markQueueItemComplete(id, null);
                } else {
                    log.warn(String.format("Unknown response payload type %s, attempting to remove by message ID", item.getClass().getSimpleName()));

                    if (log.isDebugEnabled())
                        log.debug(String.format("Received %s event for [%s] with options {%s}",
                                node,
                                itemId,
                                "null"));

                    markQueueItemComplete(itemId, new String[]{});
                }

            } else {
                String fmt = "Unknown event type %s with ID %s";
                log.warn(String.format(fmt, item.getClass().getSimpleName(), itemId));
            }


            // notify the user
            // TODO: Fix PrimeFaces push
//            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//            pushContext.push("/notifications", "");

            if (log.isDebugEnabled()) {
                log.debug("numUnansweredNotifications=" + getNumUnansweredNotifications());
            }
        }

    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
            if (log.isDebugEnabled())
                log.debug("userLoggedIn()");

            pubSubListener.registerForEvents();

            // pre-populate the list of notifications
            reloadFromRepository(DEFAULT_FETCH_COUNT);
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
                    markQueueItemComplete(ta.getItemId(), new String[]{"false"});
                    i--;
                }
            }
        }

    }

    public static final String ABORT_STRING = "abort";
    public static final int DEFAULT_FETCH_COUNT = 50;

    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();
    @SuppressWarnings("FieldCanBeLocal")
    private final Thread timedAbortProcessorThread;

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{userFeedback}")
    private IUserFeedback userFeedback;

    @ManagedProperty(value = "#{userFeedbackHistoryRepository}")
    private IUserFeedbackHistoryRepository userFeedbackHistoryRepository;

    @ManagedProperty(value = "#{privacyPolicyNegotiationHistoryRepository}")
    private IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository;

    private final List<NotificationQueueItem> timedAbortsToWatch = new ArrayList<NotificationQueueItem>();
    // NB: to avoid deadlocks, always synchronise on negotiationQueue, not on queueIDs
    private final List<NotificationQueueItem> unansweredNotifications = new LinkedList<NotificationQueueItem>();
    private final Set<String> unansweredNotificationIDs = new HashSet<String>();
    private final List<NotificationQueueItem> allNotifications = new LinkedList<NotificationQueueItem>();
    private final Set<String> allNotificationIDs = new HashSet<String>();

    public NotificationsController() {
        log.debug("NotificationsController ctor()");

        timedAbortProcessorThread = new Thread(new TimedAbortProcessor(timedAbortsToWatch));
        timedAbortProcessorThread.setName("TimedAbortProcessor");
        timedAbortProcessorThread.setDaemon(true);
    }

    @SuppressWarnings("MethodMayBeStatic")
    public boolean isDebugMode() {
        return true;
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

    @SuppressWarnings("UnusedDeclaration")
    public IPrivacyPolicyNegotiationHistoryRepository getPrivacyPolicyNegotiationHistoryRepository() {
        return privacyPolicyNegotiationHistoryRepository;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPrivacyPolicyNegotiationHistoryRepository(IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository) {
        if (log.isDebugEnabled())
            log.debug("setPrivacyPolicyNegotiationHistoryRepository() = " + privacyPolicyNegotiationHistoryRepository);

        this.privacyPolicyNegotiationHistoryRepository = privacyPolicyNegotiationHistoryRepository;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IUserFeedbackHistoryRepository getUserFeedbackHistoryRepository() {
        return userFeedbackHistoryRepository;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserFeedbackHistoryRepository(IUserFeedbackHistoryRepository userFeedbackHistoryRepository) {
        if (log.isDebugEnabled())
            log.debug("setUserFeedbackHistoryRepository() = " + userFeedbackHistoryRepository);

        this.userFeedbackHistoryRepository = userFeedbackHistoryRepository;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserFeedback(IUserFeedback userFeedback) {
        this.userFeedback = userFeedback;
    }

    @PostConstruct
    public void postConstruct() {
        timedAbortProcessorThread.start();

        // NB: Generally you DON'T want to use this method to set up your class - you want to use the LoginListener
        // - This method is called whenever the bean is created at the start of the session, while the login listener
        // - is called when the user actually logs in and an identity is available

        // call this in case we're set up after the user has logged in
        if (userService.isUserLoggedIn()) {
            loginListener.userLoggedIn();
        }
    }

    public int getNumUnansweredNotifications() {
        return unansweredNotifications.size();
    }

    public List<NotificationQueueItem> getUnansweredNegotiationQueue() {
        return unansweredNotifications;
    }

    public List<NotificationQueueItem> getAllNotificationsQueue() {
        return allNotifications;
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
        for (NotificationQueueItem item : unansweredNotifications) {
            if (itemId.equals(item.getItemId())) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem == null) {
            log.warn("selected ID not found when calling submitItem(), cannot continue");
            return;
        }

        selectedItem.setComplete(true);

        if (selectedItem.getType().equals(NotificationQueueItem.TYPE_ACK_NACK)
                || selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_ONE)
                || selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)) {

            List<String> feedback = new ArrayList<String>();
            if (selectedItem.getType().equals(NotificationQueueItem.TYPE_SELECT_MANY)) {
                // add all results
                Collections.addAll(feedback, selectedItem.getResults());
            } else {
                // add one result
                feedback.add(selectedItem.getResult());
            }

            try {
                userFeedback.submitExplicitResponse(selectedItem.getItemId(), feedback);

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.EXPLICIT_RESPONSE + " with ID " + selectedItem.getItemId());
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed explicit UF request",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of completed explicit UF request", e);
            }

        } else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_TIMED_ABORT)) {

            try {
                userFeedback.submitImplicitResponse(selectedItem.getItemId(), ABORT_STRING.equals(selectedItem.getResult()));

                if (log.isDebugEnabled())
                    log.debug("Sent " + UserFeedbackEventTopics.IMPLICIT_RESPONSE + " with ID " + selectedItem.getItemId());
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed implicit UF request",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of completed implicit UF request", e);
            }

        } else if (selectedItem.getType().equals(NotificationQueueItem.TYPE_NOTIFICATION)) {
            // no response is required
            // but we must manually remove item from queue
            markQueueItemComplete(selectedItem.getItemId(), new String[]{});
        }

    }

    public void reloadFromRepository(int howMany) {
        if (log.isDebugEnabled())
            log.debug("Loading most recent " + howMany + " notifications");

        List<UserFeedbackBean> userFeedbackBeans;
        List<UserFeedbackPrivacyNegotiationEvent> userFeedbackPrivacyNegotiationEvents;

        try {
            userFeedbackBeans = userFeedbackHistoryRepository.listPrevious(howMany);
        } catch (Exception ex) {
            log.warn("Recoverable error: Error recalling UF records: " + ex.getMessage());
            userFeedbackBeans = new ArrayList<UserFeedbackBean>();
        }
        try {
            userFeedbackPrivacyNegotiationEvents = privacyPolicyNegotiationHistoryRepository.listPrevious(howMany);
        } catch (Exception ex) {
            log.warn("Recoverable error: Error recalling PPN records: " + ex.getMessage());
            userFeedbackPrivacyNegotiationEvents = new ArrayList<UserFeedbackPrivacyNegotiationEvent>();
        }

        replaceCacheWithList(userFeedbackBeans, userFeedbackPrivacyNegotiationEvents);
    }

    public void reloadFromRepository(Date sinceWhen) {
        if (log.isDebugEnabled())
            log.debug("Loading notifications since " + sinceWhen);

        List<UserFeedbackBean> userFeedbackBeans;
        List<UserFeedbackPrivacyNegotiationEvent> userFeedbackPrivacyNegotiationEvents;

        try {
            userFeedbackBeans = userFeedbackHistoryRepository.listSince(sinceWhen);
        } catch (Exception ex) {
            log.warn("Recoverable error: Error recalling UF records: " + ex.getMessage());
            userFeedbackBeans = new ArrayList<UserFeedbackBean>();
        }
        try {
            userFeedbackPrivacyNegotiationEvents = privacyPolicyNegotiationHistoryRepository.listSince(sinceWhen);
        } catch (Exception ex) {
            log.warn("Recoverable error: Error recalling PPN records: " + ex.getMessage());
            userFeedbackPrivacyNegotiationEvents = new ArrayList<UserFeedbackPrivacyNegotiationEvent>();
        }

        replaceCacheWithList(userFeedbackBeans, userFeedbackPrivacyNegotiationEvents);
    }

    private void replaceCacheWithList(List<UserFeedbackBean> ufList, List<UserFeedbackPrivacyNegotiationEvent> ppnList) {
        synchronized (allNotifications) {
            synchronized (unansweredNotifications) {
                if (log.isDebugEnabled())
                    log.debug("Replacing cache with lists");

                allNotifications.clear();
                unansweredNotifications.clear();
                allNotificationIDs.clear();

                for (UserFeedbackBean uf : ufList) {
                    NotificationQueueItem item = createNotificationQueueItemFromUserFeedbackBean(uf);
                    addItemToQueue(item);
                }
                for (UserFeedbackPrivacyNegotiationEvent ppn : ppnList) {
                    NotificationQueueItem item = NotificationQueueItem.forPrivacyPolicyNotification(ppn.getRequestId(), ppn);
                    addItemToQueue(item);
                }
            }
        }
    }

    private NotificationQueueItem createNotificationQueueItemFromUserFeedbackBean(UserFeedbackBean bean) {
        String proposalText = bean.getProposalText();
        String[] options = bean.getOptions().toArray(new String[bean.getOptions().size()]);

        NotificationQueueItem newItem;

        if (bean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {
            switch (bean.getType()) {
                case ExpProposalType.ACKNACK:
                    // This is an AckNack notification
                    newItem = NotificationQueueItem.forAckNack(bean.getRequestId(), proposalText, options);
                    break;

                case ExpProposalType.CHECKBOXLIST:
                    // This is a select-many notification
                    newItem = NotificationQueueItem.forSelectMany(bean.getRequestId(), proposalText, options);
                    break;

                case ExpProposalType.RADIOLIST:
                    // This is a select-one notification
                    newItem = NotificationQueueItem.forSelectOne(bean.getRequestId(), proposalText, options);
                    break;

                default:
                    log.error("Unknown UserFeedbackBean type = " + bean.getType());
                    return null;
            }

        } else if (bean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
            // This is a timed abort
            Date timeout = new Date(new Date().getTime() + bean.getTimeout());

            newItem = NotificationQueueItem.forTimedAbort(bean.getRequestId(), proposalText, timeout);

        } else if (bean.getMethod() == FeedbackMethodType.SHOW_NOTIFICATION) {
            // This is a simple (no response required) notification

            newItem = NotificationQueueItem.forNotification(bean.getRequestId(), proposalText);

        } else {
            log.error("Cannot handle UserFeedbackBean with method " + bean.getMethod().toString());
            return null;
        }

        if (bean.getStage() == FeedbackStage.COMPLETED) {
            newItem.setComplete(true);
            newItem.setResults(options);
        }

        return newItem;
    }

    private void addItemToQueue(NotificationQueueItem item) {
        synchronized (allNotifications) {
            if (allNotificationIDs.contains(item.getItemId())) {
                log.warn("NQI event ID " + item.getItemId() + " already in cache - ignoring");
                return;
            }

            if (log.isDebugEnabled())
                log.debug("Adding NQI event ID [" + item.getItemId() + "] to cache");

            allNotificationIDs.add(item.getItemId());
            allNotifications.add(item);
            Collections.sort(allNotifications);

            if (!item.isComplete()) {
                if (log.isDebugEnabled())
                    log.debug("NQI event ID [" + item.getItemId() + "] is not completed, adding to unanswered cache");

                synchronized (unansweredNotifications) {
                    unansweredNotificationIDs.add(item.getItemId());
                    unansweredNotifications.add(item);
                    Collections.sort(unansweredNotifications);
                }
            }
        }
    }

    private void markQueueItemComplete(String itemId, String[] results) {
        if (log.isDebugEnabled()) {
            String fmt = "Completing notification item ID %s";
            log.debug(String.format(fmt, itemId));
        }

        // NB: All incomplete notifications should be in the unanswered queue, and only incomplete ones should be in this queue
        synchronized (unansweredNotifications) {
            for (NotificationQueueItem nqi : allNotifications) {
                if (!nqi.getItemId().equals(itemId)) continue;

                if (log.isDebugEnabled()) {
                    String fmt = "Removing notification item of type %s with ID %s";
                    log.debug(String.format(fmt, nqi.getType(), itemId));
                }

                synchronized (nqi) {
                    nqi.setResults(results);
                    nqi.setComplete(true);
                    unansweredNotifications.remove(nqi);
                    unansweredNotificationIDs.remove(itemId);
                }

                break;
            }
        }

        // remove any timed aborts
        synchronized (timedAbortsToWatch) {
            for (NotificationQueueItem nqi : timedAbortsToWatch) {
                if (!nqi.getItemId().equals(itemId)) continue;

                timedAbortsToWatch.remove(nqi);
                break;
            }
        }


    }


}
