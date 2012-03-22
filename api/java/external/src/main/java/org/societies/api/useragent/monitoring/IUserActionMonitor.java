package org.societies.api.useragent.monitoring;

import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	
	public void monitor(IIdentity owner, IAction action);
	
	//Deprecated
	public void monitor(ServiceResourceIdentifier serviceId, IIdentity owner, String action);
}