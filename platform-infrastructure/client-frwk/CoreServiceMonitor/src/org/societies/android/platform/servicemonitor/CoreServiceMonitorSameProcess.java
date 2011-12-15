package org.societies.android.platform.servicemonitor;

import org.societies.android.platform.interfaces.ICoreServiceMonitor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CoreServiceMonitorSameProcess extends Service implements ICoreServiceMonitor {


	private final IBinder binder;
	
	public CoreServiceMonitorSameProcess() {
		super();
		binder = new LocalBinder();
	}
	
	public class LocalBinder extends Binder {
		CoreServiceMonitorSameProcess getService() {
			return CoreServiceMonitorSameProcess.this;
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

	@Override
	public String getGreeting() {
		return "Bah humbug";
	}

}
