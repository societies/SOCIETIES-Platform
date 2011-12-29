package org.societies.security.digsig;

import org.societies.api.security.digsig.ISignatureMgr;

public class SignatureMgr implements ISignatureMgr {

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
