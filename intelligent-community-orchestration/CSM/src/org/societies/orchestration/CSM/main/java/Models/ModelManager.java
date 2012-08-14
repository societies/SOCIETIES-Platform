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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;

/**
 * Describe your class here...
 *
 * @author John
 *
 */
public class ModelManager {

	//private String modelName;
	private Models models;
	private Map<String, ArrayList<String>> attModel;
	private Map<String, String> attRange;
	private Map<String, Map<String, String>> modelAttRange;
	private Map<Integer, ArrayList<String>> userSubscribed;
	private ICisAdvertisementRecord[] cisAdvertisementRecords;
	private ICisDirectory cisDirectory;
	private ICisAdvertisementRecord cisAdvertisementRecord;

	
	
	public ModelManager()  {
		// 
		models = new Models();
		populateModels();
	}

	private void populateModels(){
		//
		cisAdvertisementRecords = cisDirectory.searchByName("");
		for (ICisAdvertisementRecord cr : cisAdvertisementRecords){
			addModel(cr);
		}
	}
	
	public void addModel(ICisAdvertisementRecord cr){
		//
		// getCISAdvert();
		// getCISContext();
		Model newModel = new Model(cr.getName());
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
		//
		//**************************************
		removeAttModel(cr);
		removeModelRange(cr.getName());
	}
	
	public void updateModel(){
		//
		// TODO figure out later if needed 
	}
	
	public void updateSubscribed(Integer user, String model){
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add(model);
		userSubscribed.put(user, tmpList);

	}
	
	public ArrayList<String> getSubscribed(Integer user){
		if (userSubscribed.containsKey(user)){
			return userSubscribed.get(user);
		}
		return null;
	}

	public HashMap<String, String> getUnSubscribed(Integer user){
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
	
	private void addModelRange(ICisAdvertisementRecord cr){
		//
		// add model range for new model
	}
	
	private void removeAttModel(ICisAdvertisementRecord cr){
		//
		HashMap<String, MembershipCriteria> mc = cr.getMembershipCriteria();
		for (String att : mc.keySet()){
			ArrayList<String> list = new ArrayList<String>();
			list = attModel.get(att);
			if (list.size() > 1){
				list.remove(att);
				attModel.remove(att);
				attModel.put(att, list);
			} else {
				attModel.remove(att);
			}

		}
			//
	}


	
	private void removeModelRange(String name){
		//
		// remove model from model range list
		modelAttRange.remove(name);
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
