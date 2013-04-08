package org.societies.webapp.entity;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;

import java.io.Serializable;
import java.util.Date;

public class NotificationQueueItem implements Serializable {

    private Date arrivalDate;
    private Object payload;
    private IIdentity pubsubService;
    private String node;
    private String itemId;

    public NotificationQueueItem(Object payload) {
        arrivalDate = new Date();
        this.payload = payload;
    }

    public NotificationQueueItem(IIdentity pubsubService, String node, String itemId, Object payload) {
        arrivalDate = new Date();
        this.pubsubService = pubsubService;
        this.node = node;
        this.itemId = itemId;
        this.payload = payload;

    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public Object getPayload() {
        return payload;
    }

    public IIdentity getPubsubService() {
        return pubsubService;
    }

    public String getNode() {
        return node;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        if (payload == null) {
            return "- no data -";
        }

        if (payload instanceof UserFeedbackPrivacyNegotiationEvent) {
            return "Privacy policy negotiation";
        }

        return payload.getClass().getSimpleName();
    }

    public String getInfoLink() {
        if (payload == null) {
            return "";
        }

        if (payload instanceof UserFeedbackPrivacyNegotiationEvent) {
            return "privacy_policy_negotiation.xhtml?id=" + itemId;
        }

        return payload.getClass().getSimpleName();
    }


}
