package org.societies.comm.xmpp.datatypes;

import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class Stanza {
	private final String id;
	private final Endpoint from;
	private final Endpoint to;

	public static Stanza fromPacket(Packet packet) {
		Endpoint to = Endpoint.fromJID(packet.getTo());
		Endpoint from = Endpoint.fromJID(packet.getFrom());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}
	
	public Stanza(String id, Endpoint from, Endpoint to) {
		this.id = id;
		this.from = from;
		this.to = to;
		// Note: Whack won't let us get the Nature out of a packet (IQ, Presence
		// or Message)
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

	public Endpoint getFrom() {
		return from;
	}

	public Endpoint getTo() {
		return to;
	}
}
