package org.societies.useragent.api.feedback;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.schema.useragent.feedback.FeedbackStage;

import java.util.Date;
import java.util.List;

public interface IAccessControlHistoryRepository {

    List<UserFeedbackAccessControlEvent> listPrevious(int howMany);

    List<UserFeedbackAccessControlEvent> listSince(Date sinceWhen);

    List<UserFeedbackAccessControlEvent> listIncomplete();

    UserFeedbackAccessControlEvent getByRequestId(String requestId);

    void insert(UserFeedbackAccessControlEvent event);

    void updateStage(String requestId, FeedbackStage newStage);

    int truncate();
}
