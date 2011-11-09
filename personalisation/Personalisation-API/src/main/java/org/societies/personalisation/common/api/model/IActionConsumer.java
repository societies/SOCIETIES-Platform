package org.societies.personalisation.common.api.model;

import java.util.List;


/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public interface IActionConsumer {

	/**
	 * 	// replace with ServiceResourceIdentifier 
	 * This method is used by the User Agent Subsystem to locate the right instance
	 * of a IActionConsumer by filtering them based on their service identifier
	 * @return 		The service has to return its own service identifier
	 */
	public ServiceResourceIdentifier getServiceIdentifier();

	/**
	 * If the service has registered itself with a service type, it should return this
	 * using this method.
	 * @return		The service's type
	 */
	public String getServiceType();

	/**
	 * If the service fits under more than one service type category, it can return a
	 * list of service types using this method.
	 * @return 		A list of service types in String format
	 */
	public List<String> getServiceTypes();

	/**
	 * This method is used by the User Agent subsystem to send actions to be
	 * implemented by the services.
	 * @return		The service should return true if the action was implemented
	 * successfully or false if not.
	 * 
	 * @param dpi    The Digital Identity of the user currently using the service 	// replace with EntityIdentifier
	 * @param obj    The IAction object to be implemented
	 */
	public boolean setIAction(EntityIdentifier dpi, IAction obj);

}