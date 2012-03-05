package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;

public class CISCommunicationMgrFactoryImpl implements ICISCommunicationMgrFactory {

	private HashMap<IIdentity,ICommManager> cisCommManagers;
	private String genericPassword;
	
	public CISCommunicationMgrFactoryImpl(ICommManager endpoint, String genericPassword) {
		cisCommManagers = new HashMap<IIdentity, ICommManager>();
		this.genericPassword = genericPassword;
	}
	
	@Override
	public ICommManager getNewCommManager(IIdentity cisIdentity, String credentials) {
		XCCommunicationMgr commMgr = new XCCommunicationMgr(cisIdentity.getDomain(), cisIdentity.getJid(), credentials);
		cisCommManagers.put(cisIdentity, commMgr);
		return commMgr;
	}

	@Override
	public Map<IIdentity, ICommManager> getAllCISCommMgrs() {
		return cisCommManagers;
	}

	@Override
	public ICommManager getNewCommManager() {
		String randomCisIdentifier = UUID.randomUUID().toString();
		// TODO verify if exists
		IIdentity cisIdentity = null; //TODO
		XCCommunicationMgr commMgr = new XCCommunicationMgr(cisIdentity.getDomain(), cisIdentity.getJid(), genericPassword);
		cisCommManagers.put(cisIdentity, commMgr);
		return commMgr;
	}
}
