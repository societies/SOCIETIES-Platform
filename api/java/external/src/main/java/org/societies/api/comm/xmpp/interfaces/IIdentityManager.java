package org.societies.api.comm.xmpp.interfaces;

import org.societies.api.comm.xmpp.datatypes.Identity;

public interface IIdentityManager {
	public Identity fromJid(String jid);
}
