package org.societies.privacytrust.privacyprotection.api.internal;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;

/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 19:17:12
 */
public interface IPrivacyPreferenceConditionMonitor {

	/**
	 * 
	 * @param contextId
	 * @param userIdentity
	 */
	public void contextEventReceived(ICtxAttributeIdentifier contextId, EntityIdentifier userIdentity);

}