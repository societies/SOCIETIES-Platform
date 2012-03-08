package com.disaster.idisaster.test;

import android.test.ActivityInstrumentationTestCase2;
import com.disaster.idisaster.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super("com.disaster.idisaster", HelloAndroidActivity.class);
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

