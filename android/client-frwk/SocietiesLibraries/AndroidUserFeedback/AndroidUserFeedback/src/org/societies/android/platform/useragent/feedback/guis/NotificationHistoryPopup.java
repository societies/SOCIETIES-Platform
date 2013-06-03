package org.societies.android.platform.useragent.feedback.guis;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import org.societies.android.api.internal.R;
import org.societies.android.platform.useragent.feedback.EventHistory;
import org.societies.android.platform.useragent.feedback.model.NotificationHistoryItem;

import java.util.List;

public class NotificationHistoryPopup extends ListActivity {
    protected final String LOG_TAG = this.getClass().getCanonicalName();

    private final EventHistory eventHistoryService = EventHistory.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Dialog);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notification_history);

    }


    private void populateHistoryList() {
        List<NotificationHistoryItem> historyItemsList;
        historyItemsList = eventHistoryService.getHistoryItemsList();


    }


}
