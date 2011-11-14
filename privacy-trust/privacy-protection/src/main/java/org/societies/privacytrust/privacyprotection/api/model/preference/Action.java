package org.societies.privacytrust.privacyprotection.api.model.preference;


import java.io.IOException;
import java.io.Serializable;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.ActionConstants;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.TargetMatchConstants;


/**
 * The Action class represents an operation that can be performed on a Resource. 
 * The Action can be "READ", "WRITE", "CREATE", "DELETE" as listed in the ActionConstants enumeration. 
 * @author Elizabeth
 *
 */
public class Action implements Serializable{

	private org.societies.privacytrust.privacyprotection.api.model.preference.constants.ActionConstants action;
	private boolean optional;
	
	private Action(){
		
	}
	public Action(ActionConstants action){
		this.action = action;
		this.optional = false;
	}
	
	public Action(ActionConstants action, boolean isOptional){
		this.action = action;
		this.optional = isOptional;
	}
	
	public void setOptional(boolean isOptional){
		this.optional = isOptional;
	}
	public boolean isOptional(){
		return this.optional;
	}
	public ActionConstants getActionType(){
		return this.action;
	}
	public TargetMatchConstants getType(){
		return TargetMatchConstants.ACTION;
	}
	
	public String toXMLString(){
		String str = "\n<Action>";
		str = str.concat("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" " +
				"\n\t\t\tDataType=\"org.personalsmartspace.spm.preference.api.platform.constants.ActionConstants\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.action.toString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		str = str.concat(this.printOptional());
		str = str.concat("\n</Action>");
		return str;
	}
	private String printOptional(){
		return "\n<optional>"+this.optional+"</optional>";
	}
	public String toString(){
		return this.toXMLString();
	}
	public static void main(String[] args) throws IOException{
		Action action = new Action(ActionConstants.READ);
		System.out.println(action.toXMLString());
	}
}
