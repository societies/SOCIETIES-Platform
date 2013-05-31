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

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

/**
 * Mock class for ICommManager.
 *
 * @author Chris Lima
 *
 */
public class MockCommManager implements ICommManager {

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#UnRegisterCommManager()
	 */
	@Override
	public boolean UnRegisterCommManager() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#addRootNode(org.societies.api.comm.xmpp.datatypes.XMPPNode)
	 */
	@Override
	public void addRootNode(XMPPNode arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#getIdManager()
	 */
	@Override
	public IIdentityManager getIdManager() {
		return new MockIdentityManager();
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#getInfo(org.societies.api.identity.IIdentity, java.lang.String, org.societies.api.comm.xmpp.interfaces.ICommCallback)
	 */
	@Override
	public String getInfo(IIdentity arg0, String arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#getItems(org.societies.api.identity.IIdentity, java.lang.String, org.societies.api.comm.xmpp.interfaces.ICommCallback)
	 */
	@Override
	public String getItems(IIdentity arg0, String arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#isConnected()
	 */
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#register(org.societies.api.comm.xmpp.interfaces.IFeatureServer)
	 */
	@Override
	public void register(IFeatureServer arg0) throws CommunicationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#register(org.societies.api.comm.xmpp.interfaces.ICommCallback)
	 */
	@Override
	public void register(ICommCallback arg0) throws CommunicationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#removeRootNode(org.societies.api.comm.xmpp.datatypes.XMPPNode)
	 */
	@Override
	public void removeRootNode(XMPPNode arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#sendIQGet(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object, org.societies.api.comm.xmpp.interfaces.ICommCallback)
	 */
	@Override
	public void sendIQGet(Stanza arg0, Object arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#sendIQSet(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object, org.societies.api.comm.xmpp.interfaces.ICommCallback)
	 */
	@Override
	public void sendIQSet(Stanza arg0, Object arg1, ICommCallback arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#sendMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void sendMessage(Stanza arg0, Object arg1)
			throws CommunicationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommManager#sendMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.lang.Object)
	 */
	@Override
	public void sendMessage(Stanza arg0, String arg1, Object arg2)
			throws CommunicationException {
		// TODO Auto-generated method stub

	}

}
