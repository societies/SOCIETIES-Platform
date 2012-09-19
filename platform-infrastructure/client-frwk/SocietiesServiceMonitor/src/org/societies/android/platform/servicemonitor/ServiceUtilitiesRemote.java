package org.societies.android.platform.servicemonitor;

import org.societies.android.api.servicelifecycle.IServiceUtilities;
import org.societies.android.api.utilities.RemoteServiceHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Remote ServiceManagement service wrapper for {@link IServiceUtilities} methods 
 */
public class ServiceUtilitiesRemote extends Service {
	private static final String LOG_TAG = ServiceUtilitiesRemote.class.getName();
	private Messenger inMessenger;

	@Override
	public void onCreate () {
		ServiceUtilitiesBase serviceManagementBase = new ServiceUtilitiesBase(this.getApplicationContext());
		
		this.inMessenger = new Messenger(new RemoteServiceHandler(serviceManagementBase.getClass(), serviceManagementBase, IServiceUtilities.methodsArray));
		Log.i(LOG_TAG, "ServiceUtilitiesRemote creation");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "ServiceUtilitiesRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServiceUtilitiesRemote terminating");
	}

}
