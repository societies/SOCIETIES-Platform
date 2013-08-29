/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
