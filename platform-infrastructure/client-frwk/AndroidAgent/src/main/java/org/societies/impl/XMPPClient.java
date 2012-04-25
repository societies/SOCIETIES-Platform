package org.societies.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.interfaces.Callback;
import org.societies.interfaces.XMPPAgent;
import org.societies.utilities.DBC.Dbc;
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
	
	public XMPPClient(ResourceBundle configutationBundle) {
		
		String server = configutationBundle.getString("server");
		int port = Integer.parseInt(configutationBundle.getString("port"));
		username = configutationBundle.getString("username");
		password = configutationBundle.getString("password");
		resource = configutationBundle.getString("resource");
		boolean debug;
		try {
			debug = configutationBundle.getString("debug").equalsIgnoreCase("true");
		} catch(MissingResourceException e) {
			debug = false;
		}
		
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
	
	public void register(String[] elementNames, String[] namespaces, final Callback callback) {
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
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void unregister(String[] elementNames, String[] namespaces) {
		ProviderManager pmgr = ProviderManager.getInstance();	
		
		for(int i=0; i<elementNames.length; i++) {
			for(int j=0; j<namespaces.length; j++) {
				ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple = new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[j]);		
				providerRegistrar.unregister(tuple);
				if(!providerRegistrar.isRegistered(tuple)) { 
					ProviderManager.getInstance().removeIQProvider(tuple.elementName, tuple.namespace);
					ProviderManager.getInstance().removeExtensionProvider(tuple.elementName, tuple.namespace);
				}
			}
		}
		
		disconnect();
	}
	
	@Override
	public void sendMessage(String messageXml) {
		try {
			connect();	
			
			connection.sendPacket(createPacketFromXml(messageXml));
		
			disconnect();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	

	@Override
	public void sendIQ(String xml, final Callback callback) {
		try {
			connect(); 
			
			PacketListener packetListener = new PacketListener() {
				@Override
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
			throw new RuntimeException(e);
		}
	}
	
	public String getIdentity() {
		try {
			connect();			
			String identity = connection.getUser();			
			disconnect();			
			return identity;
		} catch (XMPPException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public String getItems(String entity, String node, final Callback callback) throws CommunicationException {		
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
			throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	private void connect() throws XMPPException {		
		if(!connection.isConnected()) {
			connection.connect();
			connection.login(username, password, resource);
		}
		usingConnectionCounter++;
	}
	
	private void disconnect() {
		usingConnectionCounter--;
		if(usingConnectionCounter == 0)
			connection.disconnect();		
	}
	
	private Packet createPacketFromXml(final String xml) {
		return new Packet() {
			@Override
			public String toXML() {
				return xml;
			}				
		};
	}
	
	private boolean isDiscoItem(IQ iq) throws SAXException, IOException, ParserConfigurationException {
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
		return connection.isConnected();
	}
}
