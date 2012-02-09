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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.interfaces.IIdentityManager;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.example.fortunecookie.IWisdom;
import org.societies.example.fortunecookieservice.schema.Cookie;
import org.springframework.context.ApplicationListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class EventClient implements Runnable, ApplicationListener<PubsubEvent> {

	private static final String EVENTING_NODE_NAME = "Fortune_Cookies";
	private IIdentityManager idManager;
	private IWisdom fcGenerator;

	public IWisdom getFcGenerator() {
		return fcGenerator;
	}

	public void setFcGenerator(IWisdom fcGenerator) {
		this.fcGenerator = fcGenerator;
	}
	
	public EventClient() {
		idManager = new IdentityManager();
	}
	
	public void run() {
		//FIRST, CREATE THE EVENTING NODE
		Identity pubsubID = idManager.fromJid("XCManager.red.local");
		PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
		PubsubEventStream eventStream = eventFactory.getStream(pubsubID, EVENTING_NODE_NAME);
		
		//SUBSCRIBE TO EVENTS - IMPLEMENT THE ApplicationListerner<PubsubEvent> interface
		eventStream.addApplicationListener(this);
		
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
		
			//GENERATE EVENT
			PubsubEvent event = new PubsubEvent(this, entry);
			eventStream.multicastEvent(event);
		} while (true);
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)*/
	@Override
	public void onApplicationEvent(PubsubEvent eventDetails) {
		System.out.println("New Event raised on node: " + eventDetails.getNode());
		System.out.println("Unique ID: " + eventDetails.getItemId());
		System.out.println("Source service: " + eventDetails.getSource());
		System.out.println("Event Datetime: " + eventDetails.getTimestamp());
		
		Element payload = eventDetails.getPayload();
		String title = payload.getFirstChild().getTextContent();
		String wisdom = payload.getLastChild().getTextContent();
		System.out.println("Details: " + wisdom);
	}	
}
