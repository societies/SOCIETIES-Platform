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

import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;


/**
 * Internal interface to protect a data by obfuscating it
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:53
 */
public interface IDataObfuscationManager {
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
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to instantiate the obfuscator)
	 * @throws PrivacyException
	 */
	public DataWrapper obfuscateData(DataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException;

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
	 * @return ID of the obfuscated version of the data if the persistence is enabled and if the obfuscated data exists
	 * @return otherwise ID of the non-obfuscated data
	 * @throws PrivacyException
	 */
	public String hasObfuscatedVersion(DataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException;
}