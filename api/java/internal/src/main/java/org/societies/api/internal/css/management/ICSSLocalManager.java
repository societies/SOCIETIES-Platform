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
package org.societies.api.internal.css.management;

import java.util.concurrent.Future;

import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssProfile;


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
 * This interface will be implemented for rich and cloud nodes. This interface is used when calling the CSSManager
 * on a different node.
 */
public interface ICSSLocalManager {

	
	 /**
	  * Register with chosen Domain Server
	  * 
	  * @param profile
	 * @return Future<CssInterfaceResult>
	  */
	 Future<CssInterfaceResult> registerXMPPServer(CssProfile profile);
	/**
	 * Unregister with chosen Domain Server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> unregisterXMPPServer(CssProfile profile);
	/**
	 * Login with chosen Domain server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> loginXMPPServer(CssProfile profile);
	/**
	 * Logout from chosen Domain server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> logoutXMPPServer(CssProfile profile);

	 /**
	 * Login a user to a CSS. 
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> loginCSS(CssProfile profile);	

	/**
	 * Logout the user from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> logoutCSS(CssProfile profile);	
	
	/**
	 * Register a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> registerCSS(CssProfile profile);

	/**
	 * Unregister the CSS
	 * TODO Is a CSS deleted or made inactive
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> unregisterCSS(CssProfile profile);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> registerCSSNode(CssProfile profile);
	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> unregisterCSSNode(CssProfile profile);
	
	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile. 
	 * If none exists or the local cache is deemed to have expired this will 
	 * result in a synchronisation with the cloud node. 
	 * 
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> getCssProfile();

	/**
	 * Modify the CSS Profile
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> modifyCssProfile(CssProfile profile);
	/**
	 * Change the status a CSS device
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> changeCSSNodeStatus(CssProfile profile);
	
	/**
	 * Synchronise the CSS profile. The CSS cloud node's current profile is synchronised
	 * with the local device's cached version
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	 Future<CssInterfaceResult> synchProfile(CssProfile profile);
	
	 /**
	  * Set the presence status of the user
	  * 
	  * @param profile
	  * @return Future<CssInterfaceResult>
	  */
	 Future<CssInterfaceResult> setPresenceStatus(CssProfile profile);
}
