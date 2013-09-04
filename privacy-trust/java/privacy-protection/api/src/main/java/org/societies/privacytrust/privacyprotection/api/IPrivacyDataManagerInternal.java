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
package org.societies.privacytrust.privacyprotection.api;

import java.util.List;

import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * Interface internal to privacy components to manage data access control and data access conditions.
 * @author Olivier Maridat (Trialog)
 */
public interface IPrivacyDataManagerInternal {
	/**
	 * Retrieve the relevant permissions for these data identifier and these actions
	 * 
	 * @param requestor Requestor of the data. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId List of ids of the requested data.
	 * @param actions List of actions requested over these data
	 * @return The list of available privacy permissions for each data (if any). Most relevant permissions (as many actions as possible) will be returned.
	 * @throws PrivacyException if wrong parameters or storage issue
	 */
	public List<ResponseItem> getPermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException;

	/**
	 * Retrieve the relevant permissions
	 * Duplication of methods {@link #getPermissions(RequestorBean, List, List)} for utility purpose
	 * @see IPrivacyDataManagerInternal#getPermissions(RequestorBean, List, List)
	 */
	public List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;

	/**
	 * Retrieve the relevant permissions for this data identifier
	 * Duplication of methods {@link #getPermissions(RequestorBean, List, List)} for utility purpose
	 * @see IPrivacyDataManagerInternal#getPermissions(RequestorBean, List, List)
	 */
	public List<ResponseItem> getPermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#getPermissions(RequestorBean, DataIdentifier)
	 */
	@Deprecated
	public List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> getPermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#getPermissions(RequestorBean, DataIdentifier, List)
	 */
	@Deprecated
	public List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> getPermissions(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions) throws PrivacyException;

	/**
	 * Update access control permission over a data
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @param actions List of actions to request over this data.
	 * @param decision Permission decision for this data.
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision decision) throws PrivacyException;

	/**
	 * Update access control permissions over a data
	 * Duplication of methods {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)} for utility purpose
	 * @pre The same decision will be applied to all data ids
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @param actions List of actions to request over this data.
	 * @param decision Permission decision to apply to each data
	 * @return Success of the operation
	 * @throws PrivacyException
	 * @see {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)}
	 */
	public boolean updatePermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, Decision decision) throws PrivacyException;
	
	/**
	 * Update access control permissions over a data
	 * Duplication of methods {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)} for utility purpose
	 * @pre dataIds and decisions have the same size
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Generally the local CSS Id.
	 * @param actions List of actions to request over this data.
	 * @param decisions Permission decision for each data.
	 * @return Success of the operation
	 * @throws PrivacyException
	 * @see {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)}
	 */
	public boolean updatePermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, List<Decision> decisions) throws PrivacyException;

	/**
	 * Update access control permissions over a data
	 * Duplication of methods {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)} for utility purpose
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param permission Expression of the permission
	 * @return Success of the operation
	 * @throws PrivacyException
	 * @see {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)}
	 */
	public boolean updatePermission(RequestorBean requestor, ResponseItem permission) throws PrivacyException;

	/**
	 * Update access control permissions over a data
	 * Duplication of methods {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)} for utility purpose
	 * 
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param permissions List of permissions
	 * @return Success of the operation
	 * @throws PrivacyException
	 * @see {@link #updatePermission(RequestorBean, DataIdentifier, List, Decision)}
	 */
	public boolean updatePermissions(RequestorBean requestor, List<ResponseItem> permissions) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#updatePermissions(RequestorBean, List)
	 */
	@Deprecated
	public boolean updatePermissions(Requestor requestor, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem> permissions) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#updatePermission(RequestorBean, ResponseItem)
	 */
	@Deprecated
	public boolean updatePermission(Requestor requestor, org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem permission) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#updatePermission(RequestorBean, DataIdentifier, List, Decision)
	 */
	@Deprecated
	public boolean updatePermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions, org.societies.api.privacytrust.privacy.model.privacypolicy.Decision permission) throws PrivacyException;


	/**
	 * Delete the relevant permissions
	 * 
	 * @param requestor Requestor of the obfuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId Id of the requested data.
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException;
	/**
	 * Delete the relevant permissions
	 * 
	 * @param requestor Requestor of the obfuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataId Id of the requested data.
	 * @param actions List of actions
	 * @return Success of the operation
	 * @throws PrivacyException
	 */
	public boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;
	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#updatePermissions(RequestorBean, List)
	 */
	@Deprecated
	public boolean deletePermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException;

	/**
	 * Will be removed in R1.2
	 * @see IPrivacyDataManagerInternal#updatePermissions(RequestorBean, List)
	 */
	@Deprecated
	public boolean deletePermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions) throws PrivacyException;

}