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
import org.societies.api.css.directory.ICssDirectory;

//import org.societies.api.internal.css.discovery.ICssDiscovery;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;

import org.societies.api.cis.management.ICisEditor;*/

import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisParticipant;
import org.societies.orchestration.api.ICisProposal;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;

import org.societies.api.internal.context.broker.ICtxBroker;

import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;

import org.societies.api.internal.useragent.feedback.IUserFeedback;

import org.societies.api.internal.useragent.model.ExpProposalContent;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxQuality;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;

import org.societies.api.css.management.ICssActivity;

import org.societies.api.identity.IIdentityManager;

import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ISuggestedCommunityAnalyser;
import org.societies.orchestration.api.IRelevantCommunityAnalyser;
import org.societies.orchestration.api.SuggestedCommunityAnalyserBean;
import org.societies.orchestration.api.SuggestedCommunityAnalyserMethodType;
import org.societies.orchestration.api.SuggestedCommunityAnalyserResultBean;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;

import org.societies.api.personalisation.mgmt.IPersonalisationManager;

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

public class RelevantCommunityAnalyser implements IRelevantCommunityAnalyser
{
	
	private IIdentity linkedCss;
	
	private org.societies.api.internal.context.broker.ICtxBroker userContextBroker;
	private org.societies.api.context.broker.ICtxBroker externalContextBroker;

	private ArrayList<ICisProposal> refusals;
	
	private IUserFeedback userFeedback;
	
	private ICisManager cisManager;
    
	private ArrayList<CtxEntity> availableContextData;
	
	private ICssDirectory userCssDirectory;
	
	private CommunityRecommender communityRecommender;
	
	private ICommManager commManager;
    private ICommCallback commCallback;
	private IIdentityManager identityManager;
	
	private IPrivacyDataManager privacyDataManager;
	
	private IPersonalisationManager personalisationManager;
	
	private IServiceDiscovery serviceDiscovery;
	private IServiceDiscoveryCallback serviceDiscoveryCallback;
	
	private IDeviceManager deviceManager;
	
	private SuggestedCommunityAnalyserBean suggestedCommunityAnalyserBean;
	private SuggestedCommunityAnalyserResultBean suggestedCommunityAnalyserResultBean;
	private SuggestedCommunityAnalyserMethodType suggestedCommunityAnalyserMethodType;
	
	private ArrayList<ArrayList<String>> refusedOrAmendedSuggestions;
    
	private HashMap<String, String> recordedMetadata;
	
	private ArrayList<String> currentActionsMetadata;
	private ArrayList<Integer> proposedActionsWithMetadata;
	
	private ArrayList<ProximityRecord> proximityHistory;	
	/*
     * Constructor for SuggestedCommunityAnalyser
     * 
	 * Description: The constructor creates the SuggestedCommunityAnalyser
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public RelevantCommunityAnalyser(IIdentity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		//else
		//	this.linkedDomain = linkedEntity;
		
		proximityHistory = new ArrayList<ProximityRecord>();
		recordedMetadata = new HashMap<String, String>();
		refusals = new ArrayList<ICisProposal>();
		
		try {
			List<CtxIdentifier> ctxMetadata = userContextBroker.lookup(CtxModelType.ATTRIBUTE, "hasCLM").get();
			
			for (int i = 0; i < ctxMetadata.size(); i++) {
				CtxAttribute thisMetadata = (CtxAttribute) userContextBroker.retrieve(ctxMetadata.get(i)).get();
				String thisMetadataValue = thisMetadata.getStringValue();
				recordedMetadata.put(thisMetadataValue.split("---")[0].split("CIS ID: ")[1], thisMetadataValue.split("---")[1]); 
			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} catch (ExecutionException e) {
			
			e.printStackTrace();
		} catch (CtxException e) {
			
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
			
		//new ProximityRecordingThread().start();
	}
	
	
	
    public void initialiseRelevantCommunityAnalyser() {
    	//getCommManager().register(this);
    	identityManager = commManager.getIdManager();
    	
    	new RelevantCommunityAnalyser(linkedCss, "CSS");
    }
        
    @Override
    public ArrayList<IIdentity> processRelevanceRecommendations() {
    	currentActionsMetadata = new ArrayList<String>();
    	proposedActionsWithMetadata = new ArrayList<Integer>(); 
    	
    	String returnStatement = "";
    	
    	HashMap<String, ArrayList<ArrayList<ICisProposal>>> convertedRecommendations = new HashMap<String, ArrayList<ArrayList<ICisProposal>>>();
		//ICis proposedCis = cisManager.getBlankCisRecord();
    	ICisProposal proposedCis = null;
    	proposedCis = new ICisProposal();
    	
    	//if (cssList == null)
    	//	return null;
    	//else if (!(cssList.size() > 0))
    	//	return null;
    	
    	//Iterator<IIdentity> cssListIterator = currentActionsMetadata.iterator();//cssList.iterator();
    	
    	//while (cssListIterator.hasNext()) {
    	//	try {
		//		proposedCis.addMember(cssListIterator.next().getJid(), "participant");
		//	} catch (CommunicationException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
    	//}
    	
    	
	    
	    return null;
	    
    }
    
    public HashMap<String, ArrayList<ArrayList<ICisProposal>>> advancedCisCreationAnalysis(ArrayList<ICisProposal> proposedCiss, ArrayList<ArrayList<ICisProposal>> proposedConfigurations, ArrayList<ICisProposal> proposedDeletions) {
    	HashMap<String, ArrayList<ArrayList<ICisProposal>>> finalisedCiss = new HashMap<String, ArrayList<ArrayList<ICisProposal>>>();
    	
    	return finalisedCiss;
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
    
    public void setExternalContextBroker(org.societies.api.context.broker.ICtxBroker externalContextBroker) {
    	System.out.println("GOT user context broker" + userContextBroker);
    	this.externalContextBroker = externalContextBroker;
    }
    
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
    
    public void setIdentityManager(IIdentityManager identityManager) {
		this.identityManager = identityManager;
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
		
        case processCSMRecommendations:
		try {
			//IIdentity owner = identityManager.fromJid(scaBean..getIdentity());
			//String serviceType = scaBean.getServiceType();
			processRelevanceRecommendations();
			break;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
      
        
    	}
    	
    	return null;
    }
    
}

