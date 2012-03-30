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
package org.societies.android.platform.client;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisSubscribed;

/**
 * Android implementation of ICisManager.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */
public class CisManager implements ICisManager {
    public ICisOwned createCis(String arg0, String arg1, String arg2,
	    String arg3, int arg4) {
	// TODO Auto-generated method stub
	return null;
    }

    private List<ICisOwned> ownedDisasterList = new ArrayList<ICisOwned>();
    //List to hold disasters this users is only a member of:
    private List<ICisSubscribed> subscribedDisasterList = new ArrayList<ICisSubscribed>();

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#createCis(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public ICisEditor createCis(String cssId, String cssPassword,
	    String cisName, int mode) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#deleteCis(java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean deleteCis(String cssId, String cssPassword, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#getCis(java.lang.String, java.lang.String)
     */
    public ICisRecord getCis(String cssId, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#getCisList(org.societies.api.cis.management.ICisRecord)
     */
    public ICisRecord[] getCisList(ICisRecord query) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisManager#requestNewCisOwner(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean requestNewCisOwner(String currentOwnerCssId,
	    String currentOwnerCssPassword, String newOwnerCssId, String cisId) {
	// TODO Auto-generated method stub
	return null;
    }

}
