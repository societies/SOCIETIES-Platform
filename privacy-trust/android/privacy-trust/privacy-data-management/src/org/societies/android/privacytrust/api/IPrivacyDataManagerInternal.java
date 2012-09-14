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
package org.societies.android.privacytrust.api;

import java.util.List;

import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;


/**
 * Interface internal to privacy components to manage data access control and data access conditions.
 * @author Olivier Maridat (Trialog)
 */
public interface IPrivacyDataManagerInternal {
	/**
	 * Find the relevant permission
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @return The ResponseItem of this permission
	 * @throws PrivacyException
	 */
	public ResponseItem getPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;
	
	/**
	 * Update access control permissions over a data
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @param actions List of actions to request over this data.
	 * @param permission Permission.
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission) throws PrivacyException;
	
	/**
	 * Update access control permissions over a data
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param permission Expression of the permission
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean updatePermission(RequestorBean requestor, ResponseItem permission) throws PrivacyException;
	
	/**
	 * Delete the relevant permission
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean deletePermission(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException;

}