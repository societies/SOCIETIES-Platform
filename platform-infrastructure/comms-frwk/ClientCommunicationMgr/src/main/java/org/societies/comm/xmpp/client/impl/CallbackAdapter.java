package org.societies.comm.xmpp.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.interfaces.Callback;
import org.xmpp.packet.Packet;

import android.content.Context;
import android.content.ServiceConnection;

public class CallbackAdapter implements Callback {
	
	private static final Logger log = LoggerFactory.getLogger(CallbackAdapter.class);
	
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
			log.error(e.getMessage(), e);
		} 

	}
	@Override
	public void receiveError(String xml) {
		unbindService();
		
		try {
			Packet packet = marshaller.unmarshallIq(xml);
			callback.receiveError(stanzaFromPacket(packet), null); // TODO parse error
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
	}
	
	private void unbindService() {
		context.unbindService(service);
	}
	
	private Stanza stanzaFromPacket(Packet packet) {
		IdentityManager idm = new IdentityManager();
		Identity to = idm.fromJid(packet.getTo().toString());
		Identity from = idm.fromJid(packet.getFrom().toString());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}
}
