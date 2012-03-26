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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.example.calculator.ICalc;
import org.societies.example.IExamplesCallback;
import org.societies.example.calculator.ICalcRemote;
import org.societies.example.fortunecookie.IWisdom;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class ClientTester implements IExamplesCallback {

	private ICommManager commManager;
	private ICalcRemote remoteCalculator;
	private PubsubClient pubSubManager;
	private IWisdom fcGenerator;
	private IEventMgr eventMgr;

	private static Logger LOG = LoggerFactory.getLogger(ClientTester.class);

	public IEventMgr getEventMgr() { return eventMgr; }
	public void setEventMgr(IEventMgr eventMgr) { this.eventMgr = eventMgr; }
	
	public ICommManager getCommManager() { return commManager;	}
	public void setCommManager(ICommManager commManager) { this.commManager = commManager; }

	public ICalcRemote getRemoteCalculator() { return remoteCalculator; }
	public void setRemoteCalculator(ICalcRemote remoteCalculator) { this.remoteCalculator = remoteCalculator; }
	
	public PubsubClient getPubSubManager() { return this.pubSubManager; }
	public void setPubSubManager(PubsubClient pubSubManager) { this.pubSubManager = pubSubManager; 	}

	public IWisdom getFcGenerator() { return fcGenerator; }
	public void setFcGenerator(IWisdom fcGenerator) { this.fcGenerator = fcGenerator; }

	
	//ENTRY POINT
	public void StartTest() {
		//TEST OSGI EVENTING
		//System.out.println("Starting OSGI Eventing Test");
		TestInternalEventing springTest = new TestInternalEventing();
		springTest.setEventMgr(eventMgr);
		Thread springThread = new Thread(springTest);
		springThread.start();
		
		//TEST MESSAGING
		//System.out.println("Starting Client Test");
		//getRemoteCalculator().Add(2, 3, this);
		//System.out.println("Waiting...");
		
		//TEST PUBSUB
		//TestExternalEventing testPubSub = new TestExternalEventing();
		//testPubSub.setFcGenerator(this.fcGenerator);
		//testPubSub.setPubSubManager(this.pubSubManager);
		//testPubSub.setCommManager(this.commManager);
		//Thread pubsubThread = new Thread(testPubSub);
		//pubsubThread.start();
	}
	
	/* (non-Javadoc)
	 * @see org.societies.comm.examples.commsmanager.ICalcRemoteCallback#receiveCalcResult(java.lang.Object) */
	@Override
	public void receiveExamplesResult(Object calcResult) {
		int result = (Integer)calcResult;
		System.out.println(result);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("I am doing other stuffs while waiting for asynch reply");
	}
}
