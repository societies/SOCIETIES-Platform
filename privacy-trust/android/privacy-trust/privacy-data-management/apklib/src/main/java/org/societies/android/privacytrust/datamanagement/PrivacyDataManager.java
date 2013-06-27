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
package org.societies.android.privacytrust.datamanagement;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.identity.util.DataIdentifierFactory;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.utilities.MissingClientPackageException;
import org.societies.android.privacytrust.api.IDataObfuscator;
import org.societies.android.privacytrust.api.IPrivacyDataManagerInternal;
import org.societies.android.privacytrust.datamanagement.callback.PrivacyDataIntentSender;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.util.ObfuscatorFactory;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager implements IPrivacyDataManager {
	private final static String TAG = PrivacyDataManager.class.getSimpleName();

	private Context context;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private PrivacyDataManagerRemote privacyDataManagerRemote;
	private PrivacyDataIntentSender intentSender;


	public PrivacyDataManager(Context context)  {
		this.context = context;
		// Init tools
		privacyDataManagerInternal = new PrivacyDataManagerInternal();
		privacyDataManagerRemote = new PrivacyDataManagerRemote(context);
		intentSender = new PrivacyDataIntentSender(context);
	}


	@Override
	public void checkPermission(String clientPackage, RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		dataIds.add(dataId);
		checkPermission(clientPackage, requestor, dataIds, actions);
	}
	@Override
	public void checkPermission(String clientPackage, RequestorBean requestor, DataIdentifier dataId, Action action) throws PrivacyException {
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		checkPermission(clientPackage, requestor, dataId, actions);
	}
	@Override
	public void checkPermission(String clientPackage, RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		// -- Verify parameters
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == requestor) {
			Log.e(TAG, "verifyParemeters: Not enought information: requestor is missing");
			throw new NullPointerException("Not enought information: requestor is missing");
		}
		if (null == dataIds) {
			Log.e(TAG, "verifyParemeters: Not enought information: data id is missing");
			throw new NullPointerException("Not enought information: data id is missing");
		}

		// -- Launch action
		CheckPermissionTask task = new CheckPermissionTask(context, clientPackage); 
		task.execute(requestor, dataIds, actions);
	}
	@Override
	public void checkPermission(String clientPackage, RequestorBean requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException {
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		checkPermission(clientPackage, requestor, dataIds, actions);
	}
	private class CheckPermissionTask extends AsyncTask<Object, Object, Boolean> {
		private Context context;
		private String clientPackage;
		private int progress = 0;

		public CheckPermissionTask(Context context, String clientPackage) {
			this.context = context;
			this.clientPackage = clientPackage;
		}

		@Override
		protected void onPreExecute() {
			publishProgress(progress, "Loading...");
		}

		protected Boolean doInBackground(Object... args) {
			RequestorBean requestor = (RequestorBean) args[0];
			List<DataIdentifier> dataIds = (List<DataIdentifier>) args[1];
			List<Action> actions = (List<Action>) args[2];

			try {
				// -- Retrieve a stored permission
				//				ResponseItem privacyPermission = privacyDataManagerInternal.getPermission(requestor, dataId, actions);
				//				if (null != privacyPermission) {
				//					Log.d(TAG, "Local Permission retrieved");
				//					// Publish progress
				//					if (!checkAndPublishProgress((progress = 100), "Local permission retrieved")) {
				//						return false;
				//					}
				//					intentSender.sendIntentCheckPermission(clientPackage, privacyPermission);
				//					return true;
				//				}


				// -- Permission not available: remote call
				Log.d(TAG, "No Local Permission retrieved: remote call");
				privacyDataManagerRemote.checkPermission(clientPackage, requestor, dataIds, actions);
				// Publish progress
				if (!checkAndPublishProgress((progress = progress+30), "Remote access control required: request sent")) {
					return false;
				}
			}
			catch (PrivacyException e) {
				intentSender.sendIntentError(clientPackage, MethodType.CHECK_PERMISSION.name(), "Unexpected error during access control: "+e.getMessage());
				publishProgress((progress = 100), "Unexpected error during access control: "+e.getMessage());
				return false;
			}
			publishProgress((progress = 100), "Access control finished");
			return true;
		}

		/**
		 * Publish progress and if cancel is required: send cancel intent
		 * @param progress Progress number
		 * @param msg Progress message description
		 * @return true if the process can continue, false if it needs to stop
		 */
		private boolean checkAndPublishProgress(int progress, String msg) {
			publishProgress(progress, msg);
			if (isCancelled()) {
				intentSender.sendIntentCancel(clientPackage, MethodType.CHECK_PERMISSION.name());
				return false;
			}
			return true;
		}
	}

	@Override
	public void obfuscateData(String clientPackage, RequestorBean requestor, DataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == requestor) {
			Log.e(TAG, "verifyParemeters: Not enought information: requestor is missing");
			throw new NullPointerException("Not enought information: requestor is missing");
		}
		if (null == dataWrapper) {
			Log.e(TAG, "verifyParemeters: Not enought information: data wrapper is missing");
			throw new NullPointerException("Not enought information: data wrapper is missing");
		}
		if (null == dataWrapper.getData()) {
			Log.e(TAG, "verifyParemeters: Not enought information: data is missing");
			throw new NullPointerException("Not enought information: data is missing");
		}
		if (null == dataWrapper.getDataType()) {
			Log.e(TAG, "verifyParemeters: Not enought information: data type is missing");
			throw new NullPointerException("Not enought information: data type is missing");
		}

		// -- Launch action
		ObfuscationTask task = new ObfuscationTask(context, clientPackage); 
		task.execute(requestor, dataWrapper);

	}
	private class ObfuscationTask extends AsyncTask<Object, Object, Boolean> {
		private Context context;
		private String clientPackage;
		private int progress = 0;

		public ObfuscationTask(Context context, String clientPackage) {
			this.context = context;
			this.clientPackage = clientPackage;
		}

		@Override
		protected void onPreExecute() {
			publishProgress(progress, "Loading...");
		}

		protected Boolean doInBackground(Object... args) {
			RequestorBean requestor = (RequestorBean) args[0];
			DataWrapper dataWrapper = (DataWrapper) args[1];

			try {
				// -- Retrieve the obfuscation level
				double obfuscationLevel = privacyDataManagerInternal.getObfuscationLevel(requestor, DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, dataWrapper.getDataType()));
				// If no obfuscation is required: return directly the wrapped data
				if (-1 == obfuscationLevel || obfuscationLevel >= 1) {
					publishProgress(100, "Obfuscation finished");
					intentSender.sendIntentDataObfuscation(clientPackage, dataWrapper);
				}
				// Obfuscation level in [0, 1]
				if (obfuscationLevel < 0) {
					obfuscationLevel = 0.001;
				}
				// Publish progress
				if (!checkAndPublishProgress((progress = progress+10), "Obfuscation level retrieved")) {
					return false;
				}

				// -- Mapping: retrieve the relevant obfuscator
				IDataObfuscator obfuscator = ObfuscatorFactory.getDataObfuscator(dataWrapper);
				// Publish progress
				if (!checkAndPublishProgress((progress = progress+5), "Obfuscator algorithm identified")) {
					return false;
				}

				// -- Obfuscate
				DataWrapper obfuscatedDataWrapper = null;
				// - Obfuscation
				// Local obfuscation
				if (obfuscator.getObfuscatorInfo().isObfuscable()) {
					Log.d(TAG, "Local obfuscation");
					obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
					// Publish progress
					if (!checkAndPublishProgress((progress = 100), "Obfuscation done")) {
						return false;
					}
					// Send data
					intentSender.sendIntentDataObfuscation(clientPackage, obfuscatedDataWrapper);
				}
				// Remote obfuscation needed
				else {
					Log.d(TAG, "Remote obfuscation required");
					privacyDataManagerRemote.obfuscateData(clientPackage, requestor, dataWrapper);
					// Publish progress
					if (!checkAndPublishProgress((progress = progress+30), "Remote obfuscation required: request sent")) {
						return false;
					}
				}
			}
			catch(PrivacyException e) {
				intentSender.sendIntentError(clientPackage, MethodType.OBFUSCATE_DATA.name(), "Unexpected error during obfuscation: "+e.getMessage());
				publishProgress(100, "Unexpected error during obfuscation: "+e.getMessage());
				return false;
			}
			catch(Exception e) {
				intentSender.sendIntentError(clientPackage, MethodType.OBFUSCATE_DATA.name(), "Unexpected error during obfuscation: "+e.getMessage());
				publishProgress(100, "Unexpected error during obfuscation: "+e.getMessage());
				return false;
			}
			publishProgress(100, "Obfuscation finished");
			return true;
		}

		/**
		 * Publish progress and if cancel is required: send cancel intent
		 * @param progress Progress number
		 * @param msg Progress message description
		 * @return true if the process can continue, false if it needs to stop
		 */
		private boolean checkAndPublishProgress(int progress, String msg) {
			publishProgress(progress, msg);
			if (isCancelled()) {
				intentSender.sendIntentCancel(clientPackage, MethodType.OBFUSCATE_DATA.name());
				return false;
			}
			return true;
		}
	}


	@Override
	public boolean startService() {
		privacyDataManagerRemote.bindToComms();
		return true;
	}

	@Override
	public boolean stopService() {
		privacyDataManagerRemote.unbindFromComms();
		return true;
	}


	// -- Private methods
	private boolean containsAction(List<Action> actions, Action action) {
		if (null == actions || actions.size() <= 0 || null == action) {
			return false;
		}
		for(Action actionTmp : actions) {
			if (actionTmp.equals(action)) {
				return true;
			}
		}
		return false;
	}
}
