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

import java.util.ArrayList;

import org.societies.android.api.personalisation.model.AAction;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class APreferenceOutcome extends AAction implements Parcelable {

	private int confidenceLevel;
	private ArrayList<String> parameterNames;
	private AQualityofPreference qop;

	public APreferenceOutcome(){
		super();
	}

	public APreferenceOutcome(AServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value){
		super();
		setServiceID(serviceID);
		setServiceType(serviceType);
		setparameterName(parameterName);
		setvalue(value);
		this.confidenceLevel = 51;
	}

	private APreferenceOutcome(Parcel in){
		
		this.setServiceID((AServiceResourceIdentifier) in.readParcelable(AServiceResourceIdentifier.class.getClassLoader()));
		this.setServiceType(in.readString());
		this.setparameterName(in.readString());
		this.setvalue(in.readString());
		this.qop = in.readParcelable(AQualityofPreference.class.getClassLoader());
		this.confidenceLevel = in.readInt();
		
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(this.getServiceID(), flags);
		out.writeString(this.getServiceType());
		out.writeString(this.getparameterName());
		out.writeString(this.getvalue());
		out.writeParcelable(this.qop, flags);
		out.writeInt(this.confidenceLevel);
		
	}

	public static final Parcelable.Creator<APreferenceOutcome> CREATOR = new Parcelable.Creator<APreferenceOutcome>() {

        public APreferenceOutcome createFromParcel(Parcel in) {
            return new APreferenceOutcome(in);
        }

        public APreferenceOutcome[] newArray(int size) {
            return new APreferenceOutcome[size];
        }

    };
	/**
	 * Method to set the confidence level
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * @see IOutcome#getConfidenceLevel()
	 */
	public int getConfidenceLevel() {
		return confidenceLevel;
	}


	
	public void setQualityofPreference(AQualityofPreference qop){
		this.qop = qop;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}



	public AQualityofPreference getQualityofPreference() {
		// TODO Auto-generated method stub
		return this.qop;
	}
	


}