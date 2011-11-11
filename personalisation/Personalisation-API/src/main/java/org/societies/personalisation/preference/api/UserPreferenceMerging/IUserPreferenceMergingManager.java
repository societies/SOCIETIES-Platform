package org.societies.personalisation.preference.api.UserPreferenceMerging;

import java.util.Date;
import java.util.List;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.IAction;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:51:55
 */
public interface IUserPreferenceMergingManager{

	/**
	 * 
	 * @param date
	 */
	public void explicitlyTriggerLearning(Date date);

	/**
	 * 
	 * @param date
	 * @param serviceId
	 * @param action
	 */
	public void explicitlyTriggerLearning(Date date, ServiceResourceIdentifier serviceId, IAction action);

	/**
	 * 
	 * @param dpi
	 * @param date
	 * @param serviceId
	 * @param action
	 */
	public void explicitlyTriggerLearning(EntityIdentifier dpi, Date date, ServiceResourceIdentifier serviceId, IAction action);



	/**
	 * 
	 * @param action
	 * @param dpi
	 */
	public void processActionReceived(IAction action, EntityIdentifier dpi);

	/**
	 * 
	 * @param dpi
	 * @param serviceID
	 * @param serviceType
	 * @param prefName
	 */
	public void sendEvent(EntityIdentifier dpi, ServiceResourceIdentifier serviceID, String serviceType, String prefName);

}