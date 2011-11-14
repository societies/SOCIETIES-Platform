package org.societies.privacytrust.privacyprotection.api.model.preference;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;




/**
 * This interface defines the methods needed by a class representing a privacy outcome. 
 * @author Elizabeth
 *
 */
public interface IPrivacyOutcome {

	public PrivacyPreferenceTypeConstants getOutcomeType();
	public int getConfidenceLevel();
}
