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
package org.societies.integration.test.bit.asyncmessage;

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
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.examples.calculatorbean.CalcBean;
import org.societies.api.schema.examples.calculatorbean.MethodType;
import org.societies.test.Testnode;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class MessageTestCase {

	private static final String MESSAGE_1 = "Message Test774";
	private static final String MESSAGE_1_REPLY = "Message Reply Test774";
	private static final String INFOQUERY_1 = "Info Query Test774";
	private static final String INFOQUERY_1_REPLY = "Info Query Reply Test774";
	private static Logger LOG = LoggerFactory.getLogger(MessageTestCase.class);
	private ServerCallback sCallback;
	private IIdentity myIdentity;

	public MessageTestCase() {
	}

	/** THIS IS THE TEST OBJECT USED AS THE MESSAGE BEAN
	 * @param message string to add to be uses as a test message
	 * @return
	 */
	private Testnode createTestItem(String message)  {
		Testnode tn = new Testnode();
		tn.setTestattribute(message);
		return tn;
	}
	
	@Before
	public void setUp() {
		LOG.info("###1854... setUp");

		//GET CURRENT NODE IDENTITY
		myIdentity = TestCase774.commManager.getIdManager().getThisNetworkNode();
		LOG.info("###1854... getThisNetworkNode: " + myIdentity.getJid());

		//SETUP SERVER TO RECEIVE MESSAGES/IQs
		sCallback = new ServerCallback();
		
		//REGISTER
		try {
			TestCase774.commManager.register(sCallback);
		} catch (CommunicationException ce) {
			fail("CommException registering testCase774 featureserver: " + ce.getMessage());
		}
		
		LOG.info("### Registered Featureserver Testcase774");
	}

	@Test
	public void TestSendIQ() {
		LOG.info("###1854... TestSendIQ");

		//SETUP CALLBACK FOR RETURNED RESULT
		ClientCallback cCallback = new ClientCallback();

		//CREATE MESSAGE BEAN
		LOG.info("### Creating a test object");
		Testnode testObj = createTestItem(INFOQUERY_1);

		//SET DESTINATION (SELF)
		Stanza stanza = new Stanza(myIdentity);
		
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "cCallback.RecieveResult()"
			LOG.info("### Sending IQ");
			TestCase774.commManager.sendIQGet(stanza, testObj, cCallback);
		} catch (CommunicationException ce) {
			fail("CommException sending IQ: " + ce.getMessage());
		};

		synchronized (cCallback) {
			LOG.info("TestSendIQ waiting...");
			try {
				cCallback.wait(15*1000);
			} catch (InterruptedException e) {
				LOG.info("TestSendIQ InterruptedException: " + e.getMessage());
				fail("InterruptedException");
			}
		}

		// CHECK INFO QUERY ARRIVED AT SERVER
		assertNotNull("Timed out", sCallback.getMessageReceived());
		String result = sCallback.getMessageReceived();
		LOG.info("### result: " + result);	
		assertEquals(INFOQUERY_1, result);
		
		// CHECK INFO QUERY RESULT
		assertNotNull("Timed out", cCallback.getMessageObject());
		result = cCallback.getMessageObject().getTestattribute();
		LOG.info("### result: " + result);	
		assertEquals(INFOQUERY_1_REPLY, result);
	}

	@Test
	public void TestSendMessage() {
		LOG.info("###1854... TestSendMessage");

		//CREATE MESSAGE BEAN
		LOG.info("### Creating a test object");
		Testnode testObj = createTestItem(MESSAGE_1);

		//SET DESTINATION (SELF)
		Stanza stanza = new Stanza(myIdentity);
		
		try {
			//SEND MESSAGE - NO RESPONSE
			LOG.info("### Sending Message");
			TestCase774.commManager.sendMessage(stanza, testObj);
		} catch (CommunicationException ce) {
			fail("CommException sending Message: " + ce.getMessage());
		};

		synchronized (sCallback) {
			LOG.info("TestSendMessage waiting...");
			try {
				sCallback.wait(15*1000);
			} catch (InterruptedException e) {
				LOG.info("TestSendMessage InterruptedException: " + e.getMessage());
				fail("InterruptedException");
			}
		}

		// CHECK Message ARRIVED AT SERVER
		assertNotNull("Timed out", sCallback.getMessageReceived());
		String result = sCallback.getMessageReceived();
		LOG.info("### result: " + result);	
		assertEquals(MESSAGE_1, result);
	}

	@After
	public void tearDown() {
	}
}