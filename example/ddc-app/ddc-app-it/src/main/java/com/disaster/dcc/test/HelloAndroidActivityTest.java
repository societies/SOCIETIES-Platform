package com.disaster.dcc.test;

import android.test.ActivityInstrumentationTestCase2;
import com.disaster.dcc.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<DccApp> {

    public HelloAndroidActivityTest() {
        super("com.disaster.dcc", DccApp.class);
    }

    public void testActivity() {
        DccApp activity = getActivity();
        assertNotNull(activity);
    }
}

