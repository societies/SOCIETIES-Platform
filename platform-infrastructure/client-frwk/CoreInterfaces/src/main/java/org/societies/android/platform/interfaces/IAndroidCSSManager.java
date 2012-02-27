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
package org.societies.android.platform.interfaces;

import java.util.concurrent.Future;

import org.societies.api.android.internal.model.AndroidCSSProfile;
import org.societies.api.internal.css.management.CSSInterfaceResult;
import org.societies.api.internal.css.management.CSSProfile;


/**
 * 
 * This interface forms the basis of the CSSManager, a component that will exist on all nodes. Its main tasks are:
 * 
 * 1. Allow a user to register or unregister a CSS
 * 2. Allow a user to login into a CSS
 * 3. Allow a user to logout out of a CSS
 * 3. Add and remove nodes that make up a CSS
 * 4. Get the current CSS profile
 * 5. Allow the status of a device to be changed
 * 6. Synchronise profile data
 * 7. Modify the CSS profile 
 * 
 * This interface will be implemented for android nodes. 
 *
 */
public interface IAndroidCSSManager {
	String methodsArray [] = {"registerXMPPServer(CSSProfile profile)",
			"unregisterXMPPServer(CSSProfile profile)",
			"loginXMPPServer(CSSProfile profile)",
			"logoutXMPPServer(CSSProfile profile)",
			"loginCSS(String client, AndroidCSSProfile profile)", 
			"logoutCSS(String client, AndroidCSSProfile profile)",
			"registerCSS(String client, AndroidCSSProfile profile)",
			"unregisterCSS(String client, AndroidCSSProfile profile)",
			"registerCSSDevice(String client, AndroidCSSProfile profile)",
			"unregisterCSSDevice(String client, AndroidCSSProfile profile)",
			"getAndroidCSSProfile(String client)",
			"modifyAndroidCSSProfile(String client, AndroidCSSProfile profile)",
			"changeCSSNodeStatus(String client, AndroidCSSProfile profile)",
			"synchProfile(String client, AndroidCSSProfile profile)",
			"setPresenceStatus(String client, AndroidCSSProfile profile)"};
	/**
	 * Register with chosen Domain Server
	 * 
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile registerXMPPServer(CSSProfile profile);
	/**
	 * Unregister with chosen Domain Server
	 * 
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile unregisterXMPPServer(CSSProfile profile);
	/**
	 * Login with chosen Domain server
	 * 
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile loginXMPPServer(CSSProfile profile);
	/**
	 * Logout from chosen Domain server
	 * 
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile logoutXMPPServer(CSSProfile profile);

	
	/**
	 * Login a user to a CSS. The registration of devices included in the profile
	 * is implied.
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile loginCSS(String client, AndroidCSSProfile profile);	

	/**
	 * Logout the user from a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile logoutCSS(String client, AndroidCSSProfile profile);	
	
	/**
	 * Register a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile registerCSS(String client, AndroidCSSProfile profile);

	/**
	 * Unregister the CSS
	 * TODO Is a CSS deleted or made inactive
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile unregisterCSS(String client, AndroidCSSProfile profile);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile registerCSSDevice(String client, AndroidCSSProfile profile);
	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile unregisterCSSDevice(String client, AndroidCSSProfile profile);
	
	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile. 
	 * If none exists or the local cache is deemed to have expired this will 
	 * result in a synchronisation with the cloud node. 
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile getAndroidCSSProfile(String client);

	/**
	 * Modify the CSS Profile
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile modifyAndroidCSSProfile(String client, AndroidCSSProfile profile);
	/**
	 * Change the status a CSS device
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile changeCSSNodeStatus(String client, AndroidCSSProfile profile);
	
	/**
	 * Synchronise the CSS profile. The CSS cloud node's current profile is synchronised
	 * with the local device's cached version
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile synchProfile(String client, AndroidCSSProfile profile);
	
	/**
	 * Set the presence status of the user
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSProfile
	 */
	AndroidCSSProfile setPresenceStatus(String client, AndroidCSSProfile profile);
}
