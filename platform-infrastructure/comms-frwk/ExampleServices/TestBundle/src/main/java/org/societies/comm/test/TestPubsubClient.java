package org.societies.comm.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.schema.examples.calculatorbean.CalcBean;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.test.Testnode;
import org.springframework.context.ApplicationListener;

public class TestPubsubClient extends Thread implements Subscriber, ApplicationListener<PubsubEvent> {
	
	private static Logger LOG = LoggerFactory
			.getLogger(TestPubsubClient.class);
	
	private PubsubClient psc;
	private IIdentityManager idm;
	private ICommManager endpoint;
	
	public TestPubsubClient(PubsubClient psc, ICommManager endpoint) {
		this.psc = psc;
		this.endpoint = endpoint;
		idm = endpoint.getIdManager();
		LOG.info("TestPubsubClient initialized! Launching test thread...");
		start();
	}

	private Testnode createTestItem()  {
//	private CalcBean createTestItem()  {
		Testnode tn = new Testnode();
		tn.setTestattribute("testValue");
		return tn;
		
//		CalcBean calc = new CalcBean();
//		calc.setMessage("successful");
//		return calc; 
	}

	@Override
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
		LOG.info("### pubsubEvent from "+pubsubService+" referring to node "+node+": "+item.getClass().getName());
		if (item instanceof CalcBean) {
			CalcBean calc = (CalcBean)item;
			LOG.info("###Object Info: " + calc.getMessage());
		}
		if (item instanceof Testnode) {
			Testnode t = (Testnode)item;
			LOG.info("###Object Info: " + t.getTestattribute());
		}
	}

	@Override
	public void run() {
		try {
			IIdentity psService = idm.getThisNetworkNode();
			String node = "testNode";
			List<String> packageList = new ArrayList<String>();
			List<String> classList = new ArrayList<String>();
			packageList.add("org.societies.test");
			classList.add(Testnode.class.getCanonicalName());
			LOG.info("ready to start Pubsub and Eventing tests for jid '"+psService.getJid()+"'");
			
			Thread.sleep(1000);
			LOG.info("### going to add JAXB package...");
			psc.addJaxbPackages(packageList); // TODO DEPRECATED; Application listener is also DEPRECATED
			try {
				psc.addSimpleClasses(classList);
				LOG.info("### going to create testNode...");
				psc.ownerCreate(psService, node);
			} catch (XMPPError e) {
				LOG.error("Node already exists", e);
			} catch (ClassNotFoundException e) {
				LOG.error("ClassNotFoundException!!! EVERYTHING GOING TO HELL!", e);
			}
			LOG.info("### created testNode! going to subscribe testNode...");
			psc.subscriberSubscribe(psService, node, this);
			LOG.info("### subscribed testNode! going to publish in testNode...");
			Object item = createTestItem(); // <-- Create a meaningful object instance -- createTestItem();
			psc.publisherPublish(psService, node, null, item);
			LOG.info("### published in testNode! finishing Pubsub tests...");
			
			/*
			LOG.info("### starting events test...");
			String node2 = "testNode2";
			PubsubEventFactory pef = PubsubEventFactory.getInstance(idm.getThisNetworkNode());
			PubsubEventStream stream = pef.getStream(psService, node2);
			stream.addJaxbPackages(packageList);
			LOG.info("### got stream");
			stream.addApplicationListener(this);
			LOG.info("### added listener");
			PubsubEvent event = new PubsubEvent(this, item);
			stream.multicastEvent(event);
			LOG.info("### posted event");
			*/

		} catch (XMPPError e) {
			LOG.error(e.getStanzaErrorString(), e); //.getMessage(), e);
		} catch (CommunicationException e) {
			LOG.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		} catch (JAXBException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void onApplicationEvent(PubsubEvent arg0) {
		LOG.info("### applicationEvent from "+arg0.getPubsubService()+" referring to node "+arg0.getNode()+": "+arg0.getPayload().toString());
	}
}
