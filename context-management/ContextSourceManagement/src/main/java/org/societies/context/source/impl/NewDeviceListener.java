package org.societies.context.source.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;

public class NewDeviceListener implements Runnable{
	private static Logger LOG = LoggerFactory.getLogger(ContextSourceManagement.class);

	private IDeviceManager deviceManager;
	private IDevice newDevice;

	public NewDeviceListener(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		if (LOG.isDebugEnabled()) LOG.debug(this+" created");
		
	}

	/**
	 * @return the deviceManager
	 */
	public IDeviceManager getDeviceManager() {
		return deviceManager;
	}

	/**
	 * @param deviceManager the deviceManager to set
	 */
	public void setDeviceManager(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
