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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.model.DeviceMgmtConstants;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.springframework.osgi.context.BundleContextAware;
//ADD BJB 31 08 2012
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceType;

//END BJB

public class DeviceManager implements IDeviceManager, BundleContextAware {

	private static Logger LOG = LoggerFactory.getLogger(DeviceManager.class);

	private Map<String, DeviceImpl> deviceInstanceContainer;

	private final Map<String, Map<String, DeviceImpl>> deviceFamilyContainer;

	private final Map<String, ServiceRegistration> serviceRegistrationConatainer;

	private DeviceImpl deviceImpl;

	private Map<String, String[]> deviceServiceNamesContainer;

	public static BundleContext bundleContext;

	private BidiMap deviceIdBindingTable;

	private IIdentityManager idManager;

	private ICommManager commManager;

	private INetworkNode nodeId = null;

	// BJB 10/09/2012: Change Dictionary<String, String> -> Dictionary<String, Object>
	private Dictionary<String, Object> properties;
	// END
	
	private ServiceRegistration registration;

	private DmCommManager deviceCommManager;
	
	// BJB 10/09/2012
	// add const for new properties attached to the IDevice osgi service
	public static final String SERVICE_PROVIDER = "ServiceProvider";
	public static final String TARGET_PLATFORM = "TargetPlatform";
	// END

	public DeviceManager() {

		deviceFamilyContainer = new HashMap<String, Map<String, DeviceImpl>>();
		deviceServiceNamesContainer = new HashMap<String, String[]>();
		// TODO Fill this table
		deviceIdBindingTable = new DualHashBidiMap();

		serviceRegistrationConatainer = new HashMap<String, ServiceRegistration>();
	}

	public DmCommManager getDeviceCommManager() {
		return deviceCommManager;
	}

	public void setDeviceCommManager(DmCommManager deviceCommManager) {
		this.deviceCommManager = deviceCommManager;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;

		idManager = commManager.getIdManager();

		nodeId = idManager.getThisNetworkNode();
	}

	public void setBundleContext(BundleContext bundleContext) {

		this.bundleContext = bundleContext;
	}

	public void removeDeviceFromContainer(String deviceFamily, String deviceId) {
		if (deviceFamilyContainer.get(deviceFamily).get(deviceId) != null) {
			DeviceImpl deviceImpl = deviceFamilyContainer.get(deviceFamily)
					.get(deviceId);

			DeviceCommonInfo deviceCommon = new DeviceCommonInfo(deviceFamily,
					deviceImpl.getDeviceName(), deviceImpl.getDeviceType(),
					deviceImpl.getDeviceDescription(),
					deviceImpl.getDeviceConnectionType(),
					deviceImpl.getDeviceLocation(),
					deviceImpl.getDeviceProvider(), deviceImpl.getDeviceId(),
					deviceImpl.isContextSource());

			deviceCommManager.fireDeviceDisconnected(deviceId, deviceCommon);

			deviceFamilyContainer.get(deviceFamily).remove(deviceId);
			deviceServiceNamesContainer.remove(deviceId);
			deviceIdBindingTable.inverseBidiMap().removeValue(deviceId);

			if (serviceRegistrationConatainer.get(deviceId) != null) {
				serviceRegistrationConatainer.get(deviceId).unregister();
			}

			deviceImpl = null;
			deviceCommon = null;
		}
	}

	public List<String> getDeviceServiceNames(String deviceId) {
		LOG.info("////////////////////////////////// getDeviceServiceIds : "
				+ deviceId);
		String[] deviceListArray = deviceServiceNamesContainer.get(deviceId);

		LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: fireNewDeviceConnected "
				+ deviceServiceNamesContainer.toString());

		LOG.info("////////////////////////////////// getDeviceServiceIds : "
				+ deviceListArray.toString());

		if (deviceListArray != null) {
			LOG.info("////////////////////////////////// deviceListArray non null");

			List<String> deviceNamesList = new ArrayList<String>();

			for (String str : deviceListArray) {
				deviceNamesList.add(str);
			}
			LOG.info("////////////////////////////////// deviceListArray non null"
					+ deviceNamesList.toString());
			return deviceNamesList;
		}
		return null;
	}

	public String getPhysicalDeviceId(String deviceId) {
		String physicalDeviceId = (String) deviceIdBindingTable
				.inverseBidiMap().getKey(deviceId);

		LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: getPhysicalDeviceId ::::::::::: "
				+ physicalDeviceId);

		if (physicalDeviceId != null) {
			return physicalDeviceId;
		}
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////Interfaces exposed to the device drivers
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * TODO Add in this method a call to a device binding table class to
	 * generate an Id to each new device connected
	 */
	@Override
	public String fireNewDeviceConnected(String physicalDeviceId,
			DeviceCommonInfo deviceCommonInfo, String[] serviceNames) {
		LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: fireNewDeviceConnected ");

		// Check if the device Family container contains device Instance
		// container for this family of devices
		if (deviceFamilyContainer.get(deviceCommonInfo.getDeviceFamilyIdentity()) == null) {
			// Create a new device instance container
			deviceInstanceContainer = new HashMap<String, DeviceImpl>();

			// TODO here generate the deviceId from the CssId and CssNodeId
			// int deviceId = rdmNumber.nextInt();

			String deviceId = nodeId.getJid() + "/"
					+ deviceCommonInfo.getDeviceFamilyIdentity() + "/"
					+ deviceCommonInfo.getDeviceType() + "/" + physicalDeviceId;

			deviceIdBindingTable.put(deviceId, physicalDeviceId);

			LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: deviceIdBindingTable.getKey ="
					+ deviceIdBindingTable.getKey(physicalDeviceId));
			LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info:  deviceIdBindingTable.inverseBidiMap().getKey ="
					+ deviceIdBindingTable.inverseBidiMap().getKey(deviceId));

			properties = new Hashtable<String, Object>();

			properties.put(DeviceMgmtConstants.DEVICE_NODE_ID, nodeId.getJid());
			properties.put(DeviceMgmtConstants.DEVICE_ID, deviceId);
			properties.put(DeviceMgmtConstants.DEVICE_NAME,	deviceCommonInfo.getDeviceName());
			properties.put(DeviceMgmtConstants.DEVICE_TYPE,deviceCommonInfo.getDeviceType());
			properties.put(DeviceMgmtConstants.DEVICE_FAMILY,deviceCommonInfo.getDeviceFamilyIdentity());
			properties.put(DeviceMgmtConstants.DEVICE_LOCATION,deviceCommonInfo.getDeviceLocation());
			properties.put(DeviceMgmtConstants.DEVICE_PROVIDER,deviceCommonInfo.getDeviceProvider());
			properties.put(DeviceMgmtConstants.DEVICE_CONNECTION_TYPE,deviceCommonInfo.getDeviceConnectionType());
			if (deviceCommonInfo.getContextSource()) {
				properties.put(DeviceMgmtConstants.DEVICE_CONTEXT_SOURCE, "isContextSource");
			} else {
				properties.put(DeviceMgmtConstants.DEVICE_CONTEXT_SOURCE, "isNotContextSource");
			}
			// BJB : 31 08 2012
			properties.put(TARGET_PLATFORM,"SOCIETIES");
			properties.put(SERVICE_PROVIDER,"ICT-SOCIETIES");
			
			Service deviceService = new Service();
			deviceService.setServiceCategory(deviceCommonInfo.getDeviceType());
			deviceService.setServiceDescription(deviceCommonInfo.getDeviceDescription());

			deviceService.setServiceLocation(deviceCommonInfo.getDeviceLocation());

			deviceService.setServiceName(deviceCommonInfo.getDeviceName());
			deviceService.setServiceType(ServiceType.DEVICE);
			if (deviceCommonInfo.getContextSource()) {
				deviceService.setContextSource("isContextSource"); 
				} 
			else {
				deviceService.setContextSource("isNotContextSource");  
			 }
			
			properties.put("ServiceMetaModel", deviceService);

			// END BJB 31 08 2012
			// Object lock = new Object();

			// create a new IDevice implementation
			deviceImpl = new DeviceImpl(this, nodeId.getJid(), deviceId,
					deviceCommonInfo);

			deviceInstanceContainer.put(deviceId, deviceImpl);
			deviceServiceNamesContainer.put(deviceId, serviceNames);
			LOG.info(" %%%%%%%%%%%%%%%%======================%%%%%%%%%%%%%%%%% DeviceManager info: fireNewDeviceConnected "
					+ deviceServiceNamesContainer.toString());
			deviceFamilyContainer.put(
					deviceCommonInfo.getDeviceFamilyIdentity(),
					deviceInstanceContainer);

			synchronized (this) {
				registration = bundleContext.registerService(
						IDevice.class.getName(), deviceImpl, properties);

				LOG.info("-- A device service with the deviceId: "
						+ properties.get("deviceId") + " has been registred");

				// Add the service registration to the map for future use (to
				// unregister a service, to change his property...etc)
				serviceRegistrationConatainer.put(deviceId, registration);

				// Adding generated deviceId to deviceCommonInfo object
				deviceCommonInfo.setDeviceID(deviceId);

				LOG.info("-- Device Manager before Broadcast");
				// Broadcast the the device connected event to the other nodes
				deviceCommManager.fireNewDeviceConnected(deviceId,
						deviceCommonInfo);

				LOG.info("-- Device Manager after Broadcast");

				return deviceId;
			}

		} else {
			// The bundle is Known, so get the device instance container
			deviceInstanceContainer = deviceFamilyContainer
					.get(deviceCommonInfo.getDeviceFamilyIdentity());

			if (!deviceIdBindingTable.containsValue(physicalDeviceId)) {

				String deviceId = nodeId.getJid() + "/"
						+ deviceCommonInfo.getDeviceFamilyIdentity() + "/"
						+ deviceCommonInfo.getDeviceType() + "/"
						+ physicalDeviceId;

				deviceIdBindingTable.put(deviceId, physicalDeviceId);

				properties = new Hashtable<String, Object>();

				properties.put(DeviceMgmtConstants.DEVICE_NODE_ID,
						nodeId.getJid());
				properties.put(DeviceMgmtConstants.DEVICE_ID, deviceId);
				properties.put(DeviceMgmtConstants.DEVICE_NAME,
						deviceCommonInfo.getDeviceName());
				properties.put(DeviceMgmtConstants.DEVICE_TYPE,
						deviceCommonInfo.getDeviceType());
				properties.put(DeviceMgmtConstants.DEVICE_FAMILY,
						deviceCommonInfo.getDeviceFamilyIdentity());
				properties.put(DeviceMgmtConstants.DEVICE_LOCATION,
						deviceCommonInfo.getDeviceLocation());
				properties.put(DeviceMgmtConstants.DEVICE_PROVIDER,
						deviceCommonInfo.getDeviceProvider());
				properties.put(DeviceMgmtConstants.DEVICE_CONNECTION_TYPE,
						deviceCommonInfo.getDeviceConnectionType());

				if (deviceCommonInfo.getContextSource()) {
					properties.put(DeviceMgmtConstants.DEVICE_CONTEXT_SOURCE,
							"isContextSource");
				} else {
					properties.put(DeviceMgmtConstants.DEVICE_CONTEXT_SOURCE,
							"isNotContextSource");
				}
				// BJB : 31 08 2012
				properties.put(TARGET_PLATFORM,"SOCIETIES");
				properties.put(SERVICE_PROVIDER,"ICT-SOCIETIES");

				Service deviceService = new Service();
				deviceService.setServiceCategory(deviceCommonInfo.getDeviceType());
				deviceService.setServiceDescription(deviceCommonInfo.getDeviceDescription());

				deviceService.setServiceLocation(deviceCommonInfo.getDeviceLocation());

				deviceService.setServiceName(deviceCommonInfo.getDeviceName());
				deviceService.setServiceType(ServiceType.DEVICE);
				if (deviceCommonInfo.getContextSource()) {
					deviceService.setContextSource("isContextSource"); 
					} 
				else {
					deviceService.setContextSource("isNotContextSource");  
				 }
				
				properties.put("ServiceMetaModel", deviceService);

				// END BJB 31 08 2012

				// Object lock = new Object();

				// create a new IDevice implementation
				deviceImpl = new DeviceImpl(this, nodeId.getJid(), deviceId,
						deviceCommonInfo);

				deviceInstanceContainer.put(deviceId, deviceImpl);
				deviceServiceNamesContainer.put(deviceId, serviceNames);
				LOG.info(" %%%%%%%%%%%%%%%%======================%%%%%%%%%%%%%%%%% DeviceManager info: fireNewDeviceConnected "
						+ deviceServiceNamesContainer.toString());

				deviceFamilyContainer.put(
						deviceCommonInfo.getDeviceFamilyIdentity(),
						deviceInstanceContainer);

				synchronized (this) {
					registration = bundleContext.registerService(
							IDevice.class.getName(), deviceImpl, properties);

					LOG.info("-- A device service with the deviceId: "
							+ properties.get("deviceId")
							+ " has been registred");

					// Add the service registration to the map for future use
					// (to unregister a service, to change his property...etc)
					serviceRegistrationConatainer.put(deviceId, registration);

					// Adding generated deviceId to deviceCommonInfo object
					deviceCommonInfo.setDeviceID(deviceId);

					LOG.info("-- Device Manager before Broadcast");

					// Broadcast the the device connected event to the other
					// nodes
					deviceCommManager.fireNewDeviceConnected(deviceId,
							deviceCommonInfo);

					LOG.info("-- Device Manager after Broadcast");

					return deviceId;
				}

			}
			return null;
		}
	}

	@Override
	public String fireDeviceDisconnected(String deviceFamily,
			String physicalDeviceId) {
		String deviceId = (String) deviceIdBindingTable
				.getKey(physicalDeviceId);

		if (deviceFamilyContainer.get(deviceFamily) != null) {
			if (deviceId != null) {
				if (deviceFamilyContainer.get(deviceFamily).get(deviceId) != null) {
					deviceFamilyContainer.get(deviceFamily).get(deviceId)
							.removeDevice();
					return physicalDeviceId;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Not implemented yet
	 */
	@Override
	public String fireNewDataReceived(String deviceFamily,
			String physicalDeviceId, Dictionary<String, Object> data) {

		return null;
	}

	@Override
	public String fireNewSharedDevice(DeviceCommonInfo deviceCommonInfo,
			IIdentity deviceNodeId) {
		LOG.info("-- Device Manager : create a new local device "
				+ deviceCommonInfo.getDeviceName()
				+ " representing a remote device connected to CSS Node "
				+ deviceNodeId);
		return "device A";
	}

	@Override
	public boolean fireDisconnectedSharedDevice(String deviceId) {
		// TODO Auto-generated method stub
		return true;
	}

}
