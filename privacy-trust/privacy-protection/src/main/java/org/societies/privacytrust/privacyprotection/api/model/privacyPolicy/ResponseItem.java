package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.Serializable;

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
		String str = "\n<Response>";
		str = str.concat(this.item.toXMLString());
		str = str.concat(this.decisionAsXML());
		str = str.concat("\n</Response>");
		return str;
	}

	public String decisionAsXML(){
		String str = "\n<Decision>";
		str = str.concat("\n\t<Attribute AttributeId=\"Decision\" " +
		"\n\t\t\tDataType=\"org.personalsmartspace.spm.negotiation.api.platform.Decision\">");
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
}
