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

import java.util.concurrent.ExecutionException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;

/**
 * @author Eliza
 *
 */
public class DObfPreferenceCreator {

	final PrivacyPreferenceManager privPrefMgr;
	final ICtxBroker ctxBroker;
	
	public DObfPreferenceCreator(PrivacyPreferenceManager manager){
		this.privPrefMgr = manager;
		this.ctxBroker = this.privPrefMgr.getCtxBroker();
	}
	
	public void createPreference(RequestorBean requestor, Resource resource, double obfuscationLevel){
		
		DObfPreferenceDetailsBean details = new DObfPreferenceDetailsBean();
		details.setRequestor(requestor);
		details.setResource(resource);
		DObfOutcome dobfOutcome = new DObfOutcome(obfuscationLevel);
		PrivacyPreference preference = null;
		
		try {
			
			DataIdentifier dataIdentifier = ResourceUtils.getDataIdentifier(resource);
			if (dataIdentifier.getScheme().equals(DataIdentifierScheme.CONTEXT)){
				if (dataIdentifier instanceof CtxIdentifier){
					CtxModelObject ctxModelObject = this.ctxBroker.retrieve((CtxIdentifier) dataIdentifier).get();
					if (ctxModelObject instanceof CtxAttribute){
						if (!(((CtxAttribute) ctxModelObject).getQuality().getOriginType().equals(CtxOriginType.MANUALLY_SET))){
							String val = ((CtxAttribute) ctxModelObject).getStringValue();
							IPrivacyPreferenceCondition condition = new ContextPreferenceCondition(ctxModelObject.getId(), OperatorConstants.EQUALS, val);
							preference = new PrivacyPreference(condition);
							preference.add(new PrivacyPreference(dobfOutcome));
						}
						
					}
				}
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

		if (preference==null){
			preference = new PrivacyPreference(dobfOutcome);
		}

		DObfPreferenceTreeModel model = new DObfPreferenceTreeModel(details, preference);
		privPrefMgr.storeDObfPreference(details, model);
		
	}
}
