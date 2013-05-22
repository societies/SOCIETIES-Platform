package org.societies.android.platform.useragent.feedback;

import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;

import java.util.Date;
import java.util.List;

public interface INotificationHistoryRepository {

    List<NotificationHistoryItem> listPrevious(int howMany);

    List<NotificationHistoryItem> listSince(Date sinceWhen);
}
