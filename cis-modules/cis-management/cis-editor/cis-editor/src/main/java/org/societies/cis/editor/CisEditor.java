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

package org.societies.cis.editor;

import java.util.HashSet;

import java.util.Set;

//import org.societies.cis.mgmt;
import org.societies.api.internal.cis.management.CisActivityFeed;
import org.societies.api.internal.cis.management.CisRecord;
import org.societies.api.internal.cis.management.ServiceSharingRecord;

public class CisEditor implements ICisEditor {


	public CisRecord cisRecord;
	public CisActivityFeed cisActivityFeed;
	public Set<ServiceSharingRecord> sharedServices; 
//	public CommunityManagement commMgmt;

	public String[] membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together
	
	public static final int MAX_NB_MEMBERS = 100;// TODO: this is temporary, we have to set the memberCss to something more suitable

	// constructor for creating a CIS from scratch	
	public CisEditor(String ownerCss, String cisId,
			String membershipCriteria, String permaLink, String password) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		membersCss = new String[MAX_NB_MEMBERS];
		
		cisRecord = new CisRecord(cisActivityFeed,ownerCss, membershipCriteria, cisId, permaLink, membersCss,
				password, sharedServices);
		

		// TODO: broadcast its creation to other nodes?

	}
	
	// if just ownerCss and cisId are passed,
	// password will be set to ""
	// membership to "default"
	// permalink to ""
	public CisEditor(String ownerCss, String cisId) {
		
		membersCss = new String[MAX_NB_MEMBERS];
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		
		cisRecord = new CisRecord(cisActivityFeed,ownerCss, "default", cisId, "", membersCss,
				"", sharedServices);
		

		// TODO: broadcast its creation to other nodes?

	}

	// constructor for creating a CIS from a CIS record, maybe the case when we are retrieving data from a database
	// TODO: double check if we should clone the related objects or just copy the reference (as it is now)
	public CisEditor(CisRecord cisRecord) {
		
		this.cisRecord = cisRecord; 
		
		this.cisActivityFeed = this.cisRecord.feed;
		this.sharedServices = this.cisRecord.sharedServices;
		
		
		// TODO: broadcast its creation to other nodes?

	}

	// index for hash and equals was only the cisRecord
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cisRecord == null) ? 0 : cisRecord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CisEditor other = (CisEditor) obj;
		if (cisRecord == null) {
			if (other.cisRecord != null)
				return false;
		} else if (!cisRecord.equals(other.cisRecord))
			return false;
		return true;
	}

    
    
}
