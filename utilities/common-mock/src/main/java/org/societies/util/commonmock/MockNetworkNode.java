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
package org.societies.util.commonmock;

import java.io.Serializable;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;


/**
 * Mock class for IIdentity
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class MockNetworkNode implements INetworkNode, Serializable {
	private static final long serialVersionUID = 716854261365638402L;
	protected IdentityType type;
	protected String identifier;
	protected String domainIdentifier;
	protected String nodeIdentifier;

	
	public MockNetworkNode(IdentityType type, String identifier, String domainIdentifier, String nodeIdentifier) {
		this.type = type;
		this.identifier = identifier;
		this.domainIdentifier = domainIdentifier;
		this.nodeIdentifier = nodeIdentifier;
	}
	public MockNetworkNode(String fullJid) {
		int pos = fullJid.indexOf('@');
		if( pos >0){
			this.type = IdentityType.CSS;

		}else{
			this.type = IdentityType.CIS;
			pos = fullJid.indexOf('.');
			if(pos >0){

			}else {
				throw new IllegalArgumentException("invalid jid");
			}
		}
		this.identifier = fullJid.substring(0,pos);
		this.domainIdentifier = fullJid.substring(pos+1);
		this.nodeIdentifier = "RICH";
	}

	
	@Override
	public String toString() {
		return getJid();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((domainIdentifier == null) ? 0 : domainIdentifier.toLowerCase().hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.toLowerCase().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IIdentity))
			return false;
		IIdentity other = (IIdentity) obj;
		if (domainIdentifier == null) {
			if (other.getDomain() != null)
				return false;
		} else if (!domainIdentifier.equalsIgnoreCase(other.getDomain()))
			return false;
		if (identifier == null) {
			if (other.getIdentifier() != null)
				return false;
		} else if (!identifier.equalsIgnoreCase(other.getIdentifier()))
			return false;
		return true;
	}

	public IdentityType getType() {
		return type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDomain() {
		return domainIdentifier;
	}

	public String getJid(){
		if (type.equals(IdentityType.CSS))
			return identifier+"@"+domainIdentifier;
		else
			return identifier+"."+domainIdentifier;
	}

	public String getBareJid() {
		String identity = ((IIdentity)this).getJid();
		int pos = identity.indexOf('/');
		if( pos >0){
			return identity.substring(0,pos);
		}
		return identity;
	}
	
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}
}
