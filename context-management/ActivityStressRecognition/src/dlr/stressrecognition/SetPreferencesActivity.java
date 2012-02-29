package dlr.stressrecognition;

import dlr.stressrecognition.utils.AppSharedPrefs;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Extension of the PreferenceActivity in the Android Framework.
 * 
 * @author Michael Gross
 *
 */
public class SetPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        getPreferenceManager().setSharedPreferencesName(
                AppSharedPrefs.PREFS_NAME);
        addPreferencesFromResource(R.xml.prefs);
    }
}
