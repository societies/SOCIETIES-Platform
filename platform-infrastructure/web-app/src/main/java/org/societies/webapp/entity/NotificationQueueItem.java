package org.societies.webapp.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class NotificationQueueItem implements Serializable {
    protected static final Logger log = LoggerFactory.getLogger(NotificationQueueItem.class);

    public static final String TYPE_PRIVACY_POLICY_NEGOTIATION = "PPN";
    public static final String TYPE_TIMED_ABORT = "TIMED_ABORT";
    public static final String TYPE_ACK_NACK = "ACK_NACK";
    public static final String TYPE_SELECT_ONE = "SELECT_ONE";
    public static final String TYPE_SELECT_MANY = "SELECT_MANY";
    public static final String TYPE_UNKNOWN = "UNKNOWN";


    public static NotificationQueueItem forPrivacyPolicyNotification(IIdentity pubSubService, String pubSubNode, String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
        return new NotificationQueueItem(pubSubService, pubSubNode, itemId, payload);
    }

    public static NotificationQueueItem forTimedAbort(IIdentity pubSubService, String pubSubNode, String itemId, String title, Date timeout) {
        return new NotificationQueueItem(pubSubService, pubSubNode, itemId, title, timeout);
    }

    public static NotificationQueueItem forAckNack(IIdentity pubSubService, String pubSubNode, String itemId, String title) {
        return new NotificationQueueItem(pubSubService, pubSubNode, itemId, TYPE_ACK_NACK, title,
                new String[]{"Yes", "No"});
    }

    public static NotificationQueueItem forSelectOne(IIdentity pubSubService, String pubSubNode, String itemId, String title, String[] options) {
        return new NotificationQueueItem(pubSubService, pubSubNode, itemId, TYPE_SELECT_ONE, title, options);
    }

    public static NotificationQueueItem forSelectMany(IIdentity pubSubService, String pubSubNode, String itemId, String title, String[] options) {
        return new NotificationQueueItem(pubSubService, pubSubNode, itemId, TYPE_SELECT_MANY, title, options);
    }

    private final Date arrivalDate;
    private final UserFeedbackPrivacyNegotiationEvent ufPPN;
    private final IIdentity pubSubService;
    private final String title;
    private final String pubSubNode;
    private final String itemId;
    private final String type;
    private final String[] options;
    private final Date timeoutTime;
    private String result;
    private String[] results;

    private NotificationQueueItem(IIdentity pubSubService, String pubSubNode, String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
        this.arrivalDate = new Date();
        this.pubSubService = pubSubService;
        this.pubSubNode = pubSubNode;
        this.itemId = itemId;
        this.ufPPN = payload;
        this.options = new String[0];
        this.type = TYPE_PRIVACY_POLICY_NEGOTIATION;
        this.title = payload.getNegotiationDetails().getRequestor().getRequestorId();
        this.timeoutTime = null;
    }

    public NotificationQueueItem(IIdentity pubSubService, String pubSubNode, String itemId, String title, Date timeout) {
        this.arrivalDate = new Date();
        this.pubSubService = pubSubService;
        this.pubSubNode = pubSubNode;
        this.itemId = itemId;
        this.ufPPN = null;
        this.options = new String[0];
        this.type = TYPE_TIMED_ABORT;
        this.title = title;
        this.timeoutTime = timeout;
    }

    private NotificationQueueItem(IIdentity pubSubService, String pubSubNode, String itemId, String type, String title, String[] options) {
        this.arrivalDate = new Date();
        this.pubSubService = pubSubService;
        this.pubSubNode = pubSubNode;
        this.itemId = itemId;
        this.ufPPN = null;
        this.options = options;
        this.type = type;
        this.title = title;
        this.timeoutTime = null;
    }


    public Date getArrivalDate() {
        return arrivalDate;
    }

    public Object getPrivacyPolicyNegotiation() {
        return ufPPN;
    }

    public IIdentity getPubSubService() {
        return pubSubService;
    }

    public String getPubSubNode() {
        return pubSubNode;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getInfoLink() {
        if (ufPPN != null) {
            return "privacy_policy_negotiation.xhtml?id=" + itemId;
        }

        return "";
    }

    public String getType() {
        return type;
    }

    public String[] getOptions() {
        return options;
    }

    public Date getTimeoutTime() {
        return timeoutTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (log.isTraceEnabled())
            log.trace("setResult() for " + getTitle() + "=" + result);

        this.result = result;
    }

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        if (log.isTraceEnabled())
            log.trace("setResults() for " + getTitle() + "=" + Arrays.toString(results));

        this.results = results;
    }
}
