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
package org.societies.android.platform.privacytrust.datamanagement;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.identity.util.DataIdentifierFactory;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.platform.privacytrust.R;
import org.societies.android.platform.privacytrust.policymanagement.PrivacyPolicyManagerActivity;
import org.societies.android.privacytrust.datamanagement.service.PrivacyDataManagerLocalService;
import org.societies.android.privacytrust.datamanagement.service.PrivacyDataManagerLocalService.LocalBinder;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
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
public class PrivacyDataManagerActivity extends Activity implements OnClickListener {
	private final static String TAG = PrivacyDataManagerActivity.class.getSimpleName();

	private TextView txtResult;

	private boolean ipBoundToService = false;
	private IPrivacyDataManager privacyDataManagerService = null;
	private ClientCommunicationMgr clientCommManager;
	private List<ResponseItem> retrievedpermissions;


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
		txtResult.setText(R.string.txt_nothing);
		// If this service is available
		if (ipBoundToService) {
			try {
				txtResult.setText("Waiting");
				RequestorCisBean requestor = new RequestorCisBean();
				requestor.setRequestorId("university.societies.local");
				requestor.setCisRequestorId("cis-aa667d2a-9330-4d44-8c0d-c7d2df32a782.societies.local");
				DataIdentifier dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+requestor.getCisRequestorId()+"/cis-member-list");
				List<Action> actions = new ArrayList<Action>();
				Action action = new Action();
				action.setActionConstant(ActionConstants.READ);
				actions.add(action);
				privacyDataManagerService.checkPermission(this.getPackageName(), requestor, dataId, actions);
			} catch (PrivacyException e) {
				Log.e(TAG, "Error during the checkPermission", e);
				txtResult.setText(R.string.txt_nothing);
				Toast.makeText(this, "Error during the checkPermission: "+e.getMessage(), Toast.LENGTH_SHORT);
			}
			catch (Exception e) {
				Log.e(TAG, "Fatal error during the checkPermission", e);
				txtResult.setText(R.string.txt_nothing);
				Toast.makeText(this, "Fatal error during the checkPermission: "+e.getMessage(), Toast.LENGTH_SHORT);
			}
		}
		else {
			txtResult.setText(R.string.txt_nothing);
			Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
	}

	// Receiver
	private class bReceiver extends BroadcastReceiver  {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());

			boolean ack =  intent.getBooleanExtra(IPrivacyDataManager.INTENT_RETURN_STATUS_KEY, false);
			StringBuffer sb = new StringBuffer();
			sb.append(intent.getAction()+": "+(ack ? "success" : "failure"));
			if (ack && (intent.getAction().equals(MethodType.CHECK_PERMISSION.name()))) {
				retrievedpermissions = (List<ResponseItem>) intent.getSerializableExtra(IPrivacyDataManager.INTENT_RETURN_VALUE_KEY);
				sb.append("Privacy permission retrieved: "+(null != retrievedpermissions && retrievedpermissions.size() > 0));
				if (null != retrievedpermissions && retrievedpermissions.size() > 0) {
					sb.append("\nDecision: "+retrievedpermissions.get(0).getDecision().name());
					sb.append("\nOn resource: "+retrievedpermissions.get(0).getRequestItem().getResource().getDataIdUri());
				}
			}
			txtResult.setText(sb.toString());
		}
	}


	/* **************
	 * Button Listeners
	 * ************** */

	public void onClick(View v) {
		if (R.id.btnLaunchTestCheckPermission == v.getId()) {
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
		txtResult.setText(R.string.txt_nothing);
	}

	/* **************
	 * Activity Lifecycle
	 * ************** */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacydata);

		// -- Create a link with editable area
		txtResult = (TextView) findViewById(R.id.txtResult);
		// -- Create a link with buttons
		((Button) findViewById(R.id.btnLaunchTestCheckPermission)).setOnClickListener(this);
		((Button) findViewById(R.id.btnReset)).setOnClickListener(this);

		clientCommManager = new ClientCommunicationMgr(this.getApplicationContext(), true);
		//		txtConnectivity.setText("Is connected? "+(clientCommManager.isConnected() ? "yes" : "no"));


		// -- Create a link with services
		Intent ipIntent = new Intent(getApplicationContext(), PrivacyDataManagerLocalService.class);
		getApplicationContext().bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);

		//REGISTER BROADCAST
		IntentFilter intentFilter = new IntentFilter() ;
		intentFilter.addAction(IPrivacyDataManager.INTENT_DEFAULT_ACTION);
		intentFilter.addAction(MethodType.CHECK_PERMISSION.name());
		intentFilter.addAction(MethodType.OBFUSCATE_DATA.name());

		getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
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
			Log.d(TAG, "Connect to service: IPrivacyDataManager");
			LocalBinder binder = (LocalBinder) service;
			privacyDataManagerService = (IPrivacyDataManager) binder.getService();
			ipBoundToService = true;
		}
	};
}
