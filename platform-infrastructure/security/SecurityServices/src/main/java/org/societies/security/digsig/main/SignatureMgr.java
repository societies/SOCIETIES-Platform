package org.societies.security.digsig.main;

import org.societies.api.identity.IIdentity;
import org.societies.api.security.digsig.ISignatureMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureMgr implements ISignatureMgr {

	private static Logger LOG = LoggerFactory.getLogger(SignatureMgr.class);

	public SignatureMgr() {
		LOG.info("SignatureMgr()");
	}
	
	@Override
	public String signXml(String xml, String xmlNodeId, IIdentity identity) {
		
		LOG.debug("signXml(..., {}, {})", xmlNodeId, identity);

		return xml;  // FIXME
	}

	@Override
	public boolean verify(String xml) {
		LOG.debug("verify()");
		return true;  // FIXME
	}
}
