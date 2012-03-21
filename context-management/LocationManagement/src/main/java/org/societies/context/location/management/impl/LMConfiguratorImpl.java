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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.societies.api.internal.css.devicemgmt.comm.EventsType;
import org.societies.api.schema.css.devicemanagment.DmEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.context.api.user.location.ILocationManagementConfigurator;
import org.springframework.context.ApplicationListener;

public class LMConfiguratorImpl implements ILocationManagementConfigurator{

	private static final Set<String> entityIds = new HashSet<String>();
	private DevicesListener devicesListener = new DevicesListener();
	
	public LMConfiguratorImpl(){
		
		//TODO change - for testing
		synchronized (entityIds) {
			entityIds.add("1");
			entityIds.add("2");	
		}
	}
	
	public Collection<String> getEntityIds(){
		List<String> lEntityIds = new ArrayList<String>();
		synchronized (entityIds) {
			lEntityIds.addAll(entityIds);
		}
		return lEntityIds;
	}
	
	private class DevicesListener implements ApplicationListener<PubsubEvent>{
		@Override
		public void onApplicationEvent(PubsubEvent arg0) {
			DmEvent dmEvent;
			
			//TODO change to MAC address and entity id
			if (EventsType.DEVICE_CONNECTED.equals(arg0.getNode())){
				dmEvent = (DmEvent)arg0.getPayload();
				synchronized (dmEvent) {
					entityIds.add(dmEvent.getDeviceId());	
				}
				
				
			}else if (EventsType.DEVICE_DISCONNECTED.equals(arg0.getNode())){
				dmEvent = (DmEvent)arg0.getPayload();
				synchronized (dmEvent) {
					entityIds.remove(dmEvent.getDeviceId());	
				}
			}
		}
	}

}
