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
package org.societies.android.api.internal.personalisation.model;

import java.util.Date;


import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class APreferenceTreeModel implements Parcelable {

	private AServiceResourceIdentifier serviceID;
	private String serviceType;
	private String preferenceName;
	private APreference preference;
	private Date lastModifiedDate;
    protected boolean asksAllowsChildren;
    
	public APreferenceTreeModel(APreference root) {
		this(root, false);
		this.preference = root;
	}

	public APreferenceTreeModel(APreference root, boolean asksAllowsChildren) {
        super();
        this.preference = root;
        this.asksAllowsChildren = asksAllowsChildren;
    }
	public APreference getRootPreference(){
		return this.preference;
	}
	public String getPreferenceName() {
		return this.preferenceName;
	}

	public AServiceResourceIdentifier getServiceID() {
		return this.serviceID;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setPreferenceName(String prefname) {
		this.preferenceName = prefname;
	}

	public void setServiceID(AServiceResourceIdentifier id) {
		this.serviceID = id;
		
	}
	public void setServiceType(String type) {
		this.serviceType = type;
		
	}


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private APreferenceTreeModel(Parcel in){
		this((APreference) in.readParcelable(APreference.class.getClassLoader()));
		this.serviceID = in.readParcelable(AServiceResourceIdentifier.class.getClassLoader());
		this.preferenceName = in.readString();
		this.serviceType = in.readString();
		this.lastModifiedDate.setTime(in.readLong());
	}
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(this.preference, flags);
		out.writeParcelable(this.serviceID, flags);
		out.writeString(preferenceName);
		out.writeString(this.serviceType);
		out.writeLong(this.lastModifiedDate.getTime());
		
	}

	public Date getLastModifiedDate() {
		// TODO Auto-generated method stub
		return this.lastModifiedDate;
	}

	public void setLastModifiedDate(Date d) {
		this.lastModifiedDate = d;
		
	}
	

	public static final Parcelable.Creator<APreferenceTreeModel> CREATOR = new Parcelable.Creator<APreferenceTreeModel>() {

        public APreferenceTreeModel createFromParcel(Parcel in) {
            return new APreferenceTreeModel(in);
        }

        public APreferenceTreeModel[] newArray(int size) {
            return new APreferenceTreeModel[size];
        }

    };

}