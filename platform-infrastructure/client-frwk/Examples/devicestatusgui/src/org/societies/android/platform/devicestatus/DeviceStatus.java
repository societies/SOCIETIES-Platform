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

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.devicemonitor.IDeviceStatus;
import org.societies.android.api.internal.devicemonitor.ProviderStatus;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DeviceStatus implements IDeviceStatus {
	Context androidContext;
	public DeviceStatus(Context context) {
		this.androidContext = context;
	}
	       
	/*
	 * @see org.societies.android.platform.interfaces.IDeviceStatus#isInternetConnectivityOn(java.lang.String)
	 */
	public boolean isInternetConnectivityOn(String callerPackageName) {
		ConnectivityManager connectivity = (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED);
	}

	/*
	 * @see org.api.android.internal.IDeviceStatus#getConnectivityProvidersStatus(java.lang.String)
	 */
	public List<?> getConnectivityProvidersStatus(String callerPackageName) {
		List<ProviderStatus> connectivityProviders = new ArrayList<ProviderStatus>();
		
		// --- Mobile, Wifi, Wimax, ...
		ConnectivityManager connectivity = (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
		int length = networkInfos.length;
		for (int i=0; i<length; i++) {
			NetworkInfo networkInfo = networkInfos[i];
			connectivityProviders.add(new ProviderStatus(networkInfo.getTypeName(), !networkInfo.getState().equals(NetworkInfo.State.UNKNOWN)));
		}

		// --- Bluetouth
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		connectivityProviders.add(new ProviderStatus("Bluetouth", (null != bluetoothAdapter && !bluetoothAdapter.isEnabled())));
		
		return connectivityProviders;
	}
	
	/*
	 * @see org.societies.android.platform.interfaces.IDeviceStatus#getLocationProvidersStatus(java.lang.String)
	 */
	public List<?> getLocationProvidersStatus(String callerPackageName) {
		List<ProviderStatus> locationProviders = new ArrayList<ProviderStatus>();
		LocationManager locationManager = (LocationManager) androidContext.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getAllProviders();
		for (String provider : providers) {
			locationProviders.add(new ProviderStatus(provider, locationManager.isProviderEnabled(provider)));
		}
		return locationProviders;
	}
}
