package org.societies.webapp.controller.privacy;

import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.useragent.model.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.osgi.event.*;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.webapp.controller.BasePageController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.LinkedList;
import java.util.Queue;

@ManagedBean(name = "ppNegotiation")
@SessionScoped
public class PrivacyPolicyNegotiationController extends BasePageController {

    private class PubSubListener extends EventListener {

        @Override
        public void handleInternalEvent(InternalEvent event) {
            log.trace("checkForNotifications(): Privacy Policy Negotiation object found");

            // get the policy from the event payload
            UserFeedbackPrivacyNegotiationEvent privEvent = (UserFeedbackPrivacyNegotiationEvent) event.geteventInfo();
//            ResponsePolicy policy = privEvent.getResponsePolicy();

            // queue the policy
            negotiationEventQueue.add(privEvent);
//            addResponsePolicyToQueue(policy);

            // notify the user
            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
            pushContext.push("/pnb", "");
        }

        @Override
        public void handleExternalEvent(CSSEvent event) {
        }

        public void registerForUFeedbackEvents() {
            if (getEventMgr() == null) {
                log.error("Event manager was null, cannot register for events");
                return;
            }

            String eventFilter = "(&" +
                    "(" + CSSEventConstants.EVENT_NAME + "=privacyNegotiation)" +
                    "(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/feedback)" +
                    ")";
            getEventMgr().subscribeInternalEvent(this,
                    new String[]{EventTypes.UF_PRIVACY_NEGOTIATION},
                    eventFilter);
            log.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION + " events");

        }

        public void sendResponse(ResponsePolicy responsePolicy, NegotiationDetails negotiationDetails) {
            UserFeedbackPrivacyNegotiationEvent payload = new UserFeedbackPrivacyNegotiationEvent();
            payload.setResponsePolicy(responsePolicy);
            payload.setNegotiationDetails(negotiationDetails);
            InternalEvent event = new InternalEvent(
                    EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                    "privacyNegotiationResponse",
                    "org/societies/useragent/feedback",
                    payload);

            try {
                eventMgr.publishInternalEvent(event);

                removeCurrentRequestFromQueue();
            } catch (EMSException e) {
                log.error("Error publishing notification of completed negotiation", e);
            }
        }

    }

    @ManagedProperty(value = "#{eventMgmtRef}")
    private IEventMgr eventMgr;

    private final Queue<UserFeedbackPrivacyNegotiationEvent> negotiationEventQueue = new LinkedList<UserFeedbackPrivacyNegotiationEvent>();
    private final PubSubListener pubSubListener = new PubSubListener();

    public PrivacyPolicyNegotiationController() {
        log.trace("PrivacyPolicyNegotiationController ctor()");
    }

    public IEventMgr getEventMgr() {
        return eventMgr;
    }

    public void setEventMgr(IEventMgr eventMgr) {
//        log.trace("setEventMgr: " + eventMgr);
        this.eventMgr = eventMgr;

        pubSubListener.registerForUFeedbackEvents();
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
        if (!negotiationEventQueue.isEmpty())
            negotiationEventQueue.remove();
    }

    public void completeNegotiation() {
        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetails negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        responsePolicy.setNegotiationStatus(NegotiationStatus.SUCCESSFUL);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);
    }

    public void cancelNegotiation() {
        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetails negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);
    }

}
