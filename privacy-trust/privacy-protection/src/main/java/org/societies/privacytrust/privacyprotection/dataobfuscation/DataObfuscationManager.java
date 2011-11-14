package org.societies.privacytrust.privacyprotection.dataobfuscation;

import org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.DataObfuscationException;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;

/**
 * Implementation of IDataObfuscationManager
 * State: skeleton 
 * @author olivierm
 */
public class DataObfuscationManager implements IDataObfuscationManager {
	@Override
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws DataObfuscationException {
		return null;
	}

	@Override
	public String hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws DataObfuscationException {
		return null;
	}

}
