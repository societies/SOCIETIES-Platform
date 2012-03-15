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

import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.css.devicemanagment.DmEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;

/**
 * 
 * Describe your class here...
 *
 * @author guyf@il.ibm.com
 *
 */
public class CommAdapterImpl implements DmCommManager{

	public static final String DEVICE_CONNECTED = "DEVICE_REGISTERED";
	public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
	public static final String DATA_CAHNGED_PREFIX_EVENT = "DEVICE_DATA_CAHNGED_PREFIX";
	public static final String JID = "XCManager.societies.local";
	public static final String SCHEMA = "org.societies.api.schema.css.devicemanagement";

	private IIdentityManager idManager;
	private ICommManager commManager;
	
	@Override
	public void fireNewDeviceConnected(String deviceId,DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		sendEvent(DEVICE_CONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDisconnected(String deviceId,	DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		sendEvent(DEVICE_DISCONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDataChanged(String deviceId,DeviceCommonInfo deviceCommonInfo, String key,String value) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		dmEvent.setKey(key);
		dmEvent.setValue(value);
		String eventNodeId = DATA_CAHNGED_PREFIX_EVENT+deviceId;
		sendEvent(eventNodeId, dmEvent);
	}
	
	private DmEvent generateEvent(String deviceId, DeviceCommonInfo deviceCommonInfo){
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceId);
		dmEvent.setDescription(deviceCommonInfo.getDeviceDescription());
		dmEvent.setType(deviceCommonInfo.getDeviceType());
		return dmEvent;
	}
	
	private void sendEvent(String type, DmEvent dmEvent){
		try{
			idManager = commManager.getIdManager();
			IIdentity pubsubID;
			pubsubID = idManager.fromJid(JID);
			
			PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
			PubsubEventStream eventStream=null;

			eventStream = eventFactory.getStream(pubsubID, type);
			//ADD LIST OF PACKAGES TO SUPPORT SCHEMA OBJECTS
			List<String> packageList = new ArrayList<String>();
			packageList.add(SCHEMA);
			
			try {
				eventStream.addJaxbPackages(packageList);
			} catch (JAXBException e1) {
				//ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
				e1.printStackTrace();
			}
			
			//GENERATE EVENT
			PubsubEvent event = new PubsubEvent(this, dmEvent);
			eventStream.multicastEvent(event);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
}

