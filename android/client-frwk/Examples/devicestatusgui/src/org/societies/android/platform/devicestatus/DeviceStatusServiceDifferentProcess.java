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
package org.societies.android.platform.devicestatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.devicemonitor.IDeviceStatus;
import org.societies.android.api.internal.devicemonitor.BatteryStatus;
import org.societies.android.api.internal.devicemonitor.ProviderStatus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;

/**
 * Android Service running in a different process of its activity
 * This service uses DeviceStatus and wraps it into an Android service
 * @see org.societies.android.platform.devicestatus.DeviceStatus
 * @author Olivier Maridat (Trialog)
 */
public class DeviceStatusServiceDifferentProcess extends Service implements IDeviceStatus {
	private Messenger inMessenger;
	private IDeviceStatus deviceStatusAccessor;
	
	
	/* ***
	 * Constructor
	 **** */
	
	public DeviceStatusServiceDifferentProcess() {
		super();
		this.inMessenger = new Messenger(new IncomingHandler());
		// Creation of an instance of the Java implementation of IDeviceStatus
		deviceStatusAccessor = new DeviceStatus(this);
	}

	
	/* ***
	 * Android Service Management
	 **** */
	
	public class ExternalBinder extends Binder {
		DeviceStatusServiceDifferentProcess getService() {
			return DeviceStatusServiceDifferentProcess.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String targetMethod = ServiceMethodTranslator.getMethodSignature(IDeviceStatus.methodsArray, message.what);
			
			if (targetMethod != null) {
				try {
					Log.i(this.getClass().getSimpleName(), "Target method: " + targetMethod);
					
					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.i(this.getClass().getSimpleName(), "Target method param types: " + element.getName());
			
					}
								
					Method method = DeviceStatusServiceDifferentProcess.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(IDeviceStatus.methodsArray, message.what), parameters);
					Log.i(this.getClass().getSimpleName(), "Found method: " + method.getName());
					try {
						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
						Log.i(this.getClass().getSimpleName(),"Number of parameters: " + params.length); 

						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
						for (int i = 0; i < paramTypeList.length; i++) {
							Class bundleParam [] = {String.class};
							Object bundleValue [] = {paramNameList[i]};
							Method bundleMethod = Bundle.class.getMethod("get" + paramTypeList[i], bundleParam);
							
							params[i] = bundleMethod.invoke(message.getData(), bundleValue);
							Log.i(this.getClass().getSimpleName(), "parameter i = " + i + " value: " + params[i]);
						}
						method.invoke(DeviceStatusServiceDifferentProcess.this, params);
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

	
	/* ***
	 * IDeviceStatus implementation
	 **** */

	/*
	 * @see org.api.android.internal.IDeviceStatus#isInternetConnectivityOn(java.lang.String)
	 */
	public boolean isInternetConnectivityOn(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "isInternetConnectivityOn called");
		// -- Create Data		
		boolean isInternetConnectivityOn = deviceStatusAccessor.isInternetConnectivityOn(callerPackageName);
		
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(CONNECTIVITY_STATUS);
		intent.putExtra(CONNECTIVITY_INTERNET_ON, isInternetConnectivityOn);
		// Intentionally restricting potential intent receiver to client 
		intent.setPackage(callerPackageName);
		this.sendBroadcast(intent);
		
		return isInternetConnectivityOn;
	}
	
	/*
	 * @see org.api.android.internal.IDeviceStatus#getConnectivityProvidersStatus(java.lang.String)
	 */
	public List<?> getConnectivityProvidersStatus(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "getConnectivityProvidersStatus called");
		// -- Create Data
		boolean isInternetEnabled = deviceStatusAccessor.isInternetConnectivityOn(callerPackageName);
		ArrayList<ProviderStatus> connectivityProviders = (ArrayList<ProviderStatus>) deviceStatusAccessor.getConnectivityProvidersStatus(callerPackageName);
		
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(CONNECTIVITY_STATUS);
		intent.putExtra(CONNECTIVITY_INTERNET_ON, isInternetEnabled);
		intent.putParcelableArrayListExtra(CONNECTIVITY_PROVIDER_LIST, connectivityProviders);
		// Intentionally restricting potential intent receiver to client 
		intent.setPackage(callerPackageName);
		// Send
		this.sendBroadcast(intent);
		
		return connectivityProviders;
	}

	/*
	 * @see org.api.android.internal.IDeviceStatus#getLocationProvidersStatus(java.lang.String)
	 */
	public List<?> getLocationProvidersStatus(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "getLocationProvidersStatus called");
		// -- Create Data
		ArrayList<ProviderStatus> locationProviders = (ArrayList<ProviderStatus>) deviceStatusAccessor.getLocationProvidersStatus(callerPackageName);
		
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(LOCATION_STATUS);
		intent.putParcelableArrayListExtra(LOCATION_PROVIDER_LIST, locationProviders);
		// Intentionally restricting potential intent receiver to client 
		intent.setPackage(callerPackageName);
		// Send
		this.sendBroadcast(intent);
		
		return locationProviders;
	}
}
