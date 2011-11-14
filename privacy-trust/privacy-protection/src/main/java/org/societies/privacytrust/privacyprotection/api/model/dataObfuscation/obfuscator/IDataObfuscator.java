package org.societies.privacytrust.privacyprotection.api.model.dataObfuscation.obfuscator;

import org.societies.privacytrust.privacyprotection.api.model.dataObfuscation.dataWrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.api.model.dataObfuscation.listener.IDataObfuscationListener;


/**
 * This interface defines an obfuscator.
 * An Obfuscator represents an obfuscation algorithm,
 * and each type of data needs an obfuscation algorithm.
 * @author Olivier Maridat
 * @date 14 oct. 2011
 */
public interface IDataObfuscator {
	/**
	 * Protect data wrapped in the obfuscator to a correct obfuscation level.
	 * 
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1.  With 0, there is no obfuscation
	 * @param listener A listener to receive the result
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to instanciate the obfuscator)
	 * @throws Exception
	 */
	public IDataWrapper obfuscateData(double obfuscationLevel, IDataObfuscationListener listener) throws Exception;
}
