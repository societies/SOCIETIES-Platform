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
package org.societies.context.location.management.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;
import org.societies.api.internal.css.devicemgmt.comm.EventsType;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.css.devicemanagement.DmEvent;
import org.societies.context.api.user.location.ILocationManagementAdapter;
import org.societies.context.api.user.location.ILocationManagementConfigurator;


public class LMConfiguratorImpl implements ILocationManagementConfigurator{
	/** The logging facility. */
	private static final Logger log = LoggerFactory.getLogger(LMConfiguratorImpl.class);
	
	private PubsubClient pubSubManager; 
	private ICommManager commManager;
	private ICommManagerController commManagerController;
	private IDeviceRegistry deviceRegistry;
	private ILocationManagementAdapter iLocationManagementAdapter; 
	 
	public void init(PubsubClient pubSubManager,ICommManager commManager, IDeviceRegistry deviceRegistry, ICommManagerController commMngrController, ILocationManagementAdapter callback){
		this.iLocationManagementAdapter = callback;
		this.deviceRegistry = deviceRegistry;
		this.pubSubManager = pubSubManager;
		this.commManager = commManager;
		this.deviceRegistry = deviceRegistry;
		this.commManagerController = commMngrController;
		
		registerWithoutPubSub();
		/*
		 * TODO Temp removal of pubSub eventing
		 * register();
		 * 
		 */
	}

	private void registerWithoutPubSub(){
		log.info("start 'registerWithoutPubSub'");
		try{
			String otherNetworkNodes = "";
			for (INetworkNode networkNode : commManagerController.getOtherNodes()){
				iLocationManagementAdapter.registerCSSdevice(networkNode.getJid(),"", "00:00:00:00:00");
				otherNetworkNodes += networkNode.getJid() +" , \t";
			}
			log.info("Related network nodes: "+otherNetworkNodes);
			
			String thisNodeId = commManager.getIdManager().getThisNetworkNode().getJid();
			iLocationManagementAdapter.registerCSSdevice(thisNodeId,"", "00:00:00:00:00");
			
			log.info("This network node: "+thisNodeId);
			
		}catch (Exception e) {
			log.error("Exception msg: "+e.getMessage()+"\t cause: "+e.getCause(),e);
		}
		log.info("finish 'registerWithoutPubSub'");
		
	}
	private DeviceConnected deviceConnected = new DeviceConnected();
	private DeviceDisconnected deviceDisconnected = new DeviceDisconnected();
	
	private void register(){
		try {
			IIdentity identity = commManager.getIdManager().getThisNetworkNode();
			List<String> packageList = new ArrayList<String>();
			packageList.add("org.societies.api.schema.css.devicemanagement");
			pubSubManager.addJaxbPackages(packageList);
			pubSubManager.subscriberSubscribe(identity, EventsType.DEVICE_CONNECTED, deviceConnected);
			pubSubManager.subscriberSubscribe(identity, EventsType.DEVICE_DISCONNECTED, deviceDisconnected);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Error e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private class DeviceConnected implements Subscriber{

		@Override
		public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
			DmEvent dmEvent = null;
			try{
				dmEvent = (DmEvent)item;
				
				DeviceCommonInfo deviceCommonInfo = deviceRegistry.findDevice(dmEvent.getDeviceId());
				String macAddress = deviceCommonInfo.getDevicePhysicalAddress();
				String senderNodeId = dmEvent.getSenderNetworkNode();
				
				iLocationManagementAdapter.registerCSSdevice(senderNodeId, dmEvent.getDeviceId(), macAddress);
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	private class DeviceDisconnected implements Subscriber{

		@Override
		public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
			DmEvent dmEvent = null;
			try{
				dmEvent = (DmEvent)item;
				
				DeviceCommonInfo deviceCommonInfo = deviceRegistry.findDevice(dmEvent.getDeviceId());
				String macAddress = deviceCommonInfo.getDevicePhysicalAddress();
				String senderNodeId = dmEvent.getSenderNetworkNode();
				
				iLocationManagementAdapter.removeCSSdevice(senderNodeId, dmEvent.getDeviceId(), macAddress);
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public IDeviceRegistry getDeviceRegistry() {
		return deviceRegistry;
	}

	public void setDeviceRegistry(IDeviceRegistry deviceRegistry) {
		this.deviceRegistry = deviceRegistry;
	}

	@Override
	public Collection<String> getEntityIds() {
		// TODO Auto-generated method stub
		return null;
	}	

}
