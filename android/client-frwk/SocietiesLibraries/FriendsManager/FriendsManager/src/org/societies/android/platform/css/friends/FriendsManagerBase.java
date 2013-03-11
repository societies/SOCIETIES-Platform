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
package org.societies.android.platform.css.friends;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.api.internal.cssmanager.IFriendsManager;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class FriendsManagerBase implements IFriendsManager {
	//Logging tag
	private static final String LOG_TAG = FriendsManagerBase.class.getName();

	//XMPP Communication namespaces and associated entities
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cssmanagement");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");
    
    private ClientCommunicationMgr ccm;
    private boolean restrictBroadcast;
    private Context androidContext;
    private boolean connectedToComms = false;
    //private IIdentity cloudNodeIdentity = null;
    
    //>>>>>>>>>>>>>>>>>>>>>>>>>SERVICE LIFECYCLE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**Default Constructor */
    public FriendsManagerBase(Context context) {
    	this(context, true);
    }
    
    /**Constructor */
    public FriendsManagerBase(Context context, boolean restrictBroadcast) {
    	this.androidContext = context;
		this.restrictBroadcast = restrictBroadcast;
		this.ccm = new ClientCommunicationMgr(context, true);
    }
    
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>IServiceManager>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "FriendsManager startService binding to comms");
	        this.ccm.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						connectedToComms = true;
						//REGISTER NAMESPACES
						ccm.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		FriendsManagerBase.this.androidContext.sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		FriendsManagerBase.this.androidContext.sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		FriendsManagerBase.this.androidContext.sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "FriendsManager stopService unregistering namespaces");
        	ccm.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					ccm.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		FriendsManagerBase.this.androidContext.sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		FriendsManagerBase.this.androidContext.sendBroadcast(intent);
    	}
    	return true;
    }
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			FriendsManagerBase.this.androidContext.sendBroadcast(intent);
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>IFriendsManager>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public List<CssAdvertisementRecord> getCssFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getCssFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_FRIENDS);
		try {
			IIdentity cloudNodeIdentity = this.ccm.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNodeIdentity);
			ICommCallback callback = new FriendsCallback(client, IAndroidCSSManager.GET_CSS_FRIENDS);

			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (InvalidFormatException ife) {
			Log.e(this.getClass().getName(), "Error retrieving the Cloud node identity", ife);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 
		return null;
	}

	@Override
	public List<CssAdvertisementRecord> getSuggestedFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getSuggestedFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.SUGGESTED_FRIENDS);
		try {
			IIdentity cloudNodeIdentity = this.ccm.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNodeIdentity);
			ICommCallback callback = new FriendsCallback(client, IAndroidCSSManager.SUGGESTED_FRIENDS);
        
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (InvalidFormatException ife) {
			Log.e(this.getClass().getName(), "Error retrieving the Cloud node identity", ife);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 
		return null;
	}

	@Override
	public CssRecord readProfileRemote(String client, String cssId) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS Identity parameter must have a value", null != cssId && cssId.length() > 0);
		Log.d(LOG_TAG, "readProfileRemote called with client: " + client);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_RECORD);
		try {
			Stanza stanza = new Stanza(ccm.getIdManager().fromJid(cssId));
			ICommCallback callback = new FriendsCallback(client, IAndroidCSSManager.READ_PROFILE_REMOTE);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Sent stanza");
		} catch (InvalidFormatException ex) {
			Log.e(this.getClass().getName(), "Error getting record from: " + cssId, ex);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 		
		return null;
	}

	@Override
	public void sendFriendRequest(String client, String cssId) {
		Log.d(LOG_TAG, "sendFriendRequest called by client: " + client + " for: " + cssId);
		AsyncFriendRequests methodAsync = new AsyncFriendRequests();
		String params[] = {client, cssId, IAndroidCSSManager.SEND_FRIEND_REQUEST};
		methodAsync.execute(params);
	}

	@Override
	public CssAdvertisementRecord[] getFriendRequests(String client) {
		Log.d(LOG_TAG, "getFriendRequests called by client: " + client);
		AsyncGetFriendRequests methodAsync = new AsyncGetFriendRequests();
		String params[] = {client};
		methodAsync.execute(params);
		
		return null;
	}

	@Override
	public void acceptFriendRequest(String client, String cssId) {
		Log.d(LOG_TAG, "acceptFriendRequest called by client: " + client);
		AsyncFriendRequests methodAsync = new AsyncFriendRequests();
		String params[] = {client, cssId, IAndroidCSSManager.ACCEPT_FRIEND_REQUEST};
		methodAsync.execute(params);
	}
	
	private class AsyncGetFriendRequests extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "GetFriendRequests - doInBackground");
			String results [] = new String[1];
			results[0] = params[0];
			//MESSAGE BEAN
			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setMethod(MethodType.GET_FRIEND_REQUESTS);
			//COMMS CONFIG
			ICommCallback discoCallback = new FriendsCallback(params[0], IAndroidCSSManager.GET_FRIEND_REQUESTS);
			try {
				IIdentity cloudNodeIdentity = FriendsManagerBase.this.ccm.getIdManager().getCloudNode();
				Stanza stanza = new Stanza(cloudNodeIdentity);
	        	ccm.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
	        } catch (InvalidFormatException ife) {
				Log.e(this.getClass().getName(), "Error retrieving the Cloud node identity", ife);
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}

	/**
	 * This class carries out the AcceptFriendRequests method call asynchronously
	 */
	private class AsyncFriendRequests extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "AsyncFriendRequests - doInBackground");
			
			//PARAMETERS
			String client = params[0];
			String targetCssId = params[1];
			String method = params[2];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setTargetCssId(targetCssId);
			if (method.equals(IAndroidCSSManager.SEND_FRIEND_REQUEST)) {
				messageBean.setMethod(MethodType.SEND_CSS_FRIEND_REQUEST_INTERNAL);
			} else {
				messageBean.setMethod(MethodType.ACCEPT_CSS_FRIEND_REQUEST_INTERNAL);
				messageBean.setRequestStatus(CssRequestStatusType.ACCEPTED);	
			}
			try {
				//COMMS CONFIG
				IIdentity cloudNodeIdentity = FriendsManagerBase.this.ccm.getIdManager().getCloudNode();
				Stanza stanza = new Stanza(cloudNodeIdentity);
	        	ccm.sendMessage(stanza, messageBean);
			} catch (InvalidFormatException ife) {
				Log.e(this.getClass().getName(), "Error retrieving the Cloud node identity", ife);
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "AsyncFriendRequests - onPostExecute");
	    }
	}
	
	/**
	 * Callback used with Android Comms for CSSManager
	 *
	 */
	private class FriendsCallback implements ICommCallback {
		String returnIntent;
		String client;
		
		public FriendsCallback(String client, String returnIntent) {
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
				if (IAndroidCSSManager.SUGGESTED_FRIENDS == this.returnIntent || IAndroidCSSManager.GET_CSS_FRIENDS == this.returnIntent || IAndroidCSSManager.GET_FRIEND_REQUESTS==this.returnIntent) {
					CssAdvertisementRecord advertArray[] = new CssAdvertisementRecord[resultBean.getResultAdvertList().size()]; 
					advertArray = resultBean.getResultAdvertList().toArray(advertArray);					
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, advertArray);
				}
				//cssRecords
				else if (IAndroidCSSManager.READ_PROFILE_REMOTE == this.returnIntent) { 
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, resultBean.getResult().isResultStatus());
					CssRecord aRecord = resultBean.getResult().getProfile();
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
				}				
				if (restrictBroadcast) {
					intent.setPackage(client);
				}
				FriendsManagerBase.this.androidContext.sendBroadcast(intent);
				Log.d(LOG_TAG, "FriendsManager Callback receiveResult sent return value: " + retValue);
			}
		}
	}
	
}
