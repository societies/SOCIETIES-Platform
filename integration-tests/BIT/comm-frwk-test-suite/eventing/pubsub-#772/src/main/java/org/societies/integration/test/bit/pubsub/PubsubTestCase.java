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
package org.societies.integration.test.bit.pubsub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.examples.calculatorbean.CalcBeanResult;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class PubsubTestCase {

	private static final String PUBSUB_NODE_NAME = "IntegrationTestNode";
	private static final String PUBSUB_EVENT_ID = "test123";
	private static Logger LOG = LoggerFactory.getLogger(PubsubTestCase.class);
	private PubsubCallback callback;
	private IIdentity myIdentity;

	public PubsubTestCase() {
	}

	//private Testnode createTestItem()  {
	private CalcBeanResult createTestItem()  {
		//Testnode tn = new Testnode();
		//tn.setTestattribute("testValue");
		//return tn;
		CalcBeanResult calc = new CalcBeanResult();
		calc.setResult(1);
		calc.setText(PUBSUB_EVENT_ID);
		return calc;
	}
	
	@Before
	public void setUp() {
		LOG.info("###772... setUp");

		//GET CURRENT NODE IDENTITY
		myIdentity = TestCase772.commManager.getIdManager().getThisNetworkNode();
		LOG.info("###771... getThisNetworkNode: " + myIdentity.getJid());

		//SETUP PUBSUB RETURN STUFF
		callback = new PubsubCallback();
		
		//ADD LIST OF PACKAGES TO ADD SCHEMA OBJECTS
		List<String> packageList = new ArrayList<String>();
		packageList.add("org.societies.test");
		try {
			TestCase772.pubSubManager.addJaxbPackages(packageList);
		} catch (JAXBException e) {
			fail("Exception adding JAXB test package: " + e.getMessage());
		}
		
		//CREATE A PUB-SUB NODE
		LOG.info("### Creating PubsubNode");
		try {
			TestCase772.pubSubManager.ownerCreate(myIdentity, PUBSUB_NODE_NAME);
		} catch (XMPPError xe) {
			fail("XMPPException creating pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException creating pubsub node: " + ce.getMessage());
		}
		LOG.info("### Created PubsubNode");
	}

	@Test
	public void TestAdd() {
		LOG.info("###771... TestAdd");

		//GET A LIST OF PUBSUB TOPICS
		LOG.info("### Querying list of Nodes");
		List<String> listTopics;
		try {
			listTopics = TestCase772.pubSubManager.discoItems(myIdentity, null);
			for (String s: listTopics)
				LOG.info("### Node: " + s);
			String returnedNode = listTopics.get(0);
			assertEquals(returnedNode, PUBSUB_NODE_NAME);
		} catch (XMPPError xe) {
			fail("XMPPException querying pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException querying pubsub node: " + ce.getMessage());
		}
		
		//SUBSCRIBE
		LOG.info("### Subscribing to pubsub");
		Subscription mySub;
		try {
			mySub = TestCase772.pubSubManager.subscriberSubscribe(myIdentity, PUBSUB_NODE_NAME, callback);
			assertEquals(mySub.getNode(), PUBSUB_NODE_NAME);
		} catch (XMPPError xe) {
			fail("XMPPException subscribing to pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException subscribing to pubsub node: " + ce.getMessage());
		}
		
		//CREATE OBJECT TO PUBLISH
		LOG.info("### Creating a test object");
		Object testObj = createTestItem();
		
		//PUBLISH
		LOG.info("### Publishing Object");
		try {
			String published = TestCase772.pubSubManager.publisherPublish(myIdentity, PUBSUB_NODE_NAME, PUBSUB_EVENT_ID, testObj);
			LOG.info("### Publishing Result: " + published);
		} catch (XMPPError xe) {
			fail("XMPPException publishing to pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException publishing to pubsub node: " + ce.getMessage());
		}
		
		synchronized (callback) {
			LOG.info("TestAdd waiting...");
			try {
				callback.wait(15*1000);
			} catch (InterruptedException e) {
				LOG.info("TestAdd InterruptedException: " + e.getMessage());
				fail("InterruptedException");
				e.printStackTrace();
			}
		}

		// if the method times out, fail
		assertNotNull("Timed out", callback.getEventObject());
				
		// otherwise, check the result
		String result = callback.getEventObject().getText();
		LOG.info("### result: " + result);	
		assertEquals(PUBSUB_EVENT_ID, result);
	}

	@After
	public void tearDown() {
		LOG.info("### Unsubscribing from node");
		try {
			TestCase772.pubSubManager.subscriberUnsubscribe(myIdentity, PUBSUB_NODE_NAME, callback);
		} catch (XMPPError xe) {
			fail("XMPPException Unsubscribing from pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException Unsubscribing from pubsub node: " + ce.getMessage());
		}
		
		LOG.info("### Deleting pubsub node");
		try {
			TestCase772.pubSubManager.ownerDelete(myIdentity, PUBSUB_NODE_NAME);
		} catch (XMPPError xe) {
			fail("XMPPException Deleting pubsub node: " + xe.getMessage());
		} catch (CommunicationException ce) {
			fail("CommException Deleting pubsub node: " + ce.getMessage());
		}
		LOG.info("###772... tearDown complete");
	}
}