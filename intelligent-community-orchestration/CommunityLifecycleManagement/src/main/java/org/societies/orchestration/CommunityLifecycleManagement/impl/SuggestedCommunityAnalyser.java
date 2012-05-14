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
import java.util.Set;

import static org.mockito.Mockito.*;

import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.api.internal.css.directory.ICssDirectory;

import org.societies.api.internal.css.discovery.ICssDiscovery;

//import org.societies.api.internal.cis.management.ICisActivityFeed;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.api.internal.cis.management.ICisActivity;
//import org.societies.api.internal.cis.management.ICis;
//import org.societies.api.internal.cis.management.ICisManager;

/**import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;

import org.societies.api.cis.management.ICisEditor;*/

import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisParticipant;
//import org.societies.orchestration.api.ICisEditor;
//import org.societies.api.cis.management.ICisSubscribed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;

//import org.societies.api.cis.management.ICis;

//import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

//import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

//import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

//import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.model.ExpProposalContent;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.css.management.ICssActivity;
import org.societies.api.identity.IIdentityManager;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ISuggestedCommunityAnalyser;
import org.societies.orchestration.api.SuggestedCommunityAnalyserBean;
import org.societies.orchestration.api.SuggestedCommunityAnalyserMethodType;
import org.societies.orchestration.api.SuggestedCommunityAnalyserResultBean;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
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

public class SuggestedCommunityAnalyser implements ISuggestedCommunityAnalyser
{
	
	private IIdentity linkedCss;
	
	private ICtxBroker userContextBroker;
	//private IUserCtxDBMgr userContextDatabaseManager;
	//private IUserCtxBroker userContextBroker;
	//private ICommunityCtxBroker communityContextBroker;
	//private IUserCtxBrokerCallback userContextBrokerCallback;
	private ArrayList<ICis> recentRefusals;
	
	private IUserFeedback userFeedback;
	private IUserFeedbackCallback userFeedbackCallback;
	
	private ICisManager cisManager;
    
	private ArrayList<CtxEntity> availableContextData;
	
	private ICssDirectory userCssDirectory;
	
	private CommunityRecommender communityRecommender;
	
	private ICommManager commManager;
    private ICommCallback commCallback;
	private IIdentityManager identityManager;
	
	private IPrivacyDataManager privacyDataManager;
	
	private IPersonalisationManager personalisationManager;
	//Eliza commented: (this doesn't exist anymore:
	//private IPersonalisationCallback personalisationCallback;
	
	private IServiceDiscovery serviceDiscovery;
	private IServiceDiscoveryCallback serviceDiscoveryCallback;
	
	private IDeviceManager deviceManager;
	
	//private ISuggestedCommunityAnalyser suggestedCommunityAnalyser;
	private SuggestedCommunityAnalyserBean suggestedCommunityAnalyserBean;
	private SuggestedCommunityAnalyserResultBean suggestedCommunityAnalyserResultBean;
	private SuggestedCommunityAnalyserMethodType suggestedCommunityAnalyserMethodType;
	
	private ArrayList<ArrayList<String>> refusedOrAmendedSuggestions;
    
	private HashMap<String, String> recordedMetadata;
	
	private ArrayList<String> currentActionsMetadata;
	private ArrayList<Integer> proposedActionsWithMetadata;
	
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
    	recordedMetadata = new HashMap<String, String>();
    	new SuggestedCommunityAnalyser(linkedCss, "CSS");
    }
    
    @Override
    public ArrayList<String> processEgocentricRecommendations(HashMap<String, ArrayList<ICisOwned>> cisRecommendations, ArrayList<String> cissToCreateMetadata) {
    	//go straight to Community Recommender
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>();
    	
    	HashMap<String, ArrayList<ArrayList<ICisOwned>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisOwned>>>();
    	ArrayList<ICisOwned> creations = cisRecommendations.get("Create CISs");
    	if (creations == null)
    		creations = new ArrayList<ICisOwned>();
    	ArrayList<ICisOwned> deletions = cisRecommendations.get("Delete CISs");
    	if (deletions == null)
    		deletions = new ArrayList<ICisOwned>();
    	ArrayList<ArrayList<ICisOwned>> abstractCreations = new ArrayList<ArrayList<ICisOwned>>();
    	ArrayList<ArrayList<ICisOwned>> abstractDeletions = new ArrayList<ArrayList<ICisOwned>>();
    	
    	if (creations != null)
    		if (creations.size() != 0)
    			for (int i = 0; i < creations.size(); i++) {
    				ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
    				it.add(creations.get(i));
    	            abstractCreations.add(it);
    			}
    	
    	
    	if (deletions != null) {
    		if (deletions.size() != 0)
    			for (int i = 0; i < deletions.size(); i++) {
    				ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
    				it.add(deletions.get(i));
    	            abstractDeletions.add(it);
    			}
    	}
    	
    	if (creations != null) {
    	    abstractCreations = new ArrayList<ArrayList<ICisOwned>>();
    	    abstractCreations.add(creations);
    	    convertedRecommendations.put("Create CISs", abstractCreations);
    	}

    	if (deletions != null) {
    	    abstractDeletions = new ArrayList<ArrayList<ICisOwned>>();
    	    abstractDeletions.add(deletions);
    	    convertedRecommendations.put("Delete CISs", abstractDeletions);
    	}
    	
    	for (int i = 0; i < creations.size(); i++) {
    		if (checkForPreferenceConflicts("Create CISs", abstractCreations).size() != 0) {
    			creations.remove(i);
    			cissToCreateMetadata.set(i, "FAILED");
    		}
    	}
    	
    	for (int i = 0; i < deletions.size() && deletions != null; i++) {
    		if (checkForPreferenceConflicts("Delete CISs", abstractDeletions).size() != 0) {
    			deletions.remove(i);
    		    cissToCreateMetadata.set(i, "FAILED");
    	    }
    	}
    	
    	if (convertedRecommendations.size() != 0) {
    		return communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(convertedRecommendations, cissToCreateMetadata);
    	}
    	else return null;
    	
    }
    
    public ArrayList<String> processEgocentricConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisOwned>>> cisRecommendations, ArrayList<String> cissToCreateMetadata) {
    	//go straight to Community Recommender
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>();
    	
    	return communityRecommender.identifyCisActionForEgocentricCommunityAnalyser(cisRecommendations, cissToCreateMetadata);
    	
    }
    
    public void processCSCWRecommendations(HashMap<String, ArrayList<ICisOwned>> cisRecommendations) {
    	
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>();
    	
    	HashMap<String, ArrayList<ArrayList<ICisOwned>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisOwned>>>();
    	ArrayList<ICisOwned> creations = cisRecommendations.get("Create CISs");
    	if (creations == null)
    		creations = new ArrayList<ICisOwned>();
    	ArrayList<ICisOwned> deletions = cisRecommendations.get("Delete CISs");
    	if (deletions == null)
    		deletions = new ArrayList<ICisOwned>();
    	ArrayList<ArrayList<ICisOwned>> abstractCreations = new ArrayList<ArrayList<ICisOwned>>();
    	ArrayList<ArrayList<ICisOwned>> abstractDeletions = new ArrayList<ArrayList<ICisOwned>>();
    	
		
		if (creations != null)
			if (creations.size() != 0)
				for (int i = 0; i < creations.size(); i++) {
					ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
					it.add(creations.get(i));
	            	abstractCreations.add(it);
				}
	
		    
		
    	//for (int i = 0; i < deletions.size() && deletions != null; i++) {
    		//if (checkForPreferenceConflicts("Delete CISs", abstractDeletions).size() != 0)
    			//deletions.remove(i);
    	//}		
		
			ArrayList<String> preferenceConflicts = checkForPreferenceConflicts("Create CISs", abstractCreations);
			if (preferenceConflicts.size() != 0)
				for (int i = 0; i < preferenceConflicts.size(); i++) {
				    creations.remove(Integer.valueOf(preferenceConflicts.get(i).split("---")[0]));
		        }
	
			ArrayList<String> preferenceConflictsDeletion = checkForPreferenceConflicts("Delete CISs", abstractCreations);
			if (preferenceConflictsDeletion.size() != 0)
				for (int i = 0; i < preferenceConflictsDeletion.size(); i++) {
				    deletions.remove(Integer.valueOf(preferenceConflictsDeletion.get(i).split("---")[0]));
		        }
			
			for (int i = 0; i < creations.size(); i++) {
		    	
	    		ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractCreations);
	    		boolean refuseSuggestion = false;
	    		if (privacyConflicts.size() != 0) {
	    			for (int m = 0; m < privacyConflicts.size(); m++) {
	    				if (privacyConflicts.get(m).contains("User"))
	    				    refuseSuggestion = true;
	    				else if (privacyConflicts.get(m).contains("CSS: ")) {
	    					ICisOwned updatedCreation = creations.get(i);
	    					
	    					Future<Set<ICisParticipant>> theParticipants = updatedCreation.getMemberList();
	    					ArrayList<IIdentity> theMembers = new ArrayList<IIdentity>();
	    					if (theParticipants != null) {
	    						Set<ICisParticipant> theParticipantsSet = null;
								try {
									theParticipantsSet = theParticipants.get();
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	    						if (theParticipantsSet != null) {
	    							Iterator<ICisParticipant> it = theParticipantsSet.iterator();
	    							while (it.hasNext()) {
	        							try {
											theMembers.add(identityManager.fromJid(it.next().toString()));
										} catch (InvalidFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	        						}
	    						}
	    					}
	    						
	    					
	    					
	    					theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
	    					try {
								updatedCreation.removeMemberFromCIS(privacyConflicts.get(m).split("CSS: ")[1]);
							} catch (CommunicationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    					creations.set(i, updatedCreation);
	    				}
	    					
	    			}
	    		}
	    		if (refuseSuggestion == true)
	    			creations.remove(i);
	    		
	    		
	    	}
	    	
	    	for (int i = 0; i < deletions.size(); i++) {
	        	
	    		ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractDeletions);
	    		boolean refuseSuggestion = false;
	    		if (privacyConflicts.size() != 0) {
	    			for (int m = 0; m < privacyConflicts.size(); m++) {
	    				if (privacyConflicts.get(m).equals("User policy"))
	    				    refuseSuggestion = true;
	    				else if (privacyConflicts.get(m).contains("CSS: ")) {
	    					
	    					ICisOwned updatedCreation = creations.get(i);
	    					Future<Set<ICisParticipant>> theParticipants = updatedCreation.getMemberList();
	    					ArrayList<IIdentity> theMembers = new ArrayList<IIdentity>();
	    					if (theParticipants != null) {
	    						Set<ICisParticipant> theParticipantsSet = null;
								try {
									theParticipantsSet = theParticipants.get();
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	    						if (theParticipantsSet != null) {
	    							Iterator<ICisParticipant> it = theParticipantsSet.iterator();
	    							while (it.hasNext()) {
	        							try {
											theMembers.add(identityManager.fromJid(it.next().toString()));
										} catch (InvalidFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	        						}
	    						}
	    					}
	    					//ArrayList<IIdentity> theMembers = updatedCreation.getMembersList();
	    					//theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
	    					//updatedCreation.setMembersList();
	    					creations.set(i, updatedCreation);
	    				}
	    			}
	    		}
	    		if (refuseSuggestion == true)
	    			deletions.remove(i);
	    		
	    		
	    	}
	
		for (int i = 0; i < creations.size(); i++) {
			ICisOwned cisProposal = creations.get(i);
			List<ICis> userJoinedCissTemp = cisManager.getCisList();
			ArrayList<ICis> userJoinedCiss = new ArrayList<ICis>();
			for (int m = 0; m < userJoinedCissTemp.size(); m++) {
				userJoinedCiss.add(userJoinedCissTemp.get(m));
			}
			for (int m = 0; m < userJoinedCiss.size(); m++) {
				//if (userJoinedCiss.get(m).getMembershipCriteria == cisProposal.getMembershipCriteria()) {
				    //if (userJoinedCiss.get(m).getMembersList() == cisProposal.getMembersList()) {
				        //creations.remove(i);
				        //Feedback to CSM Analyser suggesting to either remove this model,
				        //add another attribute to it (suggest one?), or change one or more
				        //model attributes (specify them?)
			        //}
				    //else if (userJoinedCiss.get(m).getMembersList().size() >= (1.3 * cisProposal.getMembersList().size()) {
				    //    creations.add(sub-CIS for shared CSSs);
				    //}
				//}
				//if the membership criteria for the existing CIS somehow conflict with that of the suggestion,
				//may be grounds to delete the old CIS. E.g. the suggestion is based on location,
				//which is different to the location of an existing CIS, which is known to be short-term temporary.
			}
			//userContextBroker.evaluateSimilarity("location", convertedRecommendations.get(i).get(0).getMembersList());
			//if user isn't a member of another CIS that matches this, OK
		
			//if > 90% of members share that context, and there's no other context with more coverage of members,
			//let this be the highest level CIS criteria.
			//
			//else if (more members share some other context)
			//    convertedRecommendations.replace(i, same but with criteria changed to other context, or original as sub-CIS too);
		
			//else
			//    convertedRecommendations.remove(i);
		
			//If activity feed shows history in last few minutes, it's temporary and should be made immediately, as sub-CIS if an encapsulating CIS is available; otherwise, 8 months - ongoing, last month - long-term; last few days - medium-term
	    
			//Friends, CSS directory, working colleagues, same address: ongoing
			//Shared interests, personal attributes like languages spoken and age: sub-CIS.
			ArrayList<ICisOwned> ciss = new ArrayList<ICisOwned>();
			ciss.add(cisProposal);
			convertedRecommendations = advancedCisCreationAnalysis(ciss);
			
		}
	
	    
	    
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	for (int i = 0; i < creations.size(); i++) {
    		//userContextBroker.evaluateSimilarity("location", convertedRecommendations.get(i).get(0).getMembersList());
    		//if user isn't a member of another CIS that matches this, OK
    		
    		//if > 90% of members share that context, and there's no other context with more coverage of members,
    		//let this be the highest level CIS criteria.
    		//
    		//else if (more members share some other context)
    		//    convertedRecommendations.replace(i, same but with criteria changed to other context, or original as sub-CIS too);
    		
    		//else
    		//    convertedRecommendations.remove(i);
    		
    		//If activity feed shows history in last few minutes, it's temporary; otherwise, 8 months - ongoing, last month - long-term; last few days - medium-term
    	    
    		//Friends, CSS directory, working colleagues, same address: ongoing
    		//Shared interests, personal attributes like languages spoken and age: sub-CIS.
    	}
    	
    	for (int i = 0; i < deletions.size(); i++) {
    		//If activity feed of CIS is dead for a while, depending on criteria it's based on, OK.
    	}
    	
    	
    	abstractCreations.clear();
    	if (creations.size() != 0) {
    		for (int i = 0; i < creations.size(); i++) {
				ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
				it.add(creations.get(i));
	            abstractCreations.add(it);
			}
    	    convertedRecommendations.put("Create CISs", abstractCreations);
    	}
    	
    	abstractDeletions.clear();
    	if (deletions.size() != 0) {
    		for (int i = 0; i < deletions.size(); i++) {
				ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
				it.add(deletions.get(i));
	            abstractDeletions.add(it);
			}
    	    convertedRecommendations.put("Delete CISs", abstractDeletions);
    	}
    	
    	if (convertedRecommendations.size() != 0) {
	    	currentActionsMetadata = communityRecommender.identifyCisActionForCSCW(convertedRecommendations);
	    	ArrayList<String> cisIds = new ArrayList<String>();
	    	
	        for (int i = 0; i < currentActionsMetadata.size(); i++) {
	        	cisIds.add(currentActionsMetadata.get(i).split("---")[0].split("CIS ID: ")[1]);
	        	if (recordedMetadata.get(cisIds.get(i)) == null)
	        		recordedMetadata.put(cisIds.get(i), currentActionsMetadata.get(i));
	        }
	    }
    	
    	//if (convertedRecommendations.size() != 0) {
    	//	ArrayList<String> actionMetadata = communityRecommender.identifyCisActionForCSMAnalyser(convertedRecommendations);
    	//}
    	
    }
    
    public void processCSCWConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisOwned>>> cisRecommendations) {
    	//go straight to Community Recommender
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>();
    	
    	communityRecommender.identifyCisActionForCSCW(cisRecommendations);
    	
    }
    
    
    
    @Override
    public void processCSMAnalyserRecommendations(ArrayList<IIdentity> cssList, ArrayList<CtxAttribute> sharedContextAttributes, ArrayList<CtxAssociation> sharedContextAssociations, ArrayList<ICssActivity> sharedCssActivities, ArrayList<IActivity> sharedCisActivities) {
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>(); 
    	
    	HashMap<String, ArrayList<ArrayList<ICisOwned>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisOwned>>>();
		//ICis proposedCis = cisManager.getBlankCisRecord();
    	ICisOwned proposedCis = null;
    	if (cisManager != null)
    		proposedCis = cisManager.getBlankCisOwned();
    	
    	/**	if (cisManager.getCisList().size() > 0)
    			proposedCis = cisManager.getCisList().get(0);
    		else
    			return;
    	*/
    		
    	//proposedCis.setName("");
    	//proposedCis.setType("");
    	//proposedCis.setMembershipCriteria(-1);
    	
    	//proposedCis.setMembersList(cssList);
    	ArrayList<Object> membershipCriteria = new ArrayList<Object>();
    	for (int i = 0; i < sharedContextAttributes.size(); i++) {
    		membershipCriteria.add(sharedContextAttributes.get(i));
    	}
    	for (int i = 0; i < sharedContextAssociations.size(); i++) {
    		membershipCriteria.add(sharedContextAssociations.get(i));
    	}
    	for (int i = 0; i < sharedCisActivities.size(); i++) {
    		membershipCriteria.add(sharedCisActivities.get(i));
    	}
    	for (int i = 0; i < sharedCssActivities.size(); i++) {
    		membershipCriteria.add(sharedCssActivities.get(i));
    	}
    	//proposedCis.setMembershipCriteria(membershipCriteria);
    	
    	ArrayList<ICisOwned> creations = new ArrayList<ICisOwned>();
    	creations.add(proposedCis);
		if (creations == null)
			creations = new ArrayList<ICisOwned>();
		
		ArrayList<ArrayList<ICisOwned>> abstractCreations = new ArrayList<ArrayList<ICisOwned>>();
		
		if (creations != null)
			if (creations.size() != 0)
				for (int i = 0; i < creations.size(); i++) {
					ArrayList<ICisOwned> it = new ArrayList<ICisOwned>();
					it.add(creations.get(i));
	            	abstractCreations.add(it);
				}
	
		
			ArrayList<String> preferenceConflicts = checkForPreferenceConflicts("Create CISs", abstractCreations);
			if (preferenceConflicts.size() != 0)
				for (int i = 0; i < preferenceConflicts.size(); i++) {
				    creations.remove(Integer.valueOf(preferenceConflicts.get(i).split("---")[0]));
		        }
	
			
for (int i = 0; i < creations.size(); i++) {
		    	
	    		ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractCreations);
	    		boolean refuseSuggestion = false;
	    		if (privacyConflicts.size() != 0) {
	    			for (int m = 0; m < privacyConflicts.size(); m++) {
	    				if (privacyConflicts.get(m).contains("User"))
	    				    refuseSuggestion = true;
	    				else if (privacyConflicts.get(m).contains("CSS: ")) {
	    					ICisOwned updatedCreation = creations.get(i);
	    					
	    					Future<Set<ICisParticipant>> theParticipants = updatedCreation.getMemberList();
	    					ArrayList<IIdentity> theMembers = new ArrayList<IIdentity>();
	    					if (theParticipants != null) {
	    						Set<ICisParticipant> theParticipantsSet = null;
								try {
									theParticipantsSet = theParticipants.get();
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (ExecutionException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	    						if (theParticipantsSet != null) {
	    							Iterator<ICisParticipant> it = theParticipantsSet.iterator();
	    							while (it.hasNext()) {
	        							try {
											theMembers.add(identityManager.fromJid(it.next().toString()));
										} catch (InvalidFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	        						}
	    						}
	    					}
	    						
	    					
	    					
	    					theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
	    					try {
								updatedCreation.removeMemberFromCIS(privacyConflicts.get(m).split("CSS: ")[1]);
							} catch (CommunicationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    					creations.set(i, updatedCreation);
	    				}
	    					
	    			}
	    		}
	    		if (refuseSuggestion == true)
	    			creations.remove(i);
	    		
	    		
	    	}
			
		/**for (int i = 0; i < creations.size(); i++) {
	
			ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractCreations);
			boolean refuseSuggestion = false;
			if (privacyConflicts.size() != 0) {
				for (int m = 0; m < privacyConflicts.size(); m++) {
					if (privacyConflicts.get(m).contains("User"))
				    	refuseSuggestion = true;
					else if (privacyConflicts.get(m).contains("CSS: ")) {
						ICisOwned updatedCreation = creations.get(i);
						//ArrayList<IIdentity> theMembers = updatedCreation.getMembersList();
						//theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
						//updatedCreation.setMembersList();
						creations.set(i, updatedCreation);
					}
					
				}
			}
			if (refuseSuggestion == true)
				creations.remove(i);
		
		
		}*/
	
		for (int i = 0; i < creations.size(); i++) {
			ICisOwned cisProposal = creations.get(i);
			List<ICis> userJoinedCissTemp = cisManager.getCisList();
			ArrayList<ICis> userJoinedCiss = new ArrayList<ICis>();
			for (int m = 0; m < userJoinedCissTemp.size(); m++) {
				userJoinedCiss.add(userJoinedCissTemp.get(m));
			}
			for (int m = 0; m < userJoinedCiss.size(); m++) {
				//if (userJoinedCiss.get(m).getMembershipCriteria == cisProposal.getMembershipCriteria()) {
				    //if (userJoinedCiss.get(m).getMembersList() == cisProposal.getMembersList()) {
				        //creations.remove(i);
				        //Feedback to CSM Analyser suggesting to either remove this model,
				        //add another attribute to it (suggest one?), or change one or more
				        //model attributes (specify them?)
			        //}
				    //else if (userJoinedCiss.get(m).getMembersList().size() >= (1.3 * cisProposal.getMembersList().size()) {
				    //    creations.add(sub-CIS for shared CSSs);
				    //}
				//}
				//if the membership criteria for the existing CIS somehow conflict with that of the suggestion,
				//may be grounds to delete the old CIS. E.g. the suggestion is based on location,
				//which is different to the location of an existing CIS, which is known to be short-term temporary.
			}
			//userContextBroker.evaluateSimilarity("location", convertedRecommendations.get(i).get(0).getMembersList());
			//if user isn't a member of another CIS that matches this, OK
		
			//if > 90% of members share that context, and there's no other context with more coverage of members,
			//let this be the highest level CIS criteria.
			//
			//else if (more members share some other context)
			//    convertedRecommendations.replace(i, same but with criteria changed to other context, or original as sub-CIS too);
		
			//else
			//    convertedRecommendations.remove(i);
		
			//If activity feed shows history in last few minutes, it's temporary and should be made immediately, as sub-CIS if an encapsulating CIS is available; otherwise, 8 months - ongoing, last month - long-term; last few days - medium-term
	    
			//Friends, CSS directory, working colleagues, same address: ongoing
			//Shared interests, personal attributes like languages spoken and age: sub-CIS.
			ArrayList<ICisOwned> ciss = new ArrayList<ICisOwned>();
			ciss.add(cisProposal);
			convertedRecommendations = advancedCisCreationAnalysis(ciss);
			
		}
	
	    /**abstractCreations.clear();
	    if (creations.size() != 0) {
		    for (int i = 0; i < creations.size(); i++) {
			    ArrayList<ICis> it = new ArrayList<ICis>();
			    it.add(creations.get(i));
                abstractCreations.add(it);
		    }
	        convertedRecommendations.put("Create CISs", abstractCreations);
	    }
	
	    abstractDeletions.clear();
	    if (deletions.size() != 0) {
		    for (int i = 0; i < deletions.size(); i++) {
			    ArrayList<ICis> it = new ArrayList<ICis>();
			    it.add(deletions.get(i));
                abstractDeletions.add(it);
		    }
	        convertedRecommendations.put("Delete CISs", abstractDeletions);
	    }*/
	
	    if (convertedRecommendations.size() != 0) {
	    	currentActionsMetadata = communityRecommender.identifyCisActionForCSMAnalyser(convertedRecommendations);
	    	ArrayList<String> cisIds = new ArrayList<String>();
	    	
	        for (int i = 0; i < currentActionsMetadata.size(); i++) {
	        	cisIds.add(currentActionsMetadata.get(i).split("---")[0].split("CIS ID: ")[1]);
	        	if (recordedMetadata.get(cisIds.get(i)) == null)
	        		recordedMetadata.put(cisIds.get(i), currentActionsMetadata.get(i));
	        }
	    }
    }
    
    public ArrayList<String> checkForPrivacyConflicts(ArrayList<ArrayList<ICisOwned>> recommendations) {
    	ArrayList<String> conflictingPrivacyPolicies = new ArrayList<String>();
    	for (int i = 0; i < recommendations.size(); i++) {
    		if (recommendations.get(i) != null) {
    			if (recommendations.get(i).get(0) != null) {
    				if (recommendations.get(i).get(0).getMembershipCriteria() != null) {
    					for (int m = 0; m < recommendations.get(i).get(0).getMembershipCriteria().size(); m++) {
    		    		    for (int n = 0; n < recommendations.get(i).get(0).getMembersList().size(); n++) {
    		    		        IIdentity thisMember = recommendations.get(i).get(0).getMembersList().get(n);
    		    			    Requestor thisRequestor = new Requestor(thisMember);
    		    		        Future<List<CtxIdentifier>> id = null;
    							try {
    								id = userContextBroker.lookup(CtxModelType.ATTRIBUTE, recommendations.get(i).get(0).getMembershipCriteria().get(m));
    							} catch (CtxException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    		    		        List<CtxIdentifier> id2 = null;
    							try {
    								id2 = id.get();
    							} catch (InterruptedException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} catch (ExecutionException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    		    		        
    		    		        CtxAttribute thisAttribute = null;
    							try {
    								thisAttribute = (CtxAttribute) userContextBroker.retrieve(id2.get(0)).get();
    							} catch (InterruptedException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} catch (ExecutionException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} catch (CtxException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    		    		        //Action xAction = new Action();
    		    		        ResponseItem response = null;
    							try {
    								response = privacyDataManager.checkPermission(thisRequestor, linkedCss, id2.get(0), null);
    							} catch (PrivacyException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    		    			    Decision decision = response.getDecision();
    		    		        if (decision.values()[0] != decision.PERMIT) conflictingPrivacyPolicies.add(i + "---" + thisAttribute + "---" + "User");
    		    		        //else if (ctxBroker.get(thisMember, thisAttribute).equals("Access refused"))
    		    		        //    conflictingPrivacyPolicies.add(i + "---" + thisAttribute + "---" + "CSS: " + thisMember.toString());
    		    		    }
    		    		}
    				}
    			}
    		}
    		
    	}
    	
    	return conflictingPrivacyPolicies;
    	
    }
    
    public ArrayList<String> checkForPreferenceConflicts(String action, ArrayList<ArrayList<ICisOwned>> recommendations) {
    	ArrayList<String> conflictingPreferences = new ArrayList<String>();
    	
    	for (int i = 0; i < recommendations.size(); i++) {
    		//for (int m = 0; m < recommendations.get(i).get(0).getMembershipCriteria().size(); m++) {
    		    //for (int n = 0; n < recommendations.get(i).get(0).getMembersList().size(); n++) {
    			    //boolean passed = personalisationManager.getPreference(recommendations.get(i).get(0).getMembershipCriteria().get(m), recommendations.get(i).get(0).getMembers(n), "refuse CIS action with given criteria", arg3) != null;
    			    //if (passed == false) conflictingPreferences.add(recommendations.get(i))
    		    //}
    		//}
    	}
    	
    	//personalisationManager.getPreference(arg0, arg1, arg2, arg3, "refuse CIS action with given criteria", arg5)
		return conflictingPreferences;
    }
    
    public HashMap<String, ArrayList<ArrayList<ICisOwned>>> advancedCisCreationAnalysis(ArrayList<ICisOwned> proposedCiss) {
    	HashMap<String, ArrayList<ArrayList<ICisOwned>>> finalisedCiss = new HashMap<String, ArrayList<ArrayList<ICisOwned>>>();
    	for (int i = 0; i < proposedCiss.size(); i++) {
    		ICisOwned thisCis = proposedCiss.get(i);
    		boolean allAttributes = true;
    		//for (int m = 0; m < thisCis.getMembershipCriteria().size(); m++) {
    		    //if (thisCis.getMembershipCriteria.get(m) instanceof CtxAssociation)
    		        //allAttributes = false;
    		//}
    		//if (allAttributes == true) {
    		    //if (thisCis.getMembershipCriteria().get(m).getType().equals("address") {
		            //if (thisCis.getMembershipCriteria().contains("friends")) {
    		            //Put address as sub-CIS of friends CIS
    		            //proposedActionsWithMetadata.add(i);
    	            //}
    		        //else
    		            //Put address first, and other attributes as sub-CISs.
    		    //        
		        //}
    		    //else {
    		        //ArrayList<ArrayList<ICis>> csmFeedback = new ArrayList<ArrayList<ICis>();
    		        //csmFeedback
    		        //finalisedCiss.put("Remove from CSM", csmFeedback);
    		        
    	        //}
    		//}
    		//for (int m = 0; m < thisCis.getMembershipCriteria().size(); m++) {
    		    //if (thisCis.getMembershipCriteria.get(m) instanceof CtxAssociation) {
    		    //    CtxAssociation theCriteria = thisCis.getMembershipCriteria.get(m);
    		    //    if (theCriteria.getId().getType().equals("proximity")) {
    		    //        //need access to proximity on other CSSs and
    		              //timestamp on proximity associations
    		    //    }
    	
    		    //}
    		    //else if (thisCis.getMembershipCriteria.get(m) instanceof CtxAttribute) {
		        //    CtxAttribute theCriteria = thisCis.getMembershipCriteria.get(m);
    		    //    if (thisCis.getMembershipCriteria().get(m).getType().equals("address") {
		        //        //if (thisCis.getMembershipCriteria().contains("friends"))
    		                  //Put address as sub-CIS of friends CIS
    		              //else
    		                  //Put address first, and other attributes as sub-CISs.
    		    //        
		        //    }
		        //}
    		//}
    	}
    	return finalisedCiss;
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
    
    public ICommCallback getCommCallback() {
    	return commCallback;
    }
    
    public void setCommCallback(ICommCallback commCallback) {
    	this.commCallback = commCallback;
    }
    
    public IServiceDiscovery getServiceDiscovery() {
    	return serviceDiscovery;
    }
    
    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
    	this.serviceDiscovery = serviceDiscovery;
    }
    
    public IServiceDiscoveryCallback getServiceDiscoveryCallback() {
    	return serviceDiscoveryCallback;
    }
    
    public void setServiceDiscoveryCallback(IServiceDiscoveryCallback serviceDiscoveryCallback) {
    	this.serviceDiscoveryCallback = serviceDiscoveryCallback;
    }
    
    public IDeviceManager getDeviceManager() {
    	return deviceManager;
    }
    
    public void setDeviceManager(IDeviceManager deviceManager) {
    	this.deviceManager = deviceManager;
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
    
/*   Eliza: these are not needed anymore
 *  public IPersonalisationCallback getPersonalisationCallback() {
    	return personalisationCallback;
    }
    
    public void setPersonalisationCallback(IPersonalisationCallback personalisationCallback) {
    	this.personalisationCallback = personalisationCallback;
    }*/
    
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
				//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
				//String serviceType = scaBean.getServiceType();
				processEgocentricRecommendations(scaBean.getCiss(), scaBean.getCissMetadata());
				break;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		case processEgocentricConfigurationRecommendations:
			try {
				//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
				//String serviceType = scaBean.getServiceType();
				processEgocentricConfigurationRecommendations(scaBean.getConfigureCiss(), scaBean.getCissMetadata());
				break;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
        case processCSMRecommendations:
		try {
			//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
			//String serviceType = scaBean.getServiceType();
			processCSMAnalyserRecommendations(scaBean.getCssList(), scaBean.getSharedContextAttributes(), scaBean.getSharedContextAssociations(), scaBean.getSharedCssActivities(), scaBean.getSharedCisActivities());
			break;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
        /**case processCSMConfigurationRecommendations:
    		try {
    			//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
    			//String serviceType = scaBean.getServiceType();
    			processCSMAnalyserConfigurationRecommendations(scaBean.getConfigureCiss());
    			break;
    		} catch (RuntimeException e) {
    			e.printStackTrace();
    		}*/
        case processCSCWRecommendations:
    		try {
    			//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
    			//String serviceType = scaBean.getServiceType();
    			processCSCWRecommendations(scaBean.getCiss());
    			break;
    		} catch (RuntimeException e) {
    			e.printStackTrace();
    		}
        case processCSCWConfigurationRecommendations:
    		try {
    			//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
    			//String serviceType = scaBean.getServiceType();
    			processCSCWConfigurationRecommendations(scaBean.getConfigureCiss());
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
    
    
    
    /**public void processCSMAnalyserRecommendations(HashMap<String, ArrayList<ICis>> cisRecommendations) {
	HashMap<String, ArrayList<ArrayList<ICis>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICis>>>();
	ArrayList<ICis> creations = cisRecommendations.get("Create CISs");
	if (creations == null)
		creations = new ArrayList<ICis>();
	ArrayList<ICis> deletions = cisRecommendations.get("Delete CISs");
	if (deletions == null)
		deletions = new ArrayList<ICis>();
	ArrayList<ArrayList<ICis>> abstractCreations = new ArrayList<ArrayList<ICis>>();
	ArrayList<ArrayList<ICis>> abstractDeletions = new ArrayList<ArrayList<ICis>>();
	
	if (creations != null)
		if (creations.size() != 0)
			for (int i = 0; i < creations.size(); i++) {
				ArrayList<ICis> it = new ArrayList<ICis>();
				it.add(creations.get(i));
	            abstractCreations.add(it);
			}
	
	
	if (deletions != null) {
		if (deletions.size() != 0)
			for (int i = 0; i < deletions.size(); i++) {
				ArrayList<ICis> it = new ArrayList<ICis>();
				it.add(deletions.get(i));
	            abstractDeletions.add(it);
			}
	}
	
	
	for (int i = 0; i < creations.size(); i++) {
		if (checkForPreferenceConflicts("Create CISs", abstractCreations).size() != 0)
			creations.remove(i);
	}
	
	for (int i = 0; i < deletions.size() && deletions != null; i++) {
		if (checkForPreferenceConflicts("Delete CISs", abstractDeletions).size() != 0)
			deletions.remove(i);
	}
	
	for (int i = 0; i < creations.size(); i++) {
	
		ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractCreations);
		boolean refuseSuggestion = false;
		if (privacyConflicts.size() != 0) {
			for (int m = 0; m < privacyConflicts.size(); m++) {
				if (privacyConflicts.get(m).contains("User policy"))
				    refuseSuggestion = true;
				else if (privacyConflicts.get(m).contains("CSS: ")) {
					ICis updatedCreation = creations.get(i);
					//ArrayList<IIdentity> theMembers = updatedCreation.getMembersList();
					//theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
					//updatedCreation.setMembersList();
					creations.set(i, updatedCreation);
				}
					
			}
		}
		if (refuseSuggestion == true)
			creations.remove(i);
		
		
	}
	
	for (int i = 0; i < deletions.size(); i++) {
    	
		ArrayList<String> privacyConflicts = checkForPrivacyConflicts(abstractDeletions);
		boolean refuseSuggestion = false;
		if (privacyConflicts.size() != 0) {
			for (int m = 0; m < privacyConflicts.size(); m++) {
				if (privacyConflicts.get(m).equals("User policy"))
				    refuseSuggestion = true;
				else if (privacyConflicts.get(m).contains("CSS: ")) {
					ICis updatedCreation = creations.get(i);
					//ArrayList<IIdentity> theMembers = updatedCreation.getMembersList();
					//theMembers.remove(privacyConflicts.get(m).split("CSS: ")[1]);
					//updatedCreation.setMembersList();
					creations.set(i, updatedCreation);
				}
			}
		}
		if (refuseSuggestion == true)
			deletions.remove(i);
		
		
	}
	
	for (int i = 0; i < creations.size(); i++) {
		//userContextBroker.evaluateSimilarity("location", convertedRecommendations.get(i).get(0).getMembersList());
		//if user isn't a member of another CIS that matches this, OK
		
		//if > 90% of members share that context, and there's no other context with more coverage of members,
		//let this be the highest level CIS criteria.
		//
		//else if (more members share some other context)
		//    convertedRecommendations.replace(i, same but with criteria changed to other context, or original as sub-CIS too);
		
		//else
		//    convertedRecommendations.remove(i);
		
		//If activity feed shows history in last few minutes, it's temporary and should be made immediately, as sub-CIS if an encapsulating CIS is available; otherwise, 8 months - ongoing, last month - long-term; last few days - medium-term
	    
		//Friends, CSS directory, working colleagues, same address: ongoing
		//Shared interests, personal attributes like languages spoken and age: sub-CIS.
	}
	
	for (int i = 0; i < deletions.size(); i++) {
		//If activity feed of CIS is dead for a while, depending on criteria it's based on, OK.
	}
	
	
	abstractCreations.clear();
	if (creations.size() != 0) {
		for (int i = 0; i < creations.size(); i++) {
			ArrayList<ICis> it = new ArrayList<ICis>();
			it.add(creations.get(i));
            abstractCreations.add(it);
		}
	    convertedRecommendations.put("Create CISs", abstractCreations);
	}
	
	abstractDeletions.clear();
	if (deletions.size() != 0) {
		for (int i = 0; i < deletions.size(); i++) {
			ArrayList<ICis> it = new ArrayList<ICis>();
			it.add(deletions.get(i));
            abstractDeletions.add(it);
		}
	    convertedRecommendations.put("Delete CISs", abstractDeletions);
	}
	
	if (convertedRecommendations.size() != 0) {
		ArrayList<String> actionMetadata = communityRecommender.identifyCisActionForCSMAnalyser(convertedRecommendations);
	}
}

public void processCSMAnalyserConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICis>>> cisRecommendations) {
	//go straight to Community Recommender
	
	communityRecommender.identifyCisActionForCSMAnalyser(cisRecommendations);
	
}*/
    
}