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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceEvaluator;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PrivateContextCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.management.PrivatePreferenceCache;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging.DObfPreferenceCreator;

/**
 * @author Eliza
 *
 */
public class DObfPrivacyPreferenceManager {

	
	private PrivatePreferenceCache prefCache;
	private final PrivateContextCache contextCache;
	private ITrustBroker trustBroker;
	
	public DObfPrivacyPreferenceManager(PrivatePreferenceCache prefCache, PrivateContextCache contextCache, ITrustBroker trustBroker){
		this.prefCache = prefCache;
		this.contextCache = contextCache;
		this.trustBroker = trustBroker;
		
	}
	/**
	 * new methods;
	 */
	public boolean deleteDObfPreference(DObfPreferenceDetailsBean details) {
		return this.prefCache.removeDObfPreference(details);

	}

	
	public double evaluateDObfPreference(DObfPreferenceDetailsBean details) {
		DObfPreferenceTreeModel model = this.prefCache.getDObfPreference(details);
		if (model!=null){
			IPrivacyOutcome outcome = evaluatePreference(model.getRootPreference());
			if (outcome instanceof DObfOutcome){
				return ((DObfOutcome) outcome).getObfuscationLevel();
			}else{
				return -1;
			}
		}else{
			return -1;
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
	public DObfPreferenceTreeModel getDObfPreference(
			DObfPreferenceDetailsBean details) {
		return this.prefCache.getDObfPreference(details);
	}

	public List<DObfPreferenceDetailsBean> getDObfPreferenceDetails() {
		return this.prefCache.getDObfPreferenceDetails();
	}

	public boolean storeDObfPreference(DObfPreferenceDetailsBean details,
			DObfPreferenceTreeModel model) throws PrivacyException {
		if(model.getDetails().equals(details)){
			return this.prefCache.addDObfPreference(details, model);
		}
		
		throw new PrivacyException("DObfPreferenceDetailsBean parameter did not match DObfPrivacyPreferenceTreeModel.getDetails()");		
	}

}
