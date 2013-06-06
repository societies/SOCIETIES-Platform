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
package org.societies.api.internal.privacytrust.privacyprotection;

import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

/**
 * Interface exposed to Societies components in order to manage access control over resources
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:26
 * @updated 05-jun.-2013
 */
public interface IPrivacyDataManager {

	/**
	 * Check if a requestor has the permission to perform actions under a personal data
	 * 
	 * @param requestor Requestor of the data. It may be a CSS, or a CSS requesting a data through a 3P service or a CIS.
	 * @param dataId Id of the requested data
	 * @param actions List of actions requested over this data. At least one mandatory action is required.
	 * @return A list of ResponseItem containing privacy permission information: PERMIT or DENY. If the requested data is composed of several data (sub-types), then several ResponseItems are returned, one per data. E.g.: name is composed of firstname and lastname. If only one data is requested, then one ResponseItem is returned. E.g.: books. Some optional actions may be avoided and may not be covered by these permissions. E.g. if READ (mandatory) and WRITE (optional) are requested, this method may return PERMIT only on READ depending of the user's decisions.
	 * @throws PrivacyException if parameters are not correct, or if the privacy layer is not ready
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;
	/**
	 * Will be removed in 1.2
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Deprecated
	public List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> checkPermission(Requestor requestor, DataIdentifier dataId, List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions) throws PrivacyException;

	/**
	 * Check if a requestor has the permission to perform actions under a list of personal data
	 * Duplicate of method @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 * for utility purpose
	 * 
	 * @param requestor Requestor of the data. It may be a CSS, or a CSS requesting a data through a 3P service or a CIS.
	 * @param dataIds List of id of requested data
	 * @param actions List of actions requested over this data. At least one mandatory action is required.
	 * @return A list of ResponseItem containing privacy permission information: PERMIT or DENY. If a requested data is composed of several data (sub-types), then several ResponseItems are returned, one per data. E.g.: name is composed of firstname and lastname. If only one data is requested, then one ResponseItem is returned. E.g.: books. Some optional actions may be avoided and may not be covered by these permissions. E.g. if READ (mandatory) and WRITE (optional) are requested, this method may return PERMIT only on READ depending of the user's decisions.
	 * @throws PrivacyException if parameters are not correct, or if the privacy layer is not ready
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException;
	
	/**
	 * Check if a requestor has the permission to perform actions under a list of personal data
	 * Duplicate of method @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 * for utility purpose
	 * 
	 * @param requestor Requestor of the data. It may be a CSS, or a CSS requesting a data through a 3P service or a CIS.
	 * @param dataId Id of the requested data
	 * @param action Mandatory action requested over this data
	 * @return A list of ResponseItem containing privacy permission information: PERMIT or DENY. If a requested data is composed of several data (sub-types), then several ResponseItems are returned, one per data. E.g.: name is composed of firstname and lastname. If only one data is requested, then one ResponseItem is returned. E.g.: books. Some optional actions may be avoided and may not be covered by these permissions. E.g. if READ (mandatory) and WRITE (optional) are requested, this method may return PERMIT only on READ depending of the user's decisions.
	 * @throws PrivacyException if parameters are not correct, or if the privacy layer is not ready
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, DataIdentifier dataId, Action actions) throws PrivacyException;
	
	/**
	 * Check if a requestor has the permission to perform actions under a list of personal data
	 * Duplicate of method @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 * for utility purpose
	 * 
	 * @param requestor Requestor of the data. It may be a CSS, or a CSS requesting a data through a 3P service or a CIS.
	 * @param dataIds List of id of requested data
	 * @param action Mandatory action requested over this data
	 * @return A list of ResponseItem containing privacy permission information: PERMIT or DENY. If a requested data is composed of several data (sub-types), then several ResponseItems are returned, one per data. E.g.: name is composed of firstname and lastname. If only one data is requested, then one ResponseItem is returned. E.g.: books. Some optional actions may be avoided and may not be covered by these permissions. E.g. if READ (mandatory) and WRITE (optional) are requested, this method may return PERMIT only on READ depending of the user's decisions.
	 * @throws PrivacyException if parameters are not correct, or if the privacy layer is not ready
	 */
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, Action actions) throws PrivacyException;

	/**
	 * Protect a data following the user preferences by obfuscating it to a correct
	 * obfuscation level. The data information are wrapped into a relevant data
	 * wrapper in order to execute the best obfuscation algorithm
	 *
	 * @param requestor Requestor of the ofuscation. It may be a CSS, or a CSS requesting a data through a 3P service, or a CIS.
	 * @param dataWrapper Data wrapped in a relevant data wrapper. @see{org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory} to select the relevant DataWrapper.
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to launch the obfuscation)
	 * @throws PrivacyException if parameters are not correct (especially the data wrapper), or if the privacy layer is not ready
	 */
	public Future<DataWrapper> obfuscateData(RequestorBean requestor, DataWrapper dataWrapper) throws PrivacyException;
}