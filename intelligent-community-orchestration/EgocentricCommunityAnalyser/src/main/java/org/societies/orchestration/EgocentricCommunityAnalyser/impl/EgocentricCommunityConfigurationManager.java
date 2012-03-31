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

package org.societies.orchestration.EgocentricCommunityAnalyser.impl;

import org.societies.api.internal.css.directory.ICssDirectory;

import static org.mockito.Mockito.*;

import org.societies.api.internal.css.discovery.ICssDiscovery;

//import org.societies.api.internal.cis.management.ICisActivityFeed;
//import org.societies.api.internal.cis.management.ICisManager;
//import org.societies.api.internal.cis.management.ServiceSharingRecord;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.api.internal.cis.management.ICisActivity;
//import org.societies.api.internal.cis.management.ICisRecord;

import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisSubscribed;
import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisActivity;
import org.societies.api.cis.management.ICisActivityFeed;
//import org.societies.api.cis.management.ICis;

import org.societies.api.internal.css.management.CSSRecord;
import org.societies.api.internal.css.management.ICssActivity;
import org.societies.api.internal.css.management.ICssActivityFeed;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;

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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.identity.IIdentity;
import org.societies.orchestration.api.ISuggestedCommunityAnalyser;
//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;
//import org.societies.comm.xmpp.interfaces.FeatureServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This is the class for the Egocentric Community Configuration Manager component
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the configuration of CISs.
 * Configuration here refers to the nature, or structure, of a CIS itself being changed, and not
 * a sub-CIS or parent CIS being changed in any way, although a CIS 'splitting' into two or more
 * CISs (where the original CIS is deleted in the process) is covered as configuration. It includes:
 * 
 *   - CIS members being removed
 *   - CIS attributes being changed, including its name, definition, membership criteria, goal/purpose (if any), etc.
 *   - CIS splitting or merging into/with other CISs.
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

public class EgocentricCommunityConfigurationManager //implements ICommCallback
{
	
	private IIdentity linkedCss;
    
	//private IIdentity linkedDomain;
	
	private ICtxBroker userContextBroker;
	//private IUserCtxDBMgr userContextDatabaseManager;
	//private IUserCtxBroker userContextBroker;
	//private ICommunityCtxBroker communityContextBroker;
	//private IUserCtxBrokerCallback userContextBrokerCallback;

	private ArrayList<ICisRecord> recentRefusals;

	private IUserFeedback userFeedback;

	private IUserFeedbackCallback userFeedbackCallback;
    
	private ISuggestedCommunityAnalyser suggestedCommunityAnalyser;
	private ICisManager cisManager;
	private ICSSLocalManager cssManager;
	private ICssActivityFeed activityFeed;
	
	private ICommManager commManager;
	
	
	/*
     * Constructor for EgocentricCommunityConfigurationManager
     * 
	 * Description: The constructor creates the EgocentricCommunityConfigurationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public EgocentricCommunityConfigurationManager(IIdentity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		//else
			//this.linkedDomain = linkedEntity;
	}
	
	/*
	 * Description: The method looks for CISs to configure, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public ArrayList<String> identifyCissToConfigure(HashMap <IIdentity, String> userCissMetadata) {
		ArrayList<ICisRecord> cisRecords = new ArrayList();
		ArrayList<ICisRecord> cissToConfigure = new ArrayList<ICisRecord>();
		HashMap<ICisRecord, ICisRecord> configurationsToCiss = new HashMap<ICisRecord, ICisRecord>();
		
		cssManager = mock(ICSSLocalManager.class);
		activityFeed = mock(ICssActivityFeed.class);
		
		if (linkedCss != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs administrated by the CSS */);
		}
		
		//if (linkedDomain != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs in the domain */);
		//}
		
		//for (int i = 0; i < records.size(); i++)
		    //cisRecords.add(records[i]);
		
		//process
		
		//v1.0 algorithm
		    //For each CIS member, If CIS member has been inactive for 1 month, suggest them to leave
		    //If suggestion refused, suggest again in 2 * time before last suggestion to do so (or time inactive if this is first time)
		    //If no response to suggestion after 1 month, issue warning. If no response to warning after another month, remove CSS from CIS
		// ^ Above would be tailored to the nature of the CIS, e.g. one where members join and leave rapidly might lower these
		//timeframes to 1 hour, etc.
		
		//Removing members
		//for (int i = 0; i < cisRecords.size(); i++) {
		//    CisRecord cisUnderAnalysis = cisRecords.get(i);
		//    ArrayList<IIdentity> cisMembers = cisUnderAnalysis.getMembers();
		//    for (int i = 0; i < cisMembers.size(); i++) {
		//        if (cisUnderAnalysis.getActivityFeed().getLastActivityForMember(cisMembers.get(i)).getTimestamp() < Time.current() - 240000000)
		//            //make the suggestion to User Agent based on calcluation - see later
		
		//    }
	    //}
		
		//If two unrelated CISs are similar, suggest merge
		//for (int i = 0; i < cisRecords.size(); i++) {
				//    CisRecord cisUnderAnalysis = cisRecords.get(i);
				//    ArrayList<IIdentity> cisMembers = cisUnderAnalysis.getMembers();
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
				//    ArrayList<IIdentity> cisMembers = cisUnderAnalysis.getMembers();
				//    for (int i = 0; i < cisMembers.size(); i++) {
				//        if (cisUnderAnalysis.getActivityFeed().getLastActivityForMember(cisMembers.get(i)).getTimestamp() < Time.current() - 240000000)
				//            //make the suggestion to User Agent based on calcluation - see later
				
				//    }
			    //}
		
		//Change owner or administrator
		boolean stopLoop = false;
		//for (int i = 0; (i < cisRecords.size()) && (stopLoop == false); i++) {
		//    ICisRecord cisUnderAnalysis = cisRecords.get(i);
		//    ArrayList<IIdentity> cisMembers = cisUnderAnalysis.getMembers();
		//    for (int i = 0; i < cisMembers.size(); i++) {
		//        CisActivityFeed theFeed = cisUnderAnalysis.getActivityFeed();
		//        if ((theFeed.getAcitivites(cisMembers.get(i).size()) >= ((theFeed.getActivities().size()/cisUnderAnalysis.getMembers().size())))
		//            && the median isn't above 1.5 * average) {
		//            suggest admin if not already
		//        }
		//        if ((cisUnderAnalysis.getMembershipCriteria().contains("service X") 
		//            && cisMembers.get(i).ownedServices().contains("service X"))) {
		//            suggest owner if not already
		//        }
        //        else {
		//            ArrayList<IIdentity> rivals = new ArrayList<IIdentity>;
		//            boolean someoneBetter = false;
		//            for (int m = 0; m < cisMembers.sie(); m++) {
		//                if (m == i) continue;
		//                else if (theFeed.getAcitivites(cisMembers.get(i).size()) >= theFeed.getActivities(cisMembers.get(m).size()) ) {
		//                    if (theFeed.getAcitivites(cisMembers.get(i).size()) == theFeed.getActivities(cisMembers.get(m).size()))
		//                        rivals.add(cisMembers.get(m));
		//                }
		//                else someoneBetter = true;
		//            }
		//            if (someoneBetter == false) {
		//                if (rivals.size() > 0) {
		//                    for (int n = 0; n < rivals.size(); n++)
		//                        if ((cisUnderAnalysis.getMembershipCriteria().contains("service X") 
		//                            && cisMembers.get(n).ownedServices().contains("service X"))) {
		//                            suggest rival as owner and stop top-level loop
		//                            cissToConfigure.add(cisUnderAnalysis);
		//                            configurationsToCiss.add(cisUnderAnalysis, new ICisRecord(copied except this is the owner));
		//                            stopLoop = true;
		//                        }
		//                else
		//                    suggest owner if not already
		//                }
		//            }
		//        }
		//        if high betweenness centrality OR prestige OR closeness from CIS activity feed (need to know syntaxes of feeds)
		//        suggest owner for whoever ranks highest on average across all these, and admin for others who rank high for the individual ones / slightly lower across all three
		//    }
	    //}
		
		
		
		
		HashMap<ICisRecord, ICisRecord> finalConfiguredCiss = new HashMap<ICisRecord, ICisRecord>();
		
		//can't use GUI in tests
		//finalConfiguredCiss = getUserFeedbackOnConfiguration(cissToConfigure);
		
		finalConfiguredCiss = configurationsToCiss;
		
		Iterator<ICisRecord> iterator = cissToConfigure.iterator();
		
		while (iterator.hasNext()) {
		    ICisRecord configurableCis = iterator.next();
            ICisRecord cisConfiguration = finalConfiguredCiss.get(configurableCis);
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
		return suggestedCommunityAnalyser.processEgocentricConfigurationRecommendations(null, null);
		
	}
	
	public ArrayList<ICisRecord> getUserFeedbackOnConfiguration(ArrayList<ICisRecord> cissToConfigure) {
		ArrayList<ICisRecord> finalisedCiss = null;
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
		   	Iterator<ICisRecord> iterator = cissToConfigure.iterator();
			while (iterator.hasNext()) {
			    ICisRecord potentiallyCreatableCis = iterator.next();
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
	
    public void initialiseEgocentricCommunityConfigurationManager() {
    	//getCommManager().register(this);
    	
    	new EgocentricCommunityConfigurationManager(linkedCss, "CSS");
    }
    
    public IIdentity getLinkedCss() {
    	return linkedCss;
    }
    
    public void setLinkedCss(IIdentity linkedCss) {
    	this.linkedCss = linkedCss;
    }
    
    /**public IIdentity getLinkedDomain() {
    	return linkedDomain;
    }
    
    public void setLinkedDomain(IIdentity linkedDomain) {
    	this.linkedDomain = linkedDomain;
    }*/
    
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
    
    public ISuggestedCommunityAnalyser getSuggestedCommunityAnalyser() {
    	return suggestedCommunityAnalyser;
    }
    
    public void setSuggestedCommunityAnalyser(ISuggestedCommunityAnalyser suggestedCommunityAnalyser) {
    	this.suggestedCommunityAnalyser = suggestedCommunityAnalyser;
    }
    
    public ICisManager getCisManager() {
    	return cisManager;
    }
    
    public void setCisManager(ICisManager cisManager) {
    	this.cisManager = cisManager;
    }
    
    public ICommManager getCommManager() {
    	return commManager;
    }
    
    public void setCommManager(ICommManager commManager) {
    	this.commManager = commManager;
    }
    
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