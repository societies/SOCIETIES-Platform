package org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener;

import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;


/**
 * This interface defines a listener for an obfuscation operation.
 * @author olivierm
 * @date 14 oct. 2011
 */
public interface IDataObfuscationListener {
	/**
	 * When an obfuscation have been done
	 * 
	 * @param data Obfuscated data in a wrapper
	 */
	public void onObfuscationDone(IDataWrapper data);
	/**
	 * When obfuscation action have been cancelled
	 * 
	 * @param msg Explaination message
	 */
	public void onObfuscationCancelled(String msg);
	/**
	 * When obfuscation action have been stopped by an error
	 * 
	 * @param msg Explaination message
	 * @param e Exception
	 */
	public void onObfuscationAborted(String msg, Exception e);
	/**
	 * When an obfuscated version of the data have been retrieved
	 * 
	 * @param dataId ID of the obfuscated version of the data if the persistence is enabled and if the obfuscated data exists, otherwise ID of the non-obfuscated data
	 * @param retrieved True if an obfuscated version has been retrieved, else false
	 */
	public void onObfuscatedVersionRetrieved(DataIdentifier dataId, boolean retrieved);
}
