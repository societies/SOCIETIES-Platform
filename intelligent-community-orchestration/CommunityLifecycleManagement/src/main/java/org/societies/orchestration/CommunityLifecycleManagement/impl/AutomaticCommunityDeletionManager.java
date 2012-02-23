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

package org.societies.orchestration.CommunityLifecycleManagement.impl;

import org.societies.api.internal.css.directory.ICssDirectory;

import org.societies.api.internal.css.discovery.ICssDiscovery;

import org.societies.api.internal.cis.management.CisActivityFeed;
import org.societies.api.internal.cis.management.ServiceSharingRecord;
import org.societies.api.internal.cis.management.CisActivity;
import org.societies.api.internal.cis.management.CisRecord;
import org.societies.api.internal.cis.management.ICisManager;

//import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

//import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

//import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

//import org.societies.api.mock.Identity;
import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;



//import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedback;


import org.societies.api.internal.useragent.model.ExpProposalContent;

/**
 * This is the class for the Automatic Community Deletion Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the deletion of CISs. This 
 * is achieved by perform various forms of analysis on CSSs, CISs, their attributes, and their
 * connections, and using different algorithms. Social network analysis methods and similarity of users
 * -based approaches and algorithms will be used, including an
 * approach that views groups/CISs as either ongoing (non-terminating, with no deadline or 
 * fulfillable purpose for existing) or temporary (not going to last, e.g. because it exists just
 * for a goal that will be completed, or has a clear lifespan, or group breakdown is inevitable). 
 * 
 */

public class AutomaticCommunityDeletionManager //implements ICommCallback
{

	private Identity linkedCss; // No datatype yet defined for CSS
	
    private CisRecord linkedCis;
    
    //private Domain linkedDomain;  // No datatype yet representing a domain
	private Identity linkedDomain;
	
	private int longestTimeWithoutActivity; //measured in minutes
	
	private ICtxBroker userContextBroker;
	//private IUserCtxDBMgr userContextDatabaseManager;
	//private IUserCtxBroker userContextBroker;
	//private ICommunityCtxBroker communityContextBroker;
	//private IUserCtxBrokerCallback userContextBrokerCallback;
	private ICisManager cisManager;
	private IUserFeedback userFeedback;
	//private IUserFeedbackCallback userFeedbackCallback;
	private String userResponse;
	
	private ArrayList<CisRecord> recentRefusals;
	
	/*
     * Constructor for AutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityDeletionManager(Identity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		else
			this.linkedDomain = linkedEntity;
	}
	
	/*
     * Constructor for AutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component on a CIS, either at a domain/cloud level or for an administrating CSS.
	 * Parameters: 
	 * 				linkedCis - the CIS on behalf of which this object is to operate, i.e.
	 *                          continually checking for whether to delete it/suggest deleting it.
	 */
	
	public AutomaticCommunityDeletionManager(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/*
	 * Description: The method looks for CISs to delete, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public void identifyCissToDelete() {
		CisRecord[] records;
		if (linkedCss != null) {
			//records = cisManager.getCisList(new CisRecord(null, linkedCss.toString(), null, null, null, null, null, null)/** CISs administrated by the CSS */);
		}
		if (linkedCis != null) {
			//CisRecord james = new CisRecord();
			records = new CisRecord[1];
			records[0] = linkedCis;
			//CisRecord[] records = ICisManager.getCisList(/** This CIS */new CisRecord());
		}
		if (linkedDomain != null) {
			//records = cisManager.getCisList(new CisRecord(null, linkedDomain.toString(), null, null, null, null, null, null));
			//CisRecord[] records = ICisManager.getCisList(/** CISs in the domain */);
		}
		
		//process
		
		CisRecord record;
		ArrayList<CisRecord> cissToDelete = new ArrayList<CisRecord>();
		
		// VERY SIMPLISTIC v0.1 ALGORITHM
		//if (theCisRecord.getActivityFeed().getHistory().latestDate() <= Date.timestamp() - 5) {
		//    if (theCisRecord.getActivityFeed().getHistory().latestDate() <= Date.timestamp() - (longestTimeWithoutActivity/1440)) {
		//        if (theCisRecord.getActivityFeed().search("CommunityLifecycleManagement metadata: temporary short-term)) {
	    //            cissToDelete.add(theCisRecord);
		//        }
		//        If the CIS has never gone such a long period without activity before, 
		//        suggest deletion via User Agent to CIS owner/administrators, i.e. whoever
		//        this deployment runs on behalf of.
		//  
		//        Date date= new java.util.Date();
		//        System.out.println(new Timestamp(date.getTime()));
        //        ^Above 2 lines would produce something of this format: 
		//           2010-03-08 14:59:30.252
		//
		//        Future directions here can include - being able to identify CISs to delete very soon,
		//        or at more flexible time than just the 5 days as above,
		//        after lack of activity or other key event e.g. purpose fulfillment or location change.
		
		//    }
		//}
		//invoke UserAgent suggestion GUI for deletions
				//OR
				//automatically call CIS management functions to delete CISs

		//    
		
		
		
		List<String> options = new ArrayList<String>();
		options.add("options");
		userResponse = null;
		boolean responded = false;
		//userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects these CISs may be obsolete. If you would like to delete one or more of these CISs, please check them.", options), userFeedbackCallback);
		for (int i = 0; i < 300; i++) {
		    if (userResponse == null)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
			    responded = true;
		}
		
		if (responded == false) {
		    //User obviously isn't paying attention to CSS, so put the message in the background/list of messages for them to see at their leisure.
		    String background = "This message is in your inbox or something, waiting for you to read it";
		}
		else {
		   	Iterator<CisRecord> iterator = cissToDelete.iterator();
			while (iterator.hasNext()) {
			    CisRecord potentiallyDeletableCis = iterator.next();
		        if (userResponse.equals("Yes")) {
				    
			       // cisManager.deleteCis(linkedCss, potentiallyDeletableCis.getCisId());
		        }
		        else {
		    	    recentRefusals.add(potentiallyDeletableCis);
		    	    //store as context the CIS is marked as one level up on the Ongoing/Temporary chain
		    	    //(short-term temporary becomes medium-term, becomes long-term, becomes ongoing.
		    	    //Purely a mechanic for use by this service.
		        }
		   }
		}
	}
	
    public void initialiseAutomaticCommunityDeletionManager() {
    	//getCommManager().register(this);

    	new AutomaticCommunityDeletionManager(linkedCss, "CSS");
    }

    public Identity getLinkedCss() {
    	return linkedCss;
    }
    
    public void setLinkedCss(Identity linkedCss) {
    	this.linkedCss = linkedCss;
    }
    
    public CisRecord getLinkedCis() {
    	return linkedCis;
    }
    
    public void setLinkedCis(CisRecord linkedCis) {
    	this.linkedCis = linkedCis;
    }
    
    public Identity getLinkedDomain() {
    	return linkedDomain;
    }
    
    public void setLinkedDomain(Identity linkedDomain) {
    	this.linkedDomain = linkedDomain;
    }
    
    /**
    public void setUserContextDatabaseManager(IUserCtxDBMgr userContextDatabaseManager) {
    	System.out.println("GOT database" + userContextDatabaseManager);
    	this.userContextDatabaseManager = userContextDatabaseManager;
    }*/
    
    public void setUserContextBroker(ICtxBroker userContextBroker) {
    	System.out.println("GOT user context broker" + userContextBroker);
    	this.userContextBroker = userContextBroker;
    }
    
    /**public void setUserContextBrokerCallback(ICtxBrokerCallback userContextBrokerCallback) {
    	System.out.println("GOT user context broker callback" + userContextBrokerCallback);
    	this.userContextBrokerCallback = userContextBrokerCallback;
    }
    
    public void setCommunityContextBroker(ICommunityCtxBroker communityContextBroker) {
    	System.out.println("GOT community context broker" + communityContextBroker);
    	this.communityContextBroker = communityContextBroker;
    }*/
    
    public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
    
    public void setUserFeedback(IUserFeedback userFeedback) {
    	this.userFeedback = userFeedback;
    }
    
    //public void setUserFeedbackCallback(IUserFeedbackCallback userFeedbackCallback) {
    //	this.userFeedbackCallback = userFeedbackCallback;
    //}
    
    public void getUserResponse(String userResponse) {
    	this.userResponse = userResponse;
    }
    
  //public CommManagerBundle getCommManager() {
    //	return commManager;
    //}
    
    //public void setCommManager(CommManagerBundle commManager) {
    //	this.commManager = commManager;
    //}
    
    /**Returns the list of package names of the message beans you'll be passing*/
    public List<String> getJavaPackages() {
		return null;
    	
    }
    
    /**Returns the list of namespaces for the message beans you'll be passing*/
    public List<String> getXMLNamespaces() {
    	return null;
    }
    
    /** Put your functionality here if there is NO return object, ie, VOID */
    //public void receiveMessage(Stanza stanza, Object messageBean) {
    //	return null;
    //}
    
    /** Put your functionality here if there IS a return object */
    //public Object getQuery(Stanza stanza, Object messageBean) {
    //	return null;
    //}
    
    /** Put your functionality here if there IS a return object and you are updating also */
    //public Object setQuery(Stanza arg0, Object arg1) {
    //	return null;
    //}
    
}