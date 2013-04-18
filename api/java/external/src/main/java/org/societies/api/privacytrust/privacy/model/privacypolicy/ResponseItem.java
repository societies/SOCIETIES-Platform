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
package org.societies.api.privacytrust.privacy.model.privacypolicy;


import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * The ResponseItem class represents the response to a RequestItem contained in the RequestPolicy of a service provider. 
 * It is constructed after the privacy preference evaluation has been performed and the system can decide to permit or deny the request. 
 * The ResponseItem contains the requestItem object and a Decision flag. The Decision flag can be any of the types listed in the Decision enumeration. 
 * INDETERMINATE suggests that the RequestItem has be altered per the user's wishes (such as adding extra conditions or removing an action) 
 * and needs to be accepted by the service provider. NOT_APPLICABLE suggests that the piece of data the RequestItem refers to does not exist 
 * as a type in the CSS (for example a service may request access to room temperature but the CSS does not have such a type in the system 
 * because the CSS has no temperature sensor )
 * @author Elizabeth
 *
 */
@Deprecated
public class ResponseItem implements Serializable{

	RequestItem item;
	Decision decision;

	private ResponseItem(){

	}
	public ResponseItem(RequestItem item, Decision decision){
		this.item = item;
		this.decision = decision;
	}

	public Decision getDecision(){
		return this.decision;
	}

	public RequestItem getRequestItem(){
		return this.item;
	}

	public String toXMLString(){
		StringBuilder str = new StringBuilder("\n<Response>");
		str.append(getRequestItem().toXMLString());
		str.append(decisionAsXML());
		str.append("\n</Response>");
		return str.toString();
	}

	public String decisionAsXML(){
		String str = "\n<Decision>";
		str = str.concat("\n\t<Attribute AttributeId=\"Decision\" " +
		"\n\t\t\tDataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.decision.toString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		str = str.concat("\n</Decision>");
		return str;
	}
	public String toString(){
		return this.toXMLString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// -- Verify reference equality
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		// -- Verify obj type
		ResponseItem rhs = (ResponseItem) obj;
		return new EqualsBuilder()
			.append(this.getDecision().name(), rhs.getDecision().name())
			.append(this.getRequestItem(), rhs.getRequestItem())
			.isEquals();
	}
}
