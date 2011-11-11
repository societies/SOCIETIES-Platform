package org.societies.personalisation.preference.api.UserPreferenceMerging;

import org.societies.personalisation.preference.api.model.IPreference;


/**
 * @author Elizabeth
 * @version 1.0
 * @created 11-Nov-2011 14:51:54
 */
public interface IUserPreferenceMerging {

	/**
	 * Method to merge two preference trees
	 * @return	the merged tree
	 * 
	 * @param oldTree    the existing tree
	 * @param newNode    the new tree
	 * @param title    this is optional, an empty string would suffice.
	 */
	public IPreference mergeTrees(IPreference oldTree, IPreference newNode, String title);

}