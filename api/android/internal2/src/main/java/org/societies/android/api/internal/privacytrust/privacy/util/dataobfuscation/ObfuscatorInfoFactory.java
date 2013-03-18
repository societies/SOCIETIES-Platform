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

import java.util.HashMap;
import java.util.Map;

import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.CtxAttributeTypes;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.LocationCoordinatesObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.NameObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.ObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.PostalLocationObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.StatusObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.TemperatureObfuscatorInfo;
import org.societies.android.api.internal.privacytrust.privacy.model.dataobfuscation.UnobfuscableInfo;



/**
 * Utilities to retrieve obfuscation information about a data type
 *
 * @author Olivier Maridat (Trialog)
 * @date 12 mar. 2013
 */
public class ObfuscatorInfoFactory {
	private static Map<String, ObfuscatorInfo> mappingDataType2ObfuscatorInfo;
	private static UnobfuscableInfo unobfuscatableInfo;
	private static boolean loaded = false;

	public ObfuscatorInfoFactory() {
		if (!loaded) {
			// Load unobfuscable data type info
			unobfuscatableInfo = new UnobfuscableInfo();

			// Load Mapping
			loadMappingDataType2ObfuscatorInfo();

			loaded = true;
		}
	}


	/**
	 * Retrieve obfuscator information about this data type
	 * To know if this data type is obfuscable or not
	 * how (continuous, discrete),
	 * and retrieve examples of obfuscation for the GUI
	 * @param dataType Data type to obfuscate
	 * @return Obfuscator information
	 */
	public ObfuscatorInfo getObfuscatorInfo(String dataType) {
		if (mappingDataType2ObfuscatorInfo.containsKey(dataType)) {
			return mappingDataType2ObfuscatorInfo.get(dataType);
		}
		return unobfuscatableInfo;
	}


	// -- Private methods

	private void loadMappingDataType2ObfuscatorInfo() {
		if (null == mappingDataType2ObfuscatorInfo) {
			mappingDataType2ObfuscatorInfo = new HashMap<String, ObfuscatorInfo>();
		}
		mappingDataType2ObfuscatorInfo.clear();

		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.NAME, new NameObfuscatorInfo());
		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.LOCATION_COORDINATES, new LocationCoordinatesObfuscatorInfo());
		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.STATUS, new StatusObfuscatorInfo());
		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.TEMPERATURE, new TemperatureObfuscatorInfo());
		PostalLocationObfuscatorInfo postalLocationObfuscatorInfo = new PostalLocationObfuscatorInfo();
		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.ADDRESS_HOME_CITY, postalLocationObfuscatorInfo);
		mappingDataType2ObfuscatorInfo.put(CtxAttributeTypes.ADDRESS_WORK_CITY, postalLocationObfuscatorInfo);
	}
}
