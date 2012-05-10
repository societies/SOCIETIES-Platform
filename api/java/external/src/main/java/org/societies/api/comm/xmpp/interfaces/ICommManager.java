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

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * The Interface ICommManager.
 *
 * @author Joao M. Goncalves (PTIN)
 * 
 * This is the interface of the service exposed by this OSGi bundle. It
 * defines methods that allow other bundles to send and register for the
 * receipt of XMPP messages. This service handles the connection,
 * parsing and routing of messages, allowing individual bundles to focus
 * on the specific logic. Each registered bundle must be able to address
 * some namespace. After a bundle registrers with the
 * CommunicationFrameworkBundle claiming some namespace, the
 * CommunicationFrameworkBundle will automatically adjust its supported
 * features in order to include that namespace.
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ICommManager {
	
	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected();
	

	/**
	 * Register the Feature Server.
	 *
	 * @param featureServer the feature server
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void register(IFeatureServer featureServer) throws CommunicationException;
	

	/**
	 * Register the Callback class.
	 *
	 * @param messageCallback the message callback
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void register(ICommCallback messageCallback) throws CommunicationException;


	/**
	 * Send iq get to the XMPP Server.
	 *
	 * @param stanza the stanza
	 * @param payload the payload
	 * @param callback the callback
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void sendIQGet(Stanza stanza, Object payload,
			ICommCallback callback) throws CommunicationException;
	

	/**
	 * Send iq set to the XMPP Server.
	 *
	 * @param stanza the stanza
	 * @param payload the payload
	 * @param callback the callback
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void sendIQSet(Stanza stanza, Object payload,
			ICommCallback callback) throws CommunicationException;


	/**
	 * Send a message to the XMPP Server.
	 *
	 * @param stanza the stanza
	 * @param type the type
	 * @param payload the payload
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void sendMessage(Stanza stanza, String type, Object payload)
			throws CommunicationException;
	

	/**
	 * Send a message to the XMPP Server.
	 *
	 * @param stanza the stanza
	 * @param payload the payload
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public void sendMessage(Stanza stanza, Object payload)
			throws CommunicationException;


	/**
	 * Adds the root node.
	 *
	 * @param newNode the new node
	 */
	public void addRootNode(XMPPNode newNode);


	/**
	 * Removes the root node.
	 *
	 * @param node the node to be removed
	 */
	public void removeRootNode(XMPPNode node);
	

	/**
	 * Gets the disco#info detail from the XMPP Server.
	 *
	 * @param entity the entity
	 * @param node the node
	 * @param callback the callback
	 * @return the info
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public String getInfo(IIdentity entity, String node, ICommCallback callback) throws CommunicationException;
	

	/**
	 * Gets the disco#items detail from the XMPP Server.
	 *
	 * @param entity the entity
	 * @param node the node
	 * @param callback the callback
	 * @return the items
	 * @throws CommunicationException the communication exception {@link  org.societies.api.comm.xmpp.exceptions.CommunicationException}
	 */
	public String getItems(IIdentity entity, String node, ICommCallback callback) throws CommunicationException;
	

	/**
	 * Gets the ID manager.
	 *
	 * @return the id manager
	 */
	public IIdentityManager getIdManager();
	

	/**
	 * Unregister the Comm Manager.
	 *
	 * @return true, if successful
	 */
	public boolean UnRegisterCommManager();

	// void sendResult(? originalPayload, Object resultPayload); //TODO only
	// needed for asynch IQ handling
}
