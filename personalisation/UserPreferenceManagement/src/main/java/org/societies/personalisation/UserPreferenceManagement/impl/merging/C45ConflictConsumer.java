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
package org.societies.personalisation.UserPreferenceManagement.impl.merging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.preference.api.model.IC45Consumer;
import org.societies.personalisation.preference.api.model.IC45Output;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;

/**
 * 
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class C45ConflictConsumer implements IC45Consumer{

	private ServiceResourceIdentifier serviceID;
	private String parameterName;
	private IIdentity userId;
	private UserPreferenceManagement prefMgr;
	private String serviceType;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public C45ConflictConsumer(IIdentity userID, String serviceType, ServiceResourceIdentifier id, String parameterName, UserPreferenceManagement prefMgr){
		this.userId = userID;
		this.parameterName = parameterName;
		this.serviceType = serviceType;
		this.serviceID = id;
		this.prefMgr = prefMgr;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.preference.api.model.IC45Consumer#handleC45Output(java.util.List)
	 */
	@Override
	public void handleC45Output(List<IC45Output> outputList) {
		IPreferenceTreeModel tree = null;
		
		for (IC45Output c45 : outputList){
			if (c45.getOwner().equals(this.userId) && c45.getServiceId().equals(serviceID)){
				List<IPreferenceTreeModel> tList = c45.getTreeList();
				for (IPreferenceTreeModel t :tList){
					if (t.getPreferenceDetails().getPreferenceName().equalsIgnoreCase(this.parameterName)){
						tree = t;
					}
				}
			}
		}
		
		if (tree!=null){
			//IPreference preference =  new TreeConverter().convertToPreferenceTree(tree.getRoot());
			PreferenceDetails detail = new PreferenceDetails(this.serviceType, this.serviceID, this.parameterName);
			this.prefMgr.storePreference(this.userId, detail, tree.getRootPreference());
		}
		//add confidence levels (cast the leaves of the tree to IOutcome)
		//store in DB using the preference manager
	}

}
