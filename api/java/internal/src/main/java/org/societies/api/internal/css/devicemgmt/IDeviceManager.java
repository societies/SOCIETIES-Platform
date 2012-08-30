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
package org.societies.api.internal.css.devicemgmt;

import java.util.Dictionary;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
/**
 * Interface used by the device deriver bundles to inform the device manager about a state of devices
 *
 * @author Rafik
 *
 */
public interface IDeviceManager {
	


	/**
	 * 
	 * @param physicalDeviceId
	 * @param deviceCommonInfo
	 * @param serviceIds
	 * @return a deviceId
	 */
	public String fireNewDeviceConnected (String physicalDeviceId, DeviceCommonInfo deviceCommonInfo, String [] serviceNames);
	
	/**
	 * Method used to inform the Device Manager about disconnection of a device
	 * 
	 * @param deviceFamily
	 * @param deviceMacAddress
	 */
	public String fireDeviceDisconnected (String deviceFamily, String physicalDeviceId);
	
	/**
	 * 
	 * Method used to inform the Device Manager about new data received for a given device
	 * @param deviceFamily
	 * @param physicalDeviceId
	 * @param data is a dictionary that can contains information about battery level change, device location change, value change and so on
	 */
	public String fireNewDataReceived (String deviceFamily, String physicalDeviceId, Dictionary<String, Object> data);
	
	
	/**
	 * BJB : 30/08/2012, (remove this when tested)
	 * Method used to inform the Device Manager about a new remote device shared through SLM. The DM will create a local OSGi IDevice service to represent this remote device 
	 * @param deviceCommonInfo
	 * @param deviceNodeId: the JID of the CSS Node on which the device is connected.
	 * @return a deviceId (or null)
	 */
	public String fireNewSharedDevice (DeviceCommonInfo deviceCommonInfo, IIdentity deviceNodeId);
	
	/**
	 * BJB : 30/08/2012 (remove this once tested)
	 * Method used to inform the Device Manager about an uninstall of shared device through SLM. The DM will destroy the local OSGi IDevice service 
	 * @param a deviceId
	 * @return true or false. False if the deviceId does not exist.
	 */
	public boolean fireDisconnectedSharedDevice (String deviceId);
	
}
