package org.societies.api.useragent.monitoring;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(ServiceResourceIdentifier serviceId, String owner, String action);
}