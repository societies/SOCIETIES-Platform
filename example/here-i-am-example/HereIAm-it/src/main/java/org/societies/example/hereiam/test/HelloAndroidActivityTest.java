package org.societies.example.hereiam.test;

import android.test.ActivityInstrumentationTestCase2;
import org.societies.example.hereiam.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super("org.societies.example.hereiam", HelloAndroidActivity.class);
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

