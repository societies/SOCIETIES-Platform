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

package org.societies.android.platform.servicemonitor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.societies.android.api.servicelifecycle.IServiceUtilities;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;


/**
 * This class acts as the base functionality for the {@link IServiceUtilities} service, both local and remote.
 * 
 *
 */
public class ServiceUtilitiesBase implements IServiceUtilities { 
	//Logging tag
    private static final String LOG_TAG = ServiceUtilitiesBase.class.getName();
    private ClientCommunicationMgr commMgr;
    private Context androidContext;
    private boolean restrictBroadcast;

    
    public ServiceUtilitiesBase(Context androidContext) {
    	this(androidContext, true);
    }
    
    /**
     * Constructor
     */
    public ServiceUtilitiesBase(Context androidContext, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Implementation of IServiceUtilities
     */
    
	/* @see org.societies.android.api.servicelifecycle.IServiceUtilities#getMyServiceId(java.lang.String) */
	public ServiceResourceIdentifier getMyServiceId(String client) {
		Log.d(LOG_TAG, "Calling getMyServiceId from client: " + client);
		
		AsynGetMyServiceId methodAsync = new AsynGetMyServiceId();
		String params [] = {client};
		methodAsync.execute(params);

		return null;
	}

	private String getCallingAppName(String client) {
		List<PackageInfo> packs = this.androidContext.getPackageManager().getInstalledPackages(0);
		String appName = client; //DEFAULT BACK TO PACKAGE
		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);
			if (p.versionName == null) { //A SYSTEM PACKAGE - IGNORE
				continue ;
			}
			//FILTER PACKAGES
			String pack = p.packageName;
			if (pack.contains(client))
				appName = p.applicationInfo.loadLabel(this.androidContext.getPackageManager()).toString();
		}
		return appName;
	}

	/**
	 * AsyncTask classes required to carry out threaded tasks. These classes should be used where it is estimated that 
	 * the task length is unknown or potentially long. While direct usage of the Communications components for remote 
	 * method invocation is an explicitly asynchronous operation, other usage is not and in general the use of these types of classes
	 * is encouraged. Remember, Android Not Responding (ANR) exceptions will be invoked if the main application thread is abused
	 * and the application will be closed down by Android very soon after.
	 * 
	 * Although the result of an AsyncTask can be obtained by using <AsyncTask Object>.get() it's not a good idea as 
	 * it will effectively block the parent method until the result is delivered back and so render the use if the AsyncTask
	 * class ineffective. Use Intents as an asynchronous callback mechanism.
	 */
		
	/**
	 * This class carries out the getMyServiceId method call asynchronously
	 */
	private class AsynGetMyServiceId extends AsyncTask<String, Void, String[]> {
		
		/**
		 * Carry out compute task 
		 */
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			String results [] = new String[1];
			results[0] = params[0];
			
			ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
			String appName = getCallingAppName(params[0]);
			try {
				String uri = "http://" + commMgr.getIdManager().getThisNetworkNode().getJid() + "/" + appName;
			
				sri.setIdentifier(new URI(uri));
			} catch (URISyntaxException e) {
				Log.d(LOG_TAG, "Exception parsing URI");
			} catch (InvalidFormatException e) {
				Log.d(LOG_TAG, "Exception getting Network node id");
			}
			sri.setServiceInstanceIdentifier(params[0]);

			//Communicate result by Intent back to the plugin
			if (params[0] != null) {
				Intent intent = new Intent(GET_MY_SERVICE_ID);
				intent.putExtra(INTENT_RETURN_VALUE, (Parcelable)sri);
				if (restrictBroadcast)
					intent.setPackage(params[0]);
				ServiceUtilitiesBase.this.androidContext.sendBroadcast(intent);
			}
	 
			return results;
		}
		
		/**
		 * Handle the communication of the result
		 */
		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}

}
