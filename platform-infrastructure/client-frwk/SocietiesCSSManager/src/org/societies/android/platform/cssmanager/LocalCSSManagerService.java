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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;


import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.android.api.internal.cssmanager.AndroidCSSRecord;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.android.api.css.directory.ACssAdvertisementRecord;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.management.CSSManagerEnums;
import org.societies.api.schema.css.directory.CssDirectoryBean;
import org.societies.api.schema.css.directory.CssDirectoryBeanResult;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;
import org.societies.android.platform.content.CssRecordDAO;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

/**
 * This Android Service is a Societies core service that implements local CSSManager
 * functionality and where relevant makes remote calls on its OSGi peer.
 *
 */
public class LocalCSSManagerService extends Service implements IAndroidCSSManager {

	//Logging tag
	private static final String LOG_TAG = LocalCSSManagerService.class.getName();
	
	//Notification Tags
	private static final String NEW_CSS_NODE = "New CSS Node";
	private static final String OLD_CSS_NODE = "Old CSS Node";
	private static final String NODE_LOGIN = "Node Logged in";
	
	private static final String ANDROID_PROFILING_NAME = "SocietiesCSSManager";

	//Pubsub packages
	private static final String PUBSUB_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
	//XMPP Communication namespaces and associated entities
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", 
									"cssManagerResultBean", "cssDirectoryBean", "cssDirectoryBeanResult");
    private static final List<String> NAME_SPACES = Arrays.asList(
    		"http://societies.org/api/schema/cssmanagement", "http://societies.org/api/schema/css/directory",
			  "http://societies.org/api/schema/cis/directory");
    private static final List<String> PACKAGES = Arrays.asList(
		"org.societies.api.schema.cssmanagement", "org.societies.api.schema.css.directory",
		  "org.societies.api.schema.cis.directory");
    //default destination of communication - CSS Cloud node
    private static final String DEFAULT_DESTINATION = "xcmanager.societies.local";
    
    private static final List<String> classList = Collections.singletonList(PUBSUB_CLASS);

	/**
	 * CSS Manager intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	//Intents corresponding to return values of methods
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.cssmanager.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.cssmanager.ReturnStatus";

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

	public static final String SUGGESTED_FRIENDS = "org.societies.android.platform.cssmanager.SUGGESTED_FRIENDS";
	public static final String GET_CSS_FRIENDS = "org.societies.android.platform.cssmanager.GET_CSS_FRIENDS";
	public static final String FIND_ALL_CSS_ADVERTISEMENT_RECORDS = "org.societies.android.platform.cssmanager.FIND_ALL_CSS_ADVERTISEMENT_RECORDS";
	public static final String FIND_FOR_ALL_CSS = "org.societies.android.platform.cssmanager.FIND_FOR_ALL_CSS";
	public static final String READ_PROFILE_REMOTE = "org.societies.android.platform.cssmanager.READ_PROFILE_REMOTE";
	public static final String SEND_FRIEND_REQUEST = "org.societies.android.platform.cssmanager.SEND_FRIEND_REQUEST";
	
    private IIdentity cloudNodeIdentity = null;
    private IIdentity domainNodeIdentity = null;
    private ClientCommunicationMgr ccm;

	private IBinder binder = null;
    
//	private Messenger inMessenger;
	private AndroidCSSRecord cssRecord;
	
	private CssRecordDAO cssRecordDAO;
	
	private String cloudCommsDestination = DEFAULT_DESTINATION;
	private String domainCommsDestination = null;
	
	private PubsubClientAndroid pubsubClient = null;
	
	private HashMap<String, Subscriber> pubsubSubscribes = new HashMap<String, Subscriber>();

	
	
	//Service API overrides
	
	@Override
	public void onCreate () {
//		Traceview 
//		Debug.startMethodTracing(ANDROID_PROFILING_NAME);
		
		Log.d(LOG_TAG, "Thread is: " + Thread.currentThread());

		Log.d(LOG_TAG, "CSSManager opening database");
		this.cssRecordDAO = new CssRecordDAO(this);

//		this.inMessenger = new Messenger(new RemoteServiceHandler(this.getClass(), this));
		
		this.binder = new LocalBinder();

		this.cssRecord = null;
		
		Log.d(LOG_TAG, "CSSManager service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CSSManager service terminating");
//		Traceview 
//		Debug.stopMethodTracing();
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
	
	//Client Persistence methods
	
	/**
	 * Insert or update the database with the new version of the CSS Record
	 * 
	 * @param aRecord
	 */
	private void updateLocalCSSrecord(AndroidCSSRecord aRecord) {
		if (cssRecordDAO.cssRecordExists()) {
			this.cssRecordDAO.updateCSSRecord(aRecord);			
		} else {
			this.cssRecordDAO.insertCSSRecord(aRecord);			
		}
	}

	/**
	 * Get the current CSSRecord stored locally
	 *  
	 * @return {@link AndroidCSSRecord} null if no local CSSRecord found
	 */
	private AndroidCSSRecord readLocalCSSRecord() {
		AndroidCSSRecord record = this.cssRecordDAO.readCSSrecord();
		return record;
	}
	//Service API that service offers

	public AndroidCSSRecord changeCSSNodeStatus(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "changeCSSNodeStatus called with client: " + client);
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method call either results in a local persistence access or a remote
	 * action to update the local persistence cache of the CSSRecord
	 */
	public AndroidCSSRecord getAndroidCSSRecord(String client) {
		Log.d(LOG_TAG, "getAndroidCSSRecord called with client: " + client);
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		
		AndroidCSSRecord record = null;
		
		if (null == this.readLocalCSSRecord()) {
			record = this.synchProfile(client, record);
		} else {
			if (client != null) {
				Intent intent = new Intent(GET_ANDROID_CSS_RECORD);
				
				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);

				intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) record);
				intent.setPackage(client);

				LocalCSSManagerService.this.sendBroadcast(intent);
			}

		}
		return null;
	}

	public AndroidCSSRecord loginCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "loginCSS called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		//delay subscribing for Pubsub events until successful login to Domain Server
		Log.d(LOG_TAG, "CSSManager registering for Pubsub events");
		this.registerForPubsub();

		this.assignConnectionParameters();
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		CssRecord localCssrecord = convertAndroidCSSRecord(record);
		
		//add the local Android node information
		localCssrecord.setCssNodes(createAndroidLocalNode());
		
		messageBean.setProfile(localCssrecord);
		messageBean.setMethod(MethodType.LOGIN_CSS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, LOGIN_CSS);

		try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public void loginXMPPServer(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "loginXMPPServer called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		this.ccm = new ClientCommunicationMgr(this);
		
		String params [] = {record.getCssIdentity(), record.getDomainServer(), record.getPassword(), client};
		
		DomainLogin domainLogin = new DomainLogin();
		
		domainLogin.execute(params);

	}

	public AndroidCSSRecord logoutCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "logoutCSS called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);

		Log.d(LOG_TAG, "CSSManager unregistering from Pubsub events");
		this.unregisterFromPubsub();

		ccm.register(ELEMENT_NAMES, new CSSManagerCallback(client, LOGOUT_CSS));
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		CssRecord localCssrecord = convertAndroidCSSRecord(record);
		
		//add the local Android node information
		localCssrecord.setCssNodes(createAndroidLocalNode());
		
		messageBean.setProfile(localCssrecord);

		messageBean.setMethod(MethodType.LOGOUT_CSS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, LOGOUT_CSS);
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 
		return null;
	}

	public void logoutXMPPServer(String client) {
		Log.d(LOG_TAG, "logoutXMPPServer called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
				
		String params [] = {client};

		
		DomainLogout domainLogout = new DomainLogout();
		
		domainLogout.execute(params);

	}

	public AndroidCSSRecord modifyAndroidCSSRecord(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "modifyAndroidCSSRecord called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		CssRecord localCssrecord = convertAndroidCSSRecord(record);
		
		
		messageBean.setProfile(localCssrecord);
		messageBean.setMethod(MethodType.MODIFY_CSS_RECORD);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, MODIFY_ANDROID_CSS_RECORD);

		try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public AndroidCSSRecord registerCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerCSS called with client: " + client);
		return null;
	}

	public AndroidCSSRecord registerCSSDevice(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerCSSDevice called with client: " + client);
		return null;
	}

	public void registerXMPPServer(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "registerXMPPServer called with client: " + client);
		Log.d(LOG_TAG, "registering user: " + record.getCssIdentity() + " at domain: " + record.getDomainServer());
		
		String params [] = {record.getCssIdentity(), record.getDomainServer(), record.getPassword(), client};

		Log.d(LOG_TAG, "Thread is: " + Thread.currentThread());
		
		DomainRegistration domainRegister = new DomainRegistration();
		
		domainRegister.execute(params);
		
	}

	public AndroidCSSRecord setPresenceStatus(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "setPresenceStatus called with client: " + client);
		return null;
	}

	public AndroidCSSRecord synchProfile(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "synchProfile called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setProfile(record);
		messageBean.setMethod(MethodType.SYNCH_PROFILE);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, SYNCH_PROFILE);
		
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public AndroidCSSRecord unregisterCSS(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterCSS called with client: " + client);
		return null;
	}

	public AndroidCSSRecord unregisterCSSDevice(String client, AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterCSSDevice called with client: " + client);
		return null;
	}

	public void unregisterXMPPServer(String client,	AndroidCSSRecord record) {
		Log.d(LOG_TAG, "unregisterXMPPServer called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		
		String params [] = {client};
		
		DomainUnRegistration domainUnreg = new DomainUnRegistration();
		
		domainUnreg.execute(params);

	}
	public List<ACssAdvertisementRecord> findAllCssAdvertisementRecords(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "findAllCssAdvertisementRecords called with client: " + client);
		Log.d(LOG_TAG, "loginCSS called with client: " + client);
		
		CssDirectoryBean directoryBean = new CssDirectoryBean();
		directoryBean.setMethod(org.societies.api.schema.css.directory.MethodType.FIND_ALL_CSS_ADVERTISEMENT_RECORDS);

		//N.B. Use Domain Authority node
		Stanza stanza = new Stanza(this.domainNodeIdentity);
		
		ICommCallback callback = new CSSDirectoryCallback(client, LOGIN_CSS);

		try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, directoryBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public List<ACssAdvertisementRecord> findForAllCss(String client, String searchTerm) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("Search term parameter must have a value", null != searchTerm && searchTerm.length() > 0);
		
		Log.d(LOG_TAG, "findForAllCss called with client: " + client + " search: " + searchTerm);
		CssDirectoryBean directoryBean = new CssDirectoryBean();
		
		ACssAdvertisementRecord aAdvert = new ACssAdvertisementRecord();
		aAdvert.setId(searchTerm);
		directoryBean.setCssA(aAdvert);
		directoryBean.setMethod(org.societies.api.schema.css.directory.MethodType.FIND_FOR_ALL_CSS);

		//N.B. Use Domain Authority node
		Stanza stanza = new Stanza(this.domainNodeIdentity);
		
		ICommCallback callback = new CSSDirectoryCallback(client, FIND_FOR_ALL_CSS);

		try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, directoryBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public List<ACssAdvertisementRecord> getCssFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getCssFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_FRIENDS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, GET_CSS_FRIENDS);
		
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public List<ACssAdvertisementRecord> getSuggestedFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getSuggestedFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.SUGGESTED_FRIENDS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, SUGGESTED_FRIENDS);
		
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public AndroidCSSRecord readProfileRemote(String client, String cssId) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS Identity parameter must have a value", null != cssId && cssId.length() > 0);
		Log.d(LOG_TAG, "AndroidCSSRecord called with client: " + client);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_RECORD);
		try {
			Stanza stanza = new Stanza(ccm.getIdManager().fromJid(cssId));
			ICommCallback callback = new CSSManagerCallback(client, READ_PROFILE_REMOTE);
			
			ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Sent stanza");
		} catch (InvalidFormatException ex) {
			Log.e(this.getClass().getName(), "Error getting record from: " + cssId, ex);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 		
		return null;
	}

	public void sendFriendRequest(String client, String cssId) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS Identity parameter must have a value", null != cssId && cssId.length() > 0);
		Log.d(LOG_TAG, "sendFriendRequest called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.SEND_CSS_FRIEND_REQUEST);

		Stanza stanza = new Stanza(cloudNodeIdentity);		
		ICommCallback callback = new CSSManagerCallback(client, SEND_FRIEND_REQUEST);
        try {
    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendMessage(stanza, messageBean);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        }
	}

	/**
	 * AsyncTask classes required to carry out threaded tasks. These classes should be used where it is estimated that 
	 * the task length is unknown or potentially long. While direct usage of the Communications components for remote 
	 * method invocation is an explicitly asynchronous operation, other usage is not and the use of these types of classes
	 * is encouraged. Remember, Android Not Responding (ANR) exceptions will be invoked if the main app thread is abused
	 * and the app will be closed down by Android very soon after.
	 * 
	 * Although the result of an AsyncTask can be obtained by using <AsyncTask Object>.get() it's not a good idea as 
	 * it will effectively block the parent method until the result is delivered back and so render the use if the AsyncTask
	 * class ineffective. Use Intents as an asynchronous callback mechanism.
	 */
	
	/**
	 * This class carries out the registration of a CSS (XMPP) identity for a given domain server
	 */
	private class DomainRegistration extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("Four parameters must be supplied", params.length >= 4);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			Log.d(LOG_TAG, "DomainRegistration param username: " + params[0]);
			Log.d(LOG_TAG, "DomainRegistration param domain server: " + params[1]);
			Log.d(LOG_TAG, "DomainRegistration param password: " + params[2]);
			Log.d(LOG_TAG, "DomainRegistration param client: " + params[3]);
			
			String results [] = new String[4];

			try {
				INetworkNode networkNode = LocalCSSManagerService.this.ccm.newMainIdentity(params[0], params[1], params[2]);
				
				if (null != networkNode && null != networkNode.getDomain() && null != networkNode.getIdentifier()) {
					Log.d(LOG_TAG, "domain registration successful");
					
					results[0]  = networkNode.getIdentifier();
					results[1] = networkNode.getDomain();
					results[2] = params[2];
					results[3] = params[3];
				}
			} catch (XMPPError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return results;
		}
		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
			
			Intent intent = new Intent(LocalCSSManagerService.REGISTER_XMPP_SERVER);
			
			if (null != results[0]) {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
			} else {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, false);
			}

			AndroidCSSRecord aRecord = new AndroidCSSRecord();
			aRecord.setCssIdentity(results[0]);
			aRecord.setDomainServer(results[1]);
			aRecord.setPassword(results[2]);
			
			intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
			
			intent.setPackage(results[3]);

			Log.d(LOG_TAG, "DomainRegistration result sent");

			LocalCSSManagerService.this.sendBroadcast(intent);

	    }
	}
	/**
	 * This class carries out the un-registration of a CSS (XMPP) identity for a given domain server
	 */
	private class DomainUnRegistration extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("Four parameters must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "DomainUnRegistration - doInBackground");
			Log.d(LOG_TAG, "DomainUnRegistration param client: " + params[0]);
			
			String results [] = new String[1];

			if (LocalCSSManagerService.this.ccm.destroyMainIdentity()) {
				Log.d(LOG_TAG, "domain unregistration successful");
				
				results[0] = params[0];
			}
			
			return results;
		}
		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainUnRegistration - onPostExecute");
			
			Intent intent = new Intent(LocalCSSManagerService.UNREGISTER_XMPP_SERVER);
			
			if (null != results[0]) {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
			} else {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, false);
			}

			AndroidCSSRecord aRecord = new AndroidCSSRecord();
			
			intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
			
			intent.setPackage(results[0]);

			Log.d(LOG_TAG, "DomainUnRegistration result sent");

			LocalCSSManagerService.this.sendBroadcast(intent);

	    }
	}
	/**
	 * 
	 * This class handles the logging in to a previously registered domain and identity
	 *
	 */
	private class DomainLogin extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("Four parameters must be supplied", params.length >= 4);
			Log.d(LOG_TAG, "DomainLogin - doInBackground");
			Log.d(LOG_TAG, "DomainLogin param username: " + params[0]);
			Log.d(LOG_TAG, "DomainLogin param domain server: " + params[1]);
			Log.d(LOG_TAG, "DomainLogin param password: " + params[2]);
			Log.d(LOG_TAG, "DomainLogin param client: " + params[3]);
			
			String results [] = new String[4];

			INetworkNode networkNode = LocalCSSManagerService.this.ccm.login(params[0], params[1], params[2]);
			
			if (null != networkNode && null != networkNode.getDomain() && null != networkNode.getIdentifier() && LocalCSSManagerService.this.ccm.isConnected()) {
				Log.d(LOG_TAG, "domain login successful");
				
				results[0]  = networkNode.getIdentifier();
				results[1] = networkNode.getDomain();
				results[2] = params[2];
				results[3] = params[3];
			}
			
			return results;
		}
		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainLogin - onPostExecute");
			
			Intent intent = new Intent(LocalCSSManagerService.LOGIN_XMPP_SERVER);
			
			if (null != results[0]) {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
			} else {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, false);
			}

			AndroidCSSRecord aRecord = new AndroidCSSRecord();
			aRecord.setCssIdentity(results[0]);
			aRecord.setDomainServer(results[1]);
			aRecord.setPassword(results[2]);
			
			intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
			
			intent.setPackage(results[3]);

			Log.d(LOG_TAG, "DomainLogin result sent");

			LocalCSSManagerService.this.sendBroadcast(intent);

	    }
	}

	/**
	 * 
	 * This class handles the logging out of a previously registered domain and identity
	 *
	 */
	private class DomainLogout extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("Four parameters must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "DomainLogout - doInBackground");
			Log.d(LOG_TAG, "DomainLogout param client: " + params[0]);
			
			String results [] = new String[1];
			
			if (LocalCSSManagerService.this.ccm.logout()) {
				Log.d(LOG_TAG, "domain logout successful");
				LocalCSSManagerService.this.ccm.UnRegisterCommManager();
				LocalCSSManagerService.this.ccm = null;
				
				results[0] = params[0];
			}
			
			return results;
		}
		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainLogout - onPostExecute");
			
			Intent intent = new Intent(LocalCSSManagerService.LOGOUT_XMPP_SERVER);
			
			if (null != results[0]) {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
			} else {
				intent.putExtra(INTENT_RETURN_STATUS_KEY, false);
			}

			AndroidCSSRecord aRecord = new AndroidCSSRecord();
			
			intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
			
			intent.setPackage(results[0]);

			Log.d(LOG_TAG, "DomainLogout result sent");

			LocalCSSManagerService.this.sendBroadcast(intent);

	    }
	}

	/**
	 * Callback used with Android Comms for CSSManager
	 *
	 */
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


		public void receiveError(Stanza arg0, XMPPError arg1) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveError");
			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveInfo");
			
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveItems");
			
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveMessage");
			
			
		}

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				CssManagerResultBean resultBean = (CssManagerResultBean) retValue;
				//cssAdvertisementRecords
				if (SUGGESTED_FRIENDS == this.returnIntent || GET_CSS_FRIENDS == this.returnIntent) {
					ACssAdvertisementRecord advertArray [] = ACssAdvertisementRecord.getArray(resultBean.getResultAdvertList());
					
					intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
					
					intent.putExtra(INTENT_RETURN_VALUE_KEY, advertArray);
				}
				//cssRecords
				else { 
					intent.putExtra(INTENT_RETURN_STATUS_KEY, resultBean.getResult().isResultStatus());
					AndroidCSSRecord aRecord = AndroidCSSRecord.convertCssRecord(resultBean.getResult().getProfile());
					intent.putExtra(INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
					this.updateLocalPersistence(aRecord);
				}
				
				intent.setPackage(client);
				LocalCSSManagerService.this.sendBroadcast(intent);
				Log.d(LOG_TAG, "CSSManagerCallback Callback receiveResult sent return value: " + retValue);
				LocalCSSManagerService.this.ccm.unregister(LocalCSSManagerService.ELEMENT_NAMES, this);
			}
		}
		
		/**
		 * Decide which actions requires a database interaction
		 * @param record
		 */
		private void updateLocalPersistence(AndroidCSSRecord record) {
			if (this.returnIntent.equals(LOGIN_CSS) || 
					this.returnIntent.equals(SYNCH_PROFILE) || 
					this.returnIntent.equals(MODIFY_ANDROID_CSS_RECORD)) {
				LocalCSSManagerService.this.updateLocalCSSrecord(record);
			}
			
		}
	}
	/**
	 * Callback used with Android Comms for CSSDirectory
	 *
	 */
	private class CSSDirectoryCallback implements ICommCallback {
		String returnIntent;
		String client;
		
		public CSSDirectoryCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}


		public void receiveError(Stanza arg0, XMPPError arg1) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveError");
			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveInfo");
			
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveItems");
			
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveMessage");
			
			
		}

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				CssDirectoryBeanResult resultBean = (CssDirectoryBeanResult) retValue;
				ACssAdvertisementRecord advertArray [] = ACssAdvertisementRecord.getArray(resultBean.getResultCss());

				intent.putExtra(INTENT_RETURN_STATUS_KEY, true);
				
				intent.putExtra(INTENT_RETURN_VALUE_KEY, advertArray);

				intent.setPackage(client);

				LocalCSSManagerService.this.sendBroadcast(intent);

				Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveResult sent return value: " + retValue);
				
				LocalCSSManagerService.this.ccm.unregister(LocalCSSManagerService.ELEMENT_NAMES, this);
			}
		}
	}
	
	/**
	 * Unregister from already subscribed to pubsub events
	 */
	private void  unregisterFromPubsub() {
		Log.d(LOG_TAG, "Starting Pubsub un-registration: " + System.currentTimeMillis());
		
		if (null != this.pubsubClient) {
			UnSubscribeFromPubsub unSubPubSub = new UnSubscribeFromPubsub(); 
			unSubPubSub.execute(this.pubsubClient);
			
		}
	}
	
	/**
	 * Register for Pubsub events
	 */
	private void registerForPubsub() {
		
		Log.d(LOG_TAG, "Starting Pubsub registration: " + System.currentTimeMillis());
		
		this.pubsubClient = new PubsubClientAndroid(this);
		
            try {
                this.pubsubClient.addSimpleClasses(classList);

                Log.d(LOG_TAG, "Subscribing to pubsub");
    	        
    	    	SubscribeToPubsub subPubSub = new SubscribeToPubsub(); 
    	    	subPubSub.execute(this.pubsubClient);
	        } catch (ClassNotFoundException e) {
	                Log.e(LOG_TAG, "ClassNotFoundException loading " + Arrays.toString(classList.toArray()), e);
	        }
	}
	
	/**
	 * Create a new Subscriber object for Pubsub
	 * @return Subscriber
	 */
	private Subscriber createSubscriber() {
		Subscriber subscriber = new Subscriber() {
		
			public void pubsubEvent(IIdentity identity, String node, String itemId,
					Object payload) {
				Log.d(LOG_TAG, "Received Pubsub event: " + node + " itemId: " + itemId);
				if (payload instanceof CssEvent) {
					CssEvent event = (CssEvent) payload;
					Log.d(LOG_TAG, "Received event is :" + event.getType());
					
					//Create Android Notification
					int flags [] = new int [1];
					flags[0] = Notification.FLAG_AUTO_CANCEL;

					//Create Android Notification
					int notifierflags [] = new int [1];
					notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
					AndroidNotifier notifier = new AndroidNotifier(LocalCSSManagerService.this.getApplicationContext(), Notification.DEFAULT_SOUND, notifierflags);

					notifier.notifyMessage(event.getDescription(), event.getType(), LocalCSSManagerService.class);
				}
			}
		};
		return subscriber;

	}

	/**
     * 
     * Async task to un-register for CSSManager Pubsub events
     *
     */
    private class UnSubscribeFromPubsub extends AsyncTask<PubsubClientAndroid, Void, Boolean> {
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(PubsubClientAndroid... args) {
    		
    		PubsubClientAndroid pubsubAndClient = args[0];	    	

    		IIdentity pubsubService = null;
    		
//    		try {
//    	    	pubsubService = IdentityManagerImpl.staticfromJid(LocalCSSManagerService.this.cloudCommsDestination);
    			pubsubService = LocalCSSManagerService.this.cloudNodeIdentity;
//    		} catch (InvalidFormatException e) {
//    			Log.e(LOG_TAG, "Unable to obtain CSS node identity", e);
//    			this.resultStatus = false;
//    		}

    		try {
       			pubsubAndClient.subscriberUnsubscribe(pubsubService, CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.ADD_CSS_NODE));

    			pubsubAndClient.subscriberUnsubscribe(pubsubService, CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.DEPART_CSS_NODE));
    			LocalCSSManagerService.this.pubsubSubscribes.clear();

    			Log.d(LOG_TAG, "Pubsub un-subscription created");
    			Log.d(LOG_TAG, "Finishing Pubsub un-registration: " + System.currentTimeMillis());

    			
			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to unsubscribe for CSSManager events", e);

			}
    		return resultStatus;
    	}
    }

    /**
     * 
     * Async task to register for CSSManager Pubsub events
     * Note: The Subscriber objects used to subscribe to the relevant Pubsub nodes
     * are required to be used when un-registering - hence the use of the Map to store them.
     *
     */
    private class SubscribeToPubsub extends AsyncTask<PubsubClientAndroid, Void, Boolean> {
		private boolean resultStatus = true;
    	
    	protected Boolean doInBackground(PubsubClientAndroid... args) {
    		
    		PubsubClientAndroid pubsubAndClient = args[0];	    	

    		IIdentity pubsubService = null;
    		
			pubsubService = LocalCSSManagerService.this.cloudNodeIdentity;

    		try {
     			LocalCSSManagerService.this.pubsubSubscribes.put(CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.createSubscriber());
    			pubsubAndClient.subscriberSubscribe(pubsubService, CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.ADD_CSS_NODE));
    			
       			LocalCSSManagerService.this.pubsubSubscribes.put(CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.createSubscriber());
    			pubsubAndClient.subscriberSubscribe(pubsubService, CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.DEPART_CSS_NODE));
    			
    			Log.d(LOG_TAG, "Pubsub subscription created");
    			Log.d(LOG_TAG, "Finishing Pubsub registration: " + System.currentTimeMillis());


    			
			} catch (Exception e) {
    			this.resultStatus = false;
				Log.e(LOG_TAG, "Unable to register for CSSManager events", e);

			}
    		return resultStatus;
    	}
    }

    /**
     * Convert AndroidCSSRecord to CssRecord. Required for Simple XML
     * @param record
     * @return {@link AndroidCSSRecord}
     */
    private CssRecord convertAndroidCSSRecord(AndroidCSSRecord record) {
    	
    	CssRecord cssRecord = new CssRecord();
    	
    	cssRecord.setCssHostingLocation(record.getCssHostingLocation());
       	cssRecord.setCssIdentity(record.getCssIdentity());
       	cssRecord.setCssInactivation(record.getCssInactivation());
       	cssRecord.setCssRegistration(record.getCssRegistration());
       	cssRecord.setCssUpTime(record.getCssUpTime());
       	cssRecord.setDomainServer(record.getDomainServer());
       	cssRecord.setEmailID(record.getEmailID());
       	cssRecord.setEntity(record.getEntity());
       	cssRecord.setForeName(record.getForeName());
       	cssRecord.setHomeLocation(record.getHomeLocation());
       	cssRecord.setIdentityName(record.getIdentityName());
       	cssRecord.setImID(record.getImID());
       	cssRecord.setName(record.getName());
       	cssRecord.setPassword(record.getPassword());
       	cssRecord.setPresence(record.getPresence());
       	cssRecord.setSex(record.getSex());
       	cssRecord.setSocialURI(record.getSocialURI());
       	cssRecord.setStatus(record.getStatus());
       	for (AndroidCSSNode node : record.getArchivedCSSNodes()) {
       		cssRecord.getArchiveCSSNodes().add(convertAndroidCSSNode(node));
       	}
       	for (AndroidCSSNode node : record.getCSSNodes()) {
       		cssRecord.getCssNodes().add(convertAndroidCSSNode(node));
       	}
       	
       	return cssRecord;
    }
    /**
     * Convert AndroidCSSNode to CssNode. Required for Simple XML
     * @param node
     * @return CssNode
     */
    private CssNode convertAndroidCSSNode(AndroidCSSNode node) {
    	CssNode cssNode = new CssNode();
    	
    	cssNode.setIdentity(node.getIdentity());
    	cssNode.setStatus(node.getStatus());
    	cssNode.setType(node.getType());
    	
    	return cssNode;
    }
    /**
     * Create the node information of the Android node logging into the CSS
     * @return List<CssNode>
     */
    private List<CssNode> createAndroidLocalNode() {
    	CssNode localNode = new CssNode();
    	List<CssNode> listNodes= new ArrayList<CssNode>();
    	
    	localNode.setIdentity(this.ccm.getIdManager().getThisNetworkNode().getJid());
    	localNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
    	localNode.setType(CSSManagerEnums.nodeType.Android.ordinal());
    	
    	Log.d(LOG_TAG, "Android Node register info - id: " + localNode.getIdentity() + 
    			" status: " + CSSManagerEnums.nodeStatus.Available.name() + " type: " + CSSManagerEnums.nodeType.Android.name());
    	
    	listNodes.add(localNode);
    	
    	return listNodes;
    }

    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
    private void assignConnectionParameters() {
		//Get the Cloud destination
    	this.cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
		Log.d(LOG_TAG, "Cloud Node: " + this.cloudCommsDestination);

    	this.domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
    	Log.d(LOG_TAG, "Domain Authority Node: " + this.domainCommsDestination);
    			
    	try {
			this.cloudNodeIdentity = IdentityManagerImpl.staticfromJid(this.cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.cloudNodeIdentity);
			
			this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(this.domainCommsDestination);
			Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);
			
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get CSS Node identity", e);
			throw new RuntimeException(e);
		}     
    }

}
