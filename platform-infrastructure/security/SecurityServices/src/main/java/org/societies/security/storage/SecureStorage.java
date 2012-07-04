/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.storage;

import org.societies.api.internal.security.storage.ISecureStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mitja Vardjan
 */
public class SecureStorage implements ISecureStorage {

	private static Logger LOG = LoggerFactory.getLogger(SecureStorage.class);

	public SecureStorage() {
		LOG.info("SecureStorage()");
	}
	
	@Override
	public String getPassword(String id) {
		LOG.debug("getPassword({})", id);

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
