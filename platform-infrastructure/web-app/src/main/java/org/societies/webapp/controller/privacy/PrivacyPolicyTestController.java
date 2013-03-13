package org.societies.webapp.controller.privacy;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.useragent.model.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.osgi.event.*;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "ppNegotiationTest")
@SessionScoped
public class PrivacyPolicyTestController extends BasePageController {

    private class PubSubListener extends EventListener {

        @Override
        public void handleInternalEvent(InternalEvent event) {
            log.info(event.geteventInfo().toString());
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
                    "(" + CSSEventConstants.EVENT_NAME + "=privacyNegotiationResponse)" +
                    "(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/feedback)" +
                    ")";
            getEventMgr().subscribeInternalEvent(this,
                    new String[]{EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE},
                    eventFilter);
            log.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + " events");

        }

        public void sendEvent(ResponsePolicy responsePolicy, NegotiationDetails negotiationDetails) {
            UserFeedbackPrivacyNegotiationEvent payload = new UserFeedbackPrivacyNegotiationEvent();
            payload.setResponsePolicy(responsePolicy);
            payload.setNegotiationDetails(negotiationDetails);
            InternalEvent event = new InternalEvent(
                    EventTypes.UF_PRIVACY_NEGOTIATION,
                    "privacyNegotiation",
                    "org/societies/useragent/feedback",
                    payload);

            try {
                eventMgr.publishInternalEvent(event);
            } catch (EMSException e) {
                log.error("Error publishing notification of new negotiation", e);
            }
        }

    }

    @ManagedProperty(value = "#{eventMgmtRef}")
    private IEventMgr eventMgr;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    private final PubSubListener pubSubListener = new PubSubListener();

    public PrivacyPolicyTestController() {
        log.trace("PrivacyPolicyTestController ctor()");
    }

    public IEventMgr getEventMgr() {
        return eventMgr;
    }

    public void setEventMgr(IEventMgr eventMgr) {
//        log.trace("setEventMgr: " + eventMgr);
        this.eventMgr = eventMgr;

        pubSubListener.registerForUFeedbackEvents();
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
//        log.trace("setUserService: " + userService);
        this.userService = userService;
    }

    public void sendEvent() {
        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId("req1");

        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action2 = new Action();
        action2.setActionConstant(ActionConstants.DELETE);
        action2.setOptional(false);
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("1");
        condition1.setOptional(true);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("2");
        condition2.setOptional(true);
        Condition condition3 = new Condition();
        condition3.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
        condition3.setValue("3");
        condition3.setOptional(true);
        Condition condition4 = new Condition();
        condition4.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
        condition4.setValue("4");
        condition4.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri("http://this.is.a.win/");
        resource.setDataType("winning");

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action2);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);
        requestItem.getConditions().add(condition3);
        requestItem.getConditions().add(condition4);

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);
        responseItems.add(responseItem);

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);


        Requestor requestor = new Requestor(userService.getIdentity());
        NegotiationDetails negotiationDetails = new NegotiationDetails(requestor, 101);

        pubSubListener.sendEvent(responsePolicy, negotiationDetails);
    }
}
