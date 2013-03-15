/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.android.platform.comms;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.android.api.comms.xmpp.XMPPNode;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.utilities.DBC.Dbc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AndroidCommsBase implements XMPPAgent {
	private static final String LOG_TAG = AndroidCommsBase.class.getName();
	private static final long PUBSUB_EVENT_CALBACK_ID = -9999999999999L;
	private static final String PUBSUB_NAMESPACE_KEY = "http://jabber.org/protocol";
	private static final boolean DEBUG_LOGGING = false;
	
	private static final String NOTIFICATION_TITLE = "Societies Communications Problem";
	private static final String COMMS_NO_CONNECTIVITY = "NotConnected";
	private static final String COMMS_CANNOT_REGISTER = "RegistrationError";
	private static final String COMMS_CANNOT_UNREGISTER = "UnRegistrationError";
	private static final String COMMS_CANNOT_SEND_MESSAGE = "SendMessageError";
	private static final String COMMS_CANNOT_SEND_IQ = "SendIQError";
	private static final String COMMS_CANNOT_CREATE_ID = "IdCreationError";
	private static final String COMMS_CANNOT_LOGIN = "LoginError";
	private static final String COMMS_CANNOT_LOGOUT = "LogoutError";

	private XMPPConnection connection;
	private String username, password, resource;
	private int usingConnectionCounter = 0;
	private ProviderElementNamespaceRegistrar providerRegistrar = new ProviderElementNamespaceRegistrar();
	private RawXmlProvider rawXmlProvider = new RawXmlProvider();
	private String domainAuthorityNode;
	int port;
	boolean debug;
	boolean restrictBroadcast;
	boolean pubsubRegistered;
	PacketListener pubsubListener;
	Context serviceContext;
	BroadcastReceiver receiver;

	
	public AndroidCommsBase(Context serviceContext, boolean restrictBroadcast) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "Service Base Object created");
		};
		this.restrictBroadcast = restrictBroadcast;
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "Restrict broadcasted intents: " + this.restrictBroadcast);
		};
		this.serviceContext = serviceContext;
		this.pubsubRegistered = false;
		this.pubsubListener = null;

		//Android Profiling
//		Debug.startMethodTracing(this.getClass().getSimpleName());
	}
	
	public boolean register(String client, String[] elementNames, String[] namespaces, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Message Beans must be specified", null != elementNames && elementNames.length > 0);
		Dbc.require("Namespaces must be specified", null != namespaces && namespaces.length > 0);
			
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "register for client: " + client);
//			for (String element : elementNames) {
//				Log.d(LOG_TAG, "register element name: " + element);
//			}
//			for (String namespace : namespaces) {
//				Log.d(LOG_TAG, "register namespace: " + namespace);
//			}
		};
		
		for(int i=0; i<elementNames.length; i++) {
			for(int j=0; j<namespaces.length; j++) {
				providerRegistrar.register(new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[j]));				
				ProviderManager.getInstance().addIQProvider(elementNames[i], namespaces[j], rawXmlProvider);
				ProviderManager.getInstance().addExtensionProvider(elementNames[i], namespaces[j], rawXmlProvider);
			}
		}
		
		//Send intent
		Intent intent = new Intent();
		if (AndroidCommsBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		try {
			//only need to register Pubsub listener once, otherwise multiple events instances 
			//are generated for a single event
			if (!this.pubsubRegistered && isNameSpacePubsub(namespaces)) {
				connect();
				
				this.pubsubListener = new RegisterPacketListener(client, remoteCallId);
				
				connection.addPacketListener(this.pubsubListener, new AndFilter(new PacketTypeFilter(Message.class), new NamespaceFilter(namespaces)));
				this.pubsubRegistered = true;
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "Pubsub event listener registered");
				};
			}
			intent.setAction(XMPPAgent.REGISTER_RESULT);
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, true);
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			intent.setAction(XMPPAgent.REGISTER_EXCEPTION);
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			
			createNotification("Error registering namespaces: " + e.getMessage(), COMMS_CANNOT_REGISTER, NOTIFICATION_TITLE);
		} finally {
			AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
		}
		
		return false;
	}
	
	public boolean unregister(String client, String[] elementNames, String[] namespaces, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Message Beans must be specified", null != elementNames && elementNames.length > 0);
		Dbc.require("Namespaces must be specified", null != namespaces && namespaces.length > 0);

		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "unregister for client: " + client);
//			for (String element : elementNames) {
//				Log.d(LOG_TAG, "unregister element name: " + element);
//			}
//			for (String namespace : namespaces) {
//				Log.d(LOG_TAG, "unregister namespace: " + namespace);
//			}
		};
		
		//remove Pubsub listener
		if (this.pubsubRegistered && isNameSpacePubsub(namespaces)) {
			connection.removePacketListener(this.pubsubListener);
			if (DEBUG_LOGGING) {
				Log.d(LOG_TAG, "Pubsub event listener unregistered");
			};
			this.pubsubRegistered = false;
		}

		//Send intent
		Intent intent = new Intent();
		if (AndroidCommsBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, remoteCallId);
		try {
			for(int i=0; i<elementNames.length; i++) {
				for(int j=0; j<namespaces.length; j++) {
					ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple = new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[j]);		
					providerRegistrar.unregister(tuple);
					if(!providerRegistrar.isRegistered(tuple)) { 
						removeProviders(tuple);
					}
				}
			}
			intent.setAction(XMPPAgent.UNREGISTER_RESULT);
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, true);
			
			disconnect();

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);			
			intent.setAction(XMPPAgent.UNREGISTER_EXCEPTION);
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			
			createNotification("Error un-registering namespaces: " + e.getMessage(), COMMS_CANNOT_UNREGISTER, NOTIFICATION_TITLE);
		} finally {
			AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
			
			//Android Profiling
//			Debug.stopMethodTracing();
		}

		return false;
	}
	
	public boolean UnRegisterCommManager(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "UnRegisterCommManager for client: " + client);
		};
		
		boolean retValue = false;
		
		//Send intent
		Intent intent = new Intent();
		if (AndroidCommsBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		try {
			retValue = UnRegisterCommManagerInternal();
			
			intent.setAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_RESULT);
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			intent.setAction(XMPPAgent.UN_REGISTER_COMM_MANAGER_EXCEPTION);
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));

			createNotification("Error unregistering all namespaces: " + e.getMessage(), COMMS_CANNOT_UNREGISTER, NOTIFICATION_TITLE);
		} finally {
			this.serviceContext.sendBroadcast(intent);
		}

		return false;
	}
	
	public boolean sendMessage(String client, String messageXml, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("XML must be specified", null != messageXml && messageXml.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "sendMessage for client: " + client);
		};
		
		//Send intent
		Intent intent = new Intent();
		if (AndroidCommsBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(XMPPAgent.INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		Packet formattedMessage = createPacketFromXml(messageXml);
		
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "sendMessage xml message size: " + formattedMessage.toXML().length());
			Log.d(LOG_TAG, "sendMessage xml message:" + createPacketFromXml(messageXml).toXML());
		};
		
		try {
			connect();	
			
			connection.sendPacket(formattedMessage);
		
			disconnect();
			intent.setAction(XMPPAgent.SEND_MESSAGE_RESULT);
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			intent.setAction(XMPPAgent.SEND_MESSAGE_EXCEPTION);
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(XMPPAgent.INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));

			createNotification("Error sending message: " + e.getMessage(), COMMS_CANNOT_SEND_MESSAGE, NOTIFICATION_TITLE);
		} finally {
			this.serviceContext.sendBroadcast(intent);
		}
		return false;
	}	

	public boolean sendIQ(String client, String xml, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("XML must be specified", null != xml && xml.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "sendIQ xml: " + xml+ " for client: " + client);
		};

		try {
			connect(); 
			
			String id = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement().getAttribute("id");
									
			if(id.equals("")) {
				throw new NullPointerException("IQ XML has no ID attribute!");
			}
			
			connection.addPacketListener(new SendIQPacketListener(client, remoteCallId), new AndFilter(new PacketTypeFilter(IQ.class),new PacketIDFilter(id))); 
			
			connection.sendPacket(createPacketFromXml(xml));		
			
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			//Send intent
			Intent intent = new Intent(SEND_IQ_EXCEPTION);
			if (AndroidCommsBase.this.restrictBroadcast) {
				intent.setPackage(client);
			}
			intent.putExtra(INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
			AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
			
			createNotification("Error invoking remote method: " + e.getMessage(), COMMS_CANNOT_SEND_IQ, NOTIFICATION_TITLE);
		}
		return false;
	}
	
	public String getIdentity(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "getIdentity for client: " + client);
		};
		
		String retValue = null;
		
		//Send intent
		Intent intent = new Intent(GET_IDENTITY);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		try {
			connect();			
			retValue = connection.getUser();			
			disconnect();			
			if (DEBUG_LOGGING) {
				Log.d(LOG_TAG, "getIdentity identity: " + retValue);
			};
			
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
			this.serviceContext.sendBroadcast(intent);
		}
		return null;
	}
	
	public String getDomainAuthorityNode(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "getDomainAuthorityNode for client: " + client);
		};
		
		//Send intent
		Intent intent = new Intent(GET_DOMAIN_AUTHORITY_NODE);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, this.domainAuthorityNode);
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		this.serviceContext.sendBroadcast(intent);

		return null;
	}
	
	public String getItems(String client, String entity, String node, long remoteCallId) {		
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Entity must be specified", null != entity && entity.length() > 0);
		Dbc.require("Node must be specified", null != node && node.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "getItems entity: " + entity + " for client: " + client);
		};
		
		String retValue = null;
		
		try {
			connect();
			
			DiscoverItems discoItems = new DiscoverItems();
			discoItems.setTo(entity);
			discoItems.setNode(node);		

			
			connection.addPacketListener(new GetItemsPacketListener(client, remoteCallId), 
					new AndFilter(new PacketTypeFilter(IQ.class),new PacketIDFilter(discoItems.getPacketID()))); 
			
			connection.sendPacket(discoItems);
			
			retValue = discoItems.getPacketID();

		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			//Send intent
			Intent intent = new Intent(GET_ITEMS_EXCEPTION);
			if (AndroidCommsBase.this.restrictBroadcast) {
				intent.setPackage(client);
			}
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
			intent.putExtra(INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
			AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
		} 	
		return null;
	}
	

	public boolean isConnected(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "isConnected for client: " + client);
		};
		
		boolean retValue = false;
		Intent intent = new Intent(IS_CONNECTED);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		
		if (null != connection) {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, isConnectedInternal());
		} else {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
		}
		
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
		this.serviceContext.sendBroadcast(intent);
		return retValue;
	}
	
	public String newMainIdentity(String client, String identifier, String domain, String password, long remoteCallId, String host) { 
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Identfier must be specified", null != identifier && identifier.length() > 0);
		Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
		Dbc.require("Password must be specified", null != password && password.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "newMainIdentity identity: " + identifier + " domain: " + domain + " password: " + password + " for client: " + client);
		};
		this.setupBroadcastReceiver();

		String retValue = null;
		//Send intent
		Intent intent = new Intent(NEW_MAIN_IDENTITY);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
		
		String serverHost = domain;
		if (null != host) {
			serverHost = host;
		}
		String serviceName = domain;
		
		try {
			if(null != connection && 
					connection.isConnected() && 
					connection.getHost().equals(serverHost) && 
					connection.getPort()==port && 
					connection.getServiceName().equals(serviceName)) {
				
				createAccount(connection, identifier, password);
				retValue = username(identifier, domain) + "/" + resource;
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "Created user JID: " + retValue);
				};
			}
			else {
				ConnectionConfiguration config = new ConnectionConfiguration(serverHost, port, serviceName);
				Connection newIdConnection = new XMPPConnection(config);			
				newIdConnection.connect();
				
				createAccount(newIdConnection, identifier, password);
				
				newIdConnection.disconnect();
				retValue = username(identifier, serviceName) + "/" + resource;
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "Created user JID: " + retValue);
				};
			}
			//Send intent
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			//Send intent
			intent = new Intent(XMPPAgent.NEW_MAIN_IDENTITY_EXCEPTION);
			if (AndroidCommsBase.this.restrictBroadcast) {
				intent.setPackage(client);
			}
			intent.putExtra(INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
			
			createNotification("Error creating new identity: " + e.getMessage(), COMMS_CANNOT_CREATE_ID, NOTIFICATION_TITLE);
		} finally {
			if (DEBUG_LOGGING) {
				Log.d(LOG_TAG, "Create intent sent for: " + retValue);
			}
			this.serviceContext.sendBroadcast(intent);
			this.teardownBroadcastReceiver();
		}
		
		return null;
	}
	/**
	 * Allow the XMPP server to be found with its IP address
	 */
	public String login(String client, String identifier, String domain, String password, String host, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Identfier must be specified", null != identifier && identifier.length() > 0);
		Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
		Dbc.require("Password must be specified", null != password && password.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "login identifier: " + identifier + " domain: " + domain + " password: " + password + " host: " + host  + " for client: " + client);
		};
		this.setupBroadcastReceiver();

		String retValue = null;
		Intent intent = new Intent(LOGIN);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		
		if(isConnectedInternal()) {
			logoutInternal();
		}
		String username = username(identifier, domain);
		loadConfig(domain, username, password, host);
		try {
			connect();
			retValue = username + "/" + resource;
			//Send intent
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);

		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			//Send intent
			intent = new Intent(LOGIN_EXCEPTION);
			if (AndroidCommsBase.this.restrictBroadcast) {
				intent.setPackage(client);
			}
			intent.putExtra(INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
			intent.putExtra(INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
			
			createNotification("Error logging in: " + e.getMessage(), COMMS_CANNOT_LOGIN , NOTIFICATION_TITLE);
		} finally {
			//Send intent
			this.serviceContext.sendBroadcast(intent);
			Log.d(LOG_TAG, "Login intent sent");
		}
		return null;
	}

	/**
	 * Allow the XMPP server to be found with its DNS resolved name
	 */
	public String login(String client, String identifier, String domain, String password, long remoteCallId) {
		this.login(client, identifier, domain, password, null, remoteCallId);
		return null;
	}	
	
	
	public boolean logout(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "logout for client: " + client);
		};
		
		boolean retValue = false;
		Intent intent = new Intent(LOGOUT);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		
		try {
			retValue = logoutInternal();
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
			this.teardownBroadcastReceiver();
		}

		return false;
	}
	
	public boolean destroyMainIdentity(String client, long remoteCallId) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "destroyMainIdentity for client: " + client);
		};
		
		//Send intent
		Intent intent = new Intent(DESTROY_MAIN_IDENTITY);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		this.serviceContext.sendBroadcast(intent);

		return false; // http://code.google.com/p/asmack/issues/detail?id=63
//		try {
//			connection.getAccountManager().deleteAccount();
//			return true;			
//		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
//			return false;
//		}
	}
	
	
	@Override
	public boolean configureAgent(String client, String xmppDomainAuthorityNode, int xmppPort, String xmppResource, boolean xmppDebug, long remoteCallId) {
		
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Domain Authority Node must be specified", null != xmppDomainAuthorityNode && xmppDomainAuthorityNode.length() > 0);
		Dbc.require("Port must be positive", xmppPort > 0);
		Dbc.require("Resource must be specified", null != xmppResource && xmppResource.length() > 0);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "configureAgent for client: " + client);
		};
		
		this.setDomainAuthorityNode(client, xmppDomainAuthorityNode);
		this.setPortNumber(client, xmppPort);
		this.setResource(client, xmppResource);
		this.setDebug(client, xmppDebug);
		
		//Send intent
		Intent intent = new Intent(CONFIGURE_AGENT);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, true);
		if (this.restrictBroadcast) {
			if (DEBUG_LOGGING) {
				Log.d(LOG_TAG, "Restrict broadcast to package: " + client);
			};
			intent.setPackage(client);
		}
		intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);

		this.serviceContext.sendBroadcast(intent);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "Return Value broadcast sent: " + intent.getAction());
		};
		
		return false;
	}

	private void setDomainAuthorityNode(String client, String domainAuthorityNode) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Domain Authority Node must be specified", null != domainAuthorityNode && domainAuthorityNode.length() > 0);
		this.domainAuthorityNode = domainAuthorityNode;
	}
	
	private void setPortNumber(String client, int port) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Port must be positive", port > 0);
		
		this.port = port;
	}
	
	private void setResource(String client, String resource) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Resource must be specified", null != resource && resource.length() > 0);
		
		this.resource = resource;		
	}
	
	private void setDebug(String client, boolean enabled) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		
		this.debug = enabled;		
	}
	
	private void removeProviders(ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "removeProviders");
		}
		ProviderManager.getInstance().removeIQProvider(tuple.elementName, tuple.namespace);
		ProviderManager.getInstance().removeExtensionProvider(tuple.elementName, tuple.namespace);
	}
	
	private void connect() throws XMPPException {		
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "connect");
		};
		
		if(!connection.isConnected()) {
			connection.connect();
			connection.login(username, password, resource);
		}
		usingConnectionCounter++;
	}
	
	private void disconnect() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "disconnect");
		};
		usingConnectionCounter--;
		if(usingConnectionCounter == 0)
			connection.disconnect();		
	}
	
	private Packet createPacketFromXml(final String xml) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "createPacketFromXml xml: " + xml);
		};
		return new Packet() {
			@Override
			public String toXML() {
				return xml;
			}				
		};
	}
	
	private boolean isDiscoItem(IQ iq) throws SAXException, IOException, ParserConfigurationException {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "isDiscoItem");
		};
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Element element = factory.newDocumentBuilder().parse(new InputSource(new StringReader(iq.toXML()))).getDocumentElement();
		if(element.getChildNodes().getLength() == 1) {
			Node query = element.getChildNodes().item(0);
			return query.getNodeName().equals("query") && query.lookupNamespaceURI(query.getPrefix()).equals(XMPPNode.ITEM_NAMESPACE);
		}
		else {
			return false;
		}
	}
	
	private static class NamespaceFilter implements PacketFilter {
		
		private List<String> namespaces;
		
		public NamespaceFilter(String[] namespaces) {
			this.namespaces = Arrays.asList(namespaces);
		}

		public boolean accept(Packet packet) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				Element element = factory.newDocumentBuilder().parse(new InputSource(new StringReader(packet.toXML()))).getDocumentElement();
				
				NodeList childs = element.getChildNodes();
				for(int i=0; i<childs.getLength(); i++) {
					Node child = childs.item(i);
					if(child instanceof Element) {
						Element childElem = (Element)child;						
						String namespace = childElem.lookupNamespaceURI(childElem.getPrefix());
						if(!namespace.equals("jabber:client") && !namespace.equals("jabber:server") && namespaces.contains(namespace))
							return true;
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
			return false;
		}
	}
	
	private void createAccount(Connection connection, String username, String password) throws XMPPException {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "createAccount user: " + username + " password: " + password);
		};
		
		AccountManager accountMgr = connection.getAccountManager();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", username);
		attributes.put("password", password);
		accountMgr.createAccount(username, password, attributes);
	}
	
	private String username(String identifier, String domain) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "username identifier: " + identifier + " domain: " + domain);
		};
		return identifier + "@" + domain;
	}
	
	/**
	 * Create XMPP Configuration object
	 * 
	 * @param server DNS name of XMPP server
	 * @param username 
	 * @param password
	 * @param host IP address of XMPP server (optional). If used, the DNS lookup to resolve the server is overridden.
	 */
	private void loadConfig(String server, String username, String password, String host) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "loadConfig server: " + server + " username: " + username + " password: " + password + " host: " + host);
		};
		
		this.username = username;
		this.password = password;
		String xmppHost = server;
		if (null != host) {
			xmppHost = host;
		}
		ConnectionConfiguration config = new ConnectionConfiguration(xmppHost, port, server);

		connection = new XMPPConnection(config);
		
		if(debug) {
			connection.addPacketListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					if (DEBUG_LOGGING) {
						Log.d(LOG_TAG, "Packet received: " + packet.toXML());
					};
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
			});
			connection.addPacketSendingListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					if (DEBUG_LOGGING) {
						Log.d(LOG_TAG, "Packet sent: " + packet.toXML());
					};
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
			});
		}
	}

	private boolean isConnectedInternal() {
		boolean retValue = false;
		
		if (null != connection) {
			retValue = connection.isConnected();
		}
		return retValue;
	}
	
	private boolean UnRegisterCommManagerInternal() {
		Set<ProviderElementNamespaceRegistrar.ElementNamespaceTuple> tuples = providerRegistrar.getRegists();
		for(ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple:tuples) {
			removeProviders(tuple);
		}
		providerRegistrar.clear();
		
		return true;
	}
	
	private boolean logoutInternal() {
		UnRegisterCommManagerInternal();		
		connection.disconnect();
		usingConnectionCounter = 0;
		
		return true;
	}
	
	/**
	 * Societies enabled aSmack Packet Listener 
	 *
	 */
	private class RegisterPacketListener implements PacketListener {
		String client;
		long remoteCallId;
		
		public RegisterPacketListener(String client, long remoteCallId) {
			this.client = client;
			this.remoteCallId = remoteCallId;
		}
		public void processPacket(Packet packet) {
			//Send intent
			Intent intent = new Intent(PUBSUB_EVENT);
			//TODO: Extra parameter required for eventual client component that 
			//wants to receive this intent
//			if (AndroidCommsBase.this.restrictBroadcast) {
//				intent.setPackage(this.client);
//			}
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, packet.toXML());
			intent.putExtra(INTENT_RETURN_CALL_ID_KEY, PUBSUB_EVENT_CALBACK_ID);
			AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
			if (DEBUG_LOGGING) {
				Log.d(LOG_TAG, "Pubsub node intent sent: " + packet.toXML());
			};
		}			
	}
	
	private class SendIQPacketListener implements PacketListener {
		String client;
		long remoteCallId;
		
		public SendIQPacketListener(String client, long remoteCallId) {
			this.client = client;
			this.remoteCallId = remoteCallId;
		}

		public void processPacket(Packet packet) {
			IQ iq = (IQ)packet;
			connection.removePacketListener(this);
			disconnect();
			
			if(iq.getType() == IQ.Type.RESULT) {
				//Send intent
				Intent intent = new Intent(SEND_IQ_RESULT);
				if (AndroidCommsBase.this.restrictBroadcast) {
					intent.setPackage(this.client);
				}
				intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, packet.toXML());
				intent.putExtra(INTENT_RETURN_CALL_ID_KEY, this.remoteCallId);
				AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
				
			} else if(iq.getType() == IQ.Type.ERROR) {
				//Send intent
				Intent intent = new Intent(SEND_IQ_ERROR);
				if (AndroidCommsBase.this.restrictBroadcast) {
					intent.setPackage(this.client);
				}
				intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, packet.toXML());
				intent.putExtra(INTENT_RETURN_CALL_ID_KEY, this.remoteCallId);
				AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
				
				createNotification("Error invoking remote method: " + packet.toXML(), COMMS_CANNOT_SEND_IQ, NOTIFICATION_TITLE);
			}
		}	
	}

	private class GetItemsPacketListener implements PacketListener {
		String client;
		long remoteCallId;

		public GetItemsPacketListener(String client, long remoteCallId) {
			this.client = client;
			this.remoteCallId = remoteCallId;
		}
		
		public void processPacket(Packet packet) {
			IQ iq = (IQ)packet;
			connection.removePacketListener(this);
			disconnect();
			try {
				if(iq.getType() == IQ.Type.RESULT) {
					if(isDiscoItem(iq)) {
						//Send intent
						Intent intent = new Intent(GET_ITEMS_RESULT);
						if (AndroidCommsBase.this.restrictBroadcast) {
							intent.setPackage(this.client);
						}
						intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, packet.toXML());
						intent.putExtra(INTENT_RETURN_CALL_ID_KEY, this.remoteCallId);
						AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
					}
				}
				else if(iq.getType() == IQ.Type.ERROR) {
					//Send intent
					Intent intent = new Intent(GET_ITEMS_ERROR);
					if (AndroidCommsBase.this.restrictBroadcast) {
						intent.setPackage(this.client);
					}
					intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, packet.toXML());
					intent.putExtra(INTENT_RETURN_CALL_ID_KEY, this.remoteCallId);
					AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				
				//Send intent
				Intent intent = new Intent(GET_ITEMS_EXCEPTION);
				if (AndroidCommsBase.this.restrictBroadcast) {
					intent.setPackage(client);
				}
				intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, "");
				intent.putExtra(INTENT_RETURN_EXCEPTION_KEY, e.getMessage());
				intent.putExtra(INTENT_RETURN_EXCEPTION_TRACE_KEY, getStackTraceArray(e));
				intent.putExtra(INTENT_RETURN_CALL_ID_KEY, remoteCallId);
				AndroidCommsBase.this.serviceContext.sendBroadcast(intent);
			}
		}				
	}

	/**
	 * Create a String array of the exception trace
	 * 
	 * @param e
	 * @return String array
	 */
	private static String[] getStackTraceArray(Exception e){
		  StackTraceElement[] stackTraceElements = e.getStackTrace();
		  
		  String[] stackTracelines = new String[stackTraceElements.length];
		  int i =0;
		  for(StackTraceElement se : stackTraceElements){
		    stackTracelines[i++] = se.toString();
		  }
		  return stackTracelines;
		}

	/**
	 * Determine if an array of namespace elements relate to XMPP Pubsub
	 * 
	 * @param namespaces
	 * @return
	 */
	private static boolean isNameSpacePubsub(String [] namespaces) {
		boolean retValue = false;
		
		for (String element : namespaces) {
			if (element.contains(PUBSUB_NAMESPACE_KEY)) {
				retValue = true;
				break;
			}
		}
		
		return retValue;
	}
	
	/**
	 * Create Android Notification
	 * @param message 
	 * @param title 
	 * @param type (not dislpayed)
	 */
	private void createNotification(String message, String title, String type) {
		int notifierflags [] = new int [1];
		notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
		AndroidNotifier notifier = new AndroidNotifier(AndroidCommsBase.this.serviceContext,
														Notification.DEFAULT_SOUND, notifierflags);

		notifier.notifyMessage(message, type, this.getClass(), title);
	}
	
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
		if (DEBUG_LOGGING) {
	        Log.d(LOG_TAG, "Set up connectivity changes broadcast receiver");
		};
        
        this.receiver = new AndroidCommsReceiver();
        this.serviceContext.registerReceiver(this.receiver, createIntentFilter());    
		if (DEBUG_LOGGING) {
	        Log.d(LOG_TAG, "Register broadcast receiver");
		};

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
		if (DEBUG_LOGGING) {
		       Log.d(LOG_TAG, "Tear down broadcast receiver");
		};
    	this.serviceContext.unregisterReceiver(this.receiver);
    }

	
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return intentFilter;
    }

    /**
     * Broadcast receiver to receive intent return values from ConnectionManager
     * 
     * TODO: If base API increases extra notification information can be displayed
     * in the more detailed notification style
     */
    private class AndroidCommsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

				boolean unConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

				if (unConnected) {
					ConnectivityManager cm =
					        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
					 
					NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
					
					String networkType = null;
					
					if (null != activeNetwork) {
						networkType = activeNetwork.getTypeName();
					}
					String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
					String extraInfo = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
					boolean failover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

					createNotification("Device has lost connectivity", COMMS_NO_CONNECTIVITY, NOTIFICATION_TITLE);
				}
			}
		}
    }

}
