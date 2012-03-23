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

package org.societies.css.devicemgmt.deviceregistry;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.css.devicemgmt.deviceregistry.*;
import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.css.devicemgmt.DeviceCommsMgr.impl.CommAdapterImpl;
//import org.societies.css.devicemgmt.DeviceCommsMgr.impl.CommAdapterImpl;
//import org.societies.css.devicemgmt.devicemgmtold.deviceregistry.api.IDeviceRegistry;




public class DeviceRegistry implements IDeviceRegistry {

    //Ensure that HashMap basis of registry is synchronized
    private Map<String, DeviceCommonInfo> registry = Collections.synchronizedMap(new HashMap<String, DeviceCommonInfo>());
    private static DeviceRegistry instance = new DeviceRegistry();
    
    private DmCommManager dmCommManager;
    

    //private static DmCommManager instance = new dmCommManager();

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
    public Collection<DeviceCommonInfo> findAllDevices() {
       
        return registry.values();
    }

    

    /**
     * Description: Finds all devices of a given device type 
     * 
     * @return Collection of CSSDevices
     */
    public Collection<DeviceCommonInfo> findByDeviceType(String deviceType) {
        
       
        Collection<DeviceCommonInfo> typedDevices = new ArrayList<DeviceCommonInfo>();

        for (DeviceCommonInfo device : registry.values()) {
            if (device.getDeviceType().equals(deviceType)) {
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
    public DeviceCommonInfo findDevice(String deviceID) {
        
    	DeviceCommonInfo device = null;

        //String key = RegistryUtility.createKeyString(deviceID);
        String key = deviceID;

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
    public boolean unregisterDevice(String deviceID) {

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
     * @return	deviceID
     */
	public String addDevice(DeviceCommonInfo device, String CSSNodeID) {
    	//registry.put(RegistryUtility.createKeyString(device.getdeviceId()), device);
		registry.put(device.getDeviceID(), device);
		//fireNewDeviceConnected(device.getDeviceID(),device);
		fireNewDeviceConnected(device.getDeviceID(), device);
		
		
		//dmCommManager.fireNewDeviceConnected(device.getDeviceID(), device);
			//dmCommManager = DmCommManager.class.newInstance();
		
        return device.getDeviceID();
		
	}

	/**
     * Description:	Remove a currently registered device from the device registry
     * 
     * @return	boolean
     */
	public boolean deleteDevice(DeviceCommonInfo device, String CSSNodeID) {
		boolean retValue = false;
        String key = RegistryUtility.createKeyString(device.getDeviceID());

        if (registry.containsKey(key)) {
            registry.remove(key);
            retValue = true;
        }
        fireDeviceDisconnected(device.getDeviceID(), device);      	
      	
        return retValue;
		
	}
	
	/**
     * Description:	Given a deviceID find all the services associated with that device
     * 
     * @return	Collection
     */


	public Collection<Object> getDeviceList(Object deviceID) {
		// TODO Auto-generated method stub
		return null;
	}

	public void fireNewDeviceConnected(String deviceID,
			DeviceCommonInfo deviceCommonInfo) {
		System.out.println("fireNewDeviceConnected " + deviceCommonInfo.getDeviceID());
		//dmCommManager.fireNewDeviceConnected(deviceCommonInfo.getDeviceID(), deviceCommonInfo);
	}

	
	public void fireDeviceDisconnected(String deviceID,
			DeviceCommonInfo deviceCommonInfo) {
		System.out.println("fireDeviceDisconnected " + deviceCommonInfo.getDeviceID());
		//dmCommManager.fireDeviceDisconnected(deviceCommonInfo.getDeviceID(), deviceCommonInfo);
	}

	
	public void fireDeviceDataChanged(String deviceId,
			DeviceCommonInfo deviceCommonInfo, String key, String value) {
		
		
	}
}

