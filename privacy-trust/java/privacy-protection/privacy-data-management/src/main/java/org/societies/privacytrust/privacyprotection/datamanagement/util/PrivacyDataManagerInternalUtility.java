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
package org.societies.privacytrust.privacyprotection.datamanagement.util;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;

/**
 * Only contains duplicated methods for utility purposes
 * @author Olivier Maridat (Trialog)
 */
public abstract class PrivacyDataManagerInternalUtility extends PrivacyDataManagerInternalDeprecation implements IPrivacyDataManagerInternal {
	@Override
	public List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		dataIds.add(dataId);
		return getPermissions(requestor, dataIds, actions);
	}

	@Override
	public List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException {
		return getPermissions(requestor, dataId, null);
	}

	public abstract List<ResponseItem> getPermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException;



	@Override
	public boolean updatePermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, Decision decision) throws PrivacyException {
		if (null == dataIds || null == decision || dataIds.size() <= 0) {
			return false;
		}

		boolean res = true;
		for(int i=0; i<dataIds.size(); i++) {
			res &= updatePermission(requestor, dataIds.get(i), actions, decision);
		}
		return res;
	}
	
	@Override
	public boolean updatePermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, List<Decision> decisions) throws PrivacyException {
		if (null == dataIds || null == decisions || dataIds.size() != decisions.size()) {
			return false;
		}

		boolean res = true;
		for(int i=0; i<dataIds.size(); i++) {
			res &= updatePermission(requestor, dataIds.get(i), actions, decisions.get(i));
		}
		return res;
	}
	
	@Override
	public boolean updatePermission(RequestorBean requestor, ResponseItem permission) throws PrivacyException {
		DataIdentifier dataId;
		try {
			dataId = ResourceUtils.getDataIdentifier(permission.getRequestItem().getResource());
		} catch (MalformedCtxIdentifierException e) {
			throw new PrivacyException("Can't retrieve the data id", e);
		}
		return this.updatePermission(requestor, dataId, permission.getRequestItem().getActions(), permission.getDecision());
	}

	@Override
	public boolean updatePermissions(RequestorBean requestor, List<ResponseItem> permissions) throws PrivacyException {
		boolean res = true;
		for (ResponseItem permission : permissions) {
			res &= updatePermission(requestor, permission);
		}
		return res;
	}

	public abstract boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission) throws PrivacyException;


	@Override
	public boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException {
		return deletePermissions(requestor, dataId, null);
	}

	public abstract boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;
}