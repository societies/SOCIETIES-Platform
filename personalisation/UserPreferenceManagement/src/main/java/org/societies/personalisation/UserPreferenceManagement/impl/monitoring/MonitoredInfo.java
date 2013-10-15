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
package org.societies.personalisation.UserPreferenceManagement.impl.monitoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * @author Elizabeth
 *
 */
public class MonitoredInfo {
	
	private ArrayList<PreferenceDetails> list;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public MonitoredInfo(){
		this.list = new ArrayList<PreferenceDetails>();
		
	}
	
	/*public void addInfo(String servInfo, String prefName){
		PreferenceInfo pInfo = new PreferenceInfo(servInfo, prefName);
		if (null==lookup(servInfo, prefName)){
			this.list.add(pInfo);
		}
	}*/
	
	public void addInfo(String serviceType, ServiceResourceIdentifier serviceID, String prefName){
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, prefName);
		if (null==lookup(serviceType, serviceID, prefName)){
			this.list.add(details);
		}		
	}
	
	public PreferenceDetails lookup(String serviceType, ServiceResourceIdentifier serviceID, String prefName){
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, prefName);
		Iterator<PreferenceDetails> i = this.list.iterator();
		while (i.hasNext()){
			PreferenceDetails prefDetail = i.next();
			if (prefDetail.equals(details)){
				return prefDetail;
			}
			
		}
		return null;
	}
	
/*	public ArrayList<String> getList(){
		if(this.logging.isDebugEnabled()){this.logging.debug("getting list of services");}
		ArrayList<String> fullNames = new ArrayList<String>();
		Iterator<PreferenceInfo> i = this.list.iterator();
		while (i.hasNext()){
			PreferenceInfo pInfo = i.next();
			String name = pInfo.getFullPreferenceName();
			fullNames.add(name);
		}
		return fullNames;
	}
	
*/
	public List<PreferenceDetails> getList(){
		return this.list;
	}

	
	public void deleteInfo (String serviceType, ServiceResourceIdentifier serviceID){
		Iterator<PreferenceDetails> i = this.list.iterator();
		
		while (i.hasNext()){
			PreferenceDetails pd = i.next();
			if (ServiceModelUtils.compare(serviceID, pd.getServiceID())){
				this.list.remove(pd);
			}
		}		
	}
	
	

	public String toString(){
		String s = "";
		
		for (PreferenceDetails d : this.list){
			s = s.concat(d.toString());
			s = s.concat("\n");
		}
		
		return s;
	}
	
}

