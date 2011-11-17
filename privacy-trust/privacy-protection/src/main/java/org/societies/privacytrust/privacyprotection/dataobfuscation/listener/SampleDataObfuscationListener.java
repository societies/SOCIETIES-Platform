package org.societies.privacytrust.privacyprotection.dataobfuscation.listener;

import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;

/**
 * @state skeleton 
 * @author olivierm
 */
public class SampleDataObfuscationListener implements IDataObfuscationListener {
	@Override
	public void onObfuscationDone(IDataWrapper data) {
		System.out.println(data);
	}

	@Override
	public void onObfuscationCancelled(String msg) {
		System.out.println("Cancelled "+msg);
	}

	@Override
	public void onObfuscationAborted(String msg, Exception e) {
		System.out.println("Aborted "+msg);
		e.printStackTrace();
	}

	@Override
	public void onObfuscatedVersionRetrieved(DataIdentifier dataId, boolean retrieved) {
		if (retrieved) {
			System.out.println("Obfuscated version retrieved: "+dataId);
		}
		else {
			System.out.println("Not obfuscated version retrieved: "+dataId);
		}
	}
}
