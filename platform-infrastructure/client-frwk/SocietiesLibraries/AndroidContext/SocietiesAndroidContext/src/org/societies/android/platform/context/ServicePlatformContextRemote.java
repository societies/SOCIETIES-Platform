package org.societies.android.platform.context;

import org.societies.android.api.context.ACtxClient;
import org.societies.android.api.utilities.RemoteServiceHandler;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Remote ServiceContext service wrapper for {@link ACtxClient} methods 
 */
public class ServicePlatformContextRemote extends Service {
	private static final String LOG_TAG = ServicePlatformContextRemote.class.getName();
	private Messenger inMessenger;

	@Override
	public void onCreate () {
		ClientCommunicationMgr ccm = new ClientCommunicationMgr(getApplicationContext());
		
		PlatformContextBase serviceBase = new PlatformContextBase(this.getApplicationContext(), ccm, false);
		
		this.inMessenger = new Messenger(new RemoteServiceHandler(serviceBase.getClass(), serviceBase, ACtxClient.methodsArray));
		Log.i(LOG_TAG, "ServicePlatformContextRemote creation");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "ServicePlatformContextRemote onBind");
		return inMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "ServicePlatformContextRemote terminating");
	}

}
