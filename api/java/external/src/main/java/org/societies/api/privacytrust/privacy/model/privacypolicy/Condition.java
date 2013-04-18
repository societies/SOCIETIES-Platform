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
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;


/**
 * The Condition class represents a condition that has to be met by the provider or the user. 
 * Possible types of Conditions are listed in the ConditionConstants enumeration.
 * @author Elizabeth
 *
 */
@Deprecated
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
	public boolean getOptional(){
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
				"\n\t\t\tDataType=\""+org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants.class.getName()+"\">");
		str = str.concat("\n\t\t<AttributeValue DataType=\""+getConditionName().name()+"\">");
		str = str.concat(getValueAsString());
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

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the theCondition
	 */
	public ConditionConstants getTheCondition() {
		return theCondition;
	}
	/**
	 * @param theCondition the theCondition to set
	 */
	public void setTheCondition(ConditionConstants theCondition) {
		this.theCondition = theCondition;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (optional ? 1231 : 1237);
		result = prime * result
				+ ((theCondition == null) ? 0 : theCondition.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Condition rhs = (Condition) obj;
		return new EqualsBuilder()
			.append(this.getConditionName().name(), rhs.getConditionName().name())
			.append(this.isOptional(), rhs.isOptional())
			.isEquals();
	}


}
