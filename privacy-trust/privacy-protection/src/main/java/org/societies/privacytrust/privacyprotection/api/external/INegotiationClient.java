package org.societies.privacytrust.privacyprotection.api.external;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.IAgreementEnvelope;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponsePolicy;
/**
 * This interface defines the methods that should be implemented by the CSS that
 * acts as a client in the Negotiation process. This means that this CSS is the
 * one that initiates the starting of the service or requests to join a CIS.
 * @author Elizabeth
 * @version 1.0
 * @created 11-Nov-2011 17:03:12
 */
public interface INegotiationClient {

	/**
	 * 
	 * @param serviceID
	 * @param providerIdentity
	 * @param envelope
	 * @param b
	 */
	public void acknowledgeAgreement(ServiceResourceIdentifier serviceID, EntityIdentifier providerIdentity, IAgreementEnvelope envelope, boolean b);

	/**
	 * 
	 * @param policy
	 */
	public void receiveNegotiationResponse(ResponsePolicy policy);

	/**
	 * 
	 * @param dpi
	 */
	public void receiveProviderIdentity(EntityIdentifier dpi);

	/**
	 * 
	 * @param policy
	 */
	public void receiveProviderPolicy(RequestPolicy policy);

	/**
	 * 
	 * @param policy
	 * @param serviceIdentifier
	 * @param serviceIdentity
	 */
	public void startPrivacyPolicyNegotiation(RequestPolicy policy, ServiceResourceIdentifier serviceIdentifier, EntityIdentifier serviceIdentity);

}