package org.societies.integration.test.bit.userfeedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class NotificationQueueItem implements Serializable, Comparable<NotificationQueueItem> {
    protected static final Logger log = LoggerFactory.getLogger(NotificationQueueItem.class);

    public static final String TYPE_PRIVACY_POLICY_NEGOTIATION = "PPN";
    public static final String TYPE_TIMED_ABORT = "TIMED_ABORT";
    public static final String TYPE_ACK_NACK = "ACK_NACK";
    public static final String TYPE_SELECT_ONE = "SELECT_ONE";
    public static final String TYPE_SELECT_MANY = "SELECT_MANY";
    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String TYPE_UNKNOWN = "UNKNOWN";


    public static NotificationQueueItem forPrivacyPolicyNotification(String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
        return new NotificationQueueItem(itemId, payload);
    }

    public static NotificationQueueItem forTimedAbort(String itemId, String title, Date timeout) {
        return new NotificationQueueItem(itemId, title, timeout);
    }

    public static NotificationQueueItem forAckNack(String itemId, String title, String[] options) {
        return new NotificationQueueItem(itemId, TYPE_ACK_NACK, title, options);
    }

    public static NotificationQueueItem forSelectOne(String itemId, String title, String[] options) {
        return new NotificationQueueItem(itemId, TYPE_SELECT_ONE, title, options);
    }

    public static NotificationQueueItem forSelectMany(String itemId, String title, String[] options) {
        return new NotificationQueueItem(itemId, TYPE_SELECT_MANY, title, options);
    }

    public static NotificationQueueItem forNotification(String itemId, String title) {
        return new NotificationQueueItem(itemId, TYPE_NOTIFICATION, title, null);
    }

    private final Date arrivalDate;
    private final UserFeedbackPrivacyNegotiationEvent ufPPN;
    private final String title;
    private final String itemId;
    private final String type;
    private final String[] options;
    private final Date timeoutTime;
    private String result;
    private String[] results;
    private boolean complete;

    private NotificationQueueItem(String itemId, UserFeedbackPrivacyNegotiationEvent payload) {
        this.arrivalDate = new Date();
        this.itemId = itemId;
        this.ufPPN = payload;
        this.options = new String[0];
        this.type = TYPE_PRIVACY_POLICY_NEGOTIATION;
        this.title = payload.getNegotiationDetails().getRequestor().getRequestorId();
        this.timeoutTime = null;
        this.complete = false;
    }

    private NotificationQueueItem(String itemId, String title, Date timeout) {
        this.arrivalDate = new Date();
        this.itemId = itemId;
        this.ufPPN = null;
        this.options = new String[0];
        this.type = TYPE_TIMED_ABORT;
        this.title = title;
        this.timeoutTime = timeout;
        this.complete = false;
    }

    private NotificationQueueItem(String itemId, String type, String title, String[] options) {
        this.arrivalDate = new Date();
        this.itemId = itemId;
        this.ufPPN = null;
        this.options = options;
        this.type = type;
        this.title = title;
        this.timeoutTime = null;
        this.complete = false;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public Object getPrivacyPolicyNegotiation() {
        return ufPPN;
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
        this.result = result;
    }

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        this.results = results;
    }

    public String getFriendlyTimeLeft() {
        if (timeoutTime == null)
            return "forever";

        long seconds = (timeoutTime.getTime() - new Date().getTime()) / 1000L;
        if (seconds < 0L)
            seconds = 0;

        // hours:minutes
        if (seconds > 3600) {
            long minutes = ((seconds % 3600L) - (seconds % 60L)) / 60L;
            long hours = (seconds - (seconds % 3600L)) / 3600L;

            return hours + "hrs, " + minutes + "mins";
        }

        // minutes:seconds
        if (seconds > 60) {
            long minutes = (seconds - (seconds % 60L)) / 60L;

            return minutes + "mins, " + (seconds % 60L) + "sec";
        }

        // seconds only
        return seconds + "sec";
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    @Override
    public int compareTo(NotificationQueueItem that) {
        return this.arrivalDate.compareTo(that.arrivalDate);
    }

}
