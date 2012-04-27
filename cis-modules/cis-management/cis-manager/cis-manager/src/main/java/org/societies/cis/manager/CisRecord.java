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


package org.societies.cis.manager;

/**
 * Stores meta data relevant for a CIS.
 * 
 * @author Babak Farshchian
 * @version 0
 */


import java.util.Set;

import org.societies.api.cis.collaboration.IServiceSharingRecord;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRecord;
import org.societies.api.cis.management.ICisSubscribed;
import org.societies.cis.activity.ActivityFeed;



public class CisRecord implements ICisOwned, ICisSubscribed{
	public ActivityFeed feed;
	public String ownerCss;
	public int membershipCriteria; // also know as mode



	public String cisName;
	public String cisJID;
	public String cisType;



	/**
	 * permaLink is a permanent URL to this CIS. A type of CIS homepage.
	 */
	public String permaLink;
	public Set<CisParticipant> membersCss; //this is currently kept at the CIS editor and just the pointer to the set is stored here 
	private String password = "none";
	private String host = "none";
	public Set<IServiceSharingRecord> sharedServices; //this is currently kept at the CIS editor and just the pointer to the set is stored here
	

	
	public CisRecord(ActivityFeed feed, String ownerCss,
			int membershipCriteria, String cisId, String permaLink,
			Set<CisParticipant> membersCss, String password, String host,
			Set<IServiceSharingRecord> sharedServices) {
		super();
		this.feed = feed;
		this.ownerCss = ownerCss;
		this.membershipCriteria = membershipCriteria;
		this.cisName = cisId;
		this.permaLink = permaLink;
		this.membersCss = membersCss;
		this.password = password;
		this.host = host;
		this.sharedServices = sharedServices;
		
		this.cisJID = cisId + "." + host;
		
		this.cisType = "default";
	}
	

	
	public CisRecord(ActivityFeed feed, String ownerCss,
			int membershipCriteria, String cisJid, String permaLink,
			Set<CisParticipant> membersCss, String password,
			Set<IServiceSharingRecord> sharedServices, String cisType, String cisName) {
		super();
		this.feed = feed;
		this.ownerCss = ownerCss;
		this.membershipCriteria = membershipCriteria;
		this.cisName = cisName;
		this.permaLink = permaLink;
		this.membersCss = membersCss;
		this.password = password;
		this.host = host;
		this.sharedServices = sharedServices;
		
		this.cisJID = cisJid;
		
		this.cisType = cisType;
	}
	
	public CisRecord(String cisJid) {		
		this.cisJID = cisJid;
		
	}

	 // hash code and equals using CISjID

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cisJID == null) ? 0 : cisJID.hashCode());
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
		CisRecord other = (CisRecord) obj;
		if (cisJID == null) {
			if (other.cisJID != null)
				return false;
		} else if (!cisJID.equals(other.cisJID))
			return false;
		return true;
	}

	
	@Override
	public String getCisType() {
		return cisType;
	}



	public void setCisType(String cisType) {
		this.cisType = cisType;
	}


	
	
	public String getOwnerCss() {
		return ownerCss;
	}


	@Override
	public int getMembershipCriteria() {
		return membershipCriteria;
	}



	public void setMembershipCriteria(int membershipCriteria) {
		this.membershipCriteria = membershipCriteria;
	}


	public void setOwnerCss(String ownerCss) {
		this.ownerCss = ownerCss;
	}


	public String getCisName() {
		return cisName;
	}


	public void setCisName(String cisId) {
		// TODO: double check that this is consistent with the fulljid
		this.cisName = cisId;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	@Override
	public String getCisId() {
		return cisJID;
	}




	@Override
	public String getName() {
		return cisName;
	}



	@Override
	public String getOwnerId() {
		return this.ownerCss;
	}



	@Override
	public String getUserDefineName() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String setUserDefinedName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}






	

	

}
