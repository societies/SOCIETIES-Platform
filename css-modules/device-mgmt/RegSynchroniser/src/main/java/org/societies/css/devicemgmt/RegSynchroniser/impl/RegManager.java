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

package org.societies.css.devicemgmt.RegSynchroniser.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.LogManager;

import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.osgi.context.BundleContextAware;

import org.societies.css.devicemgmt.deviceregistry.DeviceRegistry;
import org.societies.css.devicemgmt.deviceregistry.CSSDevice;
import org.societies.css.devicemgmt.deviceregistry.IDeviceRegistry;


//import sun.rmi.runtime.Log;

public class RegManager implements BundleContextAware{

	private static org.apache.commons.logging.Log LOG = LogFactory.getLog(RegManager.class);
    private IDeviceRegistry deviceRegistry;
    public static String EVENT_INFO = "event_info";
    private BundleContext bundleContext;
    
    

    /**
     * Constructor
     * 
     * @param context
     */
    public RegManager(BundleContext context) {
                
        //Log("Synchroniser Manager created", this.LOG);
        
    	this.bundleContext = context;
        
        this.deviceRegistry = DeviceRegistry.getInstance();

    }

    public void initiateSearch() {
    }

    /**
     * Register an events listener with the container
     * 
     * @param listener
     * @param filterOption
     */
/*
    public boolean registerEventListener() {
        boolean retValue = false;
        
        String eventTypes[] = { EventTypes.REMOVED_SERVICE_EVENT,
                EventTypes.NEW_SERVICE_EVENT, EventTypes.PSS_ADV_EVENT, EventTypes.SERVICE_LIFECYCLE_EVENT };

        IEventMgr eventsManager = null;
        try {
            eventsManager = this.serviceFinder.getEventsManager();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        if (null != eventsManager) {
            eventsManager.registerListener(
                    new RegistrySynchroniserEventListener(this.bundleContext),
                    eventTypes, null);
            retValue = true;
            
        }

        return retValue;
    }
*/
    /**
     * 
     * 
     * @param deviceId
     */
    public String[] finddeviceFullDetails(String deviceId) {
        

        return new String[1];

    }

    /**
     * Add a device to the device Registry, determine the status of the
     * device (device Locator) and publish an event to notify the peer(s) that
     * a new device has been added. If the device is public inform the ONM of
     * the new device, if the current device is the controller node, to add to
     * the Peer Group advertisement
     * 
     * @param device
     */
    public boolean addDevice(CSSDevice device) throws Exception {

        boolean retValue = false;

        
        LocalDevices.addDevice(device);
        
        return retValue;
    }

    /**
     * Convenience method to add a collection of devices
     */
    public boolean addDevices(Collection<CSSDevice> deviceCollection)
            throws Exception {
        boolean retValue = true;

        for (CSSDevice device : deviceCollection) {
            if (!this.addDevice(device)) {
                retValue = false;
                break;
            }
        }
        return retValue;
    }

    /**
     * Remove a device
     * 
     * @param device
     */
    public boolean removeDevice(String deviceID)
            throws Exception {
        

        return LocalDevices.removeDevice(deviceID);
    }

    /**
     * Convenience method to remove a collection of devices
     */
    public boolean removeDevices(
            Collection<String> deviceCollection)
            throws Exception {

        boolean retValue = true;

        for (String deviceId : deviceCollection) {
            if (!this.removeDevice(deviceId)) {
                retValue = false;
                break;
            }
        }
        return retValue;
    }

    public void initiateUpdate() {
        // TODO Auto-generated method stub

    }

    /**
     * Clear the registry
     */
    public boolean clearRegistry() throws Exception {
        boolean retValue = false;

        this.deviceRegistry.clearRegistry();

        if (0 == this.deviceRegistry.registrySize()) {
            retValue = true;
        }
        return retValue;
    }

	@Override
	public void setBundleContext(BundleContext arg0) {
		// TODO Auto-generated method stub
		
	}
}
