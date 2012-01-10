package org.societies.comm.xmpp.datatypes;

import java.util.UUID;

import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class Stanza {
	
	private final String id;
	private final Identity from;
	private final Identity to;

	public static Stanza fromPacket(Packet packet) {
		Identity to = Identity.fromJid(packet.getTo().toString());
		Identity from = Identity.fromJid(packet.getFrom().toString());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}
	
	public Stanza(String id, Identity from, Identity to) {
		this.id = id;
		this.from = from;
		this.to = to;
		// Note: Whack won't let us get the Nature out of a packet (IQ, Presence
		// or Message)
	}
	
	public Stanza(Identity to) {
		this.id = UUID.randomUUID().toString();
		this.from = null;
		this.to = to;
	}

	public IQ createIQ(IQ.Type type){
		IQ iq = new IQ();
		//TODO: Need to transform from endpoints to JIDs
		return iq;
	}
	
	public Message createMessage(Message.Type type){
		Message m = new Message();
		//TODO: Need to transform from endpoints to JIDs
		return m;
	}
	
	public String getId() {
		return id;
	}

	public Identity getFrom() {
		return from;
	}

	public Identity getTo() {
		return to;
	}
}
