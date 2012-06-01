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
package org.societies.webapp.models;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class DeviceRegistryForm {
	
	private String method;
	private String cssNodeId;
	private String deviceFamilyIdentity; 
	private String deviceName;
	private String deviceType;
	private String deviceDescription;
	private String deviceConnectionType; 
	private String deviceLocation;
	private String deviceProvider;
	private String deviceID;
	private boolean contextSource;
	
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	
	/**
	 * @return the cssNodeId
	 */
	public String getCssNodeId() {
		return cssNodeId;
	}
	/**
	 * @param cssNodeId the cssNodeId to set
	 */
	public void setCssNodeId(String cssNodeId) {
		this.cssNodeId = cssNodeId;
	}
	/**
	 * @return the deviceFamilyIdentity
	 */
	public String getDeviceFamilyIdentity() {
		return deviceFamilyIdentity;
	}
	/**
	 * @param deviceFamilyIdentity the deviceFamilyIdentity to set
	 */
	public void setDeviceFamilyIdentity(String deviceFamilyIdentity) {
		this.deviceFamilyIdentity = deviceFamilyIdentity;
	}
	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * @return the deviceDescription
	 */
	public String getDeviceDescription() {
		return deviceDescription;
	}
	/**
	 * @param deviceDescription the deviceDescription to set
	 */
	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}
	/**
	 * @return the deviceConnectionType
	 */
	public String getDeviceConnectionType() {
		return deviceConnectionType;
	}
	/**
	 * @param deviceConnectionType the deviceConnectionType to set
	 */
	public void setDeviceConnectionType(String deviceConnectionType) {
		this.deviceConnectionType = deviceConnectionType;
	}
	/**
	 * @return the deviceLocation
	 */
	public String getDeviceLocation() {
		return deviceLocation;
	}
	/**
	 * @param deviceLocation the deviceLocation to set
	 */
	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	/**
	 * @return the deviceProvider
	 */
	public String getDeviceProvider() {
		return deviceProvider;
	}
	/**
	 * @param deviceProvider the deviceProvider to set
	 */
	public void setDeviceProvider(String deviceProvider) {
		this.deviceProvider = deviceProvider;
	}
	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
	}
	/**
	 * @param deviceID the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	/**
	 * @return the contextSource
	 */
	public boolean isContextSource() {
		return contextSource;
	}
	/**
	 * @param contextSource the contextSource to set
	 */
	public void setContextSource(boolean contextSource) {
		this.contextSource = contextSource;
	}
	
	

}
