package org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener;

import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;


/**
 * This interface defines a listener for an obfuscation operation.
 * @author olivierm
 * @date 14 oct. 2011
 */
public interface IDataObfuscationListener {
	public void onObfuscationDone(IDataWrapper data);
	public void onObfuscationCancelled(String msg);
	public void onObfuscationAborted(String msg, Exception e);
	public void onObfuscatedVersionRetrieved(DataIdentifier dataId, boolean retrieved);
}
