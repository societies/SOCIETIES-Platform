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

package org.societies.css.devicemgmt.DeviceCommsMgr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.css.devicemanagement.DmEvent;
import org.springframework.context.ApplicationListener;


public class CommAdapterTestImpl implements DmCommManager,Subscriber{
	
	private IIdentityManager idManager;
	private ICommManager commManager;
	public static final String SCHEMA = "org.societies.api.schema.css.devicemanagment";
	private PubsubClient pubSubManager;  
	
	


	private String EVENTING_NODE_NAME = "GUY";
	
	int delay = 1000*20;   // delay for 5 sec.
	int period = 1000*20;  // repeat every sec.
	Timer timer = new Timer();
	
	PubsubClient pubsubClient;
	
	
	public CommAdapterTestImpl(){}
	
	@PostConstruct 
	private void init(){
		idManager = commManager.getIdManager();
		IIdentity pubsubID = null;
		
		try {
			//we can add "."
			//idManager.getThisNetworkNode().getJid();
			
			pubsubID = idManager.fromJid("XCManager.societies.local");
			pubSubManager.ownerCreate(pubsubID, EVENTING_NODE_NAME);
		} catch (InvalidFormatException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (XMPPError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	        	try{
	        		sendEvent();
	        	}catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }, delay, period);
	}
	
	public void fireNewDeviceConnected(String deviceID, DeviceCommonInfo deviceCommonInfo){
		
		
	}
	
	public void fireDeviceDisconnected(String deviceID, DeviceCommonInfo deviceCommonInfo){	
		
	}

	
	public void fireDeviceDataChanged(String deviceId,DeviceCommonInfo deviceCommonInfo, String key,String value){
		
	}

	private void sendEvent(){
		
		try{
			idManager = commManager.getIdManager();
			IIdentity pubsubID = null;
			
			try {
				//we can add "."
				//idManager.getThisNetworkNode().getJid();
				
				pubsubID = idManager.fromJid("XCManager.societies.local");
				//pubSubManager.ownerCreate(pubsubID, EVENTING_NODE_NAME);
			} catch (InvalidFormatException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			List<String> packageList = new ArrayList<String>();
			packageList.add(SCHEMA);
			try {
				pubSubManager.addJaxbPackages(packageList);
			} catch (JAXBException e1) {
				//ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
				e1.printStackTrace();
			}
			
			DmEvent dmEvent = new DmEvent();
			dmEvent.setDeviceId("123456");
			dmEvent.setDescription("aaaa");
			dmEvent.setType("bbb");
			
			
			pubSubManager.subscriberSubscribe(pubsubID, EVENTING_NODE_NAME, this);
			String published = pubSubManager.publisherPublish(pubsubID, EVENTING_NODE_NAME, "Guy", dmEvent);
			System.out.println(published);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		/*
		idManager = commManager.getIdManager();
		IIdentity pubsubID = null;
		try {
			//we can add "."
			//idManager.getThisNetworkNode().getJid();
			
			pubsubID = idManager.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
		PubsubEventStream eventStream=null;
		try{
			eventStream = eventFactory.getStream(pubsubID, EVENTING_NODE_NAME);
			System.out.println("Here");
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}catch(Error e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		//ADD LIST OF PACKAGES TO SUPPORT SCHEMA OBJECTS
		List<String> packageList = new ArrayList<String>();
		packageList.add(SCHEMA);
				
		try {
			eventStream.addJaxbPackages(packageList);
		} catch (JAXBException e1) {
			//ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
			e1.printStackTrace();
		}
		
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId("123456");
		dmEvent.setDescription("aaaa");
		dmEvent.setType("bbb");
		
		
	

		eventStream.addApplicationListener(this);
		//GENERATE EVENT
		PubsubEvent event = new PubsubEvent(this, dmEvent);
		eventStream.multicastEvent(event);
		*/
	}
	
	
/*
	@Override
	public void onApplicationEvent(PubsubEvent arg0) {
		System.out.println(arg0.getTimestamp());
		System.out.println(arg0.getSource());
		DmEvent dmEvent = null;
		try{
			dmEvent = (DmEvent)arg0.getPayload();
			System.out.println(dmEvent.getDescription());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
*/
	
	public static void main(String[] agrgs){
		CommAdapterTestImpl commAdapterImpl = new CommAdapterTestImpl();
		commAdapterImpl.sendEvent();
	}

	
	public PubsubClient getPubSubManager() {
		return pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}


	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}


	@Override
	public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
		DmEvent dmEvent = null;
		try{
			dmEvent = (DmEvent)item;
			System.out.println(dmEvent.getDescription());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
