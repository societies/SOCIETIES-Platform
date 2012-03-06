package org.societies.pubsub;

import java.io.FileDescriptor;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.comm.android.ipc.Skeleton;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;

public class PubsubService extends Service {
	
	private static final Logger log = LoggerFactory.getLogger(PubsubService.class);
	
	private static Skeleton skeleton;
	
	@Override
    public IBinder onBind(Intent intent) {  
    	log.debug("onBind"); 
    	IBinder rv = null;
    	if(skeleton != null)
    		rv = skeleton.messenger().getBinder();
    	else
    		rv = null;
    	return rv;
    }
    
    @Override
    public void onCreate()
    {    	    	
    	log.debug("onCreate");  
		try {
			ClientCommunicationMgr ccm = new ClientCommunicationMgr(PubsubService.this) {
				protected void createIdentityManager() {
					try {
						idm = new IdentityManagerImpl("android@societies.local/default");
					} catch (InvalidFormatException e) {
						throw new RuntimeException(e);
					}
				}
			};
	        ICommManager endpoint = new CommManagerAdapter(ccm);
	        PubsubClientImpl pubsubClient = new PubsubClientImpl(endpoint);
	        Pubsub pubsub = new PubsubSkeleton(pubsubClient);
    		skeleton = new Skeleton(pubsub);	
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}  	
    }
    
    @Override
    public void onDestroy()
    {
    	log.debug("onDestroy");      
    }
    
    private static class CommManagerAdapter implements ICommManager {
    	
    	private ClientCommunicationMgr clientCommunicationMgr;
    	
    	public CommManagerAdapter(ClientCommunicationMgr clientCommunicationMgr) {
    		this.clientCommunicationMgr = clientCommunicationMgr;
    	}

		public void register(IFeatureServer featureServer)
				throws CommunicationException {
			throw new UnsupportedOperationException();
		}

		public void register(ICommCallback messageCallback)
				throws CommunicationException {
			clientCommunicationMgr.register(PubsubClientImpl.getXMLElements(), messageCallback);
		}

		public void sendIQGet(Stanza stanza, Object payload,
				ICommCallback callback) throws CommunicationException {						
			clientCommunicationMgr.sendIQ(stanza, IQ.Type.GET, payload, callback);			
		}

		public void sendIQSet(Stanza stanza, Object payload,
				ICommCallback callback) throws CommunicationException {
			clientCommunicationMgr.sendIQ(stanza, IQ.Type.SET, payload, callback); // TODO remove dep with smack by changing interface
		}

		public void sendMessage(Stanza stanza, String type, Object payload)
				throws CommunicationException {
			Message.Type mType = Message.Type.valueOf(type);
			clientCommunicationMgr.sendMessage(stanza, mType, payload);
		}

		public void sendMessage(Stanza stanza, Object payload)
				throws CommunicationException {
			clientCommunicationMgr.sendMessage(stanza, payload);
		}

		public void addRootNode(XMPPNode newNode) {
			throw new UnsupportedOperationException("Not implemented!");
		}

		public void removeRootNode(XMPPNode node) {
			throw new UnsupportedOperationException("Not implemented!");
		}

		public String getInfo(IIdentity entity, String node,
				ICommCallback callback) throws CommunicationException {
			throw new UnsupportedOperationException("Not implemented!");
		}

		public String getItems(IIdentity entity, String node,
				ICommCallback callback) throws CommunicationException {
			return clientCommunicationMgr.getItems(entity, node, callback);
		}

		public IIdentity getIdentity() { 
			return clientCommunicationMgr.getIdentity();
		}

		public IIdentityManager getIdManager() {
			return clientCommunicationMgr.getIdManager();
		}
    }
}
