package org.societies.android.platform.events.notifications;

import org.societies.android.platform.events.notifications.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FriendsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friends, menu);
        return true;
    }
}
