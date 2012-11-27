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
package org.societies.android.privacytrust.policymanagement;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.identity.DataIdentifierFactory;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.api.internal.privacytrust.privacyprotection.model.privacypolicy.AAction;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ActionConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.android.privacytrust.R;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService.LocalBinder;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Olivier Maridat (Trialog)
 * @date 28 nov. 2011
 */
public class PrivacyPolicyManagerActivity extends Activity {
	private final static String TAG = PrivacyPolicyManagerActivity.class.getSimpleName();

	private TextView txtLocation;

	private boolean ipBoundToService = false;
	private IPrivacyPolicyManager privacyPolicyManagerService = null;
	private ClientCommunicationMgr clientCommManager;


	//Enter local user credentials and domain name
	private static final String USER_NAME = "university";
	private static final String USER_PASS = "university";
	private static final String XMPP_DOMAIN = "societies.local";

	/* **************
	 * Activity Lifecycle
	 * ************** */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// -- Create a link with editable area
		txtLocation = (TextView) findViewById(R.id.txtLocation);

		clientCommManager = new ClientCommunicationMgr(this);


		// -- Create a link with services
		Intent ipIntent = new Intent(this.getApplicationContext(), PrivacyPolicyManagerLocalService.class);
		bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);

		//REGISTER BROADCAST
		IntentFilter intentFilter = new IntentFilter() ;
		intentFilter.addAction(MethodType.GET_PRIVACY_POLICY.name());

		this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
	}

	protected void onStop() {
		super.onStop();
		// -- Unlink with services
		if (ipBoundToService) {
			unbindService(inProcessServiceConnection);
		}
		// -- Logout
		clientCommManager.logout();
	}

	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			privacyPolicyManagerService = (IPrivacyPolicyManager) binder.getService();
			ipBoundToService = true;
		}
	};

	/* **************
	 * Button Listeners
	 * ************** */

	/**
	 * Call an in-process service. Service consumer simply calls service API and can use 
	 * return value
	 *  
	 * @param view
	 */
	public void onLaunchTest(View view) {
		// If this service is available
		if (ipBoundToService) {
			try {
				RequestorCisBean requestor = new RequestorCisBean();
				requestor.setRequestorId("master.societies.local");
				requestor.setCisRequestorId("cis-15a35e8e-4d3a-4a43-ac20-b83393b4547e.societies.local");
				privacyPolicyManagerService.getPrivacyPolicy(this.getPackageName(), requestor);
				txtLocation.setText("Waiting");
			} catch (PrivacyException e) {
				Log.e(TAG, "Error during the privacy policy retrieving", e);
				Toast.makeText(this, "Error during the privacy policy retrieving: "+e.getMessage(), Toast.LENGTH_SHORT);
			}
		}
		else {
			Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
	}

	public void onConnect(View view) {
		// -- Login to XMPP Server
		Log.d(TAG, "loginXMPPServer user: " + USER_NAME + " pass: " + USER_PASS + " domain: " + XMPP_DOMAIN);
		INetworkNode networkNode = clientCommManager.login(USER_NAME, XMPP_DOMAIN, USER_PASS);
		Toast.makeText(this, "Connected to :"+networkNode.getJid(), Toast.LENGTH_SHORT);
	}

	/**
	 * Utilities button to reset all values of this activity
	 * @param view
	 */
	public void onButtonResetClick(View view) {
		txtLocation.setText("Nothing yet");
	}


	private class bReceiver extends BroadcastReceiver  {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());

			if ((intent.getAction().equals(MethodType.GET_PRIVACY_POLICY))) {
				//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
				boolean ack =  1 == intent.getIntExtra(IPrivacyPolicyManager.INTENT_RETURN_STATUS_KEY, 0) ? true : false;
				StringBuffer sb = new StringBuffer();
				if (ack) {
					RequestPolicy privacyPolicy = (RequestPolicy) intent.getSerializableExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY);
					sb.append("Privacy policy retrieved: "+(null != privacyPolicy));
					if (null != privacyPolicy) {
						sb.append(privacyPolicyManagerService.toXmlString(privacyPolicy));
					}
				}
				else {
					sb.append("Arg, no ack");
				}
				txtLocation.setText(sb.toString());
			}
		}
	};
}
