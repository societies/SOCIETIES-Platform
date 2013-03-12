package org.societies.api.internal.useragent.model;

import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.io.Serializable;

public class UserFeedbackPrivacyNegotiationEvent implements Serializable {
    private ResponsePolicy responsePolicy;
    private NegotiationDetails negotiationDetails;

    public ResponsePolicy getResponsePolicy() {
        return responsePolicy;
    }

    public void setResponsePolicy(ResponsePolicy responsePolicy) {
        this.responsePolicy = responsePolicy;
    }

    public void setNegotiationDetails(NegotiationDetails negotiationDetails) {
        this.negotiationDetails = negotiationDetails;
    }

    public NegotiationDetails getNegotiationDetails() {
        return negotiationDetails;
    }
}
