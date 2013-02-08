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
package org.societies.android.api.internal.privacytrust;

import java.util.List;

import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.DataWrapper;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import android.os.Parcelable;

/**
 * Interface exposed to Societies components in order to manage access control over resources
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:26
 */
public interface IPrivacyDataManager {
	/**
	 * Intent default action: If there is an error, the action name can't be retrieve and this one is used instead.
	 * Must be used in the listening IntentFilter
	 */
	public static final String INTENT_DEFAULT_ACTION = "org.societies.android.privacytrust.datamanagement.DefaultAction";
	/**
	 * Intent field: Return value of the request
	 */
    public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.privacytrust.datamanagement.ReturnValue";
    /**
     * Intent field: Status of the request
     */
    public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.privacytrust.datamanagement.ReturnStatus";
    /**
	 * Intent field: Error description if the request status is failure
	 */
    public static final String INTENT_RETURN_STATUS_MSG_KEY = "org.societies.android.privacytrust.datamanagement.ReturnStatusMsg";

    
	/**
	 * Check permission to access/update/disclose a data
	 * @param clientPackage Client package name
	 * @param requestor Id of the requestor: CSS {@link RequestorBean}, CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean}
	 * @param dataId Id of the requested data
	 * @param action Actions requested over this data
	 * @post The response is available in an Intent: {@link MethodType}::CHECK_PERMISSION. {@link IPrivacyDataManager}INTENT_RETURN_STATUS_KEY contains the status of the request and the meaning of an eventual failure is available in {@link IPrivacyDataManager}::INTENT_RETURN_STATUS_MSG_KEY. {@link IPrivacyDataManager}::INTENT_RETURN_VALUE_KEY contains a {@link ResponseItem}
	 * @throws PrivacyException
	 */
	public void checkPermission(String clientPackage, RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException;

	/**
	 * Protect a data following the user preferences by obfuscating it to a correct
	 * obfuscation level. The data information are wrapped into a relevant data
	 * wrapper in order to execute the relevant obfuscation operation into relevant
	 * information.
	 * @param clientPackage Client package name
	 * @param requestor Id of the requestor: CSS {@link RequestorBean}, CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean}
	 * @param dataWrapper Data Id wrapped in the relevant DataWrapper. Only the Id information is mandatory to retrieve an obfuscated version. Use {@link DataWrapperFactory} to select the relevant {@link DataWrapper}
	 * @post The response is available in an Intent: {@link MethodType}::CHECK_PERMISSION. {@link IPrivacyDataManager}INTENT_RETURN_STATUS_KEY contains the status of the request and the meaning of an eventual failure is available in {@link IPrivacyDataManager}::INTENT_RETURN_STATUS_MSG_KEY. {@link IPrivacyDataManager}::INTENT_RETURN_VALUE_KEY contains a {@link IDataWrapper} wrapping the obfuscated data
	 * @throws PrivacyException
	 */
	public void obfuscateData(String clientPackage, RequestorBean requestor, IDataWrapper<Parcelable> dataWrapper) throws PrivacyException;

	/**
	 * Check if there is an obfuscated version of the data and return its ID.
	 * @param clientPackage Client package name
	 * @param requestor Id of the requestor: CSS {@link RequestorBean}, CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean}
	 * @param dataWrapper Data Id wrapped in the relevant DataWrapper. Only the Id information is mandatory to retrieve an obfuscated version. Use {@link DataWrapperFactory} to select the relevant {@link DataWrapper}
	 * @post The response is available in an Intent: {@link MethodType}::CHECK_PERMISSION. {@link IPrivacyDataManager}INTENT_RETURN_STATUS_KEY contains the status of the request and the meaning of an eventual failure is available in {@link IPrivacyDataManager}::INTENT_RETURN_STATUS_MSG_KEY. {@link IPrivacyDataManager}::INTENT_RETURN_VALUE_KEY contains a {@link DataIdentifier} containing the id of the data to use
	 * @throws PrivacyException
	 */
	public void hasObfuscatedVersion(String clientPackage, RequestorBean requestor, IDataWrapper<Parcelable> dataWrapper) throws PrivacyException;
}