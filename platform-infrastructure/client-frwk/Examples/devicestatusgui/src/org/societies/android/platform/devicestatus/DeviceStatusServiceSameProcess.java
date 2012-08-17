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

import java.util.List;

import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.devicemonitor.IDeviceStatus;
import org.societies.android.api.internal.devicemonitor.BatteryStatus;
import org.societies.android.api.internal.devicemonitor.ProviderStatus;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Android Service running in the same process of its activity
 * This service uses DeviceStatus and wraps it into an Android service
 * @see org.societies.android.platform.devicestatus.DeviceStatus
 * @author Olivier Maridat (Trialog)
 */
public class DeviceStatusServiceSameProcess extends Service implements IDeviceStatus {
	private final IBinder binder;
	private IDeviceStatus deviceStatusAccessor;
	
	
	/* ***
	 * Constructor
	 **** */
	
	public DeviceStatusServiceSameProcess() {
		super();
		// Creation of a binder for the service
		binder = new LocalBinder();
		// Creation of an instance of the Java implementation of IDeviceStatus
		deviceStatusAccessor = new DeviceStatus(this);
	}
	
	
	/* ***
	 * Android Service Management
	 **** */
	
	public class LocalBinder extends Binder {
		DeviceStatusServiceSameProcess getService() {
			return DeviceStatusServiceSameProcess.this;
		}
	}
	
	@Override
	/**
	 * Return binder object to allow calling component access to service's
	 * public methods
	 */
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return this.binder;
	}
	
	
	/* ***
	 * IDeviceStatus implementation
	 **** */

	public boolean isInternetConnectivityOn(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "["+callerPackageName+"] has called 'isInternetConnectivityOn'");
		return deviceStatusAccessor.isInternetConnectivityOn(callerPackageName);
	}

	public List<?> getLocationProvidersStatus(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "["+callerPackageName+"] has called 'getLocationProvidersStatus'");
		return deviceStatusAccessor.getLocationProvidersStatus(callerPackageName);
	}


	public List<?> getConnectivityProvidersStatus(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "["+callerPackageName+"] has called 'getConnectivityProvidersStatus'");
		return deviceStatusAccessor.getConnectivityProvidersStatus(callerPackageName);
	}
}
