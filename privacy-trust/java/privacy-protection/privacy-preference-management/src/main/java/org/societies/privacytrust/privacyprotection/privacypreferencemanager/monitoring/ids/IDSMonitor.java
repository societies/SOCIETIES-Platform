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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.ids;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation.PreferenceConditionExtractor;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class IDSMonitor implements CtxChangeEventListener {
	private final PrivacyPreferenceManager privPrefMgr;
	Hashtable<CtxIdentifier, List<IDSPreferenceDetailsBean>> monitoring = new Hashtable<CtxIdentifier, List<IDSPreferenceDetailsBean>>();
	private final ICtxBroker ctxBroker;
	private final IIdentity userIdentity;

	public IDSMonitor(IIdentity userIdentity, ICtxBroker ctxBroker, PrivacyPreferenceManager privPrefMgr) {
		this.userIdentity = userIdentity;
		this.ctxBroker = ctxBroker;
		this.privPrefMgr = privPrefMgr;
		loadPreferenceDetails();
		registerForEvents();
	}
	
	
	private void registerForEvents() {
		Enumeration<CtxIdentifier> ctxIdEnum = monitoring.keys();
		
		while(ctxIdEnum.hasMoreElements()){
			try {
				ctxBroker.registerForChanges(this, ctxIdEnum.nextElement());
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void loadPreferenceDetails(){
		List<IDSPreferenceDetailsBean> details = privPrefMgr.getIDSPreferenceDetails();

		for (IDSPreferenceDetailsBean detail: details){
			PreferenceConditionExtractor extractor = new PreferenceConditionExtractor();
			IPrivacyPreferenceTreeModel model = privPrefMgr.getIDSPreference(detail);
			if (null!=model){
				List<CtxIdentifier> ctxIds = extractor.extractConditions(model);
				for (CtxIdentifier ctxId: ctxIds){
					if (monitoring.containsKey(ctxId)){
						monitoring.get(ctxId).add(detail);
					}else{
						ArrayList<IDSPreferenceDetailsBean> list = new ArrayList<IDSPreferenceDetailsBean>();
						list.add(detail);
						monitoring.put(ctxId, list);

					}
				}
			}

		}
	}



	@Override
	public void onModification(CtxChangeEvent event) {
		//TODO: for second trial. instruct IdentitySelection to change identities. 
		
	}
	
	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
