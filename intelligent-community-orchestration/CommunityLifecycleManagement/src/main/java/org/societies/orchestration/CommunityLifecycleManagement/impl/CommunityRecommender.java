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

import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.api.css.directory.ICssDirectory;

//import org.societies.api.css.discovery.ICssDiscovery;

//import org.societies.api.internal.cis.management.ICisActivityFeed;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.api.internal.cis.management.ICisActivity;
//import org.societies.api.internal.cis.management.ICis;
//import org.societies.api.internal.cis.management.ICisManager;

import org.societies.api.cis.directory.ICisAdvertisementRecord;
/**import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;

import org.societies.api.cis.management.ICisEditor;*/

import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisParticipant;
import org.societies.orchestration.api.ICisProposal;
//import org.societies.orchestration.api.ICisEditor;

//import org.societies.api.cis.management.ICisSubscribed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
//import org.societies.api.cis.management.ICis;

//import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

//import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

//import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.management.ICssActivityFeed;
import org.societies.api.css.management.ICssRecord;

//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer; 
//import org.societies.comm.xmpp.interfaces.ICommCallback;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



//import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;
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
		
	private int longestTimeWithoutActivity;
	
	private ICtxBroker userContextBroker;

	private ICisManager cisManager;
	private IUserFeedback userFeedback;

	private String userResponse;
	
	private ArrayList<ICisProposal> recentRefusals;

	private IUserFeedbackCallback userFeedbackCallback;
	
	//private ArrayList<ICis> cissToCreate;
	//private HashMap<String, ArrayList<ArrayList<ICis>>> cissToConfigure;
	//private ArrayList<ICis> cissToDelete;
	
	private ICommManager commManager;
	private ICommCallback commCallback;
	
	private IServiceDiscovery serviceDiscovery;
	private IServiceDiscoveryCallback serviceDiscoveryCallback;
	
	private IDeviceManager deviceManager;
	
	private IIdentityManager identityManager;
	
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
	
	public ArrayList<String> identifyAndPerformCisActions(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cisPossibilities, ArrayList<String> cissToCreateMetadata) {
		ArrayList<String> cisAddMetadata = new ArrayList<String>();
		ArrayList<String> cisNotDeletedMetadata = new ArrayList<String>();
		ArrayList<String> cisCreatedFromConfigurationMetadata = new ArrayList<String>();
		if (cisPossibilities.get("Create CISs") != null) {
			ArrayList<ArrayList<ICisProposal>> theList = cisPossibilities.get("Create CISs");
			ArrayList<ICisProposal> theSubList = new ArrayList<ICisProposal>();
			for (int i = 0; i < theList.size(); i++)
				theSubList.add(theList.get(i).get(0));
			cisAddMetadata = identifyCissToCreate(theSubList, cissToCreateMetadata);
		}
		    
		if (cisPossibilities.get("Delete CISs") != null) {
			ArrayList<ArrayList<ICisProposal>> theList = cisPossibilities.get("Delete CISs");
			ArrayList<ICisProposal> theSubList = new ArrayList<ICisProposal>();
			for (int i = 0; i < theList.size(); i++)
				theSubList.add(theList.get(i).get(0));
			cisNotDeletedMetadata = identifyCissToDelete(theSubList, cissToCreateMetadata);
		}
		HashMap<String, ArrayList<ArrayList<ICisProposal>>> temp = new HashMap<String, ArrayList<ArrayList<ICisProposal>>>();
		ArrayList<ArrayList<ICisProposal>> temp2 = cisPossibilities.get("Configure CISs");
		
		if (temp2 != null) {
			if (temp2.size() > 0)
				temp.put("Configure CISs", cisPossibilities.get("Configure CISs"));
		}
		temp2 = cisPossibilities.get("Merge CISs");
		if (temp2 != null) {
			if (temp2.size() > 0)
				temp.put("Merge CISs", cisPossibilities.get("Merge CISs"));
		}
		
		temp2 = cisPossibilities.get("Split CISs");
		if (temp2 != null) {
			if (temp2.size() > 0)
				temp.put("Split CISs", cisPossibilities.get("Split CISs"));
		}
		
		if (temp.size() > 0)
		    cisCreatedFromConfigurationMetadata = identifyCissToConfigure(temp, cissToCreateMetadata);
		
		ArrayList<String> cisMetadata = new ArrayList<String>();
		for (int i = 0; i < cisAddMetadata.size(); i++)
			cisMetadata.add(cisAddMetadata.get(i));
		for (int i = 0; i < cisNotDeletedMetadata.size(); i++)
			cisMetadata.add(cisNotDeletedMetadata.get(i));
		for (int i = 0; i < cisCreatedFromConfigurationMetadata.size(); i++)
			cisMetadata.add(cisCreatedFromConfigurationMetadata.get(i));
		return cisMetadata;
	}
	
	public ArrayList<String> identifyCisActionForEgocentricCommunityAnalyser(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cisPossibilities, ArrayList<String> cissToCreateMetadata) {
		
		return identifyAndPerformCisActions(cisPossibilities, cissToCreateMetadata);
		
		/**ArrayList<String> cisAddMetadata = new ArrayList<String>();
		ArrayList<String> cisNotDeletedMetadata = new ArrayList<String>();
		ArrayList<String> cisCreatedFromConfigurationMetadata = new ArrayList<String>();
		if (cisPossibilities.get("Create CISs") != null)
		    cisAddMetadata = identifyCissToCreate(cisPossibilities.get("Create CISs").get(0), cissToCreateMetadata);
		if (cisPossibilities.get("Delete CISs") != null)
		    cisNotDeletedMetadata = identifyCissToDelete(cisPossibilities.get("Delete CISs").get(0), cissToCreateMetadata);
		HashMap<String, ArrayList<ArrayList<ICisProposal>>> temp = new HashMap<String, ArrayList<ArrayList<ICisProposal>>>();
		temp.put("Configure CISs", cisPossibilities.get("Configure CISs"));
		temp.put("Merge CISs", cisPossibilities.get("Merge CISs"));
		temp.put("Split CISs", cisPossibilities.get("Split CISs"));
		if (temp.size() > 0)
		    cisCreatedFromConfigurationMetadata = identifyCissToConfigure(temp, cissToCreateMetadata);
		
		ArrayList<String> cisMetadata = new ArrayList<String>();
		for (int i = 0; i < cisAddMetadata.size(); i++)
			cisMetadata.add(cisAddMetadata.get(i));
		for (int i = 0; i < cisNotDeletedMetadata.size(); i++)
			cisMetadata.add(cisNotDeletedMetadata.get(i));
		for (int i = 0; i < cisCreatedFromConfigurationMetadata.size(); i++)
			cisMetadata.add(cisCreatedFromConfigurationMetadata.get(i));
		
		return cisMetadata;*/
	}
	
	public ArrayList<String> identifyCisActionForCSCW(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cisPossibilities) {
		
		return identifyAndPerformCisActions(cisPossibilities, null);
		
		/**ArrayList<String> cisAddMetadata = new ArrayList<String>();
		ArrayList<String> cisNotDeletedMetadata = new ArrayList<String>();
		ArrayList<String> cisCreatedFromConfigurationMetadata = new ArrayList<String>();
		if (cisPossibilities.get("Create CISs") != null) {
			ArrayList<ArrayList<ICisProposal>> theList = cisPossibilities.get("Create CISs");
			ArrayList<ICisProposal> theSubList = new ArrayList<ICisProposal>();
			for (int i = 0; i < theList.size(); i++)
				theSubList.add(theList.get(i).get(0));
			cisAddMetadata = identifyCissToCreate(theSubList, null);
		}
		    
		if (cisPossibilities.get("Delete CISs") != null) {
			ArrayList<ArrayList<ICisProposal>> theList = cisPossibilities.get("Delete CISs");
			ArrayList<ICisProposal> theSubList = new ArrayList<ICisProposal>();
			for (int i = 0; i < theList.size(); i++)
				theSubList.add(theList.get(i).get(0));
			cisNotDeletedMetadata = identifyCissToDelete(theSubList, null);
		}
		HashMap<String, ArrayList<ArrayList<ICisProposal>>> temp = new HashMap<String, ArrayList<ArrayList<ICisProposal>>>();
		temp.put("Configure CISs", cisPossibilities.get("Configure CISs"));
		temp.put("Merge CISs", cisPossibilities.get("Merge CISs"));
		temp.put("Split CISs", cisPossibilities.get("Split CISs"));
		
		if (temp.size() > 0)
		    cisCreatedFromConfigurationMetadata = identifyCissToConfigure(temp, null);
		
		ArrayList<String> cisMetadata = new ArrayList<String>();
		for (int i = 0; i < cisAddMetadata.size(); i++)
			cisMetadata.add(cisAddMetadata.get(i));
		for (int i = 0; i < cisNotDeletedMetadata.size(); i++)
			cisMetadata.add(cisNotDeletedMetadata.get(i));
		for (int i = 0; i < cisCreatedFromConfigurationMetadata.size(); i++)
			cisMetadata.add(cisCreatedFromConfigurationMetadata.get(i));
		return cisMetadata;*/
		
	}
	
    public ArrayList<String> identifyCisActionForCSMAnalyser(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cisPossibilities, ArrayList<String> metadata) {
    	return identifyAndPerformCisActions(cisPossibilities, metadata);
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
	
	public ArrayList<String> identifyCissToCreate(ArrayList<ICisProposal> creatableCiss, ArrayList<String> cissToCreateMetadata) {		
		HashMap<Integer, ICisProposal> cissToCreate = new HashMap<Integer, ICisProposal>();
		
		//Can't use GUI in tests
		//cissToCreate = getUserFeedbackOnCreation(cissToCreate);
		
		for (int i = 0; i < creatableCiss.size(); i++) {
			cissToCreate.put(new Integer(i), creatableCiss.get(i));
		}
		
		if (cissToCreateMetadata == null) {
			cissToCreateMetadata = new ArrayList<String> ();
			Set<Integer> keys = cissToCreate.keySet();
			Iterator<Integer> keysIt = keys.iterator();
			while (keysIt.hasNext()) {
				Integer cisAccepted = keysIt.next();
				cissToCreateMetadata.add("CIS ID: " + cissToCreate.get(cisAccepted).getActualCis().getCisId() + "---" + "DESCRIPTION: " + cissToCreate.get(cisAccepted).getDescription() + "---");
			}
		}
		
		if (cissToCreate != null) {
			int lastIndex = -1;
		    for (int i = 0; i < cissToCreate.size(); i++) {
		    	Future<ICisOwned> createdCisFuture = null;
			    try {
		    	    createdCisFuture = cisManager.createCis(linkedCss.getIdentifier(), null, cissToCreate.get(i).getName(), null, 0);
			    } catch (NullPointerException e) {
			    	e.printStackTrace();
			    }
			    
		    	ICisOwned createdCis = null;
				try {
					if (createdCisFuture != null)
					    createdCis = createdCisFuture.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
				
				if (cissToCreateMetadata.size() > i) {
					boolean foundCis = false;
					for (int m = lastIndex + 1; (m < creatableCiss.size()) && (foundCis == false); m++) {
						if (cissToCreate.get(m) != null) {
							if (!(cissToCreateMetadata.get(m).split("---")[0].substring(0, 7).equals("CIS ID: ")))
								cissToCreateMetadata.set(m, "CIS ID: " + createdCis.getCisId() + "---" + cissToCreateMetadata.get(m));
							lastIndex = m;
							foundCis = true;
						}
					}
				}
			    
			    //ICisAdvertisementRecord createdCisAdvert = new ICisAdvertisementRecord(createdCis.getName(), createdCis.getDescription(), createdCis.getCisEditor().getURI());
			    //for (int m = 0; m < cissToCreate.get(i).getMemberList().size(); m++) {
			    //    ICssRecord member = cssManager.getCssRecord(cissToCreate.get(i).getMemberList().get(m));
			    //    ICssActivityFeed feed = member.getActivityFeed();
			    //    feed.addActivity(createdCisAdvert);
		        //}
		    }
		}
		return cissToCreateMetadata;
	}
	
	public HashMap<Integer, ICisProposal> getUserFeedbackOnCreation(ArrayList<ICisProposal> cissToCreate) {
		HashMap<Integer, ICisProposal> finalisedCiss = new HashMap<Integer, ICisProposal>();
		String[] options = new String[cissToCreate.size()];
		options[0] = "options";
		for (int i = 0; i < cissToCreate.size(); i++) {
			options[i] = cissToCreate.get(i).toString();
		}
		String userResponse = "";
		boolean responded = false;
		userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects the follwing CISs may benefit you. If you would like to create one or more of these CISs, please check them.", options), userFeedbackCallback);
		for (int i = 0; i < 300; i++) {
		    if (userResponse.equals(""))
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
		   	for (int i = 0; i < cissToCreate.size(); i++) {
			    ICisProposal potentiallyCreatableCis = cissToCreate.get(i);
		        if (userResponse.charAt(i) == 'Y') {
				    finalisedCiss.put(new Integer(i), potentiallyCreatableCis);
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
	
	public ArrayList<String> identifyCissToDelete(ArrayList<ICisProposal> cisPossibilities, ArrayList<String> cissToDeleteMetadata) {	
		HashMap<Integer, ICisProposal> cissToDelete = new HashMap<Integer, ICisProposal>();
		
		//Can't use GUI in tests
        //cissToDelete = getUserFeedbackOnDeletion(cisPossibilities);
		
		for (int i = 0; i < cisPossibilities.size(); i++) {
			cissToDelete.put(new Integer(i), cisPossibilities.get(i));
		}
		
		for (int i = 0; i < cissToDelete.size(); i++) {
			cisManager.deleteCis(linkedCss.getIdentifier(), cissToDelete.get(i).getActualCis().getCisId(), null);
	    }
		
		ArrayList<String> cissNotDeletedMetadata = new ArrayList<String>();
		for (int i = 0; i < cissToDeleteMetadata.size(); i++)
			cissNotDeletedMetadata.add(cissToDeleteMetadata.get(i));
		
		for (int i = 0; i < cisPossibilities.size(); i++) {
		    if (!cissToDelete.containsKey(i)) {
		        if (cissNotDeletedMetadata.get(i).contains("short-term temporary"))
		            cissNotDeletedMetadata.set(i, cissNotDeletedMetadata.get(i).replace("short-term temporary", "medium-term temporary"));
		        if (cissNotDeletedMetadata.get(i).contains("medium-term temporary"))
		            cissNotDeletedMetadata.set(i, cissNotDeletedMetadata.get(i).replace("medium-term temporary", "long-term temporary"));
		        if (cissNotDeletedMetadata.get(i).contains("long-term temporary"))
		            cissNotDeletedMetadata.set(i, cissNotDeletedMetadata.get(i).replace("long-term temporary", "ongoing"));
		    }
		    else
		    	cissNotDeletedMetadata.remove(i);
		}
		return cissNotDeletedMetadata;
	}
	
	public HashMap<Integer, ICisProposal> getUserFeedbackOnDeletion(ArrayList<ICisProposal> cissToDelete) {
		HashMap<Integer, ICisProposal> realCissToDelete = new HashMap<Integer, ICisProposal>();
		List<String> options = new ArrayList<String>();
		for (int i = 0; i < cissToDelete.size(); i++) {
			options.add(cissToDelete.get(i).toString());
		}
		//options.add("options");
		userResponse = "";
		boolean responded = false;
		//userFeedback.getExplicitFB(arg0, arg1, arg2);
		//userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects these CISs may be obsolete. If you would like to delete one or more of these CISs, please check them.", options.toArray(new String[options.size()]), userFeedbackCallback);
		while (userResponse.equals("")) {
		    if (userResponse.equals(""))
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
			    responded = true;
		}
		
		if (userResponse.equals("")) {
		    //User obviously isn't paying attention to CSS, so put the message in the background/list of messages for them to see at their leisure.
		    String background = "This message is in your inbox or something, waiting for you to read it";
		}
		else {
			for (int i = 0; i < cissToDelete.size(); i++) {
			    ICisProposal potentiallyDeletableCis = cissToDelete.get(i);
		        if (userResponse.charAt(i) == 'Y') {
				    realCissToDelete.put(new Integer(i), potentiallyDeletableCis);
				 // cisManager.deleteCis(linkedCss, potentiallyDeletableCis.getCisId());
		        }
		        else {
		    	    recentRefusals.add(potentiallyDeletableCis);
		        }
		   }
		}
		return realCissToDelete;
	}
	
	public ArrayList<String> identifyCissToConfigure(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cisPossibilities, ArrayList<String> cissToCreateMetadata) {
	
		
	    HashMap<String, ArrayList<ArrayList<ICisProposal>>> cissToConfigure = new HashMap<String, ArrayList<ArrayList<ICisProposal>>> ();
	
	    
	    //can't use GUI in tests
	    //cissToConfigure = getUserFeedbackOnConfiguration(cissToConfigure);
	
	    cissToConfigure = cisPossibilities;
	    
	    int configuresSize = 0;
	    int mergesSize = 0;
	    int splitsSize = 0;
	    
	    if (cissToConfigure.get("Configure CISs") != null)
	    	configuresSize = cissToConfigure.get("Configure CISs").size();
	    if (cissToConfigure.get("Merge CISs") != null)
	    	mergesSize = cissToConfigure.get("Merge CISs").size();
	    if (cissToConfigure.get("Split CISs") != null)
	    	splitsSize = cissToConfigure.get("Split CISs").size();
	    //Iterator<ArrayList<ICis>> iterator = cissToConfigure.iterator();
	
	    int metadataCounter = 0;
	    
	    for (int i = 0; (i < cissToConfigure.size()) && 
	    		        ((i < configuresSize) ||
	    		         (i < mergesSize) ||
	    		         (i < splitsSize)); i++) {
	        if (i < configuresSize) {
	        	
	    	    ArrayList<ICisProposal> configurableCisProposals = cissToConfigure.get("Configure CISs").get(i);
	    	    //ArrayList<ICisOwned> configurableCis = new ArrayList<ICisOwned>();
	    	    ArrayList<ICisProposal> configurableCis = new ArrayList<ICisProposal>();
	    	    Iterator<ICisProposal> it = configurableCisProposals.iterator();
	    	    while (it.hasNext())
	    	    	configurableCis.add(it.next());
	    	    	//configurableCis.add(((ICisOwned)it.next().getActualCis()));
	    	    	
                IIdentity cisID = null;
	            try {
				    cisID = identityManager.fromJid(configurableCis.get(0).getActualCis().getCisId());
			    } catch (InvalidFormatException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
			    }
	            
	            ICisOwned ownedCis = (ICisOwned) configurableCis.get(0).getActualCis();
	            
	            Set<ICisParticipant> participants = null;
				try {
					participants = ownedCis.getMemberList().get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        ArrayList<String> members = new ArrayList<String>();
	            Iterator<ICisParticipant> partIt = participants.iterator();
	            while (partIt.hasNext()) {
	                members.add(it.next().toString());
	            }
	            
	            if (!(members.contains(configurableCis.get(1).getMemberList()))) {
	                if(!(members.contains(linkedCss)) && (configurableCis.get(1).getMemberList().contains(linkedCss.getBareJid()))) {
	                    //cisManager.joinCis(linkedCss, cisID);
	                	
	                }
	                else {
                    	Set<ICisParticipant> theMembers = new HashSet<ICisParticipant>();
                    	Set<String> theUltimateMembers = configurableCis.get(1).getMemberList();
                    	Iterator<String> ultimateMembersIterator = theUltimateMembers.iterator();
                    	while (ultimateMembersIterator.hasNext()) {
                    		String thisMember = ultimateMembersIterator.next();
                    		if (!(members.contains(thisMember))) {
                    			//cisManager.setMembersList(cisID, configurableCis.get(1).getMemberList());
                        		
                        		//ICisAdvertisementRecord cisAdvert = new ICisAdvertisementRecord(configurableCis.get(0).getName(), configurableCis.get(0).getDescription());
                			    
                			    //ICssRecord member = cssManager.getCssRecord(ownedCis.getMemberList().get(ownedCis.getParticipant(thisMember)));
                			    //ICssActivityFeed feed = member.getActivityFeed();
                			    //feed.addActivity(cisAdvert);
                		        //
                    		}
                    	}
                        
                    }
	                //else {
	                    //cisManager.setMembersList(cisID, configurableCis.get(1).getMemberList());
	                //}
	            }
	            if (!(configurableCis.get(1).getMemberList().contains(members))) {
                    if(!(configurableCis.get(1).getMemberList().contains(linkedCss)) && (members.contains(linkedCss))) {
                        //cisManager.leaveCis(linkedCss, cisID);
                    }
                    else {
                    	Set<ICisParticipant> theMembers = new HashSet<ICisParticipant>();
                    	Set<String> theUltimateMembers = configurableCis.get(1).getMemberList();
                    	Iterator<String> membersIterator = members.iterator();
                    	while (membersIterator.hasNext()) {
                    		String thisMember = membersIterator.next();
                    		if (!(theUltimateMembers.contains(thisMember))) {
                    			//cisManager.removeMember(cisID, thisMember);
                    		}
                    	}
                        
                    }
                }
	            if (ownedCis.getMembershipCriteria() != configurableCis.get(1).getMembershipCriteria()) {
	                //cisManager.setMembershipCriteria(cisID, configurableCis.get(1).getMembershipCriteria());
	            }
	            if (ownedCis.getOwnerId() != configurableCis.get(1).getOwnerId()) {
	                //cisManager.setOwner(cisID, configurableCis.get(1).getOwnerId());
	            	
	            	ownedCis.setOwnerId(configurableCis.get(1).getOwnerId());
	            	
	            }
	            
	            Set<String> administratorList = configurableCis.get(1).getAdministratorList();
	            if (ownedCis.getAdministrators() != administratorList) {
		            
		            Iterator<String> adminIterator = administratorList.iterator();
		            while (adminIterator.hasNext()) {
		            	String next = adminIterator.next();
		            	Set<ICisParticipant> admins = null;
						try {
							admins = ownedCis.getAdministrators().get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	if (!(admins.contains(next))) {
		            		try {
								ownedCis.addAdministrator(identityManager.fromJid(next));
							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            	}
		            }
	            	for (int m = 0; m < configurableCis.get(1).getAdministratorList().size(); m++) {
	            		
	            	}
	                //cisManager.setAdministrators(cisID, configurableCis.get(1).getAdministratorList());
	            }
	            
	            
	            
	            //cisManager.configureCis(cisID, ownedCis);
	          
	            cissToCreateMetadata.remove(metadataCounter);
	        }
	        if (i < mergesSize) {
	        	
	            //if "merge with other CIS"
        	    //
	            
	            ArrayList<ICisProposal> configurableCisProposals = cissToConfigure.get("Merge CISs").get(i);
	    	    ArrayList<ICisOwned> configurableCis = new ArrayList<ICisOwned>();
	    	    Iterator<ICisProposal> it = configurableCisProposals.iterator();
	    	    while (it.hasNext())
	    	    	configurableCis.add(((ICisOwned)it.next().getActualCis()));
	            
	            //
	            //IIdentity[] membersList = configurableCis.get(0).getMembersList();
	            //IIdentity[] membersList2 = configurableCis.get(1).getMembersList()
	        
	            IIdentity[] membersList = new IIdentity[1];
	            IIdentity[] membersList2 = new IIdentity[1];
	            IIdentity[] mergedMembersList = new IIdentity[membersList.length + membersList2.length];
	            //for (int m = 0; m < mergedMembersList.length; m++)
	            //    
	            //    if (m < membersList.length) {
	            //        if (!mergedMembersList.contains(membersList[m]))
	            //            mergedMembersList[m] = membersList[m];
	            //    }
	            //    else if (!mergedMembersList.contains(membersList2[m-membersList.length])) (mergedMembersList[m] = membersList2[m-membersList.length];
	        
	            //IIdentity[] administrators = configurableCis.get(0).getAdministrators();
	            //IIdentity[] administrators2 = configurableCis.get(1).getAdministrators()
	            //IIdentity[] mergedAdministrators = new IIdentity[administrators.size() + administrators2.size()];
	            //for (int m = 0; m < mergedAdministrators.length; m++)
	            //    
	            //    if (m < administrators.length) {
	            //        if (!mergedadministrators.contains(administrators[m]))
	            //            mergedAdministrators[m] = administrators[m];
	            //    }
	            //    else if (!mergedAdministrators.contains(administrators2[m-administrators.length])) (mergedAdministrators[m] = administrators2[m-administrators.length];
	        
	            //IIdentity[] owner = configurableCis.get(0).getMembersList();
                //if (configurableCis.get(i).get(0).getOwner() != configurableCis.get(1).getOwner())
	            //    owner = configurableCis.get(1);
	        
	            //for (int m = 0; m < mergedMembersList.length; m++)
	            //    
	            //    if (m < membersList.length) {
	            //        if (!mergedMembersList.contains(membersList[m]))
	            //            mergedMembersList[m] = membersList[m];
	            //    }
	            //    else if (!mergedMembersList.contains(membersList2[m-membersList.length])) (mergedMembersList[m] = membersList2[m-membersList.length];
	        
	        
	            Future<ICisOwned> createdCisFuture = cisManager.createCis(configurableCis.get(0).getName() + " MERGED WITH " + configurableCis.get(1).getName(),
	                             mergedMembersList.toString(), null, null, 0);
	            ICisOwned createdCis = null;
			    try {
			    	createdCis = createdCisFuture.get();
			    } catch (InterruptedException e) {
			    	// TODO Auto-generated catch block
			    	e.printStackTrace();
			    } catch (ExecutionException e) {
			    	// TODO Auto-generated catch block
			    	e.printStackTrace();
			    }
	            for (int m = 0; m < cisPossibilities.size(); m++) {
	            	if ((configurableCis.get(0).getCisId() == cisPossibilities.get("Merge CISs").get(m).get(0).getActualCis().getCisId()) && 
	        	    	(configurableCis.get(1).getCisId() == cisPossibilities.get("Merge CISs").get(m).get(1).getActualCis().getCisId())	)
	        	    	cissToCreateMetadata.set(metadataCounter, "CIS ID: " + createdCis.getCisId() + "---" + cissToCreateMetadata.get(metadataCounter));
	            }
	            
	            metadataCounter++;
	        }
	        
	        if (i < splitsSize) {
        	    //if "split into distinct CISs"
        	    //
	            
	            ArrayList<ICisProposal> configurableCisProposals = cissToConfigure.get("Split CISs").get(i);
	    	    ArrayList<ICisOwned> configurableCis = new ArrayList<ICisOwned>();
	    	    Iterator<ICisProposal> it = configurableCisProposals.iterator();
	    	    while (it.hasNext())
	    	    	configurableCis.add(((ICisOwned)it.next().getActualCis()));
	    	    
	            //cisManager.createCis(configurableCis.get(1));
	            //cisManager.createCis(configurableCis.get(2));
	        
	            String membersListTemp = "";
	        
	            Future<ICisOwned> createdCisFuture = cisManager.createCis(configurableCis.get(1).getName(),
                        membersListTemp, null, null, 0);
                ICisOwned createdCis = null;
                try {
	                createdCis = createdCisFuture.get();
                } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
 	                e.printStackTrace();
                } catch (ExecutionException e) {
 	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
                for (int m = 0; m < cisPossibilities.size(); m++) {
	            if ((configurableCis.get(0).getCisId() == cisPossibilities.get("Split CISs").get(m).get(0).getActualCis().getCisId())) 
		            cissToCreateMetadata.set(metadataCounter, "CIS ID: " + createdCis.getCisId() + "---" + cissToCreateMetadata.get(metadataCounter));
                }
                metadataCounter = metadataCounter + 2;
	        }
        	//if "switch sub-CIS and CIS"
        	//
        	//
	        // cisManager.configureCis(linkedCss, potentiallyConfigurableCis.getCisId());
        }
	    return cissToCreateMetadata;
    }

    public HashMap<String, ArrayList<ArrayList<ICisProposal>>> getUserFeedbackOnConfiguration(HashMap<String, ArrayList<ArrayList<ICisProposal>>> cissToConfigure) {
	    HashMap<String, ArrayList<ArrayList<ICisProposal>>> realCissToConfigure = new HashMap<String, ArrayList<ArrayList<ICisProposal>>> ();
	    String[] options = new String[cissToConfigure.get("Configure CISs").size() + cissToConfigure.get("Merge CISs").size() + cissToConfigure.get("Split CISs").size()];
	    options[0] = "options";
	    for (int i = 0; i < cissToConfigure.get("Configure CISs").size(); i++) {
			ICis thisCis = cissToConfigure.get("Configure CISs").get(i).get(0).getActualCis();
			ICis thisCisConfigured = cissToConfigure.get("Configure CISs").get(i).get(1).getActualCis();
			//if (thisCis.getMembersList().size() < thisCisConfigured.getMembersList().size()) {
			    //String members = "";
			    //for (int m = 0; m < thisCisConfigured.getMembersList()) {
			    //    if (!thisCis.getMembersList().contains(thisCisConfigured.getMembersList().get(m)))
			    //        members.append(thisCisConfigured.getMembersList().get(m));
			    //}
				//options[i] = "Change the CIS " + thisCis.getId() + thisCis.getName() + " to add the following members: " + members;
			//}
			//if (thisCis.getMembersList().size() > thisCisConfigured.getMembersList().size()) {
		        //String members = "";
		        //for (int m = 0; m < thisCis.getMembersList()) {
		        //    if (!thisCisConfigured.getMembersList().contains(thisCis.getMembersList().get(m)))
		        //        members.append(thisCis.getMembersList().get(m));
		        //}
			    //options[i] = "Change the CIS " + thisCis.getId() + thisCis.getName() + " to remove the following members: " + members;
		    //}
			//if (thisCis.getMembersList().size() == thisCisConfigured.getMembersList().size())
				options[i] = "Change CIS " + thisCis.getCisId() + " to have these attributes: " + cissToConfigure.get("Configure CISs").get(i).get(1).toString();
		}
        for (int i = 0; i < cissToConfigure.get("Merge CISs").size(); i++) {
			
	    	options[options.length] = "Merge " + cissToConfigure.get("Merge CISs").get(i).get(0).getActualCis().toString() + "and " + cissToConfigure.get("Merge CISs").get(i).get(1).getActualCis().toString();
		}
        for (int i = 0; i < cissToConfigure.get("Split CISs").size(); i++) {
	
	        options[options.length] ="Split " + cissToConfigure.get("Split CISs").get(i).get(0).getActualCis().toString() + " into " + cissToConfigure.get("Split CISs").get(i).get(1).getActualCis().toString() + " and " + cissToConfigure.get("Split CISs").get(i).get(2).toString();
        }
	    String userResponse = null;
	    boolean responded = false;
	    userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects the follwing CISs should be configured as described. If you approve these actions for one or more of these CISs, please check them.", options), userFeedbackCallback);
	    while (userResponse == null) {
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
	   	   /** Iterator<ICis> iterator = cissToConfigure.iterator();
		    while (iterator.hasNext()) {
		        ICis potentiallyCreatableCis = iterator.next();
	            if (userResponse.equals("Yes")) {
			        finalisedCiss.add(potentiallyCreatableCis);
		            // cisManager.createCis(linkedCss, potentiallyCreatableCis.getCisId());
	            }
	            else {
	    	        recentRefusals.add(potentiallyCreatableCis);
	            }
	       }*/
	    }
	    return realCissToConfigure;
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