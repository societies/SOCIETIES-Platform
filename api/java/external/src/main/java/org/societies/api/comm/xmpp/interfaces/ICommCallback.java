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
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * The Interface ICommCallback.
 *
 * @author Miquel Martin (NEC)
 * 
 * Implementors of this interface are meant to process the replies resulting
 * from {@link Stanza} messages of the IQ type.
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.REQUIRED)
public interface ICommCallback {
	
	/**
	 * Gets the XML namespaces which this feature handles.
	 *
	 * @return the XML namespaces
	 */
	List<String> getXMLNamespaces();

	/**
	 * Get the java package to which the objects this feature deals with belong.
	 *
	 * @return the java packages
	 */
	List<String> getJavaPackages();
	
	/**
	 * Receive a result.
	 *
	 * @param stanza information regarding the stanza that wrapped the payload
	 * (e.g. To/From IDs)
	 * @param payload the payload of the result message
	 */
	void receiveResult(Stanza stanza, Object payload);

	/**
	 * Receive an error.
	 *
	 * @param stanza information regarding the error stanza (e.g. To/From IDs)
	 * @param error the error
	 */
	void receiveError(Stanza stanza, XMPPError error);
	
	/**
	 * Receive Info
	 *
	 * @param stanza the stanza
	 * @param node the node
	 * @param info the info
	 */
	void receiveInfo(Stanza stanza, String node, XMPPInfo info);
	
	/**

	 * Receive Items
	 *
	 * @param stanza the stanza
	 * @param node the node
	 * @param items the items
	 */
	void receiveItems(Stanza stanza, String node, List<String> items);
	
	/**
	 * Receive a Message
	 *
	 * @param  stanza information regarding the stanza that wrapped the payload
	 * (e.g. To/From IDs)
	 * @param payload the payload of the message
	 */
	void receiveMessage(Stanza stanza, Object payload);
}
