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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;
import org.societies.privacytrust.privacyprotection.util.preference.PrivacyPreferenceUtils;

/**
 * @author Eliza
 *
 */
public class AccessControlPreferenceManager {

	private final static Logger logging = LoggerFactory.getLogger(AccessControlPreferenceManager.class);
	private final PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	private final IUserFeedback userFeedback;
	private final ITrustBroker trustBroker;
	private final ICtxBroker ctxBroker;
	private final IPrivacyAgreementManager agreementMgr;
	private final IIdentityManager idMgr;

	public AccessControlPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache, IUserFeedback userFeedback, ITrustBroker trustBroker, ICtxBroker ctxBroker, IPrivacyAgreementManager agreementMgr, IIdentityManager idMgr){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.userFeedback = userFeedback;
		this.trustBroker = trustBroker;
		this.ctxBroker = ctxBroker;
		this.agreementMgr = agreementMgr;
		this.idMgr = idMgr;

	}


	private ResponseItem checkPreferenceForAccessControl(AccessControlPreferenceDetailsBean details, IPrivacyPreferenceTreeModel model, List<Condition> conditions) throws MalformedCtxIdentifierException{
		RequestorBean requestor = details.getRequestor();
		DataIdentifier dataId = DataIdentifierFactory.fromUri(details.getResource().getDataIdUri());
		Action action = details.getAction();
		this.logging.debug("Evaluating preference");
		IPrivacyOutcome outcome = this.evaluatePreference(model.getRootPreference(), conditions);

		String actionList = "";

		if (null==outcome){
			this.logging.debug("Evaluation returned no result. Asking the user: "+dataId.getType());

			this.logging.debug("Evaluation returned no result. Asking the user: "+dataId.getType());

			String allow = "Allow";
			String deny = "Deny";


			List<String> response = new ArrayList<String>();

			String proposalText = requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation.";
			try {
				response = this.userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, new String[]{allow,deny})).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (response.contains(allow)){
				this.storeDecision(requestor, dataId, conditions, action,  PrivacyOutcomeConstantsBean.ALLOW);
				return this.createResponseItem(requestor, dataId, action, conditions, Decision.PERMIT);
			}else{
				this.storeDecision(requestor, dataId, conditions, action, PrivacyOutcomeConstantsBean.BLOCK);
				return this.createResponseItem(requestor, dataId, action, conditions, Decision.DENY);
			}
			/*int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				this.askToStoreDecision(requestor, dataId, conditions, actions,  PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
			}else{
				this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
			}*/
		}else{
			if (((AccessControlOutcome) outcome).getEffect()==PrivacyOutcomeConstantsBean.ALLOW){
				this.logging.debug("Returning PERMIT decision for resource: "+dataId.getUri());
				return this.createResponseItem(requestor, dataId, action, conditions, Decision.PERMIT);
			}
			this.logging.debug("Returning DENY decision for resource: "+dataId.getUri());
			return this.createResponseItem(requestor, dataId, action, conditions, Decision.DENY);
		}
	}

	private IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref, List<Condition> conditions){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache, trustBroker);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluateAccessCtrlPreference(privPref, conditions);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException{

		if (null==dataId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : null");
			return null;

		}
		List<Condition> conditions = new ArrayList<Condition>();

		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\nctxId: "+dataId.getUri()+"\n and actions...");

		try {
			AgreementEnvelope agreementEnv = this.agreementMgr.getAgreement(RequestorUtils.toRequestor(requestor, this.idMgr));
			if (agreementEnv!=null){
				IAgreement agreement = agreementEnv.getAgreement();
				for (ResponseItem item: agreement.getRequestedItems()){
					if (item.getRequestItem().getResource().getDataType().equals(dataId.getType())){
						conditions = item.getRequestItem().getConditions();
						//JOptionPane.showMessageDialog(null, "Found conditions in agreement");
					}
				}

			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
		details.setRequestor(requestor);
		Resource resource = ResourceUtils.create(dataId.getUri());
		details.setResource(resource);
		List<AccessControlPreferenceTreeModel> models = new ArrayList<AccessControlPreferenceTreeModel>();
		Hashtable<Action,ResponseItem> items = new Hashtable<Action,ResponseItem>();

		List<Action> notExistsPreference = new ArrayList<Action>();
		for (Action action: actions){
			details.setAction(action);
			this.logging.debug("Retrieving preference for: "+PrivacyPreferenceUtils.toString(details));
			this.logging.debug("Retrieved preference for: "+PrivacyPreferenceUtils.toString(details));
			try{
				ResponseItem evaluationResult = this.evaluateAccCtrlPreference(details, conditions); 
				//this.checkPreferenceForAccessControl(model, requestor, dataId, conditions, action);
				if (evaluationResult!=null){
					items.put(action, evaluationResult);
				}else{
					notExistsPreference.add(action);
				}
			}
			catch (PrivacyException pe){
				notExistsPreference.add(action);
			}

		}


		//TODO: merge response items
		ResponseItem item = new ResponseItem();
		item.setDecision(Decision.PERMIT);
		RequestItem reqItem = new RequestItem();		
		reqItem.setResource(resource);
		item.setRequestItem(reqItem);


		String proposalText = requestor.getRequestorId().toString()+" is requesting access to perform the following actions to resource: \n"
				+ "type: "+dataId.getType()+"\n(resource Id: "+dataId.getUri()+")\nSelect the actions you want to allow to be performed.";
		String[] actionsStr = new String[notExistsPreference.size()];
		int i = 0;
		for (Action a: notExistsPreference){
			actionsStr[i] = a.getActionConstant().name();
			i++;
		}
		ExpProposalContent expContent = new ExpProposalContent(proposalText, actionsStr);

		List<String> response;
		try {
			response = this.userFeedback.getExplicitFB(ExpProposalType.CHECKBOXLIST, expContent).get();
			if (response.size()==actionsStr.length){
				for (Action act : notExistsPreference){
					this.storeDecision(requestor, dataId, conditions, act, PrivacyOutcomeConstantsBean.ALLOW);
					item.getRequestItem().getActions().add(act);
				}
				item.setDecision(Decision.PERMIT);

			}else if (response.size()==0){
				item.setDecision(Decision.DENY);
				for (Action act : notExistsPreference){
					this.storeDecision(requestor, dataId, conditions, act, PrivacyOutcomeConstantsBean.BLOCK);
				}
			}else{
				for (Action act: notExistsPreference){
					if (response.contains(act.getActionConstant().name())){
						this.storeDecision(requestor, dataId, conditions, act, PrivacyOutcomeConstantsBean.ALLOW);
						item.getRequestItem().getActions().add(act);
						item.setDecision(Decision.PERMIT);
					}else{
						this.storeDecision(requestor, dataId, conditions, act, PrivacyOutcomeConstantsBean.BLOCK);

					}
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Enumeration<Action> actionkeys = items.keys();

		while (actionkeys.hasMoreElements()){
			Action nextElement = actionkeys.nextElement();
			if (items.get(nextElement).getDecision().equals(Decision.PERMIT)){
				reqItem.getActions().add(nextElement);		
			}else{
				item.setDecision(Decision.DENY);
			}
		}


		return item;

	}

	private void storeDecision(RequestorBean requestor, DataIdentifier dataId, List<Condition> conditions,Action action,  PrivacyOutcomeConstantsBean decision){
		Resource resource = new Resource();
		resource.setDataIdUri(dataId.getUri());
		resource.setScheme(dataId.getScheme());
		resource.setDataType(dataId.getType());
		List<RequestorBean> requestors = new ArrayList<RequestorBean>();
		requestors.add(requestor);

		try {
			AccessControlOutcome outcome = new AccessControlOutcome(decision);




			AccessControlPreferenceDetailsBean detailsBean = new AccessControlPreferenceDetailsBean();
			AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(detailsBean, this.createAccessCtrlPrivacyPreference(conditions,action, resource, outcome));

			detailsBean.setRequestor(requestor);
			detailsBean.setAction(action);
			detailsBean.setResource(resource);
			this.prefCache.addAccCtrlPreference(detailsBean, model);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//}
	}

	private PrivacyPreference createAccessCtrlPrivacyPreference(List<Condition> conditions, Action action, Resource resource, AccessControlOutcome outcome) throws PrivacyException{
		PrivacyPreference withAllConditionsPreference = this.createPreferenceWithPrivacyConditions(conditions, action, outcome);
		if (resource.getScheme().equals(DataIdentifierScheme.CONTEXT)){
			try {
				CtxAttributeIdentifier ctxIdentifier = (CtxAttributeIdentifier) ResourceUtils.getDataIdentifier(resource);
				CtxAttribute ctxAttribute = (CtxAttribute) this.ctxBroker.retrieve(ctxIdentifier).get();
				if (ctxAttribute!=null){
					if (null!=ctxAttribute.getQuality().getOriginType()){
						if (ctxAttribute.getQuality().getOriginType().equals(CtxOriginType.INFERRED) || ctxAttribute.getQuality().getOriginType().equals(CtxOriginType.SENSED)){
							ContextPreferenceCondition condition;
							PrivacyPreference conditionPreference;
							switch (ctxAttribute.getValueType()){
							case DOUBLE:
								condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getDoubleValue().toString());
								conditionPreference = new PrivacyPreference(condition);
								conditionPreference.add(withAllConditionsPreference);
								return conditionPreference;

							case INTEGER:
								condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getIntegerValue().toString());
								conditionPreference = new PrivacyPreference(condition);
								conditionPreference.add(withAllConditionsPreference);
								return conditionPreference;
							case STRING: 
								condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxAttribute.getStringValue());
								conditionPreference = new PrivacyPreference(condition);
								conditionPreference.add(withAllConditionsPreference);
								return conditionPreference;
							}

						}
					}
				}else{
					throw new PrivacyException("Could not create access control preference as there was no ctxAttribute found in DB with the provided dataIdentifier");
				}


			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		}
		return withAllConditionsPreference;
	}
	private PrivacyPreference createPreferenceWithPrivacyConditions(
			List<Condition> conditions, Action action,
			AccessControlOutcome outcome) {

		PrivacyPreference rootPreference = new PrivacyPreference(outcome); 
		for (Condition condition : conditions){
			rootPreference = this.getPrivacyCondition(rootPreference, condition);
		}

		return rootPreference;
	}

	private PrivacyPreference getPrivacyCondition(PrivacyPreference preference, Condition condition){

		PrivacyPreference pref = new PrivacyPreference(new PrivacyCondition(condition));
		pref.add(preference);
		return pref;

	}
	private ResponseItem createResponseItem(RequestorBean requestor, DataIdentifier dataId, Action action, List<Condition> conditions, Decision decision){

		RequestItem reqItem = new RequestItem();
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		reqItem.setActions(actions);
		reqItem.setConditions(conditions);
		Resource resource = new Resource();
		resource.setDataIdUri(dataId.getUri());
		resource.setDataType(dataId.getType());
		resource.setScheme(dataId.getScheme());
		reqItem.setResource(resource);
		ResponseItem respItem = new ResponseItem();
		respItem.setDecision(decision);
		respItem.setRequestItem(reqItem);

		return respItem;
	}

	/*	private List<Action> adjustActions(List<Action> actions){
		boolean hasCreate = false;
		boolean hasWrite = false;
		boolean hasDelete = false;
		for (Action a: actions){
			if (a.getActionConstant().equals(ActionConstants.CREATE)){
				hasCreate = true;
			}
			if (a.getActionConstant().equals(ActionConstants.WRITE)){
				hasWrite = true;
			}

			if (a.getActionConstant().equals(ActionConstants.DELETE)){
				hasDelete = true;
			}
		}
		Action write = new Action();
		write.setActionConstant(ActionConstants.WRITE);
		Action create = new Action();
		create.setActionConstant(ActionConstants.CREATE);
		Action delete = new Action();
		delete.setActionConstant(ActionConstants.DELETE);
		Action read = new Action();
		read.setActionConstant(ActionConstants.READ);


		if (hasCreate){
			actions = new ArrayList<Action>();

			actions.add(write);
			actions.add(create);
			actions.add(delete);
			actions.add(read);
		} else 	if (hasWrite){
			actions = new ArrayList<Action>();
			actions.add(write);
			actions.add(read);

			if (hasDelete){
				actions.add(delete);
			}
		}
		return actions;
	}*/


	/**
	 * new methods;
	 */

	public boolean deleteAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		return this.prefCache.removeAccCtrlPreference(details);
	}

	public ResponseItem evaluateAccCtrlPreference(
			AccessControlPreferenceDetailsBean details, List<Condition> conditions) throws PrivacyException {

		AccessControlPreferenceTreeModel model = this.prefCache.getAccCtrlPreference(details);

		if (model!=null){
			try {
				return this.checkPreferenceForAccessControl(details, model, conditions);
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		throw new PrivacyException("Could not find preference for given details");
	}
	public AccessControlPreferenceTreeModel getAccCtrlPreference(
			AccessControlPreferenceDetailsBean details) {
		return this.prefCache.getAccCtrlPreference(details);
	}
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails() {
		return this.prefCache.getAccCtrlPreferenceDetails();
	}

	public boolean storeAccCtrlPreference(
			AccessControlPreferenceDetailsBean details,
			AccessControlPreferenceTreeModel model) {
		return this.prefCache.addAccCtrlPreference(details, model);
	}

	public static void main(String[] args){
		AccessControlPreferenceManager prefMgr = new AccessControlPreferenceManager(null, null, null, null, null, null, null);

		List<Condition> conditions = new ArrayList<Condition>();
		Condition condition = new Condition();
		condition.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		condition.setValue("No");
		Condition condition2 = new Condition();
		condition2.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		condition2.setValue("24");
		conditions.add(condition2);
		conditions.add(condition);
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		AccessControlOutcome outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
		PrivacyPreference pref = prefMgr.createPreferenceWithPrivacyConditions(conditions, action, outcome);
		System.out.println(pref.getRoot());

	}

}
