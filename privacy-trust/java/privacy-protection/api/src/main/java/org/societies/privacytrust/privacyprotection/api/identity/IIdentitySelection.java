package org.societies.privacytrust.privacyprotection.api.identity;

import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;

public interface IIdentitySelection {
	
	/**
	 * Called by the Privacy Negotiation Agent behaving as a service consumer in the end of the negotiation process, after the Privacy Policy Negotiation Aggreement is reached but before it is signed.
	 * It evaluates the suitability of the identity options to be used in the interaction with the remote entity, and returns a list to be processed w.r.t. the User Identity Selection Preferences, and ultimately to be .
	 * 
	 * @param ppna
	 * @return
	 */
	
	List<IIdentityOption> processIdentityContext(IAgreement ppna);
	
	/**
	 * 
	 * To be used when a remote entity requests some data to a user-owned entity. It evaluates the risk of linkability existing identities in the case of disclosure.
	 * 
	 * @param remoteEntity
	 * @param userOwnedEntity
	 * @param dataToBeReleased
	 * @return
	 */
	IIdentityOption evaluateLinkability(IIdentity remoteEntity, IIdentity userOwnedEntity, Resource dataToBeReleased);
}
