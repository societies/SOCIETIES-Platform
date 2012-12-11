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
package org.societies.privacytrust.privacyprotection.privacynegotiation;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.preference.IUserPreferenceManagement;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.remote.INegotiationAgentRemote;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.identity.IIdentitySelection;
import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.client.NegotiationClient;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider.PolicyRetriever;


public class PrivacyPolicyNegotiationManager extends EventListener implements IPrivacyPolicyNegotiationManager {
	
	//private NegotiationClient negClient;
	//private PrivacyPolicyRegistryManager privacyPolicyRegMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IIdentity myPublicDPI;
	private PolicyRetriever servicePolicyRetriever;
	private Hashtable<Requestor, NegotiationClient> negClients ;
	private LocalServiceStartedListener localServiceStartedListener; 
	private IUserFeedback userFeedback;
	private IUserPreferenceManagement prefMgr;

	private ICtxBroker ctxBroker;
	
	private IEventMgr eventMgr;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	//ICommManager!
	private IIdentityManager idm;
	
	private IPrivacyAgreementManagerInternal privacyAgreementManagerInternal;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	private INegotiationAgentRemote negotiationAgentRemote;
	
	private IIdentitySelection identitySelection;
	
	private ICommManager commsMgr;
	
	
	private IPrivacyPolicyManager privacyPolicyManager;
	/**
	 * @return the prefMgr
	 */
	public IUserPreferenceManagement getPrefMgr() {
		return prefMgr;
	}
	/**
	 * @param prefMgr the prefMgr to set
	 */
	public void setPrefMgr(IUserPreferenceManagement prefMgr) {
		this.prefMgr = prefMgr;
	}
	
	

/*	public PrivacyPolicyRegistryManager getPrivacyPolicyRegMgr(){
		return this.privacyPolicyRegMgr;
	}
	

	*//**
	 * @param privacyPolicyRegMgr the privacyPolicyRegMgr to set
	 *//*
	public void setPrivacyPolicyRegMgr(PrivacyPolicyRegistryManager privacyPolicyRegMgr) {
		this.privacyPolicyRegMgr = privacyPolicyRegMgr;
	}
	*/
	
	public IPrivacyPreferenceManager getPrivacyPreferenceManager() {
		// TODO Auto-generated method stub
		return privacyPreferenceManager;
	}
    /**
	 * @param privacyPreferenceManager the privacyPreferenceManager to set
	 */
	public void setPrivacyPreferenceManager(IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
	}
	/**
	 * @return the idm
	 */
	public IIdentityManager getIdm() {
		return idm;
	}
	/**
	 * @param idm the idm to set
	 */
	public void setIdm(IIdentityManager idm) {
		this.idm = idm;
	}
	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}
	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
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


	/**
	 * @return the privacyDataManagerInternal
	 */
	public IPrivacyDataManagerInternal getPrivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}
	/**
	 * @param privacyDataManagerInternal the privacyDataManagerInternal to set
	 */
	public void setPrivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}


	/**
	 * @return the negotiationAgentRemote
	 */
	public INegotiationAgentRemote getNegotiationAgentRemote() {
		return negotiationAgentRemote;
	}
	/**
	 * @param negotiationAgentRemote the negotiationAgentRemote to set
	 */
	public void setNegotiationAgentRemote(INegotiationAgentRemote negotiationAgentRemote) {
		this.negotiationAgentRemote = negotiationAgentRemote;
		
		this.logging.debug("Set Remote Negotiation Agent : "+this.negotiationAgentRemote.getClass().getName());
	}
	/**
	 * @return the identitySelection
	 */
	public IIdentitySelection getIdentitySelection() {
		return identitySelection;
	}
	/**
	 * @param identitySelection the identitySelection to set
	 */
	public void setIdentitySelection(IIdentitySelection identitySelection) {
		this.identitySelection = identitySelection;
	}
	public PrivacyPolicyNegotiationManager(){	
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
	}

    public void initialisePrivacyPolicyNegotiationManager(){
		/*
		this.myPublicDPI = this.IDM.getPublicDigitalPersonalIdentifier();
		String localServiceID = (String) cc.getProperties().get(PssConstants._FW_SERVICE_ID);
		this.policyMgrServiceID = new PssServiceIdentifier(localServiceID, this.myPublicDPI);
		*/
		//Register Negotiation Agent and Client with OSGi so that the ONM-SM can find them
		
		//this.privacyPolicyRegMgr = new PrivacyPolicyRegistryManager(this.getCtxBroker());
		//this.privacyPolicyRegMgr.setPublicDPI(this.myPublicDPI);
		
		
		this.servicePolicyRetriever = new PolicyRetriever(this, this.getEventMgr());
		this.negClients = new Hashtable<Requestor, NegotiationClient>();
		/*
		 * TODO: this has to start automatically
		 * this.negAgent = new NegotiationAgent(this.myContext, this.myPublicDPI, this.getPrivacyPolicyRegMgr(), this.adMgr);
		 * this.myContext.registerService(INegotiationAgent.class.getName(), this.negAgent, new Hashtable<String,Object>());
		 */
		//this.localServiceStartedListener = new LocalServiceStartedListener(getEventMgr(), getIdm(), this.getPrivacyPolicyRegMgr()); 
		
		//JOptionPane.showMessageDiathis.logging.debug(null, "PolicyManager initialised");
		this.registerForFailedNegotiationEvent();
		this.logging.debug("Started PrivacyPolicyNegotiationManager");
    }
  
	
	
/*	public PrivacyOutcomeConstants checkPermission(Permission permission, IIdentity requestorDPI) {
		if (permission instanceof CtxPermission){
			CtxPermission ctxPermission = (CtxPermission) permission;
			return this.checkPermission(ctxPermission, requestorDPI);
		}
		return PrivacyOutcomeConstants.BLOCK;
	}

	public PrivacyOutcomeConstants checkPermission(CtxPermission permission, IIdentity requestorDPI) {
		try {
			Action action = this.createActionObject(permission);
			return this.privPrefMgr.checkPermission(permission.getResource(),action,requestorDPI);
		} catch (PrivacyPreferenceException e) {
			e.printStackTrace();
			return PrivacyOutcomeConstants.BLOCK;
		}
		
		
	}

	private Action createActionObject(CtxPermission permission) throws PrivacyPreferenceException{
		String strAction = permission.getActions().toUpperCase();
		try{
			return new Action(ActionConstants.valueOf(strAction));
		}catch (IllegalArgumentException e){
			this.logging.debug("Action: "+strAction+"  is not recognised as valid ActionConstants value");
		}
	
		throw new PrivacyPreferenceException("Permission action "+permission.getActions()+"not recognisable");
	}

	
	public IIdentity selectExactIdentity(List<IIdentity> dpis, IAgreement agreement) {
		return this.privPrefMgr.evaluateIDSPreferences(agreement, dpis);
	}*/

	


	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyNegotiationManager#negotiateCISPolicy(org.societies.api.identity.RequestorCis)
	 */
	@Override
	public void negotiateCISPolicy(RequestorCis requestor){
		
		if (this.negClients.containsKey(requestor)){
			this.logging.debug("Another negotiation has been requested while a previous one is ongoing with the same requestor");
			if (!askUserNegotiationStarted(requestor)){
				this.logging.debug("User has aborted the new negotiation request.");
				return;
			}
			
			/*int n = JOptionPane.showConfirmDialog(null, "A Privacy Policy Negotiation process has already started with CIS : \n"
				    +requestor.toString()+". Do you want to abort current negotiation and start a new one?", "Privacy Policy Negotiation Manager message", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.NO_OPTION){	
				return;
			}*/
			
			this.negClients.remove(requestor);
			this.logging.debug("User has aborted the previous negotiation request");
		}
		this.logging.debug("Starting new negotiation with cis: "+requestor.toString());
		NegotiationClient negClient = new NegotiationClient(this.negotiationAgentRemote, this);
		negClient.startNegotiation(requestor);
		this.negClients.put(requestor, negClient);
	}
	
	/**
	 * 
	 * @param requestor
	 * @return true to abort previous negotiation
	 */
	private boolean askUserNegotiationStarted(Requestor requestor){
		String abort = "Abort previous";
		String ignore = "Ignore new request";
		ExpProposalContent content;
		if (requestor instanceof RequestorCis){
			content = new ExpProposalContent("A Privacy Policy Negotiation process has already started with CIS : \n"
				    +requestor.toString()+". A new Privacy Policy Negotiation process was requested to be performed. Do you want to abort the previous negotiation and start a new one?", new String[]{abort, ignore});
		}else{
			content = new ExpProposalContent("A Privacy Policy Negotiation process has already started with service : \n"
				    +requestor.toString()+". A new Privacy Policy Negotiation process was requested to be performed. Do you want to abort the previous negotiation and start a new one?", new String[]{abort, ignore});
		}
		
		try {
			List<String> response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, content).get();
			for (String str : response){
				if (str.equalsIgnoreCase(ignore)){
					return false;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyNegotiationManager#negotiateServicePolicy(org.societies.api.identity.RequestorService)
	 */
	@Override
	public void negotiateServicePolicy(RequestorService requestor){
		if (this.negClients.containsKey(requestor)){
			this.logging.debug("Another negotiation has been requested while a previous one is ongoing with the same requestor");
			if (!askUserNegotiationStarted(requestor)){
				this.logging.debug("User has aborted the new negotiation request.");
				return;
			}
/*			int n = JOptionPane.showConfirmDialog(null, "A Privacy Policy Negotiation process has already started with service: \n"
				    +requestor.toString()+". Do you want to abort current negotiation and start a new one?", "Privacy Policy Negotiation Manager message", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.NO_OPTION){	
				return;
			}*/
			this.logging.debug("User has aborted the previous negotiation request");
			this.negClients.remove(requestor);
		}
		this.logging.debug("Starting new negotiation with service: "+requestor.toString());
		NegotiationClient negClient = new NegotiationClient(this.getNegotiationAgentRemote(), this);
		negClient.startNegotiation(requestor);
		this.negClients.put(requestor, negClient);		
	}
	
/*	public void addPrivacyPolicyForService(Requestor serviceID,
			RequestPolicy policy) {
		if (serviceID !=null){
			try {
				Subject sub = new Subject(this.getIdm().parseDigitalPersonalIdentifier(serviceID.getOperatorId()), serviceID);
				policy.setRequestor(sub);
				this.getPrivacyPolicyRegMgr().addPolicy(serviceID, policy);
				
			} catch (MalformedDigitialPersonalIdentifierException e) {
				this.logging.debug("Could not parse DPI from Requestor. Privacy Policy not added for serviceID "+serviceID.toUriString());
				e.printStackTrace();
			}
		}else{
			this.logging.debug("Attempted to add a new service privacy policy with a null service ID. Service Privacy Policy NOT added");
		}
		
	}
	
	
	public void addPrivacyPolicyForService(Requestor serviceID, File xmlFile) {
		XMLPolicyReader reader = new XMLPolicyReader(this.myContext);
		RequestPolicy policy = reader.readPolicyFromFile(xmlFile);
		this.getPrivacyPolicyRegMgr().addPolicy(serviceID, policy);
		
	}
	
	
	
	public void setFinalIdentity(IIdentity serviceDPI,
			IIdentity userDPI, Requestor serviceID) {
		this.privPrefMgr.addIDSDecision(userDPI, serviceDPI, serviceID);
		if (userDPI==null){
			//abort
			this.negClients.remove(serviceID);
			return;
		}
		//JOptionPane.showMessageDialog(null, "SET FINAL IDENTITY :"+userDPI.toUriString()+" for serviceID: "+serviceID.toUriString());
		if (this.negClients.containsKey(serviceID)){
			NegotiationClient client = this.negClients.get(serviceID);
			this.negClients.remove(serviceID);
			client.setFinalIdentity(serviceDPI, userDPI, serviceID);
			
			//JOptionPane.showMessageDialog(null, "Negotiation process complete. Removing negClient");
		}else{
			JOptionPane.showMessageDialog(null, "NegClients doesn't contain serviceID: "+serviceID.toUriString());
		}
		
		
	}*/
	

	@Override
	public void handleInternalEvent(InternalEvent event) {
		this.logging.debug("Received an event: "+event.geteventType());
		if (event.geteventType().equals(EventTypes.FAILED_NEGOTIATION_EVENT)){
			FailedNegotiationEvent negEvent = (FailedNegotiationEvent) event.geteventInfo();
			Requestor id = negEvent.getRequestor();
			this.logging.debug("Received Failed Negotiation event for : "+negEvent.getRequestor().toString());
			if(this.negClients.containsKey(id)){
				//INegotiationClient client = this.negClients.get(id);
				this.negClients.remove(id);
				this.logging.debug("Destroying NegotiationClient instance");
				//JOptionPane.showMessageDialog(null, "Negotiation with: "+id.toUriString()+" failed.");
			}
		}else if (event.geteventType().equals(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT)){
			PPNegotiationEvent ppnEvent = (PPNegotiationEvent) event.geteventInfo();
			
			Requestor requestor = ppnEvent.getAgreement().getRequestor();
			this.logging.debug("Received successfull Negotiation event for : "+requestor.toString());
			if (this.negClients.containsKey(requestor)){
				this.negClients.remove(requestor);
				this.logging.debug("Destroying NegotiationClient instance");
			}
			
		}
		
		this.logging.debug("Finished executing handleInternalEvent");
	}
	

	private void registerForFailedNegotiationEvent(){
		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.FAILED_NEGOTIATION_EVENT, EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, null);
		
		this.logging.debug("Registered for events: "+EventTypes.FAILED_NEGOTIATION_EVENT);

	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}
	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		this.idm = commsMgr.getIdManager();
	}
	/**
	 * @return the privacyAgreementManagerInternal
	 */
	public IPrivacyAgreementManagerInternal getPrivacyAgreementManagerInternal() {
		return privacyAgreementManagerInternal;
	}
	/**
	 * @param privacyAgreementManagerInternal the privacyAgreementManagerInternal to set
	 */
	public void setPrivacyAgreementManagerInternal(
			IPrivacyAgreementManagerInternal privacyAgreementManagerInternal) {
		this.privacyAgreementManagerInternal = privacyAgreementManagerInternal;
	}
	/**
	 * @return the privacyPolicyManager
	 */
	public IPrivacyPolicyManager getPrivacyPolicyManager() {
		return privacyPolicyManager;
	}
	/**
	 * @param privacyPolicyManager the privacyPolicyManager to set
	 */
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}
	/**
	 * @return the userFeedback
	 */
	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}
	/**
	 * @param userFeedback the userFeedback to set
	 */
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public void bindNegotiationAgentRemote(INegotiationAgentRemote negAgent, Dictionary<Object,Object> props){
		this.logging.debug("NegotiationComms bound");
		this.negotiationAgentRemote = negAgent;
	}
	
	
	
}
	