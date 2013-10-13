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
package org.societies.privacytrust.privacyprotection.api.util;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.ContextPreferenceConditionBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.OperatorConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPOutcomeBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyConditionBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyConditionConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceConditionBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceTypeConstantsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RuleTargetBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.TrustPreferenceConditionBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;

/**
 * @author Eliza
 *
 */
public class PrivacyPreferenceUtils {



	/*
	 * FROM BEAN TO OBJECT METHODS
	 */


	public static PPNPrivacyPreferenceTreeModel toPPNPrivacyPreferenceTreeModel(PPNPrivacyPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException, URISyntaxException{

		return new PPNPrivacyPreferenceTreeModel(bean.getDetails(), toPPNPrivacyPreference(bean.getPref(), idMgr));
	}

	public static IDSPrivacyPreferenceTreeModel toIDSPrivacyPreferenceTreeModel(IDSPrivacyPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException{

		return new IDSPrivacyPreferenceTreeModel(bean.getDetails() , toIDSPrivacyPreference(bean.getPref(), idMgr));
	}

	public static DObfPreferenceTreeModel toDObfPreferenceTreeModel(DObfPrivacyPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException{

		return new DObfPreferenceTreeModel(bean.getDetails(), toDObfPrivacyPreference(bean.getPref(), idMgr));
	}

	public static AccessControlPreferenceTreeModel toAccCtrlPreferenceTreeModel(AccessControlPreferenceTreeModelBean bean, IIdentityManager idMgr) throws InvalidFormatException, URISyntaxException{
		AccessControlPreferenceTreeModel model = new AccessControlPreferenceTreeModel(bean.getDetails(), toAccCtrlPrivacyPreference(bean.getPref(), idMgr));
		return model;
	}

	public static PrivacyPreference toPPNPrivacyPreference(PPNPreferenceBean bean, IIdentityManager idMgr) throws URISyntaxException{


		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<PPNPreferenceBean> beans = bean.getChildren();

			for (PPNPreferenceBean b : beans){
				preference.add(toPPNPrivacyPreference(b, idMgr));
			}

			return preference;
		}

		if (bean.getOutcome()!=null){

			return new PrivacyPreference(toPPNOutcome(bean.getOutcome(), idMgr));
		}

		PrivacyPreference preference = new PrivacyPreference();
		List<PPNPreferenceBean> beans = bean.getChildren();

		for (PPNPreferenceBean b : beans){
			preference.add(toPPNPrivacyPreference(b, idMgr));
		}

		return preference;


	}


	public static PrivacyPreference toIDSPrivacyPreference(IDSPreferenceBean bean, IIdentityManager idMgr) throws InvalidFormatException{
		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<IDSPreferenceBean> beans = bean.getChildren();

			for (IDSPreferenceBean b : beans){
				preference.add(toIDSPrivacyPreference(b, idMgr));
			}

			return preference;
		}

		if (bean.getOutcome()!=null){
			return new PrivacyPreference(toIDSOutcome(bean.getOutcome(), idMgr));
		}

		PrivacyPreference preference = new PrivacyPreference();
		List<IDSPreferenceBean> beans = bean.getChildren();

		for (IDSPreferenceBean b : beans){
			preference.add(toIDSPrivacyPreference(b, idMgr));
		}

		return preference;


	}

	public static PrivacyPreference toDObfPrivacyPreference(DObfPreferenceBean bean, IIdentityManager idMgr){
		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<DObfPreferenceBean> beans = bean.getChildren();

			for (DObfPreferenceBean b : beans){
				preference.add(toDObfPrivacyPreference(b, idMgr));
			}

			return preference;
		}

		if (bean.getOutcome()!=null){
			return new PrivacyPreference(toDObfOutcome(bean.getOutcome(), idMgr));
		}

		PrivacyPreference preference = new PrivacyPreference();
		List<DObfPreferenceBean> beans = bean.getChildren();

		for (DObfPreferenceBean b : beans){
			preference.add(toDObfPrivacyPreference(b, idMgr));
		}

		return preference;
	}


	public static PrivacyPreference toAccCtrlPrivacyPreference(AccessControlPreferenceBean bean, IIdentityManager idMgr) throws URISyntaxException{
		if (bean.getCondition()!=null){
			PrivacyPreference preference = new PrivacyPreference(toPrivacyPreferenceCondition(bean.getCondition()));
			List<AccessControlPreferenceBean> beans = bean.getChildren();

			for (AccessControlPreferenceBean b : beans){
				preference.add(toAccCtrlPrivacyPreference(b, idMgr));
			}

			return preference;
		}

		if (bean.getOutcome()!=null){
			return new PrivacyPreference(toAccessControlOutcome(bean.getOutcome(), idMgr));
		}

		PrivacyPreference preference = new PrivacyPreference();
		List<AccessControlPreferenceBean> beans = bean.getChildren();

		for (AccessControlPreferenceBean b : beans){
			preference.add(toAccCtrlPrivacyPreference(b, idMgr));
		}

		return preference;
	}
	public static IPrivacyOutcome toPPNOutcome(
			PPNPOutcomeBean bean, IIdentityManager idMgr) throws URISyntaxException {
		return new PPNPOutcome(bean.getDecision());


	}

	public static IdentitySelectionPreferenceOutcome toIDSOutcome(
			IDSOutcomeBean bean, IIdentityManager idMgr) throws InvalidFormatException {
		IdentitySelectionPreferenceOutcome outcome = new IdentitySelectionPreferenceOutcome(idMgr.fromJid(bean.getUserIdentity()));
		outcome.setShouldUseIdentity(bean.isShouldUseIdentity());
		return outcome;
	}

	public static DObfOutcome toDObfOutcome(DObfOutcomeBean bean, IIdentityManager idMgr){
		return new DObfOutcome(bean.getObfuscationLevel());

	}

	public static AccessControlOutcome toAccessControlOutcome(AccessControlOutcomeBean bean, IIdentityManager idMgr) throws URISyntaxException{
		AccessControlOutcome outcome = new AccessControlOutcome(bean.getEffect());
		outcome.setConfidenceLevel(bean.getConfidenceLevel());
		return outcome;
	}
	public static RuleTarget toRuleTarget(RuleTargetBean bean, IIdentityManager idMgr) {
		try {
			return new RuleTarget(RequestorUtils.toRequestors(bean.getSubjects(), idMgr), ResourceUtils.toResource(bean.getResource()), ActionUtils.toActions(bean.getActions()));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RuleTarget(new ArrayList<Requestor>(),ResourceUtils.toResource(bean.getResource()), ActionUtils.toActions(bean.getActions()));
		}

	}


	public static PrivacyOutcomeConstants toPrivacyOutcomeConstant(
			PrivacyOutcomeConstantsBean bean) {
		if (bean.compareTo(PrivacyOutcomeConstantsBean.ALLOW)==0){
			return PrivacyOutcomeConstants.ALLOW;
		}else{
			return PrivacyOutcomeConstants.BLOCK;
		}
	}

	public static IPrivacyPreferenceCondition toPrivacyPreferenceCondition(
			PrivacyPreferenceConditionBean bean) {
		if (bean instanceof ContextPreferenceConditionBean){
			return toContextPreferenceCondition((ContextPreferenceConditionBean) bean);
		}else if (bean instanceof PrivacyConditionBean){
			return new PrivacyCondition(((PrivacyConditionBean) bean).getCondition());
		}else
			return toTrustPreferenceCondition((TrustPreferenceConditionBean) bean);

	}



	public static IPrivacyPreferenceCondition toTrustPreferenceCondition(
			TrustPreferenceConditionBean bean) {
		// TODO Auto-generated method stub
		try {
			return new TrustPreferenceCondition(TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(bean.getTrustId()), bean.getValue());
		} catch (MalformedTrustedEntityIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static IPrivacyPreferenceCondition toContextPreferenceCondition(
			ContextPreferenceConditionBean bean) {
		// TODO Auto-generated method stub
		try {
			return new ContextPreferenceCondition((CtxAttributeIdentifier) CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(bean.getCtxID()), toOperator(bean.getOperator()), bean.getValue());
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static OperatorConstants toOperator(OperatorConstantsBean bean) {
		if (bean.compareTo(OperatorConstantsBean.EQUALS)==0){
			return OperatorConstants.EQUALS;
		}

		if (bean.compareTo(OperatorConstantsBean.GREATER_OR_EQUAL_THAN)==0){
			return OperatorConstants.GREATER_OR_EQUAL_THAN;
		}

		if (bean.compareTo(OperatorConstantsBean.GREATER_THAN)==0){
			return OperatorConstants.GREATER_THAN;
		}

		if (bean.compareTo(OperatorConstantsBean.LESS_OR_EQUAL_THAN)==0){
			return OperatorConstants.LESS_OR_EQUAL_THAN;
		}

		return OperatorConstants.LESS_THAN;
	}




	/*
	 * FROM OBJECT TO BEAN METHODS
	 */

	public static PPNPrivacyPreferenceTreeModelBean toPPNPrivacyPreferenceTreeModelBean(PPNPrivacyPreferenceTreeModel model){
		PPNPrivacyPreferenceTreeModelBean bean = new PPNPrivacyPreferenceTreeModelBean();
		bean.setDetails(model.getDetails());
		bean.setPref(toPPNPreferenceBean(model.getRootPreference()));

		return bean;
	}

	public static IDSPrivacyPreferenceTreeModelBean toIDSPreferenceTreeModelBean(IDSPrivacyPreferenceTreeModel model){
		IDSPrivacyPreferenceTreeModelBean bean = new IDSPrivacyPreferenceTreeModelBean();
		bean.setDetails(model.getDetails());
		bean.setPref(toIDSPreferenceBean(model.getRootPreference()));


		return bean;
	}

	public static DObfPrivacyPreferenceTreeModelBean toDObfPrivacyPreferenceTreeModelBean(DObfPreferenceTreeModel model){
		DObfPrivacyPreferenceTreeModelBean bean = new DObfPrivacyPreferenceTreeModelBean();
		bean.setDetails(model.getDetails());
		bean.setPref(toDObfPreferenceBean(model.getRootPreference()));

		return bean;
	}

	public static AccessControlPreferenceTreeModelBean toAccessControlPreferenceTreeModelBean(AccessControlPreferenceTreeModel model){
		AccessControlPreferenceTreeModelBean bean = new AccessControlPreferenceTreeModelBean();
		bean.setDetails(model.getDetails());
		bean.setPref(toAccessControlPreferenceBean(model.getPref()));
		return bean;
	}


	public static PPNPreferenceBean toPPNPreferenceBean(
			IPrivacyPreference rootPreference) {
		PPNPreferenceBean bean = new PPNPreferenceBean();


		if  (rootPreference.isLeaf()){
			bean.setOutcome(toPPNPOutcomeBean((PPNPOutcome) rootPreference.getOutcome()));
			return bean;	
		}

		if (rootPreference.isBranch()){
			if (rootPreference.getCondition()!=null){
				bean.setCondition(toConditionBean(rootPreference.getCondition()));
			}
		}


		List<PPNPreferenceBean> beans = new ArrayList<PPNPreferenceBean>();

		Enumeration<PrivacyPreference> children = rootPreference.children();
		while (children.hasMoreElements()){
			beans.add(toPPNPreferenceBean(children.nextElement()));
		}

		bean.setChildren(beans);
		return bean;

	}

	public static IDSPreferenceBean toIDSPreferenceBean(
			IPrivacyPreference rootPreference) {

		if (rootPreference.isLeaf()){
			IDSPreferenceBean bean = new IDSPreferenceBean();
			bean.setOutcome(toIdentitySelectionPreferenceOutcomeBean((IdentitySelectionPreferenceOutcome) rootPreference.getOutcome()));
			return bean;
		}
		IDSPreferenceBean bean = new IDSPreferenceBean();
		if (rootPreference.getCondition()!=null){
			bean.setCondition(toConditionBean(rootPreference.getCondition()));
		}

		ArrayList<IDSPreferenceBean> beans = new ArrayList<IDSPreferenceBean>();

		Enumeration<PrivacyPreference> children = rootPreference.children();

		while(children.hasMoreElements()){
			beans.add(toIDSPreferenceBean(children.nextElement()));
		}

		bean.setChildren(beans);
		return bean;
	}

	public static AccessControlPreferenceBean toAccessControlPreferenceBean(IPrivacyPreference rootPreference){
		if (rootPreference.isLeaf()){
			AccessControlPreferenceBean bean = new AccessControlPreferenceBean();
			bean.setOutcome(toAccessControlOutcomeBean((AccessControlOutcome) rootPreference.getOutcome()));
			return bean;
		}
		AccessControlPreferenceBean bean = new AccessControlPreferenceBean();
		if (rootPreference.getCondition()!=null){
			bean.setCondition(toConditionBean(rootPreference.getCondition()));
		}

		ArrayList<AccessControlPreferenceBean> beans = new ArrayList<AccessControlPreferenceBean>();

		Enumeration<PrivacyPreference> children = rootPreference.children();

		while(children.hasMoreElements()){
			beans.add(toAccessControlPreferenceBean(children.nextElement()));
		}

		bean.setChildren(beans);
		return bean;
	}


	public static DObfPreferenceBean toDObfPreferenceBean(IPrivacyPreference rootPreference){
		DObfPreferenceBean bean = new DObfPreferenceBean();
		if (rootPreference.isLeaf()){
			bean.setOutcome(toDObfOutcomeBean((DObfOutcome) rootPreference.getOutcome()));
			return bean;
		}

		if (rootPreference.isBranch()){
			if(rootPreference.getCondition()!=null){
				bean.setCondition(toConditionBean(rootPreference.getCondition()));
			}
		}

		List<DObfPreferenceBean> beans = new ArrayList<DObfPreferenceBean>();

		Enumeration<PrivacyPreference> children = rootPreference.children();
		while(children.hasMoreElements()){
			beans.add(toDObfPreferenceBean(children.nextElement()));
		}
		bean.setChildren(beans);
		return bean;
	}
	public static PrivacyPreferenceConditionBean toConditionBean(
			IPrivacyPreferenceCondition condition) {
		if (condition instanceof ContextPreferenceCondition){
			return toContextPreferenceConditionBean((ContextPreferenceCondition) condition);
		}else if (condition instanceof PrivacyCondition){
			return toPrivacyConditionBean((PrivacyCondition) condition);
		}

		return toTrustPreferenceConditionBean((TrustPreferenceCondition) condition);
	}

	private static PrivacyConditionBean toPrivacyConditionBean(
			PrivacyCondition condition) {
		PrivacyConditionBean bean = new PrivacyConditionBean();
		bean.setCondition(condition.getCondition());
		return bean;
	}

	public static TrustPreferenceConditionBean toTrustPreferenceConditionBean(
			TrustPreferenceCondition condition) {
		TrustPreferenceConditionBean bean = new TrustPreferenceConditionBean();
		bean.setTrustId(TrustModelBeanTranslator.getInstance().fromTrustedEntityId(condition.getTrustId()));
		bean.setType(PrivacyConditionConstantsBean.TRUST);
		bean.setValue(condition.getTrustThreshold());
		return bean;
	}

	public static ContextPreferenceConditionBean toContextPreferenceConditionBean(
			ContextPreferenceCondition condition) {
		ContextPreferenceConditionBean bean = new ContextPreferenceConditionBean();
		bean.setCtxID((CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(condition.getCtxIdentifier()));
		bean.setOperator(toOperatorConstantsBean(condition.getOperator()));
		bean.setType(PrivacyConditionConstantsBean.CONTEXT);
		bean.setValue(condition.getValue());

		return bean;
	}

	public static OperatorConstantsBean toOperatorConstantsBean(
			OperatorConstants operator) {
		if (operator.compareTo(OperatorConstants.EQUALS)==0){
			return OperatorConstantsBean.EQUALS;
		}

		if (operator.compareTo(OperatorConstants.GREATER_OR_EQUAL_THAN)==0){
			return OperatorConstantsBean.GREATER_OR_EQUAL_THAN;	
		}

		if (operator.compareTo(OperatorConstants.GREATER_THAN)==0){
			return OperatorConstantsBean.GREATER_THAN;
		}

		if (operator.compareTo(OperatorConstants.LESS_OR_EQUAL_THAN)==0){
			return OperatorConstantsBean.LESS_OR_EQUAL_THAN;
		}

		return OperatorConstantsBean.LESS_THAN;

	}

	public static PPNPOutcomeBean toPPNPOutcomeBean(PPNPOutcome outcome) {
		PPNPOutcomeBean bean = new PPNPOutcomeBean();
		bean.setConfidenceLevel(outcome.getConfidenceLevel());
		bean.setDecision(outcome.getDecision());


		return bean;
	}

	public static IDSOutcomeBean toIdentitySelectionPreferenceOutcomeBean(IdentitySelectionPreferenceOutcome outcome){
		IDSOutcomeBean bean = new IDSOutcomeBean();
		bean.setUserIdentity(outcome.getIdentity().getJid());
		bean.setShouldUseIdentity(outcome.isShouldUseIdentity());
		return bean;
	}

	public static DObfOutcomeBean toDObfOutcomeBean(DObfOutcome outcome){
		DObfOutcomeBean bean = new DObfOutcomeBean();
		bean.setConfidenceLevel(outcome.getConfidenceLevel());
		bean.setObfuscationLevel(outcome.getObfuscationLevel());
		bean.setType(PrivacyPreferenceTypeConstantsBean.DATA_OBFUSCATION);
		return bean;
	}

	public static AccessControlOutcomeBean toAccessControlOutcomeBean(AccessControlOutcome outcome){
		AccessControlOutcomeBean bean = new AccessControlOutcomeBean();
		bean.setConfidenceLevel(outcome.getConfidenceLevel());
		bean.setEffect(outcome.getEffect());
		bean.setType(PrivacyPreferenceTypeConstantsBean.ACCESS_CONTROL);
		return bean;

	}
	public static RuleTargetBean toRuleTargetBean(RuleTarget ruleTarget) {
		RuleTargetBean bean = new RuleTargetBean();


		bean.setActions(ActionUtils.toActionBeans(ruleTarget.getActions()));

		bean.setResource(ResourceUtils.toResourceBean(ruleTarget.getResource()));

		bean.setSubjects(RequestorUtils.toRequestorBeans(ruleTarget.getRequestors()));

		return bean;
	}

	public static PrivacyPreferenceTypeConstantsBean toPrivacyPreferenceTypeConstantsBean(
			PrivacyPreferenceTypeConstants outcomeType) {
		if (outcomeType.compareTo(PrivacyPreferenceTypeConstants.PRIVACY_POLICY_NEGOTIATION)==0){
			return PrivacyPreferenceTypeConstantsBean.PRIVACY_POLICY_NEGOTIATION;
		}

		if (outcomeType.compareTo(PrivacyPreferenceTypeConstants.DATA_OBFUSCATION)==0){
			return PrivacyPreferenceTypeConstantsBean.DATA_OBFUSCATION;
		}

		return PrivacyPreferenceTypeConstantsBean.DATA_OBFUSCATION;
	}

	public static PrivacyOutcomeConstantsBean toPrivacyOutcomeConstantsBean(
			PrivacyOutcomeConstants effect) {

		if (effect.compareTo(PrivacyOutcomeConstants.ALLOW)==0){
			return PrivacyOutcomeConstantsBean.ALLOW;
		}

		return PrivacyOutcomeConstantsBean.BLOCK;
	}

	public static boolean equals (PPNPreferenceDetailsBean bean1, Object bean2){
		if (bean1 == bean2) {
			return true;
		}
		if (bean2 == null) {
			return false;
		}
		if (!(bean2 instanceof PPNPreferenceDetailsBean)) {
			return false;
		}
		PPNPreferenceDetailsBean other = (PPNPreferenceDetailsBean) bean2;

		if (bean1.getRequestor() == null) {
			if (other.getRequestor() != null) {
				return false;
			}
		} else if (!RequestorUtils.equals(bean1.getRequestor(), other.getRequestor())){
			return false;
		}
		
		if (bean1.getAction() == null){
			if (other.getAction()!=null){
				return false;
			}
		}else if (!ActionUtils.equals(bean1.getAction(), other.getAction())){
			return false;
		}
		
		if (bean1.getResource() == null) {
			if (other.getResource() != null) {
				return false;
			}
		} else if (!ResourceUtils.equals(bean1.getResource(), other.getResource())){
			return false;
		}
		return true;
	}


	public static boolean equals (AccessControlPreferenceDetailsBean bean1, Object bean2){

		if (bean1 == bean2) {
			return true;
		}
		if (bean2 == null) {
			return false;
		}
		if (!(bean2 instanceof AccessControlPreferenceDetailsBean)) {
			return false;
		}
		AccessControlPreferenceDetailsBean other = (AccessControlPreferenceDetailsBean) bean2;
		if (bean1.getAction() == null) {
			if (other.getAction() != null) {
				return false;
			}
		} else if (!ActionUtils.equals(bean1.getAction(), other.getAction())){
			return false;
		}
		if (bean1.getRequestor() == null) {
			if (other.getRequestor() != null) {
				return false;
			}
		} else if (!RequestorUtils.equals(bean1.getRequestor(), other.getRequestor())){
			return false;
		}
		if (bean1.getResource() == null) {
			if (other.getResource() != null) {
				return false;
			}
		} else if (!ResourceUtils.equals(bean1.getResource(), other.getResource())){
			return false;
		}
		return true;
	}


	public static boolean equals(DObfPreferenceDetailsBean bean1, Object bean2){
		if (bean1 == bean2) {
			return true;
		}
		if (bean2 == null) {
			return false;
		}
		if (!(bean2 instanceof DObfPreferenceDetailsBean)) {
			return false;
		}
		DObfPreferenceDetailsBean other = (DObfPreferenceDetailsBean) bean2;

		if (bean1.getRequestor() == null) {
			if (other.getRequestor() != null) {
				return false;
			}
		} else if (!RequestorUtils.equals(bean1.getRequestor(), other.getRequestor())){
			return false;
		}
		if (bean1.getResource() == null) {
			if (other.getResource() != null) {
				return false;
			}
		} else if (!ResourceUtils.equals(bean1.getResource(), other.getResource())){
			return false;
		}
		return true;
	}

	public static boolean equals(IDSPreferenceDetailsBean bean1, Object bean2){
		if (bean1 == bean2) {
			return true;
		}
		if (bean2 == null) {
			return false;
		}
		if (!(bean2 instanceof IDSPreferenceDetailsBean)) {
			return false;
		}
		IDSPreferenceDetailsBean other = (IDSPreferenceDetailsBean) bean2;
		if (bean1.getAffectedIdentity() == null) {
			if (other.getAffectedIdentity() != null) {
				return false;
			}
		} else if (!bean1.getAffectedIdentity().equals(other.getAffectedIdentity())) {
			return false;
		}
		if (bean1.getRequestor() == null) {
			if (other.getRequestor() != null) {
				return false;
			}
		} else if (!RequestorUtils.equals(bean1.getRequestor(), other.getRequestor())){
			return false;
		}
		return true;
	}

	public static String toString(AccessControlPreferenceDetailsBean bean){
		StringBuilder builder = new StringBuilder();
		builder.append("AccessControlPreferenceDetailsBean [getResource()=");
		builder.append(ResourceUtils.toString(bean.getResource()));
		builder.append(", getRequestor()=");
		builder.append(RequestorUtils.toString(bean.getRequestor()));
		builder.append(", getAction()=");
		builder.append(ActionUtils.toString(bean.getAction()));
		builder.append("]");
		return builder.toString();
	}

	public static String toString(PPNPreferenceDetailsBean bean){
		StringBuilder builder = new StringBuilder();
		builder.append("PPNPreferenceDetailsBean [getResource()=");
		builder.append(ResourceUtils.toString(bean.getResource()));
		builder.append(", getRequestor()=");
		builder.append(RequestorUtils.toString(bean.getRequestor()));
		builder.append("]");
		return builder.toString();
	}
	public static String toString(IDSPreferenceDetailsBean bean){
		StringBuilder builder = new StringBuilder();
		builder.append("IDSPreferenceDetailsBean [getAffectedIdentity()=");
		builder.append(bean.getAffectedIdentity());
		builder.append(", getRequestor()=");
		builder.append(RequestorUtils.toString(bean.getRequestor()));
		builder.append("]");
		return builder.toString();
	}

	public static String toString(DObfPreferenceDetailsBean bean){
		StringBuilder builder = new StringBuilder();
		builder.append("DObfPreferenceDetailsBean [getResource()=");
		builder.append(ResourceUtils.toString(bean.getResource()));
		builder.append(", getRequestor()=");
		builder.append(RequestorUtils.toString(bean.getRequestor()));
		builder.append("]");
		return builder.toString();
	}
}
