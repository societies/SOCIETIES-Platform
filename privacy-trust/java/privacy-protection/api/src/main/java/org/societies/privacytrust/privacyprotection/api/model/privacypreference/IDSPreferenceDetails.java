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

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;



public class IDSPreferenceDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IIdentity affectedDPI;
	private Requestor requestor;
	
	public IDSPreferenceDetails(IIdentity affectedDPI){
		this.affectedDPI = affectedDPI;
	}
	
	public void setAffectedDPI(IIdentity affectedDPI) {
		this.affectedDPI = affectedDPI;
	}

	public IIdentity getAffectedDPI() {
		return affectedDPI;
	}

	
	private boolean compareRequestorIdentities(IIdentity id){
		if (id==null){
			if (this.getRequestor().getRequestorId()==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (this.getRequestor().getRequestorId()==null){
				return false;
			}else{
				if (id.toString().equals(getRequestor().getRequestorId().toString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	private boolean compareServiceID(ServiceResourceIdentifier serviceID2){
		
		if (this.getRequestor() instanceof RequestorService){
			if (serviceID2==null){
				if (((RequestorService) this.getRequestor()).getRequestorServiceId() == null){
					return true;
				}else{
					return false;
				}
			}else{
				if (((RequestorService) this.getRequestor()).getRequestorServiceId()==null){
					return false;
				}else{
					if (serviceID2.toString().equalsIgnoreCase(((RequestorService) this.getRequestor()).getRequestorServiceId().toString())){
						return true;
					}else{
						return false;
					}
				}
			}
		}else{
			return false;
		}
		
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof IDSPreferenceDetails){
			IDSPreferenceDetails details = (IDSPreferenceDetails) obj;
			if (getAffectedDPI().toString().equalsIgnoreCase(details.getAffectedDPI().toString())){
				return this.compareRequestors(details.getRequestor());
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
	private boolean compareRequestors(Requestor requestor) {
		if (this.compareRequestorIdentities(requestor.getRequestorId())){
			if (this.requestor instanceof RequestorService){
				if (requestor instanceof RequestorService){
					return this.compareServiceID(((RequestorService) requestor).getRequestorServiceId());
				}
			}else if (this.requestor instanceof RequestorCis){
				if (requestor instanceof RequestorCis){
					return this.compareCisID(((RequestorCis) requestor).getCisRequestorId());
				}
			}
		}
		
		return false;
	}
	private boolean compareCisID(IIdentity cisRequestorId) {
		if (this.requestor instanceof RequestorCis){
			return (((RequestorCis) this.requestor).getCisRequestorId().toString().equals(cisRequestorId.toString()));
		}
		return false;
	}
	@Override
	public String toString(){
		String str = "\n";
		str = str.concat("AffectedDPI: "+this.getAffectedDPI().toString());
		
		if (this.getRequestor()!=null){
			str = str.concat("\nRequestor DPI: "+this.getRequestor().toString());
		}
		str = str.concat("\n");
		return str;
	}

	/**
	 * @return the requestor
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * @param requestor the requestor to set
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}


	
}
