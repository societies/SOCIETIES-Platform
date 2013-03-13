package org.societies.webapp.controller.privacy;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.LinkedList;
import java.util.Queue;

@ManagedBean(name = "ppNegotiation")
@SessionScoped
public class PrivacyPolicyNegotiationController extends BasePageController {

    private class PubSubListener implements Subscriber {

        public void registerForEvents() {
            if (log.isTraceEnabled())
                log.trace("registerForEvents()");

            if (getPubsubClient() == null) {
                log.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
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

        public void sendResponse(ResponsePolicy responsePolicy, NegotiationDetailsBean negotiationDetails) {
            if (log.isTraceEnabled())
                log.trace("sendResponse(): privacyNegotiationResponse");

            UserFeedbackPrivacyNegotiationEvent payload = new UserFeedbackPrivacyNegotiationEvent();
            payload.setResponsePolicy(responsePolicy);
            payload.setNegotiationDetails(negotiationDetails);

            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        null,
                        payload);

                removeCurrentRequestFromQueue();
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed negotiation",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error publishing notification of completed negotiation", e);
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (log.isTraceEnabled())
                log.trace("pubsubEvent(): node=" + node + " item=" + item);

            // get the policy from the event payload
            UserFeedbackPrivacyNegotiationEvent privEvent = (UserFeedbackPrivacyNegotiationEvent) item;

            // queue the policy
            negotiationEventQueue.add(privEvent);

            // notify the user
            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
            pushContext.push("/pnb", "");
        }
    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
            if (log.isTraceEnabled())
                log.trace("userLoggedIn()");

            pubSubListener.registerForEvents();
        }

        @Override
        public void userLoggedOut() {
            if (log.isTraceEnabled())
                log.trace("userLoggedOut()");
        }
    }

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final Queue<UserFeedbackPrivacyNegotiationEvent> negotiationEventQueue = new LinkedList<UserFeedbackPrivacyNegotiationEvent>();
    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();

    public PrivacyPolicyNegotiationController() {
        log.trace("PrivacyPolicyNegotiationController ctor()");
    }

    public PubsubClient getPubsubClient() {
        return pubsubClient;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPubsubClient(PubsubClient pubsubClient) {
        this.pubsubClient = pubsubClient;
    }

    public UserService getUserService() {
        return userService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserService(UserService userService) {
        if (log.isTraceEnabled())
            log.trace("setUserService() = " + userService);

        if (this.userService != null) {
            this.userService.removeLoginListener(loginListener);
        }

        this.userService = userService;
        this.userService.addLoginListener(loginListener);
    }

    public Decision[] getDecisionOptions() {

        return new Decision[]{
                Decision.DENY,
                Decision.PERMIT,
                Decision.INDETERMINATE,
                Decision.NOT_APPLICABLE
        };
    }

    public ResponsePolicy getResponsePolicy() {
        UserFeedbackPrivacyNegotiationEvent event = getCurrentNegotiationEvent();

        return event != null
                ? event.getResponsePolicy()
                : null;
    }

    public UserFeedbackPrivacyNegotiationEvent getCurrentNegotiationEvent() {
        if (this.negotiationEventQueue.isEmpty())
            return null;

        return this.negotiationEventQueue.peek();
    }

    private void removeCurrentRequestFromQueue() {
        log.trace("removeCurrentRequestFromQueue()");

        if (!negotiationEventQueue.isEmpty())
            negotiationEventQueue.remove();
    }

    public void completeNegotiation() {
        log.trace("completeNegotiation()");

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        responsePolicy.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);
    }

    public void cancelNegotiation() {
        log.trace("cancelNegotiation()");

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);
    }

}
