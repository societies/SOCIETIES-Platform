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

import java.util.ArrayList;
import java.util.List;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class AMembershipCrit extends MembershipCrit implements Parcelable {

	private static final long serialVersionUID = -4953520609328465990L;

	public AMembershipCrit() {
		super();
	}

	public List<ACriteria> getACriteria() {
		List<ACriteria> returnList = new ArrayList<ACriteria>();
		for (Criteria crit: super.getCriteria()) {
			returnList.add( ACriteria.convertCriteria(crit));
		}		
		return returnList;
	}
	
	public void setACriteria(List<ACriteria> listing) {
		super.getCriteria().clear();
		for (ACriteria acrit: listing) {
			super.getCriteria().add(ACriteria.convertACriteria(acrit));
		}
	}
	
	/* @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.getACriteria());
	}
	
	private AMembershipCrit(Parcel in) {
		super();
		this.setACriteria(in.readArrayList(this.getClass().getClassLoader()));
		//this.setACriteria(in.createTypedArrayList(ACriteria.CREATOR));
	}

	public static final Parcelable.Creator<AMembershipCrit> CREATOR = new Parcelable.Creator<AMembershipCrit>() {
		public AMembershipCrit createFromParcel(Parcel in) {
			return new AMembershipCrit(in);
		}

		public AMembershipCrit[] newArray(int size) {
			return new AMembershipCrit[size];
		}
	};
	
	public static AMembershipCrit convertMembershipCrit(MembershipCrit memberCrit) {
		AMembershipCrit amemberCrit = new AMembershipCrit();
		List<ACriteria> returnList = new ArrayList<ACriteria>();

		if (null != memberCrit && null != memberCrit.getCriteria()) {
			for (Criteria crit: memberCrit.getCriteria()) {
				returnList.add(ACriteria.convertCriteria(crit));
			}
		}
		amemberCrit.setACriteria(returnList);
		
		return amemberCrit;
	}
	
	public static MembershipCrit convertAMembershipCrit(AMembershipCrit amemberCrit) {
		MembershipCrit memberCrit = new MembershipCrit();
		List<Criteria> returnList = new ArrayList<Criteria>();

		if (null != amemberCrit && null != amemberCrit.getCriteria()) {
			for (ACriteria acrit: amemberCrit.getACriteria()) {
				returnList.add(ACriteria.convertACriteria(acrit));
			}
		}
		memberCrit.setCriteria(returnList);
		
		return memberCrit;
	}

	
}
