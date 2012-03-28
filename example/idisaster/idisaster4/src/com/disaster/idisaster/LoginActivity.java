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
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.text.InputType;

import android.widget.Toast;

import android.widget.Button;
import android.view.View.OnClickListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.view.inputmethod.InputMethodManager;


/**
 * This activity is responsible for user login,
 * including handling wrong user name and password.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */

public class LoginActivity extends Activity implements OnClickListener {

	private EditText userNameView;
	private EditText userPasswordView;
	private String userName;
	private String userPassword;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView (R.layout.login_layout);

		// Get editable fields
		userNameView = (EditText) findViewById(R.id.editUserName);
	    userPasswordView = (EditText) findViewById(R.id.editPassword);
	    userPasswordView.setInputType (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    	// Add click listener to button
    	final Button button = (Button) findViewById(R.id.loginButton);
    	button.setOnClickListener(this);
    	
//	    Test dialog
//    	iDisasterApplication.getinstance().showDialog (this, getString(R.string.loginTestDialog), getString(R.string.dialogOK));

    }
 		
/**
 * onClick is called when button is clicked because
 * the OnClickListener is assigned to the button
 */
	public void onClick (View view) {
			
    	if (userNameView.getText().length() == 0) {					// check input for user name

    	// Hide the soft keyboard otherwise the toast message does appear more clearly.
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(userNameView.getWindowToken(), 0);
	    
    		Toast.makeText(this, getString(R.string.toastUserName), 
    				Toast.LENGTH_LONG).show();
    		return;

    	} else if (userPasswordView.getText().length() == 0) {		// check input for password

    		// Hide the soft keyboard otherwise the toast message does appear more clearly.
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(userPasswordView.getWindowToken(), 0);

    	    Toast.makeText(this, getString(R.string.toastPassword), 
	    			Toast.LENGTH_LONG).show();
	    	return;

    	} else {													// verify the password and store in preferences file

    		userName = userNameView.getText().toString();
    		userPassword = userPasswordView.getText().toString();

//TODO: the user name and pasword should be checked by the platform
        	// Instantiate the Societies platform => needs modification of platforLogin
        	iDisasterApplication.getinstance().platformLogIn ();

    		
    		boolean loginCode = false;	// TODO: replace by code returned by Societes API
    			    		
    		// Create dialog for wrong password
    		if (loginCode) { 							
    			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    			alertBuilder.setMessage(getString(R.string.loginDialog))
    				.setCancelable(false)
    				.setPositiveButton (getString(R.string.dialogOK), new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
	    		           userNameView.setText(getString(R.string.emptyText));
	    		           userNameView.setHint(getString(R.string.loginUserNameHint));
	    		           userPasswordView.setText(getString(R.string.emptyText));
	    		           userPasswordView.setHint(getString(R.string.loginPasswordHint));
	    		           return;
    					}
    				});
	    		AlertDialog alert = alertBuilder.create();
	    		alert.show();
	    		return;
	   		}
	    		
    		// Store user name and password in preferences
        	iDisasterApplication.getinstance().setUserName (userName, userPassword);
        	
// Code for testing the correct setting of preferences 
//    	    String testName = iDisasterApplication.getinstance().preferences.
//    	    	getString ("pref.username","");
//    	    String testPassword = iDisasterApplication.getinstance().preferences.
//    	    	getString ("pref.password","");
//    	    Toast.makeText(this, "Debug: "  + testName + " " + testPassword, 
//    			Toast.LENGTH_LONG).show();

    	    // Hide the soft keyboard:
			// - the soft keyboard will not appear on next activity window!
    	    InputMethodManager mgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    	    mgr.hideSoftInputFromWindow(userPasswordView.getWindowToken(), 0);

//    	    finish();	// noHistory=true in Manifest => the activity is removed from the activity stack and finished.

	    	// Send intent to Disaster activity
	    	startActivity(new Intent(LoginActivity.this, DisasterListActivity.class));
	    }
    }
}
