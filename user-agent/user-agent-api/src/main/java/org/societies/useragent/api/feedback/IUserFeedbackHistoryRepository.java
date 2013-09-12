package org.societies.useragent.api.feedback;

import org.societies.api.schema.useragent.feedback.FeedbackStage;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.Date;
import java.util.List;

public interface IUserFeedbackHistoryRepository {

    List<UserFeedbackBean> listPrevious(int howMany);

    List<UserFeedbackBean> listSince(Date sinceWhen);

    List<UserFeedbackBean> listIncomplete();

    UserFeedbackBean getByRequestId(String requestId);

    void insert(UserFeedbackBean ufBean);

    void updateStage(String requestId, FeedbackStage newStage);

    void completeExpFeedback(String requestId, List<String> values);

    void completeImpFeedback(String requestId, boolean result);

    int truncate();
}
