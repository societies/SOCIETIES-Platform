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
	
	/**
	 * This method should be called by a service when a personalisable parameter is changed manually by the user
	 * 
	 * @param owner		this is the identity of the consumer CSS i.e. the user who is currently using this service
	 * @param action	this datatype contains details of the action that the user has performed i.e. what
	 * 					personalisable parameter has been changed in the service and how is has been changed
	 */
	public void monitor(IIdentity owner, IAction action);
	
	//Deprecated
	//public void monitor(ServiceResourceIdentifier serviceId, IIdentity owner, String action);
}