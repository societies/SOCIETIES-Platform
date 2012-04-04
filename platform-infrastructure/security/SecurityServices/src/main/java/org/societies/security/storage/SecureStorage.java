package org.societies.security.storage;

import org.societies.api.internal.security.storage.ISecureStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureStorage implements ISecureStorage {

	private static Logger LOG = LoggerFactory.getLogger(SecureStorage.class);

	public SecureStorage() {
		LOG.info("SecureStorage()");
	}
	
	@Override
	public String getPassword(String id) {
		LOG.debug("getPassword({})", id);
		return "fooPass";  // FIXME
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.storage.ISecureStorage#getDocument(java.lang.String)
	 */
	@Override
	public byte[] getDocument(String id) {
		LOG.debug("getDocument({})", id);
		return "fooDoc".getBytes();  // FIXME
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.storage.ISecureStorage#putDocument(java.lang.String, byte[])
	 */
	@Override
	public boolean putDocument(String id, byte[] doc) {
		LOG.debug("putDocument({}, ...)", id);
		return true;  // FIXME
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.storage.ISecureStorage#putPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean putPassword(String id, String passwd) {
		LOG.debug("putPassword({}, ...)", id);
		return true;  // FIXME
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.storage.ISecureStorage#getDocumentIds()
	 */
	@Override
	public String[] getDocumentIds() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.security.storage.ISecureStorage#getPasswordIds()
	 */
	@Override
	public String[] getPasswordIds() {
		// TODO Auto-generated method stub
		return null;
	}
}
