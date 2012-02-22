package org.societies.comm.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TestPubsubClient extends Thread implements Subscriber, ApplicationListener<PubsubEvent> {
	
	private static Logger LOG = LoggerFactory
			.getLogger(TestPubsubClient.class);
	
	private PubsubClient psc;
	private IIdentityManager idm;
	private ICommManager endpoint;
	
	@Autowired
	public TestPubsubClient(PubsubClient psc, ICommManager endpoint) {
		this.psc = psc;
		idm = new IdentityManager();
		this.endpoint = endpoint;
		start();
	}

//	private Testnode createTestItem()  {
//		Testnode tn = new Testnode();
//		tn.setTestattribute("testValue");
//		return tn;
//	}

	@Override
	public void pubsubEvent(Identity pubsubService, String node, String itemId,
			Object item) {
		LOG.info("### pubsubEvent from "+pubsubService+" referring to node "+node+": "+item.getClass().getName());
	}

	@Override
	public void run() {
		try {
			Identity psService = idm.fromJid("societiespubsub.red.local");
			String node = "testNode";
			List<String> packageList = new ArrayList<String>();
			packageList.add("org.societies.test");
			
			Thread.sleep(1000);
			LOG.info("### going to add JAXB package...");
			psc.addJaxbPackages(packageList);
			LOG.info("### going to create testNode...");
			psc.ownerCreate(psService, node);
			LOG.info("### created testNode! going to subscribe testNode...");
			psc.subscriberSubscribe(psService, node, this);
			LOG.info("### subscribed testNode! going to publish in testNode...");
			Object item = null; // <-- Create a meaningful object instance -- createTestItem();
			psc.publisherPublish(psService, node, null, item);
			LOG.info("### published in testNode! finishing Pubsub tests...");
			
			LOG.info("### starting events test...");
			String node2 = "testNode2";
			PubsubEventFactory pef = PubsubEventFactory.getInstance(endpoint.getIdentity());
			PubsubEventStream stream = pef.getStream(psService, node2);
			stream.addJaxbPackages(packageList);
			LOG.info("### got stream");
			stream.addApplicationListener(this);
			LOG.info("### added listener");
			PubsubEvent event = new PubsubEvent(this, item);
			stream.multicastEvent(event);
			LOG.info("### posted event");

		} catch (XMPPError e) {
			LOG.error(e.getMessage());
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		} catch (JAXBException e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void onApplicationEvent(PubsubEvent arg0) {
		LOG.info("### applicationEvent from "+arg0.getPubsubService()+" referring to node "+arg0.getNode()+": "+arg0.getPayload().toString());
	}
}
