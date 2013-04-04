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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.identity.Requestor;

/**
 * This class represents the Request Policy of the Provider and lists the context types it is requesting access to, the Actions it is going to perform 
 * to these items and its own terms and conditions that define what happens to the data after disclosure
 * . 
 * @author Elizabeth
 *
 */
@Deprecated
public class RequestPolicy implements Serializable{

	private Requestor requestor;
	private List<RequestItem> requests;

	private RequestPolicy(){
		this.requests = new ArrayList<RequestItem>();
	}

	public RequestPolicy(List<RequestItem> requests){
		this.requests = requests;
	}
	public RequestPolicy(Requestor sub, List<RequestItem> requests) {
		this.requestor = sub;
		this.requests = requests;
	}

	public List<RequestItem> getRequests(){
		return this.requests;
	}

	public Requestor getRequestor(){
		return this.requestor;
	}

	public void setRequestor(Requestor subject){
		this.requestor = subject;
	}
	
	public String toXMLString(){
		StringBuilder str = new StringBuilder("<RequestPolicy>");
		if (this.hasRequestor()){
			str.append("<Subject>"+this.requestor.toXMLString()+"</Subject>");
		}
		for (RequestItem item : requests){
			str.append(item.toXMLString());
		}
		str.append("</RequestPolicy>");
		return str.toString();
	}

	public boolean hasRequestor(){
		return (this.requestor!=null);
	}
	public String toString(){
		return this.toXMLString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestor == null) ? 0 : requestor.hashCode());
		result = prime * result
				+ ((requests == null) ? 0 : requests.hashCode());
		return result;
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
		RequestPolicy rhs = (RequestPolicy) obj;
		return new EqualsBuilder()
			.append(this.getRequestor(), rhs.getRequestor())
			.append(this.getRequests(), rhs.getRequests())
			.isEquals();
	}


}
