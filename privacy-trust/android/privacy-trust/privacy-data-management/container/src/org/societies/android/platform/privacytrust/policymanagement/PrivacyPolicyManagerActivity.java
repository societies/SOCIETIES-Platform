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
package org.societies.android.platform.privacytrust.policymanagement;

import java.util.List;

import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.android.platform.privacytrust.R;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService;
import org.societies.android.privacytrust.policymanagement.service.PrivacyPolicyManagerLocalService.LocalBinder;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Olivier Maridat (Trialog)
 * @date 28 nov. 2011
 */
public class PrivacyPolicyManagerActivity extends Activity implements OnClickListener {
	private final static String TAG = PrivacyPolicyManagerActivity.class.getSimpleName();

	private TextView txtLocation;
	private TextView txtConnectivity;

	private boolean ipBoundToService = false;
	private IPrivacyPolicyManager privacyPolicyManagerService = null;
	private ClientCommunicationMgr clientCommManager;
	private RequestPolicy retrievedPrivacyPolicy;
	private RequestPolicy defaultPrivacyPolicy;


	//Enter local user credentials and domain name
	private static final String USER_NAME = "university";
	private static final String USER_PASS = "university";
	private static final String XMPP_DOMAIN = "societies.local";

	/* **************
	 * Business
	 * ************** */

	// Sender
	/**
	 * Call an in-process service. Service consumer simply calls service API and can use 
	 * return value
	 *  
	 * @param view
	 */
	public void onLaunchTest(View view) {
		txtLocation.setText(R.string.txt_nothing);
		// If this service is available
		if (ipBoundToService) {
			try {
				txtLocation.setText("Waiting");
				RequestorCisBean owner = new RequestorCisBean();
				owner.setRequestorId("university.societies.local");
				owner.setCisRequestorId("cis-c9140e94-bdc5-4644-b2ad-2bc10198d89c.societies.local");
				if (R.id.btnLaunchTest1 == view.getId()) {
					privacyPolicyManagerService.getPrivacyPolicy(this.getPackageName(), owner);
				}
				if (R.id.btnLaunchTest1bis == view.getId()) {
					owner.setRequestorId("emma.societies.local");
					owner.setCisRequestorId("cis-a1fcced7-fae7-4253-a44f-27a281811ca1.societies.local");
					privacyPolicyManagerService.getPrivacyPolicy(this.getPackageName(), owner);
				}
				else if (R.id.btnLaunchTest2 == view.getId()) {
					RequestPolicy newPrivacyPolicy = PrivacyPolicyUtils.inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants.MEMBERS_ONLY, null);
					newPrivacyPolicy.setRequestor(owner);
					privacyPolicyManagerService.updatePrivacyPolicy(this.getPackageName(), newPrivacyPolicy);
				}
				else if (R.id.btnLaunchTest4 == view.getId()) {
					privacyPolicyManagerService.deletePrivacyPolicy(this.getPackageName(), owner);
				}
			} catch (PrivacyException e) {
				Log.e(TAG, "Error during the privacy policy retrieving", e);
				txtLocation.setText(R.string.txt_nothing);
				Toast.makeText(this, "Error during the privacy policy retrieving: "+e.getMessage(), Toast.LENGTH_SHORT);
			}
			catch (Exception e) {
				Log.e(TAG, "Fatal error during the privacy policy retrieving", e);
				txtLocation.setText(R.string.txt_nothing);
				Toast.makeText(this, "Fatal error during the privacy policy retrieving: "+e.getMessage(), Toast.LENGTH_SHORT);
			}
		}
		else {
			txtLocation.setText(R.string.txt_nothing);
			Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
	}

	// Receiver
	private class bReceiver extends BroadcastReceiver  {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());

			boolean ack =  intent.getBooleanExtra(IPrivacyPolicyManager.INTENT_RETURN_STATUS_KEY, false);
			StringBuffer sb = new StringBuffer();
			sb.append(intent.getAction()+": "+(ack ? "success" : "failure"));
			if (ack && (intent.getAction().equals(MethodType.GET_PRIVACY_POLICY.name()))) {
				retrievedPrivacyPolicy = (RequestPolicy) intent.getSerializableExtra(IPrivacyPolicyManager.INTENT_RETURN_VALUE_KEY);
				sb.append("Privacy policy retrieved: "+(null != retrievedPrivacyPolicy));
				if (null != retrievedPrivacyPolicy) {
					sb.append(PrivacyPolicyUtils.toXmlString(retrievedPrivacyPolicy));
				}
			}
			txtLocation.setText(sb.toString());
		}
	}

	/* **************
	 * Button Listeners
	 * ************** */

	public void onClick(View v) {
		if (R.id.btnLaunchTest1 == v.getId() || R.id.btnLaunchTest1bis == v.getId() || R.id.btnLaunchTest2 == v.getId() || R.id.btnLaunchTest4 == v.getId()) {
			onLaunchTest(v);
		}
		else if (R.id.btnReset == v.getId()) {
			onButtonResetClick(v);
		}
		else {
			Toast.makeText(this, "What button did you clicked on?", Toast.LENGTH_SHORT);
		}
	};

	/**
	 * Utilities button to reset all values of this activity
	 * @param view
	 */
	public void onButtonResetClick(View view) {
		txtLocation.setText(R.string.txt_nothing);
	}

	/* **************
	 * Activity Lifecycle
	 * ************** */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacypolicy);

		// -- Create a link with editable area
		txtLocation = (TextView) findViewById(R.id.txtLocation);
		// -- Create a link with buttons
		((Button) findViewById(R.id.btnLaunchTest1)).setOnClickListener(this);
		((Button) findViewById(R.id.btnLaunchTest1bis)).setOnClickListener(this);
		((Button) findViewById(R.id.btnLaunchTest2)).setOnClickListener(this);
		((Button) findViewById(R.id.btnLaunchTest4)).setOnClickListener(this);
		((Button) findViewById(R.id.btnReset)).setOnClickListener(this);

		clientCommManager = new ClientCommunicationMgr(this.getApplicationContext(), true);
		//		txtConnectivity.setText("Is connected? "+(clientCommManager.isConnected() ? "yes" : "no"));


		// -- Create a link with services
		Intent ipIntent = new Intent(getApplicationContext(), PrivacyPolicyManagerLocalService.class);
		getApplicationContext().bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);

		//REGISTER BROADCAST
		IntentFilter intentFilter = new IntentFilter() ;
		intentFilter.addAction(IPrivacyPolicyManager.INTENT_DEFAULT_ACTION);
		intentFilter.addAction(MethodType.GET_PRIVACY_POLICY.name());
		intentFilter.addAction(MethodType.UPDATE_PRIVACY_POLICY.name());
		intentFilter.addAction(MethodType.DELETE_PRIVACY_POLICY.name());
		intentFilter.addAction(MethodType.INFER_PRIVACY_POLICY.name());

		this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);

		// Mock data
		try {
			defaultPrivacyPolicy = PrivacyPolicyUtils.inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants.MEMBERS_ONLY, null);
		}
		catch(PrivacyException e) {
			Log.e(TAG, "Cannot generate default privacy policy: "+e.getMessage());
		}
	}

	protected void onStop() {
		super.onStop();
		// -- Unlink with services
		if (ipBoundToService) {
			getApplicationContext().unbindService(inProcessServiceConnection);
		}
		// -- Logout
		//		clientCommManager.logout();
	}

	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Connect to service: IPrivacyPolicyManager");
			LocalBinder binder = (LocalBinder) service;
			privacyPolicyManagerService = (IPrivacyPolicyManager) binder.getService();
			ipBoundToService = true;
		}
	};
}
