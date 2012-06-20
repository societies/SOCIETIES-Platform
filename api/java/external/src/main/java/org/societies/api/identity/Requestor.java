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
package org.societies.api.identity;

import java.io.Serializable;

/**
 * This class is used to represent a CSS requesting resources 
 *
 * @author Eliza, Nicolas, Olivier
 *
 */
public class Requestor implements Serializable {
	private static final long serialVersionUID = -4907392226764120739L;
	private final IIdentity requestorId;

	
	/**
	 * Create a CSS requestor from the CSS identity
	 * @param requestorId CSS identity
	 */
	public Requestor(IIdentity requestorId){
		this.requestorId = requestorId;
		
	}
	
	
	/**
	 * Identity of the resource requestor
	 * @return the CSS identity
	 */
	public IIdentity getRequestorId() {
		return requestorId;
	}

	
	/* *************************
	 *         Tools           *
	 ************************* */
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Requestor: [CSS Identity: "+requestorId.getJid()+"]";
	}
	
	/**
	 * Return a string (XML formatted) representing the object.
	 * This string doesn't contain the XML header, but represents exactly
	 * all data containing in this object. The generated string can be used
	 * to regenerate this Java object.
	 * @return a XML string representation of the object
	 */
	public String toXMLString() {
		String subjectIdType = new String("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		StringBuilder str = new StringBuilder("\n\t<Attribute AttributeId=\""+subjectIdType+"\"");
		str.append("\n\t\t\tDataType=\""+IIdentity.class.getCanonicalName()+"\">");
		str.append("\n\t\t<AttributeValue>");
		str.append(requestorId.getJid());
		str.append("</AttributeValue>");
		str.append("\n\t</Attribute>");
		return str.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestorId == null) ? 0 : requestorId.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Requestor other = (Requestor) obj;
		if (requestorId == null) {
			if (other.requestorId != null) {
				return false;
			}
		} else if (!requestorId.equals(other.requestorId)) {
			return false;
		}
		return true;
	}
}
