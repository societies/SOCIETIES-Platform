/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.disaster.idisaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the activity (without GUI) that starts 
 * when you first click on the icon in Android.
 * 
 * The GUI is not shown the first time the application
 * is launched since the user should first log in.
 * 
 * @author Jacqueline.Floch@sintef.no
 * 
 *
 */
public class StartActivity extends Activity implements OnClickListener {

	String userName;
	String disasterName;

    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    				
	    setContentView (R.layout.start_layout);	// create GUI
	    // Add click listener to button
	    final Button button = (Button) findViewById(R.id.startButton);
	    button.setOnClickListener(this);

//	    Test dialog
//    	iDisasterApplication.getinstance().showDialog (this, getString(R.string.startTestDialog), getString(R.string.dialogOK));

    }

/**
 * getPreferences retrieves the preferences stored in the preferences file.
 */
    private void getPreferences () {

    	userName = iDisasterApplication.getinstance().getUserName ();
    	disasterName = iDisasterApplication.getinstance().getDisasterName ();
	}
		
/**
 * startNextActivity is called when to select the next activity.
 * 
 * If the user is not registered, it starts the LoginActivity,
 * otherwise if no disaster is selected, it starts the DisasterActivity
 * otherwise it starts the HomeActivity.
 */
	private void startNextActivity () {
		
    	if (userName == getString(R.string.noPreference)) {								// no user name (no password)
    		startActivity(new Intent(StartActivity.this, LoginActivity.class));
    		return;
    	} else if (disasterName == getString(R.string.noPreference)) {					// no disaster selected
    		startActivity(new Intent(StartActivity.this, DisasterListActivity.class));
    		return;
    	} else {
    		
    		startActivity(new Intent(StartActivity.this, DisasterActivity.class));
    		return;
    	}
    }

/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 */
	public void onClick (View view) {
		getPreferences ();				// retrieve user preferences
		startNextActivity ();			// select next activity
	}

}