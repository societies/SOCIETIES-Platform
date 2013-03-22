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



import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.TargetMatchConstants;


/**
 * The Action class represents an operation that can be performed on a Resource. 
 * The Action can be "READ", "WRITE", "CREATE", "DELETE" as listed in the ActionConstants enumeration. 
 * @author Elizabeth
 *
 */
@Deprecated
public class Action implements Serializable{

	protected ActionConstants action;
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
	
	public Action(Action action){
		this.action = action.getActionType();
		this.optional = action.isOptional();
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
	public ActionConstants getActionConstants(){
		return this.action;
	}
	public TargetMatchConstants getType(){
		return TargetMatchConstants.ACTION;
	}
	
	public String toXMLString(){
		String str = "\n<Action>";
		str = str.concat("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" " +
				"\n\t\t\tDataType=\""+org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.class.getName()+"\">");

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + (optional ? 1231 : 1237);
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
		Action rhs = (Action) obj;
		return new EqualsBuilder()
			.append(this.getActionType().name(), rhs.getActionType().name())
			.append(this.isOptional(), rhs.isOptional())
			.isEquals();
	}

}
