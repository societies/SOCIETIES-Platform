package org.societies.android.platform.cis;

import org.societies.android.platform.CisRecord;

import android.app.Activity;
import android.os.Bundle;

public class CisManagerActivity extends Activity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main);
//        CommunicationAdapter comAd = new CommunicationAdapter(this);
        CisRecord group = new CisRecord("A", "B", "C", "D");
//        comAd.createGroup(group);
    }

}

