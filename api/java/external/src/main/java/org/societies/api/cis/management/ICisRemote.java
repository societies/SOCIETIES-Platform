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
package org.societies.api.cis.management;

import org.societies.api.identity.Requestor;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * @author thomas.vilarinho@sintef.no
 *
 */


@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICisRemote {
	
	/**
	 * same as the one below but using the Requestor object instead of RequestorBean
	 * 
	 * @param {@link Requestor} requestor object identifying if the user is 
	 * @param callback callback function
	 */
	@Deprecated
    public void getInfo(Requestor req,ICisManagerCallback callback);
    
	/**
	 * Get info from a CIS.
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * it has the  info from the CIS
	 * 
	 * @param {@link Requestor} requestor object identifying if the user is
	 * @param callback callback function
	 */
    public void getInfo(RequestorBean req,ICisManagerCallback callback);
    
	/**
	 * same as the one below but using the Requestor object instead of RequestorBean
	 * 
	 * @param {@link Requestor} requestor object identifying if the user is 
	 * @param callback callback function
	 */
    @Deprecated
    public void getListOfMembers(Requestor req, ICisManagerCallback callback);
    
	/**
	 * Get list of members from a CIS.
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * which will have a Who with a list of Participant objects
	 * 
	 * @param {@link RequestorBean} requestor object identifying if the user is 
	 * @param callback callback function
	 */
    public void getListOfMembers(RequestorBean req, ICisManagerCallback callback);

}
