/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.Subject;





/**
 * This class represents the Request Policy of the Provider and lists the context types it is requesting access to, the Actions it is going to perform 
 * to these items and its own terms and conditions that define what happens to the data after disclosure
 * . 
 * @author Elizabeth
 *
 */
public class RequestPolicy implements Serializable{

	private Subject requestor;
	private List<RequestItem> requests;

	private RequestPolicy(){
		this.requests = new ArrayList<RequestItem>();
	}

	public RequestPolicy(List<RequestItem> requests){
		this.requests = requests;
	}
	public RequestPolicy(Subject sub, List<RequestItem> requests) {
		this.requestor = sub;
		this.requests = requests;
	}

	public List<RequestItem> getRequests(){
		return this.requests;
	}
	
	public Subject getRequestor(){
		return this.requestor;
	}

	public void setRequestor(Subject subject){
		this.requestor = subject;
	}
	public String toXMLString(){
		String str = "<RequestPolicy>";
		if (this.hasRequestor()){
			str = str.concat(this.requestor.toXMLString());
		}
		for (RequestItem item : requests){
			str = str.concat(item.toXMLString());
		}
		str = str.concat("</RequestPolicy>");
		return str;
	}
	
	public boolean hasRequestor(){
		return (this.requestor!=null);
	}
	public String toString(){
		return this.toXMLString();
	}
}
