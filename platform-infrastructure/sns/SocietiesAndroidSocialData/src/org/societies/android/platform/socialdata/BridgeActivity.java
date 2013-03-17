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
package org.societies.android.platform.socialdata;

import org.societies.android.platform.socialdata.SocialTokenManager.LocalBinder;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.platfrom.sns.android.socialapp.Constants;
import org.societies.platfrom.sns.android.socialapp.WebActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

/**
 * Activity to do the bridge between a service and a starActivityForResult activity.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class BridgeActivity extends Activity {
	
	private static final String LOG_TAG = BridgeActivity.class.getName();
	
	public static final String EXTRA_SOCIAL_NETWORK = "org.societies.android.platform.socialdata.BridgeActivity.extra.SOCIAL_NETWORK";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		SocialNetwork socialNetwork = (SocialNetwork)intent.getSerializableExtra(EXTRA_SOCIAL_NETWORK);
		
		switch(socialNetwork) {
		case FACEBOOK :
			openBrowser(Constants.FB_URL, Constants.FB_CODE);
			break;
		case FOURSQUARE:
			openBrowser(Constants.FQ_URL, Constants.FQ_CODE);
			break;
		case TWITTER:
			openBrowser(Constants.TW_URL, Constants.TW_CODE);
			break;
		case LINKEDIN:
			openBrowser(Constants.LK_URL, Constants.LK_CODE);
			break;
		}
	}
	
	private void openBrowser(String uri, int requestCode){
		Intent intent = new Intent(this, WebActivity.class);
		intent.putExtra(Constants.SSO_URL, uri);
		startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
		if(resultCode == RESULT_CANCELED) {
			returnEmptyToken(requestCode);
		}
		else {			
			String token = data.getStringExtra(Constants.ACCESS_TOKEN);
			String expires = data.getStringExtra(Constants.TOKEN_EXPIRATION);
			returnToken(socialNetwork(requestCode), token, expires);
		}
	}	
	
	private void returnToken(final SocialNetwork socialNetwork, final String token, final String expires) {
		final ServiceConnection connection = new ServiceConnection() {

	        public void onServiceConnected(ComponentName name, IBinder service) {
	        	Log.d(LOG_TAG, "Connecting to service");

	        	try {
		        	//GET LOCAL BINDER
		            LocalBinder binder = (LocalBinder) service;
		
		            //OBTAIN SERVICE API
		            SocialTokenManager tokenMgr = (SocialTokenManager) binder.getService();
		           	            	        	
		            tokenMgr.returnToken(socialNetwork, token, expires);
		            
		            finish();
	        	} catch (Exception ex) {
	        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
	        	}         	
	        }
	        
	        public void onServiceDisconnected(ComponentName name) {
	        	Log.d(LOG_TAG, "Disconnecting from service");
	        }
		};		
		
		Intent intentService = new Intent(this.getApplicationContext(), SocialTokenManager.class);
        this.getApplicationContext().bindService(intentService, connection, Context.BIND_AUTO_CREATE);
	}
	
	private void returnEmptyToken(int requestCode) {
		returnToken(socialNetwork(requestCode), null, null);
	}
	
	private SocialNetwork socialNetwork(int requestCode) {
		if (requestCode == Constants.FB_CODE){				
			return SocialNetwork.FACEBOOK;			
		}
		else if(requestCode == Constants.TW_CODE){
			return SocialNetwork.TWITTER;
		}
		else if(requestCode == Constants.FQ_CODE){
			return SocialNetwork.FOURSQUARE;
		}	
		else if(requestCode == Constants.LK_CODE) {
			return SocialNetwork.LINKEDIN;
		}
		
		throw new IllegalArgumentException("No social network bound to requestCode: "+requestCode); 
	}
	
	public static void startActivityForSN(Context context, SocialNetwork socialNetwork) {
		Intent intent = new Intent(context, BridgeActivity.class);
		intent.putExtra(EXTRA_SOCIAL_NETWORK, (Parcelable)socialNetwork);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
