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
package org.societies.android.platform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.IQ;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.ListCrit;
import org.societies.api.schema.cis.manager.ListResponse;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;


/**
 * In charge of setting up communication with the CisManager cloud.
 * In charge of communicating queries and updates to CisManager cloud.
 * Is implemented similar to an Android content provider in that it 
 * provides a generic DB-based interface based on the same classes 
 * that are provided in a content provider.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class CommunicationAdapter extends Service implements ISocialAdapter{

	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("CommunityManager", "ListResponse");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cis/manager",
														    	  "http://societies.org/api/schema/activityfeed",	  		
																  "http://societies.org/api/schema/cis/community");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cis.manager",
													    	   "org.societies.api.schema.activityfeed",
															   "org.societies.api.schema.cis.community");
    private ClientCommunicationMgr commMgr;
    private IBinder binder = null;
    private static final String LOG_TAG = CommunicationAdapter.class.getName();
    
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Android Service methods >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Override
	public void onCreate () {
		this.binder = new LocalBinder();
		Log.d(LOG_TAG, "CommunicationAdapter service starting");
		try {
			//INSTANTIATE COMMS MANAGER
			commMgr = new ClientCommunicationMgr(this);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    
	}
    
    @Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CommunicationAdapter service terminating");
	}
    
    /**Create Binder object for local service invocation */
	public class LocalBinder extends Binder {
		public CommunicationAdapter getService() {
			return CommunicationAdapter.this;
		}
	}
	
	/* @see android.app.Service#onBind(android.content.Intent) */
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ISocialAdapter Methods >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	/* @see org.societies.android.platform.ISocialAdapter#getListOfOwnedCis(org.societies.android.platform.ISocialAdapterCallback)*/
	public void getListOfOwnedCis(ISocialAdapterCallback callback) {
		Log.d(LOG_TAG, "getListOfOwnedCis called");
		
		//MESSAGE BEAN
		CommunityManager messageBean = new CommunityManager();
		org.societies.api.schema.cis.manager.List listCISs = new org.societies.api.schema.cis.manager.List();
		listCISs.setListCriteria(ListCrit.OWNED);
		messageBean.setList(listCISs);
		
		//COMMS STUFF
		IIdentity toID = commMgr.getIdManager().getCloudNode();
		Stanza stanza = new Stanza(toID);
		
		//MANAGE THE CALLBACKS
		ICommCallback commCallback = new CommunityCallback(stanza.getId(), callback);
        try {
        	Log.d(LOG_TAG, "Sending stanza");
        	commMgr.register(ELEMENT_NAMES, commCallback);
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, commCallback);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "ERROR sending message: " + e.getMessage());
        }
	}

	
	
    /**
     * TODO: Need to implement this. It is going to be either done through a presence
     * value or through rela XMPP login.
     * @return
     */
    public boolean isConnected(){
	return false;
    }
    /**
     * When CommunicationAdapter is created it does not go online automatically
     * You have to call this method explicitly
     * 
     * @return:
     */
    public int connect(){
	//TODO: log in to network.
	
	return 0;
    }
    
    /**
     * @return
     */
    public int disconnect(){
    	//	online = false;
    	// TODO: clean up network
    	return 0;
    }
    
    public Cursor query(Uri uri, 
    		String[] projection, 
    		String selection, 
    		String[] selectionArgs, 
    		String sortOrder){
    	String[] columnNames = {SocialContract.Community.NAME,
				SocialContract.Community.OWNER_ID,
//				SocialContract.Community.CREATION_DATE
				};
    	MatrixCursor cursor= new MatrixCursor(columnNames, 10);
    	String[] columnValues = {"XYZ", "babak@societies.org", "today"};
    	cursor.addRow(columnValues);
    	return cursor;
    }

	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		// ContentValues testValues = null;
	    long testID = 1;

	    // testValues = new ContentValues(values);
    	return Uri.withAppendedPath(uri, Long.toString(testID));    	
	}

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int connect(String username, String password) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager
	 */
	private class CommunityCallback implements ICommCallback {
		
		//MAP TO STORE THE ALL THE CLIENT CALLBACKS
		private final Map<String, ISocialAdapterCallback> clientCallbacks = new HashMap<String, ISocialAdapterCallback>();

		/** Constructor for callback
		 * @param clientID unique ID of send request to comms framework
		 * @param clientCallback callback from originating client
		 */
		public CommunityCallback(String clientID, ISocialAdapterCallback clientCallback) {
			//STORE THIS CALLBACK FOR THIS CLIENT ID
			clientCallbacks.put(clientID, clientCallback);
		}

		/**Returns the correct ISocialAdapterCallback client for this request 
		 * @param requestID the id of the initiating request
		 * @return
		 * @throws UnavailableException
		 */
		private ISocialAdapterCallback getRequestingClient(String requestID) {
			ISocialAdapterCallback requestingClient = (ISocialAdapterCallback) clientCallbacks.get(requestID);
			clientCallbacks.remove(requestID);
			return requestingClient;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveResult(Stanza returnStanza, Object msgBean) {
			Log.d(LOG_TAG, "Callback receiveResult of type: " + msgBean.getClass().getName());
	
			// --------- COMMUNITY MANAGEMENT ListResponse BEAN ---------
			if (msgBean instanceof ListResponse) {
				Log.d(LOG_TAG, "ListResponse Result!");
				
				ListResponse communityResult = (ListResponse) msgBean; 
				//GET CORRECT CLIENT'S ISocialAdapterCallback FOR THIS REQUEST
				ISocialAdapterCallback clientCallback = getRequestingClient(returnStanza.getId());
				clientCallback.receiveResult(communityResult.getCommunity());	
			}
			// --------- ACTIVITY BEAN ---------
			//else if (msgBean instanceof ActivityBean)) {
			//	DO SOMETHING ELSE
			//}
		}

		public void receiveError(Stanza returnStanza, XMPPError error) {
			Log.d(LOG_TAG, "Callback receiveError");			
		}

		public void receiveInfo(Stanza returnStanza, String arg1, XMPPInfo info) {
			Log.d(LOG_TAG, "Callback receiveInfo");
		}

		public void receiveItems(Stanza returnStanza, String arg1, List<String> items) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		public void receiveMessage(Stanza returnStanza, Object messageBean) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
