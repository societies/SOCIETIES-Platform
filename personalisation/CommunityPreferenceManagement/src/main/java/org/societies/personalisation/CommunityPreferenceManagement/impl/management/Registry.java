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
package org.societies.personalisation.CommunityPreferenceManagement.impl.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class Registry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1523266247660963721L;
	private Hashtable<PreferenceDetails, CtxIdentifier> mappings; 
	private int index ;
	private final IIdentity CISId;
	
	public Registry(IIdentity CISId){
		this.CISId = CISId;
		this.mappings = new Hashtable<PreferenceDetails,CtxIdentifier>();
		this.index = 0;
	}
	
	public String getNameForNewPreference(){
		this.index += 1;
		return "community_preference_"+this.index;
	}
	
	public void addPreference(PreferenceDetails detail, CtxIdentifier id){
		this.mappings.put(detail, id);
		
	}
	
	public void addPreference(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName, CtxIdentifier id){
		PreferenceDetails detail = new PreferenceDetails(serviceType, serviceID, preferenceName);
		this.mappings.put(detail, id);
	}
	
	
	public void deletePreference(PreferenceDetails detail){
		this.mappings.remove(detail);
		
	}
	
	public void deletePreference(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		PreferenceDetails detail = new PreferenceDetails(serviceType, serviceID, preferenceName);
		this.mappings.remove(detail);
	}
	
	
	public CtxIdentifier getCtxID (PreferenceDetails details){
		Enumeration<PreferenceDetails> e = this.mappings.keys();
		
		while(e.hasMoreElements()){
			PreferenceDetails key = e.nextElement();
			if (key.equals(details)){
				return this.mappings.get(key);
			}
		}
		/*if (this.mappings.containsKey(details)){
			return this.mappings.get(details);
		}*/
		return null;
	}
	
	public CtxIdentifier getCtxID (String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, preferenceName);
		if (this.mappings.containsKey(details)){
			return this.mappings.get(details);
		}
		return null;
	}
	
	
	public List<String> getPreferenceNamesofService(String serviceType, ServiceResourceIdentifier serviceID){
		ArrayList<String> prefNames = new ArrayList<String>();
		Enumeration<PreferenceDetails> e = this.mappings.keys();
		while (e.hasMoreElements()){
			PreferenceDetails d = e.nextElement();
			if (ServiceModelUtils.compare(serviceID, d.getServiceID())){
				prefNames.add(d.getPreferenceName());
			}
		}
		return prefNames;
	}
	
	public ArrayList<PreferenceDetails> getPreferenceDetailsOfAllPreferences(){
		ArrayList<PreferenceDetails> list = new ArrayList<PreferenceDetails>();
		Enumeration<PreferenceDetails> keys = this.mappings.keys();
		 
		while(keys.hasMoreElements()){
			list.add(keys.nextElement());
		}
		
		return list;
		
	}

	public IIdentity getCISId() {
		return CISId;
	}

}
