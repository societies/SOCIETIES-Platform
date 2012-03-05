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

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.example.fortunecookie.IWisdom;
import org.societies.example.fortunecookieservice.schema.Cookie;
import org.springframework.context.ApplicationListener;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestInternalExternalEventing implements Runnable, ApplicationListener<PubsubEvent> {

	private static final String EVENTING_NODE_NAME = "Fortune_Cookies";
	private IIdentityManager idManager;
	private IWisdom fcGenerator;
	private ICommManager commManager;
	
	public IWisdom getFcGenerator() { return fcGenerator; }
	public void setFcGenerator(IWisdom fcGenerator) {this.fcGenerator = fcGenerator; }
	
	public ICommManager getCommManager() { return commManager; }
	public void setCommManager(ICommManager commManager) { this.commManager = commManager; }
	
	public TestInternalExternalEventing() { }
	
	public void run() {
		//GET IDENTITY MANAGER
		idManager = commManager.getIdManager();
		IIdentity pubsubID = null;
		try {
			pubsubID = idManager.fromJid("XCManager.societies.local");
			
			//FIRST, CREATE THE EVENTING NODE
			PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
			PubsubEventStream eventStream = eventFactory.getStream(pubsubID, EVENTING_NODE_NAME);
		
			//ADD LIST OF PACKAGES TO ADD SCHEMA OBJECTS
			List<String> packageList = new ArrayList<String>();
			packageList.add("org.societies.comm.examples.calculatorbean");
			packageList.add("org.societies.comm.examples.fortunecookiebean");
			eventStream.addJaxbPackages(packageList);
					
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
						
				//GENERATE EVENT
				PubsubEvent event = new PubsubEvent(this, cookie);
				eventStream.multicastEvent(event);
				
			} while (true);
		} catch (InvalidFormatException formatEx) {
			formatEx.printStackTrace(); //Error with the Jabber ID, check format
		} catch (JAXBException e) {
			e.printStackTrace();		//ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)*/
	@Override
	public void onApplicationEvent(PubsubEvent eventDetails) {
		System.out.println("New Event raised on node: " + eventDetails.getNode());
		System.out.println("Unique ID: " + eventDetails.getItemId());
		System.out.println("Source service: " + eventDetails.getSource());
		System.out.println("Event Datetime: " + eventDetails.getTimestamp());
		
		Cookie cookie = (Cookie)eventDetails.getPayload();
		String wisdom = cookie.getValue();
		System.out.println("Details: " + wisdom);
	}	
}
