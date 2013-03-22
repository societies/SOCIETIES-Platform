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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class MockDataManagerInternal implements IPrivacyDataManagerInternal {

	Hashtable<Integer,ResponseItem> responseItems;
	
	public MockDataManagerInternal() {
		responseItems = new Hashtable<Integer,ResponseItem>();
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)
	 */
	@Override
	public boolean updatePermission(Requestor arg0, ResponseItem responseItem) throws PrivacyException {
		// TODO Auto-generated method stub
		this.responseItems.put(this.responseItems.size(),responseItem);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.schema.identity.DataIdentifier, java.util.List, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision)
	 */
	@Override
	public boolean updatePermission(Requestor arg0, DataIdentifier arg2, List<Action> arg3, Decision arg4)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}

	public ResponseItem getResponseItem(int index){

		if (this.responseItems.containsKey(index)){
			return this.responseItems.get(index);
		}else{
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Override
	public ResponseItem getPermission(Requestor requestor,
			DataIdentifier dataId, List<Action> actions)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermissions(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier)
	 */
	@Override
	public List<ResponseItem> getPermissions(Requestor requestor,
			DataIdentifier dataId) throws PrivacyException {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermissions(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier)
	 */
	@Override
	public boolean deletePermissions(Requestor requestor, DataIdentifier dataId)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Override
	public boolean deletePermission(Requestor requestor, DataIdentifier dataId,
			List<Action> actions) throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}
}
