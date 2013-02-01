/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.societies.android.api.apppreferences;

import java.util.HashMap;
import java.util.Set;

/**
 * Interface defines how to access and update Societies Android app shared preferences.
 * Implemenation can be used to access preferences for both Java and Javascript (via PhoneGap/Cordova plugin) 
 */

public interface IAppPreferences {
	/**
	 * Get all of the preferences
	 * 
	 * @return HashMap<String,?> all preferences
	 */
	HashMap<String,?> getAllPrefs(); 
	
	/**
	 * Get all of the preference names
	 * 
	 * @return Set<String> of preference names
	 */
	Set<String> getPrefNames();
	/**
	 * Get a String preference value for a given preference name
	 * 
	 * @param prefName
	 * @return String preference value
	 */
	String getStringPrefValue(String prefName);
	/**
	 * Get an Integer preference value for a given preference name
	 * 
	 * @param prefName
	 * @return int preference value
	 */
	int getIntegerPrefValue(String prefName);
	/**
	 * Get an Long preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	long getLongPrefValue(String prefName);
	/**
	 * Get an Float preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	float getFloatPrefValue(String prefName);
	/**
	 * Get a boolean preference value for a given preference name
	 * 
	 * @param prefName
	 * @return long preference value
	 */
	boolean getBooleanPrefValue(String prefName);
}
