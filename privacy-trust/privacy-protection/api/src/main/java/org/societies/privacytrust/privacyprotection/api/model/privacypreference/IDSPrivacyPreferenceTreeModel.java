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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;

import java.io.Serializable;

import javax.swing.tree.DefaultTreeModel;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference.constants.PrivacyPreferenceTypeConstants;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;

/**
 * This class is used to represent a privacy preference for identity selection. This class represents a node in a tree. 
 * If the node is a branch, then the embedded object of the node is a condition (IPrivacyPreferenceCondition), otherwise, 
 * if it's a leaf, the embedded object is an IdentitySelectionPreferenceOutcome.
 * @author Elizabeth
 *
 */
public class IDSPrivacyPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable {

	
	private EntityIdentifier affectedDPI;
	private EntityIdentifier serviceDPI;
	private ServiceResourceIdentifier serviceID;
	private PrivacyPreferenceTypeConstants myPrivacyType;
	private IPrivacyPreference pref;
	
	public IDSPrivacyPreferenceTreeModel(EntityIdentifier affectedDPI,  IPrivacyPreference preference){
		super(preference);
		this.setAffectedDPI(affectedDPI);
		this.myPrivacyType = PrivacyPreferenceTypeConstants.IDS;
		this.pref = preference;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceTreeModel#getPrivacyType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getPrivacyType() {
		return this.getPrivacyType();
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceTreeModel#getRootPreference()
	 */
	@Override
	public IPrivacyPreference getRootPreference() {
		return this.pref;
	}


	public void setAffectedDPI(EntityIdentifier affectedDPI) {
		this.affectedDPI = affectedDPI;
	}


	public EntityIdentifier getAffectedDPI() {
		return affectedDPI;
	}


	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}


	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}


	public void setServiceDPI(EntityIdentifier serviceDPI) {
		this.serviceDPI = serviceDPI;
	}


	public EntityIdentifier getServiceDPI() {
		return serviceDPI;
	}

}

