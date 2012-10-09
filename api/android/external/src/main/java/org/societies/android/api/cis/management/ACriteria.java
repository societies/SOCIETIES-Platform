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

import org.societies.api.schema.cis.community.Criteria;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class ACriteria extends Criteria implements Parcelable {

	private static final long serialVersionUID = -2563557421598732799L;

	public ACriteria() {
		super();
	}
	
	/* @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getAttrib());
		dest.writeString(this.getOperator());
		dest.writeString(this.getValue1());
		dest.writeString(this.getValue2());
		dest.writeInt(this.getRank());		
	}

	private ACriteria(Parcel in) {
		super();
		this.setAttrib(in.readString());
		this.setOperator(in.readString());
		this.setValue1(in.readString());
		this.setValue2(in.readString());
		this.setRank(in.readInt());
	}
	
	public static final Parcelable.Creator<ACriteria> CREATOR = new Parcelable.Creator<ACriteria>() {
		public ACriteria createFromParcel(Parcel in) {
			return new ACriteria(in);
		}

		public ACriteria[] newArray(int size) {
			return new ACriteria[size];
		}
	};
	
	public static ACriteria convertCriteria(Criteria criteria) {
		ACriteria acriteria = new ACriteria();
		acriteria.setAttrib(criteria.getAttrib());
		acriteria.setOperator(criteria.getOperator());
		acriteria.setValue1(criteria.getValue1());
		acriteria.setValue2(criteria.getValue2());
		acriteria.setRank(criteria.getRank());
		
		return acriteria;
	}
	
	public static Criteria convertACriteria(ACriteria acriteria) {
		Criteria criteria = new Criteria();
		criteria.setAttrib(acriteria.getAttrib());
		criteria.setOperator(acriteria.getOperator());
		criteria.setValue1(acriteria.getValue1());
		criteria.setValue2(acriteria.getValue2());
		criteria.setRank(acriteria.getRank());
		
		return criteria;
	}
}
