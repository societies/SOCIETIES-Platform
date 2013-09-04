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

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.TargetMatchConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
/**
 * the Resource class is used to represent  a piece of data type belonging to the user 
 * (i.e context data, preference data, profile data). It contains the id of the data and the type of data. 
 * @author Elizabeth
 *
 */
@Deprecated
public class Resource implements Serializable{

	private DataIdentifier dataId;
	private String dataType;
	private DataIdentifierScheme scheme;

	private Resource(){
		//		if (scheme==null){
		//			JOptionPane.showMessageDialog(null, "constructor: SCHEME IS NULL");
		//			throw new NullPointerException();
		//		}else{
		//			JOptionPane.showMessageDialog(null, "SCHEME IS: "+scheme.toString());
		//		}
	}
	public Resource(DataIdentifier dataId){
		this.dataId = dataId;
		this.dataType = dataId.getType();

		this.scheme = dataId.getScheme();
		//		if (scheme==null){
		//			JOptionPane.showMessageDialog(null, "constructor: SCHEME IS NULL");
		//			throw new NullPointerException();
		//		}else{
		//			JOptionPane.showMessageDialog(null, "SCHEME IS: "+scheme.toString());
		//		}
	}


	public Resource(DataIdentifierScheme scheme, String type){
		this.dataType = type;
		this.scheme = scheme;
		//		if (scheme==null){
		//			JOptionPane.showMessageDialog(null, "constructor: SCHEME IS NULL");
		//			throw new NullPointerException();
		//		}else{
		//			JOptionPane.showMessageDialog(null, "SCHEME IS: "+scheme.toString());
		//		}
	}
	public TargetMatchConstants getType(){
		return TargetMatchConstants.RESOURCE;
	}

	public String getDataType(){
		return this.dataType;
	}


	public DataIdentifier getDataId(){
		return this.dataId;
	}

	public void stripIdentifier(){
		this.dataId = null;
	}

	public void setPublicCtxIdentifier(DataIdentifier ctxId){
		this.dataId = ctxId;
	}

	public String toXMLString(){
		StringBuilder str = new StringBuilder("\n<Resource>");
		if (this.dataId!=null){
			str.append(this.ctxIDToXMLString());
		}
		if (this.dataType!=null){
			str.append(this.ctxTypeToXMLString());
		}
		str.append("\n</Resource>");
		return str.toString();
	}

	private String ctxIDToXMLString(){
		StringBuilder str = new StringBuilder();
		str.append("\n\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:resource-id\"" +
				"\n \t\t\tDataType=\"org.societies.api.context.model.CtxIdentifier\">");

		str.append("\n\t\t<AttributeValue>");
		str.append(dataId.getUri());
		str.append("</AttributeValue>");

		str.append("\n\t</Attribute>");
		return str.toString();
	}

	private String ctxTypeToXMLString(){
		StringBuilder str = new StringBuilder();
		//		if(scheme==null){
		//			JOptionPane.showMessageDialog(null, "SCHEME IS NULL");
		//		}
		if (scheme.equals(DataIdentifierScheme.CONTEXT)){
			str.append("\n\t<Attribute AttributeId=\""+DataIdentifierScheme.CONTEXT+"\"" +
					"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		}
		else if (scheme.equals(DataIdentifierScheme.CIS)){
			str.append("\n\t<Attribute AttributeId=\""+DataIdentifierScheme.CIS+"\"" +
					"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		}

		else if (scheme.equals(DataIdentifierScheme.DEVICE)){
			str.append("\n\t<Attribute AttributeId=\""+DataIdentifierScheme.DEVICE+"\"" +
					"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		}

		else if (scheme.equals(DataIdentifierScheme.ACTIVITY)){
			str.append("\n\t<Attribute AttributeId=\""+DataIdentifierScheme.ACTIVITY+"\"" +
					"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		}
		else {
			str.append("\n\t<Attribute AttributeId=\"unknown\"" +
					"\n\t\t\tDataType=\"http://www.w3.org/2001/XMLSchema#string\">");
		}
		str.append("\n\t\t<AttributeValue>");
		str.append(this.dataType);
		str.append("</AttributeValue>");
		str.append("\n\t</Attribute>");
		return str.toString();	
	}

	public String toString(){
		return this.toXMLString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((dataId == null) ? 0 : dataId.hashCode());
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
		Resource rhs = (Resource) obj;
		return new EqualsBuilder()
		.append(this.getDataType(), rhs.getDataType())
		.append(this.getDataId(), rhs.getDataId())
		.isEquals();
	}

	/**
	 * @return the scheme
	 */
	public DataIdentifierScheme getScheme() {
		return scheme;
	}



}

