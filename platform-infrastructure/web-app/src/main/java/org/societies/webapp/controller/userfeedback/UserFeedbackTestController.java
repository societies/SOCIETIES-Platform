package org.societies.webapp.controller.userfeedback;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackHistoryRequest;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.ILoginListener;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "ufTestController")
@SessionScoped
public class UserFeedbackTestController extends BasePageController {

    private class PubSubListener implements Subscriber {

        public void registerForEvents() {

            if (getPubsubClient() == null) {
                log.error("PubSubClient was null, cannot register for events");
                return;
            }

            try {
                getPubsubClient().subscriberSubscribe(getUserService().getIdentity(),
                        EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE,
                        this);

                if(log.isDebugEnabled()) log.debug("Subscribed to " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + " events");
            } catch (Exception e) {
                addGlobalMessage("Error subscribing to pubsub notifications",
                        e.getMessage(),
                        FacesMessage.SEVERITY_ERROR);
                log.error("Error subscribing to pubsub notifications (id="
                        + getUserService().getIdentity()
                        + " event=" + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, e);
            }
        }

        @Override
        public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
            if (log.isDebugEnabled())
                log.debug("pubsubEvent(): node=" + node + " item=" + item);

        }

    }

    private class LoginListener implements ILoginListener {

        @Override
        public void userLoggedIn() {
            pubSubListener.registerForEvents();
        }

        @Override
        public void userLoggedOut() {
        }
    }

    private static final String[] COLORS = new String[]{
            "RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "INDIGO", "VIOLET"
    };
    private static final String[] FEATURES = new String[]{
            "2 LEGS", "4 LEGS", "SWIMS", "JUMPS", "LONG NECK", "REALLY HEAVY", "WILL EAT YOU"
    };
    //    private static final String[] CLASSIFICATIONS = new String[]{
//            "KINGDOM", "PHYLYM", "CLASS", "ORDER", "FAMILY", "GENUS", "SPECIES"
//    };
    private static final String[] ANIMALS = new String[]{
            "CAT", "OTTER", "MOUSE", "DOG", "HORSE", "BADGER", "OSTRICH", "SEAL", "HEDGEHOG", "LION", "TIGER", "GIRAFFE", "HEFFALUMP"
    };

    private static final Random random = new Random();

    @ManagedProperty(value = "#{commMngrRef}")
    private ICommManager commManager;

    @ManagedProperty(value = "#{pubsubClient}")
    private PubsubClient pubsubClient;

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{userFeedback}")
    private IUserFeedback userFeedback;


    private final PubSubListener pubSubListener = new PubSubListener();
    private final LoginListener loginListener = new LoginListener();
    private static int req_counter = 0;

    public UserFeedbackTestController() {
        log.debug("UserFeedbackTestController ctor()");
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
//        if (log.isDebugEnabled())
//            log.debug("setUserService() = " + userService);

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

    public ICommManager getCommManager() {
        return commManager;
    }

    public void setCommManager(ICommManager commManager) {
        this.commManager = commManager;
    }

    public void sendPpnEvent() throws ExecutionException, InterruptedException {
        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId("req" + ++req_counter);

        SecureRandom random = new SecureRandom();

        ResponsePolicy responsePolicy = buildResponsePolicy(requestorBean);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(new BigInteger(130, random).intValue());

        log.info("PPN: Sending event");
        userFeedback.getPrivacyNegotiationFBAsync(responsePolicy, negotiationDetails, new IUserFeedbackResponseEventListener<ResponsePolicy>() {
            @Override
            public void responseReceived(ResponsePolicy result) {
                addGlobalMessage("PPN Response received",
                        result.getResponsePolicyId() + " = " + result.getNegotiationStatus(),
                        FacesMessage.SEVERITY_INFO);
            }
        });

    }

    public void sendSimpleNotificationEvent() {
        String proposalText = "I'm thinking of an animal: " + randomIdentifier();

        log.info("Simple: Sending event");
        userFeedback.showNotification(proposalText);
        log.info("Simple: Done");
    }

    public void sendAckNackEvent() {
        String proposalText = "Do you like " + randomIdentifier() + "?";
        String[] options = new String[]{"Yes", "No"}; // this actually has no effect for acknack

        log.info("Acknack: Sending event");
        ExpProposalContent content = new ExpProposalContent(proposalText, options);
        userFeedback.getExplicitFBAsync(ExpProposalType.ACKNACK, content, new IUserFeedbackResponseEventListener<List<String>>() {
            @Override
            public void responseReceived(List<String> result) {
                log.info("Acknack: Response received");
                addGlobalMessage("AckNack Response received",
                        (result != null && result.size() > 0) ? result.get(0) : "null",
                        FacesMessage.SEVERITY_INFO);
            }
        });
        log.info("Acknack: Sent");
    }

    public void sendSelectOneEvent_blocking() throws InterruptedException, ExecutionException {
        String proposalText = "What colour is a " + randomIdentifier() + "? (testing)";

        ExpProposalContent content = new ExpProposalContent(proposalText, COLORS);
        Future<List<String>> result = userFeedback.getExplicitFB(ExpProposalType.RADIOLIST, content);

        // USE YOUR RESULT HERE
        addGlobalMessage("SelectOne Response received",
                (result.get() != null && result.get().size() > 0) ? result.get().get(0) : "null",
                FacesMessage.SEVERITY_INFO);
    }

    public void sendSelectOneEvent() {
        String proposalText = "What colour is a " + randomIdentifier() + "? (testing)";

        log.info("SelectOne: Sending event");
        ExpProposalContent content = new ExpProposalContent(proposalText, COLORS);
        userFeedback.getExplicitFBAsync(ExpProposalType.RADIOLIST, content, new IUserFeedbackResponseEventListener<List<String>>() {
            @Override
            public void responseReceived(List<String> result) {
                log.info("SelectOne: Response received");
                addGlobalMessage("SelectOne Response received",
                        (result != null && result.size() > 0) ? result.get(0) : "null",
                        FacesMessage.SEVERITY_INFO);
            }
        });
        log.info("SelectOne: Sent");
    }

    public void sendSelectManyEvent() {
        String proposalText = "Select features of a " + randomIdentifier() + ": (testing)";

        log.info("SelectMany: Sending event");
        ExpProposalContent content = new ExpProposalContent(proposalText, FEATURES);
        userFeedback.getExplicitFBAsync(ExpProposalType.CHECKBOXLIST, content, new IUserFeedbackResponseEventListener<List<String>>() {
            @Override
            public void responseReceived(List<String> result) {
                log.info("SelectMany: Response received");
                addGlobalMessage("SelectMany Response received",
                        (result != null) ? Arrays.toString(result.toArray()) : "null",
                        FacesMessage.SEVERITY_INFO);
            }
        });
        log.info("SelectMany: Sent");
    }

    public void sendTimedAbortEvent(long sec) {
        String proposalText = "This is a timed abort";

        log.info("TimedAbort: Sending event");
        ImpProposalContent content = new ImpProposalContent(proposalText, (int) sec * 1000);
        userFeedback.getImplicitFBAsync(ImpProposalType.TIMED_ABORT, content, new IUserFeedbackResponseEventListener<Boolean>() {
            @Override
            public void responseReceived(Boolean result) {
                log.info("TimedAbort: Response received");
                addGlobalMessage("TimedAbort Response received",
                        (result != null) ? result.toString() : "null",
                        FacesMessage.SEVERITY_INFO);
            }
        });
        log.info("TimedAbort: Sent");
    }

    public void sendAccessControlEvent() {

        Requestor requestor = new Requestor(userService.getIdentity());

        List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
        responseItems.add(buildAccessResponseItem("http://this.is.a.win/", CtxAttributeTypes.NAME));
        responseItems.add(buildAccessResponseItem("http://paddy.rules/", CtxAttributeTypes.LOCATION_COORDINATES));
        responseItems.add(buildAccessResponseItem("http://something.something.something/", CtxAttributeTypes.STATUS));
        responseItems.add(buildAccessResponseItem("http://something.something.something/", CtxAttributeTypes.TEMPERATURE));
        responseItems.add(buildAccessResponseItem("http://something.something.something/", CtxAttributeTypes.ADDRESS_HOME_CITY));
        responseItems.add(buildAccessResponseItem("http://something.something.something/", CtxAttributeTypes.ADDRESS_WORK_CITY));

        userFeedback.getAccessControlFBAsync(requestor, responseItems, new IUserFeedbackResponseEventListener<List<AccessControlResponseItem>>() {
            @Override
            public void responseReceived(List<AccessControlResponseItem> result) {
                log.info("AccessControl: Response received");
                addGlobalMessage("AccessControl Response received",
                        (result != null && result.size() > 0) ? result.get(0).getDecision().toString() : "null",
                        FacesMessage.SEVERITY_INFO);
            }
        });
        log.info("AccessControl: Sent");
    }

    public void resetUserFeedback() {
        userFeedback.clear();
    }

    public void requestAndroidHistoryBean() {

        Stanza stanza = new Stanza(UUID.randomUUID().toString(),
                commManager.getIdManager().getThisNetworkNode(),
                commManager.getIdManager().getThisNetworkNode());

        UserFeedbackHistoryRequest request = new UserFeedbackHistoryRequest();

        try {
            commManager.sendIQGet(stanza, request, new ICommCallback() {
                @Override
                public List<String> getXMLNamespaces() {
                    return Arrays.asList("http://societies.org/api/schema/useragent/feedback",
                            "http://societies.org/api/internal/schema/useragent/feedback");
                }

                @Override
                public List<String> getJavaPackages() {
                    return Arrays.asList("org.societies.api.schema.useragent.feedback",
                            "org.societies.api.internal.schema.useragent.feedback");
                }

                @Override
                public void receiveResult(Stanza stanza, Object payload) {
                    log.info("receiveResult stanza={}, payload={}",
                            new Object[]{stanza, payload});
                }

                @Override
                public void receiveError(Stanza stanza, XMPPError error) {
                    log.error("receiveError stanza={}, error={}",
                            new Object[]{stanza, error});
                }

                @Override
                public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
                    log.info("receiveInfo stanza={}, info={}",
                            new Object[]{stanza, info});
                }

                @Override
                public void receiveItems(Stanza stanza, String node, List<String> items) {
                    log.info("receiveItems stanza={}, items={}",
                            new Object[]{stanza, items});
                }

                @Override
                public void receiveMessage(Stanza stanza, Object payload) {
                    log.info("receiveMessage stanza={}, payload={}",
                            new Object[]{stanza, payload});
                }
            });
        } catch (CommunicationException e) {
            log.error("Error sending testing IQ", e);
        }
    }

    private static ResponsePolicy buildResponsePolicy(RequestorBean requestorBean) {
        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildPrivacyResponseItem("http://this.is.a.win/", "Location"));
        responseItems.add(buildPrivacyResponseItem("http://paddy.rules/", "Status"));
        responseItems.add(buildPrivacyResponseItem("http://something.something.something/", "Hair colour"));

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);
        return responsePolicy;
    }

    private static ResponseItem buildPrivacyResponseItem(String uri, String dataType) {
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("12");
        condition1.setOptional(false);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("true");
        condition2.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri(uri);
        resource.setDataType(dataType);

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);

        return responseItem;
    }

    private static AccessControlResponseItem buildAccessResponseItem(String uri, String dataType) {
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("12");
        condition1.setOptional(false);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("true");
        condition2.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri(uri);
        resource.setDataType(dataType);

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        AccessControlResponseItem responseItem = new AccessControlResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);

        return responseItem;
    }

    private static String randomIdentifier() {
        return COLORS[random.nextInt(COLORS.length)]
                + " " + ANIMALS[random.nextInt(ANIMALS.length)]
                + " " + random.nextInt(10000);
    }
}
