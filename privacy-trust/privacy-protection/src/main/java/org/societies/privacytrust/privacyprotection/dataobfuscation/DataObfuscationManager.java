package org.societies.privacytrust.privacyprotection.dataobfuscation;

import org.societies.privacytrust.privacyprotection.api.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;

/**
 * Implementation of IDataObfuscationManager
 * @state skeleton 
 * @author olivierm
 */
public class DataObfuscationManager implements IDataObfuscationManager {
	@Override
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException {
		// TODO : populate this stub function
		
		// -- Verify params
		if (null == dataWrapper) {
			PrivacyException e = new PrivacyException("No data to obfuscate");
			if (null != listener) {
				listener.onObfuscationAborted("No data to obfuscate", e);
			}
			return null;
		}
		if (dataWrapper instanceof DataWrapper && null == ((DataWrapper) dataWrapper).getObfuscator()) {
			PrivacyException e = new PrivacyException("Not enought information in the wrapper to obfuscate this data");
			if (null != listener) {
				listener.onObfuscationAborted("Not enought information in the wrapper to obfuscate this data", e);
			}
			return null;
		}
		
		// -- Obfuscate
		IDataWrapper obfuscatedDataWrapper = null;
		// Check if it is ready for obfuscation
		if (!dataWrapper.isReadyForObfuscation()) {
			PrivacyException e = new PrivacyException("This data wrapper is not ready for obfuscation. Data are needed.");
			listener.onObfuscationAborted("This data wrapper is not ready for obfuscation. Data are needed.", e);
			return null;
		}
		try {
			// Obfuscation
			obfuscatedDataWrapper = ((DataWrapper) dataWrapper).getObfuscator().obfuscateData(obfuscationLevel, listener);
			// Persistence
			if (dataWrapper.isPersistenceEnabled()) {
				// TODO: persiste the obfuscated data using a data broker
//				System.out.println("Persist the data "+dataWrapper.getDataId());
			}
			// Call listener
			listener.onObfuscationDone(obfuscatedDataWrapper);
		}
		catch(Exception e) {
			listener.onObfuscationAborted("Obfuscation aborted", e);
		}
		return obfuscatedDataWrapper;
	}

	@Override
	public DataIdentifier hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException {
		// TODO : populate this stub function
		
		// -- Verify params
		if (null == dataWrapper) {
			PrivacyException e = new PrivacyException("No data: so, we can't search obfuscated version");
			if (null != listener) {
				listener.onObfuscationAborted("No data: so, we can't search obfuscated version", e);
			}
			return null;
		}

		
		// -- Search obfuscatred version
		if (dataWrapper.isPersistenceEnabled()) {
			// TODO: retrieve obfsucated data ID using data broker
			// An obfuscated version exist
			if (false) {
//				System.out.println("Retrieve the persisted data id of data id "+dataWrapper.getDataId());
			}
		}
		// There is no obfuscated version
		listener.onObfuscatedVersionRetrieved(dataWrapper.getDataId(), false);
		return dataWrapper.getDataId();
	}

}
