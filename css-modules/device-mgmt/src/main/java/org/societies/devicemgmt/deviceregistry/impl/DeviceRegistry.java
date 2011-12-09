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

package org.societies.devicemgmt.deviceregistry.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.devicemgmt.deviceregistry.api.IDeviceRegistry;



public class DeviceRegistry implements IDeviceRegistry {

    //Ensure that HashMap basis of registry is synchronized
    private Map<String, CSSDevice> registry = Collections.synchronizedMap(new HashMap<String, CSSDevice>());
    private static DeviceRegistry instance = new DeviceRegistry();

    

    /**
     * private constructor
     */
    private DeviceRegistry() {

 //       cssPublicIdentifier = null;

    }

    /**
     * Description: public object for retrieving the device registry single
     * instance
     * 
     * @return	instance of the device registry
     */
    public static DeviceRegistry getInstance() {
        return instance;
    }

 

    /**
     * Description: Find all devices registered in the device registry
     * 
     * @return	collection of CSSDevices
     */
    public Collection<CSSDevice> findAllDevices() {
       
        return registry.values();
    }

    

    /**
     * Description: Finds all devices of a given device type 
     * 
     * @return Collection of CSSDevices
     */
    public Collection<CSSDevice> findByDeviceType(String deviceType) {
        
       
        Collection<CSSDevice> typedDevices = new ArrayList<CSSDevice>();

        for (CSSDevice device : registry.values()) {
            if (device.getdeviceType().equals(deviceType)) {
                typedDevices.add(device);
            }
        }
        return (0 != typedDevices.size()) ? typedDevices : null;
    }

   
    /**
     * Description: find a device in the registry given a device ID
     * 
     * @return	CSSDevice
     */
    public CSSDevice findDevice(IDeviceIdentifier deviceID) {
        
    	CSSDevice device = null;

        String key = RegistryUtility.createKeyString(deviceID);

        if (registry.containsKey(key)) {
            device = registry.get(key);
        }
        return device;
    }

    /**
     * Description: Remove a device from the device registry given the deviceID
     * 
     * @return	boolean
     */
    public boolean unregisterDevice(IDeviceIdentifier deviceID) {

        boolean retValue = false;
        String key = RegistryUtility.createKeyString(deviceID);

        if (registry.containsKey(key)) {
            registry.remove(key);
            retValue = true;
        }
        return retValue;
    }

    /**
     * Description:	Clear all the current entries in the device registry
     * 
     * @return
     */

    public void clearRegistry() {
        
        registry.clear();

    }

    /**
     * Description:	Get the number of devices currently in the device registry
     * 
     * @return	int
     */

    public int registrySize() {
       
        return registry.size();
    }

     
    /**
     * Description:	Add a new device to the device registry
     * 
     * @return	IDeviceIdentifier
     */
	public IDeviceIdentifier addDevice(CSSDevice device, Object CSSID) {
    	registry.put(RegistryUtility.createKeyString(device
                .getDeviceId()), device);
        return device.getDeviceId();
		
	}

	/**
     * Description:	Remove a currently registered device from the device registry
     * 
     * @return	boolean
     */
	public boolean deleteDevice(CSSDevice device, Object CSSID) {
		boolean retValue = false;
        String key = RegistryUtility.createKeyString(device.getDeviceId());

        if (registry.containsKey(key)) {
            registry.remove(key);
            retValue = true;
        }
        return retValue;
		
	}
	
	/**
     * Description:	Given a deviceID find all the services associated with that device
     * 
     * @return	Collection
     */

	@Override
	public Collection<Object> getDeviceServiceList(Object deviceID) {
		// TODO Auto-generated method stub
		return null;
	}
}

