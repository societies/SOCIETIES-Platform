/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.phongegap;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Contacts.People;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;


import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;


import org.societies.android.platform.SocialContract;
import com.google.gson.Gson;

/**
 * PhoneGap plugin to allow the CISManager service to be used by HTML web views.
 * 
 * Note: As a PhoneGap plugin is not a standard Android component a lot of assumed 
 * functionality such as creating intents and binding to services is not automatic. The 
 * Plugin class does however have an application context, this.ctx, which supplies the 
 * context to allow this functionality to operate.
 * 
 *
 */
public class PluginCISManager extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCISManager.class.getName();

	private static final String CONTENT_PRV_AUTH_URI = "content://org.societies.android.SocialProvider";
	
	private static final String COMUNITIES__STRING_URI = "content://org.societies.android.SocialProvider/communities";
	
	/**
	 * Actions required to bind and unbind to any Android service(s) 
	 * required by this plugin. It is imperative that dependent 
	 * services are binded to before invoking invoking methods.
	 */
	private static final String CONNECT_SERVICE = "connectService";
	private static final String DISCONNECT_SERVICE = "disconnectService";
	/**
	 * Ancilliary functionality
	 */
	private static final String CREATE_CIS = "createCIS";
	
	//Required to match method calls with callbackIds (used by PhoneGap)
	private HashMap<String, String> methodCallbacks;

    private boolean connectedtoCSSManager = false;


    /**
     * Constructor
     */
    public PluginCISManager() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();
    }

    /** TODO: check if we need one
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
 	
    }
    
    /** TODO: check if we need one
     * Unbind from service
     */
    private void disconnectServiceBinding() {
    	
    }


	@Override
	/**
	 * This method is the receiving side of the Javascript-Java bridge
	 * This particular method variant caters for asynchronous method returns 
	 * in situations where the result will be determined in some undefined future instance
	 */
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "execute: " + action + " for callback: " + callbackId);

		
		
		PluginResult result = null;

		if (action.equals(CREATE_CIS)) {

			Log.d(LOG_TAG, "create cis inside cis plugin");
			ContentResolver cr = this.ctx.getContentResolver();
			/*Log.d(LOG_TAG, "run async");
			RunAsync ru = new RunAsync(cr);
			Log.d(LOG_TAG, "run");
			this.ctx.runOnUiThread(ru);
			Log.d(LOG_TAG, "runt");
			AsynchronousQueryHandler qr = ru.getQr();
			Log.d(LOG_TAG, "async handler");*/
			
			ContentValues createCISValue = new ContentValues();
			createCISValue.put(SocialContract.Community.GLOBAL_ID , "flamengo.societies.org");
			createCISValue.put(SocialContract.Community.TYPE , "futebol");
			createCISValue.put(SocialContract.Community.NAME , "Flamengo");
//			createCISValue.put(SocialContract.Community.DISPLAY_NAME , "FLA");
			createCISValue.put(SocialContract.Community.OWNER_ID, "babak@societies.org");
//			createCISValue.put(SocialContract.Community.CREATION_DATE , "Today");
//			createCISValue.put(SocialContract.Community.MEMBERSHIP_TYPE, "Open");
			createCISValue.put(SocialContract.Community.DIRTY , "yes");
			
			cr.query(RawContacts.CONTENT_URI, null, null, null, null);
			
			Uri COMUNITIES_URI = Uri.parse(COMUNITIES__STRING_URI);
			Log.d(LOG_TAG, "before start insert");
			cr.insert(COMUNITIES_URI, createCISValue);
			Log.d(LOG_TAG, "inserted ok");
			//qr.startInsert(1, callbackId, COMUNITIES_URI, createCISValue);
			

			//result = new PluginResult(PluginResult.Status.NO_RESULT);
			//result.setKeepCallback(true);
			result = new PluginResult(PluginResult.Status.OK);
			result.setKeepCallback(false);
			Log.d(LOG_TAG, "going to return");

            return result;
		} 


		return result;	
	}

	@Override
	/**
	 * Unbind from service to prevent service being kept alive
	 */
	public void onDestroy() {
		disconnectServiceBinding();
	}
	
	class RunAsync implements Runnable{

		ContentResolver cr;
		AsynchronousQueryHandler qr;
		
		RunAsync(ContentResolver cr){
			this.cr = cr;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			qr = new AsynchronousQueryHandler(cr);
		}
		
		public AsynchronousQueryHandler getQr(){
			return qr;
		}
		
	}
	
	
    class AsynchronousQueryHandler extends AsyncQueryHandler{
    	ContentResolver cr;
    	public AsynchronousQueryHandler(ContentResolver cr){
    		super(cr);
    		this.cr = cr;
    	}
    	

    	protected void onQueryComplete(int token, Object cookie,Cursor cursor){
    		Log.d(LOG_TAG, "onQueryComplete from PluginCISManager");
    		if(token != 1){
    			Log.d(LOG_TAG, "onQueryComplete from PluginCISManager has token diff fra 1");
    		}
    		
    	}
    	
    	protected void onInsertComplete(int token,Object cookie,Uri uri){
    		Log.d(LOG_TAG, "onInsertComplete from PluginCISManager");
    		if(token != 1){
    			Log.d(LOG_TAG, "onInsertComplete from PluginCISManager has token diff fra 1");
				PluginResult result = new PluginResult(PluginResult.Status.ERROR);
				result.setKeepCallback(false);
    		}else{
    			String[] projection = {SocialContract.Community.GLOBAL_ID};
    			Cursor cur = cr.query(uri, projection, null, null, null);
    			if(cur != null)
    			{
    			   cur.moveToFirst();
    			   String jid = cur.getString(0);
    			   Log.d(LOG_TAG, "queried jid" + jid);
    			   
    			   JSONObject critJson = new JSONObject();
    			   JSONObject cisJson = new JSONObject();
    			   try{
    			   
    			    
    			    critJson.accumulate("attribute", "location");
    			    critJson.accumulate("operation", "diff");
    			    critJson.accumulate("value", "Peru");
    			    
    		        
    		        cisJson.accumulate("cisName", "test");
    		        cisJson.accumulate("cisType", "volley");
    		        cisJson.accumulate("cisOwner", "thomas@soc.com");
    		        cisJson.accumulate("cisCriteria", critJson);
    		        cisJson.accumulate("cisDescription", "desc");
    		        cisJson.accumulate("cisRecordIdentity", "jid@soc.com");
    			   }catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

    		        
	   				PluginResult result = new PluginResult(PluginResult.Status.OK, cisJson); // TODO: change last string to json cis
	   				result.setKeepCallback(false);
	   				PluginCISManager.this.success(result, cookie.toString());
	   				
//	   				Handler handler = new Handler(Looper.getMainLooper());
//	   				handler.getLooper().getThread().
	   				
    			} else {
    			  // content Uri was invalid or some other error occurred 
    			}
    			
    			
    		}
    		
    	}
    	
    		
    }
    
  
}
