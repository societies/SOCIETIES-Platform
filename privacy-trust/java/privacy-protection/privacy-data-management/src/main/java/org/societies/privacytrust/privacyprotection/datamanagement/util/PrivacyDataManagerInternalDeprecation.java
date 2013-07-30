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

import java.util.List;

import org.societies.api.identity.Requestor;
import org.societies.api.identity.SimpleDataIdentifier;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.DecisionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;

/**
 * Only contains deprecated methods that will be removed in future releases
 * This class is inherited by the up to date implementation of IPrivacuDataManagerInternal
 * 
 * @author Olivier Maridat (Trialog)
 */
public abstract class PrivacyDataManagerInternalDeprecation implements IPrivacyDataManagerInternal {
	@Deprecated
	@Override
	public List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> getPermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
		return ResponseItemUtils.toResponseItems(getPermissions(RequestorUtils.toRequestorBean(requestor), dataId));
	}

	@Deprecated
	@Override
	public List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> getPermissions(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions) throws PrivacyException {
		return this.getPermissions(RequestorUtils.toRequestorBean(requestor), dataId, ActionUtils.toActionBeans(actions));
	}
	
	public abstract List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException;
	
	public abstract List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;

	
	@Deprecated
	@Override
	public boolean updatePermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions, org.societies.api.privacytrust.privacy.model.privacypolicy.Decision permission) throws PrivacyException {
		return this.updatePermission(RequestorUtils.toRequestorBean(requestor), dataId, ActionUtils.toActionBeans(actions), DecisionUtils.toDecisionBean(permission));
	}

	@Deprecated
	@Override
	public boolean updatePermission(Requestor requestor, org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem permission) throws PrivacyException {
		DataIdentifier dataId;
		// Data id
		if (null != permission.getRequestItem().getResource().getDataId()) {
			dataId = permission.getRequestItem().getResource().getDataId();
		}
		// Data type only
		else if (null != permission.getRequestItem().getResource().getDataType() && !"".equals(permission.getRequestItem().getResource().getDataType())) {
			dataId = new SimpleDataIdentifier();
			dataId.setType(permission.getRequestItem().getResource().getDataType());
			dataId.setScheme(permission.getRequestItem().getResource().getScheme());
		}
		else {
			throw new PrivacyException("[Parameters] DataId or DataType is missing");
		}
		return this.updatePermission(requestor, dataId, permission.getRequestItem().getActions(), permission.getDecision());
	}

	@Deprecated
	@Override
	public boolean updatePermissions(Requestor requestor, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> permissions1) throws PrivacyException {
		List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> permissions = ResponseItemUtils.toResponseItems(permissions1);
		boolean res = true;
		for (org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem permission : permissions) {
			res &= updatePermission(requestor, permission);
		}
		return res;
	}
	
	public abstract boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission) throws PrivacyException;

	
	@Deprecated
	@Override
	public boolean deletePermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
		return deletePermissions(RequestorUtils.toRequestorBean(requestor), dataId);
	}

	@Deprecated
	@Override
	public boolean deletePermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions) throws PrivacyException {
		return deletePermissions(RequestorUtils.toRequestorBean(requestor), dataId, ActionUtils.toActionBeans(actions));
	}
	
	public abstract boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException;
	
	public abstract boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;
}
