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

package org.societies.android.api.servicelifecycle;

import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import android.os.Parcel;
import android.os.Parcelable;

public class AServiceImplementation extends ServiceImplementation implements Parcelable {

	private static final long serialVersionUID = -3863142958575222945L;

	public AServiceImplementation() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int) */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getServiceNameSpace());
		dest.writeString(this.getServiceProvider());
		dest.writeString(this.getServiceVersion());
		dest.writeString(this.getServiceClient());
	}
	
	private AServiceImplementation(Parcel in) {
		super();
		this.setServiceNameSpace(in.readString());
		this.setServiceProvider(in.readString());
		this.setServiceVersion(in.readString());
		this.setServiceClient(in.readString());
	}
	
	public static final Parcelable.Creator<AServiceImplementation> CREATOR = new Parcelable.Creator<AServiceImplementation>() {
		public AServiceImplementation createFromParcel(Parcel in) {
			return new AServiceImplementation(in);
		}

		public AServiceImplementation[] newArray(int size) {
			return new AServiceImplementation[size];
		}
	};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getServiceNameSpace() == null) ? 0 : this.getServiceNameSpace().hashCode());
		result = prime * result + ((this.getServiceProvider() == null) ? 0 : this.getServiceProvider().hashCode());
		result = prime * result + ((this.getServiceVersion() == null) ? 0 : this.getServiceVersion().hashCode());
		result = prime * result + ((this.getServiceClient() == null) ? 0 : this.getServiceClient().hashCode());
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
		
		AServiceImplementation other = (AServiceImplementation) obj;
		//getServiceNameSpace()
		if (this.getServiceNameSpace() == null) {
			if (other.getServiceNameSpace() != null)
				return false;
		} else if (!this.getServiceNameSpace().equals(other.getServiceNameSpace()))
			return false;
		//getServiceProvider()
		if (this.getServiceProvider() == null) {
			if (other.getServiceProvider() != null)
				return false;
		} else if (!this.getServiceProvider().equals(other.getServiceProvider()))
			return false;
		//getServiceVersion()
		if (this.getServiceVersion() == null) {
			if (other.getServiceVersion() != null)
				return false;
		} else if (!this.getServiceVersion().equals(other.getServiceVersion()))
			return false;
		//getServiceClient()
		if (this.getServiceClient() == null) {
			if (other.getServiceClient() != null)
				return false;
		} else if (!this.getServiceClient().equals(other.getServiceClient()))
			return false;
		
		return true;
	}
	
	/**Converts ServiceImplementation to AServiceImplementation
	 * 
	 * @param serviceImpl
	 * @return
	 */
	public static AServiceImplementation convertServiceImplementation(ServiceImplementation serviceImpl) {
		if(serviceImpl == null) return new AServiceImplementation();

		AServiceImplementation aservImpl = new AServiceImplementation();
		aservImpl.setServiceClient(serviceImpl.getServiceClient());
		aservImpl.setServiceNameSpace(serviceImpl.getServiceNameSpace());
		aservImpl.setServiceProvider(serviceImpl.getServiceProvider());
		aservImpl.setServiceVersion(serviceImpl.getServiceVersion());
		
		return aservImpl;
	}
	
	/**Converts AServiceImplementation to ServiceImplementation
	 * 
	 * @param serviceImpl
	 * @return
	 */
	public static ServiceImplementation convertAServiceImplementation(AServiceImplementation aserviceImpl) {
		if(aserviceImpl == null) return new ServiceImplementation();

		ServiceImplementation servImpl = new ServiceImplementation();
		servImpl.setServiceClient(aserviceImpl.getServiceClient());
		servImpl.setServiceNameSpace(aserviceImpl.getServiceNameSpace());
		servImpl.setServiceProvider(aserviceImpl.getServiceProvider());
		servImpl.setServiceVersion(aserviceImpl.getServiceVersion());
		
		return servImpl;
	}
}
