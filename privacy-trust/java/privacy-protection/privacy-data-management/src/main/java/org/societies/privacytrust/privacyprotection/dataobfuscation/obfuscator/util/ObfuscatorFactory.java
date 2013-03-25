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
package org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.util;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Activity;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.PostalLocation;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Status;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Temperature;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.dataobfuscation.IDataObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.ActivityObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.LocationCoordinatesObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.NameObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.PostalLocationObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.StatusObfuscator;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.TemperatureObfuscator;

/**
 * Obfuscator utility class to retrieve the relevant obfuscator
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class ObfuscatorFactory {
	/**
	 * Retrieve the relevant obfuscator
	 * @param dataWrapper
	 * @return
	 * @throws PrivacyException
	 */
	public static IDataObfuscator getDataObfuscator(DataWrapper dataWrapper) throws PrivacyException {
		IDataObfuscator obfuscator = null;
		if (dataWrapper.getData() instanceof LocationCoordinates) {
			obfuscator = new LocationCoordinatesObfuscator(dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Name) {
			obfuscator = new NameObfuscator(dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Temperature) {
			obfuscator = new TemperatureObfuscator(dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Activity) {
			obfuscator = new ActivityObfuscator(dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Status) {
			obfuscator = new StatusObfuscator(dataWrapper);
		}
		else if (dataWrapper.getData() instanceof PostalLocation) {
			obfuscator = new PostalLocationObfuscator(dataWrapper);
		}
		else {
			throw new PrivacyException("Obfuscation aborted: no known obfuscator for this type of data");
		}
		return obfuscator;
	}
}
