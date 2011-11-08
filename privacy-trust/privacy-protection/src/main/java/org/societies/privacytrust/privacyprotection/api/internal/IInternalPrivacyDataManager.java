/**
 * 
 */
package org.societies.privacytrust.privacyprotection.api.internal;

/**
 * Internal interface to manage data access control and data access conditions.
 * @author olivierm
 */
public interface IInternalPrivacyDataManager {
	/**
	 * Update access control permissions over a data
	 * @param dataId
	 * @param agreementId
	 * @param ownerId
	 * @param requestorId
	 * @bug Type are not good yet.
	 */
	public void updatePermissions(String dataId, String agreementId, String ownerId, String requestorId);
}
