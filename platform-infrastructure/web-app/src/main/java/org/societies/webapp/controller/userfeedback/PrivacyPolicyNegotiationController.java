package org.societies.webapp.controller.userfeedback;

import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.PrivacyPolicyNegotiationListener;
import org.societies.webapp.wrappers.RequestItemWrapper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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

    @ManagedProperty(value = "#{privacyPolicyNegotiationListener}")
    private PrivacyPolicyNegotiationListener negotiationListener;

    private String negotiationID;

    public PrivacyPolicyNegotiationController() {
        if (log.isDebugEnabled())
            log.debug("PrivacyPolicyNegotiationController ctor()");

    }

    @PostConstruct
    public void initMethod() {
        if (log.isDebugEnabled())
            log.debug("PrivacyPolicyNegotiationController init()");
        negotiationID = getIdFromQueryString();
        UserFeedbackPrivacyNegotiationEvent event = negotiationListener.getNegotiationEvent(negotiationID);

        if (event != null) {
            if (log.isDebugEnabled())
                log.debug("Preparing event for GUI with ID " + negotiationID);

            prepareEventForGUI(event);
        } else {
            log.warn("Event not found for ID " + negotiationID);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public PrivacyPolicyNegotiationListener getNegotiationListener() {
        return negotiationListener;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setNegotiationListener(PrivacyPolicyNegotiationListener negotiationListener) {
        if (log.isDebugEnabled())
            log.debug("setNegotiationListener()");
        this.negotiationListener = negotiationListener;
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

    public ResponsePolicy getResponsePolicy() {
        UserFeedbackPrivacyNegotiationEvent event = getCurrentNegotiationEvent();

        return event != null
                ? event.getResponsePolicy()
                : null;
    }

    public UserFeedbackPrivacyNegotiationEvent getCurrentNegotiationEvent() {
        return negotiationListener.getNegotiationEvent(negotiationID);
    }

    public String completeNegotiationAction() {
        log.debug("completeNegotiation() id=" + negotiationID);

        UserFeedbackPrivacyNegotiationEvent event = negotiationListener.getNegotiationEvent(negotiationID);
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

        negotiationListener.completeNegotiation(negotiationID);

        return checkNextNegotiation();
    }

    public String cancelNegotiationAction() {
        log.debug("cancelNegotiation()");

        ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
        NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

        prepareEventForTransmission(responsePolicy);

        negotiationListener.cancelNegotiation(negotiationID);

        return checkNextNegotiation();
    }

    private String checkNextNegotiation() {
        if (log.isDebugEnabled())
            if ((negotiationListener.getQueuedNegotiationCount() > 0))
                log.debug("Next negotiation ID should be " + negotiationListener.getNextNegotiationID());
            else
                log.debug("No more negotiations in queue");

        return (negotiationListener.getQueuedNegotiationCount() > 0)
                ? "next" // redirect to next negotiation page
                : "home"; // redirect to home
    }


    private void prepareEventForGUI(UserFeedbackPrivacyNegotiationEvent event) {
        log.debug("prepareEventForGUI()");

        if (event.getResponsePolicy() == null) {
            this.log.debug("Policy parameter is null");
        } else {
            this.log.debug("Policy contains: " + event.getResponsePolicy().getResponseItems().size() + " responseItems");
        }
        List<ResponseItem> responseItems = event.getResponsePolicy().getResponseItems();
        for (ResponseItem response : responseItems) {
            this.log.debug("Processing item: " + response.getRequestItem().getResource().getDataType());
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
        log.debug("prepareEventForTransmission()");
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

            if (log.isDebugEnabled())
                log.debug("actions=" + Arrays.toString(requestWrapper.getSelectedActionNames().toArray()));


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
                log.debug("action: " + action.getActionConstant());
            }

        }

        clearResponseItemWrapper(responsePolicy);
    }

    private static void clearResponseItemWrapper(ResponsePolicy responsePolicy) {
        for (ResponseItem item : responsePolicy.getResponseItems()) {
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
