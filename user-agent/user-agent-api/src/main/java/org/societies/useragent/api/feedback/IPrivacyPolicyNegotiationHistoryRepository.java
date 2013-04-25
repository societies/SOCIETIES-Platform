package org.societies.useragent.api.feedback;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;

import java.util.Date;
import java.util.List;

public interface IPrivacyPolicyNegotiationHistoryRepository {

    List<UserFeedbackPrivacyNegotiationEvent> listPrevious(int howMany);

    List<UserFeedbackPrivacyNegotiationEvent> listSince(Date sinceWhen);

}
