package org.societies.privacytrust.privacyprotection.api.internal;

import java.util.List;
import java.util.Map;

import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.NegotiationAgreement;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;
import org.societies.privacytrust.privacyprotection.mock.EntityIdentifier;
import org.societies.privacytrust.privacyprotection.mock.ServiceResourceIdentifier;

/**
 * @author olivierm
 * @version 1.0
 * @created 17-nov.-2011 11:12:31
 */
public interface IPolicyAgreementManager {
	/**
	 * Delete and agreement
	 * @param id
	 */
	public boolean deleteAgreement(String id);

	/**
	 * Delete agreements following criteria
	 * @param criteria
	 */
	public boolean deleteAgreements(Map criteria);

	/**
	 * Retrieve an agreement
	 * @param id
	 */
	public NegotiationAgreement getAgreement(String id);

	/**
	 * Retrieve agreements following criteria
	 * @param criteria
	 */
	public List<NegotiationAgreement> getAgreements(Map criteria);

	/**
	 * The objective here is to retrieve the part of the SERVICE negotiation agreement
	 * which is relevant about this data for this consumer, in order to know usage
	 * conditions of this data. E.g. obfuscation or not, disclosure conditions...
	 * 
	 * @param dataId
	 * @param ownerId
	 * @param requestorId
	 * @param serviceId
	 */
	public ResponseItem getPermissionConditionsInAgreement(DataIdentifier dataId, EntityIdentifier ownerId, EntityIdentifier requestorId, ServiceResourceIdentifier serviceId);

	/**
	 * The objective here is to retrieve the part of the CIS negotiation agreement
	 * which is relevant about this data for this consumer, in order to know usage
	 * conditions of this data. E.g. obfuscation or not, disclosure conditions...
	 * 
	 * @param dataId
	 * @param ownerId
	 * @param requestorId
	 * @param cisId
	 */
	public ResponseItem getPermissionConditionsInAgreement(DataIdentifier dataId, EntityIdentifier ownerId, EntityIdentifier requestorId, EntityIdentifier cisId);

	/**
	 * Update Negotiation Agreement (with a Service)
	 * 
	 * @param agreement
	 * @param myId
	 * @param serviceId
	 */
	public NegotiationAgreement updateAgreement(NegotiationAgreement agreement, EntityIdentifier myId, ServiceResourceIdentifier serviceId);

	/**
	 * Update Negotiation Agreement (with a CIS)
	 * 
	 * @param agreement
	 * @param myId
	 * @param cisId
	 */
	public NegotiationAgreement updateAgreement(NegotiationAgreement agreement, EntityIdentifier myId, EntityIdentifier cisId);

}
