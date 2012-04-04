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

import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBroker;
//import org.societies.api.internal.context.broker.ICommunityCtxBroker;
//import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.identity.IIdentity;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

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
	
	private ArrayList<ICisRecord> recentRefusals;

	private IUserFeedbackCallback userFeedbackCallback;
	
	private ArrayList<ICisRecord> cissToCreate;
	private HashMap<String, ArrayList<ArrayList<ICisRecord>>> cissToConfigure;
	private ArrayList<ICisRecord> cissToDelete;
	
	private ICommManager commManager;
	
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
	
	public ArrayList<String> identifyCisActionForEgocentricCommunityAnalyser(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisPossibilities, ArrayList<String> cissToCreateMetadata) {
		ArrayList<String> cisAddMetadata = new ArrayList<String>();
		ArrayList<String> cisDeleteMetadata = new ArrayList<String>();
		ArrayList<String> cisConfigureMetadata = new ArrayList<String>();
		if (cisPossibilities.get("Create CISs") != null)
		    cisAddMetadata = identifyCissToCreate(cisPossibilities.get("Create CISs").get(0), cissToCreateMetadata);
		if (cisPossibilities.get("Delete CISs") != null)
		    cisDeleteMetadata = identifyCissToDelete(cisPossibilities.get("Delete CISs").get(0), cissToCreateMetadata);
		HashMap<String, ArrayList<ArrayList<ICisRecord>>> temp = new HashMap<String, ArrayList<ArrayList<ICisRecord>>>();
		temp.put("Configure CISs", cisPossibilities.get("Configure CISs"));
		temp.put("Merge CISs", cisPossibilities.get("Merge CISs"));
		temp.put("Split CISs", cisPossibilities.get("Split CISs"));
		if (temp.size() > 0)
		    cisConfigureMetadata = identifyCissToConfigure(temp, cissToCreateMetadata);
		
		ArrayList<String> cisMetadata = new ArrayList<String>();
		for (int i = 0; i < cisAddMetadata.size(); i++)
			cisMetadata.add(cisAddMetadata.get(i));
		for (int i = 0; i < cisDeleteMetadata.size(); i++)
			cisMetadata.add(cisDeleteMetadata.get(i));
		for (int i = 0; i < cisConfigureMetadata.size(); i++)
			cisMetadata.add(cisConfigureMetadata.get(i));
		
		return cisMetadata;
	}
	
	public void identifyCisActionForCSCW(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisPossibilities) {
		
	}
	
    public void identifyCisActionForCSMAnalyser(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisPossibilities) {
		
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
	
	public ArrayList<String> identifyCissToCreate(ArrayList<ICisRecord> creatableCiss, ArrayList<String> cissToCreateMetadata) {		
		//Can't use GUI in tests
		//cissToCreate = getUserFeedbackOnCreation(cissToCreate);
		
		cissToCreate = creatableCiss;
		for (int i = 0; i < cissToCreateMetadata.size(); i++) {
			if (!(cissToCreateMetadata.get(i).split("---")[0].substring(0, 3).equals("CIS")))
				cissToCreateMetadata.set(i, cissToCreateMetadata.get(i).concat(cissToCreate.get(i).toString()));
		}
		return new ArrayList<String>();
		//if (cissToCreate != null) 
		  //  for (int i = 0; i < cissToCreate.size(); i++)
			//    cisManager.createCis(linkedCss.getIdentifier(), cissToCreate.get(i).getCisId());
	}
	
	public ArrayList<ICisRecord> getUserFeedbackOnCreation(ArrayList<ICisRecord> cissToCreate) {
		ArrayList<ICisRecord> finalisedCiss = null;
		String[] options = new String[cissToCreate.size()];
		options[0] = "options";
		for (int i = 0; i < cissToCreate.size(); i++) {
			options[i] = cissToCreate.get(i).toString();
		}
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
		   	Iterator<ICisRecord> iterator = cissToCreate.iterator();
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
	
	/*
	 * Description: The method looks for CISs to delete, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public ArrayList<String> identifyCissToDelete(ArrayList<ICisRecord> cisPossibilities, ArrayList<String> cissToCreateMetadata) {	
		//Can't use GUI in tests
        //cissToDelete = getUserFeedbackOnDeletion(cisPossibilities);
		
		cissToDelete = cisPossibilities;
		
		//for (int i = 0; i < cissToDelete.size(); i++) {
			//cisManager.deleteCis(linkedCss.getIdentifier(), cissToDelete.get(i).getCisId());
	    //}
		//for (int i = 0; i < cisPossibilities.size(); i++) {
		//    if (!cissToDelete.contains(cisPossibilities.get(i)))
		//        if (cissToCreateMetadata.get(i).contains("short-term temporary"))
		//            cissToCreateMetadata.set(i, cissToCreateMetadata.get(i).replace("short-term temporary", "medium-term temporary");
		//        if (cissToCreateMetadata.get(i).contains("medium-term temporary"))
		//            cissToCreateMetadata.set(i, cissToCreateMetadata.get(i).replace("medium-term temporary", "long-term temporary");
		//        if (cissToCreateMetadata.get(i).contains("long-term temporary"))
		//            cissToCreateMetadata.set(i, cissToCreateMetadata.get(i).replace("long-term temporary", "ongoing");
		//}
		return new ArrayList<String>();
	}
	
	public ArrayList<ICisRecord> getUserFeedbackOnDeletion(ArrayList<ICisRecord> cissToDelete) {
		ArrayList<ICisRecord> realCissToDelete = new ArrayList<ICisRecord>();
		List<String> options = new ArrayList<String>();
		for (int i = 0; i < cissToDelete.size(); i++) {
			options.add(cissToDelete.get(i).toString());
		}
		//options.add("options");
		userResponse = null;
		boolean responded = false;
		//userFeedback.getExplicitFB(arg0, arg1, arg2);
		//userFeedback.getExplicitFB(0,  new ExpProposalContent("SOCIETIES suspects these CISs may be obsolete. If you would like to delete one or more of these CISs, please check them.", options.toArray(new String[options.size()]), userFeedbackCallback);
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
		
		if (userResponse == null) {
		    //User obviously isn't paying attention to CSS, so put the message in the background/list of messages for them to see at their leisure.
		    String background = "This message is in your inbox or something, waiting for you to read it";
		}
		else {
		   	Iterator<ICisRecord> iterator = cissToDelete.iterator();
			while (iterator.hasNext()) {
			    ICisRecord potentiallyDeletableCis = iterator.next();
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
	
	public ArrayList<String> identifyCissToConfigure(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisPossibilities, ArrayList<String> cissToCreateMetadata) {
	
	    cissToConfigure = new HashMap <String, ArrayList<ArrayList<ICisRecord>>> ();
	
	    
	    //can't use GUI in tests
	    //finalConfiguredCiss = getUserFeedbackOnConfiguration(cissToConfigure);
	
	    //finalConfiguredCiss = cissToConfigure;
	
	    //Iterator<ArrayList<ICisRecord>> iterator = cissToConfigure.iterator();
	
	    for (int i = 0; i < cissToConfigure.size(); i++) {
	        ArrayList<ICisRecord> configurableCis = cissToConfigure.get("Configure CISs").get(i);
            //IIdentity cisID = configurableCis.get(i).get(0).getCisId();
		    //if (cisManager.get(cisID).getMembersList() != configurableCis.get(i).get(1).getMembersList())
	        //    cisManager.setMembersList(cisID, configurableCis.get(i).get(1).getMembersList();
	        //if (cisManager.get(cisID).getMembershipCriteria() != configurableCis.get(i).get(1).getMembershipCriteria())
	        //    cisManager.setMembershipCriteria(cisID, configurableCis.get(i).get(1).getMembershipCriteria();
	        //if (cisManager.get(cisID).getOwner() != configurableCis.get(i).get(1).getOwner())
	        //    cisManager.setOwner(cisID, configurableCis.get(i).get(1).getOwner();
	        //if (cisManager.get(cisID).getAdministrators() != configurableCis.get(i).get(1).getAdministrators())
	        //    cisManager.setAdministrators(cisID, configurableCis.get(i).get(1).getAdministrators();
	        //if "remove members"
        	//    attempt to remove members - perhaps SOCIETIES platform itself should have mechanism
        	//    where if a user deletion from CIS attempt is made, 
        	//    that user will be informed by the system and given a chance to respond?
        	//    The admin/owner could have an override option in case e.g. offensive person is being deleted.
        	//if "merge with other CIS"
        	//
	        configurableCis = cissToConfigure.get("Merge CISs").get(i);
	        //
	        //IIdentity[] membersList = configurableCis.get(i).get(0).getMembersList();
	        //IIdentity[] membersList2 = configurableCis.get(i).get(1).getMembersList()
	        //IIdentity[] mergedMembersList = new IIdentity[membersList.size() + membersList2.size()];
	        //for (int m = 0; m < mergedMembersList.length; m++)
	        //    
	        //    if (m < membersList.length) {
	        //        if (!mergedMembersList.contains(membersList[m]))
	        //            mergedMembersList[m] = membersList[m];
	        //    }
	        //    else if (!mergedMembersList.contains(membersList2[m-membersList.length])) (mergedMembersList[m] = membersList2[m-membersList.length];
	        
	        //IIdentity[] administrators = configurableCis.get(i).get(0).getAdministrators();
	        //IIdentity[] administrators2 = configurableCis.get(i).get(1).getAdministrators()
	        //IIdentity[] mergedAdministrators = new IIdentity[administrators.size() + administrators2.size()];
	        //for (int m = 0; m < mergedAdministrators.length; m++)
	        //    
	        //    if (m < administrators.length) {
	        //        if (!mergedadministrators.contains(administrators[m]))
	        //            mergedAdministrators[m] = administrators[m];
	        //    }
	        //    else if (!mergedAdministrators.contains(administrators2[m-administrators.length])) (mergedAdministrators[m] = administrators2[m-administrators.length];
	        
	        //IIdentity[] owner = configurableCis.get(i).get(0).getMembersList();
            //if (configurableCis.get(i).get(0).getOwner() != configurableCis.get(i).get(1).getOwner())
	        //    owner = configurableCis.get(i).get(1);
	        
	        //for (int m = 0; m < mergedMembersList.length; m++)
	        //    
	        //    if (m < membersList.length) {
	        //        if (!mergedMembersList.contains(membersList[m]))
	        //            mergedMembersList[m] = membersList[m];
	        //    }
	        //    else if (!mergedMembersList.contains(membersList2[m-membersList.length])) (mergedMembersList[m] = membersList2[m-membersList.length];
	        
	        
	        //cisManager.createCis(configurableCis.get(i).get(0).getName() + " MERGED WITH " + configurableCis.get(i).get(1).getName(),
	        //                     mergedMembersList, null, null);
        	//if "split into distinct CISs"
        	//
	        configurableCis = cissToConfigure.get("Split CISs").get(i);
	        //cisManager.createCis(configurableCis.get(i).get(1));
	        //cisManager.createCis(configurableCis.get(i).get(2));
        	//if "switch sub-CIS and CIS"
        	//
        	//
	        // cisManager.configureCis(linkedCss, potentiallyConfigurableCis.getCisId());
        }
	    return new ArrayList<String>();
    }

    public HashMap<String, ArrayList<ArrayList<ICisRecord>>> getUserFeedbackOnConfiguration(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cissToConfigure) {
	    HashMap<String, ArrayList<ArrayList<ICisRecord>>> realCissToConfigure = new HashMap<String, ArrayList<ArrayList<ICisRecord>>> ();
	    String[] options = new String[cissToConfigure.size()];
	    options[0] = "options";
	    for (int i = 0; i < cissToConfigure.get("Configure CISs").size(); i++) {
			
	    	options[i] = cissToConfigure.get("Configure CISs").get(i).get(1).toString();
		}
        for (int i = 0; i < cissToConfigure.get("Merge CISs").size(); i++) {
			
	    	options[options.length] = "Merge " + cissToConfigure.get("Merge CISs").get(i).get(0).toString() + "and " + cissToConfigure.get("Merge CISs").get(i).get(1).toString();
		}
        for (int i = 0; i < cissToConfigure.get("Split CISs").size(); i++) {
	
	        options[options.length] ="Split " + cissToConfigure.get("Split CISs").get(i).get(0).toString() + " into " + cissToConfigure.get("Split CISs").get(i).get(1).toString() + " and " + cissToConfigure.get("Split CISs").get(i).get(2).toString();
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
	   	   /** Iterator<ICisRecord> iterator = cissToConfigure.iterator();
		    while (iterator.hasNext()) {
		        ICisRecord potentiallyCreatableCis = iterator.next();
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