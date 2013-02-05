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
package org.societies.android.platform.cssmanager;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.css.directory.IAndroidCssDirectory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssDirectoryBean;
import org.societies.api.schema.css.directory.MethodType;
import org.societies.api.schema.css.directory.CssDirectoryBeanResult;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.utilities.DBC.Dbc;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class LocalCssDirectoryService extends Service implements IAndroidCssDirectory {
	private static final String LOG_TAG = LocalCssDirectoryService.class.getName();
	
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssDirectoryBean", "cssDirectoryBeanResult");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/css/directory");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.css.directory");

    private ClientCommunicationMgr ccm;
    private IBinder binder = null;
    
    @Override
	public void onCreate () {
		this.binder = new LocalCssDirectoryBinder();
	
		this.ccm = new ClientCommunicationMgr(this);
		Log.d(LOG_TAG, "CssDirectory service starting");
	}
    
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CSSManager service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	 public class LocalCssDirectoryBinder extends Binder {
		 public LocalCssDirectoryService getService() {
	            return LocalCssDirectoryService.this;
	        }
	    }

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	public CssAdvertisementRecord[] findAllCssAdvertisementRecords(String client) {
		Log.d(LOG_TAG, "findAllCssAdvertisementRecords called by client: " + client);
		
		AsyncSearchDirectory methodAsync = new AsyncSearchDirectory();
		String params[] = {client, IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS};
		methodAsync.execute(params);
		
		return null;
	}

	public CssAdvertisementRecord[] findForAllCss(String client, String searchTerm) {
		Log.d(LOG_TAG, "getFriendRequests called by client: " + client);
		
		AsyncSearchDirectory methodAsync = new AsyncSearchDirectory();
		String params[] = {client, IAndroidCssDirectory.FIND_FOR_ALL_CSS, searchTerm};
		methodAsync.execute(params);
		
		return null;
	}

	/**
	 * This class carries out the AcceptFriendRequests method call asynchronously
	 */
	private class AsyncSearchDirectory extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "GetFriendRequests - doInBackground");
			String results [] = new String[1];
			results[0] = params[0];
			//PARAMETERS
			String client = (String)params[0];
			String method = (String) params[1];
			String searchTerm = "";
			if (params.length == 3) 
				searchTerm = (String) params[2];

			//MESSAGE BEAN
			CssDirectoryBean directoryBean = new CssDirectoryBean();
			if (params.length == 3) {
				CssAdvertisementRecord advert = new CssAdvertisementRecord();
				advert.setName(searchTerm);
				directoryBean.setCssA(advert);
			}
			if (method.equals(IAndroidCssDirectory.FIND_FOR_ALL_CSS)) 
				directoryBean.setMethod(MethodType.FIND_FOR_ALL_CSS);
			else
				directoryBean.setMethod(MethodType.FIND_ALL_CSS_ADVERTISEMENT_RECORDS);
			
			Stanza stanza = new Stanza(ccm.getIdManager().getDomainAuthorityNode());
			ICommCallback callback = new CSSDirectoryCallback(client, method);
			try {
	    		ccm.register(ELEMENT_NAMES, callback);
				ccm.sendIQ(stanza, IQ.Type.GET, directoryBean, callback);
				Log.d(LOG_TAG, "Send stanza");
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "Error when sending message stanza", e);
	        } 
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
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
				//ACssAdvertisementRecord advertArray [] = ACssAdvertisementRecord.getArray(resultBean.getResultCss());
				List<CssAdvertisementRecord> listRecords = resultBean.getResultCss();
				CssAdvertisementRecord advertArray[] = listRecords.toArray(new CssAdvertisementRecord[listRecords.size()]);  
				
				intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_STATUS_KEY, true);
				intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_VALUE_KEY, advertArray);
				intent.setPackage(client);

				LocalCssDirectoryService.this.sendBroadcast(intent);
				Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveResult sent return value: " + retValue);
				LocalCssDirectoryService.this.ccm.unregister(LocalCssDirectoryService.ELEMENT_NAMES, this);
			}
		}
	}


}
