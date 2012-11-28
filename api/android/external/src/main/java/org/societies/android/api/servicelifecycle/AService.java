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

import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class AService extends Service implements Parcelable {

	private static final long serialVersionUID = 7945457983909141117L;
	
	public AServiceInstance getServiceInstance() {
		return AServiceInstance.convertServiceInstance(super.getServiceInstance());
	}
	public void setServiceInstance(AServiceInstance aserviceInstance) {
		super.setServiceInstance(aserviceInstance);
	}
	
	public AServiceResourceIdentifier getServiceIdentifier() {
		return AServiceResourceIdentifier.convertServiceResourceIdentifier(super.getServiceIdentifier());
	}

	public void setServiceIdentifier(AServiceResourceIdentifier aServiceResourceId) {
		super.setServiceIdentifier(aServiceResourceId);
	}
	
	public AService() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getAuthorSignature());
		dest.writeString(this.getContextSource());
		dest.writeString(this.getPrivacyPolicy());
		dest.writeString(this.getSecurityPolicy());
	    dest.writeString(this.getServiceDescription());
	    dest.writeString(this.getServiceEndpoint());
	    dest.writeParcelable(this.getServiceIdentifier(), flags);
	    dest.writeParcelable(this.getServiceInstance(), flags);
	    dest.writeString(this.getServiceLocation());
	    dest.writeString(this.getServiceName());
		dest.writeString(this.getServiceCategory());
		dest.writeString(this.getServiceStatus().toString());
		dest.writeString(this.getServiceType().toString());
	}

	private AService(Parcel in) {
		super();
		this.setAuthorSignature(in.readString());
		this.setContextSource(in.readString());
	    this.setPrivacyPolicy(in.readString());
	    this.setSecurityPolicy(in.readString());
	    this.setServiceDescription(in.readString());
	    this.setServiceEndpoint(in.readString());
	    this.setServiceIdentifier((AServiceResourceIdentifier) in.readParcelable(this.getClass().getClassLoader()) );
	    this.setServiceInstance((AServiceInstance) in.readParcelable(this.getClass().getClassLoader()) );
	    this.setServiceLocation(in.readString());
	    this.setServiceName(in.readString());
		this.setServiceCategory(in.readString());
		this.setServiceStatus(ServiceStatus.fromValue(in.readString()));
		this.setServiceType(ServiceType.fromValue(in.readString()));
	}
	
	public static final Parcelable.Creator<AService> CREATOR = new Parcelable.Creator<AService>() {
		public AService createFromParcel(Parcel in) {
			return new AService(in);
		}

		public AService[] newArray(int size) {
			return new AService[size];
		}
	};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getAuthorSignature() == null) ? 0 : this.getAuthorSignature().hashCode());
		result = prime * result + ((this.getContextSource() == null) ? 0 : this.getContextSource().hashCode());
		result = prime * result + ((this.getPrivacyPolicy() == null) ? 0 : this.getPrivacyPolicy().hashCode());
		result = prime * result + ((this.getSecurityPolicy() == null) ? 0 : this.getSecurityPolicy().hashCode());
		result = prime * result + ((this.getServiceDescription() == null) ? 0 : this.getServiceDescription().hashCode());
		result = prime * result + ((this.getServiceEndpoint() == null) ? 0 : this.getServiceEndpoint().hashCode());
		result = prime * result + ((this.getServiceIdentifier() == null) ? 0 : this.getServiceIdentifier().hashCode());
		result = prime * result + ((this.getServiceInstance() == null) ? 0 : this.getServiceInstance().hashCode());
		result = prime * result + ((this.getServiceLocation() == null) ? 0 : this.getServiceLocation().hashCode());
		result = prime * result + ((this.getServiceName() == null) ? 0 : this.getServiceName().hashCode());
		result = prime * result + ((this.getServiceCategory() == null) ? 0 : this.getServiceCategory().hashCode());
		result = prime * result + ((this.getServiceStatus() == null) ? 0 : this.getServiceStatus().hashCode());
		result = prime * result + ((this.getServiceType() == null) ? 0 : this.getServiceType().hashCode());
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
		
		AService other = (AService) obj;
		//getAuthorSignature()
		if (this.getAuthorSignature() == null) {
			if (other.getAuthorSignature() != null)
				return false;
		} else if (!this.getAuthorSignature().equals(other.getAuthorSignature()))
			return false;
		//getContextSource()
		if (this.getContextSource() == null) {
			if (other.getContextSource() != null)
				return false;
		} else if (!this.getContextSource().equals(other.getContextSource()))
			return false;
		//getPrivacyPolicy()
		if (this.getPrivacyPolicy() == null) {
			if (other.getPrivacyPolicy() != null)
				return false;
		} else if (!this.getPrivacyPolicy().equals(other.getPrivacyPolicy()))
			return false;
		//getSecurityPolicy()
		if (this.getSecurityPolicy() == null) {
			if (other.getSecurityPolicy() != null)
				return false;
		} else if (!this.getSecurityPolicy().equals(other.getSecurityPolicy()))
			return false;
		//getServiceDescription()
		if (this.getServiceDescription() == null) {
			if (other.getServiceDescription() != null)
				return false;
		} else if (!this.getServiceDescription().equals(other.getServiceDescription()))
			return false;
		//getServiceEndpoint()
		if (this.getServiceEndpoint() == null) {
			if (other.getServiceEndpoint() != null)
				return false;
		} else if (!this.getServiceEndpoint().equals(other.getServiceEndpoint()))
			return false;
		//getServiceIdentifier()
		if (this.getServiceIdentifier() == null) {
			if (other.getServiceIdentifier() != null)
				return false;
		} else if (!this.getServiceIdentifier().equals(other.getServiceIdentifier()))
			return false;
		//getServiceInstance()
		if (this.getServiceInstance() == null) {
			if (other.getServiceInstance() != null)
				return false;
		} else if (!this.getServiceInstance().equals(other.getServiceInstance()))
			return false;
		//getServiceLocation()
		if (this.getServiceLocation() == null) {
			if (other.getServiceLocation() != null)
				return false;
		} else if (!this.getServiceLocation().equals(other.getServiceLocation()))
			return false;
		//getServiceName()
		if (this.getServiceName() == null) {
			if (other.getServiceName() != null)
				return false;
		} else if (!this.getServiceName().equals(other.getServiceName()))
			return false;
		//getServiceCategory()
		if (this.getServiceCategory() == null) {
			if (other.getServiceCategory() != null)
				return false;
		} else if (!this.getServiceCategory().equals(other.getServiceCategory()))
			return false;
		//getServiceStatus()
		if (this.getServiceStatus() == null) {
			if (other.getServiceStatus() != null)
				return false;
		} else if (!this.getServiceStatus().equals(other.getServiceStatus()))
			return false;
		//getServiceType()
		if (this.getServiceType() == null) {
			if (other.getServiceType() != null)
				return false;
		} else if (!this.getServiceType().equals(other.getServiceType()))
			return false;
		return true;
	}
	
	public static AService convertService(Service service) {
		AService aservice = new AService();
		aservice.setAuthorSignature(service.getAuthorSignature());
		aservice.setContextSource(service.getContextSource());
		aservice.setPrivacyPolicy(service.getPrivacyPolicy());
		aservice.setSecurityPolicy(service.getSecurityPolicy());
		aservice.setServiceCategory(service.getServiceCategory());
		aservice.setServiceDescription(service.getServiceDescription());
		aservice.setServiceEndpoint(service.getServiceEndpoint());
		aservice.setServiceIdentifier(AServiceResourceIdentifier.convertServiceResourceIdentifier(service.getServiceIdentifier()));
		aservice.setServiceInstance(AServiceInstance.convertServiceInstance(service.getServiceInstance()));
		aservice.setServiceLocation(service.getServiceLocation());
		aservice.setServiceName(service.getServiceName());
		aservice.setServiceStatus(service.getServiceStatus());
		aservice.setServiceType(service.getServiceType());
		
		return aservice;
	}
	
	public static Service convertAService(AService aservice) {
		Service service = new Service();		
		service.setAuthorSignature(aservice.getAuthorSignature());
		service.setContextSource(aservice.getContextSource());
		service.setPrivacyPolicy(aservice.getPrivacyPolicy());
		service.setSecurityPolicy(aservice.getSecurityPolicy());
		service.setServiceCategory(aservice.getServiceCategory());
		service.setServiceDescription(aservice.getServiceDescription());
		service.setServiceEndpoint(aservice.getServiceEndpoint());
		service.setServiceIdentifier(AServiceResourceIdentifier.convertAServiceResourceIdentifier(aservice.getServiceIdentifier()));
		service.setServiceInstance(AServiceInstance.convertAServiceInstance(aservice.getServiceInstance()));
		service.setServiceLocation(aservice.getServiceLocation());
		service.setServiceName(aservice.getServiceName());
		service.setServiceStatus(aservice.getServiceStatus());
		service.setServiceType(aservice.getServiceType());
		
		return service;
	}

}
