package org.societies.comm.xmpp.xc.impl;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.identity.IdentityManagerImpl;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class TinderUtils {
	
	public static Stanza stanzaFromPacket(Packet packet) throws InvalidFormatException {
		IIdentity to = IdentityManagerImpl.staticfromJid(packet.getTo().toString());
		IIdentity from = IdentityManagerImpl.staticfromJid(packet.getFrom().toString());
		Stanza returnStanza = new Stanza(packet.getID(), from, to);
		return returnStanza;
	}

	public static IQ createIQ(Stanza stanza, Type type) {
		IQ retIq = new IQ(type); // discarding ID generation from stanza
		retIq.setTo(stanza.getTo().getJid());
		retIq.setFrom(stanza.getFrom().getJid());
		retIq.setID(stanza.getId());
		return retIq;
	}

	public static Message createMessage(Stanza stanza, org.xmpp.packet.Message.Type type) {
		Message retMessage = new Message();
		if (type!=null)
			retMessage.setType(type);
		retMessage.setTo(stanza.getTo().getJid());
		retMessage.setFrom(stanza.getFrom().getJid());
		retMessage.setID(stanza.getId());
		return retMessage;
	}

}
