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
package org.societies.api.css.devicemgmt.model;

/**
 * This class contains property key constants used when registering IDevice services
 * All these properties are provided by the DeviceManager when it registers a new IDevice service
 * These property keys can be used by the consumer of the service as an LADP filter when tracking a given service
 * in order to track only device services which is interested in.
 *
 * @author rafik
 *
 */
public class DeviceMgmtConstants {
	
	
	
	/**
	 * Unique Identifier of a css node to which a device is connected. Visible globally in the Societies Platform
	 * this constant is to be used to get or set device service node id property
	 * @value "deviceId"
	 */
	public static final String DEVICE_NODE_ID = "deviceNodeId";

	/**
	 * Unique Identifier of a device. Visible globally in the Societies Platform
	 * this constant is to be used to get or set device service id property
	 * @value "deviceId"
	 */
	public static final String DEVICE_ID = "deviceId";
	
	/**
	 * Specific name of the device. e.g. “Light Sensor Corridor”. Provided during configuration
	 * Constant to be used to get or set device service name property
	 * @value "deviceName"
	 */
	public static final String DEVICE_NAME = "deviceName";
	
	
	/**
	 * Generic type of the device. e.g. LightSensor
	 * Constant to be used to get or set device service type property
	 * @value "deviceType"
	 */
	public static final String DEVICE_TYPE = "deviceType";
	
	
	/**
	 * Generic term associated to the driver to which the device is attached.
	 * it represents the the driver symbolic name of the bundle that manages this device 
	 * e.g. TrialogZigbeeDriver
	 * Constant to be used to get or set device service family property
	 * @value "deviceFamily"
	 */
	public static final String DEVICE_FAMILY = "deviceFamily";
	
	
	/**
	 * Description of the device in few words
	 * Constant to be used to get or set device service description property
	 * @value "deviceDescription"
	 */
	public static final String DEVICE_DESCRIPTION = "deviceDescription";
	
	
	/**
	 * The location of the device related to its real physical. 
	 * E.g. “room 127”, in a human readable format. Setup during configuration
	 * Constant to be used to get or set device service location property
	 * @value "deviceLocation"
	 */
	public static final String DEVICE_LOCATION = "deviceLocation";
	
	
	/**
	 * Name of the manufacturer.
	 * Constant to be used to get or set device service provider property
	 * @value "deviceProvider"
	 */
	public static final String DEVICE_PROVIDER = "deviceProvider";
	
	
	/**
	 * Generic name of the underlying protocol. E.g. Zigbee, Bluetooth
	 * Constant to be used to get or set device service connection type property
	 * @value "deviceConnectionType"
	 */
	public static final String DEVICE_CONNECTION_TYPE = "deviceConnectionType";
	
	
	/**
	 * This indicating whether the device is context compliant.
	 * Constant to be used to get or set device service context compliant property
	 * @value "contextCompliant"
	 */
	public static final String DEVICE_CONTEXT_SOURCE = "contextSource";
	

}
