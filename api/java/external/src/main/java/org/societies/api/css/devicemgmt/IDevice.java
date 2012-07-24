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

package org.societies.api.css.devicemgmt;

import java.util.List;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * Interface used to expose a device as an OSGI Service, this interface will be used by a device consumer to get device metadata and to get device services
 * @author Rafik (Trialog)
 *
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IDevice {

	/**
	 * 
	 * @return A device name
	 */
    public String getDeviceName();
    
    /**
	 * @return A device node Id
	 */
    public String getDeviceNodeId();
   
    /**
     * 
     * @return A device ID
     */
    public String getDeviceId();
    
    /**
     * 
     * @return a device type
     */
    public String getDeviceType();
    
    /**
     * 
     * @return a device description
     */
    public String getDeviceDescription();
    
    /**
     * 
     * @return return a device connection type (e.g. Zigbee, Wi-Fi and so on)
     */
    public String getDeviceConnectionType();
    
    /**
     * 
     * @return true if the device is enable else false
     */
    public boolean isEnable();
    
    /**
     * 
     * @return a device physical location (e.g. "room1")
     */
    public String getDeviceLocation();
    
    /**
     * 
     * @return a device provider (e.g. "Trialog")
     */
    public String getDeviceProvider();
    
    /**
     * 
     * @return true if the device can be used by the context management
     */
    public boolean isContextSource();
    
    
    /**
     * 
     * @param serviceName
     * @return a service provided by the driver that provides this device
     */
    public IDriverService getService (String serviceName);
    
    
    /**
     * 
     * @return All services provided by the driver that provides this device 
     * 
     */
    public IDriverService[] getServices ();
    
    
    /**
	 * 
	 * @return a list of event name to be used to subscribe to the events published by this device
	 */
	public List<String> getEventNameList();
}