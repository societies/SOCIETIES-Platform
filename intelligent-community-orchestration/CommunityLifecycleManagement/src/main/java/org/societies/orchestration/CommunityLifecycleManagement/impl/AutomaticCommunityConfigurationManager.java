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
import org.societies.api.internal.cis.management.ICisManager;
import org.societies.api.internal.cis.management.ServiceSharingRecord;
import org.societies.api.internal.cis.management.CisActivity;
import org.societies.api.internal.cis.management.CisRecord;

import java.util.concurrent.Future;

//import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

//import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

//import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

//import org.societies.api.internal.context.broker.IUserCtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

//import org.societies.api.mock.Identity;
import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;
//import org.societies.comm.xmpp.interfaces.FeatureServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the class for the Automatic Community Configuration Manager component
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the configuration of CISs.
 * Configuration here refers to the nature, or structure, of a CIS itself being changed, and not
 * a sub-CIS or parent CIS being changed in any way, although a CIS 'splitting' into two or more
 * CISs (where the original CIS is deleted in the process) is covered as configuration. It includes:
 * 
 *   - CIS members being added/removed
 *   - CIS attributes being changed, including its name, definition, membership criteria, goal/purpose (if any), etc.
 *   - CIS splitting or merging into/with other CISs.
 *   - And more
 *  
 * This is achieved by perform various forms of analysis on CSSs, CISs, their attributes, and their
 * connections, and using different algorithms. Social network analysis methods and similarity of users
 * -based approaches and algorithms will be used, including an
 * approach that views groups/CISs as either ongoing (non-terminating, with no deadline or 
 * fulfillable purpose for existing) or temporary (not going to last, e.g. because it exists just
 * for a goal that will be completed, or has a clear lifespan, or group breakdown is inevitable). 
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class AutomaticCommunityConfigurationManager //implements ICommCallback
{
	
	private Identity linkedCss;
	
    private CisRecord linkedCis;
    
	private Identity linkedDomain;
	
	private ICtxBroker userContextBroker;
	//private IUserCtxDBMgr userContextDatabaseManager;
	//private IUserCtxBroker userContextBroker;
	//private ICommunityCtxBroker communityContextBroker;
	//private IUserCtxBrokerCallback userContextBrokerCallback;

	private ArrayList<CisRecord> recentRefusals;

	private IUserFeedback userFeedback;

	private IUserFeedbackCallback userFeedbackCallback;
    
	/*
     * Constructor for AutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityConfigurationManager(Identity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		else
			this.linkedDomain = linkedEntity;
	}
	
	/*
     * Constructor for IAutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CIS.
	 * Parameters: 
	 * 				linkedCis - the Cis that this object will be used to check for configuration on.
	 */
	
	public AutomaticCommunityConfigurationManager(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/*
	 * Description: The method looks for CISs to configure, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public void identifyCissToConfigure() {
		ArrayList<CisRecord> cisRecords = new ArrayList();
		ArrayList<CisRecord> cissToConfigure = new ArrayList();
		
		if (linkedCss != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs administrated by the CSS */);
		}
		if (linkedCis != null) {
			//CisRecord[] records = ICisManager.getCisList(/** This CIS */);
		}
		if (linkedDomain != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs in the domain */);
		}
		
		//for (int i = 0; i < records.size(); i++)
		    //cisRecords.add(records[i]);
		
		//process
		
		//SIMPLISTIC v0.1 algorithm
		    //For each CIS member, If CIS member has been inactive for 1 month, suggest them to leave
		    //If suggestion refused, suggest again in 2 * time before last suggestion to do so (or time inactive if this is first time)
		    //If no response to suggestion after 1 month, issue warning. If no response to warning after another month, remove CSS from CIS
		// ^ Above would be tailored to the nature of the CIS, e.g. one where members join and leave rapidly might lower these
		//timeframes to 1 hour, etc.
		
		//for (int i = 0; i < cisRecords.size(); i++) {
		//    CisRecord cisUnderAnalysis = cisRecords.get(i);
		//    ArrayList<Identity> cisMembers = cisUnderAnalysis.getMembers();
		//    for (int i = 0; i < cisMembers.size(); i++) {
		//        if (cisUnderAnalysis.getActivityFeed().getLastActivityForMember(cisMembers.get(i)).getTimestamp() < Time.current() - 240000000)
		//            //make the suggestion to User Agent based on calcluation - see later
		
		//    }
	    //}
		
		//If two unrelated CISs are similar, suggest merge
		//for (int i = 0; i < cisRecords.size(); i++) {
				//    CisRecord cisUnderAnalysis = cisRecords.get(i);
				//    ArrayList<Identity> cisMembers = cisUnderAnalysis.getMembers();
		        //    for (int m = i + 1; m < cisRecords.size(); m++) {
				//        for (int n = 0; n < cisMembers.size(); n++) {
				//            if (cisMembers.contains(cisRecords.get(i).getMembers())) {}
				//                //make the suggestion to User Agent based on calcluation - see later
				
				//        }
		        //    }
			    //}
		//If one CIS seems split into two or more, suggest split
		//for (int i = 0; i < cisRecords.size(); i++) {
				//    CisRecord cisUnderAnalysis = cisRecords.get(i);
				//    ArrayList<Identity> cisMembers = cisUnderAnalysis.getMembers();
				//    for (int i = 0; i < cisMembers.size(); i++) {
				//        if (cisUnderAnalysis.getActivityFeed().getLastActivityForMember(cisMembers.get(i)).getTimestamp() < Time.current() - 240000000)
				//            //make the suggestion to User Agent based on calcluation - see later
				
				//    }
			    //}
		
		//invoke UserAgent suggestion GUI for configurations
		//OR
		//automatically call CIS management functions to configure CISs
		
		String[] options = new String[1];
		options[0] = "options";
		String userResponse = null;
		boolean responded = false;
		userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects the follwing CISs should be configured in certain ways. If you approve of any of the suggested reconfigurations, please check them.", options), userFeedbackCallback);
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
			    CisRecord potentiallyConfigurableCis = iterator.next();
		        if (userResponse.equals("Yes")) {
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
		        	//
			       // cisManager.configureCis(linkedCss, potentiallyConfigurableCis.getCisId());
		        }
		        else {
		    	    recentRefusals.add(potentiallyConfigurableCis);
		        }
		   }
		}
		
	}
	
    public void initialiseAutomaticCommunityConfigurationManager() {
    	//getCommManager().register(this);
    	
    	new AutomaticCommunityConfigurationManager(linkedCss, "CSS");
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
    	System.out.println("GOT CISRECORD" + linkedCis.toString());
    	this.linkedCis = linkedCis;
    }
    
    public Identity getLinkedDomain() {
    	return linkedDomain;
    }
    
    public void setLinkedDomain(Identity linkedDomain) {
    	this.linkedDomain = linkedDomain;
    }
    
    /**public void setUserContextDatabaseManager(IUserCtxDBMgr userContextDatabaseManager) {
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
    }*/
    
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
    
    /**For calling methods that have return types */
    //public void sendIQGet(Stanza stanza, Object messageBean, ICommCallback callback) 
    //           throws CommunicationException;
     
    /**For calling methods that have void types */
    //void sendMessage(Stanza stanza, String type, Object messageBean)
    //            throws CommunicationException;

    
}