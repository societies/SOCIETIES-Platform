package org.societies.webapp.service;

import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.*;

@ManagedBean
//@ApplicationScoped // no reason why this can't be application scoped if we require a 32-digit GUID in order to access items
// NB: yes there is, user service and pubsub service have shorter lifespans
@SessionScoped
public class PrivacyPolicyNegotiationListener extends BasePageController {

    //pubsub event schemas
    private static final List<String> EVENT_SCHEMA_CLASSES =
            Collections.unmodifiableList(Arrays.asList(
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));

    private class PubSubListener implements Subscriber {

        public void registerForEvents() {


            if (log.isDebugEnabled())
                log.debug("registerForEvents()");

            if (getPubsubClient() == null) {
                log.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
                //register schema classes
                getPubsubClient().addSimpleClasses(EVENT_SCHEMA_CLASSES);
                getPubsubClient().subscriberSubscribe(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION,
                        this);

                if (log.isDebugEnabled())
                    log.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION + " events");
            } catch (Exception e) {
                addGlobalMessage("Error subscribing to pubsub notifications",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error subscribing to pubsub notifications (id="
                        + getUserService().getIdentity()
                        + " event=" + EventTypes.UF_PRIVACY_NEGOTIATION, e);
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (item == null) {
                log.warn("pubsubEvent() with NULL item on node {} with message ID {}", node, itemId);
                return;
            }

            if (log.isDebugEnabled()) {
                String fmt = "pubsubEvent() item of type %s with message ID %s";

                log.debug(String.format(fmt,
                        item.getClass().getSimpleName(),
                        itemId
                ));

            }

            // get the policy from the event payload
            UserFeedbackPrivacyNegotiationEvent privEvent = (UserFeedbackPrivacyNegotiationEvent) item;
            addRequestToQueue(String.valueOf(privEvent.getRequestId()), privEvent);


            // notify the user
//            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//            pushContext.push("/pnb", "");
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

    @ManagedProperty(value = "#{userFeedback}")
    private IUserFeedback userFeedback;

    private final Map<String, UserFeedbackPrivacyNegotiationEvent> negotiationEventQueue = new HashMap<String, UserFeedbackPrivacyNegotiationEvent>();

    public PrivacyPolicyNegotiationListener() {
        log.debug("PrivacyPolicyNegotiationListener ctor()");
    }

    @PostConstruct
    public void postConstruct() {
        // NB: Generally you DON'T want to use this method to set up your class - you want to use the LoginListener
        // - This method is called whenever the bean is created at the start of the session, while the login listener
        // - is called when the user actually logs in and an identity is available

        // call this in case we're set up after the user has logged in
        if (userService.isUserLoggedIn()) {
            loginListener.userLoggedIn();
        }
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
    public IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserFeedback(IUserFeedback userFeedback) {
        this.userFeedback = userFeedback;
    }

    public int getQueuedNegotiationCount() {
        return negotiationEventQueue.size();
    }

    public UserFeedbackPrivacyNegotiationEvent getNegotiationEvent(String itemId) {
        return negotiationEventQueue.get(itemId);
    }

    public void completeNegotiation(String itemId) {
        log.debug("completeNegotiation() id=" + itemId);

        ResponsePolicy responsePolicy;
        NegotiationDetailsBean negotiationDetails;

        synchronized (negotiationEventQueue) {
            responsePolicy = negotiationEventQueue.get(itemId).getResponsePolicy();
            negotiationDetails = negotiationEventQueue.get(itemId).getNegotiationDetails();
        }

        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);

        try {
            userFeedback.submitPrivacyNegotiationResponse(itemId, negotiationDetails, responsePolicy);

            if (log.isDebugEnabled())
                log.debug("Sent " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + " with ID " + itemId);
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed negotiation",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }

        // Remove from queue
        removeRequestFromQueue(itemId);
    }

    public void cancelNegotiation(String itemId) {
        log.debug("cancelNegotiation() id=" + itemId);

        ResponsePolicy responsePolicy;
        NegotiationDetailsBean negotiationDetails;

        synchronized (negotiationEventQueue) {
            responsePolicy = negotiationEventQueue.get(itemId).getResponsePolicy();
            negotiationDetails = negotiationEventQueue.get(itemId).getNegotiationDetails();
        }

        responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

        try {
            userFeedback.submitPrivacyNegotiationResponse(itemId, negotiationDetails, responsePolicy);

            if (log.isDebugEnabled())
                log.debug("Sent " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + " with ID " + itemId);
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed negotiation",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }

        //Remove from queue
        removeRequestFromQueue(itemId);
    }

    public String getNextNegotiationID() {
        if (getQueuedNegotiationCount() == 0)
            return null;

        // return the first item from the queue
        return negotiationEventQueue.keySet().iterator().next();
    }


    private void removeRequestFromQueue(String itemId) {
        log.debug("removeCurrentRequestFromQueue()");

        synchronized (negotiationEventQueue) {
            if (negotiationEventQueue.containsKey(itemId))
                negotiationEventQueue.remove(itemId);
        }
    }

    private void addRequestToQueue(String id, UserFeedbackPrivacyNegotiationEvent event) {
        // queue the policy
        synchronized (negotiationEventQueue) {
            negotiationEventQueue.put(id, event);
        }
    }

}
