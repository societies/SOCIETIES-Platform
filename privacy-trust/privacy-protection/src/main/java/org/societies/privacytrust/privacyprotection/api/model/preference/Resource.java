package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.TargetMatchConstants;

/**
 * the Resource class is used to represent  a piece of data type belonging to the user 
 * (i.e context data, preference data, profile data). It contains the id of the data and the type of data. 
 * @author Elizabeth
 *
 */
public class Resource implements Serializable{

	private ICtxAttributeIdentifier ctxIdentifier;
	private String contextType;
	
	private Resource(){
		
	}
	public Resource(ICtxAttributeIdentifier ctxId){
		this.ctxIdentifier = ctxId;
		this.contextType = ctxId.getType();
	}
	
	public Resource(String type){
		this.contextType = type;
	}
	public TargetMatchConstants getType(){
		return TargetMatchConstants.RESOURCE;
	}
	
	public String getContextType(){
		return this.contextType;
	}
	
	public ICtxAttributeIdentifier getCtxIdentifier(){
		return this.ctxIdentifier;
	}
	
	public void stripIdentifier(){
		this.ctxIdentifier = null;
	}
	
	public void setPublicCtxIdentifier(ICtxAttributeIdentifier ctxId){
		this.ctxIdentifier = ctxId;
	}
	
	public String toXMLString(){
		String str = "\n<Resource>";
		if (this.ctxIdentifier!=null){
			str = str.concat(this.ctxIDToXMLString());
		}
		if (this.contextType!=null){
			str = str.concat(this.ctxTypeToXMLString());
		}
		str = str.concat("\n</Resource>");
		return str;
	}
	
	private String ctxIDToXMLString(){
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:resource-id\"" +
		"\n \t\t\tDataType=\"org.personalsmartspace.cm.model.api.pss3p.ICtxAttributeIdentifier\">");

		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.ctxIdentifier.toUriString());
		str = str.concat("</AttributeValue>");

		str = str.concat("\n\t</Attribute>");
		return str;
	}
	
	private String ctxTypeToXMLString(){
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"contextType\"" +
				"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.contextType);
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		return str;	
		}
	
	public String toString(){
		return this.toXMLString();
	}
}

