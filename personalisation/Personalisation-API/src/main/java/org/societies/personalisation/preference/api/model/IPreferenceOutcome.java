package org.societies.personalisation.preference.api;

import java.io.Serializable;

import org.societies.personalisation.common.api.IOutcome;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IPreferenceOutcome extends Serializable,IOutcome {

	public IQualityofPreference getQualityofPreference();

}