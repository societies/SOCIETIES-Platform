package org.societies.cft;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MasterPreferences extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
