package org.societies.privacytrust.privacyprotection.api.internal;

/**
 * Internal interface to manage data access control and data access conditions.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:57
 */
public interface IInternalPrivacyDataManager {

	/**
	 * Update access control permissions over a data
	 * 
	 * @param dataId
	 * @param agreementId
	 * @param ownerId
	 * @param requestorId
	 */
	public void updatePermissions(String dataId, String agreementId, String ownerId, String requestorId);

}