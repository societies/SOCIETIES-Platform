package org.societies.android.platform.pubsub;

import org.societies.android.api.pubsub.IPubsubService;
import org.societies.android.api.utilities.RemoteServiceHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;


/**
 * Remote Pubsub service wrapper for {@link PubsubServiceBase} methods 
 */
public class ServicePlatformPubsubRemote extends Service {

	private static final String LOG_TAG = ServicePlatformPubsubRemote.class.getName();
	private Messenger inMessenger;

	@Override
	public void onCreate () {
		PubsubServiceBase serviceBase = new PubsubServiceBase(this, getAndroidCommsMgr(), true);
		
		this.inMessenger = new Messenger(new RemoteServiceHandler(serviceBase.getClass(), serviceBase, IPubsubService.methodsArray));
		Log.d(LOG_TAG, "ServicePlatformPubsubRemote creation");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "ServicePlatformPubsubRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServicePlatformPubsubRemote terminating");
	}

	/**
	 * Get a Android Comms helper object suitably configured.
	 * Assumes that user has already configured and logged into the XMPP server.
	 * 
	 * @return ClientCommunicationMgr
	 */
	private PubsubCommsMgr getAndroidCommsMgr() {
		return new PubsubCommsMgr(this);
	}

}
