package org.societies.personalisation.preference.api.UserPreferenceConditionMonitor;

import org.societies.personalisation.common.api.model.ContextAttribute;
import org.societies.personalisation.common.api.model.ContextModelObject;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.IFeedbackEvent;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:52:53
 */
public interface IUserPreferenceConditionMonitor {

	public void disableAllPCM();

	/**
	 * 
	 * @param dpi
	 */
	public void disablePCM(EntityIdentifier dpi);

	public void enableAllPCM();

	/**
	 * 
	 * @param dpi
	 */
	public void enablePCM(EntityIdentifier dpi);

	/**
	 * 
	 * @param contextAttribute
	 * @param user_id
	 */
	public IPreferenceOutcome getOutcome(ContextAttribute contextAttribute, EntityIdentifier user_id);

	/**
	 * 
	 * @param user_id
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IPreferenceOutcome requestOutcomeWithCurrentContext(EntityIdentifier user_id, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param user_id
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IPreferenceOutcome requestOutcomeWithFutureContext(EntityIdentifier user_id, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param FeedbackEvent
	 */
	public void sendFeedback(IFeedbackEvent FeedbackEvent);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(ContextModelObject ctxModelObj);

}