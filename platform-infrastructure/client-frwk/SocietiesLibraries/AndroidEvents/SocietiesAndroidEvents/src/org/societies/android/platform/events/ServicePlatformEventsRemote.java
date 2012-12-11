package org.societies.android.platform.events;

import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.utilities.RemoteServiceHandler;
import org.societies.android.platform.androidutils.AppPreferences;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Remote ServiceManagement service wrapper for {@link IServiceUtilities} methods 
 */
public class ServicePlatformEventsRemote extends Service {
	private static final String DOMAIN_AUTHORITY_SERVER_PORT = "daServerPort";
	private static final String DOMAIN_AUTHORITY_NAME = "daNode";
	private static final String LOCAL_CSS_NODE_JID_RESOURCE = "cssNodeResource";

	private static final String LOG_TAG = ServicePlatformEventsRemote.class.getName();
	private Messenger inMessenger;

	@Override
	public void onCreate () {
		PlatformEventsBase serviceBase = new PlatformEventsBase(this.getApplicationContext(), createPubSubClientAndroid(), createClientCommunicationMgr(), false);
		
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
	protected PubsubClientAndroid createPubSubClientAndroid() {
		return new PubsubClientAndroid(this);
	}
	
	/**
	 * Factory method to get instance of {@link ClientCommunicationMgr}
	 * @return ClientCommunicationMgr
	 */
	protected ClientCommunicationMgr createClientCommunicationMgr() {
		ClientCommunicationMgr ccm = new ClientCommunicationMgr(this); 
		
		AppPreferences appPreferences = new AppPreferences(this);

		int xmppServerPort = appPreferences.getIntegerPrefValue(DOMAIN_AUTHORITY_SERVER_PORT);
		String domainAuthorityName = appPreferences.getStringPrefValue(DOMAIN_AUTHORITY_NAME);
		String nodeJIDResource = appPreferences.getStringPrefValue(LOCAL_CSS_NODE_JID_RESOURCE);
		
		ccm.setDomainAuthorityNode(domainAuthorityName);
		ccm.setPortNumber(xmppServerPort);
		ccm.setResource(nodeJIDResource);

		return ccm;

	}

}
