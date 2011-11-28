package org.societies.comm.xmpp.util;

import org.societies.comm.identity.Identity;
import org.societies.comm.identity.Identity.IdentityType;
import org.societies.comm.xmpp.Endpoint;
import org.societies.comm.xmpp.Stanza;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public abstract class SocietiesTinderUtil {
	
	public static Packet toPacket(Stanza stanza) {
		return null; // TODO not implemented!
	}
	
	public static Stanza toStanzaInfo(Packet packet) {
		Endpoint to = toEndpoint(packet.getTo());
		Endpoint from = toEndpoint(packet.getFrom());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}
	
	public static Endpoint toEndpoint(JID jid) {
		Identity i = new Identity(IdentityType.CSS,jid.getNode(),jid.getDomain()); // TODO hardcoded for now
		Endpoint e = new Endpoint(i, jid.getResource());
		return e;
	}

}
