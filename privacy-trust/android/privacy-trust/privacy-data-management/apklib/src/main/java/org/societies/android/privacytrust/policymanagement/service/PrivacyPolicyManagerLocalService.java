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
package org.societies.android.privacytrust.policymanagement.service;

import java.util.Map;

import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.intent.PrivacyPolicyIntentHelper;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.privacytrust.policymanagement.PrivacyPolicyManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.identity.RequestorBean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyManagerLocalService extends Service implements IPrivacyPolicyManager {
	private final static String TAG = PrivacyPolicyManagerLocalService.class.getSimpleName();

	private final IBinder binder;
	private IPrivacyPolicyManager privacyPolicyManager;


	public PrivacyPolicyManagerLocalService() {
		super();
		// Creation of a binder for the service
		binder = new LocalBinder();
		// Creation of an instance of the Java implementation
		privacyPolicyManager = new PrivacyPolicyManager(this);
	}


	/* ***
	 * Service Method Implementation
	 **** */

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#getPrivacyPolicy(org.societies.api.schema.identity.RequestorBean)
	 */
	public RequestPolicy getPrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		Log.d(TAG, "Local call to service getPrivacyPolicy()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_GET_PRIVACY_POLICY); 
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestor);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, privacyPolicy);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException {
		Log.d(TAG, "Local call to service updatePrivacyPolicy()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_UPDATE_PRIVACY_POLICY);
		RequestPolicy updatedPrivacyPolicy = null;
		try {
			updatedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, updatedPrivacyPolicy);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return updatedPrivacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#updatePrivacyPolicy(java.lang.String, org.societies.api.schema.identity.RequestorBean)
	 */
	public RequestPolicy updatePrivacyPolicy(String privacyPolicy, RequestorBean requestor) throws PrivacyException {
		Log.d(TAG, "Local call to service updatePrivacyPolicy()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_UPDATE_PRIVACY_POLICY);
		RequestPolicy updatedPrivacyPolicy = null;
		try {
			updatedPrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(privacyPolicy, requestor);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, updatedPrivacyPolicy);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return updatedPrivacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#deletePrivacyPolicy(org.societies.api.schema.identity.RequestorBean)
	 */
	public boolean deletePrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		Log.d(TAG, "Local call to service deletePrivacyPolicy()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_DELETE_PRIVACY_POLICY);
		boolean deleted = false;
		try {
			deleted = privacyPolicyManager.deletePrivacyPolicy(requestor);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, deleted);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return deleted;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#inferPrivacyPolicy(int, java.util.Map)
	 */
	public RequestPolicy inferPrivacyPolicy(int privacyPolicyType, Map configuration) throws PrivacyException {
		Log.d(TAG, "Local call to service inferPrivacyPolicy()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_INFER_PRIVACY_POLICY);
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.inferPrivacyPolicy(privacyPolicyType, configuration);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, privacyPolicy);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#toXmlString(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	public String toXmlString(RequestPolicy privacyPolicy) {
		Log.d(TAG, "Local call to service toXmlString()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_PRIVACY_POLICY_TO_XML);
		String xmlPrivacyPolicy = privacyPolicyManager.toXmlString(privacyPolicy);
		intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
		intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, xmlPrivacyPolicy);
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return xmlPrivacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#fromXmlString(java.lang.String)
	 */
	public RequestPolicy fromXmlString(String xmlPrivacyPolicy) throws PrivacyException {
		Log.d(TAG, "Local call to service fromXmlString()");
		Intent intent = new Intent(PrivacyPolicyIntentHelper.METHOD_PRIVACY_POLICY_FROM_XML);
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.fromXmlString(xmlPrivacyPolicy);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, true);
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_PRIVACY_POLICY, privacyPolicy);
		}
		catch(PrivacyException e) {
			intent.putExtra(PrivacyPolicyIntentHelper.RESULT_ACK, false);
		}
		// - Send result in intent
		sendBroadcast(intent);
		// - Send result in return value
		return privacyPolicy;
	}


	/* ***
	 * Android Service Management
	 **** */

	public class LocalBinder extends Binder {
		public PrivacyPolicyManagerLocalService getService() {
			return PrivacyPolicyManagerLocalService.this;
		}
	}

	/**
	 * Return binder object to allow calling component access to service's
	 * public methods
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}
}
