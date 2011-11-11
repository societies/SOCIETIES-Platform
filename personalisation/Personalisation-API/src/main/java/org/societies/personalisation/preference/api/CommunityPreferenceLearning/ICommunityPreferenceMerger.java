package org.societies.personalisation.preference.api.CommunityPreferenceLearning;

import java.util.Date;
import java.util.List;

import org.societies.personalisation.common.api.model.IAction;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Output;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 15:23:28
 */
public interface ICommunityPreferenceMerger {

	/**
	 * 
	 * @param date
	 * @param serviceId
	 * @param action
	 */
	public void explicitlyTriggerLearning(Date date, ServiceResourceIdentifier serviceId, IAction action);

	/**
	 * (non-Javadoc)
	 * @see org.personalsmartspace.lm.mining.c45.api.platform.
	 * IC45Consumer#handleC45Output(java.util.List)
	 * 
	 * @param list
	 */
	public void handleC45Output(List<IC45Output> list);

}