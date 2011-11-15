package org.societies.privacytrust.trust.api;

import java.util.Date;

import org.societies.privacytrust.trust.api.mock.EntityIdentifier;

public interface ITrustEvidenceCollector {

	/**
	 * 
	 * @param trustor
	 * @param provider
	 * @param serviceId
	 * @param rating
	 * @param timestamp
	 */
	public void addServiceExperience(EntityIdentifier trustor, EntityIdentifier provider, EntityIdentifier serviceId, double rating, Date timestamp);

	/**
	 * 
	 * @param trustor
	 * @param trustee
	 * @param value
	 */
	public void addTrustOpinion(EntityIdentifier trustor, EntityIdentifier trustee, double value);

	/**
	 * 
	 * @param trustor
	 * @param trustee
	 * @param type
	 * @param rating
	 * @param timestamp
	 */
	public void addUserInteractionExperience(EntityIdentifier trustor, EntityIdentifier trustee, String type, double rating, Date timestamp);
}