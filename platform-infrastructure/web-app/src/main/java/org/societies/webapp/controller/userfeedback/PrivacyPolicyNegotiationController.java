package org.societies.webapp.controller.userfeedback;

import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.wrappers.RequestItemWrapper;
import org.societies.webapp.wrappers.ResponseItemWrapper;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "ppNegotiation", eager = true)
@ViewScoped
public class PrivacyPolicyNegotiationController extends BasePageController {

    private static String getIdFromQueryString() {
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (hsr.getParameter("id") != null)
            return hsr.getParameter("id");

        return "";
    }

    @ManagedProperty(value = "#{notifications}")
    private NotificationsController notificationsController;

    @ManagedProperty(value = "#{userFeedback}")
    private IUserFeedback userFeedback;

    private String eventID;
    private UserFeedbackPrivacyNegotiationEvent event;
    private ConditionConstants newConditionToAdd;

    public PrivacyPolicyNegotiationController() {
        if (log.isDebugEnabled())
            log.debug("PrivacyPolicyNegotiationController ctor()");

    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    public IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(IUserFeedback userFeedback) {
        this.userFeedback = userFeedback;
    }

    @PostConstruct
    public void initMethod() {
        if (log.isDebugEnabled())
            log.debug("init()");
        eventID = getIdFromQueryString();
        event = notificationsController.getPrivacyNegotiationEvent(eventID);

        if (event != null) {
            if (log.isDebugEnabled())
                log.debug("Preparing event for GUI with ID " + eventID);

            prepareEventForGUI(event);
        } else {
            log.warn("Event not found for ID " + eventID);
        }
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

    @SuppressWarnings("MethodMayBeStatic")
    public List<ConditionConstants> getAvailableConditionConstants(RequestItem requestItem) {
        List<ConditionConstants> availableConstants = new ArrayList<ConditionConstants>();

        // add any missing ConditionConstants
        for (ConditionConstants constant : ConditionConstants.values()) {
            boolean found = false;

            for (Condition condition : requestItem.getConditions()) {
                if (constant.equals(condition.getConditionConstant())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                availableConstants.add(constant);
            }
        }

        return availableConstants;
    }

    public ResponsePolicy getResponsePolicy() {
        return event != null
                ? event.getResponsePolicy()
                : null;
    }

    public UserFeedbackPrivacyNegotiationEvent getCurrentNegotiationEvent() {
        return event;
    }

    public void addNewCondition(RequestItem selectedRequestItem) {
        if (newConditionToAdd == null) {
            log.warn("newConditionToAdd is null");
            return;
        }
        if (selectedRequestItem == null) {
            log.warn("selectedRequestItem is null");
            return;
        }

        Condition condition = new Condition();
        condition.setOptional(true);
        condition.setConditionConstant(newConditionToAdd);
        condition.setValue("");

        selectedRequestItem.getConditions().add(condition);

        log.debug("Adding condition {} to request item for {}",
                new String[]{newConditionToAdd.name(), selectedRequestItem.getResource().getDataType()});

        newConditionToAdd = null;
    }

    public String completeNegotiationAction() {
        log.debug("completeNegotiation() id=" + eventID);

        ResponsePolicy responsePolicy = event.getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = event.getNegotiationDetails();

        // TODO: validate action check boxes
        /*for (ResponseItem response : responsePolicy.getResponseItems()) {
            for (Action action : response.getRequestItem().getActions()) {
                ActionWrapper wrapper = (ActionWrapper) action;

                log.debug(String.format("%s: action %s selected = %s",
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

        try {
            userFeedback.submitPrivacyNegotiationResponse(eventID, negotiationDetails, responsePolicy);
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed negotiation",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }

        return "home"; // previously, could redirect to next negotiation - but this makes no sense now
    }

    public String cancelNegotiationAction() {
        log.debug("cancelNegotiation()");

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        prepareEventForTransmission(responsePolicy);

        responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

        try {
            userFeedback.submitPrivacyNegotiationResponse(eventID, negotiationDetails, responsePolicy);
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of cancelled privacy negotiation event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of cancelled privacy negotiation event", e);
        }

        return "home"; // previously, could redirect to next negotiation - but this makes no sense now
    }

    private static void prepareEventForGUI(UserFeedbackPrivacyNegotiationEvent event) {
        ResponseItemWrapper.wrapList(event.getResponsePolicy().getResponseItems());

        for (ResponseItem response : event.getResponsePolicy().getResponseItems()) {

            // wrap the sub items
            if (!(response.getRequestItem() instanceof RequestItemWrapper))
                response.setRequestItem(new RequestItemWrapper(response.getRequestItem()));

            // set to permit by default - the user can then change
            response.setDecision(Decision.PERMIT);

            // quickly sort by condition name
            Collections.sort(response.getRequestItem().getConditions(), new Comparator<Condition>() {
                @Override
                public int compare(Condition o1, Condition o2) {
                    return o1.getConditionConstant().name().compareTo(o2.getConditionConstant().name());
                }
            });

        }

    }

    private static void prepareEventForTransmission(ResponsePolicy responsePolicy) {
        // Convert ResponseItemWrappers back to ResponseItems if necessary
        ResponseItemWrapper.unwrapList(responsePolicy.getResponseItems());

        for (ResponseItem response : responsePolicy.getResponseItems()) {

            RequestItemWrapper requestItemWrapper = (RequestItemWrapper) response.getRequestItem();

            // Action strings need to be converted back to Actions
            // Actually we're just filtering out the unselected ones

            // we always need read, add it if we haven't got it
            if (!requestItemWrapper.getSelectedActionNames().contains("READ"))
                requestItemWrapper.getSelectedActionNames().add("READ");

            // upon return, the "Actions" field should only contain selected actions
            requestItemWrapper.setActions(requestItemWrapper.getSelectedActions());

            // unwrap the sub items
            RequestItem requestItem;
            if (response.getRequestItem() instanceof RequestItemWrapper) {
                requestItem = ((RequestItemWrapper) response.getRequestItem()).getRequestItem();
                response.setRequestItem(requestItem);
            } else {
                requestItem = response.getRequestItem();
            }

            // remove any optional, unset ConditionConstants
            for (int i = 0; i < requestItem.getConditions().size(); i++) {
                Condition condition = requestItem.getConditions().get(i);

                if (condition.isOptional() && condition.getValue() == null || "".equals(condition.getValue())) {
                    requestItem.getConditions().remove(i);
                    i--;
                }
            }
        }
    }

    public void setNewConditionToAdd(ConditionConstants newConditionToAdd) {
        this.newConditionToAdd = newConditionToAdd;

        log.debug("New condition to add: {}", newConditionToAdd);
    }

    public ConditionConstants getNewConditionToAdd() {
        return newConditionToAdd;
    }


}
