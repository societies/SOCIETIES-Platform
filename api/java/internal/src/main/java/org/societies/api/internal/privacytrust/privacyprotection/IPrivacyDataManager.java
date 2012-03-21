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



import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * External interface to do actions when using a data.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:26
 */
public interface IPrivacyDataManager {
	/**
	 * Check permission to access/use/disclose a data for a service usage
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data.
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Here this is the CSS_Id of the CSS receiving the request.
	 * @param requestorId The ID of the requestor of the data. Here it is the CSS_Id  of the CSS which creates the Service.
	 * @param serviceId The service_Id the service
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(CtxIdentifier dataId, IIdentity ownerId, IIdentity requestorId, ServiceResourceIdentifier serviceId);

	/**
	 * Check permission to access/use/disclose a data for a CIS usage
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId The ID of the owner of the data. Here it is the CSS_Id of the CSS receiving the request.
	 * @param requestorId The ID of the requestor of the data. Here it is the CSS_Id  of the CSS which creates the CIS.
	 * @param cisId the ID of the CIS which wants to access the data
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(CtxIdentifier dataId, IIdentity ownerId, IIdentity requestorId, IIdentity cisId);

	/**
	 * Check permission to access/use/disclose a data in a case that no negotiation have been done.
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data.
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId ID of the owner of the data. Here it is the CSS_Id of the CSS receiving the request.
	 * @param requestorId ID of the requestor of the data. Here it is the CSS_Id of the CSS which request the data.
	 * @param usage Information about the use of the data: purpose, retention-time, people who will access this data... Need to be formalised!
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(CtxIdentifier dataId, IIdentity ownerId, IIdentity requestorId, RequestPolicy usage);
	/**
	 * Protect a data following the user preferences by obfuscating it to a correct
	 * obfuscation level. The data information are wrapped into a relevant data
	 * wrapper in order to execute the relevant obfuscation operation into relevant
	 * information.
	 * Example of use:
	 * - Context Broker, to obfuscate context data (e.g. obfuscate a
	 * location)
	 * - Content Manager, to obfuscate content data (e.g. blur faces in a
	 * picture)
	 * - Anyone who wants to obfuscate a data
	 * @param dataWrapper Data wrapped in a relevant data wrapper. Use DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1. With 0 there is no obfuscation
	 * @param listener A listener to receive the result
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to instantiate the obfuscator)
	 * @throws Exception
	 */
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException;

	/**
	 * Check if there is an obfuscated version of the data and return its ID.
	 * Example of use:
	 * - Context Broker, before retrieving the data, it can try to find an already
	 * obfuscated data and retrieve it instead of the real data. Not all obfuscated
	 * data are stored to be reused, but it may be in some cases. (e.g. long
	 * processing like blur faces in a picture)
	 * - Content Manager, same usage
	 * - Anyone who wants to obfuscate a data
	 * @param dataWrapper Data ID wrapped in the relevant DataWrapper. Only the ID information is mandatory to retrieve an obfuscated version. Use DataWrapperFactory to select the relevant DataWrapper
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1. With 0 there is no obfuscation
	 * @param listener A listener to receive the result
	 * @return ID of the obfuscated version of the data if the persistence is enabled and if the obfuscated data exists
	 * @return otherwise ID of the non-obfuscated data
	 * @throws Exception
	 */
	public CtxIdentifier hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException;
}