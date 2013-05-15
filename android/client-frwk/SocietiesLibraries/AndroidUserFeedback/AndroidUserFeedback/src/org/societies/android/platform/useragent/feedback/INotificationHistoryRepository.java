package org.societies.android.platform.useragent.feedback;

import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public interface INotificationHistoryRepository {

    Future<List<NotificationHistoryItem>> listPrevious(int howMany);

    Future<List<NotificationHistoryItem>> listSince(Date sinceWhen);
}
