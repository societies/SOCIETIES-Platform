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


import org.societies.api.css.management.ICssRecord;
import org.societies.api.css.management.ISocietiesApp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * The application for managing common resources used by 
 * iDisaster application components.
 * 
 * @author 	Babak.Farshchian@sintef.no
 *			Jacqueline.Floch@sintef.no
 *
 */
public class iDisasterApplication extends Application {

	private static iDisasterApplication singleton; // Reference to the single instance of the Application
	
	public static final String PREFS_NAME = "DisasterPrefsFile"; // Preferences file
	
    String societiesServer = "server.societies.eu"; // The name of the server where cloud node is hosted
    String username = "Babak"; // username to log into societiesServer
    String password = "SocietieS"; // password for username.
    ISocietiesApp iDisasterSoc; // represents access to the SOCIETIES platform.
    ICssRecord cssRecord; // Represents information about the user of the application. to be populated.
    String cssId;  //TODO: Find out which class CssId is.


	// returns application instance
	public static iDisasterApplication getinstance () {
		return singleton;
	}
	
	@Override
	public final void onCreate() {

		super.onCreate ();
		singleton = this;

	    // Restore preferences from preferences file
		// If the preferences file by this name does not exist, it is be created
		// when an editor is retrieved and changes are committed.
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
//	    editor.commit ();
	    
	    // Test setting preferences 
	    String testname = settings.getString ("username","");
	    testname = "Babak";
	    editor.putString ("username", testname);	    
	    
	}//onCreate
	
}
