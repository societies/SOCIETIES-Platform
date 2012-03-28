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

package org.societies.android.platform.cssmanager;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.platform.intents.AndroidCoreIntents;
import org.societies.android.platform.interfaces.IAndroidCSSManager;
import org.societies.api.android.internal.model.AndroidCSSRecord;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.utilities.DBC.Dbc;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;

public class LocalCSSManagerService extends Service implements IAndroidCSSManager {

	//Logging tag
	private static final String LOG_TAG = LocalCSSManagerService.class.getName();

	//XMPP Communication namespaces and associated entities
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList(
    		"http://societies.org/api/schema/cssmanagement");
    private static final List<String> PACKAGES = Arrays.asList(
		"org.societies.api.schema.cssmanagement");
    //currently hard coded destination of communication
    private static final String DESTINATION = "xcmanager.societies.local";
    
	/**
	 * CSS Manager intents
	 */
	//Intents corresponding to return values of methods
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.cssmanager.ReturnValue";

	public static final String CHANGE_CSS_NODE_STATUS = "org.societies.android.platform.cssmanager.CHANGE_CSS_NODE_STATUS";
	public static final String GET_ANDROID_CSS_RECORD = "org.societies.android.platform.cssmanager.GET_ANDROID_CSS_RECORD";
	public static final String LOGIN_CSS = "org.societies.android.platform.cssmanager.LOGIN_CSS";
	public static final String LOGIN_XMPP_SERVER = "org.societies.android.platform.cssmanager.LOGIN_XMPP_SERVER";
	public static final String LOGOUT_CSS = "org.societies.android.platform.cssmanager.LOGOUT_CSS";
	public static final String LOGOUT_XMPP_SERVER = "org.societies.android.platform.cssmanager.LOGOUT_XMPP_SERVER";
	public static final String MODIFY_ANDROID_CSS_RECORD = "org.societies.android.platform.cssmanager.MODIFY_ANDROID_CSS_RECORD";
	public static final String REGISTER_CSS = "org.societies.android.platform.cssmanager.REGISTER_CSS";
	public static final String REGISTER_CSS_DEVICE = "org.societies.android.platform.cssmanager.REGISTER_CSS_DEVICE";
	public static final String REGISTER_XMPP_SERVER = "org.societies.android.platform.cssmanager.REGISTER_XMPP_SERVER";
	public static final String SET_PRESENCE_STATUS = "org.societies.android.platform.cssmanager.SET_PRESENCE_STATUS";
	public static final String SYNCH_PROFILE = "org.societies.android.platform.cssmanager.SYNCH_PROFILE";
	public static final String UNREGISTER_CSS = "org.societies.android.platform.cssmanager.UNREGISTER_CSS";
	public static final String UNREGISTER_CSS_DEVICE = "org.societies.android.platform.cssmanager.UNREGISTER_CSS_DEVICE";
	public static final String UNREGISTER_XMPP_SERVER = "org.societies.android.platform.cssmanager.UNREGISTER_XMPP_SERVER";


    private final IIdentity toXCManager = null;
    private ClientCommunicationMgr ccm;

	private IBinder binder = null;
    
//	private Messenger inMessenger;
	private AndroidCSSRecord cssRecord;

	@Override
	public void onCreate () {
//		this.inMessenger = new Messenger(new RemoteServiceHandler(this.getClass(), this));
		
		this.binder = new LocalBinder();

		//This should replaced with persisted value if available
		this.cssRecord = null;
		this.ccm = new ClientCommunicationMgr(this);

		Log.d(LOG_TAG, "CSSManager service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CSSManager service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	 public class LocalBinder extends Binder {
		 public LocalCSSManagerService getService() {
	            return LocalCSSManagerService.this;
	        }
	    }

	@Override
	public IBinder onBind(Intent arg0) {
//		return inMessenger.getBinder();
		return this.binder;
	}

	//Service API that service offers

	@Override
	public AndroidCSSRecord changeCSSNodeStatus(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "changeCSSNodeStatus called with client: " + client);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AndroidCSSRecord getAndroidCSSRecord(String client) {
		Log.d(LOG_TAG, "getAndroidCSSRecord called with client: " + client);
		return cssRecord;
	}

	@Override
	public AndroidCSSRecord loginCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "loginCSS called with client: " + client);
		
		Dbc.require("CSS record cannot be null", record != null);

		ccm.register(ELEMENT_NAMES, new CSSManagerCallback(client, LOGIN_CSS));
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setProfile(record);
		messageBean.setMethod(MethodType.LOGIN_CSS);

		Stanza stanza = new Stanza(toXCManager);
		
		ICommCallback callback = new CSSManagerCallback(client, LOGIN_CSS);
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage());
        }

		return null;
	}

	@Override
	public boolean loginXMPPServer(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "loginXMPPServer called with client: " + client);

		Dbc.require("CSS record cannot be null", record != null);
		boolean retValue = true;
		
		ccm.register(ELEMENT_NAMES, new CSSManagerCallback(client, LOGIN_XMPP_SERVER));
		
		/**
		 * Create intent to broadcast results to interested receivers in the event 
		 * of a client binding to the service using Android IPC
		 */
		if (client != null) {
			Intent intent = new Intent(LOGIN_XMPP_SERVER);
			intent.putExtra(INTENT_RETURN_KEY, retValue);
			intent.setPackage(client);

			
			Log.d(LOG_TAG, "loginXMPPServer sent return value: " + retValue);
			this.sendBroadcast(intent);
		}
		/**
		 * Return value returned to client binding using a local bind
		 */
		Log.d(LOG_TAG, "loginXMPPServer return value: " + retValue);
		return retValue;
	}

	@Override
	public AndroidCSSRecord logoutCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "logoutCSS called with client: " + client);
		Dbc.require("CSS record cannot be null", record != null);

		ccm.register(ELEMENT_NAMES, new CSSManagerCallback(client, LOGOUT_CSS));
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setProfile(record);
		messageBean.setMethod(MethodType.LOGOUT_CSS);

		Stanza stanza = new Stanza(toXCManager);
		
		ICommCallback callback = new CSSManagerCallback(client, LOGOUT_CSS);
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage());
        }
		return null;
	}

	@Override
	public boolean logoutXMPPServer(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "logoutXMPPServer called with client: " + client);

		Dbc.require("CSS record cannot be null", record != null);
		
		boolean retValue = true;
		ccm.unregister(ELEMENT_NAMES, new CSSManagerCallback(client, LOGOUT_XMPP_SERVER));
		/**
		 * Create intent to broadcast results to interested receivers in the event 
		 * of a client binding to the service using Android IPC
		 */
		if (client != null) {
			Intent intent = new Intent(LOGOUT_XMPP_SERVER);
			intent.putExtra(INTENT_RETURN_KEY, retValue);
			intent.setPackage(client);

			Log.d(LOG_TAG, "logoutXMPPServer sent return value: " + retValue);

			this.sendBroadcast(intent);
		}
		/**
		 * Return value returned to client binding using a local bind
		 */
		Log.d(LOG_TAG, "logoutXMPPServer return value: " + retValue);

		return retValue;
	}

	@Override
	public AndroidCSSRecord modifyAndroidCSSRecord(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "modifyAndroidCSSRecord called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord registerCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerCSS called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord registerCSSDevice(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerCSSDevice called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord registerXMPPServer(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerXMPPServer called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord setPresenceStatus(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "setPresenceStatus called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord synchProfile(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "??? called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord unregisterCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterCSS called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord unregisterCSSDevice(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterCSSDevice called with client: " + client);
		return null;
	}

	@Override
	public AndroidCSSRecord unregisterXMPPServer(String client,	AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterXMPPServer called with client: " + client);
		return null;
	}

	private class CSSManagerCallback implements ICommCallback {
		String returnIntent;
		String client;
		
		public CSSManagerCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}


		@Override
		public void receiveError(Stanza arg0, XMPPError arg1) {
			Log.d(LOG_TAG, "Callback receiveError");
			
		}

		@Override
		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "Callback receiveInfo");
			
		}

		@Override
		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "Callback receiveItems");
			
		}

		@Override
		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "Callback receiveMessage");
			
			
		}

		@Override
		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(LOG_TAG, "Callback receiveResult");
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				intent.putExtra(INTENT_RETURN_KEY, (Parcelable)retValue);
				intent.setPackage(client);

				Log.d(LOG_TAG, "Callback receiveResult sent return value: " + retValue);

				LocalCSSManagerService.this.sendBroadcast(intent);
			}
		}
	}
}
