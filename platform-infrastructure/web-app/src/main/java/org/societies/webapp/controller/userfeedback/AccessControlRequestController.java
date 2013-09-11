package org.societies.webapp.controller.userfeedback;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.wrappers.RequestItemWrapper;

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

@ManagedBean(name = "accessControlController")
@ViewScoped
public class AccessControlRequestController extends BasePageController {

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
    private UserFeedbackAccessControlEvent event;
    private ConditionConstants newConditionToAdd;

    public AccessControlRequestController() {
        if (log.isDebugEnabled())
            log.debug("AccessControlRequestController ctor()");

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
        event = notificationsController.getAcceessControlEvent(eventID);

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

    public List<ResponseItem> getResponseItems() {
        return event != null
                ? event.getResponseItems()
                : null;
    }

    public UserFeedbackAccessControlEvent getCurrentAccessEvent() {
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

    public String completeAccessRequestAction() {
        log.debug("completeAccessRequestAction() id=" + eventID);

        if (event == null) {
            log.warn("'event' is null - cannot proceed with completeAccessRequestAction() method");
            return null;
        }

        // TODO: validate action check boxes

        prepareEventForTransmission(event);

        try {
            userFeedback.submitAccessControlResponse(eventID, event.getResponseItems(), event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }

        return "home"; // previously, could redirect to next negotiation - but this makes no sense now
    }

    public String cancelAccessRequestAction() {
        log.debug("cancelAccessRequestAction()");

        if (event == null) {
            log.warn("'event' is null - cannot proceed with cancelAccessRequestAction() method");
            return null;
        }

        prepareEventForTransmission(event);

        try {
            userFeedback.submitAccessControlResponse(eventID, event.getResponseItems(), event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of cancelled access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of cancelled access control event", e);
        }

        return "home"; // previously, could redirect to next negotiation - but this makes no sense now
    }

    private static void prepareEventForGUI(UserFeedbackAccessControlEvent event) {

        List<ResponseItem> responseItems = event.getResponseItems();
        for (ResponseItem response : responseItems) {

            RequestItemWrapper request = new RequestItemWrapper(response.getRequestItem());
            response.setRequestItem(request);

            // quickly sort by condition name
            Collections.sort(request.getConditions(), new Comparator<Condition>() {
                @Override
                public int compare(Condition o1, Condition o2) {
                    return o1.getConditionConstant().name().compareTo(o2.getConditionConstant().name());
                }
            });
        }
    }

    private static void prepareEventForTransmission(UserFeedbackAccessControlEvent event) {
        for (ResponseItem response : event.getResponseItems()) {
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

        }

        clearResponseItemWrapper(event);
    }

    private static void clearResponseItemWrapper(UserFeedbackAccessControlEvent event) {
        for (ResponseItem item : event.getResponseItems()) {
            RequestItem oldItem = item.getRequestItem();
            RequestItem newItem = new RequestItem();
            newItem.setActions(oldItem.getActions());
            newItem.setConditions(oldItem.getConditions());
            newItem.setOptional(oldItem.isOptional());
            newItem.setResource(oldItem.getResource());

            item.setRequestItem(newItem);
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
