/**
 * Copyright (c) 2011, SOCIETIES Consortium
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

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

/**
 * This class represents a tree model for Privacy Policy Negotiation Preferences and encapsulates a tree of IPrivacyPreference objects.
 * @author Elizabeth
 *
 */
public class PPNPrivacyPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable {

	
	private ICtxAttributeIdentifier affectedCtxId;
	private String myContextType;
	private EntityIdentifier providerDPI;
	private ServiceResourceIdentifier serviceID;
	private PrivacyPreferenceTypeConstants myPrivacyType;
	private IPrivacyPreference pref;
	
	public PPNPrivacyPreferenceTreeModel(String myCtxType, IPrivacyPreference preference){
		super(preference);
		this.myContextType = myCtxType;
		this.myPrivacyType = PrivacyPreferenceTypeConstants.PPNP;
		this.pref = preference;
	}
	
	public ICtxAttributeIdentifier getAffectedContextIdentifier() {
		return this.getAffectedCtxId();
	}

	
	public String getContextType() {
		return this.myContextType;
	}


	@Override
	public PrivacyPreferenceTypeConstants getPrivacyType() {
		return this.myPrivacyType;
	}


	@Override
	public IPrivacyPreference getRootPreference() {
		return this.pref;
	}

	public void setAffectedCtxId(ICtxAttributeIdentifier affectedCtxId) {
		this.affectedCtxId = affectedCtxId;
	}

	public ICtxAttributeIdentifier getAffectedCtxId() {
		return affectedCtxId;
	}

	public void setProviderDPI(EntityIdentifier providerDPI) {
		this.providerDPI = providerDPI;
	}

	public EntityIdentifier getProviderDPI() {
		return providerDPI;
	}

	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}

}
