package org.societies.personalisation.preference.api.CommunityPreferenceManagement;

import org.societies.personalisation.common.api.model.PreferenceDetails;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 15:20:56
 */
public interface ICommunityPreferenceManager {

	/**
	 * 
	 * @param details
	 */
	public void deletePreference(PreferenceDetails details);

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public void deletePreference(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public IPreferenceTreeModel getModel(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param details
	 */
	public IPreferenceTreeModel getModel(PreferenceDetails details);

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 * @param preference
	 */
	public void updatePreference(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName, IPreference preference);

	/**
	 * 
	 * @param details
	 * @param preference
	 */
	public void updatePreference(PreferenceDetails details, IPreference preference);

}