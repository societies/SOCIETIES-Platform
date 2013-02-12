package org.societies.android.platform.events;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.RemoteServiceHandler;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.pubsub.helper.PubsubHelper;

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
		PlatformEventsBase serviceBase = new PlatformEventsBase(this, createPubSubClientAndroid(), createClientCommunicationMgr(), false);
		
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

	/**
	 * Factory method to get instance of {@link PubsubClientAndroid}
	 * @return PubsubClientAndroid
	 */
	protected PubsubHelper createPubSubClientAndroid() {
		return new PubsubHelper(this);
	}
	
	/**
	 * Factory method to get instance of {@link ClientCommunicationMgr}
	 * @return ClientCommunicationMgr
	 */
	protected ClientCommunicationMgr createClientCommunicationMgr() {
		return new ClientCommunicationMgr(this, true); 
	}

}
