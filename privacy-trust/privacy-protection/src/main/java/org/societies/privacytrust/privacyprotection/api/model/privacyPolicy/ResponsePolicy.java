package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.privacytrust.privacyprotection.api.model.preference.Subject;


/**
 * The ResponsePolicy class represents the response of the user to a requestPolicy of a service provider. 
 * The ResponsePolicy class contains ResponseItem objects for each of the RequestItem objects contained in 
 * the RequestPolicy of the service provider and a NegotiationStatus flag to denote the state of the negotiation.
 * @author Elizabeth
 *
 */
public class ResponsePolicy implements Serializable{

	private NegotiationStatus status;
	private List<ResponseItem> responses;
	private Subject subject;
	
	private ResponsePolicy(){
		this.responses = new ArrayList<ResponseItem>();
	}
	/**
	 * @param results
	 */
	public ResponsePolicy(Subject subject, List<ResponseItem> responses, NegotiationStatus status) {
		this.subject = subject;
		this.responses = responses;
		this.status = status;
		
	}

	public Subject getSubject(){
		return this.subject;
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
		str = str.concat(this.subject.toXMLString());
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
		"\n\t\t\tDataType=\"org.personalsmartspace.spm.negotiation.api.platform.NegotiationStatus\">");
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
