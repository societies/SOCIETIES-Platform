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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;

import org.societies.orchestration.CSM.main.java.Models.ModelManager;
import org.societies.orchestration.CSM.main.java.csm.CSM;
import org.societies.orchestration.CSM.main.java.csm.CommunitySuggestion;

import org.societies.api.osgi.event.*;
import org.societies.orchestration.api.CssDCEvent;
//import org.societies.api.osgi.event.CSSEventConstants;
//import org.societies.api.osgi.event.EventListener;
//import org.societies.api.osgi.event.EventTypes;
//import org.societies.api.osgi.event.IEventMgr;
//import org.societies.api.osgi.event.InternalEvent;

public class GroupManager  extends EventListener implements Subscriber {

	private Logger LOG = LoggerFactory.getLogger(CSM.class);

	private HashMap<String, ArrayList<String>> ownerModels;
	
	private HashMap<String, String> attMap; 			// inner index
	private HashMap<String, ArrayList<Integer>> ownAttMap;
	private ArrayList<Integer> ownerRelationship;   // mapping

	private ModelManager modelMang;
	private IEventMgr eventMgr;
	private publishSuggestion ps;
	
	public GroupManager(ModelManager modelMang)
    {
    	LOG.info("GroupManager : set up ");
    	
    	ownerModels = new HashMap<String, ArrayList<String>>();    
    	this.modelMang = modelMang;
    	ownAttMap = new HashMap<String, ArrayList<Integer>>();
      	attMap = new HashMap<String, String>(); 
      	this.registerForEvents();

    }	
	
	public void addUser(IIdentity user){
    	LOG.info("GroupManager : New User ");
    	ArrayList<String> models = new ArrayList<String>();
    	ownerModels.put(user.getJid(), models);
    }
    
    public void AttributeUpdate(IIdentity user,String att){
    	LOG.info("GroupManager : Attribute Update");
    	ArrayList<String> models = ownerModels.get(user.getJid());
    	
    }
    
    private void groupUpdateUpdate(){
    	LOG.info("GroupManager : Group Update");
    	
    	// join
    	
    	// leave

    }
    
    private void updateGrouping(IIdentity id, String att, String val)
    {
    	LOG.info("GroupManager : Update Grouping ");
    	// 
    	updateAttribute(att);
    	updateOwnerValue(id, att, val);
    	newModelCheck(id, att, val);
    	existModelCheck(id, att, val);
    }
    
    private void existModelCheck(IIdentity id, String att, String value){
    	LOG.info("GroupManager : exist Model Check ");
    	//
    	ArrayList<String> models = modelMang.getSubscribed(id);
    	for (String model : models){
    		if (modelMang.getModel(model) != null){
    			// check if new update is outside the valid range
    				CommunitySuggestion cs = new CommunitySuggestion();
    				cs.setSuggestionType("LEAVE");
    				ArrayList<String> memList = new ArrayList<String>();
    				memList.add(id.toString());
    				cs.setMembersList(memList);
    				cs.setName("TO DO");  //  TODO
    				sendMsg(cs);
    		}
    	}
    }
    
	private void registerForEvents() {
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "=newaction)" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/orchestration/CSSDC)" +
				")";
		this.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.CSSDC_EVENT}, eventFilter);
		LOG.info("GroupManager : registered for events ");

	}
	/**************************************************************
	*
	***************************************************************/
    
    public HashMap<String, Integer> getAllOwnerAtt(String att){
    	LOG.info("GroupManager : get All Owner Att ");
    	//
    	HashMap<String, Integer> i = new HashMap<String, Integer>();
    	@SuppressWarnings("rawtypes")
		Iterator it = ownAttMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<Integer>> entry = (Map.Entry<String, ArrayList<Integer>>)it.next();
            //
        	// ** TODO
        	//
            //i.put(entry.getKey(), entry.getValue().get(attMap.get(att)));
            it.remove(); 
        }
    	return i;
    }
    
    private void updateAttribute(String att){
    	LOG.info("GroupManager : update Attribute ");
    	//
    	//check attribute is in map index 
    	if (!attMap.containsKey(att)){
    		//
        	// ** TODO
        	//
    		//attMap.put(att, attMap.size());
    		//
    		for (ArrayList<Integer> ownAttValue : ownAttMap.values()) {
    			ownAttValue.set(attMap.size(), 0);
    		}
    	}
    }
    
    private void updateOwnerValue(IIdentity id, String att, String val)
    {
    	LOG.info("GroupManager : update Owner Value ");
    	//
    	// ** TODO
    	//
    	
    	//ownAttMap.get(id).set(attMap.get(att), val);
    }
    
    private void sendMsg(CommunitySuggestion cs){
    	LOG.info("GroupManager : send Msg ");
	    	ps.sendSuggestion(cs);
    }
    
    private void deleteGrouping(IIdentity id){
    	LOG.info("GroupManager : delete Grouping ");
    	// assume just user
    	ArrayList<String> models = modelMang.getSubscribed(id);
    	// send leave messages
    	// remove from various internal lists
    }
    
    private void newModelCheck(IIdentity id, String att, String value){
    	LOG.info("GroupManager : new Model Check ");
    	HashMap<String, String> models = modelMang.getUnSubscribed(id);
    	
    	// for the unsubscribed models
    	// check if new update is within the valid range
    	// if so 
    	// check if owners other attributes for this model are in required range
    	// if so send them as a JOIN message
    	
    	//
    }

	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.identity.IIdentity, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void pubsubEvent(IIdentity arg0, String arg1, String arg2,
			Object arg3) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		LOG.info("recieved an internal event");
		CssDCEvent cde = (CssDCEvent) arg0.geteventInfo();
		//String evtType = cde.getEvtT;
		IIdentity user = cde.getUserId(); 
		//if (evtType.equals("Create")){
			
		//}
		//else if ( (evtType.equals("Mod")) || (evtType.equals("Update") )){
			
		//}
		//else if (evtType.equals("Removal")){
			
		//}
		
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		LOG.info("recieved an external event");
		
	}
    
}
