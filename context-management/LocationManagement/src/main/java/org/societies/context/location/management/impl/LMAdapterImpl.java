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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.context.api.user.location.ILocationManagementAdapter;
import org.societies.context.api.user.location.ITag;
import org.societies.context.api.user.location.IUserLocation;
import org.societies.context.api.user.location.IZone;
import org.societies.context.api.user.location.IZoneId;
import org.societies.context.api.user.location.impl.CoordinateImpl;
import org.societies.context.api.user.location.impl.TagImpl;
import org.societies.context.api.user.location.impl.UserLocationImpl;
import org.societies.context.api.user.location.impl.ZoneIdImpl;
import org.societies.context.api.user.location.impl.ZoneImpl;
import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;


public class LMAdapterImpl implements ILocationManagementAdapter {
	
	//TODO TEMP - replace with config file  //
	private static final int UPDATE_CYCLE = 30*1000;
	
	private MockPZWrapperImpl pzWrapperImpl = new MockPZWrapperImpl();
	private final Timer timer = new Timer();
	private LocationManagementContextAccessor locationInference;
	
	private ICtxSourceMgr contextSourceManagement;
	private ICtxBroker contextBroker;
	private ICommManager commManager;
	
	
	private void init() throws InterruptedException, ExecutionException{
		locationInference = new LocationManagementContextAccessor();
		locationInference.init(contextSourceManagement, contextBroker, commManager);
		timer.scheduleAtFixedRate(new MockUpdateTask(), 0, UPDATE_CYCLE);
	}
	
	@Override
	public Set<String> getActiveEntitiesIdsInZone(IZoneId arg0) {
		return pzWrapperImpl.getActiveEntitiesIdsInZone((int)arg0.getId());
	}

	@Override
	public Collection<IZone> getActiveZones() {
		Collection<MockPZWrapperImpl.Zone> mockZones = pzWrapperImpl.getActiveZones();
		
		Collection<IZone> zones = new ArrayList<IZone>();
		
		IZone zone;
		for (MockPZWrapperImpl.Zone mockZone : mockZones){
			zone = convert(mockZone);
			zones.add(zone);
		}
		return zones; 
	}

	@Override
	public IUserLocation getEntityFullLocation(String entityId) {
		IUserLocation userLocation = new UserLocationImpl();
		
		MockPZWrapperImpl.Location location = pzWrapperImpl.getEntityFullLocation(entityId);
		userLocation.setXCoordinate(new CoordinateImpl(location.getX().doubleValue()));
		userLocation.setYCoordinate(new CoordinateImpl(location.getY().longValue()));
		
		List<IZone> zones = new ArrayList<IZone>();
		for (MockPZWrapperImpl.ExZone exZone : location.getZones()){
			IZone zone = convert(exZone);
			zones.add(zone);
		}
		userLocation.setZones(zones);
		return userLocation;
	}
	
	private IZone convert(MockPZWrapperImpl.ExZone mockZone){
		IZone zone = convert((MockPZWrapperImpl.Zone)mockZone);
		zone.setPersonalTag(new TagImpl(mockZone.getPersonalTag()));
		
		List<ITag> tags = new ArrayList<ITag>();
		for (String tag : mockZone.getTags()){
			tags.add(new TagImpl(tag));
		}
		zone.setTags(tags);
		
		return zone;
	}
	
	
	
	private IZone convert(MockPZWrapperImpl.Zone mockZone){
		IZone zone = new ZoneImpl();
		
		IZoneId zoneId = new ZoneIdImpl(); 
		zoneId.setId(mockZone.getZoneId());
		
		zone.setDescription(mockZone.getDescription());
		zone.setName(mockZone.getName());
		zone.setType(mockZone.getType());
		
		return zone;
	}

	@Override
	public void registerCSSdevice(String entityId,String deviceId,String macAddress) {
		// perform registration
		
	}

	@Override
	public void removeCSSdevice(String entityId,String deviceId,String macAddress) {
		//remove device
		
	}
	
	private class MockUpdateTask extends TimerTask{

		@Override
		public void run() {
			
			try{
				System.out.println("here");
				IUserLocation userLocation;
				for (IZone iZone : getActiveZones()){
					for (String entityId : getActiveEntitiesIdsInZone(iZone.getId())){
						userLocation = getEntityFullLocation(entityId);
						locationInference.updateCSM(userLocation);
					}
				}
				
					
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
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

}
