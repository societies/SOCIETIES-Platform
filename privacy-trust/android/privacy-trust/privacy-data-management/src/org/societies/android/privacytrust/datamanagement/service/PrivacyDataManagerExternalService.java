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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.api.util.ServiceMethodTranslator;
import org.societies.android.privacytrust.datamanagement.PrivacyDataManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.RequestorBean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerExternalService extends Service implements IPrivacyDataManager {
	private final static String TAG = PrivacyDataManagerExternalService.class.getSimpleName();

	private Messenger inMessenger;
	private IPrivacyDataManager privacyDataManager;


	public PrivacyDataManagerExternalService() {
		super();
		// Creation of a binder for the service
		this.inMessenger = new Messenger(new IncomingHandler());
		// Creation of an instance of the Java implementation
		privacyDataManager = new PrivacyDataManager();
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
		Log.d(TAG, "External call to service checkPermission()");
		ResponseItem permission = privacyDataManager.checkPermission(requestor, ownerId, dataId, action);
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(IPrivacyDataManager.CHECK_PERMISSION);
		intent.putExtra(IPrivacyDataManager.CHECK_PERMISSION_RESULT, permission);
		this.sendBroadcast(intent);

		return permission;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#obfuscateData(org.societies.api.schema.identity.RequestorBean, java.lang.String, org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public IDataWrapper obfuscateData(String requestor, String ownerId,
			IDataWrapper dataWrapper) throws PrivacyException {
		Log.d(TAG, "External call to service obfuscateData()");
		IDataWrapper obfuscatedDataWrapper = privacyDataManager.obfuscateData(requestor, ownerId, dataWrapper);
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(IPrivacyDataManager.OBFUSCATE_DATA);
		intent.putExtra(IPrivacyDataManager.OBFUSCATE_DATA_RESULT, obfuscatedDataWrapper);
		this.sendBroadcast(intent);
		return obfuscatedDataWrapper;
	}

	/* (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.schema.identity.RequestorBean, java.lang.String, org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String hasObfuscatedVersion(String requestor, String ownerId,
			IDataWrapper dataWrapper) throws PrivacyException {
		Log.d(TAG, "External call to service hasObfuscatedVersion()");
		String dataId = privacyDataManager.hasObfuscatedVersion(requestor, ownerId, dataWrapper);
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(IPrivacyDataManager.HAS_OBFUSCATED_VERSION);
		intent.putExtra(IPrivacyDataManager.HAS_OBFUSCATED_VERSION_RESULT, dataId);
		this.sendBroadcast(intent);
		return dataId;
	}


	/* ***
	 * Android Service Management
	 **** */

	public class ExternalBinder extends Binder {
		PrivacyDataManagerExternalService getService() {
			return PrivacyDataManagerExternalService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return inMessenger.getBinder();
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String[] privacyDataManagerMethodsArray = ServiceMethodTranslator.getMethodsArrayFromInterface(IPrivacyDataManager.class);//IPrivacyDataManager.methodsArray;//
			String targetMethod = ServiceMethodTranslator.getMethodSignature(privacyDataManagerMethodsArray, message.what);

			if (targetMethod != null) {
				try {
					Log.i(TAG, "Target method: " + targetMethod);

					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.i(TAG, "Target method param types: " + element.getName());

					}

					Method method = PrivacyDataManagerExternalService.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(privacyDataManagerMethodsArray, message.what), parameters);
					Log.i(TAG, "Found method: " + method.getName());
					try {
						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
						Log.i(TAG,"Number of parameters: " + params.length); 

						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
						for (int i = 0; i < paramTypeList.length; i++) {
							Class bundleParam [] = {String.class};
							Object bundleValue [] = {paramNameList[i]};
//							Method bundleMethod = Bundle.class.getMethod("get" + ServiceMethodTranslator.getParameterTypeWithoutPackage(paramTypeList[i]), bundleParam);
							Method bundleMethod = Bundle.class.getMethod(ServiceMethodTranslator.getGetMethodFromParameter(paramTypeList[i]), bundleParam);
							Bundle test = new Bundle();

							params[i] = bundleMethod.invoke(message.getData(), bundleValue);
							Log.i(TAG, "parameter i = " + i + " value: " + params[i]);
						}
						method.invoke(PrivacyDataManagerExternalService.this, params);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
