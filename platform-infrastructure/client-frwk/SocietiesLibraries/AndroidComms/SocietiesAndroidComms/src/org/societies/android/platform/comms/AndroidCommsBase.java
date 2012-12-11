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
import org.societies.android.api.comms.Callback;
import org.societies.android.api.comms.XMPPAgent;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.utilities.DBC.Dbc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AndroidCommsBase implements XMPPAgent {
	private static final String LOG_TAG = AndroidCommsBase.class.getName();
	
	private XMPPConnection connection;
	private String username, password, resource;
	private int usingConnectionCounter = 0;
	private ProviderElementNamespaceRegistrar providerRegistrar = new ProviderElementNamespaceRegistrar();
	private RawXmlProvider rawXmlProvider = new RawXmlProvider();
	private String domainAuthorityNode;
	int port;
	boolean debug;
	boolean restrictBroadcast;
	Context serviceContext;
	
	public AndroidCommsBase(Context serviceContext, boolean restrictBroadcast) {
		Log.d(LOG_TAG, "Service Base Object created");
		this.restrictBroadcast = restrictBroadcast;
		this.serviceContext = serviceContext;
	}
	
	public void register(String client, String[] elementNames, String[] namespaces, final Callback callback) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Message Beans must be specified", null != elementNames && elementNames.length > 0);
		Dbc.require("Namespaces must be specified", null != namespaces && namespaces.length > 0);
		Dbc.require("Callback objectmust be specified", null != callback);
		
		Log.d(LOG_TAG, "register");
//		for (String element : elementNames) {
//			Log.d(LOG_TAG, "register element name: " + element);
//		}
//		for (String namespace : namespaces) {
//			Log.d(LOG_TAG, "register namespace: " + namespace);
//		}
		
		for(int i=0; i<elementNames.length; i++) {
			for(int j=0; j<namespaces.length; j++) {
				providerRegistrar.register(new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[j]));				
				ProviderManager.getInstance().addIQProvider(elementNames[i], namespaces[j], rawXmlProvider);
				ProviderManager.getInstance().addExtensionProvider(elementNames[i], namespaces[j], rawXmlProvider);
			}
		}
		
		try {
			connect();
			
			connection.addPacketListener( new PacketListener() { // TODO remove packet listener on unregister
				public void processPacket(Packet packet) {
					callback.receiveMessage(packet.toXML());
				}			
			}, new AndFilter(new PacketTypeFilter(Message.class), new NamespaceFilter(namespaces)));
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
	}
	
	public void unregister(String client, String[] elementNames, String[] namespaces) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Message Beans must be specified", null != elementNames && elementNames.length > 0);
		Dbc.require("Namespaces must be specified", null != namespaces && namespaces.length > 0);

		Log.d(LOG_TAG, "unregister");
//		for (String element : elementNames) {
//			Log.d(LOG_TAG, "unregister element name: " + element);
//		}
//		for (String namespace : namespaces) {
//			Log.d(LOG_TAG, "unregister namespace: " + namespace);
//		}
		
		for(int i=0; i<elementNames.length; i++) {
			for(int j=0; j<namespaces.length; j++) {
				ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple = new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[j]);		
				providerRegistrar.unregister(tuple);
				if(!providerRegistrar.isRegistered(tuple)) { 
					removeProviders(tuple);
				}
			}
		}
		
		disconnect();
	}
	
	public boolean UnRegisterCommManager(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "UnRegisterCommManager");
		
		boolean retValue = false;
		
		//Send intent
		Intent intent = new Intent(UN_REGISTER_COMM_MANAGER);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		try {
			retValue = UnRegisterCommManagerInternal();
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}

		return false;
	}
	
	public void sendMessage(String client, String messageXml) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("XML must be specified", null != messageXml && messageXml.length() > 0);
		
		Packet formattedMessage = createPacketFromXml(messageXml);
		
		Log.d(LOG_TAG, "sendMessage xml message size: " + formattedMessage.toXML().length());
		Log.d(LOG_TAG, "sendMessage xml message:" + createPacketFromXml(messageXml).toXML());
		
		try {
			connect();	
			
			connection.sendPacket(formattedMessage);
		
			disconnect();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage(), e);
		}
	}	

	public void sendIQ(String client, String xml, final Callback callback) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("XML must be specified", null != xml && xml.length() > 0);
		Dbc.require("Callback objectmust be specified", null != callback);
//		Log.d(LOG_TAG, "sendIQ xml: " + xml);

		try {
			connect(); 
			
			PacketListener packetListener = new PacketListener() {
				public void processPacket(Packet packet) {
					IQ iq = (IQ)packet;
					connection.removePacketListener(this);
					disconnect();
					if(iq.getType() == IQ.Type.RESULT) {
						callback.receiveResult(packet.toXML());
					}
					else if(iq.getType() == IQ.Type.ERROR) {
						callback.receiveError(packet.toXML());
					}
				}				
			};
			
			String id = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement().getAttribute("id");
									
			if(id.equals(""))
				throw new NullPointerException("IQ XML has no ID attribute!");
			
			connection.addPacketListener(packetListener, new AndFilter(new PacketTypeFilter(IQ.class),new PacketIDFilter(id))); 
			
			connection.sendPacket(createPacketFromXml(xml));		
			
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
	}
	
	public String getIdentity(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getIdentity");
		
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
			Log.d(LOG_TAG, "getIdentity identity: " + retValue);
			
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}
		return null;
	}
	
	public String getDomainAuthorityNode(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getDomainAuthorityNode");
		
		//Send intent
		Intent intent = new Intent(GET_DOMAIN_AUTHORITY_NODE);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, this.domainAuthorityNode);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		this.serviceContext.sendBroadcast(intent);

		return null;
	}
	
	public String getItems(String client, String entity, String node, final Callback callback) throws CommunicationException {		
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Node must be specified", null != node && node.length() > 0);
		Dbc.require("Callback objectmust be specified", null != callback);
		Log.d(LOG_TAG, "getItems entity: " + entity);
		
		//Send intent
		Intent intent = new Intent(GET_ITEMS);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}


		String retValue = null;
		
		try {
			connect();
			
			DiscoverItems discoItems = new DiscoverItems();
			discoItems.setTo(entity);
			discoItems.setNode(node);		

			PacketListener packetListener = new PacketListener() {
				public void processPacket(Packet packet) {
					IQ iq = (IQ)packet;
					connection.removePacketListener(this);
					disconnect();
					try {
						if(iq.getType() == IQ.Type.RESULT) {
							if(isDiscoItem(iq))
								callback.receiveItems(packet.toXML());
							else
								callback.receiveResult(packet.toXML());
						}
						else if(iq.getType() == IQ.Type.ERROR) {
							callback.receiveError(packet.toXML());
						}
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage(), e);
					}
				}				
			};
			
			connection.addPacketListener(packetListener, new AndFilter(new PacketTypeFilter(IQ.class),new PacketIDFilter(discoItems.getPacketID()))); 
			
			connection.sendPacket(discoItems);
			
			retValue = discoItems.getPacketID();

		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}
		
		return null;
	}
	

	public Boolean isConnected(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "isConnected ");
		
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
		
		this.serviceContext.sendBroadcast(intent);
		return retValue;
	}
	
	public String newMainIdentity(String client, String identifier, String domain, String password) { // TODO this takes no credentials in a private/public key case
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Identfier must be specified", null != identifier && identifier.length() > 0);
		Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
		Dbc.require("Password must be specified", null != password && password.length() > 0);
		Log.d(LOG_TAG, "newMainIdentity identity: " + identifier + " domain: " + domain + " password: " + password);

		String retValue = null;
		//Send intent
		Intent intent = new Intent(NEW_MAIN_IDENTITY);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}


		
		String serverHost = domain;
//		int port = defaultConfig.getPort();
		String serviceName = domain;
		
		try {
			if(null != connection && 
					connection.isConnected() && 
					connection.getHost().equals(serverHost) && 
					connection.getPort()==port && 
					connection.getServiceName().equals(serviceName)) {
				
				createAccount(connection, identifier, password);
				retValue = username(identifier, domain) + "/" + resource;
				Log.d(LOG_TAG, "Created user JID: " + retValue);
			}
			else {
				ConnectionConfiguration config = new ConnectionConfiguration(domain, port, domain);
				Connection newIdConnection = new XMPPConnection(config);			
				newIdConnection.connect();
				
				createAccount(newIdConnection, identifier, password);
				
				newIdConnection.disconnect();
				retValue = username(identifier, domain) + "/" + resource;
				Log.d(LOG_TAG, "Created user JID: " + retValue);
			}
			
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			//Send intent
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}
		
		return null;
	}
	
	public String login(String client, String identifier, String domain, String password) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Identfier must be specified", null != identifier && identifier.length() > 0);
		Dbc.require("Domain must be specified", null != domain && domain.length() > 0);
		Dbc.require("Password must be specified", null != password && password.length() > 0);
		Log.d(LOG_TAG, "login identifier: " + identifier + " domain: " + domain + " password: " + password);
		
		String retValue = null;
		Intent intent = new Intent(LOGIN);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		
		if(isConnectedInternal()) {
			logoutInternal();
		}
		String username = username(identifier, domain);
		loadConfig(domain, username, password);
		try {
			connect();
			retValue = username + "/" + resource;
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			//Send intent
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}
		return null;
	}	
	
//	public String loginFromConfig(String client) {
//		Dbc.require("Client must be specified", null != client && client.length() > 0);
//		Log.d(LOG_TAG, "loginFromConfig");
//		
//		if(isConnected(client))
//			logout(client);
////		loadDefaultConfig();
//		try {
//			connect();
//			return username + "/" + resource;
//		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
//			return null;
//		}
//	}
	
	public boolean logout(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "logout");
		
		boolean retValue = false;
		Intent intent = new Intent(LOGOUT);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		
		try {
			retValue = logoutInternal();
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, retValue);
			this.serviceContext.sendBroadcast(intent);
		}

		return false;
	}
	
	public boolean destroyMainIdentity(String client) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Log.d(LOG_TAG, "destroyMainIdentity");
		
		//Send intent
		Intent intent = new Intent(DESTROY_MAIN_IDENTITY);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, false);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

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
	
	
	
	public void setDomainAuthorityNode(String client, String domainAuthorityNode) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Domain Authority Node must be specified", null != domainAuthorityNode && domainAuthorityNode.length() > 0);
		this.domainAuthorityNode = domainAuthorityNode;
	}
	
	public void setPortNumber(String client, int port) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Port must be positive", port > 0);
		
		this.port = port;
	}
	
	public void setResource(String client, String resource) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Resource must be specified", null != resource && resource.length() > 0);
		
		this.resource = resource;		
	}
	
	public void setDebug(String client, boolean enabled) {
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		
		this.debug = enabled;		
	}
	
	@Override
	public boolean configureAgent(String client, String xmppDomainAuthorityNode,
		int xmppPort, String xmppResource, boolean xmppDebug) {
		
		Dbc.require("Client must be specified", null != client && client.length() > 0);
		Dbc.require("Domain Authority Node must be specified", null != xmppDomainAuthorityNode && xmppDomainAuthorityNode.length() > 0);
		Dbc.require("Port must be positive", xmppPort > 0);
		Dbc.require("Resource must be specified", null != xmppResource && xmppResource.length() > 0);
		Log.d(LOG_TAG, "configureAgent");
		
		this.setDomainAuthorityNode(client, xmppDomainAuthorityNode);
		this.setPortNumber(client, xmppPort);
		this.setResource(client, xmppResource);
		this.setDebug(client, xmppDebug);
		
		//Send intent
		Intent intent = new Intent(CONFIGURE_AGENT);
		intent.putExtra(XMPPAgent.INTENT_RETURN_VALUE_KEY, true);
		if (this.restrictBroadcast) {
			intent.setPackage(client);
		}

		this.serviceContext.sendBroadcast(intent);

		
		return false;
	}

	private void removeProviders(ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple) {
		Log.d(LOG_TAG, "removeProviders");
		ProviderManager.getInstance().removeIQProvider(tuple.elementName, tuple.namespace);
		ProviderManager.getInstance().removeExtensionProvider(tuple.elementName, tuple.namespace);
	}
	
	private void connect() throws XMPPException {		
		Log.d(LOG_TAG, "connect");
		if(!connection.isConnected()) {
			connection.connect();
			connection.login(username, password, resource);
		}
		usingConnectionCounter++;
	}
	
	private void disconnect() {
		Log.d(LOG_TAG, "disconnect");
		usingConnectionCounter--;
		if(usingConnectionCounter == 0)
			connection.disconnect();		
	}
	
	private Packet createPacketFromXml(final String xml) {
//		Log.d(LOG_TAG, "createPacketFromXml xml: " + xml);
		return new Packet() {
			@Override
			public String toXML() {
				return xml;
			}				
		};
	}
	
	private boolean isDiscoItem(IQ iq) throws SAXException, IOException, ParserConfigurationException {
		Log.d(LOG_TAG, "isDiscoItem");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Element element = factory.newDocumentBuilder().parse(new InputSource(new StringReader(iq.toXML()))).getDocumentElement();
		if(element.getChildNodes().getLength() == 1) {
			Node query = element.getChildNodes().item(0);
			return query.getNodeName().equals("query") && query.lookupNamespaceURI(query.getPrefix()).equals(XMPPNode.ITEM_NAMESPACE);
		}
		else
			return false;
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
		Log.d(LOG_TAG, "createAccount user: " + username + " password: " + password);
		
		AccountManager accountMgr = connection.getAccountManager();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", username);
		attributes.put("password", password);
		accountMgr.createAccount(username, password, attributes);
	}
	
	private String username(String identifier, String domain) {
		Log.d(LOG_TAG, "username identifier: " + identifier + " domain: " + domain);
		return identifier + "@" + domain;
	}
	
	private void loadConfig(String server, String username, String password) {
		Log.d(LOG_TAG, "loadConfig server: " + server + " username: " + username + " password: " + password);
		
		this.username = username;
		this.password = password;
		
		ConnectionConfiguration config = new ConnectionConfiguration(server, port, server);

		connection = new XMPPConnection(config);
		
		if(debug) {
			connection.addPacketListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
//					Log.d(LOG_TAG, "Packet received: " + packet.toXML());
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
				
			});
			connection.addPacketSendingListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
//					Log.d(LOG_TAG, "Packet sent: " + packet.toXML());
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
}
