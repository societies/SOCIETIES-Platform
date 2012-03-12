package org.societies.api.internal.comm;

import java.util.Map;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

public interface ICISCommunicationMgrFactory {
	public ICommManager getNewCommManager();
	public ICommManager getNewCommManager(IIdentity cisIdentity, String credentials);
	public Map<IIdentity, ICommManager> getAllCISCommMgrs();
}
