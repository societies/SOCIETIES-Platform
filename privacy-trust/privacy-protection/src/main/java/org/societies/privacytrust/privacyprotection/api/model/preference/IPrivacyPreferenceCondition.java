package org.societies.privacytrust.privacyprotection.api.model.preference;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyConditionConstants;

/**
 * This interface defines the methods that should be implemented by a class representing a condition in an IF-THEN-ELSE preference object.
 * @author Elizabeth
 *
 */
public interface IPrivacyPreferenceCondition {
	
	public PrivacyConditionConstants getType(); 

}
