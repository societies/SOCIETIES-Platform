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

import org.societies.android.privacytrust.policymanagement.callback.PrivacyPolicyIntentSender;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.utilities.MissingClientPackageException;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManager {
	private final static String TAG = PrivacyPolicyManager.class.getSimpleName();

	private Context context;
	private PrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	private PrivacyPolicyIntentSender intentSender;


	public PrivacyPolicyManager(Context context)  {
		Log.d(TAG, "PrivacyPolicyManager Constructor");
		this.context = context;
		privacyPolicyManagerRemote = new PrivacyPolicyManagerRemote(context);
		intentSender = new PrivacyPolicyIntentSender(context);
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#getPrivacyPolicy(java.lang.String, org.societies.android.api.identity.RequestorBean)
	 */
	@Override
	public void getPrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == owner || null == owner.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		GetPrivacyPolicyTask task = new GetPrivacyPolicyTask(context, clientPackage); 
		task.execute(owner);
	}
	public class GetPrivacyPolicyTask extends AsyncTask<Object, Object, Boolean> {
		private Context context;
		private String clientPackage;
		private int progress = 0;

		public GetPrivacyPolicyTask(Context context, String clientPackage) {
			this.context = context;
			this.clientPackage = clientPackage;
		}

		@Override
		protected void onPreExecute() {
			publishProgress(progress, "Loading...");
		}

		@Override
		protected Boolean doInBackground(Object... args) {
			boolean result = false;
			// Retrieve parameter
			RequestorBean owner = (RequestorBean) args[0];

			try {
				// -- TODO Retrieve a stored privacy policy
				progress += 50;
				publishProgress(progress, "Check cached version");
				if (isCancelled()) {
					return false;
				}

				// -- PrivacyPolicy not available: remote call
				Log.d(TAG, "No Local Privacy policy retrieved: remote call");
				result = privacyPolicyManagerRemote.getPrivacyPolicy(clientPackage, owner);
				progress = 100;
				publishProgress(progress, "Remote privacy policy retrieved");
				if (isCancelled()) {
					return false;
				}
			}
			catch (PrivacyException e) {
				intentSender.sendIntentError(clientPackage, MethodType.GET_PRIVACY_POLICY.name(), "Unexpected error during retrieving: "+e.getMessage());
				progress = 0;
				publishProgress(progress, "Process failed");
				result = false;
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#updatePrivacyPolicy(java.lang.String, org.societies.android.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public void updatePrivacyPolicy(String clientPackage, RequestPolicy privacyPolicy) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}

		UpdatePrivacyPolicyTask task = new UpdatePrivacyPolicyTask(context, clientPackage); 
		task.execute(privacyPolicy);
	}
	public class UpdatePrivacyPolicyTask extends AsyncTask<RequestPolicy, Object, Boolean> {
		private Context context;
		private String clientPackage;
		private int progress = 0;

		public UpdatePrivacyPolicyTask(Context context, String clientPackage) {
			this.context = context;
			this.clientPackage = clientPackage;
		}

		@Override
		protected void onPreExecute() {
			publishProgress(progress, "Loading...");
		}

		@Override
		protected Boolean doInBackground(RequestPolicy... args) {
			boolean result = false;
			// Retrieve parameter
			RequestPolicy privacyPolicy = (RequestPolicy) args[0];

			try {
				// -- TODO Update the stored privacy policy
				progress += 50;
				publishProgress(progress, "Store a local version");
				if (isCancelled()) {
					return false;
				}

				// -- PrivacyPolicy not available: remote call
				result = privacyPolicyManagerRemote.updatePrivacyPolicy(clientPackage, privacyPolicy);
				progress = 100;
				publishProgress(progress, "Remote privacy policy updated");
				if (isCancelled()) {
					return false;
				}
			}
			catch (PrivacyException e) {
				intentSender.sendIntentError(clientPackage, MethodType.UPDATE_PRIVACY_POLICY.name(), "Unexpected error during update: "+e.getMessage());
				progress = 0;
				publishProgress(progress, "Process failed");
				result = false;
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#deletePrivacyPolicy(java.lang.String, org.societies.android.api.identity.RequestorBean)
	 */
	@Override
	public void deletePrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == owner || null == owner.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		DeletePrivacyPolicyTask task = new DeletePrivacyPolicyTask(context, clientPackage); 
		task.execute(owner);
	}
	public class DeletePrivacyPolicyTask extends AsyncTask<RequestorBean, Object, Boolean> {
		private Context context;
		private String clientPackage;
		private int progress = 0;

		public DeletePrivacyPolicyTask(Context context, String clientPackage) {
			this.context = context;
			this.clientPackage = clientPackage;
		}

		@Override
		protected void onPreExecute() {
			publishProgress(progress, "Loading...");
		}

		@Override
		protected Boolean doInBackground(RequestorBean... args) {
			boolean result = false;
			// Retrieve parameter
			RequestorBean owner = (RequestorBean) args[0];

			try {
				// -- TODO Delete the stored privacy policy
				progress += 50;
				publishProgress(progress, "Delete the local version");
				if (isCancelled()) {
					return false;
				}

				// -- Delete the remote PrivacyPolicy
				result = privacyPolicyManagerRemote.deletePrivacyPolicy(clientPackage, owner);
				progress = 100;
				publishProgress(progress, "Remote privacy policy deleted");
				if (isCancelled()) {
					return false;
				}
			}
			catch (PrivacyException e) {
				intentSender.sendIntentError(clientPackage, MethodType.DELETE_PRIVACY_POLICY.name(), "Unexpected error during delete: "+e.getMessage());
				progress = 0;
				publishProgress(progress, "Process failed");
				result = false;
			}
			return result;
		}
	}

	public RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException {
		// -- Verify
		// Empty privacy policy
		if (null == privacyPolicy || privacyPolicy.equals("")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}
		// Fill XML header if necessary
		String encoding = "UTF-8";
		if (!privacyPolicy.startsWith("<?xml")) {
			privacyPolicy = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n"+privacyPolicy;
		}
		// If only contains the XML header: empty privacy policy
		if (privacyPolicy.endsWith("?>")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}

		// -- Convert Xml to Java
		RequestPolicy result = null;
		XMLPolicyReader xmlPolicyReader = new XMLPolicyReader();
		try {
			// Transform XML Privacy Policy to Java Privacy Policy
			result = xmlPolicyReader.fromXmlString(privacyPolicy);
		} catch (Exception e) {
			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy: "+e.getMessage(), e);
		}
		return result;
	}

	@Override
	public boolean startService() {
		Log.d(TAG, "PrivacyPolicyManager startService");
		privacyPolicyManagerRemote.bindToComms();
		return true;
	}

	@Override
	public boolean stopService() {
		Log.d(TAG, "PrivacyPolicyManager stopService");
		privacyPolicyManagerRemote.unbindFromComms();
		return true;
	}
}
