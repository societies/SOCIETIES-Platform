package org.societies.personalisation.preference.api.UserPreferenceManagement;

import java.util.List;

import org.societies.personalisation.common.api.model.ContextAttribute;
import org.societies.personalisation.common.api.model.ContextModelObject;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.IOutcome;
import org.societies.personalisation.common.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.common.api.model.PreferenceDetails;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:51:53
 */
public interface IUserPreferenceManagement {

	/**
	 * 
	 * @param ownerID
	 * @param details
	 */
	public void deletePreference(EntityIdentifier ownerID, PreferenceDetails details);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public void deletePreference(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param ownerID
	 * @param outcome
	 */
	public List<ICtxAttributeIdentifier> getConditions(EntityIdentifier ownerID, IOutcome outcome);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IPreferenceTreeModel getModel(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param ownerID
	 * @param details
	 */
	public IPreferenceTreeModel getModel(EntityIdentifier ownerID, PreferenceDetails details);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IPreferenceOutcome getPreference(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 */
	public List<IPreferenceConditionIOutcomeName> getPreferenceConditions(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public List<ICtxAttributeIdentifier> getPreferenceConditions(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param userId
	 * @param attr
	 * @param preferenceIdentifier
	 */
	public List<IOutcome> reEvaluatePreferences(EntityIdentifier userId, ContextAttribute attr, List<PreferenceDetails> preferenceIdentifier);

	/**
	 * 
	 * @param ownerID
	 * @param attr
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IOutcome reEvaluatePreferences(EntityIdentifier ownerID, ContextAttribute attr, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 * @param preference
	 */
	public void updatePreference(EntityIdentifier ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName, IPreference preference);

	/**
	 * 
	 * @param ownerID
	 * @param details
	 * @param preference
	 */
	public void updatePreference(EntityIdentifier ownerID, PreferenceDetails details, IPreference preference);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(ContextModelObject ctxModelObj);

}