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
package org.societies.orchestration.CSM.main.java.Models;


//import ICtxBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.orchestration.CSM.main.java.csm.CSM;

import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.identity.IIdentity;

//***
//	for unit testing only 
//***
//import local.test.dummy.classes.CisDirectory;
//import local.test.dummy.classes.MembershipCriteria;
//import local.test.dummy.classes.Rule;
//import local.test.dummy.interfaces.ICisAdvertisementRecord;
//import local.test.dummy.interfaces.ICisDirectory;
//***
//for unit testing only 
//***

/**
 * Describe your class here...
 *
 * @author John
 *
 */
public class ModelManager {
	
	private Logger LOG = LoggerFactory.getLogger(CSM.class);
	//   Ext
	private ICisDirectory cisDirectory;
	private ICisAdvertisementRecord cisAdvertisementRecord;
	private ICtxBroker ctxBroker;
	private Models models;
	private ICisAdvertisementRecord[] cisAdvertisementRecords;
	//
	private Map<String, ArrayList<String>> attModel;
	private HashMap<String, HashMap> modelAttRange;
	private Map<IIdentity, ArrayList<String>> userSubscribed;
	
	public ModelManager()  {
		// 
		models = new Models();
		attModel = new HashMap<String, ArrayList<String>>();
		modelAttRange = new HashMap<String, HashMap>();
	}

	public void populateModels(){
		//
		LOG.info("ModelManager : populateModels ");
		//***
		//		for unit testing only 
		//***
		//CisDirectory cd = new CisDirectory();
		//cisAdvertisementRecords = cd.searchByName("");
		//***
		//		for unit testing only 
		//***		
		cisAdvertisementRecords = cisDirectory.searchByName("");

		for (ICisAdvertisementRecord cr : cisAdvertisementRecords){
			if (cr != null){
				addModel(cr);
			}
		}
	}
	
	public void addModel(ICisAdvertisementRecord cr){
		LOG.info("ModelManager : adding model " + cr.getName());
		//
		String i = cr.getName();
		System.out.println(i);
		Model newModel = new Model(i);
		newModel.setJid(cr.getId());
		HashMap<String, MembershipCriteria> mc = cr.getMembershipCriteria();
		for (String att : mc.keySet()){
			newModel.setAttValues(att, mc.get(att));
		}
		models.addModel(newModel);
		updateAttModel(cr);
		addModelRange(cr);

		
	}
	//
	public void removeModel(ICisAdvertisementRecord cr){
		LOG.debug("ModelManager : removing model " + cr.getName());
		//
		//**************************************
		removeAttModel(cr);
		removeModelRange(cr.getName());
	}
	
	public void updateModel(){
		//
		// TODO figure out later if needed phase 2
	}
	
	public void updateSubscribed(IIdentity user, String model){
		LOG.debug("ModelManager : update Subscribed ");
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add(model);
		userSubscribed.put(user, tmpList);

	}
	
	public ArrayList<String> getSubscribed(IIdentity user){
		LOG.debug("ModelManager : get Subscribed ");
		if (userSubscribed.containsKey(user)){
			return userSubscribed.get(user);
		}
		return null;
	}

	public HashMap<String, String> getUnSubscribed(IIdentity user){
		LOG.debug("ModelManager : get UN Subscribed ");
		HashMap<String, String> tmpList = models.getModelNames();
		if (userSubscribed.containsKey(user)){
			ArrayList<String> usertmpList = userSubscribed.get(user);
			for (String i : usertmpList){
				tmpList.remove(i);
			}
			//
		}
		return tmpList;
	}
	
	public Model getModel(String model){
		//
		for (Model m : models.getModels()){
			if (m.getName().equals(model)){
				return m;
			}
		}
		return null;
		
	}
	
	private void updateAttModel(ICisAdvertisementRecord cr){
		//
		HashMap<String, MembershipCriteria> mc = cr.getMembershipCriteria();
		for (String att : mc.keySet()){
			if (attModel.containsKey(att)){
				attModel.get(att).set(attModel.get(att).size(), cr.getName());
			}
			else {
				ArrayList<String> list = new ArrayList<String>();
			    list.add(cr.getName());
				attModel.put(att, list);
			}
			//
		}
		//
	}
	
	private void removeAttModel(ICisAdvertisementRecord cr){
		//
		HashMap<String, MembershipCriteria> mc = cr.getMembershipCriteria();
		for (String att : mc.keySet()){
			if (attModel.containsKey(att)){
				ArrayList<String> list = new ArrayList<String>();
				list = attModel.get(att);
				attModel.remove(att);
			}
		}
			//
	}	
	
	private void addModelRange(ICisAdvertisementRecord cr){
		//
		HashMap<String, MembershipCriteria> mc = cr.getMembershipCriteria();
		
		for (String att : mc.keySet()){
			
			MembershipCriteria memC = mc.get(att);
			Rule r = memC.getRule();
			
			HashMap<String, List<String>> ruleValues = new HashMap<String, List<String>>();
			HashMap<String, HashMap> attRules = new HashMap<String, HashMap>();
			// assume new
			if (modelAttRange.containsKey(cr.getName())){
				attRules = modelAttRange.get(cr.getName());
				if (attRules.containsKey(att)){
					ruleValues.put(r.getOperation(), r.getValues());
					attRules.put(att, ruleValues);
					modelAttRange.put(cr.getName(), attRules);
				}
			}
			else {
				ruleValues.put(r.getOperation(), r.getValues());
				attRules.put(att, ruleValues);				
				modelAttRange.put(cr.getName(), attRules);
			}
			//
		}		
	}

	private void removeModelRange(String name){
		//
		// remove model from model range list
		modelAttRange.remove(name);
	}
	/**************************************************************
	*
	*   getter
	*
	***************************************************************/	
	public ICisDirectory getCisDirectory(){
		return cisDirectory;
	}
	
	public ICtxBroker getCtxBroker(){
		return ctxBroker;
	}
	/**************************************************************
	*
	*   setter
	*
	***************************************************************/		
	public void setCisDirectory(ICisDirectory icd){
		this.cisDirectory = icd;
	}	
	
	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}	
	/**************************************************************
	*
	*   TODO legacy tidy up
	*
	***************************************************************/	
	//private static class modelAttributes{
	//	private String attributeType;
	//	private Integer rank;
	//}

	//public ArrayList getAllAttributes(){
	//	ArrayList<String> att = new ArrayList<String>();
		//for (modelAttributes ma: csModel){
		//	att.add(ma.attributeType);
		//}
		
	//	return att;
	//}
	
}
