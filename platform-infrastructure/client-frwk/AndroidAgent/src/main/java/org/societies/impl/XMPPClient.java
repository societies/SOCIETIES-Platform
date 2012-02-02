package org.societies.impl;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.interfaces.Callback;
import org.societies.interfaces.XMPPAgent;
import org.societies.utilities.DBC.Dbc;
import org.xml.sax.InputSource;

public class XMPPClient implements XMPPAgent {

	private static final Logger log = LoggerFactory.getLogger(XMPPClient.class);
	
	private XMPPConnection connection;
	private String username, password, resource;
	private int usingConnectionCounter = 0;
	private ProviderElementNamespaceRegistrar providerRegistrar = new ProviderElementNamespaceRegistrar();
	private IQProvider iqProvider = new RawXmlProvider();
	
	public XMPPClient() {
		// TODO configurations
		String server = "host";
		int port = 5222;
		username = "user@" + server; 
		password = "pass";
		resource = "default";

		ConnectionConfiguration config = new ConnectionConfiguration(server, port, server);

		connection = new XMPPConnection(config);
		
		// TODO remove debug
//		connection.addPacketListener(new PacketListener() {
//
//			public void processPacket(Packet packet) {
//				log.debug("Packet received: " + packet.toXML());
//			}
//			
//		}, new PacketFilter() {
//
//			public boolean accept(Packet packet) {
//				return true;
//			}
//			
//		});
//		connection.addPacketSendingListener(new PacketListener() {
//
//			public void processPacket(Packet packet) {
//				log.debug("Packet sent: " + packet.toXML());
//			}
//			
//		}, new PacketFilter() {
//
//			public boolean accept(Packet packet) {
//				return true;
//			}
//			
//		});
	}
	
	public void register(String[] elementNames, String[] namespaces) {
		Dbc.require("Both args must have the same size.", elementNames.length == namespaces.length);
		
		for(int i=0; i<elementNames.length; i++) {
			providerRegistrar.register(new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[i]));			
			ProviderManager.getInstance().addIQProvider(elementNames[i], namespaces[i], iqProvider);
		}
		
	}
	
	@Override
	public void unregister(String[] elementNames, String[] namespaces) {
		Dbc.require("Both lists must have the same size.", elementNames.length == namespaces.length);
		
		ProviderManager pmgr = ProviderManager.getInstance();	
		
		for(int i=0; i<elementNames.length; i++) {
			ProviderElementNamespaceRegistrar.ElementNamespaceTuple tuple = new ProviderElementNamespaceRegistrar.ElementNamespaceTuple(elementNames[i], namespaces[i]);		
			providerRegistrar.unregister(tuple);
			if(!providerRegistrar.isRegistered(tuple)) 
				ProviderManager.getInstance().removeIQProvider(tuple.elementName, tuple.namespace);
			
		}
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
					if(packet instanceof IQ) {
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
				}				
			};
			
			String id = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement().getAttribute("id");
									
			if(id.equals(""))
				throw new NullPointerException("IQ XML has no ID attribute!");
			
			connection.addPacketListener(packetListener, new PacketIDFilter(id)); 
			
			connection.sendPacket(createPacketFromXml(xml));		
			
		} catch (Exception e) {
			throw new RuntimeException(e);
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

}
