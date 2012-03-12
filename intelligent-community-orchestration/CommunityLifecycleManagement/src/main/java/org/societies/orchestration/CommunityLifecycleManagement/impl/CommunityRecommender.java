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

package org.societies.orchestration.CommunityLifecycleManagement.impl;

import static org.mockito.Mockito.*;

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

import org.societies.api.identity.IIdentity;
//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



//import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;


import org.societies.api.internal.useragent.model.ExpProposalContent;

/**
 * This is the class for the Community Recommender component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityRecommender //implements ICommCallback
{

	private IIdentity linkedCss;
		
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

	private IUserFeedbackCallback userFeedbackCallback;
	
	private ArrayList<CisRecord> cissToCreate;
	private ArrayList<CisRecord> cissToConfigure;
	private ArrayList<CisRecord> cissToDelete;
	
	/*
     * Constructor for Community Recommender
     * 
	 * Description: The constructor creates the Community Recommender
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public CommunityRecommender(IIdentity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		//else
		//	this.linkedDomain = linkedEntity;
	}
	
	public void identifyCisActionForEgocentricCommunityAnalyser(HashMap<String, ArrayList<ArrayList<CisRecord>>> cisPossibilities) {
		if (cisPossibilities.get("Create CISs") != null)
		    identifyCissToCreate(cisPossibilities.get("Create CISs").get(0));
		if (cisPossibilities.get("Delete CISs") != null)
		    identifyCissToDelete(cisPossibilities.get("Delete CISs").get(0));
		if (cisPossibilities.get("Configure CISs") != null)
		    identifyCissToConfigure(cisPossibilities.get("Configure CISs"));
	}
	
	/*
	 * Description: The method looks for CISs to create, using as a base the information related to
	 *              this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only try to create sub-CISs on it. If the linked component
	 *              is a CSS, it will check all information relevant to that CSS to create
	 *              CISs that would be relevant to them. If the linked component is 
	 *              a domain (or something else like a local area?), the checks are not 'selfish'
	 *              but try to objectively identify appropriate CISs for groups of people, based
	 *              on collective aspects like context attributes.
	 */
	
	public void identifyCissToCreate(ArrayList<CisRecord> creatableCiss) {		
		//Can't use GUI in tests
		//cissToCreate = getUserFeedbackOnCreation(cissToCreate);
		
		cissToCreate = creatableCiss;
		
		if (cissToCreate != null) 
		    for (int i = 0; i < cissToCreate.size(); i++)
			    cisManager.createCis(linkedCss.getIdentifier(), cissToCreate.get(i).getCisId());
	}
	
	public ArrayList<CisRecord> getUserFeedbackOnCreation(ArrayList<CisRecord> cissToCreate) {
		ArrayList<CisRecord> finalisedCiss = null;
		String[] options = new String[1];
		options[0] = "options";
		String userResponse = null;
		boolean responded = false;
		userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects the follwing CISs may benefit you. If you would like to create one or more of these CISs, please check them.", options), userFeedbackCallback);
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
		   	Iterator<CisRecord> iterator = cissToCreate.iterator();
			while (iterator.hasNext()) {
			    CisRecord potentiallyCreatableCis = iterator.next();
		        if (userResponse.equals("Yes")) {
				    finalisedCiss.add(potentiallyCreatableCis);
			       // cisManager.createCis(linkedCss, potentiallyCreatableCis.getCisId());
		        }
		        else {
		    	    recentRefusals.add(potentiallyCreatableCis);
		        }
		   }
		}
		return finalisedCiss;
	}
	
	/*
	 * Description: The method looks for CISs to delete, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public void identifyCissToDelete(ArrayList<CisRecord> cisPossibilities) {	
		//Can't use GUI in tests
        //cissToDelete = getUserFeedbackOnDeletion(cissToDelete);
		
		cissToDelete = cisPossibilities;
		
		for (int i = 0; i < cissToDelete.size(); i++)
			cisManager.deleteCis(linkedCss.getIdentifier(), cissToDelete.get(i).getCisId());
	}
	
	public ArrayList<CisRecord> getUserFeedbackOnDeletion(ArrayList<CisRecord> cissToDelete) {
		ArrayList<CisRecord> realCissToDelete = new ArrayList<CisRecord>();
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
				    realCissToDelete.add(potentiallyDeletableCis);
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
		return realCissToDelete;
	}
	
	public void identifyCissToConfigure(ArrayList<ArrayList<CisRecord>> cisPossibilities) {
	
	    ArrayList<CisRecord> finalConfiguredCiss = new ArrayList<CisRecord>();
	
	    //can't use GUI in tests
	    //finalConfiguredCiss = getUserFeedbackOnConfiguration(cissToConfigure);
	
	    finalConfiguredCiss = cissToConfigure;
	
	    Iterator<CisRecord> iterator = finalConfiguredCiss.iterator();
	
	    while (iterator.hasNext()) {
	        CisRecord configurableCis = iterator.next();

		    //if "remove members"
        	//    attempt to remove members - perhaps SOCIETIES platform itself should have mechanism
        	//    where if a user deletion from CIS attempt is made, 
        	//    that user will be informed by the system and given a chance to respond?
        	//    The admin/owner could have an override option in case e.g. offensive person is being deleted.
        	//if "merge with other CIS"
        	//
        	//if "split into distinct CISs"
        	//
        	//if "switch sub-CIS and CIS"
        	//
        	//if "change owner or administrator"
        	//
	        // cisManager.configureCis(linkedCss, potentiallyConfigurableCis.getCisId());
        }
    }

    public ArrayList<CisRecord> getUserFeedbackOnConfiguration(ArrayList<CisRecord> cissToConfigure) {
	    ArrayList<CisRecord> finalisedCiss = null;
	    String[] options = new String[1];
	    options[0] = "options";
	    String userResponse = null;
	    boolean responded = false;
	    userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects the follwing CISs should be configured as described. If you approve these actions for one or more of these CISs, please check them.", options), userFeedbackCallback);
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
	   	    Iterator<CisRecord> iterator = cissToConfigure.iterator();
		    while (iterator.hasNext()) {
		        CisRecord potentiallyCreatableCis = iterator.next();
	            if (userResponse.equals("Yes")) {
			        finalisedCiss.add(potentiallyCreatableCis);
		            // cisManager.createCis(linkedCss, potentiallyCreatableCis.getCisId());
	            }
	            else {
	    	        recentRefusals.add(potentiallyCreatableCis);
	            }
	       }
	    }
	    return finalisedCiss;
    }
	
    public void initialiseCommunityRecommender() {
    	//getCommManager().register(this);

    	new CommunityRecommender(linkedCss, "CSS");
    }

    public IIdentity getLinkedCss() {
    	return linkedCss;
    }
    
    public void setLinkedCss(IIdentity linkedCss) {
    	this.linkedCss = linkedCss;
    }
    
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
    
    public IUserFeedback getUserFeedback() {
    	return userFeedback;
    }
    
    public void setUserFeedback(IUserFeedback userFeedback) {
    	this.userFeedback = userFeedback;
    }
    
    public IUserFeedbackCallback getUserFeedbackCallback() {
    	return userFeedbackCallback;
    }
    
    public void setUserFeedbackCallback(IUserFeedbackCallback userFeedbackCallback) {
    	this.userFeedbackCallback = userFeedbackCallback;
    }
    
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