package org.societies.comm.xmpp.client.impl;

import static android.content.Context.BIND_AUTO_CREATE;

import java.security.InvalidParameterException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.societies.interfaces.XMPPAgent;
import org.societies.comm.android.ipc.MethodInvocationServiceConnection;
import org.societies.comm.android.ipc.IMethodInvocation;
import org.societies.comm.android.ipc.Stub;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class ClientCommunicationMgr {
	
	private static Logger LOG = LoggerFactory.getLogger(ClientCommunicationMgr.class);
	
	private static final ComponentName serviceCN = new ComponentName("org.societies", "org.societies.AgentService"); // TODO

	private Context androidContext;
	private PacketMarshaller marshaller = new PacketMarshaller();
	private ServiceConnection registerConnection;	
	private MethodInvocationServiceConnection<XMPPAgent> miServiceConnection;
	
	protected IIdentityManager idm;
	
	public ClientCommunicationMgr(Context androidContext) {
		this.androidContext = androidContext;
		Intent intent = new Intent();
        intent.setComponent(serviceCN);
		miServiceConnection = new MethodInvocationServiceConnection<XMPPAgent>(intent, androidContext, BIND_AUTO_CREATE, XMPPAgent.class);
	}
	
	public void register(final List<String> elementNames, final ICommCallback callback) {
		final List<String> namespaces = callback.getXMLNamespaces();
		marshaller.register(elementNames, callback.getXMLNamespaces(), callback.getJavaPackages());
		registerConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, new Messenger(binder));				
				agent.register(elementNames.toArray(new String[0]), namespaces.toArray(new String[0]), new CallbackAdapter(callback, androidContext, this, marshaller));				
			}

			public void onServiceDisconnected(ComponentName cn) {				
			}			
		};
		
		bindService(registerConnection);
	}
	
	public void unregister(final List<String> elementNames, final ICommCallback callback) {
		final List<String> namespaces = callback.getXMLNamespaces();		
		ServiceConnection connection = new ServiceConnection() {			
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class},  new Messenger(binder));
				agent.unregister(elementNames.toArray(new String[0]), namespaces.toArray(new String[0]));
				try {
					androidContext.unbindService(this);
					androidContext.unbindService(registerConnection);
				} catch(Exception e) {
					LOG.error("Exception while unbinding service.", e);
				}
			}
			
			public void onServiceDisconnected(ComponentName cn) {				
			}			
		};
		
		bindService(connection);
	}
	
	public void sendMessage(Stanza stanza, Message.Type type, Object payload)
			throws CommunicationException {
		
		if (payload == null) {
			throw new InvalidParameterException("Payload cannot be null");
		}
		try {
			
			String xml = marshaller.marshallMessage(stanza, type, payload);
			
			sendMessage(xml);
			
		} catch (Exception e) {
			throw new CommunicationException("Error sending message", e);
		}				
	}
	
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException {
		sendMessage(stanza, null, payload);
	}	
	
	public void sendIQ(Stanza stanza, IQ.Type type, Object payload,
			ICommCallback callback) throws CommunicationException {
		try {
			String xml = marshaller.marshallIQ(stanza, type, payload);

			sendIQ(xml, callback);
			
		} catch (Exception e) {
			throw new CommunicationException("Error sending IQ message", e);
		}
	}
	
	public IIdentity getIdentity() {
		String identityJid = getIdentityJid();
		try {
			return IdentityManagerImpl.staticfromJid(identityJid);
		} catch (InvalidFormatException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public IIdentityManager getIdManager() {
		if(idm == null) {
			try {
				idm = new IdentityManagerImpl(getIdentityJid());
			} catch (InvalidFormatException e) {
				throw new RuntimeException(e);
			}
		}
		return idm;
	}
	
	public String getItems(final IIdentity entity, final String node, final ICommCallback callback) throws CommunicationException {
		try {
			return (String)miServiceConnection.invokeAndKeepBound(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.getItems(entity.getJid(), node, new CallbackAdapter(callback, androidContext, miServiceConnection, marshaller));
				}
			});
		} catch (Throwable e) {
			if(e instanceof CommunicationException)
				throw (CommunicationException)e;
			else
				throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	private void sendMessage(final String xml) {
		ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, new Messenger(binder));
				agent.sendMessage(xml);		
				androidContext.unbindService(this);
			}

			@Override
			public void onServiceDisconnected(ComponentName cn) {				
			}
			
		};
		
		bindService(connection);
	}
	
	private void sendIQ(final String xml, final ICommCallback callback) {
		ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, new Messenger(binder));				
				agent.sendIQ(xml, new CallbackAdapter(callback, androidContext, this, marshaller));
			}

			@Override
			public void onServiceDisconnected(ComponentName cn) {				
			}
			
		};
		
		bindService(connection);
	}
	
	private void bindService(ServiceConnection connection) {
		Intent intent = new Intent();
        intent.setComponent(serviceCN);
        androidContext.bindService(intent, connection, BIND_AUTO_CREATE);
	}
	
	private String getIdentityJid() {
		String identityJid;
		try {
			identityJid = (String)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.getIdentity();
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return identityJid;
	}
}
