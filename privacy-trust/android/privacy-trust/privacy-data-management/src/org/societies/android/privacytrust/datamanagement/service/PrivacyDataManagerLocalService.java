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
package org.societies.android.privacytrust.datamanagement.service;

import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.privacytrust.datamanagement.PrivacyDataManager;
import org.societies.android.privacytrust.datamanagement.service.PrivacyDataManagerExternalService.IncomingHandler;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.RequestorBean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerLocalService extends Service implements IPrivacyDataManager {
	private final static String TAG = PrivacyDataManagerLocalService.class.getSimpleName();

	private IBinder binder;
	private IPrivacyDataManager privacyDataManager;
	
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// Creation of a binder for the service
		binder = new LocalBinder();
		// Creation of an instance of the Java implementation
		privacyDataManager = new PrivacyDataManager(this.getApplicationContext());
	}
	
	
	/* ***
	 * Service Method Implementation
	 **** */

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#checkPermission(org.societies.api.schema.identity.RequestorBean, java.lang.String, java.lang.String, org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action)
	 */
	@Override
	public ResponseItem checkPermission(RequestorBean requestor,
			String ownerId, String dataId, Action action)
			throws PrivacyException {
		Log.d(TAG, "Local call to service checkPermission()");
		return privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#obfuscateData(org.societies.api.schema.identity.RequestorBean, java.lang.String, org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public IDataWrapper obfuscateData(String requestor, String ownerId,
			IDataWrapper dataWrapper) throws PrivacyException {
		Log.d(TAG, "Local call to service obfuscateData()");
		return privacyDataManager.obfuscateData(requestor, ownerId, dataWrapper);
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.schema.identity.RequestorBean, java.lang.String, org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String hasObfuscatedVersion(String requestor, String ownerId,
			IDataWrapper dataWrapper) throws PrivacyException {
		Log.d(TAG, "Local call to service hasObfuscatedVersion()");
		return privacyDataManager.hasObfuscatedVersion(requestor, ownerId, dataWrapper);
	}
	

	/* ***
	 * Android Service Management
	 **** */
	
	public class LocalBinder extends Binder {
		public PrivacyDataManagerLocalService getService() {
			return PrivacyDataManagerLocalService.this;
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
