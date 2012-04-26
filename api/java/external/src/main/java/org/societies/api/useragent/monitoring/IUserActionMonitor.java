package org.societies.api.useragent.monitoring;

import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface IUserActionMonitor
{
	
	public void monitor(IIdentity owner, IAction action);
	
	//Deprecated
	public void monitor(ServiceResourceIdentifier serviceId, IIdentity owner, String action);
}