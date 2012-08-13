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

import java.util.Collection;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;
import org.societies.context.api.user.location.ILocationManagementAdapter;
import org.societies.context.api.user.location.IUserLocation;
import org.societies.context.api.user.location.IZone;
import org.societies.context.api.user.location.IZoneId;
import org.societies.context.location.management.PZWrapper;
import org.springframework.beans.factory.annotation.Autowired;


public class LMAdapterImpl implements ILocationManagementAdapter {
	
	//TODO TEMP - replace with config file  //
	private static final int UPDATE_CYCLE = 30*1000;
	
	private final Timer timer = new Timer();
	private LocationManagementContextAccessor locationInference;
	
	private ICtxSourceMgr contextSourceManagement;
	private ICtxBroker contextBroker;
	private ICommManager commManager;
	private PubsubClient pubSubManager; 
	private IDeviceRegistry deviceRegistry;
	private PZWrapper pzWrapper; 
	
	
	@SuppressWarnings("unused")
	private void init(){
		LMConfiguratorImpl lmConfiguratorImpl = new LMConfiguratorImpl();
		lmConfiguratorImpl.init(pubSubManager, commManager, deviceRegistry, this);
		locationInference = new LocationManagementContextAccessor();
		locationInference.init(contextSourceManagement, contextBroker, commManager);
		timer.scheduleAtFixedRate(new UpdateTask(),UPDATE_CYCLE, UPDATE_CYCLE);
	}
	
	@Override
	public Set<String> getActiveEntitiesIdsInZone(IZoneId arg0) {
		return pzWrapper.getActiveEntitiesIdsInZone(arg0);
	}
	

	@Override
	public Collection<IZone> getActiveZones() {
		return pzWrapper.getActiveZones();
	}

	@Override
	public IUserLocation getEntityFullLocation(String entityId) {
		return pzWrapper.getEntityFullLocation(entityId);
		
	}
	
	@Override
	public void registerCSSdevice(String entityId,String deviceId,String macAddress) {
		INetworkNode networkNode;
		try {
			networkNode = commManager.getIdManager().fromFullJid(entityId);
			locationInference.addDevice(networkNode, macAddress);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void removeCSSdevice(String entityId,String deviceId,String macAddress) {
		locationInference.removeDevice(macAddress);
		
	}
	
	int counter = 0;
	private class UpdateTask extends TimerTask{
		@Override
		public void run() {
			try{
				IUserLocation userLocation;
				Collection<String> registeredDevices =  locationInference.getAllRegisteredDevices();
				for (String macAddress : registeredDevices){
					userLocation = getEntityFullLocation(macAddress);
					locationInference.updateCSM(userLocation, macAddress);
				}
				
				if (counter == 0){
					registerCSSdevice(commManager.getIdManager().getThisNetworkNode().getJid(), "aaaaa", "11:11:11:11:11:11");
					counter++;
				}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
		
	/*
	 * Getters / Setters
	 * 
	 */
	public ICtxSourceMgr getContextSourceManagement() {
		return contextSourceManagement;
	}
	
	public void setContextSourceManagement(ICtxSourceMgr contextSourceManagement) {
		this.contextSourceManagement = contextSourceManagement;
	}
	
	public ICtxBroker getContextBroker() {
		return contextBroker;
	}


	public void setContextBroker(ICtxBroker contextBroker) {
		this.contextBroker = contextBroker;
	}
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public PubsubClient getPubSubManager() {
		return pubSubManager;
	}

	public void setPubSubManager(PubsubClient pubSubManager) {
		this.pubSubManager = pubSubManager;
	}

	public IDeviceRegistry getDeviceRegistry() {
		return deviceRegistry;
	}

	public void setDeviceRegistry(IDeviceRegistry deviceRegistry) {
		this.deviceRegistry = deviceRegistry;
	}

	public PZWrapper getPzWrapper() {
		return pzWrapper;
	}

	@Autowired
	public void setPzWrapper(PZWrapper pzWrapper) {
		this.pzWrapper = pzWrapper;
	}
	

}
