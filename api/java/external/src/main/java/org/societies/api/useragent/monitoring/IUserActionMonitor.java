package org.societies.api.useragent.monitoring;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.api.personalisation.model.IAction;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(ServiceResourceIdentifier serviceId, EntityIdentifier owner, IAction action);
}