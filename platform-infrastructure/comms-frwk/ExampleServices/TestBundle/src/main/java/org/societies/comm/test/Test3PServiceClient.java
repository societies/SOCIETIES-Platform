package org.societies.comm.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.test.Testnode;
import org.societies.test.event.Eventnode;

public class Test3PServiceClient extends Thread implements ICommCallback {
	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList(
					"http://societies.org/test#event",
					"http://societies.org/test"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList(
					"org.societies.test.event",
					"org.societies.test"));
	
	private static Logger LOG = LoggerFactory
			.getLogger(Test3PServiceClient.class);
	
	private IIdentityManager idm;
	private ICommManager endpoint;
	private INetworkNode thisNode;
	
	public Test3PServiceClient(ICommManager endpoint) {
		this.endpoint = endpoint;
		idm = endpoint.getIdManager();
		thisNode = idm.getThisNetworkNode();
		try {
			endpoint.register(this);
			LOG.info("Test3PServiceClient initialized! Launching test thread...");
			start();
		} catch (CommunicationException e) {
			LOG.error("CommunicationException",e);
		}
		
	}

	private Testnode createTestItem()  {
		Testnode tn = new Testnode();
		tn.setTestattribute("clientTestValue");
		return tn;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			LOG.info("Sending IQ...");
			Object o = createTestItem();
			Stanza s = new Stanza(thisNode);
			endpoint.sendIQGet(s, o, this);
		} catch (InterruptedException e) {
			LOG.error("InterruptedException",e);
		} catch (CommunicationException e) {
			LOG.error("CommunicationException",e);
		}
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		LOG.info("Got Error!");
	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		LOG.info("Got Info!");
	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		LOG.info("Got Items!");
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		LOG.info("Got Message! Eventnode:"+((Eventnode)arg1).getTestattribute());
	}

	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		LOG.info("Got IQ result! Testnode:"+((Testnode)arg1).getTestattribute());
	}
}
