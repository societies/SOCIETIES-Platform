package org.societies.comm.xmpp.interfaces;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.IdentityType;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.comm.xmpp.datatypes.EndpointImpl;
import org.societies.comm.xmpp.datatypes.IdentityImpl;

public class IdentityManager implements IIdentityManager {
	
	// TODO cache identities
	// TODO CIS CSS_RICH overlap! 
	
	public IdentityManager(){}

	public Identity fromJid(String jid) {
		String[] parts = jid.split("@|/");
		switch (parts.length) {
			case 1:
				int firstDot = jid.indexOf(".");
				return new EndpointImpl(IdentityType.CSS_RICH, jid.substring(0,firstDot), jid.substring(firstDot+1),"rich");
			case 2:
				return new IdentityImpl(IdentityType.CSS, parts[0], parts[1]);
			case 3:
				return new EndpointImpl(IdentityType.CSS_LIGHT, parts[0], parts[1], parts[2]);
			default:
				return null;
		}
	}
}
