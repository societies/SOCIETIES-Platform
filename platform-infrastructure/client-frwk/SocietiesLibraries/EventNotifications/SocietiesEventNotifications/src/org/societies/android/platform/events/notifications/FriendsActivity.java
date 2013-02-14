package org.societies.android.platform.events.notifications;

import org.societies.android.platform.events.notifications.R;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FriendsActivity extends Activity {

	private static final String EXTRA_CSS_ADVERT = "org.societies.api.schema.css.directory.CssAdvertisementRecord";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        
        CssAdvertisementRecord advert = (CssAdvertisementRecord) getIntent().getParcelableExtra(EXTRA_CSS_ADVERT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friends, menu);
        return true;
    }
}
