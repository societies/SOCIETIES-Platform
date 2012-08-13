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
 * Describe your class here...
 *
 * @author Rafik
 *
 */
public class DeviceMgmtEventConstants {
	
	/**
	 * Constant used to subscribe to the light sensors events
	 */
	public static final String LIGHT_SENSOR_EVENT = "sensor/lightSensorEvent";
	
	/**
	 * Constant used to subscribe to temperature sensor events
	 */
	public static final String TEMPERATURE_SENSOR_EVENT = "sensor/temperatureSensorEvent";
	
	
	/**
	 * Constant used to subscribe to receive GPS sensor events
	 */
	public static final String GPS_SENSOR_EVENT = "sensor/gpsSensorEvent";
	
	/**
	 * Constant used to subscribe to receive pressure mat events
	 */
	public static final String PRESSURE_MAT_EVENT = "sensor/pressureMatEvent";
	
	/**
	 * Constant used to subscribe to receive RFiD reader events
	 */
	public static final String RFID_READER_EVENT = "sensor/rfidReaderEvent";
	
	/**
	 * Constant used to subscribe to receive kinect events
	 */
	public static final String KINECT_EVENT = "sensor/kinectEvent";
	
	
	/**
	 * Constant used to subscribe to receive screen events if any
	 */
	public static final String SCREEN_EVENT = "actuator/screenEvent";
	
	/**
	 * Constant used to subscribe to receive directional speaker events if any
	 */
	public static final String DIRECTIONAL_SPEAKER_EVENT = "actuator/directionalSpeakerEvent";
	
	/**
	 * Constant used to subscribe to receive directional light actuator events if any
	 */
	public static final String LIGHT_ACTUATOR_EVENT = "actuator/lightActuatorEvent";

}
