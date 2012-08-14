/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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
package org.societies.orchestration.CSM.main.java.GroupIdentfier;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.societies.orchestration.CSM.main.java.Models.ModelManager;
import org.societies.orchestration.CSM.main.java.csm.CommunitySuggestion;

public class GroupManager {  // implements CSS Listener

	private Map<String, String> attMap; 			// inner index
	private ArrayList<Integer> ownAttValue;			// actual
	private Map<String, ArrayList<Integer>> ownAttMap;
	private ArrayList<Integer> ownerRelationship;   // mapping 
	private ModelManager modelMang;
	//private Models models;
	
	
	
    public GroupManager(ModelManager modelMang)
    {
    	this.modelMang = modelMang;
    	ownAttMap = new HashMap<String, ArrayList<Integer>>();
      	attMap = new HashMap<String, String>(); 
    	ownerRelationship  = new ArrayList<Integer>();
    }	
	
    private void createNewGrouping(){
    	//add owner & current attmap to ownattMap
    	
    }
    
    private void updateGrouping(Integer id, String att, String val)
    {
    	// 
    	updateAttribute(att);
    	updateOwnerValue(id, att, val);
    	newModelCheck(id, att, val);
    	existModelCheck(id, att, val);
    }
    
    private void existModelCheck(Integer id, String att, String value){
    	ArrayList<String> models = modelMang.getSubscribed(id);
    	for (String model : models){
    		if (modelMang.getModel(model) != null){
    			// check if new update is outside the valid range
    			//if (){
    				CommunitySuggestion cs = new CommunitySuggestion();
    				cs.setSuggestionType("LEAVE");
    				ArrayList<String> memList = new ArrayList<String>();
    				memList.add(id.toString());
    				cs.setMembersList(memList);
    				cs.setName("TO DO");  //  TODO
    				sendMsg(cs);
    			//}
    		}
    	}
    }
    
    /**************************************************************
	*
	*
	*
	***************************************************************/
    
    public HashMap<String, Integer> getAllOwnerAtt(String att){
    	HashMap<String, Integer> i = new HashMap<String, Integer>();
    	@SuppressWarnings("rawtypes")
		Iterator it = ownAttMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<Integer>> entry = (Map.Entry<String, ArrayList<Integer>>)it.next();
            i.put(entry.getKey(), entry.getValue().get(attMap.get(att)));
            it.remove(); 
        }
    	return i;
    }
    
    private void updateAttribute(String att){
    	//check attribute is in map index 
    	if (!attMap.containsKey(att)){
    		attMap.put(att, attMap.size());
    		//
    		for (ArrayList<Integer> ownAttValue : ownAttMap.values()) {
    			ownAttValue.set(attMap.size(), 0);
    		}
    	}
    }
    
    private void updateOwnerValue(Integer id, String att, String val)
    {
    	ownAttMap.get(id).set(attMap.get(att), val);
    }
    
    private void sendMsg(CommunitySuggestion cs){
    	//
    	//send required message
    }
    
    private void deleteGrouping(Integer id){
    	// assume just user
    	ArrayList<String> models = modelMang.getSubscribed(id);
    	// send leave messages
    	// remove from various internal lists
    }
    
    private void newModelCheck(Integer id, String att, String value){
    	HashMap<String, String> models = modelMang.getUnSubscribed(id);
    	
    	// for the unsubscribed models
    	// check if new update is within the valid range
    	// if so 
    	// check if owners other attributes for this model are in required range
    	// if so send them as a JOIN message
    	
    	//
    	
    }

    
    
}
