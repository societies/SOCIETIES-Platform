package com.disaster.idisaster;

//import org.societies.api.css.management.ICssRecord;
//import org.societies.api.css.management.ISocietiesApp;
// import org.societies.cis.android.client.SocietiesApp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * This activity is responsible for interaction with the
 * main home page for iDisaster.
 * The home page relates to a specific Disaster community
 * and provide access to activity feeds, users and services
 * related to the community.
 * 
 * @authors Jacqueline.Floch@sintef.no
 * 			Babak.Farshchian@sintef.no
 *
 */
public class DisasterActivity extends TabActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
// TODO: Add check that 1) a disaster is selected 2) the selected disaster still exists...
        
        setContentView(R.layout.disaster_layout);

        // Set view label to selected disaster name
    	String disasterName = iDisasterApplication.getinstance().getDisasterName ();
		TextView title = (TextView)findViewById(R.id.disasterLabel);

		title.setText (disasterName);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FeedActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("activities").setIndicator("Activities",
                          res.getDrawable(R.drawable.ic_tab_activities))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, UserActivity.class);
        spec = tabHost.newTabSpec("users").setIndicator("Users",
                          res.getDrawable(R.drawable.ic_tab_users))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ServiceActivity.class);
        spec = tabHost.newTabSpec("services").setIndicator("Services",
                          res.getDrawable(R.drawable.ic_tab_services))
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

/**
 * onCreateOptionsMenu creates the activity menu.
 */
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.clear();
    	getMenuInflater().inflate(R.menu.disaster_menu, menu);
    	
//    	It is possible to set up a variable menu		
//    	menu.findItem (R.id....).setVisible(true);	
    	return true;
    }

 /**
  * onOptionsItemSelected handles the selection of an item in the activity menu.
  */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
    		case R.id.disasterMenuSelectDisaster:
            	iDisasterApplication.getinstance().setDisasterName 
            		(getString(R.string.noPreference));									// reset user preferences
// TODO:not sure whether or not the activity should finish
// noHistory is used in Manifest to avoid putting activity on stack
//            	 finish();	
    			startActivity(new Intent(DisasterActivity.this, DisasterListActivity.class));
			break;
    	case R.id.disasterMenuLogoff:
//TODO: Call the Societies platform
        	iDisasterApplication.getinstance().setUserName
        		(getString(R.string.noPreference), getString(R.string.noPreference));	// reset user preferences
//        	iDisasterApplication.getinstance().userLoggedIn = false;

//	    	finish();	// noHistory=true in Manifest => the activity is removed from the activity stack and finished.

    		startActivity(new Intent(DisasterActivity.this, StartActivity.class));
    		break;

    		
    	default:
    		break;
    	}
    	return true;
    }

  
}