package org.societies.android.platform.useragent.feedback.model;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.Date;

public class NotificationHistoryItem implements Comparable<NotificationHistoryItem> {

    private final String uuid;
    private final Date eventDate;
    private final UserFeedbackBean userFeedbackBean;
    private final UserFeedbackPrivacyNegotiationEvent privacyNegotiationEvent;
    private final UserFeedbackAccessControlEvent accessControlEvent;

    public NotificationHistoryItem(String uuid, Date eventDate, UserFeedbackBean userFeedbackBean) {
        this.uuid = uuid;
        this.eventDate = eventDate;
        this.userFeedbackBean = userFeedbackBean;
        this.privacyNegotiationEvent = null;
        this.accessControlEvent = null;
    }

    public NotificationHistoryItem(String uuid, Date eventDate, UserFeedbackPrivacyNegotiationEvent privacyNegotiationEvent) {
        this.uuid = uuid;
        this.eventDate = eventDate;
        this.userFeedbackBean = null;
        this.privacyNegotiationEvent = privacyNegotiationEvent;
        this.accessControlEvent = null;
    }

    public NotificationHistoryItem(String uuid, Date eventDate, UserFeedbackAccessControlEvent accessControlEvent) {
        this.uuid = uuid;
        this.eventDate = eventDate;
        this.userFeedbackBean = null;
        this.privacyNegotiationEvent = null;
        this.accessControlEvent = accessControlEvent;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public UserFeedbackBean getUserFeedbackBean() {
        return userFeedbackBean;
    }

    public UserFeedbackPrivacyNegotiationEvent getPrivacyNegotiationEvent() {
        return privacyNegotiationEvent;
    }

    public UserFeedbackAccessControlEvent getAccessControlEvent() {
        return accessControlEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationHistoryItem)) return false;

        NotificationHistoryItem that = (NotificationHistoryItem) o;

        if (!eventDate.equals(that.eventDate)) return false;
        if (privacyNegotiationEvent != null ? !privacyNegotiationEvent.equals(that.privacyNegotiationEvent) : that.privacyNegotiationEvent != null)
            return false;
        if (userFeedbackBean != null ? !userFeedbackBean.equals(that.userFeedbackBean) : that.userFeedbackBean != null)
            return false;
        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + eventDate.hashCode();
        result = 31 * result + (userFeedbackBean != null ? userFeedbackBean.hashCode() : 0);
        result = 31 * result + (privacyNegotiationEvent != null ? privacyNegotiationEvent.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(NotificationHistoryItem that) {
        return this.eventDate.compareTo(that.eventDate);
    }
}
