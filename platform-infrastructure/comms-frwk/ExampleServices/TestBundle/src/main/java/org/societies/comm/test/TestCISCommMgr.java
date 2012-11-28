package org.societies.comm.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.comm.xmpp.pubsub.impl.PubsubServiceRouter;
import org.societies.identity.IdentityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCISCommMgr extends Thread {
	
	private static Logger LOG = LoggerFactory
			.getLogger(TestCISCommMgr.class);
	
	private ICISCommunicationMgrFactory ccmFactory;
	private ICommManager endpoint;
	
	@Autowired
	public TestCISCommMgr(ICommManager endpoint, ICISCommunicationMgrFactory ccmFactory) {
		this.endpoint = endpoint;
		this.ccmFactory = ccmFactory;
		start();
	}
	
	@Override
	public void run() {
		try {
			String jid = "cis1.red.local";
			String password = "password.red.local";
			IIdentity cisId = endpoint.getIdManager().fromJid(jid);
		
			LOG.info("### going to create new CIS Comm Mgr");
			ICommManager ccm = ccmFactory.getNewCommManager(cisId, password);
			
			PubsubServiceRouter psr = new PubsubServiceRouter(ccm);
			
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
