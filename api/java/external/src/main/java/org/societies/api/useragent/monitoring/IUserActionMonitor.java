package org.societies.api.useragent.monitoring;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(IServiceResourceIdentifier serviceId, EntityIdentifier owner, IAction action);
}