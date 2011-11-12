package org.societies.privacytrust.privacyprotection.api.internal;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 18:57:17
 */
public interface IPrivacyPolicyNegotiationManager {

	/**
	 * 
	 * @param css_id
	 * @param cis_id    CIS admin
	 */
	public void negotiateCISPolicy(EntityIdentifier css_id, EntityIdentifier cis_id);

	/**
	 * 
	 * @param transient_id    temp id
	 * @param service_id
	 */
	public void negotiateServicePolicy(EntityIdentifier transient_id, ServiceResourceIdentifier service_id);

}