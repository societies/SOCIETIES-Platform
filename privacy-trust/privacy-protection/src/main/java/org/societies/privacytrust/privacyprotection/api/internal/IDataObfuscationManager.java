package org.societies.privacytrust.privacyprotection.api.internal;

import org.societies.privacytrust.privacyprotection.api.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;

/**
 * Internal interface to protect a data by obfuscating it
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:53
 */
public interface IDataObfuscationManager {
	/**
	 * Protect a data following the user preferences by obfuscating it to a correct
	 * obfuscation level. The data information are wrapped into a relevant data
	 * wrapper in order to execute the relevant obfuscation operation into relevant
	 * information.
	 * Example of use:
	 * - Context Broker, to obfuscate context data (e.g. obfuscate a
	 * location)
	 * - Content Manager, to obfuscate content data (e.g. blur faces in a
	 * picture)
	 * - Anyone who wants to obfuscate a data
	 * @param dataWrapper Data wrapped in a relevant data wrapper. Use DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1. With 0 there is no obfuscation
	 * @param listener A listener to receive the result
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to instantiate the obfuscator)
	 * @throws Exception
	 */
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException;

	/**
	 * Check if there is an obfuscated version of the data and return its ID.
	 * Example of use:
	 * - Context Broker, before retrieving the data, it can try to find an already
	 * obfuscated data and retrieve it instead of the real data. Not all obfuscated
	 * data are stored to be reused, but it may be in some cases. (e.g. long
	 * processing like blur faces in a picture)
	 * - Content Manager, same usage
	 * - Anyone who wants to obfuscate a data
	 * @param dataWrapper Data ID wrapped in the relevant DataWrapper. Only the ID information is mandatory to retrieve an obfuscated version. Use DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1. With 0 there is no obfuscation
	 * @param listener A listener to receive the result
	 * @return ID of the obfuscated version of the data if the persistence is enabled and if the obfuscated data exists
	 * @return otherwise ID of the non-obfuscated data
	 * @throws Exception
	 */
	public String hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException;
}