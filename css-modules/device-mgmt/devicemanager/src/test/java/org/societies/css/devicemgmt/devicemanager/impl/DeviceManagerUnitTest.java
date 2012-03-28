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
package org.societies.css.devicemgmt.devicemanager.impl;

import static org.junit.Assert.*;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class DeviceManagerUnitTest {

	private DeviceManager deviceManager;
	private BundleContext bundleContextMock;
	private ICommManager commManagerMock;
	private IIdentityManager identityManagerMock;
	private INetworkNode iNetworkNodeMock;
	
	private String [] serviceIds = {"service1"};
	
	private Dictionary<String, String> properties;
	
	private ServiceRegistration sr;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		bundleContextMock = mock(BundleContext.class);
		
		commManagerMock = mock(ICommManager.class);
		
		identityManagerMock = mock(IIdentityManager.class);
		
		iNetworkNodeMock = mock(INetworkNode.class);
		
		deviceManager = new DeviceManager();
	
		deviceManager.setBundleContext(bundleContextMock);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		deviceManager = null;
	}


	@Test
	public void testFireNewDeviceConnected() {
		
		//Initial condition
		String deviceId;
		String physicalDeviceId;
		DeviceCommonInfo deviceCommonInfo;
		when(commManagerMock.getIdManager()).thenReturn(identityManagerMock);	
		when(identityManagerMock.getThisNetworkNode()).thenReturn(iNetworkNodeMock);
		//
		when(iNetworkNodeMock.getJid()).thenReturn("node1");
		//
		deviceManager.setCommManager(commManagerMock);
		
		
		// Test suit
		deviceCommonInfo = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "just for test", "zigbee", "room1","trialog", null, true);
		physicalDeviceId = "33:40:F5";
		deviceId = iNetworkNodeMock.getJid() + "." + deviceCommonInfo.getDeviceFamilyIdentity()+ "." + deviceCommonInfo.getDeviceType() + "." + physicalDeviceId;	
		//
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		//
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		
		//
		physicalDeviceId = "2012";
		deviceId = iNetworkNodeMock.getJid() + "." + deviceCommonInfo.getDeviceFamilyIdentity()+ "." + deviceCommonInfo.getDeviceType() + "." + physicalDeviceId;		
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		
		//
		deviceCommonInfo.setDeviceFamilyIdentity("family2");
		physicalDeviceId = "2011";	
		deviceId = iNetworkNodeMock.getJid() + "." + deviceCommonInfo.getDeviceFamilyIdentity()+ "." + deviceCommonInfo.getDeviceType() + "." + physicalDeviceId;
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
	}

}
