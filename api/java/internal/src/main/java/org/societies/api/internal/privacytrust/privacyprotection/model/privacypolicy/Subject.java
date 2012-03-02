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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy;


import java.io.Serializable;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.TargetMatchConstants;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;


/**
 * The Subject class embeds the identity of the provider CSS. 
 * @author Elizabeth
 *
 */
public class Subject implements Serializable{

	private IIdentity dpi;
	private IServiceResourceIdentifier serviceID;

	public Subject(){
		
	}
	public Subject(IIdentity dpi){
		this.dpi = dpi;
	}

	public Subject(IIdentity dpi, IServiceResourceIdentifier serviceID){
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
		"\n \t\t\tDataType=\"org.personalsmartspace.sre.api.pss3p.Identity\">");

		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.dpi.toString());
		str = str.concat("</AttributeValue>");

		str = str.concat("\n\t</Attribute>");
		return str;
	}
	
	private String serviceIDToXMLString(){
		String str = "";
		str = str.concat("\n\t<Attribute AttributeId=\"serviceID\"" +
				"\n\t\t\tDataType=\"org.personalsmartspace.sre.api.pss3p.IServiceResourceIdentifier\">");
		str = str.concat("\n\t\t<AttributeValue>");
		str = str.concat(this.serviceID.toString());
		str = str.concat("</AttributeValue>");
		str = str.concat("\n\t</Attribute>");
		return str;
		
	}
	public String toString(){
		return this.toXMLString();
	}
	
	public IIdentity getDPI(){
		return this.dpi;
	}
	
	public IServiceResourceIdentifier getServiceID(){
		return this.serviceID;
	}
	

}
