package org.societies.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.interfaces.Callback;
import org.societies.interfaces.XMPPAgent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;


public class XMPPClient implements XMPPAgent {

	private static final String LOG_TAG = XMPPClient.class.getName();
	
	private XMPPConnection connection;
	private String username, password, resource;
	private int usingConnectionCounter = 0;
	private ProviderElementNamespaceRegistrar providerRegistrar = new ProviderElementNamespaceRegistrar();
	private RawXmlProvider rawXmlProvider = new RawXmlProvider();
	private Configuration defaultConfig;
	
	public XMPPClient(ResourceBundle configutationBundle) {
		
		defaultConfig = new Configuration(configutationBundle);
		
		loadDefaultConfig();
	}
	
	public void register(String[] elementNames, String[] namespaces, final Callback callback) {
		Log.d(LOG_TAG, "register");
		for (String element : elementNames) {
			Log.d(LOG_TAG, "register element name: " + element);
		}
		for (String namespace : namespaces) {
			Log.d(LOG_TAG, "register namespace: " + namespace);
		}
		
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
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void unregister(String[] elementNames, String[] namespaces) {
		Log.d(LOG_TAG, "unregister");
		for (String element : elementNames) {
			Log.d(LOG_TAG, "unregister element name: " + element);
		}
		for (String namespace : namespaces) {
			Log.d(LOG_TAG, "unregister namespace: " + namespace);
		}
		
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
	
	public boolean UnRegisterCommManager() {
		Log.d(LOG_TAG, "UnRegisterCommManager");
		Set<ProviderElementNamespaceRegistrar.ElementNamespaceTuple> tuples = providerRegistrar.getRegists();
		for(ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple:tuples) {
			removeProviders(tuple);
		}
		providerRegistrar.clear();
		return true;
	}
	
	private void removeProviders(ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple) {
		Log.d(LOG_TAG, "removeProviders");
		ProviderManager.getInstance().removeIQProvider(tuple.elementName, tuple.namespace);
		ProviderManager.getInstance().removeExtensionProvider(tuple.elementName, tuple.namespace);
	}
	
	public void sendMessage(String messageXml) {
		Log.d(LOG_TAG, "sendMessage xml: " + messageXml);
		try {
			connect();	
			
			connection.sendPacket(createPacketFromXml(messageXml));
		
			disconnect();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}	

	public void sendIQ(String xml, final Callback callback) {
		Log.d(LOG_TAG, "sendIQ xml: " + xml);
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
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String getIdentity() {
		Log.d(LOG_TAG, "getIdentity");
		try {
			connect();			
			String identity = connection.getUser();			
			disconnect();			
			Log.d(LOG_TAG, "getIdentity identity: " + identity);
			return identity;
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String getDomainAuthorityNode() {
		Log.d(LOG_TAG, "getDomainAuthorityNode");
		return defaultConfig.getDomainAuthorityNode();
	}
	
	public String getItems(String entity, String node, final Callback callback) throws CommunicationException {		
		Log.d(LOG_TAG, "getItems entity: " + entity);
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
			
			return discoItems.getPacketID();
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new CommunicationException(e.getMessage());
		}
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
		Log.d(LOG_TAG, "createPacketFromXml xml: " + xml);
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

	public Boolean isConnected() {
		Log.d(LOG_TAG, "isConnected + connected: " + connection.isConnected() );
		return connection.isConnected();
	}
	
	public String newMainIdentity(String identifier, String domain, String password) throws CommunicationException { // TODO this takes no credentials in a private/public key case
		Log.d(LOG_TAG, "newMainIdentity identity: " + identifier + " domain: " + domain + " password: " + password);

		String serverHost = domain;
		int port = defaultConfig.getPort();
		String serviceName = domain;
		
		try {
			if(connection.isConnected() && connection.getHost().equals(serverHost) && connection.getPort()==port && connection.getServiceName().equals(serviceName)) {
				createAccount(connection, identifier, password);
			}
			else {
				ConnectionConfiguration config = new ConnectionConfiguration(domain, port, domain);
				Connection newIdConnection = new XMPPConnection(config);			
				newIdConnection.connect();
				createAccount(newIdConnection, identifier, password);
				newIdConnection.disconnect();
			}
		} catch (XMPPException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new CommunicationException(e.getMessage());
		}		
		
		return username(identifier, domain) + "/" + resource;
	}
	
	private void createAccount(Connection connection, String username, String password) throws XMPPException {
		Log.d(LOG_TAG, "createAccount user: " + username + " password: " + password);
		
		AccountManager accountMgr = connection.getAccountManager();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", username);
		attributes.put("password", password);
		accountMgr.createAccount(username, password, attributes);
	}
	
	public String login(String identifier, String domain, String password) {
		Log.d(LOG_TAG, "login identifier: " + identifier + " domain: " + domain + " password: " + password);
		
		if(isConnected())
			logout();
		String username = username(identifier, domain);
		loadConfig(domain, username, password);
		try {
			connect();
			return username + "/" + resource;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			return null;
		}
	}	
	
	public String loginFromConfig() {
		Log.d(LOG_TAG, "loginFromConfig");
		if(isConnected())
			logout();
		loadDefaultConfig();
		try {
			connect();
			return username + "/" + resource;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			return null;
		}
	}
	
	private String username(String identifier, String domain) {
		Log.d(LOG_TAG, "username identifier: " + identifier + " domain: " + domain);
		return identifier + "@" + domain;
	}
	
	public boolean logout() {
		Log.d(LOG_TAG, "logout");
		UnRegisterCommManager();		
		connection.disconnect();
		usingConnectionCounter = 0;
		return true;
	}
	
	public boolean destroyMainIdentity() {
		Log.d(LOG_TAG, "destroyMainIdentity");
		return false; // http://code.google.com/p/asmack/issues/detail?id=63
//		try {
//			connection.getAccountManager().deleteAccount();
//			return true;			
//		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
//			return false;
//		}
	}
	
	private void loadDefaultConfig() {		
		Log.d(LOG_TAG, "loadDefaultConfig");
		loadConfig(defaultConfig.getServer(), defaultConfig.getUsername(), defaultConfig.getPassword());
	}
	
	private void loadConfig(String server, String username, String password) {
		Log.d(LOG_TAG, "loadConfig server: " + server + " username: " + username + " password: " + password);
		
		int port = defaultConfig.getPort();
		this.username = username;
		this.password = password;
		resource = defaultConfig.getResource();
		boolean debug = defaultConfig.getDebug();
		
		ConnectionConfiguration config = new ConnectionConfiguration(server, port, server);

		connection = new XMPPConnection(config);
		
		if(debug) {
			connection.addPacketListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					Log.d(LOG_TAG, "Packet received: " + packet.toXML());
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
				
			});
			connection.addPacketSendingListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					Log.d(LOG_TAG, "Packet sent: " + packet.toXML());
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
				
			});
		}
	}
}
