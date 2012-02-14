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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;

import java.io.Serializable;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;


public class PPNPreferenceDetails implements Serializable{

	private String contextType;
	private CtxAttributeIdentifier affectedCtxID;
	private Identity requestorDPI;
	private IServiceResourceIdentifier serviceID;
	public PPNPreferenceDetails(String contextType){
		this.setContextType(contextType);
	}

	public void setAffectedCtxID(CtxAttributeIdentifier affectedCtxID) {
		this.affectedCtxID = affectedCtxID;
	}

	public CtxAttributeIdentifier getAffectedCtxID() {
		return affectedCtxID;
	}

	public void setRequestorDPI(Identity requestorDPI) {
		this.requestorDPI = requestorDPI;
	}

	public Identity getRequestorDPI() {
		return requestorDPI;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getContextType() {
		return contextType;
	}
	
	private boolean compareRequestorDPIs(Identity dpi){
		if (dpi==null){
			if (this.requestorDPI==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (this.requestorDPI==null){
				return false;
			}else{
				if (dpi.toString().equals(requestorDPI.toString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	private boolean compareServiceID(IServiceResourceIdentifier serviceID2){
		if (serviceID2==null){
			if (this.serviceID == null){
				return true;
			}else{
				return false;
			}
		}else{
			if (serviceID2==null){
				return false;
			}else{
				if (serviceID2.toString().equalsIgnoreCase(this.serviceID.toString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	private boolean compareCtxIDs(CtxAttributeIdentifier ctxID){
		if (ctxID==null){
			if (this.affectedCtxID==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (this.affectedCtxID==null){
				return false;
			}else{
				if (ctxID.toString().equals(this.affectedCtxID.toString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof PPNPreferenceDetails){
			PPNPreferenceDetails det = (PPNPreferenceDetails) obj;
			if (det.getContextType().equalsIgnoreCase(contextType)){
				if (compareCtxIDs(det.getAffectedCtxID())){
					if (compareRequestorDPIs(det.getRequestorDPI())){
						return this.compareServiceID(det.getServiceID());
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		String str = "\n";
		str = str.concat("Context Type: "+this.contextType);
		if (this.affectedCtxID!=null){
			str = str.concat("\nAffected CtxID: "+this.affectedCtxID.toString());
		}
		
		if (this.requestorDPI!=null){
			str = str.concat("\nRequestor DPI: "+this.requestorDPI.toString());
		}
		str = str.concat("\n");
		return str;
	}

	public void setServiceID(IServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public IServiceResourceIdentifier getServiceID() {
		return serviceID;
	}
	
}
