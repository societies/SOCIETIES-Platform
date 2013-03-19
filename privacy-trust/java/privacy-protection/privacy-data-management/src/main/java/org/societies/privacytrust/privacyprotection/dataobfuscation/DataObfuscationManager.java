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
package org.societies.privacytrust.privacyprotection.dataobfuscation;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.dataobfuscation.IDataObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.util.ObfuscatorFactory;

/**
 * Implementation of IDataObfuscationManager
 * @author Olivier Maridat (Trialog)
 */
public class DataObfuscationManager implements IDataObfuscationManager {

	@Override
	public DataWrapper obfuscateData(DataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException {
		// -- Verify params
		// Obfuscation level in ]0, 1]
		if (obfuscationLevel > 1) {
			obfuscationLevel = 1;
		}
		if (obfuscationLevel < 0) {
			obfuscationLevel = 0.000001;
		}
		// Return directly if obfuscation level is 1
		if (1 == obfuscationLevel) {
			return dataWrapper;
		}


		// -- Mapping: retrieve the relevant obfuscator
		IDataObfuscator obfuscator = ObfuscatorFactory.getDataObfuscator(dataWrapper);

		// -- Obfuscate
		DataWrapper obfuscatedDataWrapper = null;
		try {
			// - Obfuscation
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
			// - Persistence
			if (obfuscator.getObfuscatorInfo().isPersistable()) {
				persistDataWrapper(obfuscatedDataWrapper);
			}
		}
		catch(Exception e) {
			throw new PrivacyException("Obfuscation aborted", e);
		}
		return obfuscatedDataWrapper;
	}

	@Override
	public DataWrapper hasObfuscatedVersion(DataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException {
		return dataWrapper;
	}

	private DataIdentifier persistDataWrapper(DataWrapper dataWrapper) {
		return null;
	}
}
