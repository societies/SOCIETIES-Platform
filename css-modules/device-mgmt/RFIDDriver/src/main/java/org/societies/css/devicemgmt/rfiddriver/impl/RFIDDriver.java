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
package org.societies.css.devicemgmt.rfiddriver.impl;


import java.util.Dictionary;
import java.util.Hashtable;


import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.societies.api.css.devicemgmt.rfid.IRfidDriver;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.css.devicemgmt.model.DeviceMgmtDriverServiceConstants;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.css.devicemgmt.rfiddriver.readers.RfidReader;
import org.springframework.osgi.context.BundleContextAware;


/**
 *
 * @author Eliza
 *
 */
public class RFIDDriver implements /*IRfidDriver,*/ BundleContextAware{

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	Hashtable<String, SocketClient> sockets;
	private final String DEVICE_FAMILY_IDENTITY = "org.societies.css.devicemgmt.RFIDDriver";
	private final String DEVICE_NAME = "RFID_READER";
	private final String DEVICE_DESCR = "RFID Location Management System";
	private final String DEVICE_PROVIDER = "HWU";
	private BundleContext bc;
	
	private DeviceCommonInfo testRfidReaderCommonInfo;
	
	private DeviceCommonInfo rfidReaderCommonInfo1;
	private DeviceCommonInfo rfidReaderCommonInfo2;
	
	private Dictionary<String, String> properties;
	
	private ServiceRegistration testRfidReaderReg;
	private ServiceRegistration rfidReaderReg1;
	private ServiceRegistration rfidReaderReg2;
	
	private IEventMgr eventMgr;
	
	private IDeviceManager deviceMgr;
	
	private RfidReader testRfidReader;
	private RfidReader rfidReader1;
	private RfidReader rfidReader2;
	
	private String testPhysicalDeviceId = "127.0.0.1";
	private String physicalDeviceId1 = "137.195.27.197";
	private String physicalDeviceId2 = "137.195.27.198";
	
	
	
	private String [] rfidReaderServiceNamesList = {DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE};
	
	//BundleContext injection
	public void setBundleContext(BundleContext bc) {
		this.bc =  bc;
		
	}
	
	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}
	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}
	/**
	 * @return the deviceMgr
	 */
	public IDeviceManager getDeviceMgr() {
		return deviceMgr;
	}
	/**
	 * @param deviceMgr the deviceMgr to set
	 */
	public void setDeviceMgr(IDeviceManager deviceMgr) {
		this.deviceMgr = deviceMgr;
	}
	
	
	
	public void initialiseRFIDDriver() {
		
		//sockets = new Hashtable<String, SocketClient>();
		
		testRfidReaderCommonInfo = new DeviceCommonInfo(DEVICE_FAMILY_IDENTITY, DEVICE_NAME, 
				DeviceTypeConstants.RFID_READER, DEVICE_DESCR, "ETHERNET", "LearningZone_0", 
				DEVICE_PROVIDER, null, false);
		
		rfidReaderCommonInfo1 = new DeviceCommonInfo(DEVICE_FAMILY_IDENTITY, DEVICE_NAME, 
				DeviceTypeConstants.RFID_READER, DEVICE_DESCR, "ETHERNET", "LearningZone_1", 
				DEVICE_PROVIDER, null, false);
		
		rfidReaderCommonInfo2 = new DeviceCommonInfo(DEVICE_FAMILY_IDENTITY, DEVICE_NAME, 
				DeviceTypeConstants.RFID_READER, DEVICE_DESCR, "ETHERNET", "LearningZone_2", 
				DEVICE_PROVIDER, null, false);
		
		
		testRfidReader = new RfidReader(eventMgr);
		
		rfidReader1 = new RfidReader(eventMgr);
		
		rfidReader2 = new RfidReader(eventMgr);
		
		synchronized(this) 
		{
			properties = new Hashtable<String, String>();
			
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					testPhysicalDeviceId);
			
			testRfidReaderReg = bc.registerService(IDriverService.class.getName(), testRfidReader, properties);
			
			
			properties = new Hashtable<String, String>();
			
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					physicalDeviceId1);
			
			rfidReaderReg1 = bc.registerService(IDriverService.class.getName(), rfidReader1, properties);
			
			
			
			properties = new Hashtable<String, String>();
			
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					physicalDeviceId2);
			
			rfidReaderReg2 = bc.registerService(IDriverService.class.getName(), rfidReader2, properties);
		}
		
		String testRfidReaderDeviceId = deviceMgr.fireNewDeviceConnected(testPhysicalDeviceId, testRfidReaderCommonInfo, rfidReaderServiceNamesList);
		String rfidReaderDeviceId1 = deviceMgr.fireNewDeviceConnected(physicalDeviceId1, rfidReaderCommonInfo1, rfidReaderServiceNamesList);
		String rfidReaderDeviceId2 = deviceMgr.fireNewDeviceConnected(physicalDeviceId2, rfidReaderCommonInfo2, rfidReaderServiceNamesList);
		
		
		testRfidReader.setDeviceId(testRfidReaderDeviceId);
		rfidReader1.setDeviceId(rfidReaderDeviceId1);
		rfidReader2.setDeviceId(rfidReaderDeviceId2);
		
		
	}
	
	
	public void stop(BundleContext context) throws Exception {
		
		stopappli();
		
		if(rfidReaderReg1 != null){
			rfidReaderReg1.unregister();
		}	
		if(rfidReaderReg2 != null){
			rfidReaderReg2.unregister();
		}
		if(testRfidReaderReg != null){
			testRfidReaderReg.unregister();
		}
		
	}
	
	
	public void stopappli(){

		if (rfidReader1!=null){
			deviceMgr.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", physicalDeviceId1);
			rfidReader1 = null;
		}
		else {
			System.out.println("no reader 1");
		}
		if (rfidReader2!=null){
			deviceMgr.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", physicalDeviceId2);
			rfidReader2 = null;
		}
		else {
			System.out.println("no reader 2");
		}
		if (testRfidReader!=null){
			deviceMgr.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", testPhysicalDeviceId);
			testRfidReader = null;
		}
		else {
			System.out.println("no reader test");
		}
	}

	/*
	@Override
	public void connect(String ipAddress) {
		if (sockets.containsKey(ipAddress)){
			this.logging.debug("Already connected to: "+ipAddress);
			return;
		}

		SocketClient socketClient = new SocketClient(ipAddress);
		if (socketClient.checkIp(ipAddress)){
			socketClient.setEventMgr(eventMgr);
			socketClient.start();
			
			
			DeviceCommonInfo deviceCommonInfo = new DeviceCommonInfo(DEVICE_FAMILY_IDENTITY, 
					DEVICE_NAME,
					DeviceTypeConstants.RFID_READER,
					DEVICE_DESCR, 
					"ETHERNET", 
					"LearningZone", 
					DEVICE_PROVIDER, 
					null, 
					false);
			this.deviceMgr.fireNewDeviceConnected(ipAddress, deviceCommonInfo, new String[]{DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE});
			
			
			this.sockets.put(ipAddress, socketClient);
		}else{
			this.logging.error(ipAddress+" not valid. ignoring request");
		}
		
	}
	//*/
}
