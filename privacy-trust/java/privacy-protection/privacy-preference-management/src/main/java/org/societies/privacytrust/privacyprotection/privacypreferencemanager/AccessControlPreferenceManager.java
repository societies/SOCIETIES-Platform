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
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.DObfPreferenceCreator;

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
	private final DObfPreferenceCreator dobfPrefCreator;
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private String[] sensedDataTypes;



	public AccessControlPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache, IUserFeedback userFeedback, ITrustBroker trustBroker, ICtxBroker ctxBroker, IPrivacyAgreementManager agreementMgr, IIdentityManager idMgr, DObfPreferenceCreator dobfPrefCreator, IPrivacyDataManagerInternal privacyDataManagerInternal){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.userFeedback = userFeedback;
		this.trustBroker = trustBroker;
		this.ctxBroker = ctxBroker;
		this.agreementMgr = agreementMgr;
		this.idMgr = idMgr;
		this.dobfPrefCreator = dobfPrefCreator;
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		sensedDataTypes = new String[]{CtxAttributeTypes.TEMPERATURE, 
				CtxAttributeTypes.STATUS,
				CtxAttributeTypes.LOCATION_SYMBOLIC,
				CtxAttributeTypes.LOCATION_COORDINATES,
				CtxAttributeTypes.ACTION};


	}


	private boolean isAttributeSensed(String type) {

		for (String sensedType : sensedDataTypes){
			if (sensedType.equalsIgnoreCase(type)){
				return true;
			}
		}

		return false;
	}

	/*	private ResponseItem checkPreferenceForAccessControl(AccessControlPreferenceDetailsBean details, IPrivacyPreferenceTreeModel model, List<Condition> conditions) throws MalformedCtxIdentifierException{
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
			int n = myMessageBox.showConfirmDialog(requestor.getRequestorId().toString()+" is requesting access to: \n"
					+ "resource:"+dataId.getType()+"\n("+dataId.getUri()+")\nto perform a "+actionList+" operation. \nAllow?", "Access request", JOptionPane.YES_NO_OPTION);
			if (n==JOptionPane.YES_OPTION){
				this.askToStoreDecision(requestor, dataId, conditions, actions,  PrivacyOutcomeConstants.ALLOW);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.PERMIT);
			}else{
				this.askToStoreDecision(requestor, dataId, conditions, actions, PrivacyOutcomeConstants.BLOCK);
				return this.createResponseItem(requestor, dataId, actions, conditions, Decision.DENY);
			}
		}else{
			if (((AccessControlOutcome) outcome).getEffect()==PrivacyOutcomeConstantsBean.ALLOW){
				this.logging.debug("Returning PERMIT decision for resource: "+dataId.getUri());
				return this.createResponseItem(requestor, dataId, action, conditions, Decision.PERMIT);
			}
			this.logging.debug("Returning DENY decision for resource: "+dataId.getUri());
			return this.createResponseItem(requestor, dataId, action, conditions, Decision.DENY);
		}
	}*/

	public IPrivacyOutcome evaluatePreference(IPrivacyPreference privPref, List<Condition> conditions){
		PreferenceEvaluator ppE = new PreferenceEvaluator(this.contextCache, trustBroker);
		Hashtable<IPrivacyOutcome, List<CtxIdentifier>> results = ppE.evaluateAccessCtrlPreference(privPref, conditions);
		Enumeration<IPrivacyOutcome> outcomes = results.keys();
		//JOptionPane.showMessageDialog(null, results.size());
		if (outcomes.hasMoreElements()){
			return outcomes.nextElement();
		}

		return null;

	}

	/*
	 * OK
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException{
		if (null==dataIds || dataIds.size()==0){
			this.logging.debug("requested permission without specifying data identifiers!");
			throw new PrivacyException("requested permission without specifying data identifiers!");
		}

		List<String> dataTypes = new ArrayList<String>();
		for (DataIdentifier dataId : dataIds){
			dataTypes.add(dataId.getType());
		}


		Hashtable<ResponseItem, List<Condition>> conditions = new Hashtable<ResponseItem, List<Condition>>();

		String strToPrint = "data identifiers: ";
		for (DataIdentifier dataId : dataIds){
			strToPrint = strToPrint.concat(dataId.getUri()+"\n");
		}
		if (null==action){
			this.logging.debug("requested permission for: "+strToPrint+" without specifying action");
			throw new PrivacyException("requested permission for: "+strToPrint+" without specifying action");	
		}
		if (null==requestor){
			this.logging.debug("requested permission to "+action.getActionConstant().value()+"  these items: "+strToPrint+" with null requestor");
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+"  these items: "+strToPrint+" with null requestor");
		}		
		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\n"+strToPrint+"\n and action: "+action.getActionConstant());

		/**
		 * retrieve agreed conditions from agreement.
		 */
		try {

			AgreementEnvelope agreementEnv = this.agreementMgr.getAgreement(RequestorUtils.toRequestor(requestor, this.idMgr));
			if (agreementEnv!=null){
				IAgreement agreement = agreementEnv.getAgreement();
				for (ResponseItem item: agreement.getRequestedItems()){
					for (String dataType : dataTypes){
						if (dataType.equalsIgnoreCase(item.getRequestItem().getResource().getDataType())){
							conditions.put(item, item.getRequestItem().getConditions());
						}
					}
				}

			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<ResponseItem> permissions = new ArrayList<ResponseItem>();

		List<DataIdentifier> preferencesExist = new ArrayList<DataIdentifier>();
		List<AccessControlResponseItem> preferencesDoNotExist = new ArrayList<AccessControlResponseItem>();

		for (DataIdentifier dataId : dataIds){
			AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
			details.setAction(action);
			details.setRequestor(requestor);
			Resource resource = ResourceUtils.create(dataId.getUri());
			details.setResource(resource);

			List<Condition> conditionsListForSingleItem = getConditionsHelperMethod(dataId, conditions.keys());
			ResponseItem evaluateAccCtrlPreference = this.evaluateAccCtrlPreference(details, conditionsListForSingleItem);
			if (evaluateAccCtrlPreference==null){

				AccessControlResponseItem respItem = new AccessControlResponseItem();
				RequestItem reqItem = new RequestItem();
				reqItem.setConditions(conditionsListForSingleItem);
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				reqItem.setActions(actions);
				resource.setDataIdUri(dataId.getUri());
				resource.setScheme(dataId.getScheme());
				resource.setDataType(dataId.getType());
				reqItem.setResource(resource);
				respItem.setRequestItem(reqItem);
				preferencesDoNotExist.add(respItem);
			}else{
				preferencesExist.add(dataId);
				permissions.add(evaluateAccCtrlPreference);
			}
		}
		if (preferencesDoNotExist.size()>0){
			try {
				List<AccessControlResponseItem> list = this.userFeedback.getAccessControlFB(RequestorUtils.toRequestor(requestor, idMgr), preferencesDoNotExist).get();
				for (AccessControlResponseItem item: list){
					if (item.isRemember()){
						this.privacyDataManagerInternal.updatePermission(requestor, item);
						this.storeDecision(requestor, item.getRequestItem().getResource(), item.getRequestItem().getConditions(), action, item.getDecision());
						this.logging.debug("Stored access control feedback as preference");
					}else{
						this.logging.debug("One-off access granted. Permission not stored permanently");
					}
					if (item.isObfuscationInput()){
						this.dobfPrefCreator.createPreference(requestor, item.getRequestItem().getResource(), item.getObfuscationLevel());
						this.logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
					}
				}
				permissions.addAll(list);
				return permissions;
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return permissions;
	}

	private List<Condition> getConditionsHelperMethod(DataIdentifier dataId, Enumeration<ResponseItem> fromAgreementItems){
		while (fromAgreementItems.hasMoreElements()){
			ResponseItem responseItem = fromAgreementItems.nextElement();
			if (responseItem.getRequestItem().getResource().getDataType().equalsIgnoreCase(dataId.getType())){
				return responseItem.getRequestItem().getConditions();
			}
		}
		return new ArrayList<Condition>();
	}

	/*
	 * OK
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, Action action) throws PrivacyException{
		if (null==dataId){
			this.logging.debug("requested permission for null CtxIdentifier. returning : null");
			throw new PrivacyException("requested permission for null CtxIdentifier!");
		}
		if (null==action){
			throw new PrivacyException("requested permission for: "+dataId.getUri()+" without specifying action");	
		}
		if (null==requestor){
			throw new PrivacyException("requested permission to "+action.getActionConstant().value()+" : "+dataId.getUri()+" with null requestor");
		}
		List<Condition> conditions = new ArrayList<Condition>();
		this.logging.debug("checkPermission: \nRequestor: "+requestor.toString()+"\nctxId: "+dataId.getUri()+"\n and action: "+action.getActionConstant());

		/**
		 * retrieve agreed conditions from agreement.
		 */
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

		if (conditions==null){
			conditions = new ArrayList<Condition>();
		}
		AccessControlPreferenceDetailsBean details = new AccessControlPreferenceDetailsBean();
		details.setAction(action);
		details.setRequestor(requestor);
		Resource resource = ResourceUtils.create(dataId.getUri());
		details.setResource(resource);

		ResponseItem evaluateAccCtrlPreference = this.evaluateAccCtrlPreference(details, conditions);

		if (evaluateAccCtrlPreference==null){			
			AccessControlResponseItem responseItem = new AccessControlResponseItem();

			RequestItem requestItem = new RequestItem();
			List<Action> actions = new ArrayList<Action>();
			actions.add(action);
			requestItem.setActions(actions);
			requestItem.setConditions(conditions);
			requestItem.setResource(resource);
			responseItem.setRequestItem(requestItem);
			List<AccessControlResponseItem> responseItems = new ArrayList<AccessControlResponseItem>();
			responseItems.add(responseItem);

			try{	
				List<AccessControlResponseItem> resultlist = this.userFeedback.getAccessControlFB(RequestorUtils.toRequestor(requestor, idMgr), responseItems).get();
				if (resultlist.size()==0){
					responseItem.setDecision(Decision.DENY);
					return responseItem;
				}

				AccessControlResponseItem accessControlResponseItem = resultlist.get(0);
				if (accessControlResponseItem.isRemember()){
					this.privacyDataManagerInternal.updatePermission(requestor, accessControlResponseItem);
					this.storeDecision(requestor, resource, accessControlResponseItem.getRequestItem().getConditions(), action, accessControlResponseItem.getDecision());
					this.logging.debug("Stored access control feedback as preference");
				}else{
					this.logging.debug("One-off access granted. Permission not stored permanently");
				}
				if (accessControlResponseItem.isObfuscationInput()){
					this.dobfPrefCreator.createPreference(requestor, accessControlResponseItem.getRequestItem().getResource(), accessControlResponseItem.getObfuscationLevel());
					this.logging.debug("Stored DObf preference based on user input to the access control feedback popup.");
				}else{
					this.logging.debug("Obfuscation not requested in the access control");
				}
				return accessControlResponseItem;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			responseItem.setDecision(Decision.DENY);
			return responseItem;
		}else{
			return evaluateAccCtrlPreference;
		}

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


	private void storeDecision(RequestorBean requestor, Resource resource, List<Condition> conditions,Action action,  Decision decision){

		List<RequestorBean> requestors = new ArrayList<RequestorBean>();
		requestors.add(requestor);

		try {
			AccessControlOutcome outcome;
			if (decision.equals(Decision.PERMIT)){
				outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
			}else{
				outcome = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);

			}

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
				// 2013-04-16: updated by Olivier to replace CtxAttributeIdentifier to CtxIdentifier. Add logic to handle also CtxEntity and not just CtxAttribute
				CtxIdentifier ctxIdentifier = (CtxIdentifier) ResourceUtils.getDataIdentifier(resource);
				CtxModelObject ctxModelObject = this.ctxBroker.retrieve(ctxIdentifier).get();
				ContextPreferenceCondition condition;
				PrivacyPreference conditionPreference;
				// -- CtxAttribute
				if (ctxModelObject!=null && ctxModelObject instanceof CtxAttribute){
					CtxAttribute ctxAttribute = (CtxAttribute) ctxModelObject;
					// CtxAttribute is inferred or sensed: add a privacy preference condition
					if (isAttributeSensed(ctxAttribute.getType())){
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
					// -- CtxEntity
					else if (ctxModelObject!=null && ctxModelObject instanceof CtxEntity){
						CtxEntity ctxEntity = (CtxEntity) ctxModelObject;
						// TODO for Eliza: check if it is relevant to add a ContextPreferenceCondition or not
						//						condition = new ContextPreferenceCondition(ctxIdentifier, OperatorConstants.EQUALS, ctxEntity.getOwnerId());
						//						conditionPreference = new PrivacyPreference(condition);
						//						conditionPreference.add(withAllConditionsPreference);
						//						return conditionPreference;
						// comment from Eliza: this will never happen.
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
				//return this.checkPreferenceForAccessControl(details, model, conditions);
				IPrivacyOutcome evaluatePreference = this.evaluatePreference(model.getPref(), conditions);

				if (evaluatePreference!=null){
					if (evaluatePreference instanceof AccessControlOutcome){
						DataIdentifier dataId = ResourceUtils.getDataIdentifier(details.getResource());
						if (((AccessControlOutcome) evaluatePreference).getEffect().equals(PrivacyOutcomeConstantsBean.ALLOW)){
							return this.createResponseItem(details.getRequestor(), dataId, details.getAction(), conditions, Decision.PERMIT);
						}else{
							return this.createResponseItem(details.getRequestor(), dataId, details.getAction(), conditions, Decision.DENY);
						}
					}else{
						throw new PrivacyException("An unexpected error occured. The evaluated outcome was not of type AccessControlOutcome");
					}
				}
			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		this.logging.debug("Could not find preference for given details");
		return null;

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


	/**
	 * 
	 * @param requestor 
	 * @param dataIds	the list of requestedItems for which preferences exist
	 * @return			a hashtable whose keys represent the preference conditions (context)
	 */
	public Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> getContextConditions(Requestor requestor, List<DataIdentifier> dataIds){
		Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>> detailsToBeMonitored = new Hashtable<CtxIdentifier, ArrayList<AccessControlPreferenceDetailsBean>>();


		List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails = this.getAccCtrlPreferenceDetails();
		String display = "";
		for (AccessControlPreferenceDetailsBean detail : accCtrlPreferenceDetails){
			display = display.concat("\nRequestor: "+RequestorUtils.toString(detail.getRequestor())+", resource: "+ResourceUtils.toString(detail.getResource())+", action: "+detail.getAction().toString());
		}
		//JOptionPane.showMessageDialog(null, "Found prefs: "+accCtrlPreferenceDetails.size()+display);

		//for every requested item in the privacy policy
		for (DataIdentifier requestedDataId : dataIds){
			//JOptionPane.showMessageDialog(null, "requested ids loop: "+requestedDataId.getType());
			//for every preference 
			for (AccessControlPreferenceDetailsBean detail: accCtrlPreferenceDetails){
				//JOptionPane.showMessageDialog(null, "requested detail loop: "+detail.getResource().getDataType()+" \n"+detail.getRequestor().getRequestorId());
				//if the preference refers to this resource
				if (requestedDataId.getType().equalsIgnoreCase(detail.getResource().getDataType())){
					//if the preference refers to this requestor


					if (RequestorUtils.equal(detail.getRequestor(), RequestorUtils.toRequestorBean(requestor))){
						//JOptionPane.showMessageDialog(null, "Requestor: "+RequestorUtils.toString(detail.getRequestor())+" vs "+RequestorUtils.toString(RequestorUtils.toRequestorBean(requestor)));
						//retrieve the preference, iterate through it, and retrieve all the conditions
						AccessControlPreferenceTreeModel accCtrlPreference = this.getAccCtrlPreference(detail);
						IPrivacyPreference rootPreference = accCtrlPreference.getRootPreference();
						Enumeration<IPrivacyPreference> postorderEnumeration = rootPreference.postorderEnumeration();
						ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();
						while (postorderEnumeration.hasMoreElements()){

							IPrivacyPreference nextElement = postorderEnumeration.nextElement();

							if (nextElement.getUserObject()!=null){
								//JOptionPane.showMessageDialog(null, "Processing element "+nextElement.getUserObject().toString());
								if (nextElement.getUserObject() instanceof ContextPreferenceCondition){
									CtxIdentifier contextConditionID =((ContextPreferenceCondition)nextElement.getCondition()).getCtxIdentifier(); 
									//if the list doesn't already contain this condition
									if (!ctxIds.contains(contextConditionID)){
										ctxIds.add(contextConditionID);
									}
								}
							}
						}


						for (CtxIdentifier ctxId : ctxIds){
							//if the ctxId already exists as a key, add the preference details to the list 
							if (detailsToBeMonitored.containsKey(ctxId)){
								detailsToBeMonitored.get(ctxId).add(detail);
							}else{
								//else add the new ctxID as key and add the preference details in the list
								ArrayList<AccessControlPreferenceDetailsBean> list = new ArrayList<AccessControlPreferenceDetailsBean>();
								list.add(detail);
								detailsToBeMonitored.put(ctxId, list);
							}
						}
					}
				}
			}
		}


		return detailsToBeMonitored;
	}

	private boolean contains(List<CtxIdentifier> dataIds, String uri){
		for (CtxIdentifier ctxId : dataIds){
			if (ctxId.getUri().equals(uri)){
				return true;
			}
		}

		return false;
	}
	public static void main(String[] args){
		AccessControlPreferenceManager prefMgr = new AccessControlPreferenceManager(null, null, null, null, null, null, null, null, null);

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
