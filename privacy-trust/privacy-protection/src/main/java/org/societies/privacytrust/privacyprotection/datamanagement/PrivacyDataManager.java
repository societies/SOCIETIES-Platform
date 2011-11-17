package org.societies.privacytrust.privacyprotection.datamanagement;

import org.societies.privacytrust.privacyprotection.api.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.external.IPrivacyDataManager;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponseItem;

/**
 * @state skeleton 
 * @author olivierm
 */
public class PrivacyDataManager implements IPrivacyDataManager {

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager#obfuscateData(org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper,
			double obfuscationLevel, IDataObfuscationListener listener)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager#hasObfuscatedVersion(org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public String hasObfuscatedVersion(IDataWrapper dataWrapper,
			double obfuscationLevel, IDataObfuscationListener listener)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.external.IPrivacyDataManager#checkPermission(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public ResponseItem checkPermission(Object dataId, Object ownerId,
			Object requestorId, Object serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.external.IPrivacyDataManager#checkPermission(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.String)
	 */
	@Override
	public ResponseItem checkPermission(Object dataId, Object ownerId,
			Object requestorId, String cisId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.external.IPrivacyDataManager#checkPermission(java.lang.Object, java.lang.Object, java.lang.String, org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy)
	 */
	@Override
	public ResponseItem checkPermission(Object dataId, Object ownerId,
			String requestorId, RequestPolicy usage) {
		// TODO Auto-generated method stub
		return null;
	}

}
