package org.societies.android.platform.useragent.feedback.model;

import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

public class UserFeedbackTimedAbortBean extends UserFeedbackBean {
    private boolean responseSent;

    public UserFeedbackTimedAbortBean(UserFeedbackBean prototype) {
        this.setTimeout(prototype.getTimeout());
        this.setMethod(prototype.getMethod());
        this.setOptions(prototype.getOptions());
        this.setProposalText(prototype.getProposalText());
        this.setRequestId(prototype.getRequestId());
        this.setType(prototype.getType());
        this.responseSent = false;
    }

    public boolean isResponseSent() {
        return responseSent;
    }

    public void setResponseSent(boolean responseSent) {
        this.responseSent = responseSent;
    }
}
