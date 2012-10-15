package org.societies.android.platform.events;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.RemoteServiceHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Remote ServiceManagement service wrapper for {@link IServiceUtilities} methods 
 */
public class ServicePlatformEventsRemote extends Service {
	private static final String LOG_TAG = ServicePlatformEventsRemote.class.getName();
	private Messenger inMessenger;

	@Override
	public void onCreate () {
		PlatformEventsBase serviceBase = new PlatformEventsBase(this.getApplicationContext());
		
		this.inMessenger = new Messenger(new RemoteServiceHandler(serviceBase.getClass(), serviceBase, IAndroidSocietiesEvents.methodsArray));
		Log.i(LOG_TAG, "ServicePlatformEventsRemote creation");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "ServicePlatformEventsRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServicePlatformEventsRemote terminating");
	}

}
