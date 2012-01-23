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
import java.util.Set;

import org.societies.android.platform.R;
import org.societies.android.platform.devicestatus.DeviceStatusServiceSameProcess.LocalBinder;
import org.societies.android.platform.interfaces.IDeviceStatus;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;
import org.societies.android.platform.interfaces.model.LocationProviderStatus;

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


/**
 * @author Olivier Maridat
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
        intentFilter.addAction(IDeviceStatus.CONNECTIVITY);
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
			StringBuffer sbConnectivity = new StringBuffer();
			sbConnectivity.append("** Internet is enabled? "+(targetIPService.isInternetConnectivityOn(this.getClass().getPackage().getName()) ? "yes" : "no")+"\n");
			txtConnectivity.setText(sbConnectivity.toString());
			
			StringBuffer sbLocation = new StringBuffer();
			List<LocationProviderStatus> locationProviderStatus = (List<LocationProviderStatus>) targetIPService.getLocationProvidersStatus(this.getClass().getPackage().getName());
			sbLocation.append(updateLocation(locationProviderStatus));
			txtLocation.setText(sbLocation.toString());
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append("No service connected.\n");
			txtConnectivity.setText(sb.toString());
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
    		// -- isInternetConnectivityOn
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
				txtConnectivity.setText("No such method in this service.\n");
				e.printStackTrace();
			}
    		
    		// -- getLocationProvidersStatus
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
    			txtConnectivity.setText("No such method in this service.\n");
    			e.printStackTrace();
    		}
    	}
    	else {
			txtConnectivity.setText("No service connected.\n");
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
			
			if (intent.getAction().equals(IDeviceStatus.CONNECTIVITY)) {
				Log.i(this.getClass().getSimpleName(), "Out of process real service received intent - CONNECTIVITY");

				boolean isInternetOn =  intent.getBooleanExtra(IDeviceStatus.INTENT_RETURN_KEY, false);
				
				// -- Internet enabled?
				StringBuffer sb = new StringBuffer();
				sb.append("** Internet is enabled? "+(isInternetOn ? "yes" : "no")+"\n");
				txtConnectivity.setText(sb.toString());
			}
			else if (intent.getAction().equals(IDeviceStatus.LOCATION_STATUS)) {
				Log.i(this.getClass().getSimpleName(), "Out of process real service received intent - LOCATION_STATUS");
				
				
				String returnType =  intent.getStringExtra(IDeviceStatus.INTENT_RETURN_TYPE);
				List<LocationProviderStatus> locationProvidersStatus =  intent.getParcelableArrayListExtra(IDeviceStatus.INTENT_RETURN_KEY);
				
				StringBuffer sbLocation = new StringBuffer();
				sbLocation.append(updateLocation(locationProvidersStatus));
				txtLocation.setText(sbLocation.toString());
			}
		}
		
	}
	

	public void updateConnectivity() {
		StringBuffer sb = new StringBuffer();
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// -- Internet enabled?
		sb.append("** Internet is enabled? "+(isInternetEnabled(connectivity) ? "yes" : "no")+"\n");

		// --- Mobile, Wifi, Wimax, ...
		NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
		int length = networkInfos.length;
		for (int i=0; i<length; i++) {
			NetworkInfo networkInfo = networkInfos[i];
			sb.append("** "+networkInfo.getTypeName()+" ["+networkInfo.getSubtypeName()+"]\n");
			if (networkInfo.getState().equals(NetworkInfo.State.UNKNOWN)) {
				sb.append("Not available\n");
			}
			else {
				sb.append("State: "+networkInfo.getState()+" ["+networkInfo.getDetailedState()+"]\n");
				sb.append("Reason: "+networkInfo.getReason()+"\n");
				sb.append("Extra info: "+networkInfo.getExtraInfo()+"\n");
			}
		}

		// --- BLUETOUTH
		sb.append("** Blutouth\n");
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			sb.append("Not available\n");
		}
		else if(!bluetoothAdapter.isEnabled()) {
			sb.append("Disabled\n");
		}
		else {
			sb.append("Enabled\n");
			Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				sb.append("Device available: "+pairedDevices.size()+"\n");
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					// Add the name and address to an array adapter to show in a ListView
					sb.append(device.getName()+": "+device.getAddress()+"\n");
				}
			}
			else {
				sb.append("0 device available\n");
			}
		}

		// -- Add these data to the text
		txtConnectivity.setText(sb.toString());
	}

	public boolean isInternetEnabled(ConnectivityManager connectivity) {
		return (connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED);
	}


	public void updateBattery() {
		// launch a broadcast receiver to be inform of battery status
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			double level = -1;
			int scale = -1;
			double voltage = -1;
			double temperature = -1;
			int status = -1;
			int plugged = -1;
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int rawTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				int rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

				if (rawLevel >= 0 && scale > 0) {
					level = (rawLevel * 100) / scale;
				}
				if (rawTemperature >= 0) {
					temperature = rawTemperature/10;
				}
				if (rawVoltage >= 0) {
					voltage = rawVoltage/1000;
				}
				Log.e(this.getClass().getSimpleName(), "Battery status > level: "+level+"% (="+rawLevel+"/"+scale+"), temperature: "+temperature+"°C (="+rawTemperature+"), voltage: "+voltage+"V (="+rawVoltage+"mV)");
				StringBuffer sb = new StringBuffer();
				sb.append("Remaining level: "+level+"%\n" +
						"Temperature: "+temperature+"°C\n" +
						"Voltage: "+voltage+"V\n"+
						"Status: ");

				switch(status) {
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
				sb.append(pluggedOn(plugged));
				sb.append("\n");

				txtBattery.setText(sb.toString());
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	public String pluggedOn(int plugged) {
		switch(plugged) {
		case BatteryManager.BATTERY_PLUGGED_AC:
			return ", plugged on AC";
		case BatteryManager.BATTERY_PLUGGED_USB:
			return ", plugged on USB";
		default:
			return ", not plugged";
		}
	}
	public String updateLocation(List<LocationProviderStatus> locationProviderStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("Providers:\n");
		for (LocationProviderStatus providerStatus : locationProviderStatus) {
			sb.append("* "+providerStatus.getName()+" ["+(providerStatus.isEnabled() ? "enabled" : "disabled")+"]\n");
		}
		// -- Add these data to the text
		return sb.toString();
	}
}
