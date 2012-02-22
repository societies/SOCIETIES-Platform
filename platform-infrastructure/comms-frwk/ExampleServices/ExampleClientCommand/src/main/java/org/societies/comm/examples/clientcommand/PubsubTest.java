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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.example.fortunecookie.IWisdom;
import org.societies.example.fortunecookieservice.schema.Cookie;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class PubsubTest implements Runnable, Subscriber {
	private static final String PUBSUB_NODE_NAME = "Fortune_Cookies";
	private static final String PUBSUB_NODE_DESC = "SOCIETIES Fortune Cookie Publishing Service";
	
	private PubsubClient pubSubManager;
	private IIdentityManager idManager;
	private IWisdom fcGenerator;
	
	//PROPERTIES
	public PubsubClient getPubSubManager() {
		return this.pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
	}

	public IWisdom getFcGenerator() {
		return fcGenerator;
	}

	public void setFcGenerator(IWisdom fcGenerator) {
		this.fcGenerator = fcGenerator;
	}
	
	//CONSTRUCTOR
	public PubsubTest() {
		idManager = new IdentityManager();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run() */
	@Override
	public void run() {
		//CREATE A PUB-SUB NODE
		Identity pubsubID = idManager.fromJid("XCManager.societies.local");
		try {
			pubSubManager.ownerCreate(pubsubID, PUBSUB_NODE_NAME);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
		//GET A LIST OF PUBSUB TOPICS (at root level)
		try {
			List<String> listTopics = pubSubManager.discoItems(pubsubID, null);
			for (String s: listTopics)
				System.out.println(s);
		} catch (XMPPError e2) {
			e2.printStackTrace();
		} catch (CommunicationException e2) {
			e2.printStackTrace();
		}
		
		//SUBSCRIBE
		try {
			pubSubManager.subscriberSubscribe(pubsubID, PUBSUB_NODE_NAME, this);
		} catch (XMPPError e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
		//EVERY 60 SEC, PUBLISH A NEW PIECE OF WISDOM
		do {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//GET A FORTUNE COOKIE
			Future<Cookie> asyncCookie = fcGenerator.getCookie();
			Cookie cookie = null;
			try {
				cookie = asyncCookie.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			String itemId = String.valueOf(cookie.getId());
			String text = cookie.getValue();
			
			//BUILD THE PUB-SUB ELEMENT
			/*<FortuneCookie>
			 *    <wisdom>some text</wisdom
			 *</FortuneCookie>
			 */ 
			Document doc; Element entry = null;
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				entry = doc.getDocumentElement();
				
				Node title = doc.createElementNS("http://societies.org/example/schema/fortunecookie", "FortuneCookie");
				Node wisdom = doc.createElement("wisdom"); 
				wisdom.setNodeValue(text);
				entry.appendChild(title);
				entry.appendChild(wisdom);
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//PUBLISH
			try {
				String published = pubSubManager.publisherPublish(pubsubID, PUBSUB_NODE_NAME, itemId, entry);
				System.out.println(published);
			} catch (XMPPError e) {
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.comm.xmpp.datatypes.Identity, java.lang.String, java.lang.String, org.w3c.dom.Element)*/
	@Override
	public void pubsubEvent(Identity pubsubService, String node, String itemId, Object item) {
		System.out.println("New info published on topic" + node);
		System.out.println("ID: " + itemId);
		System.out.println("Detail: " + item);
	}

}
