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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.platform.content.CssRecordDAO;
import org.societies.android.platform.cssmanager.ServiceCSSManagerLocal.LocalCSSManagerBinder;
import org.societies.android.platform.cssmanager.LocalCssDirectoryService;
import org.societies.android.platform.cssmanager.ServiceCSSManagerLocal;
import org.societies.android.platform.cssmanager.LocalCssDirectoryService.LocalCssDirectoryBinder;
import org.societies.android.api.css.directory.IAndroidCssDirectory;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.utilities.DBC.Dbc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phonegap.api.LOG;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;

/**
 * PhoneGap plugin to allow the CSSManager service to be used by HTML web views.
 * 
 * Note: As a PhoneGap plugin is not a standard Android component a lot of assumed 
 * functionality such as creating intents and binding to services is not automatic. The 
 * Plugin class does however have an application context, this.ctx, which supplies the 
 * context to allow this functionality to operate.
 * 
 *
 */
public class PluginCSSManager extends Plugin {
	//Logging tag
	private static final String LOG_TAG = PluginCSSManager.class.getName();

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
	private static final String READ_CSSRECORD = "readCSSRecord";
	
	//Required to match method calls with callbackIds (used by PhoneGap)
	private HashMap<String, String> methodCallbacks;

    private IAndroidCSSManager localCSSManager;
    private boolean connectedtoCSSManager = false;

    private IAndroidCssDirectory serviceCssDir;
    private boolean serviceCssDirConnected;
    /**
     * Constructor
     */
    public PluginCSSManager() {
    	super();
    	this.methodCallbacks = new HashMap<String, String>();
    }

    /**
     * CSSManager service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection ccsManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from LocalCSSManager service");
        	connectedtoCSSManager = false;

        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to LocalCSSManager service");
        	//get a local binder
        	LocalCSSManagerBinder binder = (LocalCSSManagerBinder) service;
            //obtain the service's API
            localCSSManager = (IAndroidCSSManager) binder.getService();
            connectedtoCSSManager = true;
            
			String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(CONNECT_SERVICE);

			PluginResult result = new PluginResult(PluginResult.Status.OK, "connected");
			result.setKeepCallback(false);
			PluginCSSManager.this.success(result, methodCallbackId);

        }
    };

    /**
     * CssDirectory service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back if service connection lost
     */
    private ServiceConnection cssDirectoryConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from LocalCSSDirectory service");
        	serviceCssDirConnected = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to LocalCSSDirectory service");
        	//GET LOCAL BINDER
        	LocalCssDirectoryBinder binder = (LocalCssDirectoryBinder) service;
            //OBTAIN  ICssDirectory API
            serviceCssDir = (IAndroidCssDirectory) binder.getService();
            serviceCssDirConnected = true;
        }
    };
    
    /**
     * Bind to the target service
     */
    private void initialiseServiceBinding() {
    	//Create intent to bind to CSSManager
    	Intent cssManagerintent = new Intent(this.ctx.getContext(), ServiceCSSManagerLocal.class);
    	this.ctx.getContext().bindService(cssManagerintent, ccsManagerConnection, Context.BIND_AUTO_CREATE);

    	//Create intent to bind to CSSDirectory
    	Intent cssDirectoryintent = new Intent(this.ctx.getContext(), LocalCssDirectoryService.class);
    	this.ctx.getContext().bindService(cssDirectoryintent, cssDirectoryConnection, Context.BIND_AUTO_CREATE);

    	//register broadcast receiver to receive CSSManager return values 
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(IAndroidCSSManager.LOGIN_CSS);
        intentFilter.addAction(IAndroidCSSManager.LOGOUT_CSS);
        intentFilter.addAction(IAndroidCSSManager.REGISTER_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.LOGIN_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.LOGOUT_XMPP_SERVER);
        intentFilter.addAction(IAndroidCSSManager.MODIFY_ANDROID_CSS_RECORD);
        intentFilter.addAction(IAndroidCSSManager.SUGGESTED_FRIENDS);
        intentFilter.addAction(IAndroidCSSManager.GET_CSS_FRIENDS);
        intentFilter.addAction(IAndroidCSSManager.GET_FRIEND_REQUESTS);
        intentFilter.addAction(IAndroidCSSManager.READ_PROFILE_REMOTE);
        intentFilter.addAction(IAndroidCSSManager.START_APP_SERVICES);
        intentFilter.addAction(IAndroidCSSManager.STOP_APP_SERVICES);
        
        intentFilter.addAction(IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS);
        intentFilter.addAction(IAndroidCssDirectory.FIND_FOR_ALL_CSS);
        
        this.ctx.getContext().registerReceiver(new bReceiver(), intentFilter);    	
    }
    
    /**
     * Unbind from service
     */
    private void disconnectServiceBinding() {
    	if (connectedtoCSSManager) {
    		Log.d(LOG_TAG,"Still connected - disconnecting CSSManager service");
    		this.ctx.getContext().unbindService(ccsManagerConnection);
    	}
    	
    	if (serviceCssDirConnected) {
    		Log.d(LOG_TAG,"Still connected - disconnecting CSSDirectory service");
    		this.ctx.getContext().unbindService(cssDirectoryConnection);
    	}
    }
	
	/**
	 * This method is the receiving side of the Javascript-Java bridge
	 * This particular method variant caters for asynchronous method returns 
	 * in situations where the result will be determined in some undefined future instance
	 */
    @Override
    public PluginResult execute(String action, JSONArray data, String callbackId) {
		Log.d(LOG_TAG, "execute: " + action + " for callback: " + callbackId);
		PluginResult result = null;

		if (action.equals(CONNECT_SERVICE)) {

			if (!connectedtoCSSManager) {
				Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
				this.methodCallbacks.put(action, callbackId);

				result = new PluginResult(PluginResult.Status.NO_RESULT);
	            result.setKeepCallback(true);

				this.initialiseServiceBinding();
			} else {
	            result = new PluginResult(PluginResult.Status.OK, "connected");
	            result.setKeepCallback(false);
			}
            
            return result;
		} 

		//uses synchronous call to ensure that service is unbound
		if (action.equals(DISCONNECT_SERVICE)) {

			this.disconnectServiceBinding();

            result = new PluginResult(PluginResult.Status.OK, "disconnected");
            result.setKeepCallback(false);

            return result;
		} 

		//uses synchronous call to current local cached CSSRecord
		if (action.equals(READ_CSSRECORD)) {
			CssRecordDAO cssRecordDAO = new CssRecordDAO(this.ctx.getContext());
			
			CssRecord record = cssRecordDAO.readCSSrecord();
			if (null != record) {
	            result = new PluginResult(PluginResult.Status.OK, convertCssRecord(cssRecordDAO.readCSSrecord()));
			} else {
	            result = new PluginResult(PluginResult.Status.ERROR, "no CSS Record");
			}
            result.setKeepCallback(false);

            return result;
		}
		
		//uses asynchronous calls
		if (this.validRemoteCall(action) && this.connectedtoCSSManager) {
			
			Log.d(LOG_TAG, "adding to Map store: " + callbackId + " for action: " + action);
			this.methodCallbacks.put(action, callbackId);
			
			//Call local service method
			if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 4))) {
				
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - hosting location: " + data.getJSONObject(1).getString("cssHostingLocation"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));

					this.localCSSManager.loginCSS(data.getString(0), createCssRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 21))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));

					this.localCSSManager.startAppServices(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 22))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));

					this.localCSSManager.stopAppServices(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 5))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - hosting location: " + data.getJSONObject(1).getString("cssHostingLocation"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));

					this.localCSSManager.logoutCSS(data.getString(0), createCssRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 0))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					this.localCSSManager.registerXMPPServer(data.getString(0), createCssRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 2))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 1 - identity: " + data.getJSONObject(1).getString("cssIdentity"));
					Log.d(LOG_TAG, "parameter 1 - password: " + data.getJSONObject(1).getString("password"));
					Log.d(LOG_TAG, "parameter 1 - domain server: " + data.getJSONObject(1).getString("domainServer"));

					this.localCSSManager.loginXMPPServer(data.getString(0), createCssRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 3))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					this.localCSSManager.logoutXMPPServer(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 11))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					Log.d(LOG_TAG, "parameter 2 - forename: " + data.getJSONObject(1).getString("foreName"));
					Log.d(LOG_TAG, "parameter 3 - name: " + data.getJSONObject(1).getString("name"));
					this.localCSSManager.modifyAndroidCSSRecord(data.getString(0), createCssRecord(data.getJSONObject(1)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 15))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					this.localCSSManager.getCssFriends(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 16))) {
				try {
					Log.d(LOG_TAG, "parameter 0: " + data.getString(0));
					this.localCSSManager.getSuggestedFriends(data.getString(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 17))) {
				try {
					this.localCSSManager.readProfileRemote(data.getString(0), data.getString(1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 18))) {
				try {
					this.localCSSManager.sendFriendRequest(data.getString(0), data.getString(1));
					result = new PluginResult(PluginResult.Status.OK);
		            result.setKeepCallback(false);
		            return result;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 19))) {
				try {
					this.localCSSManager.getFriendRequests(data.getString(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}  else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 20))) {
				try {
					this.localCSSManager.acceptFriendRequest(data.getString(0), data.getString(1));
					result = new PluginResult(PluginResult.Status.OK);
		            result.setKeepCallback(false);
		            return result;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}  
			//>>>>>>>>>>>>>>>>>>>>>>>>IAndroidCisDirectory methods >>>>>>>>>>>>>>>>>>>>>>>
			else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCssDirectory.methodsArray, 0))) {
				try {
					this.serviceCssDir.findForAllCss(data.getString(0), data.getString(1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCssDirectory.methodsArray, 1))) {
				try {
					this.serviceCssDir.findAllCssAdvertisementRecords(data.getString(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
			
			// Don't return any result now, since status results will be sent when events come in from broadcast receiver 
            result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
		} else {
			Log.d(LOG_TAG, "Plugin target method not supported");
			//if method does not exist send synchronous error result
            result = new PluginResult(PluginResult.Status.ERROR);
            result.setKeepCallback(false);
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
	
	/**
	 * Return result to Javascript call for the App Services control functions
	 * 
	 * @param methodCallbackId
	 * @param intent
	 * @param key
	 * 
	 */
	private void sendJavascriptResultForAppControl(String methodCallbackId, Intent intent, String key) {
		Log.d(LOG_TAG, "sendJavascriptResultForAppControl called for intent: " + intent.getAction() + " and callback ID: " + methodCallbackId);	

		boolean resultStatus = intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
		Log.d(LOG_TAG, "Start/Stop app services return result: " + resultStatus);

		PluginResult pResult = null;
		
		if (resultStatus) {
			pResult = new PluginResult(PluginResult.Status.OK, intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false));
			pResult.setKeepCallback(false);
			this.success(pResult, methodCallbackId);
		} else {
			pResult = new PluginResult(PluginResult.Status.ERROR);
			pResult.setKeepCallback(false);
			this.error(pResult, methodCallbackId);
		}
		//remove callback ID for given method invocation
		PluginCSSManager.this.methodCallbacks.remove(key);

		Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
	}
	/**
	 * Return result to Javascript call
	 * 
	 * @param callbackId
	 * @param intent
	 * @return boolean
	 */
	private boolean sendJavascriptResult(String methodCallbackId, Intent intent, String key) {
		Log.d(LOG_TAG, "returnJavascriptResult called for intent: " + intent.getAction() + " and callback ID: " + methodCallbackId);	
		CssRecord cssRecord = null;
		CssAdvertisementRecord advertRecord [] = null;
		PluginResult result = null;
		boolean resultStatus = false;
		
		//ADVERTISEMENT RECORDS	
		if (IAndroidCSSManager.GET_FRIEND_REQUESTS==intent.getAction() || IAndroidCSSManager.GET_CSS_FRIENDS==intent.getAction() || IAndroidCSSManager.SUGGESTED_FRIENDS==intent.getAction()) {
			resultStatus = intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
			Parcelable parcels[] = intent.getParcelableArrayExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY);
			advertRecord = new CssAdvertisementRecord [parcels.length];
			System.arraycopy(parcels, 0, advertRecord, 0, parcels.length);
		} 
		else if (IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS==intent.getAction() || IAndroidCssDirectory.FIND_FOR_ALL_CSS==intent.getAction() ) {
			resultStatus = intent.getBooleanExtra(IAndroidCssDirectory.INTENT_RETURN_STATUS_KEY, false);
			Parcelable parcels[] = intent.getParcelableArrayExtra(IAndroidCssDirectory.INTENT_RETURN_VALUE_KEY);
			advertRecord = new CssAdvertisementRecord [parcels.length];
			System.arraycopy(parcels, 0, advertRecord, 0, parcels.length);
		} 
		//CSS RECORDS 
		else  {
			resultStatus = intent.getBooleanExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
			cssRecord = (CssRecord) intent.getParcelableExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY);
		}
		
		Log.d(LOG_TAG, "Result status of remote call: " + resultStatus);
		if (resultStatus) {
			if (IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS==intent.getAction() || 
					   IAndroidCssDirectory.FIND_FOR_ALL_CSS==intent.getAction() ||
					   IAndroidCSSManager.GET_FRIEND_REQUESTS==intent.getAction() ||
					   IAndroidCSSManager.GET_CSS_FRIENDS == intent.getAction() || 
					   IAndroidCSSManager.SUGGESTED_FRIENDS == intent.getAction()) {
				result = new PluginResult(PluginResult.Status.OK, convertCssAdvertisements(advertRecord));
			} else {
				result = new PluginResult(PluginResult.Status.OK, convertCssRecord(cssRecord));
			}
			result.setKeepCallback(false);
			this.success(result, methodCallbackId);
		} else {
			result = new PluginResult(PluginResult.Status.ERROR);
			result.setKeepCallback(false);
			this.error(result, methodCallbackId);
		}
		
		//remove callback ID for given method invocation
		PluginCSSManager.this.methodCallbacks.remove(key);

		Log.d(LOG_TAG, "Plugin success method called, target: " + methodCallbackId);
		return resultStatus;
	}
	
    /**
     * Creates a JSONObject for a given {@link ACssAdvertisementRecord}
     * 
     * @param node
     * @return JSONObject 
     */
    private JSONArray convertCssAdvertisements(CssAdvertisementRecord adverts []) {
        JSONArray jArray = null;
		Gson gson = new Gson();
		try {
			jArray =  new JSONArray (new JSONTokener(gson.toJson(adverts)));
			LOG.d(LOG_TAG, gson.toJson(adverts));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jArray;
    }

	
    /**
     * Creates a JSONObject for a given AndroidCSSNode
     * 
     * @param node
     * @return JSONObject 
     */
    private JSONObject convertCssNode(CssNode node) {
        JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(node)).nextValue();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }

    /**
     * Creates a JSONObject for a given AndroidCSSRecord
     * 
     * @param record
     * @return JSONObject
     */
    private JSONObject convertCssRecord(CssRecord record) {
        JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(record)).nextValue();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jObj;
    }
    /**
     * Creates a AndroidCSSNode from a JSONObject
     * 
     * @param jRecord JSONObject representation of node
     * @return AndroidCSSNode 
     */
    private CssNode createCssNode(JSONObject jNode) {
    	CssNode anode = null;

    	Gson gson = new Gson();
    	
    	anode = gson.fromJson(jNode.toString(), CssNode.class);
    	return anode;
    }
    /**
     * Creates a AndroidCSSRecord from a JSONObject. Required as GSON
     * could not cope with class structure.
     * 
     * @param jNode JSONObject representation of record
     * @return AndroidCSSRecord 
     */
    private CssRecord createCssRecord(JSONObject jRecord) {
    	Dbc.require("JSON object cannot be null", jRecord != null);
    	CssRecord aRecord = new CssRecord();

    	
    	try {
//			aRecord.setCssHostingLocation(jRecord.getString("cssHostingLocation"));
			aRecord.setCssIdentity(jRecord.getString("cssIdentity"));
//	    	aRecord.setCssInactivation(jRecord.getString("cssInactivation"));
//	    	aRecord.setCssRegistration(jRecord.getString("cssRegistration"));
//	    	aRecord.setCssUpTime(jRecord.getInt("cssUpTime"));
	    	aRecord.setDomainServer(jRecord.getString("domainServer"));
	    	aRecord.setEmailID(jRecord.getString("emailID"));
	    	aRecord.setEntity(jRecord.getInt("entity"));
	    	aRecord.setForeName(jRecord.getString("foreName"));
	    	aRecord.setHomeLocation(jRecord.getString("homeLocation"));
//	    	aRecord.setIdentityName(jRecord.getString("identityName"));
//	    	aRecord.setImID(jRecord.getString("imID"));
	    	aRecord.setName(jRecord.getString("name"));
	    	aRecord.setPassword(jRecord.getString("password"));
//	    	aRecord.setPresence(jRecord.getInt("presence"));
	    	aRecord.setSex(jRecord.getInt("sex"));
//	    	aRecord.setSocialURI(jRecord.getString("socialURI"));
//	    	aRecord.setStatus(jRecord.getInt("status"));
	    	
	    	JSONArray cssNodes = jRecord.getJSONArray("cssNodes");
	    	List<CssNode> nodeList = new ArrayList<CssNode>();
	    	for (int i = 0; i < cssNodes.length(); i++) {
	    		nodeList.add(createCssNode(cssNodes.getJSONObject(i)));
	    	}
	    	//CssNode aNodes [] = new CssNode[cssNodes.length()];
	    	//for (int i = 0; i < cssNodes.length(); i++) {
	    	//	aNodes[i] = createCssNode(cssNodes.getJSONObject(i));
	    	//}
	    	aRecord.setCssNodes(nodeList);
//	    	aRecord.setArchiveCSSNodes(arg0)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Log.d(LOG_TAG, "Converted CSSRecord identity: " + aRecord.getCssIdentity());
    	return aRecord;
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     */
    private class bReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IAndroidCSSManager.LOGIN_CSS)) {
				Toast.makeText(PluginCSSManager.this.ctx.getContext(), "Login successful", Toast.LENGTH_SHORT).show();
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 4);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}

			} else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_CSS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 5);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
				
			} else if (intent.getAction().equals(IAndroidCSSManager.REGISTER_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 0);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
				
			} else if (intent.getAction().equals(IAndroidCSSManager.LOGIN_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 2);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.LOGOUT_XMPP_SERVER)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 3);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.MODIFY_ANDROID_CSS_RECORD)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 11);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.GET_CSS_FRIENDS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 15);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.SUGGESTED_FRIENDS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 16);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.READ_PROFILE_REMOTE)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 17);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.GET_FRIEND_REQUESTS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 19);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.START_APP_SERVICES)) {
				Toast.makeText(PluginCSSManager.this.ctx.getContext(), "Starting services...", Toast.LENGTH_SHORT).show();
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 21);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResultForAppControl(methodCallbackId, intent, mapKey);
				}
			} else if (intent.getAction().equals(IAndroidCSSManager.STOP_APP_SERVICES)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, 22);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResultForAppControl(methodCallbackId, intent, mapKey);
				}
			} 
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IAndroidCssDirectory >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
			else if (intent.getAction().equals(IAndroidCssDirectory.FIND_FOR_ALL_CSS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCssDirectory.methodsArray, 0);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} 
			else if (intent.getAction().equals(IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS)) {
				String mapKey = ServiceMethodTranslator.getMethodName(IAndroidCssDirectory.methodsArray, 1);
				
				String methodCallbackId = PluginCSSManager.this.methodCallbacks.get(mapKey);
				if (methodCallbackId != null) {
					PluginCSSManager.this.sendJavascriptResult(methodCallbackId, intent, mapKey);
				}
			} 
		}
	};

    /**
     * Determine if the Javascript action is a valid.
     * 
     * N.B. Assumes that the Javascript method name is the exact same as the 
     * Java implementation. 
     * 
     * @param action
     * @return boolean
     */
    private boolean validRemoteCall(String action) {
    	boolean retValue = false;
    	for (int i = 0; i < IAndroidCSSManager.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCSSManager.methodsArray, i))) {
        		return true;
        	}
    	}
    	for (int i = 0; i < IAndroidCssDirectory.methodsArray.length; i++) {
        	if (action.equals(ServiceMethodTranslator.getMethodName(IAndroidCssDirectory.methodsArray, i))) {
        		return true;
        	}
    	}
    	if (!retValue) {
    		Log.d(LOG_TAG, "Unable to find method name for given action: " + action);
    	}
    	return retValue;
    }
}
