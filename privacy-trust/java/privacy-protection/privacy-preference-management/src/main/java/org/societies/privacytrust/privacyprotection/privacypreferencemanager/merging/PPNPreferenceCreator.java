/** 
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

/**
 * @author Eliza
 *
 */
public class PPNPreferenceCreator {


	private PrivacyPreferenceManager privPrefMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public PPNPreferenceCreator(PrivacyPreferenceManager privacyPreferenceManager) {
		this.privPrefMgr = privacyPreferenceManager;

	}

	public void createPPNPreferencesFromAgreement(Agreement agreement){
		RequestorBean requestor = agreement.getRequestor();

		List<ResponseItem> requestedItems = agreement.getRequestedItems();
		for (ResponseItem item : requestedItems){
			List<Action> actions = item.getRequestItem().getActions();
			for (Action action: actions){
				PPNPreferenceDetailsBean detailsBean = new PPNPreferenceDetailsBean();
				detailsBean.setResource(item.getRequestItem().getResource());
				detailsBean.setAction(action);
				processMerge(item, detailsBean);
				
				PPNPreferenceDetailsBean detailsBeanWithRequestor = new PPNPreferenceDetailsBean();
				detailsBeanWithRequestor.setResource(item.getRequestItem().getResource());
				detailsBeanWithRequestor.setRequestor(requestor);
				detailsBeanWithRequestor.setAction(action);
				processMerge(item, detailsBeanWithRequestor);
				
			}
		}
	}

	private void processMerge(ResponseItem item, PPNPreferenceDetailsBean detailsBean) {

			PPNPrivacyPreferenceTreeModel existingPpnPreference = this.privPrefMgr.getPPNPreference(detailsBean);
			List<Condition> conditions = item.getRequestItem().getConditions();

			PPNPOutcome outcome = new PPNPOutcome(item.getDecision());
			IPrivacyPreference preference = createConditionPreferences(conditions, new PrivacyPreference(outcome)); 
			
			if (existingPpnPreference==null){
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(detailsBean, preference);
				this.privPrefMgr.storePPNPreference(detailsBean, model);
				
			}else{
				PrivacyPreferenceMerger merger = new PrivacyPreferenceMerger(privPrefMgr);
				
				IPrivacyPreference merged = merger.mergePPNPreference(existingPpnPreference.getRootPreference(), preference);
				PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(detailsBean, merged);
			}


		



	}

	

	private PrivacyPreference createConditionPreferences(
			List<Condition> conditions, PrivacyPreference privacyPreference) {

		for (Condition condition : conditions){
			this.logging.debug("Adding condition to accCtrlPreference: "+ConditionUtils.toString(condition));
			PrivacyPreference preference = new PrivacyPreference(new PrivacyCondition(condition));
			preference.add(privacyPreference);
			privacyPreference = (PrivacyPreference) preference.getRoot();
		}
		return privacyPreference;
	}


}
