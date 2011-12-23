package org.societies.security.sign;

import org.societies.api.security.sign.ISign;

public class Sign implements ISign {

	@Override
	public String signXml(String xml, String id) {
		// TODO
		return "signature";
	}

	@Override
	public boolean verify(String xml) {
		// TODO
		return false;
	}

}
