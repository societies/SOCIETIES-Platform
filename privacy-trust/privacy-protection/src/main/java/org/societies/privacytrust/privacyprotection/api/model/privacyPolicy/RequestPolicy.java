package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.privacytrust.privacyprotection.api.model.preference.Subject;





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
