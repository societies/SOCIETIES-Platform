package org.societies.comm.xmpp.client.impl;

import static android.content.Context.BIND_AUTO_CREATE;

import java.security.InvalidParameterException;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
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
import android.util.Log;

public class ClientCommunicationMgr {
	
	private static final String LOG_TAG = ClientCommunicationMgr.class.getName();

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
					Log.e(LOG_TAG, "Exception while unbinding service.", e);
				}
			}
			
			public void onServiceDisconnected(ComponentName cn) {				
			}			
		};
		
		bindService(connection);
	}
	
	public boolean UnRegisterCommManager() {
		boolean rv;
		try {
			rv = (Boolean)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.UnRegisterCommManager();
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return rv;
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

			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, new Messenger(binder));
				agent.sendMessage(xml);		
				androidContext.unbindService(this);
			}

			public void onServiceDisconnected(ComponentName cn) {				
			}
			
		};
		
		bindService(connection);
	}
	
	private void sendIQ(final String xml, final ICommCallback callback) {
		ServiceConnection connection = new ServiceConnection() {

			public void onServiceConnected(ComponentName cn, IBinder binder) {
				XMPPAgent agent = (XMPPAgent)Stub.newInstance(new Class<?>[]{XMPPAgent.class}, new Messenger(binder));				
				agent.sendIQ(xml, new CallbackAdapter(callback, androidContext, this, marshaller));
			}

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

	public boolean isConnected() {
		boolean connected;
		try {
			connected = (Boolean)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.isConnected();
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return connected;
	}
	
	public INetworkNode login(final String identifier, final String domain, final String password) {		
		try {
			String rv;
			rv = (String)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.login(identifier, domain, password);
				}
			});
			if(rv == null)
				return null;
			return new IdentityManagerImpl(rv).getThisNetworkNode();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public INetworkNode loginFromConfig() {
		try {
			String rv = (String)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.loginFromConfig();
				}
			});
			if(rv == null)
				return null;
			return new IdentityManagerImpl(rv).getThisNetworkNode();
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public boolean logout() {
		boolean rv;
		try {
			rv = (Boolean)miServiceConnection.invoke(new IMethodInvocation<XMPPAgent>() {
				public Object invoke(XMPPAgent agent) throws Throwable {
					return agent.logout();
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return rv;
	}
	
}
