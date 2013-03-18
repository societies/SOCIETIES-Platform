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
package org.societies.android.api.internal.privacytrust.privacy.util.dataobfuscation;

import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.CtxAttributeTypes;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;


/**
 * Utilities to instantiate DataWrapper for data obfuscation
 * @author Olivier Maridat (Trialog)
 * @date 14 oct. 2011
 */
public class DataWrapperFactory {
	// -- CONTEXT ATTRIBUTE

	

	// -- GEOLOCATION
	/**
	 * To get a LocationCoordinatesWrapper
	 * The persistence is disabled by default, the obfuscated geolocation will not
	 * be stored after obfuscation.
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @param accuracy Accuracy in meters
	 * @return A LocationCoordinatesWrapper
	 */
	public static DataWrapper getLocationCoordinatesWrapper(double latitude, double longitude, double accuracy) {
		String dataType = CtxAttributeTypes.LOCATION_COORDINATES;
		LocationCoordinates data = LocationCoordinatesUtils.create(latitude, longitude, accuracy);
		return DataWrapperUtils.create(dataType, data);
	}
	public static LocationCoordinates retrieveLocationCoordinates(DataWrapper dataWrapper) {
		String dataType = CtxAttributeTypes.LOCATION_COORDINATES;
		if (!dataType.equals(dataWrapper.getDataType())) {
			return null;
		}
		return (LocationCoordinates) dataWrapper.getData();
	}

	// -- NAME
	/**
	 * To get a NameWrapper
	 * The persistence is disabled by default, the obfuscated name will not
	 * @param firstName
	 * @param lastName
	 * @return the NameWrapper
	 */
	public static DataWrapper getNameWrapper(String firstName, String lastName) {
		String dataType = CtxAttributeTypes.NAME;
		Name data = NameUtils.create(firstName, lastName);
		return DataWrapperUtils.create(dataType, data);
	}
	public static Name retrieveName(DataWrapper dataWrapper) {
		String dataType = CtxAttributeTypes.NAME;
		if (!dataType.equals(dataWrapper.getDataType())) {
			return null;
		}
		return (Name) dataWrapper.getData();
	}
}
