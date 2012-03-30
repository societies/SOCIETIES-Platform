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

import java.util.Collection;
import java.util.List;

import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
//import org.societies.css.devicemgmt.deviceregistry.CSSDevice;

/**
 * Interface used to add and remove devices from the Device Registry. It can also be used to find devices within the registry 
 * whether all devices or particular types of devices
 *
 * @author Liam
 *
 */
public interface IDeviceRegistry {
	
	/*
	 * Description:		Add new device to the device registry
	 * 
	 * @param 			<DeviceCommonInfo>
     * @param 			CSSNodeID
	 * 				
	 * @return 			String
	 */
	public String addDevice (DeviceCommonInfo device, String CSSNodeID);

	/* 
	 * Description: 	Remove device from the device registry
	 * 
	 * @param 			<DeviceCommonInfo>
     * @param 			CSSNodeID				
	 * @return 			boolean
	 */

	public boolean deleteDevice (DeviceCommonInfo device, String CSSNodeID);
	
	/* 
	 * Description:		Search the device registry for a particular device given the deviceID 
	 * 
	 * @param 			deviceID
     * 
	 * @return 			DeviceCommonInfo
	 */

	public DeviceCommonInfo findDevice(String deviceID);
	
	/* 
	 * Description: 	Given a particular deviceID get all the services associated with that device 
	 * @return 			collection
	 */
	//public Collection<DeviceCommonInfo> getDeviceList (String deviceID);
	
	/* 
	 * Description: 	Search the device registry for all devices registered 
	 * 
	 * @return 			Collection<DeviceCommonInfo>
	 */
	
	public Collection<DeviceCommonInfo> findAllDevices();
	
	
	/* 
	 * Description:		Search the device registry for a particular device type 
	 * 
	 * @param 			deviceType
	 * @return 			collection of device types
	 */
	public Collection<DeviceCommonInfo> findByDeviceType(String deviceType);
	
	
	/* 
	 * Description:		Clear the device registry of all entries
	 * @return 			void
	 */
    public void clearRegistry();

    /* 
	 * Description:		Determine the number of devices registered in the device registry
	 * 
	 * @return 			int
	 */
    public int registrySize();
}
