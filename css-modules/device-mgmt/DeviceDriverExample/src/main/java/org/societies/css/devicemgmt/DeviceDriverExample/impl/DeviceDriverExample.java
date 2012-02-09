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
package org.societies.css.devicemgmt.DeviceDriverExample.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs;
import org.societies.css.devicemgmt.devicemanager.IDeviceManager;
import org.springframework.osgi.context.BundleContextAware;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class DeviceDriverExample implements ControllerWs, BundleContextAware{

	
	private BundleContext bundleContext;
	
	private IDeviceManager deviceManager;
	
	private ActionImpl actionImpl;
	
	private String createNewDevice = "";
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceDriverExample.class);

	private final Map<String, ActionImpl> actionInstanceContainer;
	
	
	public DeviceDriverExample() {
		
		actionInstanceContainer = new HashMap<String, ActionImpl>();

		LOG.info("DeviceDriverExample: " + "=========++++++++++------ DeviceDriverExample constructor");
	}
	
	public void setDeviceManager (IDeviceManager deviceManager)
	{
		this.deviceManager = deviceManager;
		
		LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% IDeviceManager dependency injection");
	}
	
	
	/** (non-Javadoc)
	 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
	 */
	public void setBundleContext(BundleContext arg0) {
		// TODO Auto-generated method stub
		bundleContext = arg0;
	}
	
	
	protected Map<String, ActionImpl> getActionInstanceContainer() 
	{	
		return actionInstanceContainer;
	}
	protected void setActionInstanceContainer(String actionName, ActionImpl actionInstance) 
	{
		this.actionInstanceContainer.put(actionName, actionInstance);
	}

	public void removeActionFromContainer (String actionName)
	{
		if (getActionInstanceContainer().get(actionName) != null)
		{
			getActionInstanceContainer().remove(actionName);
		}
	}


	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#createNewDevice(java.lang.String)
	 */
	public String createNewDevice(String deviceId, String actionName) {
		// TODO Auto-generated method stub
		
		LOG.info("DeviceDriverExample: " + "*********************************** createNewDevice : " + deviceId +" "+actionName);
		
		// check if the device already exists in the container
		if (getActionInstanceContainer().get(actionName) == null)
		{
			createNewDevice = deviceManager.fireNewDeviceConnected(deviceId);
			
			LOG.info("DeviceDriverExample: " + "*********************************** deviceManager.fireNewDeviceConnected");
			
			//create new instance of the DeviceImpl and expose the instance as the OSGi service by using IDevice interface
			actionImpl = new ActionImpl(bundleContext, this, actionName);		
				
			//add device instance to the container
			setActionInstanceContainer(actionName, actionImpl);
			LOG.info("DeviceDriverExample: " + "*********************************** Hi, I'm a new IAction : " + actionName);

			return "The Action "+ actionName +" has been created";
		}
		return "The action "+ actionName + " already exists";
		
	}


	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#removeDevice(java.lang.String)
	 */
	public void removeDevice(String deviceId) {
		// TODO Auto-generated method stub
		
	}


	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#sendNewData(java.lang.String, java.lang.String)
	 */
	public void sendNewData(String deviceId, String data) {
		// TODO Auto-generated method stub
		
	}

}
