package org.societies.android.platform.useragent.feedback.guis;

import org.societies.android.platform.useragent.feedback.R;
import org.societies.android.platform.useragent.feedback.R.layout;
import org.societies.android.platform.useragent.feedback.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RadioPopup extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_popup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_radio_popup, menu);
        return true;
    }
}
