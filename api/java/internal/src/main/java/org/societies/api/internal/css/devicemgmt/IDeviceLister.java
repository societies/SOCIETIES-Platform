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

import java.util.List;

import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

/**
 * This interface is used to browse devices information from the device persistent storage
 *
 * @author Rafik
 *
 */
public interface IDeviceLister {
	
	/**
	 * 
	 * @return a list of all devices connected to the CSS
	 */
	public List<DeviceCommonInfo> getAllDevices();
	

	/**
	 * This method is used to search for a device by the id of the node to which this device is connected
	 * @param nodeId
	 * @return a list of all devices connected to the given CSS node
	 */
	public List<DeviceCommonInfo> getDeviceByNodeId(String nodeId);
	
	/**
	 * This method is used to search for a device by its type
	 * @param deviceType
	 * @return a list of devices with a type 'deviceType' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByType(String deviceType);
	
	/**
	 * This method is used to search for a device by the id of the node to which this device is connected and its type
	 * @param deviceType
	 * @return a list of devices with a type 'deviceType' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByType(String nodeId, String deviceType);
	
	/**
	 * This method is used to search for a device by its name
	 * @param deviceName
	 * @return a list of devices with a name 'deviceNam' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByName (String deviceName);
	
	/**
	 * This method is used to search for a device by its name
	 * @param deviceName
	 * @return a list of devices with a name 'deviceNam' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByName (String nodeId, String deviceName);
	
	/**
	 * This method is used to search for a device by its device id
	 * @param deviceId
	 * @return a device with an id 'deviceId' if any else null
	 */
	public DeviceCommonInfo getDeviceById (String deviceId);
	
	/**
	 * This method is used to search for a device by its category
	 * @param deviceCategory
	 * @return a list of devices with a category 'deviceCategory' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByCategory(String deviceCategory);
	
	/**
	 * This method is used to search for a device by its location
	 * @param deviceLocation
	 * @return a list of devices with a location 'deviceLocation' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByLocation(String deviceLocation);
	
	/**
	 * This method is used to search for a device by its provider
	 * @param deviceProvider
	 * @returna a list of devices with a provider 'deviceProvider' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByProvider(String deviceProvider);
	
	/**
	 * This method is used to search for a device by its connection type
	 * @param deviceConnectionType
	 * @return a list of devices with a connection type 'deviceConnectionType' if any else null
	 */
	public List<DeviceCommonInfo> getDeviceByConnectionType(String deviceConnectionType);
	
	
	
	
	

}
