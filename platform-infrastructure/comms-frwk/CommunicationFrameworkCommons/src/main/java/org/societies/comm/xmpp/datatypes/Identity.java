/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
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

/**
 * @author Joao M. Goncalves (PTIN)
 * 
 * TODO
 * 
 */

package org.societies.comm.xmpp.datatypes;

public class Identity {
	private IdentityType type;
	private String identifier;
	private String domainIdentifier;
	
	public static Identity getIdentityFromJid(String jid) {
		String[] parts = jid.split("@");
		if (parts.length>1)
			return new Identity(IdentityType.CSS, parts[0], parts[1]);
		else {
			int firstDot = jid.indexOf(".");
			return new Identity(IdentityType.CIS, jid.substring(0,firstDot), jid.substring(firstDot+1));
		}
	}
	
	public Identity(IdentityType type, String identifier, String domainIdentifier) {
		this.type = type;
		this.identifier = identifier;
		this.domainIdentifier = domainIdentifier;
	}

	public boolean equals(Identity otherIdentity) {
		if (identifier.equals(otherIdentity.getIdentifier()) && domainIdentifier.equals(otherIdentity.getDomainIdentifier()))
			return true;
		else
			return false;
	}
	
	// TODO there is an implicit CSS -> XMPP Client & CIS -> XMPP XC decision here
	public String toString() {
		if (type.equals(IdentityType.CSS))
			return identifier+"@"+domainIdentifier;
		else
			return identifier+"."+domainIdentifier;
	}
	
	public enum IdentityType {
		CSS,
		CIS;
	}
	
	public IdentityType getType() {
		return type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}
	
	public String getJid(){
		return toString();
	}
}
