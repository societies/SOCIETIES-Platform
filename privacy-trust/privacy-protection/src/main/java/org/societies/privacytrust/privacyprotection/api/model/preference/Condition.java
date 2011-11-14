package org.societies.privacytrust.privacyprotection.api.model.preference;


import java.io.IOException;
import java.io.Serializable;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.ConditionConstants;


/**
 * The Condition class represents a condition that has to be met by the provider or the user. 
 * Possible types of Conditions are listed in the ConditionConstants enumeration.
 * @author Elizabeth
 *
 */
public class Condition implements Serializable{

	private ConditionConstants theCondition;
	private String value;
	private boolean optional;
	
	private Condition(){
		
	}
	public Condition(ConditionConstants conditionName, String value){
		this.theCondition = conditionName;
		this.value = value;
		this.optional = true;
	}
	
	public Condition(ConditionConstants conditionName, String value, boolean isOptional){
		this.theCondition = conditionName;
		this.value = value;
		this.optional = isOptional;
	}
	
	public boolean isOptional(){
		return this.optional;
	}
	
	public void setOptional(boolean isOptional){
		this.optional = isOptional;
	}
	
	public ConditionConstants getConditionName(){
		return this.theCondition;
	}
	
	public String getValueAsString(){
		return this.value;
	}
	
	public String toXMLString(){
		String str = "\n<Condition>";
		str = str.concat("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" " +
				"\n\t\t\tDataType=\"org.personalsmartspace.spm.preference.api.platform.constants.ConditionConstants\">");
		str = str.concat("\n\t\t<AttributeValue DataType=\""+this.theCondition.toString()+"\">");
		str = str.concat(this.value);
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		str = str.concat(this.printOptional()); 
		str = str.concat("\n</Condition>");
		return str;
	}
	private String printOptional(){
		return "\n<optional>"+this.optional+"</optional>";
	}
	public String toString(){
		return this.toXMLString();
	}
	

	
	public static void main(String[] args) throws IOException {
		Condition retentioncon = new Condition(ConditionConstants.DATA_RETENTION_IN_HOURS, "12");
		System.out.println(retentioncon.toXMLString());
		Condition sharecon = new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "no");
		System.out.println(sharecon.toXMLString());


	}

}
