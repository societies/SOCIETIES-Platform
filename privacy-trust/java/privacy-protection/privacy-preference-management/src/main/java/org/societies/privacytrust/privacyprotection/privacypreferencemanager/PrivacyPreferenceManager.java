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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.PrivacyPreferenceConditionMonitor;

/**
 * @author Elizabeth
 *
 */

public class PrivacyPreferenceManager implements IPrivacyPreferenceManager{
	
	private PrivatePreferenceCache prefCache;
	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker ctxBroker;

	private ITrustBroker trustBroker;
	
	private PrivacyPreferenceConditionMonitor privacyPCM;
	
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	
	private IIdentityManager idm;
	
	private ICommManager commsManager;
	
	private boolean test = false;
	
	private MessageBox myMessageBox;
	
	public PrivacyPreferenceManager(){
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
	}
	

	public void initialisePrivacyPreferenceManager(ICtxBroker ctxBroker, ITrustBroker trustBroker){
		this.setCtxBroker(ctxBroker);
		this.trustBroker = trustBroker;
		this.privacyPCM = new PrivacyPreferenceConditionMonitor(ctxBroker, this, getprivacyDataManagerInternal(), idm);
		prefCache = new PrivatePreferenceCache(ctxBroker);
		contextCache = new PrivateContextCache(ctxBroker);
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
	}
	
	public void initialisePrivacyPreferenceManager(){
		prefCache = new PrivatePreferenceCache(ctxBroker);
		contextCache = new PrivateContextCache(ctxBroker);
		if (this.myMessageBox==null){
			myMessageBox = new MessageBox();
		}
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
	 * @return the trustBroker
	 */
	public ITrustBroker getTrustBroker() {
		return trustBroker;
	}
	/**
	 * @param trustBroker the trustBroker to set
	 */
	public void setTrustBroker(ITrustBroker trustBroker) {
		this.trustBroker = trustBroker;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, CtxAttributeIdentifier ctxId, List<Action> actions) throws PrivacyException{
		if (null==ctxId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : null");
			return null;
			
		}
		String actionList = "";
		for (Action a : actions){
			actionList = actionList.concat(a.toString());
		}
		List<Condition> conditions = new ArrayList<Condition>();
		PPNPreferenceDetails details = new PPNPreferenceDetails(ctxId.getType());
		details.setAffectedCtxID(ctxId);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxId, conditions, actions);
		}

		details = new PPNPreferenceDetails(ctxId.getType());
		details.setRequestor(requestor);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxId, conditions, actions);
		}		

		details = new PPNPreferenceDetails(ctxId.getType());
		details.setAffectedCtxID(ctxId);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxId, conditions, actions);
		}

		details = new PPNPreferenceDetails(ctxId.getType());
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxId, conditions, actions);
		}
		
		 int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
				+ "resource:"+ctxId.getType()+"\n("+ctxId.toUriString()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
		
		if (n==JOptionPane.YES_OPTION){
			this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.ALLOW);
			return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.PERMIT);
		}else{
			this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
			return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.DENY);
		}

	}

	/*
	 * SOCIETIES new method 
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(java.lang.String, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action, org.societies.api.comm.xmpp.datatypes.IIdentity)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, String ctxType, List<Action> actions) throws PrivacyException{

		PPNPreferenceDetails details = new PPNPreferenceDetails(ctxType);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = prefCache.getPPNPreference(details);
		List<Condition> conditions = new ArrayList<Condition>();
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxType, conditions, actions);
		}
		
		details = new PPNPreferenceDetails(ctxType);
		model = prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestor, ctxType, conditions, actions);
		}
		String actionList = "";
		for (Action a : actions){
			actionList = actionList.concat(a.toString());
		}
		try {
			Future<List<CtxIdentifier>> futureCtxIds = this.getCtxBroker().lookup(CtxModelType.ATTRIBUTE, ctxType);

			List<CtxIdentifier> ctxIds = futureCtxIds.get();

			CtxAttributeIdentifier ctxId;
			if (ctxIds.size()==0){
				this.logging.debug("CtxType: "+ctxType+" not found. Returning BLOCK decision");
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);
			}else if (ctxIds.size()==1){
				ctxId = (CtxAttributeIdentifier) ctxIds.get(0);
			}else{
				this.logging.debug("Asking the user: "+ctxType);
				
				ctxId = (CtxAttributeIdentifier) myMessageBox.showInputDialog(requestor.toString()+" is requesting access to: \n"
						+ "resource:"+ctxType+"\n(to perform a "+actionList+" operation.\nSelect an attribute to provide access to or click cancel to abort.", 
						"Access request", JOptionPane.PLAIN_MESSAGE, ctxIds.toArray(), ctxIds.get(0));
				if (ctxId == null){
					this.logging.debug("User aborted. Returning block");
					this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
					return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);
				}
			}
				this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.PERMIT);
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIDSPreferences(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement, java.util.List)
	 */
	@Override
	public IIdentity evaluateIDSPreferences(IAgreement agreement, List<IIdentity> dpis){

		IIdentity selectedIIdentity = this.evaluateIDSPreferenceBasedOnAllInfo(agreement, dpis);

		if (selectedIIdentity==null){
			selectedIIdentity = this.evaluateIDSPreferenceBasedOnProviderDPI(agreement, dpis);
			if (selectedIIdentity == null){
				selectedIIdentity = this.evaluateIDSPreferenceIrrespectiveOfRequestor(agreement, dpis);
				if (selectedIIdentity == null){
					return null;
				}else if (selectedIIdentity instanceof InvalidIdentity){
					//user wants to use a new DPI
					return null;
				}else{
					return selectedIIdentity;
				}
			}else if (selectedIIdentity instanceof InvalidIdentity){
				//user wants to use a new DPI
				return null;
			}else{
				return selectedIIdentity;
			}
		}else if (selectedIIdentity instanceof InvalidIdentity){
			//user wants to use a new DPI
			return null;
		}else{
			return selectedIIdentity;
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluatePPNP(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public ResponsePolicy evaluatePPNP(RequestPolicy request){
		//TODO
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluatePPNPreference(java.lang.String)
	 */
	@Override
	public List<IPrivacyOutcome> evaluatePPNPreference(String contextType) {
		this.logging.debug("Request to evaluate Preferences referring to contextType: "+contextType);
		List<IPrivacyOutcome> outcomes = new ArrayList<IPrivacyOutcome>();
		List<IPrivacyPreferenceTreeModel> models = this.prefCache.getPPNPreferences(contextType);
		this.logging.debug("Found "+models.size()+" preferences referring contextType: "+contextType);
		for (IPrivacyPreferenceTreeModel model : models){
			IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
			if (outcome!=null){
				outcomes.add(outcome);
			}
		}
		this.logging.debug("Number of applicable preferences: "+outcomes.size());
		return outcomes;
	}
	
	public IPrivacyOutcome evaluatePPNPreference(PPNPreferenceDetails detail){
		IPrivacyPreferenceTreeModel model = this.prefCache.getPPNPreference(detail);
		if (model==null){
			JOptionPane.showMessageDialog(null, "no stored ppnp preference with these details");
		}
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		
		return outcome;
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIdSPreference(org.societies.api.identity.Requestor)
	 */
	public IIdentity evaluateIdSPreference(Requestor requestor){
		List<IDSPreferenceDetails> details = this.prefCache.getIDSPreferenceDetails();
		List<IIdentity> identities = new ArrayList<IIdentity>();
		for (IDSPreferenceDetails detail : details){
			if (detail.getRequestor().equals(requestor)){
				identities.add(this.evaluateIDSPreference(detail));
			}
		}
		
		if (identities.size()==0){
			return null;
		}
		if (identities.size()==1){
			return identities.get(0);
		}
		
		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : identities){

			strCandidateIDs.add(userId.toString());
		}
		
		String createNew = "Create new Identity";
		strCandidateIDs.add(createNew);
		String s = "";
		if (requestor instanceof RequestorService){
			s = this.askUserToSelectIdentityForStartingService((RequestorService) requestor, strCandidateIDs);
		}else if (requestor instanceof RequestorCis){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCis) requestor, strCandidateIDs);
		}else{
			s = this.askUserToSelectIdentityForInteractingWithCSS(requestor, strCandidateIDs);
		}
		
		for (IIdentity id : identities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails)
	 */
	@Override
	public IIdentity evaluateIDSPreference(IDSPreferenceDetails details) {
		IPrivacyPreferenceTreeModel model = this.prefCache.getIDSPreference(details);
		IPrivacyOutcome out = this.evaluatePreference(model.getRootPreference());
		if (out instanceof IdentitySelectionPreferenceOutcome){
			return ((IdentitySelectionPreferenceOutcome) out).getIdentity();
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateDObfPreference(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String)
	 */
	@Override
	public DObfOutcome evaluateDObfPreference(Requestor arg0, IIdentity arg1,
			String arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluateDObfOutcome(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public DObfOutcome evaluateDObfOutcome(CtxIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferenceDetails()
	 */
	@Override
	public List<IDSPreferenceDetails> getIDSPreferenceDetails() {

		return this.prefCache.getIDSPreferenceDetails();
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferences(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences( Requestor requestor, IIdentity affectedIIdentity) {
		return this.prefCache.getIDSPreferences(affectedIIdentity, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreferences(org.societies.api.identity.IIdentity)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(IIdentity userDPI) {
		return this.prefCache.getIDSPreferences(userDPI);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails)
	 */
	@Override
	public IPrivacyPreferenceTreeModel getIDSPreference(IDSPreferenceDetails details) {
		return this.prefCache.getIDSPreference(details);
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferenceDetails()
	 */
	@Override
	public List<PPNPreferenceDetails> getPPNPreferenceDetails() {
		return this.prefCache.getPPNPreferenceDetails();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(java.lang.String)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType) {
		return this.prefCache.getPPNPreferences(contextType);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType, CtxAttributeIdentifier ctxID) {
		return this.prefCache.getPPNPreferences(contextType, ctxID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(org.societies.api.identity.Requestor, java.lang.String)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType) {
		return this.prefCache.getPPNPreferences(contextType, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreferences(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(Requestor requestor, String contextType, CtxAttributeIdentifier ctxID) {
		return this.prefCache.getPPNPreferences(contextType, ctxID, requestor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getPPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails)
	 */
	@Override
	public IPrivacyPreferenceTreeModel getPPNPreference(PPNPreferenceDetails details) {
		return this.prefCache.getPPNPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getDObfPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfPreferenceDetails)
	 */
	@Override
	public IPrivacyPreferenceTreeModel getDObfPreference(DObfPreferenceDetails details){
		return this.prefCache.getDObfPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getDObfPreferences()
	 */
	@Override
	public List<DObfPreferenceDetails> getDObfPreferences(){
		return this.prefCache.getDObfPreferences();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#storeIDSPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails, org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference)
	 */
	@Override
	public void storeIDSPreference(IDSPreferenceDetails details, IPrivacyPreference preference) {
		IPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details.getAffectedDPI(),preference);
		
		if (details.getRequestor()!=null){
			((IDSPrivacyPreferenceTreeModel) model).setRequestor(details.getRequestor());
		}
		this.prefCache.addIDSPreference(details, model);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#storePPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails, org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference)
	 */
	@Override
	public void storePPNPreference(PPNPreferenceDetails details, IPrivacyPreference preference){

		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(details.getContextType(), preference);
		if (details.getAffectedCtxID()!=null){
			model.setAffectedCtxId(details.getAffectedCtxID());
		}
		if (details.getRequestor()!=null){
			model.setRequestor(details.getRequestor());
		}
		this.logging.debug("REquest to add preference :\n"+details.toString());
		this.prefCache.addPPNPreference(details, model);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deleteIDSPreference(org.societies.api.identity.IIdentity)
	 */
	@Override
	public void deleteIDSPreference(IIdentity userDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userDPI);
		this.prefCache.removeIDSPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deleteIDSPreference(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	public void deleteIDSPreference(Requestor requestor, IIdentity userIdentity) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userIdentity);
		details.setRequestor(requestor);
		this.prefCache.removeIDSPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(java.lang.String)
	 */
	@Override
	public void deletePPNPreference(String contextType){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		this.prefCache.removePPNPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public void deletePPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID) {
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedCtxID(affectedCtxID);
		this.prefCache.removePPNPreference(details);

	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(org.societies.api.identity.Requestor, java.lang.String, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public void deletePPNPreference(Requestor requestor, String contextType, CtxAttributeIdentifier affectedCtxID){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedCtxID(affectedCtxID);
		details.setRequestor(requestor);
		this.prefCache.removePPNPreference(details);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#deletePPNPreference(org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails)
	 */
	@Override
	public void deletePPNPreference(PPNPreferenceDetails details) {
		this.prefCache.removePPNPreference(details);

	}
	
	
	/* 
	 ******* PRIVATE METHODS BELOW ***************
	 */
	
	
	private ResponseItem checkPreferenceForAccessControl(IPrivacyPreferenceTreeModel model, Requestor requestor, CtxAttributeIdentifier ctxId, List<Condition> conditions, List<Action> actions){
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		String actionList = "";
		for (Action a : actions){
			actionList = actionList.concat(a.toString());
		}
		if (null==outcome){
			this.logging.debug("Evaluation returned no result. Asking the user: "+ctxId.getType());
			int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+ctxId.getType()+"\n("+ctxId.toUriString()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				this.askToStoreDecision(requestor, ctxId, conditions, actions,  PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.PERMIT);
			}else{
				this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
				return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.DENY);
			}
		}else{
			if (((PPNPOutcome) outcome).getEffect()==PrivacyOutcomeConstants.ALLOW){
				return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.PERMIT);
			}
			return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.DENY);
		}
	}

	private ResponseItem createResponseItem(Requestor requestor, CtxAttributeIdentifier ctxId, List<Action> actions, List<Condition> conditions, Decision decision){
		RequestItem reqItem = new RequestItem(new Resource(ctxId), actions, conditions);
		ResponseItem respItem = new ResponseItem(reqItem, decision);
		return respItem;
	}
	private ResponseItem createResponseItem(Requestor requestor, String ctxType, List<Action> actions, List<Condition> conditions, Decision decision){
		RequestItem reqItem = new RequestItem(new Resource(ctxType), actions, conditions);
		ResponseItem respItem = new ResponseItem(reqItem, decision);
		return respItem;
	}
	private ResponseItem checkPreferenceForAccessControl(IPrivacyPreferenceTreeModel model, Requestor requestor, String ctxType, List<Condition> conditions, List<Action> actions){
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		if (null==outcome){
			this.logging.debug("Evaluation did not return a result");

			String actionList = "";
			for (Action a : actions){
				actionList = actionList.concat(a.toString());
			}
			try {
				Future<List<CtxIdentifier>> futureCtxIds = this.getCtxBroker().lookup(CtxModelType.ATTRIBUTE, ctxType);

				List<CtxIdentifier> ctxIds = futureCtxIds.get();

				CtxAttributeIdentifier ctxId;
				if (ctxIds.size()==0){
					this.logging.debug("CtxType: "+ctxType+" not found. Returning DENY");
					return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);
				}else if (ctxIds.size()==1){
					ctxId = (CtxAttributeIdentifier) ctxIds.get(0);
				}else{
					this.logging.debug("Asking the user: "+ctxType);
					ctxId = (CtxAttributeIdentifier) myMessageBox.showInputDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
							+ "resource:"+ctxType+"\n(to perform a "+actionList+" operation.\nSelect an attribute to provide access to or click cancel to abort.", 
							"Access request", JOptionPane.PLAIN_MESSAGE, ctxIds.toArray(), ctxIds.get(0));
					if (ctxId == null){
						this.logging.debug("User aborted. Returning block");
						this.askToStoreDecision(requestor, ctxType, actions, conditions, PrivacyOutcomeConstants.BLOCK);
						return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);
					}
				}
					this.askToStoreDecision(requestor, ctxId, conditions, actions, PrivacyOutcomeConstants.ALLOW);
					return this.createResponseItem(requestor, ctxId, actions, conditions, Decision.PERMIT);
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);

			}
		}else{
			if (((PPNPOutcome) outcome).getEffect()==PrivacyOutcomeConstants.ALLOW){
				return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.PERMIT);
			}
			return this.createResponseItem(requestor, ctxType, actions, conditions, Decision.DENY);		}
	}
	private void askToStoreDecision(Requestor requestor, CtxAttributeIdentifier ctxID, List<Condition> conditions,List<Action> actions,  PrivacyOutcomeConstants decision){
		int n = myMessageBox.showConfirmDialog("Do you want to store this decision permanently?", "Access request", JOptionPane.YES_NO_OPTION);
		if (n==JOptionPane.YES_OPTION){
			
			Resource r = new Resource(ctxID);
			List<Requestor> requestors = new ArrayList<Requestor>();
			requestors.add(requestor);
			RuleTarget ruleTarget = new RuleTarget(requestors, r, actions);
			try {
				PPNPOutcome outcome = new PPNPOutcome(decision, ruleTarget, new ArrayList<Condition>());
				PrivacyPreference pref = new PrivacyPreference(outcome); 
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(ctxID.getType(), pref);
				model.setAffectedCtxId(ctxID);
				model.setRequestor(requestor);
				PPNPreferenceDetails details = new PPNPreferenceDetails(ctxID.getType());
				details.setAffectedCtxID(ctxID);
				details.setRequestor(requestor);
				this.prefCache.addPPNPreference(details, model);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void askToStoreDecision(Requestor requestor, String ctxType, List<Action> actions, List<Condition> conditions, PrivacyOutcomeConstants decision){
		int n = myMessageBox.showConfirmDialog("Do you want to store this decision permanently?", "Access request", JOptionPane.YES_NO_OPTION);
		if (n==JOptionPane.YES_OPTION){
			
			Resource r = new Resource(ctxType);
			List<Requestor> requestors = new ArrayList<Requestor>();
			requestors.add(requestor);
			RuleTarget ruleTarget = new RuleTarget(requestors, r, actions);
			try {
				PPNPOutcome outcome = new PPNPOutcome(decision, ruleTarget, new ArrayList<Condition>());
				PrivacyPreference pref = new PrivacyPreference(outcome); 
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(ctxType, pref);
				model.setRequestor(requestor);
				PPNPreferenceDetails details = new PPNPreferenceDetails(ctxType);
				details.setRequestor(requestor);
				this.prefCache.addPPNPreference(details, model);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache, trustBroker);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluatePreference(privPref);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}
	
	private class InvalidIdentity implements IIdentity{

		public InvalidIdentity(){
			
		}

		@Override
		public String getJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getBareJid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDomain() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IdentityType getType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	private IIdentity evaluateIDSPreferenceBasedOnAllInfo(IAgreement agreement, List<IIdentity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			/*			if (model == null){
				JOptionPane.showMessageDialog(null, "prefCache returned null model for details:"+details.toString());
			}*/
			IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
			if (null!=outcome){
				//JOptionPane.showMessageDialog(null, "Evaluation returned non-null outcome");
				outcomes.add(outcome);
			}
		}	

		if (outcomes.size()==0){
			//JOptionPane.showMessageDialog(null, "No preference was applicable. returning null dpi");
			return null;
		}

		if (outcomes.size()==1){
			//JOptionPane.showMessageDialog(null, "ONE preference was applicable. returning dpi:"+outcomes.get(0).getIIdentity());
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			//JOptionPane.showMessageDialog(null, "MULTIPLE preferences were applicable. asking the user");
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}

	private IIdentity evaluateIDSPreferenceBasedOnProviderDPI(IAgreement agreement, List<IIdentity> identities){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<identities.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(identities.get(i));
			details.setRequestor(agreement.getRequestor());
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
			if (null!=outcome){
				outcomes.add(outcome);
			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}

	private IIdentity evaluateIDSPreferenceIrrespectiveOfRequestor(IAgreement agreement, List<IIdentity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			IPrivacyPreferenceTreeModel model = prefCache.getIDSPreference(details);
			IdentitySelectionPreferenceOutcome outcome = (IdentitySelectionPreferenceOutcome) this.evaluatePreference(model.getRootPreference());
			if (null!=outcome){
				outcomes.add(outcome);
			}
		}	

		if (outcomes.size()==0){
			return null;
		}

		if (outcomes.size()==1){
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			List<IIdentity> dpiList = new ArrayList<IIdentity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIIdentity(agreement, dpiList);
		}
		return null;
	}

	private IIdentity askUserToSelectIIdentity(IAgreement agreement, List<IIdentity> candidateIdentities){


		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (IIdentity userId : candidateIdentities){

			strCandidateIDs.add(userId.toString());
		}

		String s = "";
		if (agreement.getRequestor() instanceof RequestorService){
			s = this.askUserToSelectIdentityForStartingService((RequestorService) agreement.getRequestor(), strCandidateIDs);
		}else if (agreement.getRequestor() instanceof RequestorCis){
			s = this.askUserToSelectIdentityForJoiningCIS((RequestorCis) agreement.getRequestor(), strCandidateIDs);
		}
		


		for (IIdentity id : candidateIdentities){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	
	private String askUserToSelectIdentityForStartingService(RequestorService requestor, List<String> strCandidates){
		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for starting session with service:\n",
						"provided by: "+requestor.getRequestorId().toString()+
						"\nwith serviceID: "+requestor.getRequestorServiceId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));
	}
	
	private String askUserToSelectIdentityForJoiningCIS(RequestorCis requestor, List<String> strCandidates){
		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for joining CIS:\n", 
						"CIS id: "+requestor.getCisRequestorId().toString()+
						 "\nadministered by: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));
	}
	
	private String askUserToSelectIdentityForInteractingWithCSS(Requestor requestor, List<String> strCandidates){
		return (String) myMessageBox.showInputDialog(
				"Select an IIdentity for interacting with  CSS:\n", 
						"CSS id: "+requestor.getRequestorId().toString(),
						JOptionPane.QUESTION_MESSAGE, 
						strCandidates.toArray(), strCandidates.get(0));
	}

	public void addIDSDecision(Requestor requestor, IIdentity selectedDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails (selectedDPI);
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel model = this.getIDSPreference(details);
	}


	/**
	 * @return the privacyDataManager
	 */
	public IPrivacyDataManagerInternal getprivacyDataManagerInternal() {
		return privacyDataManagerInternal;
	}


	/**
	 * @param privacyDataManagerInternal the privacyDataManager to set
	 */
	public void setprivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}


	/**
	 * @return the commsManager
	 */
	public ICommManager getCommsManager() {
		return commsManager;
	}


	/**
	 * @param commsManager the commsManager to set
	 */
	public void setCommsManager(ICommManager commsManager) {
		this.commsManager = commsManager;
		this.idm = commsManager.getIdManager();
	}


	/**
	 * @return the test
	 */
	public boolean isTest() {
		return test;
	}


	/**
	 * @param test the test to set
	 */
	public void setTest(boolean test) {
		this.test = test;
	}


	/**
	 * @return the myMessageBox
	 */
	public MessageBox getMyMessageBox() {
		return myMessageBox;
	}


	/**
	 * @param myMessageBox the myMessageBox to set
	 */
	public void setMyMessageBox(MessageBox myMessageBox) {
		this.myMessageBox = myMessageBox;
	}







	/*	public static void main(String[] args){
		PrivacyPreferenceManager pm = new PrivacyPreferenceManager();
		Subject sub = new Subject(new DigitalPersonalIdentifier("Provider"));
		ServiceResourceIdentifier sID = new PssServiceIdentifier("pss://Provider1234%5B574cb33a-6590-4aaa-a69c-565e83d645fd%5D@7596720118024345462");
		ResponsePolicy policy = new ResponsePolicy(sub, new ArrayList<ResponseItem>(), NegotiationStatus.SUCCESSFUL);
		NegotiationAgreement ag = new NegotiationAgreement(policy);
		List<IIdentity> dpis = new ArrayList<IIdentity>();
		dpis.add(new DigitalPersonalIdentifier("John"));
		dpis.add(new DigitalPersonalIdentifier("Sarah"));
		dpis.add(new DigitalPersonalIdentifier("Mark"));
		IIdentity selectedDPI = pm.askUserToSelectIIdentity(ag, dpis);
		System.out.println("Selected: "+selectedDPI.toUriString());

	}*/
}

