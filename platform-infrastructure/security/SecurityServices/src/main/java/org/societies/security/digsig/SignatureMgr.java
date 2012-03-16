package org.societies.security.digsig;

import org.societies.api.security.digsig.ISignatureMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureMgr implements ISignatureMgr {

	private static Logger LOG = LoggerFactory.getLogger(SignatureMgr.class);

	@Override
	public String signXml(String xml, String xmlNodeId, String identity) {
		
		LOG.debug("signXml(" + xml + ", " + xmlNodeId + ", " + identity + ")");

		/*
		SecurityManager sm = new SecurityManager();
		sm.
		*/
		
		/*
		try {
			throw new Exception();
		} catch (Exception e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			LOG.debug("stackTrace length = {}", stackTrace.length);
			if (stackTrace != null) {
				for (StackTraceElement st : stackTrace) {
					LOG.debug(" ");
					LOG.debug("  ClassName : {}", st.getClassName());
					//LOG.debug("  FileName  : {}", st.getFileName());
					//LOG.debug("  MethodName: {}", st.getMethodName());
				}
			}
		}
		*/
		
		return "";  // FIXME
	}

	@Override
	public boolean verify(String xml) {
		LOG.debug("verify({})", xml);
		return false;  // FIXME
	}
}
