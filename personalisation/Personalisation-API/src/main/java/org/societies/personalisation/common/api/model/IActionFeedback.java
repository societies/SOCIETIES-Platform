package org.societies.personalisation.common.api.model;

import java.util.List;



/**
 * @author Eliza
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public interface IActionFeedback {

	public void getResult();

	/**
	 * 	// replace with ServiceResourceIdentifier 
	 * @return 		The service identifier
	 */
	public Object getServiceIdentifier();

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

}