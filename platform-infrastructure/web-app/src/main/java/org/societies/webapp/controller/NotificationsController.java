package org.societies.webapp.controller;

import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.osgi.event.EventTypes;
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

    private class PubSubListener implements Subscriber {
        //pubsub event schemas
        private final List<String> EVENT_SCHEMA_CLASSES =
                Collections.unmodifiableList(Arrays.asList(
                        "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
                        "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));
        private final List<String> EVENT_TYPES =
                Collections.unmodifiableList(Arrays.asList(
                        EventTypes.UF_PRIVACY_NEGOTIATION,
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP));

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

            if (EventTypes.UF_PRIVACY_NEGOTIATION.equals(node)) {
                if (log.isDebugEnabled()) {
                    String fmt = "Adding notification item of type %s with ID %s";
                    log.debug(String.format(fmt, item.getClass().getSimpleName(), itemId));
                }

                negotiationQueue.add(new NotificationQueueItem(pubsubService, node, itemId, item));
                numUnreadNotifications++;
            } else if (EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE.equals(node)
                    || EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP.equals(node)) {


                for (NotificationQueueItem nqi : negotiationQueue) {
                    if (nqi.getItemId().equals(itemId)) {
                        if (log.isDebugEnabled()) {
                            String fmt = "Removing notification item of type %s with ID %s";
                            log.debug(String.format(fmt, item.getClass().getSimpleName(), itemId));
                        }

                        numUnreadNotifications--;
                        negotiationQueue.remove(nqi);
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

    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final Queue<NotificationQueueItem> negotiationQueue = new LinkedList<NotificationQueueItem>();
    private int numUnreadNotifications;

    public NotificationsController() {
        log.trace("NotificationsController ctor()");
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

    public void resetUnreadNotifications() {
        numUnreadNotifications = 0;
    }

    public Queue<NotificationQueueItem> getNegotiationQueue() {
        return negotiationQueue;
    }
}
