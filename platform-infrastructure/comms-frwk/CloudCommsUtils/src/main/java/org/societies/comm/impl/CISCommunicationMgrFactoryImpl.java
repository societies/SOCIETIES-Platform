package org.societies.comm.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.comm.xmpp.pubsub.impl.PubsubServiceRouter;
import org.societies.comm.xmpp.xc.impl.XCCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

public class CISCommunicationMgrFactoryImpl implements ICISCommunicationMgrFactory {

	private static Logger LOG = LoggerFactory
			.getLogger(CISCommunicationMgrFactoryImpl.class);
	
	private HashMap<IIdentity,ICommManager> cisCommManagers;
	private HashMap<IIdentity,PubsubServiceRouter> cisPubsubServices;
	private String genericPassword;
	private ICommManager originalEndpoint;
	private IIdentityManager idm;
	private String domainName;
	private SessionFactory sf;
	
	public CISCommunicationMgrFactoryImpl(ICommManager endpoint, String genericPassword, SessionFactory sf) {
		this.genericPassword = genericPassword;
		originalEndpoint = endpoint;
		this.sf = sf;
		
		initCISCommunicationMgrFactoryImpl();
	}
	
	public CISCommunicationMgrFactoryImpl(ICommManager endpoint, String genericPassword) {
		this.genericPassword = genericPassword;
		originalEndpoint = endpoint;
		
		initCISCommunicationMgrFactoryImpl();
	}
	
	public boolean initCISCommunicationMgrFactoryImpl() {
		cisCommManagers = new HashMap<IIdentity, ICommManager>();
		cisPubsubServices = new HashMap<IIdentity, PubsubServiceRouter>();
		idm = originalEndpoint.getIdManager();
		if (idm!=null && idm.getThisNetworkNode()!=null){
            domainName = idm.getThisNetworkNode().getDomain();
            return true;
        }
		return false;
	}
	
	@Override
	public ICommManager getNewCommManager(IIdentity cisIdentity, String credentials) throws CommunicationException {
		if (!cisIdentity.getType().equals(IdentityType.CIS))
			throw new CommunicationException("Provided identity does not belong to a CIS");
		
		// TODO verify if cisIdentity exists already
		
		// manipulate threadContextClassloader so that XCCommunicationMgr threads are created with the CommsBundle classloader
		ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(XCCommunicationMgr.class.getClassLoader());
		
		// CIS Comms
		XCCommunicationMgr commMgr = new XCCommunicationMgr(cisIdentity.getDomain(), cisIdentity.getJid(), credentials, this.idm.getDomainAuthorityNode().getJid());
		commMgr.loginFromConfig();
		if (commMgr.getIdManager()==null)
			throw new CommunicationException("Unable to create CISCommManager!");
		
		// CIS Pubsub
		PubsubServiceRouter psr = null;
		if (sf==null)
			psr = new PubsubServiceRouter(commMgr);
		else
			psr = new PubsubServiceRouter(commMgr,sf);
		
		// restore threadContextClassloader
		Thread.currentThread().setContextClassLoader(threadContextClassLoader);
		
		cisCommManagers.put(cisIdentity, commMgr);
		cisPubsubServices.put(cisIdentity, psr);
		return commMgr;
	}

	@Override
	public Map<IIdentity, ICommManager> getAllCISCommMgrs() {
		return cisCommManagers;
	}

	@Override
	public ICommManager getNewCommManager() throws CommunicationException {
		try {
			if (domainName==null)
				if (initCISCommunicationMgrFactoryImpl())
					throw new CommunicationException("Not connected to domain!");
				
			String randomCisIdentifier = IdentityManagerImpl.CIS_PREFIX+UUID.randomUUID().toString()+"."+domainName;
			IIdentity cisIdentity = idm.fromJid(randomCisIdentifier);
			return getNewCommManager(cisIdentity, genericPassword);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ICommManager getNewCommManager(String jid) throws CommunicationException {
		if (!jid.startsWith(IdentityManagerImpl.CIS_PREFIX))
			throw new CommunicationException("The provided JID is not a valid CIS JID");
		
		try {
			// TODO verify if exists one with same JID logged int
			IIdentity cisIdentity = idm.fromJid(jid);
			return getNewCommManager(cisIdentity, genericPassword);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void destroyAllConnections() {
		for (ICommManager cm : cisCommManagers.values()) {
			LOG.info("Disconnecting CIS '"+cm.getIdManager().getThisNetworkNode().getJid()+"'");
			try {
				((ICommManagerController)cm).logout();
			} catch (ClassCastException e) {
				LOG.error("ICommManager cannot be casted to ICommManagerController!!!", e);
			}
		}
	}
}
