/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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



import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;

import android.os.Parcel;
import android.os.Parcelable;



//import org.societies.api.internal.servicelifecycle.serviceRegistry.model.AServiceResourceIdentifier;

/**
 * @author Eliza
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class APreferenceDetails implements Parcelable {

	private String preferenceName;
	private AServiceResourceIdentifier serviceID = null;
	private String serviceType;

	public APreferenceDetails(){

	}


	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public APreferenceDetails(String serviceType, AServiceResourceIdentifier serviceID, String preferenceName){
		this.serviceID = serviceID;
		this.serviceType = serviceType;
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param serviceType
	 * @param preferenceName
	 */
	public APreferenceDetails(String serviceType, String preferenceName){
		this.serviceType = serviceType;
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param preferenceName
	 */
	public APreferenceDetails(String preferenceName){
		this.preferenceName = preferenceName;
	}

	private APreferenceDetails(Parcel in) {
		super();
		this.serviceType = in.readString();
		this.serviceID = in.readParcelable(AServiceResourceIdentifier.class.getClassLoader());
		this.preferenceName = in.readString();
		
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.serviceType);
		out.writeParcelable(serviceID, flags);
		out.writeString(preferenceName);
		
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	public static final Parcelable.Creator<APreferenceDetails> CREATOR = new Parcelable.Creator<APreferenceDetails>() {

        public APreferenceDetails createFromParcel(Parcel in) {
            return new APreferenceDetails(in);
        }

        public APreferenceDetails[] newArray(int size) {
            return new APreferenceDetails[size];
        }

    };
	
	/**
	 * 
	 * @param prefName
	 */
	private boolean comparePreferenceName(String prefName){
		return this.preferenceName.equalsIgnoreCase(prefName);
	}

	/**
	 * 
	 * @param sID
	 */
	private boolean compareServiceID(AServiceResourceIdentifier sID){
		return this.serviceID.toString().equalsIgnoreCase(sID.toString());
	}

	/**
	 * 
	 * @param sType
	 */
	private boolean compareServiceType(String sType){
		return this.serviceType.equalsIgnoreCase(sType);
	}

	/**
	 * 
	 * @param details
	 */
	@Override
	public boolean equals(Object details){
		if (details instanceof APreferenceDetails){
			//does not compare serviceTypes anymore
			if (compareServiceID(((APreferenceDetails) details).getServiceID())){
				if (comparePreferenceName(((APreferenceDetails) details).getPreferenceName())){
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 
	 * @param details
	 */
	public boolean equalsServiceOnlyDetails(APreferenceDetails details){

		if (compareServiceType(((APreferenceDetails) details).getServiceType())){
			if (compareServiceID(((APreferenceDetails) details).getServiceID())){

				return true;

			}
		}

		return false;
	}

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 */
	public boolean equalsServiceOnlyDetails(String serviceType, AServiceResourceIdentifier serviceID){
		if (compareServiceType(serviceType)){
			if (compareServiceID(serviceID)){

				return true;

			}
		}

		return false;
	}

	public String getPreferenceName(){
		return this.preferenceName;
	}

	public AServiceResourceIdentifier getServiceID(){
		return this.serviceID;
	}

	public String getServiceType(){
		return this.serviceType;
	}

	/**
	 * 
	 * @param preferenceName
	 */
	public void setPreferenceName(String preferenceName){
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param serviceID
	 */
	public void setServiceID(AServiceResourceIdentifier serviceID){
		this.serviceID = serviceID;
	}

	/**
	 * 
	 * @param serviceType
	 */
	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	public String toString(){
		String st;
		if (this.serviceType==null){
			st = "GenericType";
		}else{
			st = this.serviceType;
		}

		String sID;
		if (this.serviceID==null){
			sID="GenericID";
		}else{
			sID = this.serviceID.getServiceInstanceIdentifier();
		}


		return "ServiceType: "+st+"\n"
		+ "ServiceID: "+sID+"\n"
		+ "PreferenceName: "+this.preferenceName+"\n";
	}



}