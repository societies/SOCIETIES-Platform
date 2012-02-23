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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

/**
 * @author Elizabeth
 *
 */
public class MonitoringTable {

	//holds the CtxIdentifiers and all the affected services underneath them
	private Hashtable<CtxIdentifier,MonitoredInfo> mainDataTable;
	private ArrayList<IServiceResourceIdentifier> services;
	private Hashtable<PreferenceDetails, IOutcome> lastOutcomes;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * fullPreferencenametoServiceID:
	 *    String: fullpreferencename as: <serviceType>:<serviceID>.toString():<preferenceName
	 *    IServiceResourceIdentifier: the id of the service 
	 */
	private Hashtable<PreferenceDetails, IServiceResourceIdentifier> fullPreferenceNametoServiceID;
	
	public MonitoringTable(){
		 this.mainDataTable = new Hashtable<CtxIdentifier,MonitoredInfo>();
		 this.services = new ArrayList<IServiceResourceIdentifier>();
		 this.lastOutcomes = new Hashtable<PreferenceDetails, IOutcome>();
		 this.fullPreferenceNametoServiceID = new Hashtable<PreferenceDetails, IServiceResourceIdentifier>();
	}
	
	public boolean isServiceRunning(String serviceType, IServiceResourceIdentifier serviceID){
		Tools t = new Tools();
		return this.services.contains(t.convertToKey(serviceType, serviceID.toString()));
	}
	
	public List<PreferenceDetails> getAffectedPreferences(CtxIdentifier id){
		if (this.mainDataTable.containsKey(id)){
			
			MonitoredInfo mInfo = this.mainDataTable.get(id);
			List<PreferenceDetails> fullPreferenceNames = mInfo.getList();
			this.logging.debug("found "+fullPreferenceNames.size()+" affected services: ");
			for (int i = 0; i <fullPreferenceNames.size(); i++){
				this.logging.debug("Affected: "+fullPreferenceNames.get(i).getServiceID().toString());
			}
			return fullPreferenceNames;
		}else{
			//JOptionPaneshowMessageDialog(null, "CtxID: "+id.toString()+" not found in tables");
			this.logging.debug("No preferences are affected by this context event");
			return new ArrayList<PreferenceDetails>();
		}
	}
	
	public boolean isSubscribed(CtxIdentifier id){
		return this.mainDataTable.containsKey(id);
	}
	
	public void addInfo(CtxIdentifier id, IServiceResourceIdentifier serviceID, String serviceType, String prefName){
		if (this.mainDataTable.containsKey(id)){
			//JOptionPaneshowMessageDialog(null, "Adding new CtxID to tables of PCM "+id.toString());
			MonitoredInfo mInfo = this.mainDataTable.get(id);
			if (null==mInfo.lookup(serviceType, serviceID, prefName)){
				
				mInfo.addInfo(serviceType, serviceID, prefName);
				
				PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, prefName);
				//JOptionPaneshowMessageDialog(null, "Adding info to tables\n"+details.toString());
				this.fullPreferenceNametoServiceID.put(details, serviceID);
				this.logging.debug("INFO added");
			}else{
				this.logging.debug("INFO already exists");
			}
			
		}else{
			
			MonitoredInfo mInfo = new MonitoredInfo();
			mInfo.addInfo(serviceType, serviceID, prefName);
			this.mainDataTable.put(id, mInfo);
			PreferenceDetails details = new PreferenceDetails(serviceType, serviceID, prefName);
			//JOptionPaneshowMessageDialog(null, "CtxID exists in tables. Adding info for: "+details.toString());
			this.fullPreferenceNametoServiceID.put(details, serviceID);
			this.logging.debug("INFO added");
		}
	}
	

	
	private void deleteInfo(String serviceType, IServiceResourceIdentifier serviceID){
		Enumeration<CtxIdentifier> ids = this.mainDataTable.keys();
		while (ids.hasMoreElements()){
			MonitoredInfo mInfo = this.mainDataTable.get(ids.nextElement());
			mInfo.deleteInfo(serviceType, serviceID);
		}		
	}
	
	public void updateLastAction(String serviceType, IServiceResourceIdentifier serviceID, String prefName, IOutcome o){
		
		PreferenceDetails key = new PreferenceDetails(serviceType,serviceID,prefName);
		if (this.lastOutcomes.containsKey(key)){
			this.lastOutcomes.remove(key);
		}
		this.lastOutcomes.put(key, o);
	}
	
	public IOutcome getLastAction(PreferenceDetails d){
		this.logging.debug("Checking to see if the same action was sent to Proactivity last time");
		if (this.lastOutcomes.containsKey(d)){
			return this.lastOutcomes.get(d);
		}
		return null;
	}
	
	public IOutcome getLastAction(String serviceType, IServiceResourceIdentifier serviceID, String preferenceName){
		PreferenceDetails d = new PreferenceDetails(serviceType, serviceID, preferenceName);
		return getLastAction(d);
	}
	
	public void removeServiceInfo(String serviceType, IServiceResourceIdentifier serviceID){
		Enumeration<CtxIdentifier> ids = this.mainDataTable.keys();
		
		while(ids.hasMoreElements()){
			MonitoredInfo info = this.mainDataTable.get(ids.nextElement());
			info.deleteInfo(serviceType, serviceID);
		}
		
		if (this.services.contains(serviceID)){
			this.services.remove(serviceID);
		}
		
		Enumeration<PreferenceDetails> fullPreferenceNames = this.fullPreferenceNametoServiceID.keys();
		
		while (fullPreferenceNames.hasMoreElements()){
			PreferenceDetails temp = fullPreferenceNames.nextElement();
			IServiceResourceIdentifier Id = this.fullPreferenceNametoServiceID.get(temp);
			if (serviceID.equals(Id)){
				if (this.lastOutcomes.containsKey(temp)){
					this.lastOutcomes.remove(temp);
				}
			}
		}
		
	}
	
	
	public void printTable(){
		System.out.println(this.toString());
	}
	
	public String toString(){
		String s = ("--------- PCM Monitoring table ----- \n");
		Enumeration<CtxIdentifier> ctxIDs = this.mainDataTable.keys();
		while (ctxIDs.hasMoreElements()){
			MonitoredInfo info = this.mainDataTable.get(ctxIDs.nextElement());
			s.concat((info.toString()+"\n"));
		}
		
		s.concat("--$$$$$-- PCM Monitoring table --$$$$$--- \n");
		
		return s;
	}
}

