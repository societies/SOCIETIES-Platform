package org.societies.privacytrust.privacyprotection.datamanagement;

import org.societies.privacytrust.privacyprotection.api.internal.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;
import org.societies.privacytrust.privacyprotection.mock.EntityIdentifier;

/**
 * @state skeleton 
 * @author olivierm
 */
public class PrivacyDataManagerInternal implements IPrivacyDataManagerInternal {
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.internal.IPrivacyDataManagerInternal#updatePermissions(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updatePermissions(DataIdentifier dataId, String agreementId,
			EntityIdentifier ownerId, EntityIdentifier requestorId) {
		// TODO Auto-generated method stub

	}
}
