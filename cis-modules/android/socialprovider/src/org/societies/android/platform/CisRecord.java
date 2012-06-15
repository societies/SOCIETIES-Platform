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
package org.societies.android.platform;

import org.societies.android.api.external.cis.management.ICisRecord;


/**
 * @author Babak.Farshchian@sintef.no
 *
 */
public class CisRecord implements ICisRecord{
    
    private String cisId;
    private String name;
    private String ownerId;
    private String creationDate;
    private String userDefinedName;

    public CisRecord(String _cisId, String _name, String _ownerId, String _creationDate, String _udn){
	cisId = _cisId;
	name = _name;
	ownerId = _ownerId;
	creationDate = _creationDate;
	userDefinedName = _udn;
    }
    public CisRecord(String _cisId, String _name, String _ownerId, String _creationDate){
	cisId = _cisId;
	name = _name;
	ownerId = _ownerId;
	creationDate = _creationDate;
    }
    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#getCisId()
     */
    public String getCisId() {
	// TODO Auto-generated method stub
	return cisId;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#getName()
     */
    public String getName() {
	// TODO Auto-generated method stub
	return name;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#getOwnerId()
     */
    public String getOwnerId() {
	// TODO Auto-generated method stub
	return ownerId;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#getCreationDate()
     */
    public String getCreationDate() {
	// TODO Auto-generated method stub
	return creationDate;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#setUserDefinedName(java.lang.String)
     */
    public String setUserDefinedName(String _name) {
	// TODO Auto-generated method stub
	userDefinedName = _name;
	return userDefinedName;
    }

    /* (non-Javadoc)
     * @see org.societies.api.cis.management.ICisRecord#getUserDefineName()
     */
    public String getUserDefineName() {
	// TODO Auto-generated method stub
	return userDefinedName;
    }

}
