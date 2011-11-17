package org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper;

import org.societies.privacytrust.privacyprotection.dataobfuscation.DataWrapper;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.SampleObfuscator;

/**
 * This is a sample wrapper, it doing nothing
 * @state skeleton 
 * @author olivierm
 */
public class SampleWrapper extends DataWrapper {
	private SampleObfuscator obfuscator;
	
	// -- CONSTRUCTOR
	public SampleWrapper(int param1) {
		super();
		obfuscator = new SampleObfuscator(param1);
		setObfuscator(obfuscator);
		setAsReadyForObfuscation();
	}

	
	// -- GET/SET
	/**
	 * @return the param1
	 */
	public double getParam1() {
		return obfuscator.getParam1();
	}
	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(int param1) {
		obfuscator.setParam1(param1);
		if (-1 != obfuscator.getParam1()) {
			setAsReadyForObfuscation();
		}
	}
}
