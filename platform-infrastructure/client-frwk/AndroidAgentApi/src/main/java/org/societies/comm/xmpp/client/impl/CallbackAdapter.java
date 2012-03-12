package org.societies.comm.xmpp.client.impl;

import java.io.IOException;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import javax.xml.parsers.ParserConfigurationException;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.societies.interfaces.Callback;
import org.xml.sax.SAXException;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

public class CallbackAdapter implements Callback {
	
	private static final String LOG_TAG = CallbackAdapter.class.getName();
	
	private ICommCallback callback;
	private Context context;
	private ServiceConnection service;
	private PacketMarshaller marshaller;
	
	public CallbackAdapter(ICommCallback callback, Context context, ServiceConnection service, PacketMarshaller marshaller) {
		this.callback = callback;
		this.context = context;
		this.service = service;
		this.marshaller = marshaller;
	}
	
	@Override
	public void receiveResult(String xml) {
		unbindService();
		
		try {
			Packet packet = marshaller.unmarshallIq(xml);
			Object payload = marshaller.unmarshallPayload(packet);
			callback.receiveResult(stanzaFromPacket(packet), payload);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} 

	}
	@Override
	public void receiveError(String xml) {
		unbindService();
		
		try {
			Packet packet = marshaller.unmarshallIq(xml);
			callback.receiveError(stanzaFromPacket(packet), null); // TODO parse error
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} 
	}
	
	public void receiveItems(String xml) {
		unbindService();
		
		try {
			Packet packet = marshaller.unmarshallIq(xml);
			SimpleEntry<String, List<String>> nodeMap = marshaller.parseItemsResult(packet);
			callback.receiveItems(stanzaFromPacket(packet), nodeMap.getKey(), nodeMap.getValue());
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
	}
	
	public void receiveMessage(String xml) {	
		
		try {			
			Packet packet = marshaller.unmarshallMessage(xml);			
			Object payload = marshaller.unmarshallPayload(packet);				
			callback.receiveMessage(stanzaFromPacket(packet), payload);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} 
	}
	
	private void unbindService() {
		context.unbindService(service);
	}
	
	private Stanza stanzaFromPacket(Packet packet) {
		try {
			IIdentity to = IdentityManagerImpl.staticfromJid(packet.getTo().toString());
			IIdentity from =IdentityManagerImpl.staticfromJid(packet.getFrom().toString());
			Stanza returnStanza = new Stanza(packet.getPacketID(), from, to);
			return returnStanza;
		} catch (InvalidFormatException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
