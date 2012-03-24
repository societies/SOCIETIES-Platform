package com.disaster.idisaster;

//import org.societies.api.css.management.ICssRecord;
//import org.societies.api.css.management.ISocietiesApp;
// import org.societies.cis.android.client.SocietiesApp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * This activity is responsible for interaction with the
 * main home page for iDisaster.
 * The home page relates to a specific Disaster community
 * and provide access to activity feeds, users and services
 * related to the community.
 * 
 * @authors Babak.Farshchian@sintef.no
 * 			Jacqueline.Floch@sintef.no
 *
 */
public class HomeActivity extends TabActivity {

	String societiesServer = "server.societies.eu"; // The name of the server where cloud node is hosted
    String username = "Babak"; // username to log into societiesServer
    String password = "SocietieS"; // password for username.
//    ISocietiesApp iDisasterSoc; // represents access to the SOCIETIES platform.
//    ICssRecord cssRecord; // Represents information about the user of the application. to be populated.
//    String cssId;  //TODO: Find out which class CssId is.

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resuable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FeedActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("activities").setIndicator("Activities",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, UserActivity.class);
        spec = tabHost.newTabSpec("users").setIndicator("Users",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ServiceActivity.class);
        spec = tabHost.newTabSpec("services").setIndicator("Services",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Start with disasters tab visible:
        tabHost.setCurrentTab(0);

    }

    /** Called at start of the active lifetime. */
    @Override
	protected void onResume() {
		super.onResume();
	}//onResume

    
}