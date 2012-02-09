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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.css.devicemgmt.devicemanager.IAction;
import org.societies.css.devicemgmt.devicemanager.IDeviceStateVariable;

/**
 * This Class will be implemented in a device deriver bundle and exposed as an OSGi Service by this bundle
 *
 * @author rafik
 *
 */
public class ActionImpl implements IAction {
	
	private static Logger LOG = LoggerFactory.getLogger(ActionImpl.class);
	private BundleContext bundleContext;
	private ServiceRegistration registration;
	private Dictionary<String, String> properties;
	private DeviceDriverExample deviceDriverExample;
	private String actionName;
	
	
	
	
	public ActionImpl(BundleContext bc, DeviceDriverExample deviceDriverExample, String actionName) {
		
		this.actionName = actionName;
		this.bundleContext = bc;
		this.deviceDriverExample = deviceDriverExample;
		
		properties = new Hashtable<String, String>();
		
		properties.put("actionName", actionName);
		
		Object lock = new Object();

		synchronized(lock)
		{
			registration = bundleContext.registerService(IAction.class.getName(), this, properties);
			
			LOG.info("-- An action service with the action name: " + properties.get("actionName") + " has been registred"); 
		}

	}
	
	public void removeDevice()
	{
		if (registration != null)
		{
			registration.unregister();
			deviceDriverExample.removeActionFromContainer(actionName);
			
			LOG.info("-- The action " + properties.get("actionName") + " has been removed");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.societies.css.devicemgmt.devicemanager.IAction#getInputArgumentNames()
	 */
	public List<String> getInputArgumentNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.css.devicemgmt.devicemanager.IAction#getName()
	 */
	public String getName() {
		return this.actionName;
	}

	/* (non-Javadoc)
	 * @see org.societies.css.devicemgmt.devicemanager.IAction#getOutputArgumentNames()
	 */
	public List<String> getOutputArgumentNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.css.devicemgmt.devicemanager.IAction#getStateVariable(java.lang.String)
	 */
	public IDeviceStateVariable getStateVariable(String argumentName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.css.devicemgmt.devicemanager.IAction#invokeAction(java.util.Dictionary)
	 */
	public Dictionary<String, String> invokeAction(
			Dictionary<String, String> arguments) {
		// TODO Auto-generated method stub
		return null;
	}

}
