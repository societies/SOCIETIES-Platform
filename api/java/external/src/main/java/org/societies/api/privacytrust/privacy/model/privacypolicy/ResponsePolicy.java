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
import java.util.ArrayList;
import java.util.List;
import org.societies.api.identity.Requestor;

/**
 * The ResponsePolicy class represents the response of the user to a requestPolicy of a service provider. 
 * The ResponsePolicy class contains ResponseItem objects for each of the RequestItem objects contained in 
 * the RequestPolicy of the service provider and a NegotiationStatus flag to denote the state of the negotiation.
 * @author Elizabeth
 *
 */
@Deprecated
public class ResponsePolicy implements Serializable{

	private NegotiationStatus status;
	private List<ResponseItem> responses;
	private Requestor Requestor;
	
	private ResponsePolicy(){
		this.responses = new ArrayList<ResponseItem>();
	}
	/**
	 * @param results
	 */
	public ResponsePolicy(Requestor Requestor, List<ResponseItem> responses, NegotiationStatus status) {
		this.Requestor = Requestor;
		this.responses = responses;
		this.status = status;
		
	}

	public Requestor getRequestor(){
		return this.Requestor;
	}
	public NegotiationStatus getStatus(){
		return this.status;
	}
	
	public List<ResponseItem> getResponseItems(){
		return this.responses;
	}
	
	public void addResponseItem(ResponseItem item){
		this.responses.add(item);
	}
	
	public void setStatus(NegotiationStatus status){
		this.status = status;
	}
	public String toXMLString(){
		String str = "\n<ResponsePolicy>";
		str = str.concat(this.Requestor.toXMLString());
		str = str.concat(this.statusToXML());
		str = str.concat("\n<Responses>");
		for (ResponseItem item : responses){
			str = str.concat(item.toXMLString());
		}
		str = str.concat("\n</Responses>");
		str = str.concat("\n</ResponsePolicy>");
		return str;
	}
	
	private String statusToXML(){
		String str = "\n<NegotiationStatus>";
		str = str.concat("\n\t<Attribute AttributeId=\"Decision\" " +
		"\n\t\t\tDataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationStatus\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.status.toString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		str = str.concat("\n</NegotiationStatus>");
		return str;		
	}
	
	@Override
	public String toString(){
		return this.toXMLString();
	}
}
