package org.societies.security.storage;

import org.societies.api.internal.security.storage.ISecureStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureStorage implements ISecureStorage {

	private static Logger LOG = LoggerFactory.getLogger(SecureStorage.class);

	public SecureStorage() {
		LOG.info("SecureStorage()");
	}
	
	public String getPassword(String id) {
		LOG.debug("getPassword({})", id);
		return "fooPass";  // FIXME
	}
}
