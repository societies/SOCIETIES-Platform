package org.societies.useragent.api.feedback;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.FeedbackStage;

import java.util.Date;
import java.util.List;

public interface IPrivacyPolicyNegotiationHistoryRepository {

    List<UserFeedbackPrivacyNegotiationEvent> listPrevious(int howMany);

    List<UserFeedbackPrivacyNegotiationEvent> listSince(Date sinceWhen);

    List<UserFeedbackPrivacyNegotiationEvent> listIncomplete();

    UserFeedbackPrivacyNegotiationEvent getByRequestId(String requestId);

    void insert(UserFeedbackPrivacyNegotiationEvent event);

    void updateStage(String requestId, FeedbackStage newStage);
}
