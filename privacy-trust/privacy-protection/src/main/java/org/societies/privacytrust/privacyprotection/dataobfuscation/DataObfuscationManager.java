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

import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;

/**
 * Implementation of IDataObfuscationManager
 * @state skeleton 
 * @author olivierm
 */
public class DataObfuscationManager implements IDataObfuscationManager {
	@Override
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException {
		// TODO : populate this stub function
		
		// -- Verify params
		if (null == dataWrapper) {
			PrivacyException e = new PrivacyException("No data to obfuscate");
			if (null != listener) {
				listener.onObfuscationAborted("No data to obfuscate", e);
			}
			return null;
		}
		if (dataWrapper instanceof DataWrapper && null == ((DataWrapper) dataWrapper).getObfuscator()) {
			PrivacyException e = new PrivacyException("Not enought information in the wrapper to obfuscate this data");
			if (null != listener) {
				listener.onObfuscationAborted("Not enought information in the wrapper to obfuscate this data", e);
			}
			return null;
		}
		
		// -- Obfuscate
		IDataWrapper obfuscatedDataWrapper = null;
		// Check if it is ready for obfuscation
		if (!dataWrapper.isReadyForObfuscation()) {
			PrivacyException e = new PrivacyException("This data wrapper is not ready for obfuscation. Data are needed.");
			listener.onObfuscationAborted("This data wrapper is not ready for obfuscation. Data are needed.", e);
			return null;
		}
		try {
			// Obfuscation
			obfuscatedDataWrapper = ((DataWrapper) dataWrapper).getObfuscator().obfuscateData(obfuscationLevel, listener);
			// Persistence
			if (dataWrapper.isPersistenceEnabled()) {
				// TODO: persiste the obfuscated data using a data broker
//				System.out.println("Persist the data "+dataWrapper.getDataId());
			}
			// Call listener
			listener.onObfuscationDone(obfuscatedDataWrapper);
		}
		catch(Exception e) {
			listener.onObfuscationAborted("Obfuscation aborted", e);
		}
		return obfuscatedDataWrapper;
	}

	@Override
	public DataIdentifier hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel, IDataObfuscationListener listener) throws PrivacyException {
		// TODO : populate this stub function
		
		// -- Verify params
		if (null == dataWrapper) {
			PrivacyException e = new PrivacyException("No data: so, we can't search obfuscated version");
			if (null != listener) {
				listener.onObfuscationAborted("No data: so, we can't search obfuscated version", e);
			}
			return null;
		}

		
		// -- Search obfuscatred version
		if (dataWrapper.isPersistenceEnabled()) {
			// TODO: retrieve obfsucated data ID using data broker
			// An obfuscated version exist
			if (false) {
//				System.out.println("Retrieve the persisted data id of data id "+dataWrapper.getDataId());
			}
		}
		// There is no obfuscated version
		listener.onObfuscatedVersionRetrieved(dataWrapper.getDataId(), false);
		return dataWrapper.getDataId();
	}

}
