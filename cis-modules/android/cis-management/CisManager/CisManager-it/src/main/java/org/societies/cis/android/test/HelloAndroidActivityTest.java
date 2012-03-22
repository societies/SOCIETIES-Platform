package org.societies.cis.android.test;

import android.test.ActivityInstrumentationTestCase2;
import org.societies.cis.android.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<CisManagerActivity> {

    public HelloAndroidActivityTest() {
        super("org.societies.cis.android", CisManagerActivity.class);
    }

    public void testActivity() {
        CisManagerActivity activity = getActivity();
        assertNotNull(activity);
    }
}

