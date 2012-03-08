package org.societies.example.hereiam.test;

import android.test.ActivityInstrumentationTestCase2;
import org.societies.example.hereiam.*;

public class HerIAmTest extends ActivityInstrumentationTestCase2<HereIAm> {

    public HerIAmTest() {
        super("org.societies.example.hereiam", HereIAm.class);
    }

    public void testActivity() {
        HereIAm activity = getActivity();
        assertNotNull(activity);
    }
}

