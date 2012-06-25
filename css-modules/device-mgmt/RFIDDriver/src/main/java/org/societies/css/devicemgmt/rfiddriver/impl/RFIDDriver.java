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
package org.societies.css.devicemgmt.rfiddriver.impl;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.societies.api.css.devicemgmt.rfid.IRfidDriver;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.osgi.event.IEventMgr;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class RFIDDriver implements IRfidDriver {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	Hashtable<String, SocketClient> sockets;
	private final String DEVICE_FAMILY_IDENTITY = "org.societies.css.devicemgmt.RFIDDriver";
	private final String DEVICE_NAME = "RFID_READER";
	private final String DEVICE_DESCR = "RFID Location Management System";
	private final String DEVICE_PROVIDER = "HWU";
	
	public void initialiseRFIDDriver() {
		
		sockets = new Hashtable<String, SocketClient>();
		
		
	}
	
	private IEventMgr eventMgr;
	private IDeviceManager deviceMgr;
	/* (non-Javadoc)
	 * @see org.societies.api.css.devicemgmt.rfid.IRfidDriver#connect(java.lang.String)
	 */
	@Override
	public void connect(String ipAddress) {
		if (sockets.containsKey(ipAddress)){
			this.logging.debug("Already connected to: "+ipAddress);
			return;
		}

		SocketClient socketClient = new SocketClient(ipAddress);
		if (socketClient.checkIp(ipAddress)){
			socketClient.setEventMgr(eventMgr);
			socketClient.start();
			DeviceCommonInfo deviceCommonInfo = new DeviceCommonInfo(DEVICE_FAMILY_IDENTITY, 
					DEVICE_NAME,
					DeviceTypeConstants.RFID_READER,
					DEVICE_DESCR, 
					"ETHERNET", 
					"LearningZone", 
					DEVICE_PROVIDER, 
					null, 
					false);
			this.deviceMgr.fireNewDeviceConnected(ipAddress, deviceCommonInfo, new String[]{DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE});
			this.sockets.put(ipAddress, socketClient);
		}else{
			this.logging.error(ipAddress+" not valid. ignoring request");
		}
		
	}
	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}
	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}
	/**
	 * @return the deviceMgr
	 */
	public IDeviceManager getDeviceMgr() {
		return deviceMgr;
	}
	/**
	 * @param deviceMgr the deviceMgr to set
	 */
	public void setDeviceMgr(IDeviceManager deviceMgr) {
		this.deviceMgr = deviceMgr;
	}

}
