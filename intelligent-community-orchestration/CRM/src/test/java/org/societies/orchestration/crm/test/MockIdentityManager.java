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
package org.societies.orchestration.crm.test;

import java.util.Set;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;

/**
 * Mock class for IIdentityManager
 *
 * @author Christopher Viana Lima
 *
 */
public class MockIdentityManager implements IIdentityManager {

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#fromFullJid(java.lang.String)
	 */
	@Override
	public INetworkNode fromFullJid(String arg0) throws InvalidFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#fromJid(java.lang.String)
	 */
	@Override
	public IIdentity fromJid(String arg0) throws InvalidFormatException {
		// TODO Auto-generated method stub
		return new MockIdentity(arg0);
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#getCloudNode()
	 */
	@Override
	public INetworkNode getCloudNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#getDomainAuthorityNode()
	 */
	@Override
	public INetworkNode getDomainAuthorityNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#getIdentityContextMapper()
	 */
	@Override
	public IIdentityContextMapper getIdentityContextMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#getPublicIdentities()
	 */
	@Override
	public Set<IIdentity> getPublicIdentities() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#getThisNetworkNode()
	 */
	@Override
	public INetworkNode getThisNetworkNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#isMine(org.societies.api.identity.IIdentity)
	 */
	@Override
	public boolean isMine(IIdentity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#newMemorableIdentity(java.lang.String)
	 */
	@Override
	public IIdentity newMemorableIdentity(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#newTransientIdentity()
	 */
	@Override
	public IIdentity newTransientIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.identity.IIdentityManager#releaseMemorableIdentity(org.societies.api.identity.IIdentity)
	 */
	@Override
	public boolean releaseMemorableIdentity(IIdentity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
