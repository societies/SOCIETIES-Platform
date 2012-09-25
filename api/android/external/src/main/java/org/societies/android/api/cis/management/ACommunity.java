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
package org.societies.android.api.cis.management;

import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.MembershipCrit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class ACommunity extends Community implements Parcelable {
	
	private static final long serialVersionUID = 6612786379303369931L;

	public AMembershipCrit getMembershipCrit() {
		return AMembershipCrit.convertMembershipCrit( this.membershipCrit);
	}
	
	public void setMembershipCrit(AMembershipCrit amembershipCrit) {
		super.setMembershipCrit(amembershipCrit);
	}
	
	public ACommunity() {
		super();
	}
	/* @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getCommunityJid());
		dest.writeString(this.getCommunityName());
		dest.writeString(this.getCommunityType());
		dest.writeString(this.getDescription());
		dest.writeString(this.getOwnerJid());
		if (null != this.getMembershipCrit() && null != this.getMembershipCrit().getACriteria() && this.getMembershipCrit().getACriteria().size() > 0){
			dest.writeParcelable(this.getMembershipCrit(), flags);
		}
	}
			
	private ACommunity(Parcel in) {
		super();
		this.setCommunityJid(in.readString());
		this.setCommunityName(in.readString());
		this.setCommunityType(in.readString());
		this.setDescription(in.readString());
		this.setOwnerJid(in.readString());
		if(in.dataAvail() >0)
			this.setMembershipCrit((AMembershipCrit) in.readParcelable(this.getClass().getClassLoader()));
	}

	public static final Parcelable.Creator<ACommunity> CREATOR = new Parcelable.Creator<ACommunity>() {
		public ACommunity createFromParcel(Parcel in) {
			return new ACommunity(in);
		}

		public ACommunity[] newArray(int size) {
			return new ACommunity[size];
		}
	};
	
	public static ACommunity convertCommunity(Community community) {
		ACommunity acommunity = new ACommunity();
		acommunity.setCommunityJid(community.getCommunityJid());
		acommunity.setCommunityName(community.getCommunityName());
		acommunity.setCommunityType(community.getCommunityType());
		acommunity.setDescription(community.getDescription());
		acommunity.setOwnerJid(community.getOwnerJid());
		if(community.getMembershipCrit()!=null && community.getMembershipCrit().getCriteria() != null && community.getMembershipCrit().getCriteria().isEmpty()== false)
			acommunity.setMembershipCrit(AMembershipCrit.convertMembershipCrit(community.getMembershipCrit()));
		return acommunity;
	}
}
