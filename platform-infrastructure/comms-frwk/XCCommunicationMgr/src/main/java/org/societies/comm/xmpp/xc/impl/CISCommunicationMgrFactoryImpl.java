package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;

public class CISCommunicationMgrFactoryImpl implements ICISCommunicationMgrFactory {

	private static Logger LOG = LoggerFactory
			.getLogger(CISCommunicationMgrFactoryImpl.class);
	
	private HashMap<IIdentity,ICommManager> cisCommManagers;
	private String genericPassword;
	private ICommManager originalEndpoint;
	private IIdentityManager idm;
	private String domainName;
	
	public CISCommunicationMgrFactoryImpl(ICommManager endpoint, String genericPassword) {
		cisCommManagers = new HashMap<IIdentity, ICommManager>();
		this.genericPassword = genericPassword;
		originalEndpoint = endpoint;
		idm = originalEndpoint.getIdManager();
		domainName = idm.getThisNetworkNode().getDomain();
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
		try {
			String randomCisIdentifier = UUID.randomUUID().toString()+"."+domainName;
			// TODO verify if exists
			IIdentity cisIdentity = idm.fromJid(randomCisIdentifier);
			XCCommunicationMgr commMgr = new XCCommunicationMgr(cisIdentity.getDomain(), cisIdentity.getJid(), genericPassword);
			cisCommManagers.put(cisIdentity, commMgr);
			return commMgr;
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
}
