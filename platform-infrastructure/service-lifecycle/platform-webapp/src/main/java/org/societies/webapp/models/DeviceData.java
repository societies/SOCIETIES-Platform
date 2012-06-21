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
 * @author Rafik
 *
 */
public class DeviceData {
	
	private String deviceName;
	private String deviceNodeId;
	private String deviceId;
	private String deviceType;
	private String deviceDescription;
	private String deviceConnectionType;
	private boolean status;
	private String deviceLocation;
	private String deviceProvider;
	private boolean context;
	
	
	/**
	 * 
	 */
	public DeviceData() {
	}


	/**
	 * @param deviceName
	 * @param deviceNodeId
	 * @param deviceId
	 * @param deviceType
	 * @param deviceDescription
	 * @param deviceConnectionType
	 * @param status
	 * @param deviceLocation
	 * @param deviceProvider
	 * @param context
	 */
	public DeviceData(String deviceName, String deviceNodeId, String deviceId,
			String deviceType, String deviceDescription,
			String deviceConnectionType, boolean status, String deviceLocation,
			String deviceProvider, boolean context) {
		super();
		this.deviceName = deviceName;
		this.deviceNodeId = deviceNodeId;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.deviceDescription = deviceDescription;
		this.deviceConnectionType = deviceConnectionType;
		this.status = status;
		this.deviceLocation = deviceLocation;
		this.deviceProvider = deviceProvider;
		this.context = context;
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
	 * @return the deviceNodeId
	 */
	public String getDeviceNodeId() {
		return deviceNodeId;
	}


	/**
	 * @param deviceNodeId the deviceNodeId to set
	 */
	public void setDeviceNodeId(String deviceNodeId) {
		this.deviceNodeId = deviceNodeId;
	}


	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}


	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	 * @return the status
	 */
	public boolean getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
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
	 * @return the context
	 */
	public boolean getContext() {
		return context;
	}


	/**
	 * @param context the context to set
	 */
	public void setContext(boolean context) {
		this.context = context;
	}
	
	
	

}
