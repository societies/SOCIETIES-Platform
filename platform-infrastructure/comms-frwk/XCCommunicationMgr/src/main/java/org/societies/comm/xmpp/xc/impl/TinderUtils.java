package org.societies.comm.xmpp.xc.impl;

import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class TinderUtils {
	
	public static Stanza stanzaFromPacket(Packet packet) {
		Identity to = Identity.fromJid(packet.getTo().toString());
		Identity from = Identity.fromJid(packet.getFrom().toString());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}

	public static IQ createIQ(Stanza stanza, Type type) {
		IQ retIq = new IQ(type); // discarding ID generation from stanza
		retIq.setTo(stanza.getTo().getJid());
		retIq.setFrom(stanza.getFrom().getJid());
		return retIq;
	}

	public static Message createMessage(Stanza stanza, org.xmpp.packet.Message.Type type) {
		Message retMessage = new Message();
		if (type!=null)
			retMessage.setType(type);
		retMessage.setTo(stanza.getTo().getJid());
		retMessage.setFrom(stanza.getFrom().getJid());
		return retMessage;
	}

}
