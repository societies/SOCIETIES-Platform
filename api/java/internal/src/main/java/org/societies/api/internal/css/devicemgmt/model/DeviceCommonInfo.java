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
package org.societies.api.internal.css.devicemgmt.model;

/**
 * This class defines the device management common data.
 * To be used by the driver to describe the device that is connected to it
 *
 * @author rafik
 *
 */
public class DeviceCommonInfo {
	
	private String deviceFamilyIdentity; 
	private String deviceName;
	private String deviceType;
	private String deviceDescription;
	private String deviceConnectionType; 
	private String deviceLocation;
	private String deviceProvider;
	private String deviceID;
	private boolean contextSource;
	private String devicePhysicalAddress;
	
	public DeviceCommonInfo() {
		super();
	}
	
	public DeviceCommonInfo(String deviceFamilyIdentity,
			String deviceName, String deviceType,
			String deviceDescription, String deviceConnectionType,
			String deviceLocation, String deviceProvider, String deviceID,
			boolean contextSource) {
		super();
		this.deviceFamilyIdentity = deviceFamilyIdentity;
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.deviceDescription = deviceDescription;
		this.deviceConnectionType = deviceConnectionType;
		this.deviceLocation = deviceLocation;
		this.deviceProvider = deviceProvider;
		this.deviceID = deviceID;
		this.contextSource = contextSource;
	}
	
	
	public String getDeviceFamilyIdentity() {
		return deviceFamilyIdentity;
	}
	public void setDeviceFamilyIdentity(String deviceFamilyIdentity) {
		this.deviceFamilyIdentity = deviceFamilyIdentity;
	}
	
	
	
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	
	
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	
	
	public String getDeviceDescription() {
		return deviceDescription;
	}
	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}
	
	
	
	public String getDeviceConnectionType() {
		return deviceConnectionType;
	}
	public void setDeviceConnectionType(String deviceConnectionType) {
		this.deviceConnectionType = deviceConnectionType;
	}
	
	
	
	public String getDeviceLocation() {
		return deviceLocation;
	}
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	
	
	
	public String getDeviceProvider() {
		return deviceProvider;
	}
	public void setDeviceProvider(String deviceProvider) {
		this.deviceProvider = deviceProvider;
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public boolean getContextSource() {
		return contextSource;
	}
	
	public void setContextSource(boolean contextSource) {
		this.contextSource = contextSource;
	}
	
	public String getDevicePhysicalAddress() {
		return devicePhysicalAddress;
	}

	public void setDevicePhysicalAddress(String devicePhysicalAddress) {
		this.devicePhysicalAddress = devicePhysicalAddress;
	}

	@Override
	public String toString() {
		return "DeviceCommonInfo [deviceFamilyIdentity=" + deviceFamilyIdentity
				+ ", deviceName=" + deviceName 
				+ ", deviceType=" + deviceType
				+ ", deviceDescription=" + deviceDescription
				+ ", deviceConnectionType=" + deviceConnectionType
				+ ", deviceLocation=" + deviceLocation 
				+ ", deviceProvider=" + deviceProvider
				+ ", deviceID=" + deviceID 
				+ ", contextSource=" + contextSource
				+ "]";
	}
}
