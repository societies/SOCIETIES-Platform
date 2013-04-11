package org.societies.android.platform.comms;

import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.utilities.RemoteServiceHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Remote ServiceManagement service wrapper for {@link XMPPAgent} methods 
 */
public class ServicePlatformCommsRemote extends Service {

	private static final String LOG_TAG = ServicePlatformCommsRemote.class.getName();
	private Messenger inMessenger;
	private AndroidCommsBase serviceBase;

	@Override
	public void onCreate () {
		serviceBase = new AndroidCommsBase(this, true);
		
		this.inMessenger = new Messenger(new RemoteServiceHandler(serviceBase.getClass(), serviceBase, XMPPAgent.methodsArray));
		Log.d(LOG_TAG, "ServicePlatformCommsRemote creation");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "ServicePlatformCommsRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServicePlatformCommsRemote terminating");
		serviceBase.serviceCleanup();
	}


}
