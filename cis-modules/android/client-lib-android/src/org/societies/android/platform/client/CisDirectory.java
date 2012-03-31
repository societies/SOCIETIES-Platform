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

import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;

/**
 * @author Babak.Farshchian@sintef.no
 *
 */
public class CisDirectory implements ICisDirectory {

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#searchByName(java.lang.String)
     */
    public ICisAdvertisementRecord[] searchByName(String cisName) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#searchByOwner(java.lang.String)
     */
    public ICisAdvertisementRecord[] searchByOwner(String ownerId) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#searchByUri(java.lang.String)
     */
    public ICisAdvertisementRecord[] searchByUri(String uri) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#RegisterCis(org.societies.api.cis.directory.ICisAdvertisementRecord)
     */
    public Boolean RegisterCis(ICisAdvertisementRecord cis) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#UnregisterCis(org.societies.api.cis.directory.ICisAdvertisementRecord)
     */
    public Boolean UnregisterCis(ICisAdvertisementRecord cis) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#AddPeerDirectory(java.lang.String, java.lang.String, java.lang.Integer)
     */
    public Integer AddPeerDirectory(String directoryURI, String cssId,
	    Integer synchMode) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#ping()
     */
    public Boolean ping() {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.directory.ICisDirectory#getURI()
     */
    public String getURI() {
	// TODO Auto-generated method stub
	return null;
    }

}
