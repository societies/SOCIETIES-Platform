package org.societies.privacytrust.privacyprotection.api.model.preference;


import java.io.Serializable;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.TargetMatchConstants;


/**
 * The Subject class embeds the identity of the provider CSS. 
 * @author Elizabeth
 *
 */
public class Subject implements Serializable{

	private EntityIdentifier dpi;
	private ServiceResourceIdentifier serviceID;

	public Subject(){
		
	}
	public Subject(EntityIdentifier dpi){
		this.dpi = dpi;
	}

	public Subject(EntityIdentifier dpi, ServiceResourceIdentifier serviceID){
		this.dpi = dpi;
		this.serviceID = serviceID;
	}


	public TargetMatchConstants getType(){
		return TargetMatchConstants.SUBJECT;
	}

	public String toXMLString(){
		String str = "\n<Subject>";
		if (this.dpi!=null){
			str = str.concat(this.dpiToXMLString());
		}
		if (this.serviceID!=null){
			str = str.concat(this.serviceIDToXMLString());
		}
		str = str.concat("\n</Subject>");
		return str;
	}

	private String dpiToXMLString(){
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\"" +
		"\n \t\t\tDataType=\"org.personalsmartspace.sre.api.pss3p.EntityIdentifier\">");

		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.dpi.toUriString());
		str = str.concat("</AttributeValue>");

		str = str.concat("\n\t</Attribute>");
		return str;
	}
	
	private String serviceIDToXMLString(){
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"serviceID\"" +
				"\n\t\t\tDataType=\"org.personalsmartspace.sre.api.pss3p.ServiceResourceIdentifier\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.serviceID.toUriString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		return str;
		
	}
	public String toString(){
		return this.toXMLString();
	}
	
	public EntityIdentifier getDPI(){
		return this.dpi;
	}
	
	public ServiceResourceIdentifier getServiceID(){
		return this.serviceID;
	}
	

}
