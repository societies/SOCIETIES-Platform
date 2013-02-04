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

package org.societies.android.api.comms.xmpp;

import java.util.UUID;

import org.societies.api.identity.IIdentity;



/**
 * @author Joao M. Goncalves (PTIN)
 * The Stanza class, used to send messages to the XMPP server.
 */
public class Stanza {
	
	/** The id. */
	private String id;
	
	/** The IIdentity {@link org.societies.api.identity.IIdentity} of who is sending the message. */
	private IIdentity from;
	
	/** The IIdentity {@link org.societies.api.identity.IIdentity} of who will receive the message. */
	private IIdentity to;
	
	/**
	 * Instantiates a new stanza.
	 *
	 * @param id the id of the stanza
	 * @param from IIdentity {@link org.societies.api.identity.IIdentity} of who is sending the message
	 * @param to IIdentity {@link org.societies.api.identity.IIdentity} of who will receive the message 
	 */
	public Stanza(String id, IIdentity from, IIdentity to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Instantiates a new stanza.
	 *
	 * @param to IIdentity {@link org.societies.api.identity.IIdentity} of who will receive the message 
	 */
	public Stanza(IIdentity to) {
		this.id = UUID.randomUUID().toString();
		this.to = to;
	}
	
	/**
	 * Gets the id of the stanza.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the IIdentity {@link org.societies.api.identity.IIdentity} of who is sending the message
	 *
	 * @return the from
	 */
	public IIdentity getFrom() {
		return from;
	}

	/**
	 * Get the IIdentity {@link org.societies.api.identity.IIdentity} of who will receive the message.
	 *
	 * @return the to
	 */
	public IIdentity getTo() {
		return to;
	}

	/**
	 * Sets the id of the stanza.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the from of the stanza.
	 *
	 * @param from the new from
	 */
	public void setFrom(IIdentity from) {
		this.from = from;
	}

	/**
	 * Sets the to of the stanza.
	 *
	 * @param to the new to
	 */
	public void setTo(IIdentity to) {
		this.to = to;
	}

	/**
	 * To string.
	 *
	 * @return the string representation of the stanza
	 */
	@Override
	public String toString() {
		return "Stanza: from='"+from+"' to='"+to+"' id='"+id+"'";
	}
}
