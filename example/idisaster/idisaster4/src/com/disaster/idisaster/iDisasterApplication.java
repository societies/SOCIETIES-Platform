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

import java.util.ArrayList;

import org.societies.android.platform.client.SocietiesApp;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.ArrayAdapter;

/**
 * The application for managing common resources used by 
 * iDisaster application components.
 * 
 * @author 	Jacqueline.Floch@sintef.no
 *
 */
public class iDisasterApplication extends Application {
	

	private static iDisasterApplication singleton; // Reference to the single instance of the Application
	
	static final String PREFS_NAME = "iDisasterPreferences"; 	// File for storing preferences
	SharedPreferences preferences;								// Preferences shared with all activities
	Editor editor;												// Editor for changing preferences

	Boolean platformLoggedIn = false;
	SocietiesApp iDisasterSoc; 							// represents access to the SOCIETIES platform.

//TODO: remove test code	
	ArrayList <String> disasterNameList = new ArrayList ();

// Common resources	
	ArrayAdapter<String> disasterAdapter;


	// TODO: Remove unnecessary attributes 
//    String societiesServer = "server.societies.eu";	// The name of the server where cloud node is hosted
//    String username = "Babak"; 						// username to log into societiesServer
//    String password = "SocietieS";					// password for username.
//    CssRecord cssRecord;								// Represents information about the user of the application. to be populated.

//TODO: Find out which class CssId is.
//    String cssId;


	// returns application instance
	public static iDisasterApplication getinstance () {
		return singleton;
	}
	
	@Override
	public final void onCreate() {

		super.onCreate ();
		singleton = this;

	    // Restore preferences from preferences file.
		// If the preferences file does not exist, it is created when changes are committed.
		
		preferences = getSharedPreferences(PREFS_NAME, 0);
	    editor = preferences.edit();
	    editor.putString ("pref.dummy", "");
	    editor.commit ();

//TODO: remove test code
//    	for (int i = 1; i < 10; i = i + 1) {
//    		disasterNameList.add ("Disaster " + Integer.toString (i));
//		}
		disasterNameList.add ("Cyprus AMC November 2010");
		disasterNameList.add ("Cyprus AMC April 2011");
		disasterNameList.add ("Aquila");

	    if (getUserName () != getString(R.string.noPreference)){
	    	platformLogIn();	// Instantiate the Societies platform
	    }
	    
	} //onCreate

	public void platformLogIn () {

//TODO: catch exception if
//		- SOCIETIES platform is not installed on this node.
//		- user and password are not correct
		
		//Instantiate iDisasterSoc which will give a handle to the platform components
    	iDisasterSoc = new SocietiesApp (getUserName (), getPassword ());		
		platformLoggedIn = true;

	}

	public String getUserName () {
		return preferences.getString ("pref.username",getString(R.string.noPreference));
	}

	public void setUserName (String name, String password) {
    	editor.putString ("pref.username", name);
    	editor.putString ("pref.password", password);
    	editor.commit ();    	
	}

	public String getPassword () {
		return preferences.getString ("pref.password",getString(R.string.noPreference));
	}

	public String getDisasterName () {
		return preferences.getString ("pref.disastername",getString(R.string.noPreference));
	}

	public void setDisasterName (String name) {
    	editor.putString ("pref.disastername", name);
    	editor.commit ();
		
	}

/**
* showDialog is used under testing
* parameters: activity context, message to be displayed, button text
*/
	public void showDialog (Context c, String displayMessage, String buttonText) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(displayMessage)
			.setCancelable(false)
			.setPositiveButton (buttonText, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
		    	   return;
		         }
		    });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
