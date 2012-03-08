package com.disaster.idisaster;

import org.societies.api.css.management.ICssRecord;
import org.societies.api.css.management.ISocietiesApp;
import org.societies.cis.android.client.SocietiesApp;

import android.app.Activity;
import android.os.Bundle;

public class IDisaster4Activity extends Activity {
    String societiesServer = "server.societies.eu"; // The name of the server where cloud node is hosted
    String username = "Babak"; // username to log into societiesServer
    String password = "SocietieS"; // password for username.
    ISocietiesApp iDisasterSoc; // represents access to the SOCIETIES platform.
    ICssRecord cssRecord; // Represents information about the user of the application. to be populated.
    String cssId;  //TODO: Find out which class CssId is.

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Instantiate iDisasterSoc which will give us handles to platform
        // components:
        //TODO: Later on we need to throw an exceltion if SOCIETIES platform is not
        // installed on this node.
        iDisasterSoc = new SocietiesApp(username, password);
    }
}