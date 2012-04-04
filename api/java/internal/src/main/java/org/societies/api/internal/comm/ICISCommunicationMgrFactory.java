package org.societies.api.internal.comm;

import java.util.Map;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

public interface ICISCommunicationMgrFactory {
	public ICommManager getNewCommManager() throws CommunicationException;
	public ICommManager getNewCommManager(IIdentity cisIdentity, String credentials) throws CommunicationException;
	public Map<IIdentity, ICommManager> getAllCISCommMgrs() ;
}
