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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static org.mockito.Mockito.*;

import org.societies.api.internal.css.directory.ICssDirectory;

import org.societies.api.internal.css.discovery.ICssDiscovery;

//import org.societies.api.internal.cis.management.ICisActivityFeed;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.api.internal.cis.management.ICisActivity;
//import org.societies.api.internal.cis.management.ICisRecord;
//import org.societies.api.internal.cis.management.ICisManager;

import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisSubscribed;
import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisActivity;
import org.societies.api.cis.management.ICisActivityFeed;
//import org.societies.api.cis.management.ICis;

//import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

//import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

//import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

//import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.model.ExpProposalContent;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentityManager;
import org.societies.orchestration.api.SuggestedCommunityAnalyserBean;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.mgmt.IPersonalisationCallback;
//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This is the class for the Suggested Community Analyser component.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class SuggestedCommunityAnalyser //implements ICommCallback
{
	
	private IIdentity linkedCss;
	
	private ICtxBroker userContextBroker;
	//private IUserCtxDBMgr userContextDatabaseManager;
	//private IUserCtxBroker userContextBroker;
	//private ICommunityCtxBroker communityContextBroker;
	//private IUserCtxBrokerCallback userContextBrokerCallback;
	private ArrayList<ICisRecord> recentRefusals;
	private IUserFeedback userFeedback;
	private IUserFeedbackCallback userFeedbackCallback;
	
	private ICisManager cisManager;
    
	private ArrayList<CtxEntity> availableContextData;
	
	private ICssDirectory userCssDirectory;
	
	private CommunityRecommender communityRecommender;
	
	private ICommManager commManager;
	private IIdentityManager identityManager;
	
	private IPrivacyDataManager privacyDataManager;
	private IPersonalisationManager personalisationManager;
	private IPersonalisationCallback personalisationCallback;
    
	/*
     * Constructor for SuggestedCommunityAnalyser
     * 
	 * Description: The constructor creates the SuggestedCommunityAnalyser
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public SuggestedCommunityAnalyser(IIdentity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		//else
		//	this.linkedDomain = linkedEntity;
		
	}
	
    public void initialiseSuggestedCommunityAnalyser() {
    	//getCommManager().register(this);
    	identityManager = commManager.getIdManager();
    	new SuggestedCommunityAnalyser(linkedCss, "CSS");
    }
    
    public ArrayList<String> processEgocentricRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations, ArrayList<String> cissToCreateMetadata) {
    	//go straight to Community Recommender
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
    	ArrayList<ICisRecord> creations = cisRecommendations.get("Create CISs");
    	if (creations != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractCreations = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractCreations.add(creations);
    	    convertedRecommendations.put("Create CISs", abstractCreations);
    	}
    	
    	ArrayList<ICisRecord> deletions = cisRecommendations.get("Delete CISs");
    	if (deletions != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractDeletions = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractDeletions.add(deletions);
    	    convertedRecommendations.put("Delete CISs", abstractDeletions);
    	}
    	
    	return communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(convertedRecommendations, cissToCreateMetadata);
    	
    }
    
    public ArrayList<String> processEgocentricConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations, ArrayList<String> cissToCreateMetadata) {
    	//go straight to Community Recommender
    	
    	return communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(cisRecommendations, cissToCreateMetadata);
    	
    }
    
    public void processCSCWRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations) {
    	
    	
    	
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
    	ArrayList<ICisRecord> creations = cisRecommendations.get("Create CISs");
    	if (creations != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractCreations = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractCreations.add(creations);
    	    convertedRecommendations.put("Create CISs", abstractCreations);
    	}
    	
    	ArrayList<ICisRecord> deletions = cisRecommendations.get("Delete CISs");
    	if (deletions != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractDeletions = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractDeletions.add(deletions);
    	    convertedRecommendations.put("Delete CISs", abstractDeletions);
    	}
    	
    	communityRecommender.identifyCisActionForCSCW(convertedRecommendations);
    }
    
    public void processCSCWConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations) {
    	//go straight to Community Recommender
    	
    	communityRecommender.identifyCisActionForCSCW(cisRecommendations);
    	
    }
    
    public void processCSMAnalyserRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations) {
    	HashMap<String, ArrayList<ArrayList<ICisRecord>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
    	ArrayList<ICisRecord> creations = cisRecommendations.get("Create CISs");
    	if (creations != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractCreations = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractCreations.add(creations);
    	    convertedRecommendations.put("Create CISs", abstractCreations);
    	}
    	
    	ArrayList<ICisRecord> deletions = cisRecommendations.get("Delete CISs");
    	if (deletions != null) {
    	    ArrayList<ArrayList<ICisRecord>> abstractDeletions = new ArrayList<ArrayList<ICisRecord>>();
    	    abstractDeletions.add(deletions);
    	    convertedRecommendations.put("Delete CISs", abstractDeletions);
    	}
    	
    	for (int i = 0; i < convertedRecommendations.size(); i++) {
    		//userContextBroker.evaluateSimilarity("location", convertedRecommendations.get(i).get(0).getMembersList());
    		//if > 90% of members share that context, and there's no other context with more coverage of members,
    		//let this be the highest level CIS criteria.
    		//
    		//else if (more members share some other context)
    		//    convertedRecommendations.replace(i, same but with criteria changed to other context, or original as sub-CIS too);
    		
    		//else
    		//    convertedRecommendations.remove(i);
    	}
    	
    	communityRecommender.identifyCisActionForCSMAnalyser(convertedRecommendations);
    }
    
    public void processCSMAnalyserConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations) {
    	//go straight to Community Recommender
    	
    	communityRecommender.identifyCisActionForCSMAnalyser(cisRecommendations);
    	
    }
    
    public ArrayList<String> checkForPrivacyConflicts() {
    	ArrayList<String> conflictingPrivacyPolicies = new ArrayList<String>();
    	//privacyDataManager.checkPermission(arg0, arg1, arg2, arg3);
    	return conflictingPrivacyPolicies;
    	
    }
    
    public ArrayList<String> checkForPreferenceConflicts() {
    	ArrayList<String> conflictingPreferences = new ArrayList<String>();
    	
    	//personalisationManager.getPreference(arg0, arg1, arg2, arg3, "refuse CIS action with given criteria", arg5)
		return conflictingPreferences;
    	
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
    
    /**public IUserCtxDBMgr getUserContextDatabaseManager() {
    	return userContextDatabaseManager;
    }
    
    public void setUserContextDatabaseManager(IUserCtxDBMgr userContextDatabaseManager) {
    	System.out.println("GOT database" + userContextDatabaseManager);
    	this.userContextDatabaseManager = userContextDatabaseManager;
    }
    */
    public void setUserContextBroker(ICtxBroker userContextBroker) {
    	System.out.println("GOT user context broker" + userContextBroker);
    	this.userContextBroker = userContextBroker;
    }
    
    /**public void setUserContextBrokerCallback(ICtxBrokerCallback userContextBrokerCallback) {
    	System.out.println("GOT user context broker callback" + userContextBrokerCallback);
    	this.userContextBrokerCallback = userContextBrokerCallback;
    }*/
    
    public void retrieveUserContextBrokerCallback(CtxEntity theContext) {
    	availableContextData.add(theContext);
    }
    
    public ICisManager getCisManager() {
    	return cisManager;
    }
    
    public void setCisManager(ICisManager cisManager) {
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
    
    public CommunityRecommender getCommunityRecommender() {
    	return communityRecommender;
    }
    
    public void setCommunityRecommender(CommunityRecommender communityRecommender) {
    	this.communityRecommender = communityRecommender;
    }
    
    public ICommManager getCommManager() {
    	return commManager;
    }
    
    public void setCommManager(ICommManager commManager) {
    	this.commManager = commManager;
    }
    
    public IPrivacyDataManager getPrivacyDataManager() {
    	return privacyDataManager;
    }
    
    public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
    	this.privacyDataManager = privacyDataManager;
    }
    
    public IPersonalisationManager getPersonalisationManager() {
    	return personalisationManager;
    }
    
    public void setPersonalisationManager(IPersonalisationManager personalisationManager) {
    	this.personalisationManager = personalisationManager;
    }
    
    public IPersonalisationCallback getPersonalisationCallback() {
    	return personalisationCallback;
    }
    
    public void setPersonalisationCallback(IPersonalisationCallback personalisationCallback) {
    	this.personalisationCallback = personalisationCallback;
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
    public Object getQuery(Stanza stanza, Object messageBean) {
    	SuggestedCommunityAnalyserBean scaBean = (SuggestedCommunityAnalyserBean) messageBean;
    	try {
    	IIdentity returnIdentity = identityManager.fromJid("XCManager.societies.local");
    	} catch (InvalidFormatException e) {}
    	switch(scaBean.getMethod()){
		case processEgocentricRecommendations:
			try {
				//IIdentity owner = identityManager.fromJid(scaBean.getIdentity());
				//String serviceType = scaBean.getServiceType();
				break;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
    	
    	return null;
    }
    
    /** Put your functionality here if there IS a return object and you are updating also */
    //public Object setQuery(Stanza arg0, Object arg1) {
    //	return null;
    //}
    
}