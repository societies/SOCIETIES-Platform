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
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.datatypes.IdentityType;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Subject;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyOutcomeConstants;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
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
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;

import com.sun.jndi.toolkit.ctx.ComponentContext;

/**
 * @author Elizabeth
 *
 */

public class PrivacyPreferenceManager implements IPrivacyPreferenceManager{
	
	private PrivatePreferenceCache prefCache;
	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private ICtxBroker broker;

	protected synchronized void setCtxBroker(ICtxBroker ctxbroker){
		this.logging.debug("Binding CtxBroker to PolicyMgr");
		this.broker = ctxbroker;
	}

	public void initialisePrivacyPreferenceManager(ICtxBroker broker){
		this.broker = broker;
		
	}


	private PrivacyOutcomeConstants checkPreferenceForAccessControl(IPrivacyPreferenceTreeModel model, Identity requestorIdentity, CtxAttributeIdentifier ctxId, Action action){
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		if (null==outcome){
			this.logging.debug("Evaluation returned no result. Asking the user: "+ctxId.getType());
			int n = JOptionPane.showConfirmDialog(null, requestorIdentity.toString()+" is requesting access to: \n"
					+ "resource:"+ctxId.getType()+"\n("+ctxId.toUriString()+")\nto perform a "+action.getActionType()+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.ALLOW);
				return PrivacyOutcomeConstants.ALLOW;
			}else{
				this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.BLOCK);
				return PrivacyOutcomeConstants.BLOCK;
			}
		}else{
			return ((PPNPOutcome) outcome).getEffect();
		}
	}

	private PrivacyOutcomeConstants checkPreferenceForAccessControl(IPrivacyPreferenceTreeModel model, Identity requestorIdentity, String ctxType, Action action){
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference());
		if (null==outcome){
			this.logging.debug("Evaluation did not return a result");

			
			try {
				Future<List<CtxIdentifier>> futureCtxIds = this.broker.lookup(CtxModelType.ATTRIBUTE, ctxType);

				List<CtxIdentifier> ctxIds = futureCtxIds.get();

				CtxAttributeIdentifier ctxId;
				if (ctxIds.size()==0){
					this.logging.debug("CtxType: "+ctxType+" not found. Returning BLOCK decision");
					return PrivacyOutcomeConstants.BLOCK;
				}else if (ctxIds.size()==0){
					ctxId = (CtxAttributeIdentifier) ctxIds.get(0);
				}else{
					this.logging.debug("Asking the user: "+ctxType);
					ctxId = (CtxAttributeIdentifier) JOptionPane.showInputDialog(null, requestorIdentity.toString()+" is requesting access to: \n"
							+ "resource:"+ctxType+"\n(to perform a "+action.getActionType()+" operation.\nSelect an attribute to provide access to or click cancel to abort.", "Access request", JOptionPane.PLAIN_MESSAGE, null, ctxIds.toArray(), ctxIds.get(0));
					if (ctxId == null){
						this.logging.debug("User aborted. Returning block");
						this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.BLOCK);
						return PrivacyOutcomeConstants.BLOCK;
					}
				}
					this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.ALLOW);
					return PrivacyOutcomeConstants.ALLOW;
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return PrivacyOutcomeConstants.BLOCK;

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return PrivacyOutcomeConstants.BLOCK;

			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return PrivacyOutcomeConstants.BLOCK;

			}
		}else{
			return ((PPNPOutcome) outcome).getEffect();
		}
	}
	private void askToStoreDecision(Identity requestorDPI, CtxAttributeIdentifier ctxID, Action action, PrivacyOutcomeConstants decision){
		int n = JOptionPane.showConfirmDialog(null, "Do you want to store this decision permanently?", "Access request", JOptionPane.YES_NO_OPTION);
		if (n==JOptionPane.YES_OPTION){
			Subject sub = new Subject(requestorDPI);
			ArrayList<Subject> subjects = new ArrayList<Subject>();
			subjects.add(sub);
			Resource r = new Resource(ctxID);
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			RuleTarget ruleTarget = new RuleTarget(subjects, r, actions);
			try {
				PPNPOutcome outcome = new PPNPOutcome(decision, ruleTarget, new ArrayList<Condition>());
				PrivacyPreference pref = new PrivacyPreference(outcome); 
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(ctxID.getType(), pref);
				model.setAffectedCtxId(ctxID);
				model.setProviderDPI(requestorDPI);
				PPNPreferenceDetails details = new PPNPreferenceDetails(ctxID.getType());
				details.setAffectedCtxID(ctxID);
				details.setRequestorDPI(requestorDPI);
				this.prefCache.addPPNPreference(details, model);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public PrivacyOutcomeConstants checkPermission(CtxAttributeIdentifier ctxId, Action action, Identity requestorDPI) throws PrivacyException{
		if (null==ctxId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : BLOCK");
			return PrivacyOutcomeConstants.BLOCK;
		}
		PPNPreferenceDetails details = new PPNPreferenceDetails(ctxId.getType());
		details.setAffectedCtxID(ctxId);
		details.setRequestorDPI(requestorDPI);
		IPrivacyPreferenceTreeModel model = prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorDPI, ctxId, action);
		}

		details = new PPNPreferenceDetails(ctxId.getType());
		details.setRequestorDPI(requestorDPI);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorDPI, ctxId, action);
		}		

		details = new PPNPreferenceDetails(ctxId.getType());
		details.setAffectedCtxID(ctxId);
		model = this.prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorDPI, ctxId, action);
		}

		details = new PPNPreferenceDetails(ctxId.getType());
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorDPI, ctxId, action);
		}
		int n = JOptionPane.showConfirmDialog(null, requestorDPI.toString()+" is requesting access to: \n"
				+ "resource:"+ctxId.getType()+"\n("+ctxId.toUriString()+")\nto perform a "+action.getActionType()+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
		if (n==JOptionPane.YES_OPTION){
			this.askToStoreDecision(requestorDPI, ctxId, action, PrivacyOutcomeConstants.ALLOW);
			return PrivacyOutcomeConstants.ALLOW;
		}else{
			this.askToStoreDecision(requestorDPI, ctxId, action, PrivacyOutcomeConstants.BLOCK);
			return PrivacyOutcomeConstants.BLOCK;
		}

	}

	/*
	 * SOCIETIES new method 
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(java.lang.String, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action, org.societies.api.comm.xmpp.datatypes.Identity)
	 */
	@Override
	public PrivacyOutcomeConstants checkPermission(String ctxType, Action action, Identity requestorIdentity) throws PrivacyException{

		PPNPreferenceDetails details = new PPNPreferenceDetails(ctxType);
		details.setRequestorDPI(requestorIdentity);
		IPrivacyPreferenceTreeModel model = prefCache.getPPNPreference(details);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorIdentity, ctxType, action);
		}



		details = new PPNPreferenceDetails(ctxType);
		if (model!=null){
			return this.checkPreferenceForAccessControl(model, requestorIdentity, ctxType, action);
		}
		
		try {
			Future<List<CtxIdentifier>> futureCtxIds = this.broker.lookup(CtxModelType.ATTRIBUTE, ctxType);

			List<CtxIdentifier> ctxIds = futureCtxIds.get();

			CtxAttributeIdentifier ctxId;
			if (ctxIds.size()==0){
				this.logging.debug("CtxType: "+ctxType+" not found. Returning BLOCK decision");
				return PrivacyOutcomeConstants.BLOCK;
			}else if (ctxIds.size()==0){
				ctxId = (CtxAttributeIdentifier) ctxIds.get(0);
			}else{
				this.logging.debug("Asking the user: "+ctxType);
				ctxId = (CtxAttributeIdentifier) JOptionPane.showInputDialog(null, requestorIdentity.toString()+" is requesting access to: \n"
						+ "resource:"+ctxType+"\n(to perform a "+action.getActionType()+" operation.\nSelect an attribute to provide access to or click cancel to abort.", "Access request", JOptionPane.PLAIN_MESSAGE, null, ctxIds.toArray(), ctxIds.get(0));
				if (ctxId == null){
					this.logging.debug("User aborted. Returning block");
					this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.BLOCK);
					return PrivacyOutcomeConstants.BLOCK;
				}
			}
				this.askToStoreDecision(requestorIdentity, ctxId, action, PrivacyOutcomeConstants.ALLOW);
				return PrivacyOutcomeConstants.ALLOW;
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return PrivacyOutcomeConstants.BLOCK;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return PrivacyOutcomeConstants.BLOCK;

		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return PrivacyOutcomeConstants.BLOCK;

		}

	}
	
	
	private IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluatePreference(privPref);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}

	/*	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String type){

		List<CtxIdentifier> ctxlist = this.ppnpRegistry.getCtxIdentifiers(type);
		ArrayList<IPrivacyPreferenceTreeModel> modelList = new ArrayList<IPrivacyPreferenceTreeModel>();
		for (CtxIdentifier id : ctxlist){
			IPrivacyPreferenceTreeModel model = this.prefCache.getPreference(id);
			if (null!=model){
				modelList.add(model);
			}
		}
		return modelList;
	}
	 */
	
	public void storePPNPreference(PPNPreferenceDetails details, IPrivacyPreference preference){

		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(details.getContextType(), preference);
		if (details.getAffectedCtxID()!=null){
			model.setAffectedCtxId(details.getAffectedCtxID());
		}
		if (details.getRequestorDPI()!=null){
			model.setProviderDPI(details.getRequestorDPI());
			if (model.getServiceID()!=null){
				model.setServiceID(details.getServiceID());
			}
		}
		this.logging.debug("REquest to add preference :\n"+details.toString());
		this.prefCache.addPPNPreference(details, model);
	}





	@Override
	public Identity evaluateIDSPreferences(IAgreement agreement, List<Identity> dpis){

		Identity selectedIdentity = this.evaluateIDSPreferenceBasedOnAllInfo(agreement, dpis);

		if (selectedIdentity==null){
			selectedIdentity = this.evaluateIDSPreferenceBasedOnProviderDPI(agreement, dpis);
			if (selectedIdentity == null){
				selectedIdentity = this.evaluateIDSPreferenceIrrespectiveOfRequestor(agreement, dpis);
				if (selectedIdentity == null){
					return null;
				}else if (selectedIdentity instanceof InvalidIdentity){
					//user wants to use a new DPI
					return null;
				}else{
					return selectedIdentity;
				}
			}else if (selectedIdentity instanceof InvalidIdentity){
				//user wants to use a new DPI
				return null;
			}else{
				return selectedIdentity;
			}
		}else if (selectedIdentity instanceof InvalidIdentity){
			//user wants to use a new DPI
			return null;
		}else{
			return selectedIdentity;
		}

	}
	
	
	private class InvalidIdentity extends Identity{

		public InvalidIdentity(IdentityType type, String identifier,
				String domainIdentifier) {
			super(type, "", "");
			// TODO Auto-generated constructor stub
		}
		
		public InvalidIdentity(){
			super(IdentityType.CSS, "","");
		}

		@Override
		public String getJid() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	private Identity evaluateIDSPreferenceBasedOnAllInfo(IAgreement agreement, List<Identity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			details.setProviderDPI(agreement.getServiceDPI());
			details.setServiceID(agreement.getServiceIdentifier());
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
			//JOptionPane.showMessageDialog(null, "ONE preference was applicable. returning dpi:"+outcomes.get(0).getIdentity());
			return outcomes.get(0).getIdentity();
		}

		if (outcomes.size()>1){
			//JOptionPane.showMessageDialog(null, "MULTIPLE preferences were applicable. asking the user");
			List<Identity> dpiList = new ArrayList<Identity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIdentity(agreement, dpiList);
		}
		return null;
	}

	private Identity evaluateIDSPreferenceBasedOnProviderDPI(IAgreement agreement, List<Identity> dpis){
		ArrayList<IdentitySelectionPreferenceOutcome> outcomes = new ArrayList<IdentitySelectionPreferenceOutcome>();
		for (int i=0; i<dpis.size(); i++){
			IDSPreferenceDetails details = new IDSPreferenceDetails(dpis.get(i));
			details.setProviderDPI(agreement.getServiceDPI());
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
			List<Identity> dpiList = new ArrayList<Identity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIdentity(agreement, dpiList);
		}
		return null;
	}

	private Identity evaluateIDSPreferenceIrrespectiveOfRequestor(IAgreement agreement, List<Identity> dpis){
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
			List<Identity> dpiList = new ArrayList<Identity>();
			for (IdentitySelectionPreferenceOutcome out: outcomes){
				dpiList.add(out.getIdentity());
			}
			return this.askUserToSelectIdentity(agreement, dpiList);
		}
		return null;
	}

	private Identity askUserToSelectIdentity(IAgreement agreement, List<Identity> candidateDPIs){


		List<String> strCandidateIDs = new ArrayList<String>();
		strCandidateIDs.add((new InvalidIdentity()).toString());

		for (Identity userId : candidateDPIs){

			strCandidateIDs.add(userId.toString());
		}

		String s = (String) JOptionPane.showInputDialog(
				null,
				"Select an identity for starting session with service:\n"
						+ "provided by: "+agreement.getServiceDPI().toString(),
						"\nwith serviceID: "+agreement.getServiceIdentifier(),
						JOptionPane.QUESTION_MESSAGE, null,
						strCandidateIDs.toArray(), strCandidateIDs.get(0));


		for (Identity id : candidateDPIs){
			if (s.equalsIgnoreCase(id.toString())){
				return id;
			}
		}
		return new InvalidIdentity();
	}
	@Override
	public void deletePPNPreference(String contextType){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		this.prefCache.removePPNPreference(details);
	}
	@Override
	public void deletePPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID) {
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedCtxID(affectedCtxID);
		this.prefCache.removePPNPreference(details);

	}

	@Override
	public void deletePPNPreference(String contextType, CtxAttributeIdentifier affectedCtxID, Identity requestorDPI){
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		details.setAffectedCtxID(affectedCtxID);
		details.setRequestorDPI(requestorDPI);
		this.prefCache.removePPNPreference(details);
	}


	@Override
	public List<IPrivacyOutcome> evaluatePreference(String contextType) {
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

	/*	private  class  InvalidDPI implements Identity{

		private String annotation;

		public InvalidDPI(){
			this.annotation = "None, Create new Identity for me";
		}
		@Override
		public String getAnnotation() {
			return this.annotation;
		}

		@Override
		public void setAnnotation(String arg0) {
			this.annotation = arg0;
		}

		@Override
		public String toUriString() {
			return this.annotation;
		}

		public String toString(){
			return this.toUriString();
		}

	}*/

	
	public Identity evaluateIDSPreference(
			IDSPreferenceDetails details) {
		IPrivacyPreferenceTreeModel model = this.prefCache.getIDSPreference(details);
		IPrivacyOutcome out = this.evaluatePreference(model.getRootPreference());
		if (out instanceof IdentitySelectionPreferenceOutcome){
			return ((IdentitySelectionPreferenceOutcome) out).getIdentity();
		}

		return null;

	}
	
	
/*	@Override
	public IPrivacyPreferenceTreeModel getIDSPreference(
			IDSPreferenceDetails details) {
		return this.prefCache.getIDSPreference(details);
	}*/
	
	
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(
			Identity userDPI) {
		return this.prefCache.getIDSPreferences(userDPI);
	}
	@Override
	public List<IPrivacyPreferenceTreeModel> getIDSPreferences(
			Identity userDPI, Identity providerDPI) {
		return this.prefCache.getIDSPreferences(userDPI, providerDPI);
	}

	public IPrivacyPreferenceTreeModel getIDSPreference(
			IDSPreferenceDetails details) {
		return this.prefCache.getIDSPreference(details);
	}
	
	
	/*
	 * new societies method
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#getIdSPreference(org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)
	 */
	public Identity evaluateIdSPreference(IServiceResourceIdentifier service_Id){
		List<IDSPreferenceDetails> details = this.prefCache.getIDSPreferenceDetails();
		List<Identity> identities = new ArrayList<Identity>();
		for (IDSPreferenceDetails detail : details){
			if (detail.getServiceID().equals(service_Id)){
				identities.add(this.evaluateIDSPreference(detail));
			}
		}
		
		if (identities.size()==0){
			return null;
		}
		if (identities.size()==1){
			return identities.get(0);
		}
		
		Identity selectedIdentity = (Identity) JOptionPane.showInputDialog(null, "The following identities are appropriate for using service :"+service_Id.toString()+" please select one to use", "Select Identity", JOptionPane.PLAIN_MESSAGE, null, identities.toArray(), identities.get(0));
		return selectedIdentity;
		
		
		
		
	}
	
	
	@Override
	public void deleteIDSPreference(Identity userDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userDPI);
		this.prefCache.removeIDSPreference(details);

	}
	@Override
	public void deleteIDSPreference(Identity userDPI,
			Identity providerDPI) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userDPI);
		details.setProviderDPI(providerDPI);
		this.prefCache.removeIDSPreference(details);

	}
	@Override
	public void deleteIDSPreference(Identity userDPI,
			Identity providerDPI, IServiceResourceIdentifier serviceID) {
		IDSPreferenceDetails details = new IDSPreferenceDetails(userDPI);
		details.setProviderDPI(providerDPI);
		details.setServiceID(serviceID);
		this.prefCache.removeIDSPreference(details);

	}
	
	
	
	public void storeIDSPreference(IDSPreferenceDetails details,
			IPrivacyPreference preference) {
		IPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details.getAffectedDPI(),preference);
		if (details.getProviderDPI()!=null){
			((IDSPrivacyPreferenceTreeModel) model).setServiceDPI(details.getProviderDPI());
		}

		if (details.getServiceID()!=null){
			((IDSPrivacyPreferenceTreeModel) model).setServiceID(details.getServiceID());
		}
		this.prefCache.addIDSPreference(details, model);




	}
	
	
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType) {
		return this.prefCache.getPPNPreferences(contextType);
	}
	
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType,
			CtxAttributeIdentifier ctxID) {
		return this.prefCache.getPPNPreferences(contextType, ctxID);
	}
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType,
			Identity requestorDPI) {
		return this.prefCache.getPPNPreferences(contextType, requestorDPI);
	}
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType,
			CtxAttributeIdentifier ctxID, Identity requestorDPI) {
		return this.prefCache.getPPNPreferences(contextType, ctxID, requestorDPI);
	}
	@Override
	public List<IPrivacyPreferenceTreeModel> getPPNPreferences(String contextType,
			Identity requestorDPI, IServiceResourceIdentifier serviceID) {
		return this.prefCache.getPPNPreferences(contextType, requestorDPI, serviceID);
	}
	
	
	
	public void deletePPNPreference(PPNPreferenceDetails details) {
		this.prefCache.removePPNPreference(details);

	}


	
	public IPrivacyPreferenceTreeModel getPPNPreference(
			PPNPreferenceDetails details) {
		return this.prefCache.getPPNPreference(details);
	}
	
	
	
	
	public List<IDSPreferenceDetails> getIDSPreferenceDetails() {

		return this.prefCache.getIDSPreferenceDetails();
	}
	
	
	
	
	public List<PPNPreferenceDetails> getPPNPreferenceDetails() {
		return this.prefCache.getPPNPreferenceDetails();
	}
	
	
/*	@Override
	public void deleteIDSPreference(IDSPreferenceDetails details) {
		this.prefCache.removeIDSPreference(details);

	}*/
	
	
	
	public void addIDSDecision(Identity selectedDPI,
			Identity providerDPI, IServiceResourceIdentifier serviceID) {
		IDSPreferenceDetails details = new IDSPreferenceDetails (selectedDPI);
		details.setProviderDPI(providerDPI);
		details.setServiceID(serviceID);


		IPrivacyPreferenceTreeModel model = this.getIDSPreference(details);


	}

	/*
	 * new SOCIETIES method
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#evaluatePPNP(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public ResponsePolicy evaluatePPNP(RequestPolicy request){
		return null;
	}

	/*	public static void main(String[] args){
		PrivacyPreferenceManager pm = new PrivacyPreferenceManager();
		Subject sub = new Subject(new DigitalPersonalIdentifier("Provider"));
		IServiceResourceIdentifier sID = new PssServiceIdentifier("pss://Provider1234%5B574cb33a-6590-4aaa-a69c-565e83d645fd%5D@7596720118024345462");
		ResponsePolicy policy = new ResponsePolicy(sub, new ArrayList<ResponseItem>(), NegotiationStatus.SUCCESSFUL);
		NegotiationAgreement ag = new NegotiationAgreement(policy);
		List<Identity> dpis = new ArrayList<Identity>();
		dpis.add(new DigitalPersonalIdentifier("John"));
		dpis.add(new DigitalPersonalIdentifier("Sarah"));
		dpis.add(new DigitalPersonalIdentifier("Mark"));
		Identity selectedDPI = pm.askUserToSelectIdentity(ag, dpis);
		System.out.println("Selected: "+selectedDPI.toUriString());

	}*/
}

