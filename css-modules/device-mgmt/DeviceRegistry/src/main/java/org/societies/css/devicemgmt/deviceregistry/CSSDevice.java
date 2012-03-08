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


//package src.main.java.org.societies.css.devicemgmt.deviceregistry;
package org.societies.css.devicemgmt.deviceregistry;
import java.io.Serializable;
import java.util.Properties;


public class CSSDevice implements Serializable{
	
	private static final long serialVersionUID = 1L;

    /**
     * A human-readable name of the device
     */
    private String deviceName;

    /**
     * A human-readable description of this device
     */
    private String deviceDescription;
    
    /**
     * The id of this device
     */
    private String deviceId;
    
       
    /**
     * The type of this device
     */
    private String deviceType;
    
    
    /**
     * Default constructor
     * 
     * @param deviceId
     * @param deviceType
     * 
     */
    public CSSDevice(String devicename, String devicedescription, String deviceId, String deviceType) {
       
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.deviceName = devicename;
        this.deviceDescription = devicedescription;
    }

    public String getdeviceName() {
        return deviceName;
    }

    public void setdeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getdeviceDescription() {
        return deviceDescription;
    }

    public void setdeviceDescription(String deviceDescription) {
        
        this.deviceDescription = deviceDescription;
    }

    public String getdeviceId() {
        return deviceId;
    }

    public void setdeviceId(String deviceId) {
        
        this.deviceId = deviceId;
    }

    public String getdeviceType() {
        return deviceType;
    }

    public void setdeviceType(String deviceType) {
        

        this.deviceType = deviceType;
    }

    
    
    /**
     * Override inherited equals method
     * 
     * @return boolean
     */
    public boolean equals(Object object) {
        boolean returnValue = false;

        if (null != object && object instanceof CSSDevice) {
        	CSSDevice otherdevice = (CSSDevice) object;
            if (this == object) {
                returnValue = true;
            } else if (this.deviceId.equals(otherdevice.getdeviceId())) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    /**
     * Override Object hashcode method
     */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Override Object hashcode method
     */
    public String toString() {
        
        return this.deviceId.toString();
    }

	public String getDeviceId() {
		// TODO Auto-generated method stub
		return this.deviceId;
	}

}

