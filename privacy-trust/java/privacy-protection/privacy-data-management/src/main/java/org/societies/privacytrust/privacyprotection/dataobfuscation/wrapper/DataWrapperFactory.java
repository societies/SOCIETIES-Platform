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
package org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper;

import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;

/**
 * Utilities to instantiate DataWrapper for data obfuscation
 * @state skeleton 
 * @author olivierm
 * @date 14 oct. 2011
 */
public class DataWrapperFactory {
	// -- CONTEXT ATTRIBUTE
	/**
	 * To select automatically the relevant DataWrapper
	 * for this ContextAttribute
	 * 
	 * @param contextData
	 * @return A relevant DataWrapper
	 * @throw RuntimeException
	 */
	public static IDataWrapper selectDataWrapper(Object contextData) throws RuntimeException{
		// TODO : populate this stub function
		try {
			if (contextData.equals("A Data Type")) {
//				return new [A Data Type]Wrapper(data);
			}
			// TODO STUB RETURN, TO BE REMOVED
			return new SampleWrapper(3);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("No Obfuscator to obfuscate that type of data");
		}
//		throw new RuntimeException("No Obfuscator to obfuscate that type of data");
	}
	
//	// -- GEOLOCATION
//	/**
//	 * To get a GeolocaltionDataWrapper
//	 * The persistence is disabled by default, the obfuscated geolocation will not
//	 * be stored after obfuscation.
//	 * @param latitude Latitude
//	 * @param longitude Longitude
//	 * @param accuracy Accuracy in meters
//	 * @return A GeolocationDataWrapper
//	 */
//	public static GeolocationDataWrapper getGeolocationDataWrapper(double latitude, double longitude, double accuracy) {
//		return new GeolocationDataWrapper(latitude, longitude, accuracy);
//	}
}
