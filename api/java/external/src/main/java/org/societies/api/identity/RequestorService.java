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

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * This class is used to represent a Service (provided by a CSS) requesting access to resources.
 *
 * @author Olivier, Eliza, Nicolas 
 *
 */
public class RequestorService extends Requestor implements Serializable{

	private final ServiceResourceIdentifier requestorServiceId;
	
	public RequestorService(IIdentity requestorId, ServiceResourceIdentifier requestorServiceId) {
		super(requestorId);
		this.requestorServiceId = requestorServiceId;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the requestorServiceId
	 */
	public ServiceResourceIdentifier getRequestorServiceId() {
		return requestorServiceId;
	}

	@Override
	public String toXMLString(){
		String parent = super.toXMLString();
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"serviceID\"" +
				"\n\t\t\tDataType=\"org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.requestorServiceId.toString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		return parent.concat("\n"+str);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((requestorServiceId == null) ? 0 : requestorServiceId
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RequestorService)) {
			return false;
		}
		RequestorService other = (RequestorService) obj;
		if (requestorServiceId == null) {
			if (other.requestorServiceId != null) {
				return false;
			}
		} else if (!requestorServiceId.equals(other.requestorServiceId)) {
			return false;
		}
		return true;
	}
	
	
	
}
