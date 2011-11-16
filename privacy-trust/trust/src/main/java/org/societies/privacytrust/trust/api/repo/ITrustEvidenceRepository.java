package org.societies.privacytrust.trust.api.repo;

public interface ITrustEvidenceRepository {

	/**
	 * 
	 * @param listener
	 */
	public void registerTrustEvidenceEventListener(TrustEvidenceEventListener listener);
}
