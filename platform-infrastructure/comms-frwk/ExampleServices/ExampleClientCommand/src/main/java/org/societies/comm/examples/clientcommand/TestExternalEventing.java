/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.comm.examples.clientcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.example.fortunecookie.IWisdom;
import org.societies.api.schema.examples.fortunecookie.Cookie;
import org.societies.api.schema.examples.fortunecookie.FortuneCookieBeanResult;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestExternalEventing implements Runnable, Subscriber {
	private static final String PUBSUB_NODE_NAME = "Fortune_Cookies";
	private static final String PUBSUB_NODE_DESC = "SOCIETIES Fortune Cookie Publishing Service";
	
	private static Logger LOG = LoggerFactory.getLogger(TestExternalEventing.class);
	
	private PubsubClient pubSubManager;
	private IIdentityManager idManager;
	private IWisdom fcGenerator;
	private ICommManager commManager;
	
	//PROPERTIES
	public PubsubClient getPubSubManager() { return this.pubSubManager;	}
	public void setPubSubManager(PubsubClient pubSubManager) { this.pubSubManager = pubSubManager;	}

	public IWisdom getFcGenerator() { return fcGenerator; }
	public void setFcGenerator(IWisdom fcGenerator) { this.fcGenerator = fcGenerator; }
	
	public ICommManager getCommManager() { return commManager; }
	public void setCommManager(ICommManager commManager) { this.commManager = commManager; }
	
	//CONSTRUCTOR
	public TestExternalEventing() { }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run() */
	@Override
	public void run() {
		//GET IDENTITY MANAGER
		idManager = commManager.getIdManager();
		try {
			IIdentity pubsubID = idManager.getThisNetworkNode();
		
			//ADD LIST OF PACKAGES TO ADD SCHEMA OBJECTS
			List<String> packageList = new ArrayList<String>();
			packageList.add("org.societies.api.schema.examples.calculatorbean");
			packageList.add("org.societies.api.schema.examples.fortunecookie");
			pubSubManager.addJaxbPackages(packageList);
			
			//CREATE A PUB-SUB NODE
			LOG.info("### Creating PubsubNode");
			pubSubManager.ownerCreate(pubsubID, PUBSUB_NODE_NAME);
			LOG.info("### Created PubsubNode");
		
			//GET A LIST OF PUBSUB TOPICS (at root level)
			LOG.info("### Querying list of Nodes");
			List<String> listTopics = pubSubManager.discoItems(pubsubID, null);
			for (String s: listTopics)
				LOG.info("### Node: " + s);
		
			//SUBSCRIBE
			LOG.info("### Subscribing to pubsub");
			pubSubManager.subscriberSubscribe(pubsubID, PUBSUB_NODE_NAME, this);
			
			//PUBLISH A NEW PIECE OF WISDOM EVERY 60 SEC
			do {
				//GET A FORTUNE COOKIE
				Future<Cookie> asyncCookie = fcGenerator.getCookie();
				Cookie cookie = null;
				try {
					cookie = asyncCookie.get();
					LOG.info("### Generated Cookie: " + cookie.getValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				String itemID = String.valueOf(cookie.getId());
				//PUBLISH
				LOG.info("### Publishing Cookie");
				FortuneCookieBeanResult event = new FortuneCookieBeanResult();
				event.setCookie(cookie);
				
				String published = pubSubManager.publisherPublish(pubsubID, PUBSUB_NODE_NAME, itemID, event);
				System.out.println(published);
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			while (true);
		} catch (JAXBException jaxEx) {
			jaxEx.printStackTrace(); //ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
		} catch (XMPPError xmppEx) {
			xmppEx.printStackTrace();
		} catch (CommunicationException commEx) {
			commEx.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.comm.xmpp.datatypes.Identity, java.lang.String, java.lang.String, java.lang.Object) */
	@Override
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
		LOG.info("### New Pubsub Event topic: " + node);
		//CHECK WHAT PAYLOAD IS
		if (item.getClass().equals(FortuneCookieBeanResult.class)) {
			FortuneCookieBeanResult info = (FortuneCookieBeanResult)item;
			Cookie cookie = info.getCookie();
			LOG.info("### Wisdom: " + cookie.getValue());
		}		
	}
}
