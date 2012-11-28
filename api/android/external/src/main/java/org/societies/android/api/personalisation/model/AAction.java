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

package org.societies.android.api.personalisation.model;

import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.api.personalisation.model.Action;

import android.os.Parcel;
import android.os.Parcelable;

public class AAction extends Action implements Parcelable{
	
	private static final long serialVersionUID = 1L;
	AServiceResourceIdentifier aServiceID;
		
	public AAction() {
		super();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.getServiceID(), flags);
		dest.writeString(this.getServiceType());
		dest.writeString(this.getparameterName());
		dest.writeString(this.getvalue());
	}
	
	private AAction(Parcel in) {
		super();
		this.setServiceID((AServiceResourceIdentifier) in.readParcelable(AServiceResourceIdentifier.class.getClassLoader()));
		this.setServiceType(in.readString());
		this.setparameterName(in.readString());
		this.setvalue(in.readString());
	}
	
	public static final Parcelable.Creator<AAction> CREATOR = new Parcelable.Creator<AAction>() {
		public AAction createFromParcel(Parcel in) {
			return new AAction(in);
		}
		public AAction[] newArray(int size) {
			return new AAction[size];
		}
	};
	
	public static AAction convertAction(Action action) {
		AAction aaction = new AAction();
		aaction.setServiceID(AServiceResourceIdentifier.convertServiceResourceIdentifier(action.getServiceID()));
		aaction.setServiceType(action.getServiceType());
		aaction.setparameterName(action.getparameterName());
		aaction.setvalue(action.getvalue());
		
		return aaction;
	}
	
	public static Action convertAAction(AAction aaction) {
		Action action = new Action();
		action.setServiceID(AServiceResourceIdentifier.convertAServiceResourceIdentifier(aaction.getServiceID()));
		action.setServiceType(aaction.getServiceType());
		action.setparameterName(aaction.getparameterName());
		action.setvalue(aaction.getvalue());
		
		return action;
	}
	
	public void setServiceID(AServiceResourceIdentifier aServiceID){
		this.aServiceID = aServiceID;
	}
	
	public AServiceResourceIdentifier getServiceID(){
		return this.aServiceID;
	}
}
