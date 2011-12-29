package org.societies.security.digsig;

import org.societies.api.security.digsig.ISignatureMgr;

public class SignatureMgr implements ISignatureMgr {

	private final String TAG = SignatureMgr.class.getName();

	@Override
	public String signXml(String xml, String id) {
		System.out.println(TAG + ", " + "signXml(" + xml + ", " + id + ")");
		return "signature";  // FIXME
	}

	@Override
	public boolean verify(String xml) {
		System.out.println(TAG + ", " + "verify(" + xml + ")");
		return false;  // FIXME
	}

}
