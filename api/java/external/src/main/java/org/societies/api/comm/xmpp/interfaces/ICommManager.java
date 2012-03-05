/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
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

package org.societies.api.comm.xmpp.interfaces;

import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

/**
 * @author Joao M. Goncalves (PTIN)
 * 
 *         This is the interface of the service exposed by this OSGi bundle. It
 *         defines methods that allow other bundles to send and register for the
 *         receipt of XMPP messages. This service handles the connection,
 *         parsing and routing of messages, allowing individual bundles to focus
 *         on the specific logic. Each registered bundle must be able to address
 *         some namespace. After a bundle registrers with the
 *         CommunicationFrameworkBundle claiming some namespace, the
 *         CommunicationFrameworkBundle will automatically adjust its supported
 *         features in order to include that namespace.
 * 
 */
public interface ICommManager {
	public void register(IFeatureServer featureServer) throws CommunicationException;
	
	public void register(ICommCallback messageCallback) throws CommunicationException;

	public void sendIQGet(Stanza stanza, Object payload,
			ICommCallback callback) throws CommunicationException;
	
	public void sendIQSet(Stanza stanza, Object payload,
			ICommCallback callback) throws CommunicationException;

	public void sendMessage(Stanza stanza, String type, Object payload)
			throws CommunicationException;
	
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException;

	public void addRootNode(XMPPNode newNode);

	public void removeRootNode(XMPPNode node);
	
	public String getInfo(IIdentity entity, String node, ICommCallback callback) throws CommunicationException;
	
	public String getItems(IIdentity entity, String node, ICommCallback callback) throws CommunicationException;
	
	public IIdentityManager getIdManager();

	// void sendResult(? originalPayload, Object resultPayload); //TODO only
	// needed for asynch IQ handling
}
