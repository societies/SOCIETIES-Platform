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

import java.net.URI;
import java.net.URISyntaxException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import android.os.Parcel;
import android.os.Parcelable;

public class AServiceResourceIdentifier extends ServiceResourceIdentifier implements Parcelable {

	private static final long serialVersionUID = 8662675717162503985L;

	public AServiceResourceIdentifier() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getServiceInstanceIdentifier());
		dest.writeString(this.getIdentifier().toString());
	}
	
	private AServiceResourceIdentifier(Parcel in) {
		super();
		this.setServiceInstanceIdentifier(in.readString());
		try {
			this.setIdentifier(new URI(in.readString()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static final Parcelable.Creator<AServiceResourceIdentifier> CREATOR = new Parcelable.Creator<AServiceResourceIdentifier>() {
		public AServiceResourceIdentifier createFromParcel(Parcel in) {
			return new AServiceResourceIdentifier(in);
		}

		public AServiceResourceIdentifier[] newArray(int size) {
			return new AServiceResourceIdentifier[size];
		}
	};
	
	public static AServiceResourceIdentifier convertServiceResourceIdentifier(ServiceResourceIdentifier sri) {
		if (sri==null) return new AServiceResourceIdentifier();
		
		AServiceResourceIdentifier asri = new AServiceResourceIdentifier();
		asri.setIdentifier(sri.getIdentifier());
		asri.setServiceInstanceIdentifier(sri.getServiceInstanceIdentifier());
		
		return asri;
	}
	
	public static ServiceResourceIdentifier convertAServiceResourceIdentifier(AServiceResourceIdentifier asri) {
		if (asri==null) return new ServiceResourceIdentifier();
		
		ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
		sri.setIdentifier(asri.getIdentifier());
		sri.setServiceInstanceIdentifier(asri.getServiceInstanceIdentifier());
		
		return sri;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getIdentifier() == null) ? 0 : this.getIdentifier().hashCode());
		result = prime * result + ((this.getServiceInstanceIdentifier() == null) ? 0 : this.getServiceInstanceIdentifier().hashCode());
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
		
		AServiceResourceIdentifier other = (AServiceResourceIdentifier) obj;
		//getServiceInstanceIdentifier()
		if (this.getServiceInstanceIdentifier() == null) {
			if (other.getServiceInstanceIdentifier() != null)
				return false;
		} else if (!this.getServiceInstanceIdentifier().equals(other.getServiceInstanceIdentifier()))
			return false;
		//getIdentifier()
		if (this.getIdentifier() == null) {
			if (other.getIdentifier() != null)
				return false;
		} else if (!this.getIdentifier().equals(other.getIdentifier()))
			return false;
		
		return true;
	}
}
