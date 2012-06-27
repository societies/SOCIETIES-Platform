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
import java.util.Set;

import org.societies.android.platform.R;
import org.societies.android.platform.devicestatus.DeviceStatusServiceSameProcess.LocalBinder;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.api.internal.devicemonitor.IDeviceStatus;
import org.societies.android.api.internal.devicemonitor.BatteryStatus;
import org.societies.android.api.internal.devicemonitor.ProviderStatus;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Olivier Maridat (Trialog)
 * @date 28 nov. 2011
 */
public class DeviceStatusActivity extends Activity {
	private TextView txtConnectivity;
	private TextView txtBattery;
	private TextView txtLocation;

	private boolean ipBoundToService = false;
	private IDeviceStatus targetIPService = null;
	private boolean opBoundToService = false;
	private Messenger targetOPService = null;
	
	private long serviceInvoke;
	private static final int NUM_SERVICE_INVOKES = 1;

	
	/* **************
	 * Activity Lifecycle
	 * ************** */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// -- Create a link with editable area
		txtConnectivity = (TextView) findViewById(R.id.txtConnectivity);
		txtBattery = (TextView) findViewById(R.id.txtBattery);
		txtLocation = (TextView) findViewById(R.id.txtLocation);
		
		// -- Create a link with services
		Intent ipIntent = new Intent(this, DeviceStatusServiceSameProcess.class);
		Intent opIntent = new Intent(this, DeviceStatusServiceDifferentProcess.class);
		bindService(ipIntent, inProcessServiceConnection, Context.BIND_AUTO_CREATE);
		bindService(opIntent, outProcessServiceConnection, Context.BIND_AUTO_CREATE);
		
		// Register the broadcast receiver to retrieve results of an out process service call
		IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(IDeviceStatus.CONNECTIVITY_STATUS);
        intentFilter.addAction(IDeviceStatus.LOCATION_STATUS);
        this.registerReceiver(new ServiceReceiver(), intentFilter);
	}

	protected void onStop() {
		super.onStop();
		// -- Unlink with services
		if (ipBoundToService) {
			unbindService(inProcessServiceConnection);
		}
		if (opBoundToService) {
			unbindService(outProcessServiceConnection);
		}
	}

	private ServiceConnection inProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			ipBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			targetIPService = (IDeviceStatus) binder.getService();
			ipBoundToService = true;
		}
	};

	private ServiceConnection outProcessServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			opBoundToService = false;
		}
		public void onServiceConnected(ComponentName name, IBinder service) {
			opBoundToService = true;
			targetOPService = new Messenger(service);
		}
	};
	
	
	/* **************
	 * Button Listeners
	 * ************** */
	
	/**
	 * Call an in-process service. Service consumer simply calls service API and can use 
	 * return value
	 *  
	 * @param view
	 */
	public void onButtonRefreshUsingSameProcessClick(View view) {
		// If this service is available
		if (ipBoundToService) {
			// Connectivity
			List<ProviderStatus> connectivityProviderStatus = (List<ProviderStatus>) targetIPService.getConnectivityProvidersStatus(this.getClass().getPackage().getName());
			boolean isInternetEnabled = targetIPService.isInternetConnectivityOn(this.getClass().getPackage().getName());
			StringBuffer sbConnectivity = new StringBuffer();
			sbConnectivity.append(updateConnectivity(isInternetEnabled, connectivityProviderStatus));
			txtConnectivity.setText(sbConnectivity.toString());
			
			// Battery
			IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(new ServiceReceiver(), batteryLevelFilter);
			
			// Location
			List<ProviderStatus> locationProvidersStatus = (List<ProviderStatus>) targetIPService.getLocationProvidersStatus(this.getClass().getPackage().getName());
			StringBuffer sbLocation = new StringBuffer();
			sbLocation.append(updateLocation(locationProvidersStatus));
			txtLocation.setText(sbLocation.toString());
		}
		else {
			Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
	}
	
	/**
     * Call an out-of-process service. Process involves:
     * 1. Select valid method signature
     * 2. Create message with corresponding index number
     * 3. Create a bundle (cf. http://developer.android.com/reference/android/os/Bundle.html) for restrictions on data types 
     * 4. Add parameter values. The values are held in key-value pairs with the parameter name being the key
     * 5. Send message
     * 
     * Currently no return value is returned. To do so would require a reverse binding process from the service, 
     * i.e a callback interface and handler/messenger code in the consumer. The use of intents or even selective intents (define
     * an intent that can only be intercepted by a stated application) will achieve the same result with less binding.
     * @param view
     */
    public void onButtonRefreshUsingDifferentProcessClick(View view) {
    	// If this service is available
    	if (opBoundToService) {
    		// -- Connectivity
    		//isInternetConnectivityOn
    		// Name the out process method
    		String targetMethod = "isInternetConnectivityOn(String callerPackageName)";
    		Message outMessage = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IDeviceStatus.methodsArray, targetMethod), 0, 0);
    		// Fill parameters
    		Bundle outBundle = new Bundle();
    		outBundle.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.getClass().getPackage().getName());
    		outMessage.setData(outBundle);
    		// Call the out process method
    		try {
				targetOPService.send(outMessage);
			} catch (RemoteException e) {
				Toast.makeText(this, "No such method in this service.", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
    		
    		// getConnectivityProvidersStatus
    		// Name the out process method
    		String nameGetConnectivityProvidersStatus = "getConnectivityProvidersStatus(String callerPackageName)";
    		Message getConnectivityProvidersStatus = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IDeviceStatus.methodsArray, nameGetConnectivityProvidersStatus), 0, 0);
    		// Fill parameters
    		Bundle getConnectivityProvidersStatusParams = new Bundle();
    		getConnectivityProvidersStatusParams.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.getClass().getPackage().getName());
    		getConnectivityProvidersStatus.setData(getConnectivityProvidersStatusParams);
    		// Call the out process method
    		try {
    			targetOPService.send(getConnectivityProvidersStatus);
    		} catch (RemoteException e) {
    			Toast.makeText(this, "No such method in this service.", Toast.LENGTH_SHORT);
    			e.printStackTrace();
    		}
    		
    		// -- Battery
			IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			registerReceiver(new ServiceReceiver(), batteryLevelFilter);
    		
			// -- Location
    		// getLocationProvidersStatus
    		// Name the out process method
    		String nameGetLocationProvidersStatus = "getLocationProvidersStatus(String callerPackageName)";
    		Message getLocationProvidersStatus = Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IDeviceStatus.methodsArray, nameGetLocationProvidersStatus), 0, 0);
    		// Fill parameters
    		Bundle getLocationProvidersStatusParams = new Bundle();
    		getLocationProvidersStatusParams.putString(ServiceMethodTranslator.getMethodParameterName(targetMethod, 0), this.getClass().getPackage().getName());
    		getLocationProvidersStatus.setData(getLocationProvidersStatusParams);
    		// Call the out process method
    		try {
    			targetOPService.send(getLocationProvidersStatus);
    		} catch (RemoteException e) {
    			Toast.makeText(this, "No such method in this service.", Toast.LENGTH_SHORT);
    			e.printStackTrace();
    		}
    	}
    	else {
    		Toast.makeText(this, "No service connected.", Toast.LENGTH_SHORT);
		}
    }

    /**
     * Utilities button to reset all values of this activity
     * @param view
     */
	public void onButtonResetClick(View view) {
		txtConnectivity.setText("Nothing yet");
		txtBattery.setText("Nothing yet");
		txtLocation.setText("Nothing yet");
    }
	
	
	/* **************
	 * Broadcast receiver
	 * ************** */
	
	/**
	 * Broadcast receiver to receive intents from Service methods
	 * 
	 * TODO: Intent Categories could be used to discriminate between 
	 * returned method intents rather than an intent per method 
	 *
	 */
	private class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(this.getClass().getSimpleName(), intent.getAction());
			
			// Connectivity
			if (intent.getAction().equals(IDeviceStatus.CONNECTIVITY_STATUS)) {
				Log.i(this.getClass().getSimpleName(), "Out of process real service received intent - CONNECTIVITY");

				boolean isInternetEnabled = false;
				List<ProviderStatus> connectivityProviders = new ArrayList<ProviderStatus>();
				if(intent.hasExtra(IDeviceStatus.CONNECTIVITY_INTERNET_ON)) {
					isInternetEnabled = intent.getBooleanExtra(IDeviceStatus.CONNECTIVITY_INTERNET_ON, false);
				}
				if(intent.hasExtra(IDeviceStatus.CONNECTIVITY_PROVIDER_LIST)) {
					connectivityProviders = intent.getParcelableArrayListExtra(IDeviceStatus.CONNECTIVITY_PROVIDER_LIST);
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append(updateConnectivity(isInternetEnabled, connectivityProviders));
				txtConnectivity.setText(sb.toString());
			}
			// Location
			else if (intent.getAction().equals(IDeviceStatus.LOCATION_STATUS)) {
				Log.i(this.getClass().getSimpleName(), "Out of process real service received intent - LOCATION_STATUS");
				
				List<ProviderStatus> locationProvidersStatus =  intent.getParcelableArrayListExtra(IDeviceStatus.LOCATION_PROVIDER_LIST);
				
				StringBuffer sb = new StringBuffer();
				sb.append(updateLocation(locationProvidersStatus));
				txtLocation.setText(sb.toString());
			}
			// Battery
			else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				context.unregisterReceiver(this);
				double level = -1;
				double temperature = -1;
				double voltage = -1;
				int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int rawTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				int rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

				if (rawLevel >= 0 && scale > 0) {
					level = (rawLevel * 100) / scale;
				}
				if (rawTemperature >= 0) {
					temperature = rawTemperature/10;
				}
				if (rawVoltage >= 0) {
					voltage = rawVoltage/1000;
				}
				BatteryStatus batteryStatus = new BatteryStatus(level, scale, voltage, temperature, status, plugged);
				Log.e(this.getClass().getSimpleName(), "Battery status > level: "+level+"% (="+rawLevel+"/"+scale+"), temperature: "+temperature+"°C (="+rawTemperature+"), voltage: "+voltage+"V (="+rawVoltage+"mV)");
				txtBattery.setText(updateBattery(batteryStatus));
			}
		}
		
	}
	

	public String updateConnectivity(boolean isInternetEnabled, List<ProviderStatus> connectivityProviders) {
		StringBuffer sb = new StringBuffer();

		// -- Internet enabled?
		sb.append("** Internet is enabled? "+(isInternetEnabled ? "yes" : "no")+"\n");

		// --- Providers
		for(ProviderStatus connectivityProvider : connectivityProviders) {
			sb.append("** "+connectivityProvider.getName()+": "+(connectivityProvider.isEnabled() ? "enabled" : "disabled")+"\n");
		}

		return sb.toString();
	}

	public String updateBattery(BatteryStatus batteryStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("Remaining level: "+batteryStatus.getLevel()+"%\n" +
				"Temperature: "+batteryStatus.getTemperature()+"°C\n" +
				"Voltage: "+batteryStatus.getVoltage()+"V\n"+
				"Status: ");
		
		switch(batteryStatus.getStatus()) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			sb.append("charging");
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			sb.append("discharging");
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			sb.append("not charging");
			break;
		case BatteryManager.BATTERY_STATUS_FULL:
			sb.append("full");
			break;
		default: sb.append("unknown");
		}
		
		switch(batteryStatus.getPlugged()) {
		case BatteryManager.BATTERY_PLUGGED_AC:
			sb.append(", plugged on AC");
		case BatteryManager.BATTERY_PLUGGED_USB:
			sb.append(", plugged on USB");
		default:
			sb.append(", not plugged");
		}
		sb.append("\n");

		return sb.toString();
	}

	public String updateLocation(List<ProviderStatus> locationProviderStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("Providers:\n");
		for (ProviderStatus providerStatus : locationProviderStatus) {
			sb.append("* "+providerStatus.getName()+" ["+(providerStatus.isEnabled() ? "enabled" : "disabled")+"]\n");
		}
		// -- Add these data to the text
		return sb.toString();
	}
}
