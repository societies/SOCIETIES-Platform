package org.societies.comm.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentityManager;
import org.societies.test.Testnode;
import org.societies.test.event.Eventnode;

public class Test3PServiceServer extends Thread implements IFeatureServer {
	
	private static Logger LOG = LoggerFactory
			.getLogger(Test3PServiceServer.class);
	
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays.asList(
					"http://societies.org/test",
					"http://societies.org/test#event"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList(
					"org.societies.test",
					"org.societies.test.event"));
	
	private IIdentityManager idm;
	private ICommManager endpoint;
	
	public Test3PServiceServer(ICommManager endpoint) {
		this.endpoint = endpoint;
		idm = endpoint.getIdManager();
		try {
			endpoint.register(this);
			LOG.info("Test3PServiceServer initialized! Launching test thread...");
		} catch (CommunicationException e) {
			LOG.error("CommunicationException",e);
		}
		
	}

	private Testnode createTestItem()  {
		Testnode tn = new Testnode();
		tn.setTestattribute("serverTestValue");
		return tn;
	}
	
	private Eventnode createEventItem()  {
		Eventnode tn = new Eventnode();
		tn.setTestattribute("serverTestValue");
		return tn;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			LOG.info("Sending event...");
			Object o = createEventItem();
			Stanza s = new Stanza(idm.getThisNetworkNode());
			endpoint.sendMessage(s, o);
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
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO not tested
	}
	
	@Override
	public Object getQuery(Stanza arg0, Object arg1) throws XMPPError {
		LOG.info("Got IQ! Testnode:"+((Testnode)arg1).getTestattribute());
		LOG.info("Sending response and starting event thread...");
		start();
		return createTestItem();
	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		return getQuery(arg0, arg1);
	}

}
