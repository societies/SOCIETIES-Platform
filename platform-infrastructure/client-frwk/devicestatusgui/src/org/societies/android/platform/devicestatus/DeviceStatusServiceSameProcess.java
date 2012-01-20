package org.societies.android.platform.devicestatus;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.platform.interfaces.IDeviceStatus;
import org.societies.android.platform.interfaces.model.LocationProviderStatus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

public class DeviceStatusServiceSameProcess extends Service implements IDeviceStatus {
	private final IBinder binder;
	
	public DeviceStatusServiceSameProcess() {
		super();
		binder = new LocalBinder();
	}
	
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

	public boolean isInternetConnectivityOn() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED);
	}

//	public List<?> LocationProviderStatus() {
//		List<LocationProviderStatus> locationProviders = new ArrayList<LocationProviderStatus>();
//		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		List<String> providers = locationManager.getAllProviders();
//		for (String provider : providers) {
//			locationProviders.add(new LocationProviderStatus(provider, locationManager.isProviderEnabled(provider)));
//		}
//		return locationProviders;
//	}
}
