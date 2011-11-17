/**
 * 
 */
package org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator;

import org.societies.privacytrust.privacyprotection.api.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper;

/**
 * @state skeleton 
 * @author olivierm
 */
public class SampleObfuscator implements IDataObfuscator {
	private int param1;
	
	// -- CONSTRUCTOR
	public SampleObfuscator(int param1) {
		this.param1 = param1;
	}

	
	// -- METHODS
	@Override
	public IDataWrapper obfuscateData(double obfuscationLevel,
			IDataObfuscationListener listener) throws PrivacyException {
		// TODO : populate this stub function
		// Obfuscate
		param1= 0;
		return new SampleWrapper(param1);
	}


	// -- GET/SET
	/**
	 * @return the param1
	 */
	public int getParam1() {
		return param1;
	}
	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(int param1) {
		this.param1 = param1;
	}
}
