package org.societies.privacytrust.privacyprotection.api.internal;

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
	 * <ul>
	 * 	<li>Context Broker, to obfuscate context data (e.g. obfuscate a
	 * location)</li>
	 * 	<li>Content Manager, to obfuscate content data (e.g. blur faces in a
	 * picture)</li>
	 * 	<li>Anyone who wants to obfuscate a data</li>
	 * </ul>
	 * <font color="#7f9fbf"><b>@return</b></font><font color="#3f5fbf"> Obfuscated
	 * data wrapped in a DataWrapper (of the same type that the one used to
	 * instantiate the <u>obfuscator</u>)</font>
	 * <font color="#7f9fbf"><b>@throws</b></font><font color="#3f5fbf">
	 * Exception</font>
	 * 
	 * @param dataWrapper    Data wrapped in a relevant data wrapper. Use
	 * DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel    Obfuscation level, a real number between 0 and 1.
	 * With 1, there is no obfuscation
	 * @param listener    A listener to receive the result
	 */
	public Object obfuscateData(Object dataWrapper, double obfuscationLevel, Object listener);

	/**
	 * Check if there is an obfuscated version of the data and return its ID.
	 * Example of use:
	 * <ul>
	 * 	<li>Context Broker, before retrieving the data, it can try to find an already
	 * obfuscated data and retrieve it instead of the real data. Not all obfuscated
	 * data are stored to be reused, but it may be in some cases. (e.g. long
	 * processing like blur faces in a picture)</li>
	 * 	<li>Content Manager, idem</li>
	 * 	<li>Anyone who wants to obfuscate a data</li>
	 * </ul>
	 * <b>@return</b> ID of the obfuscated version of the data if the persistence is
	 * enabled and if the obfuscated data exists
	 * <b>@return</b> otherwise ID of the non-obfuscated data
	 * <b>@throws</b> Exception
	 * 
	 * @param dataWrapper    Data ID wrapped in the relevant DataWrapper. Only the ID
	 * information is mandatory to retrieve an obfuscated version. Use
	 * DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel    Obfuscation level, a real number between 0 and 1.
	 * With 1, there is no obfuscation
	 * @param listener    A listener to receive the result
	 */
	public String hasObfuscatedVersion(Object dataWrapper, double obfuscationLevel, Object listener);

}