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
package org.societies.personalisation.UserPreferenceManagement.impl;

import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.preference.api.UserPreferenceManagement.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;

public class UserPreferenceManagement implements IUserPreferenceManagement{

	public UserPreferenceManagement(){
		System.out.println("Hello! I'm the User Preference Manager!");
	}
	@Override
	public void deletePreference(EntityIdentifier ownerID,
			PreferenceDetails details) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePreference(EntityIdentifier ownerID, String serviceType,
			IServiceResourceIdentifier serviceID, String preferenceName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<CtxAttributeIdentifier> getConditions(EntityIdentifier ownerID,
			IOutcome outcome) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceTreeModel getModel(EntityIdentifier ownerID,
			String serviceType, IServiceResourceIdentifier serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceTreeModel getModel(EntityIdentifier ownerID,
			PreferenceDetails details) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceOutcome getPreference(EntityIdentifier ownerID,
			String serviceType, IServiceResourceIdentifier serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		return new PreferenceOutcome("volume", "0");
		
	}

	@Override
	public List<IPreferenceConditionIOutcomeName> getPreferenceConditions(
			EntityIdentifier ownerID, String serviceType,
			IServiceResourceIdentifier serviceID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxAttributeIdentifier> getPreferenceConditions(
			EntityIdentifier ownerID, String serviceType,
			IServiceResourceIdentifier serviceID, String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IOutcome> reEvaluatePreferences(EntityIdentifier userId,
			CtxAttribute attr, List<PreferenceDetails> preferenceIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOutcome reEvaluatePreferences(EntityIdentifier ownerID,
			CtxAttribute attr, String serviceType,
			IServiceResourceIdentifier serviceID, String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePreference(EntityIdentifier ownerID, String serviceType,
			IServiceResourceIdentifier serviceID, String preferenceName,
			IPreference preference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePreference(EntityIdentifier ownerID,
			PreferenceDetails details, IPreference preference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceived(CtxModelObject ctxModelObj) {
		// TODO Auto-generated method stub
		
	}

}
