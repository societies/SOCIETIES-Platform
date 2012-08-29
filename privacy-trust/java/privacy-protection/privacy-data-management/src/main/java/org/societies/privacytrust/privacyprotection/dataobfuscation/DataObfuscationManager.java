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

import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.LocationCoordinates;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.PostalLocation;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Status;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Temperature;
import org.societies.api.schema.activity.Activity;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.ActivityObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.LocationCoordinatesObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.NameObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.PostalLocationObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.StatusObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.TemperatureObfuscator;

/**
 * Implementation of IDataObfuscationManager
 * @author Olivier Maridat (Trialog)
 */
public class DataObfuscationManager implements IDataObfuscationManager {
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double)
	 */
	@Override
	public IDataWrapper obfuscateData(IDataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException {
		// TODO : populate this stub function

		// -- Verify params
		// Wrapper ready for obfuscation
		if (!dataWrapper.isReadyForObfuscation()) {
			throw new PrivacyException("This data wrapper is not ready for obfuscation. Data are needed.");
		}
		// Obfuscation level in [0, 1]
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
		IDataObfuscator obfuscator = getDataObfuscator(dataWrapper);

		// -- Obfuscate
		IDataWrapper obfuscatedDataWrapper = null;
		try {
			// - Obfuscation
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
			// - Persistence
			//			if (dataWrapper.isPersistenceEnabled()) {
			// TODO: persiste the obfuscated data using a data broker
			//				System.out.println("Persist the data "+dataWrapper.getDataId());
			//			}
		}
		catch(Exception e) {
			throw new PrivacyException("Obfuscation aborted", e);
		}
		return obfuscatedDataWrapper;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double)
	 */
	@Override
	public IDataWrapper hasObfuscatedVersion(IDataWrapper dataWrapper, double obfuscationLevel) throws PrivacyException {
		// TODO : populate this stub function

		// -- Search obfuscatred version
		//		if (dataWrapper.isPersistenceEnabled()) {
		// TODO: retrieve obfsucated data ID using data broker
		// An obfuscated version exist
		//			if (false) {
		//				System.out.println("Retrieve the persisted data id of data id "+dataWrapper.getDataId());
		//			}
		//		}
		return dataWrapper;
	}

	/**
	 * Retrieve the relevant obfuscator
	 * @param dataWrapper
	 * @return
	 * @throws PrivacyException
	 */
	private IDataObfuscator getDataObfuscator(IDataWrapper dataWrapper) throws PrivacyException {
		IDataObfuscator obfuscator = null;
		if (dataWrapper.getData() instanceof LocationCoordinates) {
			obfuscator = new LocationCoordinatesObfuscator((IDataWrapper<LocationCoordinates>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Name) {
			obfuscator = new NameObfuscator((IDataWrapper<Name>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Temperature) {
			obfuscator = new TemperatureObfuscator((IDataWrapper<Temperature>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Activity) {
			obfuscator = new ActivityObfuscator((IDataWrapper<Activity>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Status) {
			obfuscator = new StatusObfuscator((IDataWrapper<Status>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof PostalLocation) {
			obfuscator = new PostalLocationObfuscator((IDataWrapper<PostalLocation>) dataWrapper);
		}
		else {
			throw new PrivacyException("Obfuscation aborted: no known obfuscator for this type of data");
		}
		return obfuscator;
	}

}
