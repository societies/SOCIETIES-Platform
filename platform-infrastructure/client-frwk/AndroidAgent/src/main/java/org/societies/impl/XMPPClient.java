package org.societies.impl;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.interfaces.Callback;
import org.societies.interfaces.XMPPAgent;

public class XMPPClient implements XMPPAgent {

	private static final Logger log = LoggerFactory.getLogger(XMPPClient.class);
	
	private XMPPConnection connection;
	
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
	public void sendIQ(String id, String xml, final Callback callback) {
		try {
			connect(); // TODO close connection
			
			PacketListener packetListener = new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					log.debug("**************************************************");
					log.debug("packetReceived: "+packet.toXML());
					if(packet instanceof IQ) {
						IQ iq = (IQ)packet;
						if(iq.getType() == IQ.Type.RESULT)
							callback.receiveResult(packet.toXML());
						else if(iq.getType() == IQ.Type.ERROR)
							callback.receiveError(packet.toXML());
					}
				}				
			};
			
			connection.addPacketListener(packetListener, new PacketIDFilter(id)); 
			
			connection.sendPacket(createPacketFromXml(xml));
		
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void connect() throws XMPPException {
		// TODO
		String server = "c3s.av.it.pt";
		int port = 5222;
		String username = "android@c3s.av.it.pt";
		String password = "android";
		String resource = "default";

		ConnectionConfiguration config = new ConnectionConfiguration(server, port, server);

		connection = new XMPPConnection(config);
		connection.connect();
		connection.login(username, password, resource);
		
	}
	
	private void disconnect() {
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
