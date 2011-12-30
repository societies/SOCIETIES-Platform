package org.societies.security.digsig;

import org.societies.api.security.digsig.ISignatureMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureMgr implements ISignatureMgr {

	private static Logger LOG = LoggerFactory.getLogger(SignatureMgr.class);

	@Override
	public String signXml(String xml, String id) {
		LOG.debug("signXml({}, {})", xml, id);
		return "signature";  // FIXME
	}

	@Override
	public boolean verify(String xml) {
		LOG.debug("verify({})", xml);
		return false;  // FIXME
	}
}
