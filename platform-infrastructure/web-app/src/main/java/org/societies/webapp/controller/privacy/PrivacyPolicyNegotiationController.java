package org.societies.webapp.controller.privacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;
import org.societies.webapp.wrappers.RequestItemWrapper;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.*;

@ManagedBean(name = "ppNegotiation")
@SessionScoped
public class PrivacyPolicyNegotiationController extends BasePageController {

	//pubsub event schemas
	private static final List<String> EVENT_SCHEMA_CLASSES = 
			Collections.unmodifiableList(Arrays.asList(
					"org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
					"org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));
	 private static Logger logging = LoggerFactory.getLogger(PrivacyPolicyNegotiationController.class);
    private class PubSubListener implements Subscriber {

        public void registerForEvents() {
        	
        	
        	
            if (logging.isDebugEnabled())
                logging.debug("registerForEvents()");

            if (getPubsubClient() == null) {
                logging.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
            	//register schema classes
            	getPubsubClient().addSimpleClasses(EVENT_SCHEMA_CLASSES);
                getPubsubClient().subscriberSubscribe(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION,
                        this);

                if (logging.isDebugEnabled())
                    logging.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION + " events");
            } catch (Exception e) {
                addGlobalMessage("Error subscribing to pubsub notifications",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                logging.error("Error subscribing to pubsub notifications (id="
                        + getUserService().getIdentity()
                        + " event=" + EventTypes.UF_PRIVACY_NEGOTIATION, e);
            }
        }

        public void sendResponse(ResponsePolicy responsePolicy, NegotiationDetailsBean negotiationDetails) {
            if (logging.isDebugEnabled())
                logging.debug("sendResponse(): privacyNegotiationResponse");

            UserFeedbackPrivacyNegotiationEvent payload = new UserFeedbackPrivacyNegotiationEvent();
            payload.setResponsePolicy(responsePolicy);
            payload.setNegotiationDetails(negotiationDetails);

            try {
                getPubsubClient().publisherPublish(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        null,
                        payload);
            } catch (Exception e) {
                addGlobalMessage("Error publishing notification of completed negotiation",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                logging.error("Error publishing notification of completed negotiation", e);
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (logging.isDebugEnabled())
                logging.debug("pubsubEvent(): node=" + node + " item=" + item);

            // get the policy from the event payload
            UserFeedbackPrivacyNegotiationEvent privEvent = (UserFeedbackPrivacyNegotiationEvent) item;
            addRequestToQueue(privEvent);


            // notify the user
//            PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//            pushContext.push("/pnb", "");
        }
    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
            if (logging.isDebugEnabled())
                logging.debug("userLoggedIn()");

            pubSubListener.registerForEvents();
        }

        @Override
        public void userLoggedOut() {
            if (logging.isDebugEnabled())
                logging.debug("userLoggedOut()");
        }
    }

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final Queue<UserFeedbackPrivacyNegotiationEvent> negotiationEventQueue = new LinkedList<UserFeedbackPrivacyNegotiationEvent>();
    private int unseenNegotiationCount = 0;
    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();

    public PrivacyPolicyNegotiationController() {
        logging.debug("PrivacyPolicyNegotiationController ctor()");
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
        if (logging.isDebugEnabled())
            logging.debug("setUserService() = " + userService);

        if (this.userService != null) {
            this.userService.removeLoginListener(loginListener);
        }

        this.userService = userService;
        this.userService.addLoginListener(loginListener);
    }

    @SuppressWarnings("MethodMayBeStatic")
    public Decision[] getDecisionOptions() {

        return new Decision[]{
                Decision.DENY,
                Decision.PERMIT
        };
    }

    @SuppressWarnings("MethodMayBeStatic")
    public String[] getAvailableConditionValues(ConditionConstants condition) {
        return PrivacyConditionsConstantValues.getValues(condition);
    }

    public int getQueuedNegotiationCount() {
        return negotiationEventQueue.size();
    }

    @SuppressWarnings("UnusedParameters")
    public void setUnseenNegotiationCount(int fakeValue) {
        // this method does nothing deliberately
    }

    public int getUnseenNegotiationCount() {
        return unseenNegotiationCount;
    }

    public ResponsePolicy getResponsePolicy() {
        UserFeedbackPrivacyNegotiationEvent event = getCurrentNegotiationEvent();

        return event != null
                ? event.getResponsePolicy()
                : null;
    }

    public UserFeedbackPrivacyNegotiationEvent getCurrentNegotiationEvent() {
        unseenNegotiationCount = 0; // if we're requesting a negotiation object, we're marking all as seen

        if (this.negotiationEventQueue.isEmpty())
            return null;

        return this.negotiationEventQueue.peek();
    }

    public void completeNegotiation() {
        logging.debug("completeNegotiation()");

        unseenNegotiationCount = 0; // if we're requesting a negotiation object, we're marking all as seen

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        // TODO: validate action check boxes
        /*for (ResponseItem response : responsePolicy.getResponseItems()) {
            for (Action action : response.getRequestItem().getActions()) {
                ActionWrapper wrapper = (ActionWrapper) action;

                logging.debug(String.format("%s: action %s selected = %s",
                        response.getRequestItem().getResource().getDataIdUri(),
                        action.getActionConstant().name(),
                        wrapper.isSelected()));

                // READ is always required
                if (!wrapper.isSelected() &&
                        (ActionConstants.READ.equals(action.getActionConstant()) || !action.isOptional())) {
                    String msg = "Action %s is required for %s";

                    addGlobalMessage("Validation failed",
                            String.format(msg, action.getActionConstant().name(), response.getRequestItem().getResource().getDataIdUri()),
                            FacesMessage.SEVERITY_WARN);
                    return;
                }

            }
        }*/

        prepareEventForTransmission(responsePolicy);

        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);

        // Remove from queue
        removeCurrentRequestFromQueue();
    }

    public void cancelNegotiation() {
        logging.debug("cancelNegotiation()");

        unseenNegotiationCount = 0; // if we're requesting a negotiation object, we're marking all as seen

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        prepareEventForTransmission(responsePolicy);

        responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

        pubSubListener.sendResponse(responsePolicy, negotiationDetails);

        //Remove from queue
        removeCurrentRequestFromQueue();
    }

//    public void validateActions(FacesContext context, UIComponent component, Object value) throws ValidatorException {
//        logging.debug("validateActions()");
//
//        ((UIInput) component).setValid(false);
//        SelectManyCheckbox checkbox= ((SelectManyCheckbox) component);
//
//        logging.debug("component=" + component);
//        logging.debug("component.value=" + Arrays.toString((String[]) checkbox.getValue()));
//        logging.debug("value=" + Arrays.toString((String[]) value));
//
//        ((UIInput) component).setValid(true);
//    }


    private void removeCurrentRequestFromQueue() {
        logging.debug("removeCurrentRequestFromQueue()");

        unseenNegotiationCount = 0; // if we're requesting a negotiation object, we're marking all as seen

        synchronized (negotiationEventQueue) {
            if (!negotiationEventQueue.isEmpty())
                negotiationEventQueue.remove();
        }
    }

    private void addRequestToQueue(UserFeedbackPrivacyNegotiationEvent event) {
        prepareEventForGUI(event);

        // queue the policy
        synchronized (negotiationEventQueue) {
            negotiationEventQueue.add(event);
        }

        unseenNegotiationCount++;
    }

    private void prepareEventForGUI(UserFeedbackPrivacyNegotiationEvent event) {
        logging.debug("prepareEventForGUI()");

		if (event.getResponsePolicy()==null){
			this.logging.debug("Policy parameter is null");
		}else{
			this.logging.debug("Policy contains: "+event.getResponsePolicy().getResponseItems().size()+" responseItems");
		}
		List<ResponseItem> responseItems = event.getResponsePolicy().getResponseItems();
        for (ResponseItem response : responseItems) {
        	this.logging.debug("Processing item: "+response.getRequestItem().getResource().getDataType());
            RequestItemWrapper request = new RequestItemWrapper(response.getRequestItem());
            response.setRequestItem(request);

            // add any missing ConditionConstants
            for (ConditionConstants constant : ConditionConstants.values()) {
                boolean found = false;

                for (Condition condition : request.getConditions()) {
                    if (constant.equals(condition.getConditionConstant())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Condition newCondition = new Condition();
                    newCondition.setConditionConstant(constant);
                    newCondition.setOptional(true);
                    newCondition.setValue("");
                    request.getConditions().add(newCondition);
                }
            }

            // quickly sort by condition name
            Collections.sort(request.getConditions(), new Comparator<Condition>() {
                @Override
                public int compare(Condition o1, Condition o2) {
                    return o1.getConditionConstant().name().compareTo(o2.getConditionConstant().name());
                }
            });
        }
    }

    private void prepareEventForTransmission(ResponsePolicy responsePolicy) {
        logging.debug("prepareEventForTransmission()");
        for (ResponseItem response : responsePolicy.getResponseItems()) {
            RequestItemWrapper requestWrapper = (RequestItemWrapper) response.getRequestItem();
            RequestItem request = requestWrapper.getRequestItem();

            // remove any optional, unset ConditionConstants
            for (int i = 0; i < request.getConditions().size(); i++) {
                Condition condition = request.getConditions().get(i);

                if (condition.isOptional() && condition.getValue() == null || "".equals(condition.getValue())) {
                    request.getConditions().remove(i);
                    i--;
                }
            }


            // Action strings need to be converted back to Actions
            // Actually we're just filtering out the unselected ones

            // we always need read, add it if we haven't got it
            if (!requestWrapper.getSelectedActionNames().contains("READ"))
                requestWrapper.getSelectedActionNames().add("READ");

            if (logging.isDebugEnabled())
                logging.debug("actions=" + Arrays.toString(requestWrapper.getSelectedActionNames().toArray()));


            for (int i = 0; i < request.getActions().size(); i++) {
                Action action = request.getActions().get(i);
                boolean found = false;

                for (String name : requestWrapper.getSelectedActionNames()) {
                    if (action.getActionConstant().name().equals(name)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    request.getActions().remove(i);
                    i--;
                }
            }

            // TODO: remove after debugging
            for (Action action : request.getActions()) {
                logging.debug("action: " + action.getActionConstant());
            }

        }
        
        clearResponseItemWrapper(responsePolicy);
    }

	private void clearResponseItemWrapper(ResponsePolicy responsePolicy) {
		for (ResponseItem item : responsePolicy.getResponseItems()){
			RequestItem oldItem = item.getRequestItem();
			RequestItem newItem = new RequestItem();
			newItem.setActions(oldItem.getActions());
			newItem.setConditions(oldItem.getConditions());
			newItem.setOptional(oldItem.isOptional());
			newItem.setResource(oldItem.getResource());
			
			item.setRequestItem(newItem);
		}
		
	}


}
